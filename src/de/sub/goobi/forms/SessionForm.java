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

package de.sub.goobi.forms;
//TODO: Use generics
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.sub.goobi.beans.Benutzer;

/**
 * Die Klasse SessionForm für den überblick über die aktuell offenen Sessions 
 * 
 * @author Steffen Hankiewicz
 * @version 1.00 - 16.01.2005
 */
public class SessionForm {

   private int sessionZeit = 3600 * 2; // 2 Stunden
   private List alleSessions = new ArrayList();
   private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
   private String aktuelleZeit = formatter.format(new Date());
   private String bitteAusloggen = "";
      
   

   public int getAktiveSessions() {
      if (alleSessions == null)
         return 0;
      else
         return alleSessions.size();
   }

   

   public String getAktuelleZeit() {
      return aktuelleZeit;
   }

   

	public List getAlleSessions() {
		try {
			return alleSessions;
		} catch (RuntimeException e) {
			return null;
		}
	}

   

   @SuppressWarnings("unchecked")
private void sessionAdd(HttpSession insession) {

      insession.setMaxInactiveInterval(sessionZeit);
      
      //TODO: Remove this, it's ugly and evil.
      HashMap map = new HashMap();
      map.put("id", insession.getId());
      map.put("created", formatter.format(new Date()));
      map.put("last", formatter.format(new Date()));
      map.put("last2", Long.valueOf(System.currentTimeMillis()));
      map.put("user", " - ");
      map.put("userid", Integer.valueOf(0));
      map.put("session", insession);
      map.put("browserIcon", "none.gif");
      FacesContext context = FacesContext.getCurrentInstance();
      if (context != null) {
         HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
         
         map.put("address", request.getRemoteAddr());
         String mybrowser = request.getHeader("User-Agent");
         if (mybrowser==null) mybrowser="-";
         map.put("browser", mybrowser);
         if (mybrowser.indexOf("Gecko") > 0)
            map.put("browserIcon", "mozilla.png");
         if (mybrowser.indexOf("Firefox") > 0)
            map.put("browserIcon", "firefox.png");
         if (mybrowser.indexOf("MSIE") > 0)
            map.put("browserIcon", "ie.png");
         if (mybrowser.indexOf("Opera") > 0)
            map.put("browserIcon", "opera.gif");
         if (mybrowser.indexOf("Safari") > 0)
            map.put("browserIcon", "safari.gif");
         if (mybrowser.indexOf("Konqueror") > 0)
            map.put("browserIcon", "konqueror.gif");
         if (mybrowser.indexOf("Netscape") > 0)
            map.put("browserIcon", "netscape.gif");
      }
      alleSessions.add(map);

   }

   

   @SuppressWarnings("unchecked")
private void sessionsAufraeumen() {
      List temp = new ArrayList(alleSessions);
      for (Iterator iter = temp.iterator(); iter.hasNext();) {
         HashMap map = (HashMap) iter.next();
         long differenz = System.currentTimeMillis() - ((Long) map.get("last2")).longValue();

         if (differenz / 1000 > sessionZeit || map.get("address") == null || (map.get("user").equals("- ausgeloggt - ")) ){
            alleSessions.remove(map);
         }
      }
   }

   

   @SuppressWarnings("unchecked")
public void sessionAktualisieren(HttpSession insession) {

      boolean gefunden = false;
      aktuelleZeit = formatter.format(new Date());
      for (Iterator iter = alleSessions.iterator(); iter.hasNext();) {
         HashMap map = (HashMap) iter.next();
         if (map.get("id").equals(insession.getId())) {
            map.put("last", formatter.format(new Date()));
            map.put("last2", Long.valueOf(System.currentTimeMillis()));
            gefunden = true;
            break;
         }
      }
      if (!gefunden)
         sessionAdd(insession);
      sessionsAufraeumen();

   }

   

   @SuppressWarnings("unchecked")
public void sessionBenutzerAktualisieren(HttpSession insession, Benutzer inBenutzer) {

      for (Iterator iter = alleSessions.iterator(); iter.hasNext();) {
         HashMap map = (HashMap) iter.next();
         if (map.get("id").equals(insession.getId())) {
            if (inBenutzer != null) {
               insession.setAttribute("User", inBenutzer.getNachVorname());
               map.put("user", inBenutzer.getNachVorname());
               map.put("userid", inBenutzer.getId());
               insession.setMaxInactiveInterval(inBenutzer.getSessiontimeout());
            } else {
               map.put("user", "- ausgeloggt - ");
               map.put("userid", Integer.valueOf(0));
            }
            break;
         }
      }

   }

   

   /* prüfen, ob der Benutzer in einer anderen Session aktiv ist */
   public boolean BenutzerInAndererSessionAktiv(HttpSession insession, Benutzer inBenutzer) {
      boolean rueckgabe = false;
      //TODO: Don't use Iterators
      for (Iterator iter = alleSessions.iterator(); iter.hasNext();) {
         HashMap map = (HashMap) iter.next();
         boolean sessiongleich = map.get("id").equals(insession.getId());
         boolean nutzergleich = inBenutzer.getId().intValue() == ((Integer) map.get("userid")).intValue();
         if (!sessiongleich && nutzergleich) {
            rueckgabe = true;
            break;
         }
      }
      return rueckgabe;
   }

   

   @SuppressWarnings("unchecked")
public void alteSessionsDesSelbenBenutzersAufraeumen(HttpSession inSession, Benutzer inBenutzer) {
      List alleSessionKopie = new ArrayList(alleSessions);
    //TODO: Don't use Iterators
      for (Iterator iter = alleSessionKopie.iterator(); iter.hasNext();) {
         HashMap map = (HashMap) iter.next();
         boolean sessiongleich = map.get("id").equals(inSession.getId());
         boolean nutzergleich = inBenutzer.getId().intValue() == ((Integer) map.get("userid")).intValue();
         if (!sessiongleich && nutzergleich) {
            HttpSession tempSession = (HttpSession) map.get("session");
            try {
               if (tempSession!=null)
                  tempSession.invalidate();
            } catch (RuntimeException e) {
            }
            alleSessions.remove(map);
         }
      }
   }

   

   public String getBitteAusloggen() {
      return bitteAusloggen;
   }

   

   public void setBitteAusloggen(String bitteAusloggen) {
      this.bitteAusloggen = bitteAusloggen;
   }
   
   
}
