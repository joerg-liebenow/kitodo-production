<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://sourceforge.net/projects/jsf-comp/easysi"
	prefix="si"%>
<%-- 
	This file is part of the Goobi Application - a Workflow tool for the support
	of mass digitization.
	
	(c) 2013 Goobi. Digialisieren im Verein e.V. &lt;contact@goobi.org&gt;
	
	Visit the websites for more information.
	    		- http://www.goobi.org/en/
	    		- https://github.com/goobi
	
	This program is free software; you can redistribute it and/or modify it under
	the terms of the GNU General Public License as published by the Free Software
	Foundation; either version 2 of the License, or (at your option) any later
	version.
	
	This program is distributed in the hope that it will be useful, but WITHOUT
	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
	FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
	details.
	
	You should have received a copy of the GNU General Public License along with
	this program; if not, write to the Free Software Foundation, Inc., 59 Temple
	Place, Suite 330, Boston, MA 02111-1307 USA
	
	Linking this library statically or dynamically with other modules is making a
	combined work based on this library. Thus, the terms and conditions of the
	GNU General Public License cover the whole combination. As a special
	exception, the copyright holders of this library give you permission to link
	this library with independent modules to produce an executable, regardless of
	the license terms of these independent modules, and to copy and distribute
	the resulting executable under terms of your choice, provided that you also
	meet, for each linked independent module, the terms and conditions of the
	license of that module. An independent module is a module which is not
	derived from or based on this library. If you modify this library, you may
	extend this exception to your version of the library, but you are not obliged
	to do so. If you do not wish to do so, delete this exception statement from
	your version.
--%>

<%--  Calendar editor for newspapers --%>

<html>
<f:view locale="#{SpracheForm.locale}">
	<%@include file="/newpages/inc/head.jsp"%>
	<body onload="init()">
		<style type="text/css">
.titleManagement {
	float: left;
	margin-bottom: 5px;
	margin-right: 12px;
	width: 250px;
}

.titleManagement select {
	margin-bottom: 4px;
	width: 100%;
}

.titleManagement a {
	margin: 5px;
}

.titleContents {
	float: left;
	max-width: 600px;
	width: 100%;
}

.titleData {
	margin-bottom: 10px;
}

.fullWideBox {
	display: block;
	overflow: hidden;
	padding-left: 4px;
}

.fullWideLabel {
	float: left;
	padding-top: 5px;
}

.fullWideInput {
	margin-bottom: 3px;
	width: 100%;
}

.keepTogether {
	display: inline-block;
	margin-right: 6px;
}

.keepTogether input {
	max-width: 100px;
}

.issue {
	margin: 10px 0;
}

.bubble,.issueOption {
	font-size: 15pt;
	line-height: 8px;
}

.bubble {
	padding-right: 3px;
	vertical-align: sub;
}

.issueHeading {
	margin: 0 4px;
	width: 250px;
}

.deleteIssue {
	float: right;
	padding: 5px 0 0 5px;
}

.calendarSheet {
	border-collapse: collapse;
	table-layout: fixed;
	width: 100%;
	clear: both;
	width: 100%;
}

.calendarSheet caption {
	font-size: larger;
}

.calendarSheet td,.calendarSheet th {
	border: 1px solid;
}

.calendarSheet th {
	border-top: none;
}

.saturday {
	background-color: #B0E0E6;
}

.sunday {
	background-color: #FA8072;
}

.weekdayNoTitle {
	background-color: #C0C0C0;
}

.saturdayNoTitle {
	background-color: #778899;
}

.sundayNoTitle {
	background-color: #BC8F8F;
}

