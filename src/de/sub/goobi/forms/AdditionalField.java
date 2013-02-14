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

import java.util.List;

import javax.faces.model.SelectItem;

public class AdditionalField {
	private String titel;
	private String wert = "";
	private boolean required = false;
	private String from = "prozess";
	private List<SelectItem> selectList;
	private boolean ughbinding = false;
	private String docstruct;
	private String metadata;
	private String isdoctype = "";
	private String isnotdoctype = "";
	private String initStart = ""; // defined in projects.xml
	private String initEnd = "";
	private boolean autogenerated = false;
	private ProzesskopieForm pkf;

	public AdditionalField(ProzesskopieForm inPkf) {
		pkf = inPkf;
	}

	public String getInitStart() {
		return initStart;
	}

	public void setInitStart(String newValue) {
		initStart = newValue;
		if (initStart == null) {
			initStart = "";
		}
		this.wert = initStart + this.wert;
	}

	public String getInitEnd() {
		return initEnd;
	}

	public void setInitEnd(String newValue) {
		initEnd = newValue;
		if (initEnd == null) {
			initEnd = "";
		}
		this.wert = this.wert + initEnd;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getWert() {
		return this.wert;
	}

	public void setWert(String newValue) {
		if (newValue == null || newValue.equals(initStart)) {
			newValue = "";
		}
		if (newValue.startsWith(initStart)) {
			this.wert = newValue + initEnd;
		} else {
			this.wert = initStart + newValue + initEnd;
		}
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String infrom) {
		if (infrom != null && infrom.length() != 0)
			this.from = infrom;
	}

	public List<SelectItem> getSelectList() {
		return selectList;
	}

	public void setSelectList(List<SelectItem> selectList) {
		this.selectList = selectList;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isUghbinding() {
		return ughbinding;
	}

	public void setUghbinding(boolean ughbinding) {
		this.ughbinding = ughbinding;
	}

	public String getDocstruct() {
		return docstruct;
	}

	public void setDocstruct(String docstruct) {
		this.docstruct = docstruct;
		if (this.docstruct == null)
			this.docstruct = "topstruct";
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getIsdoctype() {
		return isdoctype;
	}

	public void setIsdoctype(String isdoctype) {
		this.isdoctype = isdoctype;
		if (this.isdoctype == null)
			this.isdoctype = "";
	}

	public String getIsnotdoctype() {
		return isnotdoctype;
	}

	public void setIsnotdoctype(String isnotdoctype) {
		this.isnotdoctype = isnotdoctype;
		if (this.isnotdoctype == null)
			this.isnotdoctype = "";
	}

	public boolean getShowDependingOnDoctype() {

		/* wenn nix angegeben wurde, dann anzeigen */
		if (isdoctype.equals("") && isnotdoctype.equals("")) {
			return true;
		}

		/* wenn pflicht angegeben wurde */
		if (!isdoctype.equals("") && !isdoctype.contains(pkf.getDocType())) {
			return false;
		}

		/* wenn nur "darf nicht" angegeben wurde */
		if (!isnotdoctype.equals("") && isnotdoctype.contains(pkf.getDocType())) {
			return false;
		}

		return true;
	}

	/**
	 * @param autogenerated the autogenerated to set
	 */
	public void setAutogenerated(boolean autogenerated) {
		this.autogenerated = autogenerated;
	}

	/**
	 * @return the autogenerated
	 */
	public boolean getAutogenerated() {
		return autogenerated;
	}
}

/* =============================================================== */
