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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.UUID;

import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

import org.apache.commons.beanutils.MethodUtils;

import com.sun.xml.internal.bind.v2.runtime.Name;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public  class RunnableQueryInsertEmployees extends RunnableInsertBasic {

	private ArrayList <String> Name = null;
	private ArrayList <String> LastName = null;
	private ArrayList <String> email = null;
	private Map city = null;
	private ArrayList country = null;
		
	public RunnableQueryInsertEmployees() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception ex)
		{
			// handle the error
		}

		//    Random rnd = new Random();

	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#run()
	 */
	public void run() {

		super.run();
	}

	/**
	 * This method allow classes that extends the basic 
	 * to query the database for local purpose
	 * @param conn
	 */
	public void executeLocalExtensions(Connection conn) {
		populateLocalInfo(conn);
		
	}


	private void populateLocalInfo(Connection conn){
            try{
	    
	    
    	    	Statement stmt = null;
                ResultSet rs = null;
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                String sqlQuery = "";
                
                /**
                 * Populate the single array taking information from the original Employees tables
                 * 
                 * City: Select ID, Name from City order by Name;
                 * 
                 * Country: select Code from Country order by Code;
                 * 
                 * Name: Select distinct first_name from employee 
                 * 
                 * +--------+------------+------------+-----------+--------+------------+---------+
		 * | emp_no | birth_date | first_name | last_name | gender | hire_date  | city_id |
                 * +--------+------------+------------+-----------+--------+------------+---------+
                 * |  10001 | 1953-09-02 | Georgi     | Facello   | M      | 1986-06-26 |    NULL |
                 * +--------+------------+------------+-----------+--------+------------+---------+
                 * 
                 * ABout date random a number starting 712223 and never more then 735630
                 * 
                 */
            Name = new ArrayList();
        	LastName = new ArrayList();
        	email = new ArrayList();
        	city = new SynchronizedMap();
        	//ArrayList country = null;
           
                sqlQuery = "Select distinct first_name from employees.employees order by first_name"; 
                rs = stmt.executeQuery(sqlQuery);
        	/**
        	 * Name first	
        	 */
                while(rs.next()){
                    Name.add(rs.getString(1));
                    
                }
                rs.close();
                

        	/**
        	 * Last second
        	 */
                sqlQuery = "Select distinct last_name from employees.employees order by first_name"; 
                rs = stmt.executeQuery(sqlQuery);
                while(rs.next()){
                    LastName.add(rs.getString(1));
                    
                }
                rs.close();
                
        	/**
        	 * City/Country 
        	 */
                sqlQuery = "Select ID, Name,CountryCode  from world.City order by Name;"; 
                rs = stmt.executeQuery(sqlQuery);
                while(rs.next()){
                    city.put(rs.getString(2), rs.getString(1)+"-"+rs.getString(3));
                    
                }
                rs.close();
                stmt.close();
                
                
                
            }
            catch(SQLException ex){ex.printStackTrace();}

            
            
	}
	@Override 
	Vector getTablesValues(boolean refresh) {

		String longtextFld = "";
		boolean lazy = false;
		int afld = 0;
		long counterFld = 0 ;

//		if(refresh && !lazyInsert1.equals(""))
//		{
//			lazy = true;
//			longtextFld = lazyLongText;
//		}
//		else{
//			if(operationShort)
//				longtextFld = StressTool.getStringFromRandom(254).substring(0,240);
//			else
//				longtextFld = StressTool.getStringFromRandom(40000);
//		}

		Vector v = new Vector();



		StringBuffer insert1 = new StringBuffer();
		StringBuffer insert2 = new StringBuffer();
		//        String uuid = UUID.randomUUID().toString();
		ArrayList <String> insertList1 = new ArrayList();
		ArrayList <String> insertList2 = new ArrayList();

		int pk = StressTool.getNumberFromRandom(2147483647).intValue();
		String insert1Str = "";
		
		
		

		if(getDbType().endsWith("MySQL"))
		{
			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
//			    +--------+------------+------------+-----------+--------+------------+---------+
//				 * | emp_no | birth_date | first_name | last_name | gender | hire_date  | city_id |
//		                 * +--------+------------+------------+-----------+--------+------------+---------+
		                 
				insert1.append("insert INTO tbtest" + iTable + " (emp_no,birth_date,first_name,last_name,gender,hire_date,city_id,CityName,CountryCode,UUID) VALUES");
				

				if(this.isUseBatchInsert())
				{



					for(int ibatch= 0 ; ibatch <=this.getIBatchInsert(); ibatch++ )
					{
					    String nameString = Name.get(StressTool.getNumberFromRandomMinMax(0, Name.size()-1).intValue());					
					    String lastNString = LastName.get(StressTool.getNumberFromRandomMinMax(0, LastName.size()-1).intValue());
					    String cityString  = (String)city.keySet().toArray()[StressTool.getNumberFromRandomMinMax(0, city.size()-1).intValue()];
					    String[] cityN_ISO= ((String)city.get(cityString)).split("-");
					    
					    int fromDaysBirth = StressTool.getNumberFromRandomMinMax(712223, 735630).intValue();
					    int fromDaysHire = StressTool.getNumberFromRandomMinMax(712223, 735630).intValue();
					    String gender ="";
					    if (fromDaysBirth % 2 == 0) {
						gender ="M";
					    } else {
						gender ="F";
					    }
					    
					    if (ibatch > 0){
						insert1.append(",");
					    }
					    insert1.append("(NULL,FROM_DAYS("+ fromDaysBirth +")," 
						    + "'" + nameString + "',"
						    + "'" + lastNString + "',"
						    + "'" + gender + "',"
						    + "FROM_DAYS("+ fromDaysHire + "),"
						    + cityN_ISO[0] + ","
						    + "'" + cityString + "',"
						    + "'" + cityN_ISO[1] + "',"
						    + "UUID())");

					}
				}
				else
				{
				    String nameString = Name.get(StressTool.getNumberFromRandomMinMax(0, Name.size()-1).intValue());					
				    String lastNString = LastName.get(StressTool.getNumberFromRandomMinMax(0, LastName.size()-1).intValue());
				    String cityString  = (String)city.keySet().toArray()[StressTool.getNumberFromRandomMinMax(0, city.size()-1).intValue()];
				    String[] cityN_ISO= ((String)city.get(cityString)).split("-");

				    int fromDaysBirth = StressTool.getNumberFromRandomMinMax(712223, 735630).intValue();
				    int fromDaysHire = StressTool.getNumberFromRandomMinMax(712223, 735630).intValue();
				    String gender ="";
				    if (fromDaysBirth % 2 == 0) {
					gender ="M";
				    } else {
					gender ="F";
				    }
				    insert1.append("(NULL,FROM_DAYS("+ fromDaysBirth +")," 
					    + "'" + nameString + "',"
					    + "'" + lastNString + "',"
					    + "'" + gender + "',"
					    + "FROM_DAYS("+ fromDaysHire + "),"
					    + cityN_ISO[0] + ","
					    + "'" + cityString + "',"
					    + "'" + cityN_ISO[1] + "',"
					    + "UUID())");


				}
				if(!insertList1.equals(""))  
					insertList1.add(insert1.toString());

				insert1.delete(0, insert1.length());
			}
		}