.issueOptions {
	padding-left: 3px;
}
</style>
		<script type="text/javascript">
			
		<%--
		 * The function deleteClickQuery() checks whether an issue shall or shall 
		 * not be deleted. The user is presented with a query whether it wants to
		 * delete the block. This is to prevent misclicks.
		 * 
		 * @return whether an issue can be deleted
		 --%>
			function deleteClickQuery() {
				return confirm("${msgs['calendar.issue.delete.query']}");
			}
		<%--
		 * The function init() is called after the DOM tree has been built. Unless
		 * the form is virginal, the button “apply changes” is hidden while not
		 * needed.
		 * 
		 * @return always true
		 --%>
			function init() {
				if (document.getElementById("form1:titleHeading").value
						.match(/\S/)) {
					document.getElementById("form1:applyChanges").style.display = "none";
				}
				return true;
			}
		<%--
		 * The function removeClickQuery() checks whether a title block shall or
		 * shall not be deleted. The user is presented with a query whether it wants
		 * to delete the block. This is to prevent misclicks. If there is only one
		 * block left, instead, the user is presented with an information that this
		 * isn’t allowed.
		 * 
		 * @return whether a title block can be deleted
		 --%>
			function removeClickQuery() {
				if (document.getElementById("form1:titlePicker").length >= 2) {
					return confirm("${msgs['calendar.title.remove.query']}");
				} else
					alert("${msgs['calendar.title.remove.disabled']}");
				return false;
			}
		<%--
		 * The function startEditTitle() is called whenever the data of the title
		 * block is being edited by the user. The button “apply changes” is shown.
		 * 
		 * @return always true
		 --%>
			function startEditTitle() {
				document.getElementById("form1:applyChanges").style.display = "inline";
				return true;
			}
		<%--
		 * The function titleDataIsValid() validates the title data typed in by the
		 * user.
		 * 
		 * The following requirements must be met:
		 * 		• The title must not be empty.
		 * 		• The dates must be well-formed.
		 * 
		 * @return whether the title data is valid
		 --%>
			function titleDataIsValid() {
				if (!document.getElementById("form1:titleHeading").value
						.match(/\S/)) {
					alert("${msgs['calendar.title.heading.invalid']}");
					document.getElementById("form1:titleHeading").focus();
					return false;
				}
				if (!document.getElementById("form1:firstAppearance").value
						.match(/^[0-3]\d\.[01]\d.\d{4}$/)) {
					alert("${msgs['calendar.title.firstAppearance.invalid']}");
					document.getElementById("form1:firstAppearance").focus();
					return false;
				}
				if (!document.getElementById("form1:lastAppearance").value
						.match(/^[0-3]\d\.[01]\d.\d{4}$/)) {
					alert("${msgs['calendar.title.lastAppearance.invalid']}");
					document.getElementById("form1:lastAppearance").focus();
					return false;
				}
				return true;
			}
		<%--
		 * The function titlePickerChangeQuery() checks whether silently changing
		 * the title block is possible. In the rare case that there are uncommited
		 * changes to the title block, the user is presented with a query whether it
		 * wants to continue, which implies that the changes will be lost.
		 * 
		 * @return whether the change request shall be processed
		 --%>
			function titlePickerChangeQuery() {
				if (document.getElementById("form1:applyChanges").style.display == "none") {
					return true;
				} else {
					return confirm("${msgs['calendar.title.lastAppearance.invalid']}");
				}
			}
		</script>
		<htm:table cellspacing="5" cellpadding="0" styleClass="layoutTable"
			align="center">
			<%@include file="/newpages/inc/tbl_Kopf.jsp"%>
			<htm:tr>
				<%@include file="/newpages/inc/tbl_Navigation.jsp"%>
				<htm:td valign="top" styleClass="layoutInhalt">

					<%-- ===================== Page main frame ===================== --%>

					<h:form id="form1" onsubmit="return titleDataIsValid()">

						<%-- Bread crumbs --%>

						<h:panelGrid width="100%" columns="1"
							styleClass="layoutInhaltKopf">
							<h:panelGroup>
								<h:commandLink value="#{msgs.startseite}" action="newMain" />
								<f:verbatim> &#8250;&#8250; </f:verbatim>
								<h:commandLink value="#{msgs.prozessverwaltung}"
									action="ProzessverwaltungAlle" />
								<f:verbatim> &#8250;&#8250; </f:verbatim>
								<h:commandLink value="#{msgs.einenNeuenProzessAnlegen}"
									action="#{ProzesskopieForm.GoToSeite1}" />
								<f:verbatim> &#8250;&#8250; </f:verbatim>
								<h:outputText value="#{msgs['calendar.header']}" />
							</h:panelGroup>
						</h:panelGrid>

						<htm:table border="0" align="center" width="100%" cellpadding="15">
							<htm:tr>
								<htm:td>
									<htm:h3>
										<h:outputText value="#{msgs['calendar.header']}" />
									</htm:h3>

									<%-- Global warnings and error messages --%>

									<h:messages globalOnly="true" errorClass="text_red"
										infoClass="text_blue" showDetail="true" showSummary="true"
										tooltip="true" />

									<%-- ===================== Page main content ====================== --%>

									<htm:div styleClass="blocksAndIssues">
										<htm:div styleClass="titleManagement">

											<%-- Select box to switch between already defined titles --%>
											<h:selectOneListbox styleClass="titlePicker" size="7"
												value="#{CalendarForm.titlePickerSelected}"
												onchange="if(titlePickerChangeQuery()){submit();}"
												id="titlePicker">
												<si:selectItems value="#{CalendarForm.titlePickerOptions}"
													var="item" itemLabel="#{item.label}"
													itemValue="#{item.value}" />
											</h:selectOneListbox>

											<%-- Buttons to add and remove titles --%>
											<h:commandLink value="#{msgs['calendar.title.add']}"
												action="#{CalendarForm.addTitleClick}" />
											<h:commandLink value="#{msgs['calendar.title.remove']}"
												action="#{CalendarForm.removeTitleClick}"
												onclick="if(!removeClickQuery()){return false;}" />
										</htm:div>

										<htm:div styleClass="titleContents">
											<%-- Input elements for base data --%>
											<htm:div styleClass="titleData">
												<htm:div>
													<h:outputLabel value="#{msgs['calendar.title.heading']}"
														styleClass="fullWideLabel" for="titleHeading" />
													<htm:span styleClass="fullWideBox">
														<h:inputText value="#{CalendarForm.titleHeading}"
															onkeydown="startEditTitle()" id="titleHeading"
															styleClass="fullWideInput" />
													</htm:span>
												</htm:div>

												<htm:div styleClass="keepTogether">
													<h:outputText
														value="#{msgs['calendar.title.firstAppearance']}" />
													<h:inputText value="#{CalendarForm.firstAppearance}"
														onkeydown="startEditTitle()" id="firstAppearance" />
												</htm:div>

												<htm:div styleClass="keepTogether">
													<h:outputText
														value="#{msgs['calendar.title.lastAppearance']}" />
													<h:inputText value="#{CalendarForm.lastAppearance}"
														onkeydown="startEditTitle()" id="lastAppearance" />
												</htm:div>

												<h:commandLink value="#{msgs['calendar.applyChanges']}"
													id="applyChanges" />
											</htm:div>

											<htm:div styleClass="issues">
												<t:dataList layout="simple" var="issue"
													value="#{CalendarForm.issues}">
													<htm:div styleClass="issue">
														<htm:span styleClass="fullWideLabel">
															<%-- bubble --%>
															<htm:span styleClass="bubble"
																style="color: #{issue.colour}">
																<h:outputText value="●" />
															</htm:span>

															<%-- Prefix text --%>
															<h:outputLabel value="#{msgs['calendar.issue']}"
																for="issueHeading" />
														</htm:span>

														<%-- Delete button --%>
														<h:commandLink value="#{msgs['calendar.issue.delete']}"
															action="#{issue.deleteClick}"
															onclick="if(!deleteClickQuery()){return false;}"
															styleClass="deleteIssue" />

														<%-- Issue name box --%>
														<htm:span styleClass="fullWideBox">
															<h:inputText value="#{issue.heading}" id="issueHeading"
																styleClass="fullWideInput" />
														</htm:span>

														<%-- Days of week --%>
														<htm:div styleClass="daysOfWeek">
															<htm:div styleClass="keepTogether">
																<h:selectBooleanCheckbox id="monday"
																	value="#{issue.monday}" onchange="submit()" />
																<h:outputLabel value="#{msgs['calendar.issue.monday']}"
																	for="monday" />
															</htm:div>

															<htm:div styleClass="keepTogether">
																<h:selectBooleanCheckbox id="tuesday"
																	value="#{issue.tuesday}" onchange="submit()" />
																<h:outputLabel value="#{msgs['calendar.issue.tuesday']}"
																	for="tuesday" />
															</htm:div>

															<htm:div styleClass="keepTogether">
																<h:selectBooleanCheckbox id="wednesday"
																	value="#{issue.wednesday}" onchange="submit()" />
																<h:outputLabel
																	value="#{msgs['calendar.issue.wednesday']}"
																	for="wednesday" />
															</htm:div>

															<htm:div styleClass="keepTogether">
																<h:selectBooleanCheckbox id="thursday"
																	value="#{issue.thursday}" onchange="submit()" />
																<h:outputLabel
																	value="#{msgs['calendar.issue.thursday']}"
																	for="thursday" />
															</htm:div>

															<htm:div styleClass="keepTogether">
																<h:selectBooleanCheckbox id="friday"
																	value="#{issue.friday}" onchange="submit()" />
																<h:outputLabel value="#{msgs['calendar.issue.friday']}"
																	for="friday" />
															</htm:div>

															<htm:div styleClass="keepTogether">
																<h:selectBooleanCheckbox id="saturday"
																	value="#{issue.saturday}" onchange="submit()" />
																<h:outputLabel
																	value="#{msgs['calendar.issue.saturday']}"
																	for="saturday" />
															</htm:div>

															<htm:div styleClass="keepTogether">
																<h:selectBooleanCheckbox id="sunday"
																	value="#{issue.sunday}" onchange="submit()" />
																<h:outputLabel value="#{msgs['calendar.issue.sunday']}"
																	for="sunday" />
															</htm:div>
														</htm:div>
													</htm:div>
												</t:dataList>
												<%-- Add button --%>
												<h:commandLink value="#{msgs['calendar.issue.add']}"
													action="#{CalendarForm.addIssueClick}" />
											</htm:div>

										</htm:div>
									</htm:div>

									<%-- Calender sheet --%>

									<htm:table styleClass="calendarSheet">
										<htm:caption>
											<h:commandLink value="←"
												action="#{CalendarForm.backwardClick}" styleClass="backward" />
											<h:outputText value="#{CalendarForm.year}" />
											<h:commandLink value="→"
												action="#{CalendarForm.forwardClick}" styleClass="forward" />
										</htm:caption>
										<htm:tr>
											<htm:th>
												<h:outputText
													value="#{msgs['calendar.sheet.column.january']}" />
											</htm:th>
											<htm:th>
												<h:outputText
													value="#{msgs['calendar.sheet.column.february']}" />
											</htm:th>
											<htm:th>
												<h:outputText value="#{msgs['calendar.sheet.column.march']}" />
											</htm:th>
											<htm:th>
												<h:outputText value="#{msgs['calendar.sheet.column.april']}" />
											</htm:th>
											<htm:th>
												<h:outputText value="#{msgs['calendar.sheet.column.may']}" />
											</htm:th>
											<htm:th>
												<h:outputText value="#{msgs['calendar.sheet.column.june']}" />
											</htm:th>
											<htm:th>
												<h:outputText value="#{msgs['calendar.sheet.column.july']}" />
											</htm:th>
											<htm:th>
												<h:outputText
													value="#{msgs['calendar.sheet.column.august']}" />
											</htm:th>
											<htm:th>
												<h:outputText
													value="#{msgs['calendar.sheet.column.september']}" />
											</htm:th>
											<htm:th>
												<h:outputText
													value="#{msgs['calendar.sheet.column.october']}" />
											</htm:th>
											<htm:th>
												<h:outputText
													value="#{msgs['calendar.sheet.column.november']}" />
											</htm:th>
											<htm:th>
												<h:outputText
													value="#{msgs['calendar.sheet.column.december']}" />
											</htm:th>
										</htm:tr>
										<t:dataList layout="simple" var="row"
											value="#{CalendarForm.calendarSheet}">
											<htm:tr>
												<t:dataList layout="simple" var="cell" value="#{row}">
													<htm:td styleClass="#{cell.styleClass}">
														<h:outputText value="#{cell.day}" />
														<htm:span styleClass="issueOptions">
															<t:dataList layout="simple" var="issueOption"
																value="#{cell.issues}">
																<h:commandLink value="●​"
																	style="color: #{issueOption.colour};"
																	title="#{issueOption.issue} #{msgs['calendar.sheet.issue.selected']}"
																	styleClass="issueOption"
																	action="#{issueOption.unselectClick}"
																	rendered="#{issueOption.selected}" />
																<h:commandLink value="○​"
																	style="color: #{issueOption.colour};"
																	title="#{issueOption.issue} #{msgs['calendar.sheet.issue.notSelected']}"
																	styleClass="issueOption"
																	action="#{issueOption.selectClick}"
																	rendered="#{not issueOption.selected}" />
															</t:dataList>
														</htm:span>
													</htm:td>
												</t:dataList>
											</htm:tr>
										</t:dataList>
									</htm:table>

									<%-- ===================== End page main content ====================== --%>

								</htm:td>
							</htm:tr>
						</htm:table>
					</h:form>

					<%-- ===================== End page main frame ===================== --%>

				</htm:td>
			</htm:tr>
			<%@include file="/newpages/inc/tbl_Fuss.jsp"%>
		</htm:table>
	</body>
</f:view>
</html>
