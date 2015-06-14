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
/**
 * @author tusa
 *
 */
/**
 * @author tusa
 *
 */
/**
 * @author tusa
 *
 */
/**
 * @author tusa
 *
 */
/**
 * @author tusa
 *
 */
/**
 * @author tusa
 *
 */
@SuppressWarnings({"unused","unchecked"})
public  class RunnableQueryInsertEmployees extends RunnableQueryInsertBasic {
    	
	private ArrayList <String> Name = null;
	private ArrayList <String> LastName = null;
	private ArrayList <String> email = null;
	private Map<String,String> city = null;
	private ArrayList <String> country = null;
	private ArrayList <String> departments = null;
	private ArrayList <String> titles = null;
	private Integer today = 0; 
	private ArrayList<Long> emp_max = new ArrayList<Long>();	
	private Map<String,ArrayList<EmployeeShort>> tableEmpNo = new SynchronizedMap(0);
//	private static Map empIdSync = new SynchronizedMap(0);
	
		
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
		ArrayList<EmployeeShort> rowValueempNo = null;
		Long min = (long) 0;
		ResultSet rs = null;
		Statement stmt = null;
	        conn.setAutoCommit(false);
	        stmt = conn.createStatement(); 
	        tableEmpNo.clear();
	        //test
	        
	        
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
            	    		
                	    	if(rowValueempNo == null)
                	    		rowValueempNo = new ArrayList<EmployeeShort>();
                	    	
                	    	String tableName = "tbtest" + iTable;   

                	    	
                	    	if(this.isDebug())
                	    	    System.out.println("Thread_ID = "+ this.getID() + " TABLE LOCK MAP TABLE = " + tableName + " LOCKS PRESENT = " + TablesLock.size(tableName)  );
                	    	
                	    	Integer  iMaxUnlink = 0;
                	    	rs = stmt.executeQuery("Select count(emp_no) from " + tableName + " where linked=0 ");	
                	    	while(rs.next()){
                	    	    iMaxUnlink = rs.getInt(1);
	                	}

                	    	
	                	String sql = "select emp_no,to_days(hire_date) from " + tableName + " where linked=0 order by emp_no limit  " 
	                			+ StressTool.getNumberFromRandomMinMax(0, iMaxUnlink/getNumberOfprimaryTables()) 
	                			+ ","  
	                			+ this.getIBatchInsert();
	                	rs = stmt.executeQuery(sql);
	                	EmployeeShort rv = null;
	                	while(rs.next()){
	                		 rv = new EmployeeShort(rs.getLong(1),rs.getLong(2));
	                		
	                		/**
	                		 * Try to acquire a lock on this ID if successfully then add to the list if already locked then it will not 
	                		 * be added to the Hash that will be operated later 
	                		 */
	                		 if( TablesLock.setempIdLOCK(tableName,new IdLock(rv.getEmpNo(), this.getID())))
	                		 {
	                		     rowValueempNo.add(rv);
	                		 }
	                	}
	                	if(rowValueempNo !=null
	                		&& rowValueempNo.size() >0
	                		){
	                	    tableEmpNo.put(tableName, new ArrayList(rowValueempNo));
	                	}
	                	   
	                	rs.close();
	                	rowValueempNo.clear();
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
                Name = new ArrayList<String>();
        	LastName = new ArrayList<String>();
        	email = new ArrayList<String>();
        	city = new SynchronizedMap();
        	departments = new ArrayList<String>();
        	titles = new ArrayList<String>();
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
	@Override 
	Vector <ArrayList<String>> getTablesValues(boolean refresh) {

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

	    Vector<ArrayList<String>> v = new Vector<ArrayList<String>>();



	    StringBuffer insert1 = new StringBuffer();
	    StringBuffer insert2 = new StringBuffer();
	    StringBuffer insert3 = new StringBuffer();

	    ArrayList<String>  insertList1 = new ArrayList<String>();
	    ArrayList<String>  insertList2 = new ArrayList<String>();

	    int pk = StressTool.getNumberFromRandom(2147483647).intValue();
	    String insert1Str = "";
	    Map<Long,Integer> updateId = new SynchronizedMap();




	    if(getDbType().endsWith("MySQL"))
	    {
		for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
		    //			    +--------+------------+------------+-----------+--------+------------+---------+
		    //				 * | emp_no | birth_date | first_name | last_name | gender | hire_date  | city_id |
		    //		                 * +--------+------------+------------+-----------+--------+------------+---------+

		    insert1.append("insert INTO tbtest" + iTable + " (emp_no,birth_date,first_name,last_name,gender,hire_date,city_id,CityName,CountryCode,UUID) VALUES");
		    if(tableEmpNo.size() > 0 && tableEmpNo.get("tbtest" + iTable) != null )
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
			}
		    }
		    else
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
			ArrayList<EmployeeShort> rowValueempNo = (ArrayList<EmployeeShort>) tableEmpNo.get("tbtest"+iTable);

