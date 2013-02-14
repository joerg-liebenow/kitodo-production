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

package de.sub.goobi.modul;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import de.sub.goobi.forms.ModuleServerForm;

public class ModulListener implements ServletContextListener {
   private static final Logger myLogger = Logger.getLogger(ModulListener.class);

   public void contextInitialized(ServletContextEvent event) {
      myLogger.debug("Starte Modularisierung-Server", null);
      new ModuleServerForm().startAllModules();
      myLogger.debug("Gestartet: Modularisierung-Server", null);
   }

   public void contextDestroyed(ServletContextEvent event) {
      myLogger.debug("Stoppe Modularisierung-Server", null);
      new ModuleServerForm().stopAllModules();
      myLogger.debug("Gestoppt: Modularisierung-Server", null);
   }
}
