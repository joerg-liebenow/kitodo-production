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

package org.goobi.production.flow.statistics.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.goobi.production.flow.statistics.IDataSource;
import org.goobi.production.flow.statistics.IStatisticalQuestion;
import org.goobi.production.flow.statistics.IStatisticalQuestionLimitedTimeframe;
import org.goobi.production.flow.statistics.enums.CalculationUnit;
import org.goobi.production.flow.statistics.enums.StatisticsMode;
import org.goobi.production.flow.statistics.enums.TimeUnit;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import de.intranda.commons.chart.renderer.ChartRenderer;
import de.intranda.commons.chart.renderer.IRenderer;
import de.intranda.commons.chart.results.DataRow;
import de.intranda.commons.chart.results.DataTable;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.Messages;
import de.sub.goobi.helper.enums.HistoryEventType;

/*****************************************************************************
 * Implementation of {@link IStatisticalQuestion}. 
 * Statistical Request with predefined Values in data Table
 * 
 * @author Wulf Riebensahm
 ****************************************************************************/
public class StatQuestCorrections implements
		IStatisticalQuestionLimitedTimeframe {

	private Date timeFilterFrom;
	private TimeUnit timeGrouping;
	private Date timeFilterTo;

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#setTimeUnit(org.goobi.production.flow.statistics.enums.TimeUnit)
	 */
	public void setTimeUnit(TimeUnit timeGrouping) {
		this.timeGrouping = timeGrouping;
	}

	private TimeUnit getTimeUnit() {
		if (timeGrouping == null) {
			throw new NullPointerException(
					"The called method in StatQuestCorrection requires that TimeUnit was set");
		}
		return this.timeGrouping;
	}

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#getDataTables(org.goobi.production.flow.statistics.IDataSource)
	 */
	public List<DataTable> getDataTables(IDataSource dataSource) {

		List<DataTable> allTables = new ArrayList<DataTable>();

		IEvaluableFilter originalFilter;

		if (dataSource instanceof IEvaluableFilter) {
			originalFilter = (IEvaluableFilter) dataSource;
		} else {
			throw new UnsupportedOperationException(
					"This implementation of IStatisticalQuestion needs an IDataSource for method getDataSets()");
		}

		//gathering IDs from the filter passed by dataSource
		List<Integer> IDlist = null;
		try {
			IDlist = originalFilter.getIDList();
		} catch (UnsupportedOperationException e) {
		}

		// adding time restrictions
		String natSQL = new SQLStepRequests(timeFilterFrom, timeFilterTo,
				getTimeUnit(), IDlist).getSQL(HistoryEventType.stepError, null,
				false, false);

		Session session = Helper.getHibernateSession();

		SQLQuery query = session.createSQLQuery(natSQL);

		//needs to be there otherwise an exception is thrown
		query.addScalar("stepCount", Hibernate.DOUBLE);
		query.addScalar("intervall", Hibernate.STRING);

		List list = query.list();

		DataTable dtbl = new DataTable(StatisticsMode.getByClassName(
				this.getClass()).getTitle()
				+ Messages.getString("_(number)"));

		DataRow dataRow;

		// each data row comes out as an Array of Objects
		// the only way to extract the data is by knowing
		// in which order they come out 
		for (Object obj : list) {
			dataRow = new DataRow(null);
			Object[] objArr = (Object[]) obj;
			try {

				// getting localized time group unit

				//setting row name with date/time extraction based on the group

				dataRow.setName(new Converter(objArr[1]).getString() + "");
				//dataRow.setName(new converter(objArr[2]).getString());

				dataRow.addValue(Messages.getString("Corrections/Errors"),
						(new Converter(objArr[0]).getDouble()));

			} catch (Exception e) {
				dataRow.addValue(e.getMessage(), new Double(0));
			}

			//finally adding dataRow to DataTable and fetching next row
			dtbl.addDataRow(dataRow);
		}

		// a list of DataTables is expected as return Object, even if there is only one 
		// Data Table as it is here in this implementation
		dtbl.setUnitLabel(Messages.getString(getTimeUnit()
				.getSingularTitle()));
		allTables.add(dtbl);
		return allTables;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#setCalculationUnit(org.goobi.production.flow.statistics.enums.CalculationUnit)
	 */
	public void setCalculationUnit(CalculationUnit cu) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestionLimitedTimeframe#setTimeFrame(java.util.Date, java.util.Date)
	 */
	public void setTimeFrame(Date timeFrom, Date timeTo) {
		this.timeFilterFrom = timeFrom;
		this.timeFilterTo = timeTo;

	}

	/*
	 * (non-Javadoc)
	 * @see org.goobi.production.flow.statistics.IStatisticalQuestion#isRendererInverted(de.intranda.commons.chart.renderer.IRenderer)
	 */
	public Boolean isRendererInverted(IRenderer inRenderer) {
		return inRenderer instanceof ChartRenderer;
	}

	public String getNumberFormatPattern() {
		return "#";
	}

}