			if(rowValueempNo != null){
			    Iterator<EmployeeShort> it = rowValueempNo.iterator();
			    int iLinked =0;

			    while(it.hasNext()){
				EmployeeShort rv = (EmployeeShort)it.next();
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

		
		if(!insertList1.equals(""))  
		    insertList1.add(insert1.toString());
		if(!insertList2.equals("")){
		    if(!insert2.toString().equals("")){
			Iterator<Long> itid = updateId.keySet().iterator();
			while(itid.hasNext()){
			    insert3.append("," +itid.next() );
			}
			String add_update=" ;update tbtest" +iTable+ " set linked=1 where emp_no in("+insert3.toString()+")";
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

	@SuppressWarnings("finally")
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
							+ ", KEY `idx_linked` (`linked`,`emp_no`)"
							+ ", KEY `time_created_up` (`time_create`,time_update)"
							+ ")");
					
					sb.append(" ENGINE="+ sTool.tableEngine) ;
	
					if(!sb.toString().equals(""))
						stmt.execute(sb.toString());
	
					sb.delete(0, sb.length());
//					stmt.execute("DROP TRIGGER IF EXISTS test.tbtest"+iTable+"_AFTER_INSERT;");
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
	
//				System.out.println(tbts1);
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
class EmployeeShort{
	Long hiredDateDay = new Long(0);
	Long empNo = new Long(0);
	
	public EmployeeShort(Long empNo, Long hireDateDay) {
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
//
///**
// * @author tusa
// * This class Object represent the lock generated by a running thread
// *  with a given Thread ID and record ID
// *  Do not support PK with multiple attributes
// */
//class IdLock{
//		Long recordId = (long)0;
//		int THID = 0;
////		String tableName = null;
//		/**
//		 * @return the recordId
//		 */
//		public synchronized Long getRecordId() {
//		    return recordId;
//		}
//		/**
//		 * @param recordId the recordId to set
//		 */
//		public synchronized void setRecordId(Long recordId) {
//		    this.recordId = recordId;
//		}
//		public synchronized int getTHID() {
//			return THID;
//		}
//		public synchronized void setTHID(int tHID) {
//			THID = tHID;
//		}
//		public IdLock(Long recordId, int tHID) {
//			this.recordId = recordId;
//			THID = tHID;
//		}
//}
//
///*
// * TODO: create class for PK with multiple fields
// */
////class IdMultiLock extends IdLock{
////    @SuppressWarnings("unchecked")
////    Map<String, Object> recordId = new SynchronizedMap(0);
////    int THID = 0;
////    public IdMultiLock(Map recordId, int tHID) {
////	if(recordId !=null
////		&& recordId instanceof Map
////		&& recordId.size() > 0
////		&& tHID > 0){
////	    this.recordId = recordId;
////	    this.THID = tHID;
////	}
////    }
////    
////    public IdMultiLock(Long recordId2, int tHID, Map<String, Object> recordIdIn,
////	    int tHID2) {
////	super(recordId2, tHID);
////	recordId = recordIdIn;
////	THID = tHID2;
////    }
////
////    public IdMultiLock(Long recordId, int tHID) {
////	super(recordId, tHID);
////	// TODO Auto-generated constructor stub
////    }
////}
//
///**
// *  * @author tusa
// * This class keep an object Map of locks for each Table
// * each table has its own list of IDLock
// * All methods are static and synchronized 
// */
//	
//class  TablesLock extends SynchronizedMap{
//    	@SuppressWarnings("rawtypes")
//	static Map <String,Map> tables = null;
//	
//        /**
//         * 
//         * @param tableName
//         * @param id
//         * @return IdLock
//         * This method return an object of type IdLock or NULL if not present
//         * The presence indicate a lock NULL means instead no Lock 
//         * 
//         */
//	@SuppressWarnings("unchecked")
//	public static synchronized IdLock getLockbyId(String tableName, Long id){
//	    	Map<Long,IdLock> locks = null;
//	    	
//	    	if(tableName != null 
//		&& !tableName.equals("")
//		&& id  !=null
//		&& id.longValue() > 0
//		){ 
//        	    if (tables == null) {
//        		tables = new SynchronizedMap(0);
//        		return null;
//        	    } else {
//        		locks = tables.get(tableName);
//        	    }
//		    
//		    if(locks.size() > 0){
//			return locks.get(id);
//		    }
//		}  
//			return null;
//		
//	}
//	/**
//	 * 
//	 * @param tableName
//	 * @param lock
//	 * @return true or false
//	 * 
//	 * This method is used to SET a lock on a ID for a given Table
//	 * if lock is successfully set return true else false 
//	 */
//	@SuppressWarnings("unchecked")
//	public static synchronized boolean setempIdLOCK(String tableName,IdLock lock){
//	    Map<Long,IdLock> locks = null;
//
//	    if(lock != null
//		    && lock.recordId != null
//		    && tableName != null 
//		    && !tableName.equals("")){
//		if(tables == null){
//		    tables = new SynchronizedMap(0);
//		}
//		else{
//		    locks = tables.get(tableName);
//		}
//		
//		if(locks == null){
//		    locks = new SynchronizedMap(0);
//		    tables.put(tableName, locks);
//		}
//		    
//		if(!locks.containsKey(lock.recordId)){
//			locks.put(lock.getRecordId(), lock);
//			tables.put(tableName, locks);
//			return true;
//		    }
//		
//	    }
//	    return false;
//	}
//	/**
//	 * 
//	 * @param tableName
//	 * @param id
//	 * @return true or false
//	 * 
//	 * This method is used to remove a lock on a record on a given Table
//	 */
//	public static synchronized boolean deleteIdLockById(String tableName, Long id){
//	    	if(tableName != null 
//		&& !tableName.equals("")
//		&& tables !=null
//		&& tables.get(tableName) != null
//		&& id  !=null
//		&& id.longValue() > 0
//		){
//	    	    
//	    	    if( tables.get(tableName)!=null
//	    		    &&tables.get(tableName).get(id)!=null ){
//	    		tables.get(tableName).remove(id);
//	    		return true;
//	    	    }
//		}
//	    return false;
//	}
//	/**
//	 * 
//	 * @param tableName
//	 * @param id
//	 * @return true or false
//	 * 
//	 * This method is used to remove a lock on a record on a given Table
//	 */
//	public static synchronized boolean deleteIdLockByThreadId(int id){
//	    	if(
//		tables !=null
//		&& tables.size() > 0
//		&& id > 0
//		){
//	    	    Iterator<String> it = tables.keySet().iterator();
//	    	    while(it.hasNext()){
//	    		String tableName = it.next();
//	    		if(tables.get(tableName)!= null && tables.get(tableName).size() > 0){
//	    		    Map<Long,IdLock> locks = tables.get(tableName);
//	    		    Iterator<Long> itLock = locks.keySet().iterator();
//	    		    while(itLock.hasNext()){
//	    			Long Id = itLock.next();
//	    			if(locks.get(Id).getTHID() == id){
//	    			    TablesLock.deleteIdLockById(tableName, Id);
//	    			}
//	    		    }
//	    		}
//	    	    }
//	    	    return true;
//	    }
//		
//	    return false;
//	}
//	
//	/**
//	 * 
//	 * @param tableName
//	 * @return true or false
//	 * 
//	 * This method reset ALL locks for the given table
//	 */
//	public static synchronized boolean deleteTable(String tableName){
//	    	if(tableName != null 
//		&& !tableName.equals("")
//		&& tables !=null
//		&& tables.get(tableName) != null
//		){
//	    	    
//	    	    if( tables.get(tableName)!=null){
//	    		tables.remove(tableName);
//	    		tables.put(tableName,  new SynchronizedMap(0));
//	    		return true;
//	    	    }
//		}
//	    return false;
//	}
//	public static synchronized int size(String tableName){
//	    if(tables !=null
//		&& tables.get(tableName) != null
//		){
//			return tables.get(tableName).size();
//	    }
//	    else
//		return 0;
//	    
//	}
//	
//}

