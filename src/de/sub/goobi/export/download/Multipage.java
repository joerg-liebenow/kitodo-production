/*
 * This file is part of the Goobi Application - a Workflow tool for the support of
 * mass digitization.
 *
 * Visit the websites for more information.
 *     - http://gdz.sub.uni-goettingen.de
 *     - http://www.goobi.org
 *     - http://launchpad.net/goobi-production
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package de.sub.goobi.export.download;

//TODO: Move parts of this into ContentServer2

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;

import de.sub.goobi.beans.Prozess;
import de.sub.goobi.metadaten.MetadatenImagesHelper;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.helper.exceptions.SwapException;


/**
 * Die Klasse Multipage dient zur Erzeugung von mehrseitigen Tiffs
 * 
 * @author Steffen Hankiewicz
 * @version 1.00 - 12.04.2005
 */
public class Multipage {
   private static final Logger myLogger = Logger.getLogger(Multipage.class);
   Helper help = new Helper();

   private void create(Prozess inProzess) throws IOException, InterruptedException, SwapException, DAOException {
      /* alle tifs durchlaufen */
      String pfad = inProzess.getImagesDirectory();
      File dir = new File(pfad);

      String[] dateien = dir.list(MetadatenImagesHelper.filter);

      /* keine Tifs vorhanden, also raus */
      if (dateien == null) {
         myLogger.debug("Verzeichnis ist leer");
         return;
      }

      /* alle Bilder in ein Array übernehmen */
      RenderedImage image[] = new PlanarImage[dateien.length];
      for (int i = 0; i < dateien.length; i++) {
         myLogger.debug(pfad + dateien[i]);
         image[i] = JAI.create("fileload", pfad + dateien[i]);
      }
      myLogger.debug("Bilder durchlaufen");

      /* --------------------------------
       * alle Bilder als Multipage erzeugen
       * --------------------------------*/
      OutputStream out = new FileOutputStream(help.getGoobiDataDirectory() + inProzess.getId().intValue()
            + File.separator + "multipage.tiff");
      TIFFEncodeParam param = new TIFFEncodeParam();
      param.setCompression(4);
      ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", out, param);
      Vector<RenderedImage> vector = new Vector<RenderedImage>();
      for (int i = 1; i < image.length; i++) {
         vector.add(image[i]);
      }
      param.setExtraImages(vector.iterator());
      encoder.encode(image[0]);
      out.close();
      myLogger.debug("fertig");
   }

   

   public void ExportStart(Prozess inProzess) throws IOException, InterruptedException, SwapException, DAOException {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (!facesContext.getResponseComplete()) {
         HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

         String fileName = inProzess.getTitel() + ".tif";

         ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
         String contentType = servletContext.getMimeType(fileName);
         response.setContentType(contentType);
         response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
         ServletOutputStream out = response.getOutputStream();

         /* --------------------------------
          * die txt-Datei direkt in den Stream schreiben lassen
          * --------------------------------*/
         String filename = help.getGoobiDataDirectory() + inProzess.getId().intValue()+ File.separator + "multipage.tiff";
         if (!(new File(filename)).exists()) {
            create(inProzess);
         }
         FileInputStream fis = null;
         try {
            fis = new FileInputStream(filename);
            byte[] buf = new byte[4 * 1024]; // 4K buffer //how about 4096?
            int bytesRead;
            while ((bytesRead = fis.read(buf)) != -1)
               out.write(buf, 0, bytesRead);
         } finally {
            if (fis != null)
               fis.close();
         }

         /* --------------------------------
          * den Stream zurückgeben
          * --------------------------------*/
         out.flush();
         facesContext.responseComplete();
      }
   }

}
