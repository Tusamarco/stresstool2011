/**

 * <p>Title: MySQL StressTool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Marco Tusa Copyright (c) 2012</p>
 * @author Marco Tusa
 * @version 1.0
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * <p>Company: MySQL</p>
 *
 */

package com.mysql.stresstool;

import java.io.BufferedReader;
import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import net.tc.utils.SynchronizedMap;

import org.apache.commons.beanutils.MethodUtils;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RunnableQueryDeleteEmployees extends RunnableQueryDeleteBasic {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#run()
	 */
	public void run() {
		super.run();

	}

	/**
	 * This method execute the delete action whatever it will be, and return a
	 * Vector with objects the following values
	 * 
	 * @return V1 time taken in system time V2 value of the delete key minor V3
	 *         value of the delete key major
	 * @throws SQLException
	 */
	@Override
	public Vector<Long> executeDeleteActions(Connection conn) {

		long execTime = 0;
		long pkStart = 0;
		long pkEnds = 0;
		ArrayList<RowValue> childDeleteIds = new ArrayList();
		Map tables = new SynchronizedMap();

		Vector vReturn = new Vector();

		long timeStart = System.currentTimeMillis();
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			stmt.execute("BEGIN");

			String SQL = "select tablename,maxid from tbtestmax order by 1";
			Map<String, RowValue> tbtestmax = new SynchronizedMap();

			ResultSet rsToDelete = stmt.executeQuery(SQL);
			while (rsToDelete.next()) {
				RowValue tbtestmaxValue = new RowValue(rsToDelete.getString(1),
						"maxid");

				tbtestmaxValue.setValue(rsToDelete.getLong(2));
				tbtestmaxValue.setMin(StressTool.getNumberFromRandomMinMax(0,tbtestmaxValue.getMax().intValue()));
				tbtestmax.put(tbtestmaxValue.getTable(), tbtestmaxValue);

			}
			rsToDelete.close();

			if (tbtestmax.size() > 0) {
				for (int iTable = 1; iTable <= getNumberOfprimaryTables(); iTable++) {
					RowValue currentMax = tbtestmax.get("tbtest" + iTable);

					long maxToDelete = new Double(((double) this.getDeleterowmaxpct() * currentMax.getMax()) / 100).longValue();


					if (currentMax.getMax().longValue() > 0) {

						// childDeleteIds = new RowValue();
						int childCount = 0;
//						PreparedStatement pstmt=conn.prepareStatement("Delete from tbtest" + iTable + " where emp_no=?");
						
						for (long iCdelete = currentMax.min.longValue(); 
								iCdelete < currentMax.getMax().longValue(); 
								iCdelete += getDeleterowsinterval()) {

							RowValue rowValue = new RowValue("tbtest" + iTable,"maxid");
							rowValue.setMin(new Long(iCdelete));
							if (iCdelete + getDeleterowsinterval() > maxToDelete) {
								rowValue.setValue(new Long(iCdelete+= maxToDelete));
							} else {
								rowValue.setValue(new Long(iCdelete+= getDeleterowsinterval()));
							}
							childDeleteIds.add(rowValue);

							int rows = 0;
							
							for(long idelete = rowValue.getMin().longValue();idelete < rowValue.getMax(); idelete++){
//								pstmt.setLong(1, idelete);
//								rows = rows + pstmt.executeUpdate();
								stmt.execute("Delete from tbtest" + iTable + " where emp_no=" + idelete);
								stmt.execute("COMMIT");
							}
							
							if (rows > 0) {
								setTotalLineDeleted(getTotalLineDeleted()
										+ rows);
							}

							
						}
						tables.put("tbtest_child" + iTable, childDeleteIds);
//						pstmt.close();

					}
					stmt.execute("COMMIT");

				}
			}
		} catch (SQLException sqle) {
			if((sqle.getErrorCode() != 1205))
				sqle.printStackTrace();
		}
		/*
		 * No Need of this given the FK and action on them ... if no FK active
		 * then yes we nned to clean it up.
		 */
		if (!isDoSimplePk() && !super.isUseFK()) {
			childDeleteIds = new ArrayList();

			Iterator tableIt = tables.keySet().iterator();
			while (tableIt.hasNext()) {
				String tableName = (String) tableIt.next();
				childDeleteIds = (ArrayList<RowValue>) tables.get(tableName);
				try {
					for (int iAr = 0; iAr < childDeleteIds.size(); iAr++) {
						stmt.executeQuery("DELETE from " + tableName
								+ " where emp_no between "
								+ childDeleteIds.get(iAr).getMin() + " and "
								+ childDeleteIds.get(iAr).getMax());
						stmt.executeQuery("Commit");
					}
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				} finally {
					try {
						stmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}

		long timeEnds = System.currentTimeMillis();
		execTime = (timeEnds - timeStart);
		vReturn.add(new Long(execTime));
		vReturn.add(new Long(pkStart));
		vReturn.add(new Long(pkEnds));

		return vReturn;

	}

}
