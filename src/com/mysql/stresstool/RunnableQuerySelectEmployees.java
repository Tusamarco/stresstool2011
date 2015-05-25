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
public  class RunnableQuerySelectEmployees  extends RunnableQuerySelectBasic  {

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
		
		String inCondition="";
		if(this.getSelectFilterMethod().toLowerCase().equals("in")){
			for(int icond = 0; icond <= 25; icond++){
				if(icond >0)
					inCondition = inCondition + ",";
				inCondition = inCondition + StressTool.getNumberFromRandomMinMax(21,75);
			}
		}
		else if(this.getSelectFilterMethod().toLowerCase().equals("match")) {
			
		}
		
		
		
		if(getDbType().equals("MySQL") && !this.isDoSimplePk() && getNumberOfJoinTables() == 0){
			select = "Select "+primaryTable+".emp_no,birth_date, TRUNCATE((datediff(now(),birth_date)/365),0) as age, first_name,last_name,gender,city_id,CityName,CountryCode ";
			
			if(this.getSelectFilterMethod().toLowerCase().equals("range")){
				select = select 
						+ "from "+ primaryTable 
						+ indexHint 
						+" where "+ primaryTable +".  (datediff(now(),birth_date)/365) > 20 and (datediff(now(),birth_date)/365) < " + StressTool.getNumberFromRandomMinMax(21,75) ;
			}
			else if (this.getSelectFilterMethod().toLowerCase().equals("in")){
				select = select + "from "+ primaryTable + indexHint +" where TRUNCATE((datediff(now(),birth_date)/365),0) IN ("+ inCondition +") ";//limit "+ this.getSelectLimit();
			}
			else
			{
				String sPk ="";
				sPk = calculaltePK(pkStart, pkEnds, sPk);
				select = select + "from "+ primaryTable + indexHint + " where "+ primaryTable +".emp_no = "+ StressTool.getNumberFromRandomMinMax(pkStart,pkEnds) ;
			}
			
			
		}
	    else if(getDbType().equals("MySQL") && !this.isDoSimplePk() && getNumberOfJoinTables() > 0)
		{
			String childFieldsToShow = "";
			String childJoinsConditions = " from "+ primaryTable + indexHint;
			
			for(int idxf = 1; idxf <= maxJoinTableIndex; idxf++ ){
				childFieldsToShow = childFieldsToShow + ", id, salary,from_date,to_date " ; 
				childJoinsConditions = childJoinsConditions + " LEFT OUTER JOIN tbtest_child" + idxf + " ON  "+ primaryTable + "."+ joinAttribute + " = tbtest_child" + idxf + ".emp_no ";
			}

			select = "Select "+primaryTable+".emp_no,birth_date, TRUNCATE((datediff(now(),birth_date)/365),0) as age, first_name,last_name,gender,city_id,CityName,CountryCode "
								+ childFieldsToShow 
								+ childJoinsConditions;
			
			if(this.getSelectFilterMethod().toLowerCase().equals("range")){
				select = select 
						+ indexHint 
						+" where  (datediff(now(),birth_date)/365) > 20 and (datediff(now(),birth_date)/365) < " + StressTool.getNumberFromRandomMinMax(21,75)
						+" and id is not null";

			}
			else if (this.getSelectFilterMethod().toLowerCase().equals("in")){

				select = select + " where TRUNCATE((datediff(now(),birth_date)/365),0) IN ("+ inCondition +") ";//limit "+ this.getSelectLimit();
			}
			else
			{
				String sPk ="";
				sPk = calculaltePK(pkStart, pkEnds, sPk);
				select = select + " where "+ primaryTable +".emp_no = "+ StressTool.getNumberFromRandomMinMax(pkStart,pkEnds) ;
			}

		}
		else if(this.isDoSimplePk())
		{
			String sPk ="";
			sPk = calculaltePK(pkStart, pkEnds, sPk);
			
			select = "Select "+primaryTable+".emp_no,birth_date, TRUNCATE((datediff(now(),birth_date)/365),0) as age, first_name,last_name,gender,city_id,CityName,CountryCode ";
		    select = select +
		        "from "+ primaryTable +" where "+ primaryTable +".emp_no in( " + sPk + " )";
			
		}
//		System.out.println(select);
		return select;
	}

	/**
	 * @return
	 * check for the max value of the table tbtest1 and for the min value return an array of int where element 0 is MIN element 1 is MAX
	 */
    @Override 
	int[] getMaxSelectValue(Connection conn) {
		//return StressTool.getNumberFromRandom(2147483647).intValue();
		String primaryTable = "tbtest" + StressTool.getNumberFromRandomMinMax(1, getNumberOfprimaryTables());
		
        Statement stmt = null;
        ResultSet rs = null;
        int[] values = new int[2];
        
        try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT MIN(emp_no) mi, MAX(emp_no) ma from " + primaryTable );
	        while(rs.next()){
	        		values[0] = rs.getInt("mi");
	        		values[1] = rs.getInt("ma");
	        }
	        
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			try{
				rs.close();
        	    stmt.close();
			}catch(Throwable th){th.printStackTrace();}
        	e.printStackTrace();
		}
        finally{
			try{
				rs.close();
        	    stmt.close();
			}catch(Throwable th){th.printStackTrace();}
		
        }
        return values;


	}
}