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

package de.sub.goobi.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

import de.sub.goobi.beans.Projekt;
import de.sub.goobi.helper.Helper;

public class ConfigProjects {
	XMLConfiguration config;
	private String projektTitel;
	private static final Logger logger = Logger.getLogger(ConfigProjects.class);

	public ConfigProjects(Projekt inProject) throws IOException {
		this(inProject, new Helper().getGoobiConfigDirectory() + "projects.xml");
	}

	public ConfigProjects(Projekt inProject, String configPfad) throws IOException {
		if (!(new File(configPfad)).exists())
			throw new IOException("File not found: " + configPfad);
		try {
			config = new XMLConfiguration(configPfad);
		} catch (ConfigurationException e) {
			logger.error(e);
			config = new XMLConfiguration();
		}
		config.setListDelimiter('&');
		config.setReloadingStrategy(new FileChangedReloadingStrategy());

		int countProjects = config.getMaxIndex("project");
		for (int i = 0; i <= countProjects; i++) {
			String title = config.getString("project(" + i + ")[@name]");
			if (title.equals(inProject.getTitel())) {
				projektTitel = "project(" + i + ").";
				break;
			}
		}

		try {
			config.getBoolean(projektTitel + "createNewProcess.opac[@use]");
		} catch (NoSuchElementException e) {
			projektTitel = "project(0).";
		}
		
	}

	

	/**
	 * Ermitteln eines bestimmten Paramters der Konfiguration als String
	 * @return Paramter als String
	 */
	public String getParamString(String inParameter) {
		try {
			config.setListDelimiter('&');
			String rueckgabe = config.getString(projektTitel + inParameter);
			return cleanXmlFormatedString(rueckgabe);
		} catch (RuntimeException e) {
			logger.error(e);
			return null;
		}
	}

	

	private String cleanXmlFormatedString(String inString) {
		if (inString != null) {
			inString = inString.replaceAll("\t", " ");
			inString = inString.replaceAll("\n", " ");
			while (inString.contains("  ")) {
				inString = inString.replaceAll("  ", " ");
			}
		}
		return inString;
	}

	

	/**
	 * Ermitteln eines bestimmten Paramters der Konfiguration mit Angabe eines Default-Wertes
	 * @return Paramter als String
	 */
	public String getParamString(String inParameter, String inDefaultIfNull) {
		try {
			config.setListDelimiter('&');
			String myParam = projektTitel + inParameter;
			String rueckgabe = config.getString(myParam, inDefaultIfNull);
			return cleanXmlFormatedString(rueckgabe);
		} catch (RuntimeException e) {
			return inDefaultIfNull;
		}
	}

	

	/**
	 * Ermitteln eines boolean-Paramters der Konfiguration
	 * @return Paramter als String
	 */
	public boolean getParamBoolean(String inParameter) {
		try {
			return config.getBoolean(projektTitel + inParameter);
		} catch (RuntimeException e) {
			return false;
		}
	}

	

	/**
	 * Ermitteln eines long-Paramters der Konfiguration
	 * @return Paramter als Long
	 */
	public long getParamLong(String inParameter) {
		try {
			return config.getLong(projektTitel + inParameter);
		} catch (RuntimeException e) {
			logger.error(e);
			return 0;
		}
	}

	

	/**
	 * Ermitteln einer Liste von Paramtern der Konfiguration
	 * @return Paramter als List
	 */
	@SuppressWarnings("unchecked")
	public List<String> getParamList(String inParameter) {
		try {
			return (List<String>) config.getList(projektTitel + inParameter);
		} catch (RuntimeException e) {
			logger.error(e);
			return new ArrayList<String>();
		}
	}

}
