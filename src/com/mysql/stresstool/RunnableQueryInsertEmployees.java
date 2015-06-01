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

import javax.management.InvalidApplicationException;

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
public  class RunnableQueryInsertEmployees extends RunnableQueryInsertBasic {

	private ArrayList <String> Name = null;
	private ArrayList <String> LastName = null;
	private ArrayList <String> email = null;
	private Map city = null;
	private ArrayList <String> country = null;
	private ArrayList <String> departments = null;
	private ArrayList <String> titles = null;
	private Integer today = 0; 
	private ArrayList <Long> emp_max = new ArrayList();
	Map tableEmpNo = new SynchronizedMap(0);
	
		
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
		if(today == 0 )
			populateLocalInfo(conn);
		try{
            /**
             * employees max - min    
             */
		ArrayList<employeeShort> rowValueempNo = new ArrayList();
		Long min = (long) 0;
		ResultSet rs = null;
		Statement stmt = null;
	        conn.setAutoCommit(false);
	        stmt = conn.createStatement(); 
	        
	        
	        rs = stmt.executeQuery("select min(maxid) as min from tbtestmax ");
                while(rs.next()){
                	min=(Long) (rs.getLong(1));
                    
                }
                rs.close();
               
                if (min > 1 ){
                	emp_max.clear();
                	Long maxId = (long)2; 
                	for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
                	    	rs = stmt.executeQuery("select max(emp_no) from tbtest" + iTable );
                	    	while(rs.next()){
                	    	    maxId = rs.getLong(1);
                	    	    emp_max.add((Long) (maxId));
                	    	}
//                	    	tableEmpNo.put("tbtest"+iTable, maxId);
                	    	try{
                	    	    stmt.execute("update tbtestmax  set maxid="+maxId+" where tablename = 'tbtest"+ iTable +"'");
                	    	    conn.commit();
                	    	}
                	    	catch (SQLException ssq){ssq.printStackTrace();}
                	}
//                	rs = stmt.executeQuery("select maxid as max, tablename from tbtestmax order by 2 ");

                	for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
	                	
	                	String sql = "select emp_no,to_days(hire_date) from tbtest" + iTable + " where linked=0 order by emp_no limit  " 
	                			+ StressTool.getNumberFromRandomMinMax(0, maxId.intValue()/getNumberOfprimaryTables()) 
	                			+ ","  
	                			+ this.getIBatchInsert();
	                	rs = stmt.executeQuery(sql);
	                	employeeShort rv = null;
	                	while(rs.next()){
	                		rv = new employeeShort(rs.getLong(1),rs.getLong(2));
	                		rowValueempNo.add(rv);
	                	}
	                	tableEmpNo.put("tbtest"+iTable, rowValueempNo);
	                	rs.close();
	                }
	                
//	                 update tbtest1 join tbtest_child1 on tbtest1.emp_no=tbtest_child1.emp_no set linked=1 where linked
                }
                else
                {
                	for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
                	    try{
                		stmt.execute("replace into tbtestmax values ('tbtest" + iTable + "', 2)" );
                		conn.commit();
                	    }catch(SQLException ssq){ssq.printStackTrace();}
                	}                    
                }
                if(!rs.isClosed())
                		rs.close();
                stmt.close();
                rs =null;
                stmt = null;
			
		}
		catch(SQLException exq){
			
		}
		
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
        	departments = new ArrayList();
        	titles = new ArrayList();
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
        	/**
        	 * departments 
        	 */
                sqlQuery = "SELECT dept_name FROM employees.departments order by 1;"; 
                rs = stmt.executeQuery(sqlQuery);
                while(rs.next()){
                	departments.add(rs.getString(1));                    
                }
                rs.close();
        	/**
        	 * titles 
        	 */
                sqlQuery = "SELECT distinct title FROM employees.titles order by 1;"; 
                rs = stmt.executeQuery(sqlQuery);
                while(rs.next()){
                	titles.add(rs.getString(1));                    
                }
                rs.close();
                
            /**
             * Today    
             */
                sqlQuery = "select to_days(now()) as today;"; 
                rs = stmt.executeQuery(sqlQuery);
                while(rs.next()){
                    today= (Integer) (rs.getInt(1));
                    
                }
                rs.close();
                stmt.close();
                
            }
            catch(SQLException ex){ex.printStackTrace();}

            
            
	}
	@SuppressWarnings("unchecked")
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
		StringBuffer insert3 = new StringBuffer();
		//        String uuid = UUID.randomUUID().toString();
		ArrayList <String> insertList1 = new ArrayList();
		ArrayList <String> insertList2 = new ArrayList();

		int pk = StressTool.getNumberFromRandom(2147483647).intValue();
		String insert1Str = "";
		Map updateId = new SynchronizedMap();
		
		
		

		if(getDbType().endsWith("MySQL"))
		{
			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
//			    +--------+------------+------------+-----------+--------+------------+---------+
//				 * | emp_no | birth_date | first_name | last_name | gender | hire_date  | city_id |
//		                 * +--------+------------+------------+-----------+--------+------------+---------+
		                 
				insert1.append("insert INTO tbtest" + iTable + " (emp_no,birth_date,first_name,last_name,gender,hire_date,city_id,CityName,CountryCode,UUID) VALUES");
				if(tableEmpNo.size()  >0 )
						insert2.append("insert INTO tbtest_child" + iTable + " (emp_no,id,salary,from_date,to_date,dept_name,title) VALUES");
				insert3.append(0);

				if(this.isUseBatchInsert())
				{



					for(int ibatch= 0 ; ibatch <=this.getIBatchInsert(); ibatch++ )
					{
					    String nameString = Name.get(StressTool.getNumberFromRandomMinMax(0, Name.size()-1).intValue());					
					    String lastNString = LastName.get(StressTool.getNumberFromRandomMinMax(0, LastName.size()-1).intValue());
					    String cityString  = (String)city.keySet().toArray()[StressTool.getNumberFromRandomMinMax(0, city.size()-1).intValue()];
					    String[] cityN_ISO= ((String)city.get(cityString)).split("-");
					    
					    int fromDaysBirth = StressTool.getNumberFromRandomMinMax(712223, today - 7300).intValue();
					    int fromDaysHire = StressTool.getNumberFromRandomMinMax(fromDaysBirth + 7300, today).intValue();
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

//					    if(emp_max.size() >0 && emp_max.get( iTable -1 ).intValue() > 10){
//					    	Integer emp_no = StressTool.getNumberFromRandomMinMax(1, emp_max.get(iTable -1).intValue()).intValue();
//					    	insert3.append(","+ emp_no);
//					    	insert2.append("("
//					    		+ emp_no
//					    		+ ", Null"
//					    		+ ", " + StressTool.getNumberFromRandomMinMax(1000, 1000000).intValue()
//					    		+ ", " + "FROM_DAYS("+ fromDaysHire + ")"
//					    		+ ", " + "FROM_DAYS("+ StressTool.getNumberFromRandomMinMax(fromDaysHire,737060) + ")"
//					    		+ ", '" + departments.get(StressTool.getNumberFromRandomMinMax(0, departments.size()).intValue()) + "'"
//					    		+ ", '" + titles.get(StressTool.getNumberFromRandomMinMax(0, titles.size()).intValue()) + "'"
//					    		+ ")");
					    	
//					    	System.out.println(insert2.toString());
						    		
//					    }
					
					}
				    if(tableEmpNo.size() > 0){
				    	ArrayList<employeeShort> rowValueempNo = (ArrayList) tableEmpNo.get("tbtest"+iTable);
				    	Iterator it = rowValueempNo.iterator();
				    	int iLinked =0;
				    	while(it.hasNext()){
				    		employeeShort rv = (employeeShort)it.next();
				    		Long emp_no = rv.getEmpNo();
				    		Long fromDaysHire = rv.getHiredDateDay();
					    	if(iLinked > 0){
					    		insert2.append(",");
					    	}
					    	else if(iLinked > 0 
					    			&& iLinked > getIBatchInsert()){
					    		break;
					    	}
					    	updateId.put(emp_no,0);
				    		insert2.append("("
						    		+ emp_no
						    		+ ", Null"
						    		+ ", " + StressTool.getNumberFromRandomMinMax(1000, 1000000).intValue()
						    		+ ", " + "FROM_DAYS("+ fromDaysHire + ")"
						    		+ ", " + "FROM_DAYS("+ StressTool.getNumberFromRandomMinMax(fromDaysHire.intValue(),737060) + ")"
						    		+ ", '" + departments.get(StressTool.getNumberFromRandomMinMax(0, departments.size()).intValue()) + "'"
						    		+ ", '" + titles.get(StressTool.getNumberFromRandomMinMax(0, titles.size()).intValue()) + "'"
						    		+ ")");
				    		iLinked++;
				    	}
				    	
				    }	

				}
				else
				{
				    {
				    String nameString = Name.get(StressTool.getNumberFromRandomMinMax(0, Name.size()-1).intValue());					
				    String lastNString = LastName.get(StressTool.getNumberFromRandomMinMax(0, LastName.size()-1).intValue());
				    String cityString  = (String)city.keySet().toArray()[StressTool.getNumberFromRandomMinMax(0, city.size()-1).intValue()];
				    String[] cityN_ISO= ((String)city.get(cityString)).split("-");

				    int fromDaysBirth = StressTool.getNumberFromRandomMinMax(712223, today - 7300).intValue();
				    int fromDaysHire = StressTool.getNumberFromRandomMinMax(fromDaysBirth + 7300, today).intValue();
				    String gender ="";
				    if (fromDaysBirth % 2 == 0) {
				    	gender ="M";
				    } else {
				    	gender ="F";
				    }
				    insert1.append("("
				    	+ "NULL,FROM_DAYS("+ fromDaysBirth +")," 
					    + "'" + nameString + "',"
					    + "'" + lastNString + "',"
					    + "'" + gender + "',"
					    + "FROM_DAYS("+ fromDaysHire + "),"
					    + cityN_ISO[0] + ","
					    + "'" + cityString + "',"
					    + "'" + cityN_ISO[1] + "',"
					    + "UUID())"
					    + "");
				    }
				    
				    if(tableEmpNo.size() > 0){
				    	ArrayList<employeeShort> rowValueempNo = (ArrayList) tableEmpNo.get("tbtest"+iTable);
				    	Iterator it = rowValueempNo.iterator();
				    	int iLinked =0;
				    	while(it.hasNext()){
				    		employeeShort rv = (employeeShort)it.next();
				    		Long emp_no = rv.getEmpNo();
				    		Long fromDaysHire = rv.getHiredDateDay();
					    	if(iLinked > 0){
					    		insert2.append(",");
					    	}
					    	else if(iLinked > 0 
					    			&& iLinked > getIBatchInsert()){
					    		break;
					    	}
					    	updateId.put(emp_no,0);
				    		insert2.append("("
						    		+ emp_no
						    		+ ", Null"
						    		+ ", " + StressTool.getNumberFromRandomMinMax(1000, 1000000).intValue()
						    		+ ", " + "FROM_DAYS("+ fromDaysHire + ")"
						    		+ ", " + "FROM_DAYS("+ StressTool.getNumberFromRandomMinMax(fromDaysHire.intValue(),737060) + ")"
						    		+ ", '" + departments.get(StressTool.getNumberFromRandomMinMax(0, departments.size()).intValue()) + "'"
						    		+ ", '" + titles.get(StressTool.getNumberFromRandomMinMax(0, titles.size()).intValue()) + "'"
						    		+ ")");
				    		iLinked++;
				    	}
				    	
				    }	

//				    if(emp_max.size() >0 && emp_max.get(iTable -1 ).intValue() > 10){
//				    	Integer emp_no = StressTool.getNumberFromRandomMinMax(1, emp_max.get(iTable -1).intValue()).intValue();
//				    	insert3.append(","+ emp_no);
//				    	insert2.append("("
//				    			+ emp_no
//					    		+ ", Null"
//					    		+ ", " + StressTool.getNumberFromRandomMinMax(1000, 1000000).intValue()
//					    		+ ", " + "FROM_DAYS("+ fromDaysHire + ")"
//					    		+ ", " + "FROM_DAYS("+ StressTool.getNumberFromRandomMinMax(fromDaysHire,737060) + ")"
//					    		+ ", '" + departments.get(StressTool.getNumberFromRandomMinMax(0, departments.size()).intValue()) + "'"
//					    		+ ", '" + titles.get(StressTool.getNumberFromRandomMinMax(0, titles.size()).intValue()) + "'"
//					    		+ ")");
//				    }


				}
				if(!insertList1.equals(""))  
					insertList1.add(insert1.toString());
				if(!insertList2.equals("")){
					if(!insert2.toString().equals("")){
					    	Iterator itid = updateId.keySet().iterator();
					    	while(itid.hasNext()){
					    	    insert3.append("," +itid.next() );
					    	}
						String add_update=" ;update tbtest" +iTable+ " set linked=1 where emp_no in("+insert3.toString()+");";
						insertList2.add(insert2.toString() + add_update);
					}
				}

				insert1.delete(0, insert1.length());
				insert2.delete(0, insert2.length());
				insert3.delete(0, insert3.length());
			}
		}

		v.add(0,insertList1);
		v.add(1,insertList2);
		//    v.add(2, new Integer(pk));

		return v;

	}

	@Override
	public boolean createSchema(StressTool sTool) {
		if(this.getNumberOfprimaryTables() != this.getNumberOfSecondaryTables()){
			try {
				throw new InvalidApplicationException("This Class require that:\n"
						+ " Main and Child tables have the same number"
						+ " in the configurarion"
						+ "\nCheck your config file for:"
						+ "numberOfprimaryTables=x \nnumberOfSecondaryTables=x");
			} catch (InvalidApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
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

			if(sTool.droptable)
			{
				if(!isDoSimplePk()){
					for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
						System.out.println("**** Please wait DROP table tbtest_child" + iTable + " it could take a LOT of time *******");
						stmt.execute(DropTables2+iTable);
					}

				System.out.println("****============================================================================*******");
				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait DROP table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute(DropTables1+iTable);
				}



				}
				stmt.execute("COMMIT");
				System.out.println("**** DROP finished *******");
				System.out.println("****============================================================================*******");

			}

			if(sTool.createtable){			
			

				StringBuffer sb = new StringBuffer();
				stmt.execute("DROP TABLE IF EXISTS tbtestmax");
				stmt.execute("CREATE TABLE IF NOT EXISTS tbtestmax (`tablename` varchar(250) PRIMARY KEY, `maxid` int(11) unsigned) ENGINE="+ sTool.tableEngine );
	
				
				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					sb.append("CREATE TABLE IF NOT EXISTS tbtest" + iTable );
					
					sb.append("(`emp_no` int(11) unsigned AUTO_INCREMENT NOT NULL,`birth_date` date NOT NULL,`first_name` varchar(14) NOT NULL," +
							"`last_name` varchar(16) NOT NULL,`gender` enum('M','F') NOT NULL,`hire_date` date NOT NULL," +
							"`city_id` int(4) DEFAULT NULL,`CityName` varchar(150) DEFAULT NULL,`CountryCode` char(3) DEFAULT NULL," +
							"`UUID` char(36) DEFAULT NULL, `linked` tinyint(1) NOT NULL DEFAULT 0, "
							+ "`time_create` timestamp NOT NULL default CURRENT_TIMESTAMP, "
							+ "`time_update` timestamp NOT NULL default CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP, "
							+ " PRIMARY KEY (`emp_no`)"
							+ ", KEY `idx_linked` (`linked`)"
							+ ", KEY `time_created_up` (`time_create`,time_update)"
							+ ")");
					
					sb.append(" ENGINE="+ sTool.tableEngine) ;
	
					if(!sb.toString().equals(""))
						stmt.execute(sb.toString());
	
					sb.delete(0, sb.length());
					stmt.execute("DROP TRIGGER IF EXISTS test.tbtest"+iTable+"_AFTER_INSERT;");
//					stmt.execute(" CREATE TRIGGER `tbtest"+ iTable +"_AFTER_INSERT` AFTER INSERT ON `tbtest"+iTable+"` FOR EACH ROW \n"
//							+ " BEGIN \n"
//							+ " DECLARE sqlcode INT DEFAULT 0; \n"
//							+ " DECLARE CONTINUE HANDLER FOR 1054 SET sqlcode = 1054; \n"
//							+ " DECLARE CONTINUE HANDLER FOR 1136 SET sqlcode = 1136; \n"
//							+ " SET @LASTINSERT=0; \n"
//							+ " SELECT MAX(emp_no) INTO @LASTINSERT FROM tbtest" + iTable +"; \n"
//							+ " REPLACE INTO tbtestmax values('tbtest"+iTable+"',@LASTINSERT) ; \n"
//							+ " END ");

					
				}
				String tbts1 = sb.toString();
	
				sb = new StringBuffer();
				
				for(int iTable = 1 ; iTable <= this.getNumberOfSecondaryTables(); iTable++){
					sb.append("CREATE TABLE IF NOT EXISTS tbtest_child" + iTable);
					sb.append("(`emp_no` int(11) unsigned NOT NULL");
					sb.append(",`id` int(11) unsigned AUTO_INCREMENT NOT NULL");
					sb.append(", `salary` int(11)  NOT NULL");
					sb.append(", `from_date` DATE NOT NULL");
					sb.append(", `to_date` DATE NOT NULL");
					sb.append(", `dept_name` VARCHAR(40)  NULL");
					sb.append(", `title` VARCHAR(50)  NULL");
					sb.append(", `time_create` timestamp NOT NULL default CURRENT_TIMESTAMP");
					sb.append(", `time_changed` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP");
					sb.append(", PRIMARY KEY (emp_no,id, from_date)");
					sb.append(", key `emp_no` (`emp_no`)");
					sb.append(", UNIQUE `UK_is` (`id`)");
					if(isFKEnable())
					    sb.append(", FOREIGN KEY (`emp_no`) REFERENCES `tbtest"+ iTable +"` (`emp_no`) ON DELETE CASCADE");
					sb.append(") ENGINE="+ sTool.tableEngine);
	
					if(!sb.toString().equals(""))
						stmt.execute(sb.toString());
	
					sb.delete(0, sb.length());
	
				}
				String tbts2 = sb.toString();
	
				System.out.println(tbts1);
				if(!isDoSimplePk())
					System.out.println(tbts2);

			}	



			if(sTool.truncate)
			{
				System.out.println("****============================================================================*******");
				if(!isDoSimplePk()){
					for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
						System.out.println("**** Please wait TRUNCATE table tbtest_child" + iTable + " it could take a LOT of time *******");
						stmt.execute(TruncateTables2+iTable);
					}
				}

				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait TRUNCATE table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute("SET FOREIGN_KEY_CHECKS=0");
					stmt.execute(TruncateTables1+iTable);
					stmt.execute("SET FOREIGN_KEY_CHECKS=1");
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
class employeeShort{
	Long hiredDateDay = new Long(0);
	Long empNo = new Long(0);
	
	public employeeShort(Long empNo, Long hireDateDay) {
		this.hiredDateDay = hireDateDay;
		this.empNo = empNo;
	}

	public Long getHiredDateDay() {
		return hiredDateDay;
	}

	public void setHiredDateDay(Long hiredDateDay) {
		this.hiredDateDay = hiredDateDay;
	}

	public Long getEmpNo() {
		return empNo;
	}

	public void setEmpNo(Long empNo) {
		this.empNo = empNo;
	}
	
		
}