//		if(!this.doSimplePk)
//		{
//			if(dbType.endsWith("MySQL"))
//			{
//				String insert2Str = ""; 
//				String insert2bStr = "";
//				String thisDate = getRangeDate();
//				
//				for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
//					insert2Str = "";
//					
//					insert2Str = insert2Str  +"insert INTO tbtest_child" + iTable + " (a,date,stroperation) VALUES(";
//					insert2Str = insert2Str  + pk + ",'" + thisDate + "',";
//					if(lazy && !lazyInsert2.equals("")){
//						insert2bStr = lazyInsert2;
//					}
//					else{
//						lazyInsert2  =  "\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0";
//						insert2bStr = lazyInsert2;
//						//	        		insert2Str = insert2Str  + "\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0";
//						//	        		lazyInsert2 = insert2Str;
//					}
//
//					if(!insert2Str.equals("")) 
//						insertList2.add(insert2Str + insert2bStr);
//
//				}
//
//			}
//		}


		v.add(0,insertList1);
		v.add(1,insertList2);
		//    v.add(2, new Integer(pk));

		return v;

	}

	@Override
	public boolean createSchema(StressTool sTool) {

		// Custom schema creation this is the default for the stresstool but can be anything  
		String DropTables1 = "Drop table IF EXISTS tbtest";
		String DropTables2 = "Drop table IF EXISTS tbtest_child";

		String TruncateTables1 = "Truncate table tbtest";
		String TruncateTables2 = "Truncate table tbtest_child";

		Connection conn =null;
		Statement stmt = null;

		try {
			if(getJdbcUrlMap().get("dbType") != null &&  !((String)getJdbcUrlMap().get("dbType")).equals("MySQL"))
			{
				conn=DriverManager.getConnection((String)getJdbcUrlMap().get("dbType"),"test", "test");
			}
			else
				conn= DriverManager.getConnection((String)getJdbcUrlMap().get("jdbcUrl"));

			conn.setAutoCommit(false);
			stmt = conn.createStatement();


			StringBuffer sb = new StringBuffer();

			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
				sb.append("CREATE TABLE IF NOT EXISTS tbtest" + iTable );
				
//				if (this.isUseAutoIncrement()){
//					sb.append("`autoInc` bigint(11) AUTO_INCREMENT NOT NULL,");
//				}
//				sb.append(" `a` int(11) NOT NULL,"); 
//				sb.append(" `uuid` char(36) NOT NULL,");
//				sb.append(" `b` varchar(100) NOT NULL,");
//				sb.append(" `c` char(200)  NOT NULL,");
//				sb.append(" `counter` bigint(20) NULL, ");
//				sb.append(" `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,");
//				sb.append(" `partitionid` int NOT NULL DEFAULT 0,");
//				sb.append(" `date` DATE NOT NULL,");
//				sb.append(" `strrecordtype` char(3) NULL");
//				if (this.isUseAutoIncrement()){
//				    if(this.partitionType.equals("range")){
//					sb.append(", PRIMARY KEY  (`autoInc`,`date`),  INDEX `IDX_a` (a),  INDEX `IDX_uuid` (uuid) ");
//				    }
//				    else{
//					sb.append(", PRIMARY KEY  (`autoInc`,`partitionid`),  INDEX `IDX_a` (a),  INDEX `IDX_uuid` (uuid) ");
//				    }
//				}
//				else{
//					if(!this.doSimplePk)
//					    if(this.partitionType.equals("range")){
//					    	sb.append(", PRIMARY KEY  (`uuid`,`date`),  INDEX `IDX_a` (a) ");
//					    }
//					    else{
//						sb.append(", PRIMARY KEY  (`uuid`,`partitionid`),  INDEX `IDX_a` (a) ");
//					    }
//					else{ 
//					    if(this.partitionType.equals("range")){
//						sb.append(", PRIMARY KEY  (`a`,`date`),  INDEX `IDX_uuid` (uuid) ");
//					    }
//					    else{
//						sb.append(", PRIMARY KEY  (`a`,`partitionid`),  INDEX `IDX_uuid` (uuid) ");
//					    }
//					}
//				}
				sb.append("(`emp_no` int(4) unsigned AUTO_INCREMENT NOT NULL,`birth_date` date NOT NULL,`first_name` varchar(14) NOT NULL," +
						"`last_name` varchar(16) NOT NULL,`gender` enum('M','F') NOT NULL,`hire_date` date NOT NULL," +
						"`city_id` int(4) DEFAULT NULL,`CityName` varchar(150) DEFAULT NULL,`CountryCode` char(3) DEFAULT NULL," +
						"`UUID` char(36) DEFAULT NULL, PRIMARY KEY (`emp_no`)) ");
				
				sb.append(" ENGINE="+ sTool.tableEngine) ;

				if(!sb.toString().equals(""))
					stmt.addBatch(sb.toString());

				sb.delete(0, sb.length());
			}
			String tbts1 = sb.toString();


			sb = new StringBuffer();
			for(int iTable = 1 ; iTable <= this.getNumberOfSecondaryTables(); iTable++){
				sb.append("CREATE TABLE IF NOT EXISTS tbtest_child" + iTable);
				sb.append("(`a` int(11) NOT NULL,");
				sb.append("`bb` int(11) AUTO_INCREMENT NOT NULL,");
				sb.append(" `date` DATE NOT NULL,");
				sb.append(" `partitionid` int NOT NULL DEFAULT 0,");
				if(isOperationShort())
					sb.append(" `stroperation` VARCHAR(254)  NULL,");
				else
					sb.append(" `stroperation` TEXT(41845)  NULL,");

				sb.append(" `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP");
				sb.append(", PRIMARY KEY  (`a`,`bb`), UNIQUE(`bb`)");
				sb.append(") ENGINE="+ sTool.tableEngine);

				if(!sb.toString().equals(""))
					stmt.addBatch(sb.toString());

				sb.delete(0, sb.length());

			}
			String tbts2 = sb.toString();

			System.out.println(tbts1);
			if(!isDoSimplePk())
				System.out.println(tbts2);


			if(sTool.droptable)
			{
				System.out.println("****============================================================================*******");
				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait DROP table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute(DropTables1+iTable);
				}

				if(!isDoSimplePk()){
					for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
						System.out.println("**** Please wait DROP table tbtest_child" + iTable + " it could take a LOT of time *******");
						stmt.execute(DropTables2+iTable);
					}


				}
				stmt.execute("COMMIT");
				System.out.println("**** DROP finished *******");
				System.out.println("****============================================================================*******");

			}

			if(sTool.createtable)
				stmt.executeBatch();


			if(sTool.truncate)
			{
				System.out.println("****============================================================================*******");

				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait TRUNCATE table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute(TruncateTables1+iTable);
				}

				if(!isDoSimplePk()){
					for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
						System.out.println("**** Please wait TRUNCATE table tbtest_child" + iTable + " it could take a LOT of time *******");
						stmt.execute(TruncateTables2+iTable);
					}
				}
				System.out.println("**** TRUNCATE finish *******");
				System.out.println("****============================================================================*******");

			}


		} catch (Exception ex) {
			ex.printStackTrace(

			);
			return false;
		} finally {

			try {
				conn.close();
				return true;
			} catch (SQLException ex1) {
				ex1.printStackTrace();
				return false;
			}

		}		

	}



}
