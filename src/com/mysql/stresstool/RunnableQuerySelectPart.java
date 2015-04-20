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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.Map;

import net.tc.utils.Utility;

import org.apache.commons.beanutils.MethodUtils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public  class RunnableQuerySelectPart  extends RunnableQuerySelectBasic  {

    /* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQuerySelectInterface#run()
	 */
    @Override
	public void run() {

    	super.run();
    }
    
	/**
	 * @param pkStart
	 * @param pkEnds
	 * @param select
	 * @return
	 */
    @Override 
    String generateSelectString(int pkStart, int pkEnds, String select) {

		int numberOfRangeKey = this.getNumberOfIntervalKeys();
		String joinAttribute = this.getJoinField();

		String primaryTable = "tbtest" + StressTool.getNumberFromRandomMinMax(1, getNumberOfprimaryTables());
		int maxJoinTableIndex = StressTool.getNumberFromRandomMinMax(1, getNumberOfJoinTables()).intValue();
		String secondaryChildRetieved = "tbtest_child" + StressTool.getNumberFromRandomMinMax(1, maxJoinTableIndex).intValue();

		String indexHint = "";
		if(super.isForceIndex() && this.getIndexName() != null){
			indexHint = " FORCE INDEX("+ this.getIndexName() +") "; 
		}

		
		/*
		 * Build the select on the base of condition passed
		 * If joins =0 then build a straight select respecting the filterMethod conditions
		 * Like range|in|equality 
		 * 
		 * If Join apply then build the query with the given child tables
		 * respecting the same filterMethod conditions
		 * */
		
		if(getDbType().equals("MySQL") && !this.isDoSimplePk() && getNumberOfJoinTables() == 0){
			select = "Select "
						+ primaryTable + ".a,b,c,counter,"
						+ primaryTable +".time,"
						+ primaryTable +".partitionid,"
						+ primaryTable +".strrecordtype" ;
			
			if(this.getSelectFilterMethod().toLowerCase().equals("range")){
				String[] arDates =  getOrderedRangeDate();
				select = select 
						+ "from "+ primaryTable 
						+ indexHint 
						+" where "+ primaryTable +".date < " 
						+ arDates[0] + " and " + primaryTable +".date < "+  arDates[1] 
						+ " limit " + this.getSelectLimit();
			}
			else if (this.getSelectFilterMethod().toLowerCase().equals("in")){

				String sPk ="";
				sPk = calculaltePK(pkStart, pkEnds, sPk);
				select = select + "from "+ primaryTable + indexHint +" where "+ primaryTable +".a IN ("+ sPk +") limit "+ this.getSelectLimit();
			}
			else
			{
				select = select + "from "+ primaryTable + indexHint + " where "+ primaryTable +".a = "+ pkStart +" limit "+ this.getSelectLimit();
			}
			
			
		}
	    else if(getDbType().equals("MySQL") && !this.isDoSimplePk() && getNumberOfJoinTables() > 0)
		{
			String childFieldsToShow = "";
			String childJoinsConditions = " from "+ primaryTable + indexHint;
			
			for(int idxf = 1; idxf <= maxJoinTableIndex; idxf++ ){
				childFieldsToShow = childFieldsToShow + ", tbtest_child" + idxf + ".a, tbtest_child"+ idxf +".time" ; 
				childJoinsConditions = childJoinsConditions + " LEFT OUTER JOIN tbtest_child" + idxf + " ON  "+ primaryTable + "."+ joinAttribute + " = tbtest_child" + idxf + ".a";
			}

			select = "Select "
								+ primaryTable +".a,b,c,counter,"
								+ primaryTable +".time,"
								+ primaryTable +".partitionid,"
								+ primaryTable +".strrecordtype, "
								+ secondaryChildRetieved +".stroperation" 
								+ childFieldsToShow 
								+ childJoinsConditions;
			
			if(this.getSelectFilterMethod().toLowerCase().equals("range")){
				String[] arDates =  getOrderedRangeDate();
				
				select = select 
							+ " where "
							+ primaryTable +".date > '" 
							+ arDates[0] + "' and " +  primaryTable +".date < '" + arDates[1] 
							+ "' limit "+ this.getSelectLimit();
			}
			else if (this.getSelectFilterMethod().toLowerCase().equals("in")){
				String sPk ="";
				sPk = calculaltePK(pkStart, pkEnds, sPk);
				select = select + " where " + primaryTable +".a IN ("+ sPk +") limit "+ this.getSelectLimit();
			}
			else
			{
				select = select + " where " + primaryTable +".a = "+ pkStart +" limit "+ this.getSelectLimit();
			}

		}
		else if(this.isDoSimplePk())
		{
			String sPk ="";
			sPk = calculaltePK(pkStart, pkEnds, sPk);
			
		    select =
		        "Select "+ primaryTable +".a,b,c,counter,"+ primaryTable +".time,strrecordtype ";
		    select = select +
		        "from "+ primaryTable +" where "+ primaryTable +".a in( " + sPk + " ) Limit "+ this.getSelectLimit() ;
			
		}
//		System.out.println(select);
		return select;
	}


}