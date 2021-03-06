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
//V 1.5 
package com.mysql.stresstool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
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


/**
 * <p>Title:test </p>
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
 * @author marcotusa
 *
 */
/**
 * @author marcotusa
 *
 */
/**
 * @author marcotusa
 *
 */
/**
 * @author marcotusa
 *
 */
/**
 * @author marcotusa
 *
 */
public  class RunnableQueryInsertBasic implements Runnable, RunnableQueryInsertInterface {

	private File queriesFileIn;
	//    Connection conn = null;

	private Map jdbcUrlMap;
	private boolean repeat = false;
	private int repeatNumber = 0;
	private int ID;
	private boolean doLog = true;
	private int sleepFor;
	private boolean operationShort =false;
	private String dbType="MySQL";
	private String engine;
	private long executionTime;
	private boolean doDelete;
	private boolean active=false;
	private boolean useBatchInsert;
	private MySQLStats mySQLStatistics;
	private boolean doSimplePk = false;
	private int iBatchInsert=50;
	private boolean ignoreBinlog = false;
	private Map classConfig = null;
	private String lazyInsert1 = "" ;
	private String lazyInsert2 = "";
	private String lazyLongText= "";
	private int numberOfprimaryTables = 1;
	private int numberOfSecondaryTables = 1;
	private boolean useAutoIncrement = false;
	private int sleepWrite = 0;
	private int yearstart =2013;
	private int monthstart = 1;
	private int daystart = 1;
	private int daystotal = 1;
	public String partitionType = "range";
	private boolean stikyconnection = true;
	private boolean debug = false;
	private int lockRetry = 0;
	private int lazyInterval = 500;
	private boolean FKEnable = false;

	/**
	 * @return the fKEnable
	 */
	public boolean isFKEnable() {
	    return FKEnable;
	}

	/**
	 * @param fKEnable the fKEnable to set
	 */
	public void setFKEnable(boolean fKEnable) {
	    FKEnable = fKEnable;
	}

	private static final ArrayList <String> CLASS_PARAMETERS = new ArrayList(Arrays.asList(
			"numberOfprimaryTables","numberOfSecondaryTables","useAutoIncrement","sleepWrite",
			"yearstart","monthstart","daystart","daystotal","partitionType","debug","stikyconnection","FKEnable","lazyInterval"));
		
	public RunnableQueryInsertBasic() {
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

		BufferedReader d = null;
		Connection conn = null;

		try {
			if(stikyconnection && getJdbcUrlMap().get("dbType") != null &&  !((String)getJdbcUrlMap().get("dbType")).equals("MySQL"))
			{
				conn=DriverManager.getConnection((String)getJdbcUrlMap().get("dbType"),"test", "test");
			}
			else if(stikyconnection)
				conn= DriverManager.getConnection((String)getJdbcUrlMap().get("jdbcUrl"));
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}




			try{

				long execTime = 0;
				int pkStart = 0 ;
				int pkEnds = 0 ;
				int intDeleteInterval = 0;
				int intBlobInterval=0;
				int intBlobIntervalLimit=StressTool.getNumberFromRandom(4).intValue();
				ThreadInfo thInfo ;

				long threadTimeStart = System.currentTimeMillis();
				active = true;


				thInfo = new ThreadInfo();
				thInfo.setId(this.ID);
				thInfo.setType("insert");
				thInfo.setStatusActive(this.isActive());

				StressTool.setInfo(this.ID,thInfo);
				boolean lazy = false;
				int lazyIntervalCurrent =0;

				for(int repeat = 0 ; repeat <= repeatNumber ; repeat++)
				{
				    if(!stikyconnection ){
					try {
					    if(conn != null && !conn.isClosed()){
						conn.close();
					    }
					    SoftReference sf = new SoftReference(DriverManager.getConnection((String)getJdbcUrlMap().get("jdbcUrl")));
					    conn =  (Connection)sf.get(); 
					} catch (SQLException ex) {
					    for(int icon = 0 ; icon <= 3 ; icon++ )
					    {
						try{
						    Thread.sleep(10000);
						    SoftReference sf = new SoftReference(DriverManager.getConnection((String)getJdbcUrlMap().get("jdbcUrl")));
						    conn =  (Connection)sf.get(); 
						}
						catch(SQLException ex2){
						    ex2.printStackTrace();
						}
					    }
					    //ex.printStackTrace();
					}

				    }
				  if(conn != null)
				  {
					try {
						executeLocalExtensions(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Statement stmt = null;
					//                ResultSet rs = null;
					//                ResultSet rs2 = null;

					conn.setAutoCommit(false);
					stmt = conn.createStatement();
					stmt.execute("SET AUTOCOMMIT=0");

				    String query = null;
					ArrayList insert1 = null;
					ArrayList insert2 = null;
					int pk = 0 ;

					if(repeat > 0 && lazyIntervalCurrent < lazyInterval){
						lazy = true;
						++lazyIntervalCurrent;
					}
					else{
						lazy = false;
						lazyIntervalCurrent =0;
					}


					intBlobInterval++;
					//IMPLEMENTING lazy
					Vector v = this.getTablesValues(lazy);

					insert1 = (ArrayList<String>) v.get(0);
					insert2 = (ArrayList<String>) v.get(1);

					//                    System.out.println(insert1);
					//                    System.out.println(insert2);

					//                    pk = ((Integer) v.get(2)).intValue();

					int[] iLine = {0,0} ;

					//                    pkStart = StressTool.getNumberFromRandom(2147483647).intValue();
					//                    pkEnds = StressTool.getNumberFromRandom(2147483647).intValue();



					try
					{

						long timeStart = System.currentTimeMillis();

						if(this.ignoreBinlog)
							stmt.execute("SET sql_log_bin=0");
						//stmt.execute("SET GLOBAL max_allowed_packet=1073741824");

						if(dbType.equals("MySQL") && !engine.toUpperCase().equals("BRIGHTHOUSE"))
							stmt.execute("BEGIN");
						else
							stmt.execute("COMMIT");
						//                                stmt.execute("SET TRANSACTION NAME 'TEST'");
						{
							Iterator<String> it = insert1.iterator();
							String tmp ="";
							while(it.hasNext()){
								tmp = (String) it.next();
								if(this.debug)
									System.out.println(tmp);
								stmt.addBatch(tmp);
							}
						}

						if(!this.doSimplePk){
							if(intBlobInterval > intBlobIntervalLimit )
							{
								String tmp ="";
								Iterator<String> it = insert2.iterator();
								while(it.hasNext()){
									tmp = (String) it.next();
									String[] tmpsAr = tmp.split(";");
									if(tmpsAr.length < 1 ){
										if(this.debug)
											System.out.println(tmp);
										
										stmt.addBatch(tmp);
									}
									else{
										for(int xs =0; xs < tmpsAr.length; xs++){
											if(this.debug)
												System.out.println(tmpsAr[xs]);
											
											stmt.addBatch(tmpsAr[xs]);
										}
										
									}
										
								}
								intBlobInterval=0;

							}
						}
						if(debug){
							System.out.println("Thread "+ thInfo.getId() +" Executing loop " + thInfo.getExecutedLoops() +" QUERY1==" + insert1);
							System.out.println("Thread "+ thInfo.getId() +" Executing loop " + thInfo.getExecutedLoops() +" QUERY2==" + insert2);

						}
						    
						iLine = executeSQL(stmt);
						//                            System.out.println("Query1 = " + insert1);
						//                            System.out.println("Query2 = " + insert2);
						//                            stmt.execute("START TRANSACTION");
						//                            stmt.execute(insert1);
						//                            iLine = stmt.executeBatch();
						//                            conn.commit();
						long timeEnds = System.currentTimeMillis();
						execTime = (timeEnds - timeStart);

					}
					catch (SQLException sqle)
					{
					    
					    conn.rollback();
						System.out.println("FAILED QUERY1==" + insert1);
						System.out.println("FAILED QUERY2==" + insert2);
						sqle.printStackTrace();
						System.exit(1);
						//conn.close();
						//this.setJdbcUrl(jdbcUrl);
						//System.out.println("Query Insert TH RE-INIZIALIZING");


					}
					finally
					{
						//                        	conn.commit();
					    	if(conn != null && !conn.isClosed()){
					    	    try {
					    			stmt.addBatch("COMMIT");
					    			executeSQL(stmt);
					    			stmt.close();
					    	    }
					    	    catch(SQLException sqle){
					    		System.out.println("#####################\n[Warning] Cannot explicitly commit given error\n#################");
					    		conn.close();
					    		continue;
					    	    }
					    	}
					    	else{
					    	    System.out.println("#####################\n[Warning] Cannot explicitly commit given connection was interrupted unexpectedly\n#################");
					    	}
						//                            intDeleteInterval++;
						if(doLog){

							System.out.println("Query Insert TH = " + this.getID()
									+ " Loop N = "+ repeat  + " InsertedRec|batchExec " +iLine[0]+"|"+((iLine.length > 1)?iLine[1]:0)
									+ " Exec Time(ms) = " + execTime
									+ " Running = " + repeat + " of " + repeatNumber + " to go =" + (repeatNumber - repeat)
									+ " Using Lazy=" + lazy);
						}
					}

					if(TablesLock.tables != null
						&& TablesLock.tables.size() > 0){
					    TablesLock.deleteIdLockByThreadId(this.ID, this.isDebug()?true:false);
					}

					thInfo.setExecutedLoops(repeat);
					if(sleepFor > 0 || this.getSleepWrite()> 0)
					{
					    if(this.getSleepWrite() > 0)
					    {
						Thread.sleep(getSleepWrite());
					    }
					    else
						Thread.sleep(sleepFor);
					}


				  }
				}

				long threadTimeEnd = System.currentTimeMillis();
				this.executionTime = (threadTimeEnd - threadTimeStart);
				//                this.setExecutionTime(executionTime);
				active = false;
				//                System.out.println("Query Insert TH = " + this.getID() + " COMPLETED!  TOTAL TIME = " + execTime + "(ms) Sec =" + (execTime/1000));
				
				thInfo.setExecutionTime(executionTime);
				thInfo.setStatusActive(false);
				StressTool.setInfo(this.ID,thInfo);
				return;

			    

			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}



		

	}

	/**
	 * This method allow classes that extends the basic 
	 * to query the database for local purpose
	 * @param conn
	 */
	public void executeLocalExtensions(Connection conn) {
		return;
		
	}

	

	
	public int[] executeSQL(Statement stmt) throws Exception {
	    int[] iLine= new int[0];
	    try{
        	    iLine = stmt.executeBatch();
        	    stmt.clearBatch();
	    }
	    catch(SQLException sqle){
		if((sqle.getErrorCode() == 1205) && lockRetry < 4){
		    lockRetry++;
		    System.out.println("Lock Found for thread = " + Thread.currentThread().getId() + " repeat N: " + lockRetry + " OF 3");		    
		    iLine = executeSQL(stmt);
		    try {
		    	iLine = executeSQL(stmt);
		    	Thread.sleep(1000);
		    } catch (InterruptedException e) {
		    		System.out.println("Error from JDBC for thread = " + Thread.currentThread().getId() +" | " + sqle.getErrorCode() + " : " +sqle.getMessage()+ "\n Reducing the FLOW and try to recover transaction");
		    		Thread.sleep(2000);
		    		executeSQL(stmt);
		    		// TODO Auto-generated catch block
		    		System.out.println("========= Reporting stack trace for debug =========");
		    		e.printStackTrace();
		    }
		}
		else if(sqle.getErrorCode() > 0 
				&& sqle.getErrorCode() != 1452
				&& sqle.getErrorCode() != 1205
				&& lockRetry < 4) {
		    	lockRetry++;
		    	System.out.println("ERROR Found for thread = " + Thread.currentThread().getId() + " repeat N: " + lockRetry + " OF 3\n" + sqle.getLocalizedMessage());
//		    	sqle.printStackTrace();
		    	
		    	try {
		    			iLine = executeSQL(stmt);
		    			Thread.sleep(1000);
		    } catch (InterruptedException e) {
		    	System.out.println("Error from JDBC for thread = " + Thread.currentThread().getId() +" | " + sqle.getErrorCode() + " : " +sqle.getMessage()+ "\n Reducing the FLOW and try to recover transaction");
		    	Thread.sleep(2000);
		    	executeSQL(stmt);
		    	// TODO Auto-generated catch block
		    	System.out.println("========= Reporting stack trace for debug =========");
		    	e.printStackTrace();
		    }
	    	
	    }
		else if(sqle.getErrorCode() == 1452){
			return new int[0];
			//System.out.print("x");
		}
		else if(lockRetry >=4){
			stmt.clearBatch();
			try{stmt.execute("ROLLBACK");}catch(Exception eex){}
			lockRetry=0;
			System.out.println("Error from JDBC  for thread = " + Thread.currentThread().getId() +" | "  + sqle.getErrorCode() + " : " +sqle.getMessage() + " Aborting");
		    //throw new Exception(sqle);
			return iLine;
		}

		
	    }
	    catch (Exception ex){
	    	throw new Exception(ex);
	    	
	    }
	    
	    return iLine;
	}

	public boolean executeSQL(String command,Connection conn) throws Exception {
	    boolean done;
	    try{

	    		Statement stmt = conn.createStatement();
        	    done = stmt.execute(command);
	    }
	    catch(SQLException sqle){
		if((sqle.getErrorCode() == 1205 ||sqle.getErrorCode() == 1213 ) && lockRetry < 4){
		    lockRetry++;
		    System.out.println("Lock Found for thread = " + Thread.currentThread().getId() + " repeat N: " + lockRetry + " OF 3");		    
		    done = executeSQL(command,conn);
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    
		}
		else{
		    throw new Exception(sqle);
		}

		
	    }
	    catch (Exception ex){
		throw new Exception(ex);
	    }
	    return done;
	}

	Vector getTablesValues(boolean refresh) {

		String longtextFld = "";
		boolean lazy = false;
		int afld = 0;
		long counterFld = 0 ;

		if(refresh && !lazyInsert1.equals(""))
		{
			lazy = true;
			longtextFld = lazyLongText;
		}
		else{
			if(operationShort)
				longtextFld = StressTool.getStringFromRandom(254).substring(0,240);
			else
				longtextFld = StressTool.getStringFromRandom(40000);
		}

		Vector v = new Vector();



		StringBuffer insert1 = new StringBuffer();
		StringBuffer insert2 = new StringBuffer();
		//        String uuid = UUID.randomUUID().toString();
		ArrayList <String> insertList1 = new ArrayList();
		ArrayList <String> insertList2 = new ArrayList();

		int pk = StressTool.getNumberFromRandom(2147483647).intValue();
		String insert1Str = "";


		if(dbType.endsWith("MySQL"))
		{
			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){

				insert1.append("insert INTO tbtest" + iTable + " (uuid,date,a,b,c,counter,partitionid,strrecordtype) VALUES");

				if(this.useBatchInsert)
				{

					insert1Str = "";
					if(lazy){
						insert1Str = lazyInsert1;

					}
					else{

						insert1Str ="\"" + (this.doSimplePk?longtextFld.substring(0, 6):longtextFld.substring(0, 99)) + "\",";
						insert1Str = insert1Str  + "\"" + (this.doSimplePk?longtextFld.substring(0, 10):longtextFld.substring(0, 199)) + "\",";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(2147483647) *   StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + "\"" + StressTool.getStringFromRandom(2) + "\"";
						insert1Str = insert1Str  + ")";
						lazyInsert1 = insert1Str;
					}


					for(int ibatch= 0 ; ibatch <=this.getiBatchInsert(); ibatch++ )
					{
					    	String thisDate = getRangeDate();

						//                    uuid = UUID.randomUUID().toString();
						pk = StressTool.getNumberFromRandom(2147483647).intValue();


						if (ibatch > 0){
							insert1.append(",(UUID(),'"+ thisDate + "'," + pk + ",");
						}
						else
						{	
							insert1.append("(UUID(),'"+ thisDate + "'," + pk + ",");
						}

						insert1.append(insert1Str);
					}
				}
				else
				{
				    	String thisDate = getRangeDate();
					insert1Str = "";
					insert1Str = insert1Str  + "(UUID(),'"+ thisDate + "'," + pk + ",";

					if(lazy){
						insert1Str = lazyInsert1;

					}
					else{
						insert1Str = insert1Str  + "\"" + (this.doSimplePk?longtextFld.substring(0, 6):longtextFld.substring(0, 99)) + "\",";
						insert1Str = insert1Str  + "\"" + (this.doSimplePk?longtextFld.substring(0, 10):longtextFld.substring(0, 199)) + "\",";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(2147483647) *
						StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + "\"" + StressTool.getStringFromRandom(2) + "\"";
						insert1Str = insert1Str  + ")";
						lazyInsert1 = insert1Str;	                
					}

					insert1.append(insert1Str);
				}
				if(!insertList1.equals(""))  
					insertList1.add(insert1.toString());

				insert1.delete(0, insert1.length());
			}
		}
		if(!this.doSimplePk)
		{
			if(dbType.endsWith("MySQL"))
			{
				String insert2Str = ""; 
				String insert2bStr = "";
				String thisDate = getRangeDate();
				
				for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
					insert2Str = "";
					
					insert2Str = insert2Str  +"insert INTO tbtest_child" + iTable + " (a,date,stroperation) VALUES(";
					insert2Str = insert2Str  + pk + ",'" + thisDate + "',";
					if(lazy && !lazyInsert2.equals("")){
						insert2bStr = lazyInsert2;
					}
					else{
						if(this.getOnDuplicateKey() == 0 ){
							lazyInsert2  =  "\"" + longtextFld + "\")";
						}
						else{
							lazyInsert2  =  "\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0";
						}
						insert2bStr = lazyInsert2;
						//	        		insert2Str = insert2Str  + "\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0";
						//	        		lazyInsert2 = insert2Str;
					}

					if(!insert2Str.equals("")) 
						insertList2.add(insert2Str + insert2bStr);

				}

			}
		}


		v.add(0,insertList1);
		v.add(1,insertList2);
		//    v.add(2, new Integer(pk));

		return v;

	}

	protected String getRangeDate() {
	    // Fill the date value with some time date
	    
	    int year = this.getYearstart();
	    int month = this.getMonthstart();
	    int day = this.getDaystart();
	    int interval = this.getDaystotal();
	    
	    return Utility.getDateFromDaysInterval(year, month, day, StressTool.getNumberFromRandom(interval).intValue());
	    
	}

	public static void main(String[] args) {
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setQueriesFileIn(java.io.File)
	 */
	public void setQueriesFileIn(File queriesFileIn) {
		this.queriesFileIn = queriesFileIn;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setJdbcUrl(java.lang.String)
	 */
	//    public void setJdbcUrl(Map jdbcUrl) {
	//        this.jdbcUrl = jdbcUrl;
	//        try {
	//            this.conn= DriverManager.getConnection(jdbcUrl);
	//        } catch (SQLException ex) {
	//            ex.printStackTrace();
	//        }
	//    }

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setJdbcUrl(java.lang.String, java.lang.String)
	 */
	public void setJdbcUrl(Map jdbcUrlMap) {
		this.setJdbcUrlMap(jdbcUrlMap);
		//        try {
		//            if(jdbcUrlMap.get("dbType") != null &&  !((String)jdbcUrlMap.get("dbType")).equals("MySQL"))
		//            {
		//                this.conn=DriverManager.getConnection((String)jdbcUrlMap.get("dbType"),"test", "test");
		//            }
		//            else
		//            this.conn= DriverManager.getConnection(jdbcUrl);
		//        } catch (SQLException ex) {
		//            ex.printStackTrace();
		//        }
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setRepet(boolean)
	 */
	public void setRepet(boolean repeat) {
		this.repeat = repeat;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setRepeatNumber(int)
	 */
	public void setRepeatNumber(int repeatNumber) {
		this.repeatNumber = repeatNumber;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setDoLog(boolean)
	 */
	public void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setSleepFor(int)
	 */
	public void setSleepFor(int sleepFor) {
		this.sleepFor = sleepFor;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setOperationShort(boolean)
	 */
	public void setOperationShort(boolean operationShort) {
		this.operationShort = operationShort;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setDbType(java.lang.String)
	 */
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setEngine(java.lang.String)
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setExecutionTime(long)
	 */
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setDoDelete(boolean)
	 */
	public void setDoDelete(boolean doDelete) {
		this.doDelete = doDelete;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setUseBatchInsert(boolean)
	 */
	public void setUseBatchInsert(boolean useBatchInsert) {
		this.useBatchInsert = useBatchInsert;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setID(int)
	 */
	public void setID(int ID) {
		this.ID = ID;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getQueriesFileIn()
	 */
	public File getQueriesFileIn() {
		return queriesFileIn;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getJdbcUrl()
	 */
	public String getJdbcUrl() {
		return (String)getJdbcUrlMap().get("jdbcUrl");
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#isRepeat()
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getRepeatNumber()
	 */
	public int getRepeatNumber() {
		return repeatNumber;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#isDoLog()
	 */
	public boolean isDoLog() {
		return doLog;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getSleepFor()
	 */
	public int getSleepFor() {
		return sleepFor;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#isOperationShort()
	 */
	public boolean isOperationShort() {
		return operationShort;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getDbType()
	 */
	public String getDbType() {
		return dbType;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getEngine()
	 */
	public String getEngine() {
		return engine;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getExecutionTime()
	 */
	public long getExecutionTime() {
		return executionTime;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getDoDelete()
	 */
	public boolean getDoDelete() {
		return doDelete;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#isActive()
	 */
	public boolean isActive() {
		return active;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#isUseBatchInsert()
	 */
	public boolean isUseBatchInsert() {
		return useBatchInsert;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getID()
	 */
	public int getID() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getMySQLStatistics()
	 */
	public MySQLStats getMySQLStatistics() {
		return mySQLStatistics;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setMySQLStatistics(com.mysql.stresstool.MySQLStats)
	 */
	public void setMySQLStatistics(MySQLStats mySQLStatistics) {
		this.mySQLStatistics = mySQLStatistics;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setDoSimplePk(boolean)
	 */
	public void setDoSimplePk(boolean doSimplePk) {
		this.doSimplePk = doSimplePk;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#doSimplePk()
	 */
	public boolean doSimplePk() {
		return doSimplePk;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#setIBatchInsert(int)
	 */
	public void setIBatchInsert(int iBatchInsert) {
		this.setiBatchInsert(iBatchInsert);
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getIBatchInsert()
	 */
	public int getIBatchInsert() {
		return getiBatchInsert();
	}

	public boolean isIgnoreBinlog() {
		return ignoreBinlog;
	}

	public void setIgnoreBinlog(boolean ignoreBinlog) {
		this.ignoreBinlog = ignoreBinlog;
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
				sb.append("CREATE TABLE IF NOT EXISTS tbtest" + iTable +"(");
				if (this.isUseAutoIncrement()){
					sb.append("`autoInc` bigint(11) AUTO_INCREMENT NOT NULL,");
				}
				sb.append(" `a` int(11) NOT NULL,"); 
				sb.append(" `uuid` char(36) NOT NULL,");
				sb.append(" `b` varchar(100) NOT NULL,");
				sb.append(" `c` char(200)  NOT NULL,");
				sb.append(" `counter` bigint(20) NULL, ");
				sb.append(" `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,");
				sb.append(" `partitionid` int NOT NULL DEFAULT 0,");
				sb.append(" `date` DATE NOT NULL,");
				sb.append(" `strrecordtype` char(3) NULL");
				if (this.isUseAutoIncrement()){
				    if(this.partitionType.equals("range")){
					sb.append(", PRIMARY KEY  (`autoInc`,`date`),  INDEX `IDX_a` (a),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
				    }
				    else{
					sb.append(", PRIMARY KEY  (`autoInc`,`partitionid`),  INDEX `IDX_a` (a),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
				    }
				}
				else{
					if(!this.doSimplePk)
					    if(this.partitionType.equals("range")){
					    	sb.append(", PRIMARY KEY  (`uuid`,`date`),  INDEX `IDX_a` (a), INDEX `IDX_date` (date) ");
					    }
					    else{
						sb.append(", PRIMARY KEY  (`uuid`,`partitionid`),  INDEX `IDX_a` (a), INDEX `IDX_date` (date)");
					    }
					else{ 
					    if(this.partitionType.equals("range")){
						sb.append(", PRIMARY KEY  (`a`,`date`),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
					    }
					    else{
						sb.append(", PRIMARY KEY  (`a`,`partitionid`),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
					    }
					}
				}
				sb.append(") ENGINE="+ sTool.tableEngine) ;

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
				if(operationShort)
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
			if(!doSimplePk)
				System.out.println(tbts2);


			if(sTool.droptable)
			{
				System.out.println("****============================================================================*******");
				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait DROP table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute(DropTables1+iTable);
				}

				if(!doSimplePk){
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

				if(!doSimplePk){
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


	public String getLazyInsert1() {
		return lazyInsert1;
	}

	public void setLazyInsert1(String lazyInsert1) {
		this.lazyInsert1 = lazyInsert1;
	}

	public String getLazyInsert2() {
		return lazyInsert2;
	}

	public void setLazyInsert2(String lazyInsert2) {
		this.lazyInsert2 = lazyInsert2;
	}

	public String getLazyLongText() {
		return lazyLongText;
	}

	public void setLazyLongText(String lazyLongText) {
		this.lazyLongText = lazyLongText;
	}

	public int getNumberOfprimaryTables() {
		return numberOfprimaryTables;
	}

	public void setNumberOfprimaryTables(int numberOfprimaryTables) {
		this.numberOfprimaryTables = numberOfprimaryTables;
	}

	public int getNumberOfSecondaryTables() {
		return numberOfSecondaryTables;
	}

	/**
	 * @return the stikyconnection
	 */
	public boolean isStikyconnection() {
	    return stikyconnection;
	}
	/**
	 * @return the stikyconnection
	 */
	public boolean getStikyconnection() {
	    return stikyconnection;
	}	

	/**
	 * @param stikyconnection the stikyconnection to set
	 */
	public void setStikyconnection(boolean stikyconnection) {
	    this.stikyconnection = stikyconnection;
	}

	public void setNumberOfSecondaryTables(int numberOfSecondaryTables) {
		this.numberOfSecondaryTables = numberOfSecondaryTables;
	}

	public boolean isDoSimplePk() {
		return doSimplePk;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}


	@Override
	public void setClassConfiguration(Map mConfig) {
		classConfig = mConfig;

		for(int i = 0 ; i < CLASS_PARAMETERS.size() ; i++){
			try {
				String methodName = (String)CLASS_PARAMETERS.get(i);
				methodName = methodName.substring(0,1).toUpperCase() + methodName.substring(1,methodName.length()); 
				methodName = "set"+methodName;


				String valueM = (String) classConfig.get(CLASS_PARAMETERS.get(i));
//				System.out.println(methodName + " = " + valueM);

				if(valueM != null){
					if(valueM.equals("true") || valueM.equals("false")){
						MethodUtils.invokeMethod(this,methodName,Boolean.parseBoolean(valueM));
					}
					else if(Utils.isNumeric(valueM)){

						MethodUtils.invokeMethod(this,methodName,Integer.parseInt(valueM));
					}
					else
						//					PropertyUtils.setProperty(this,methodName,valueM);
						//							MethodUtils.setCacheMethods(false);
						MethodUtils.invokeMethod(this,methodName,valueM);
				}

			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	        if( StressTool.getArgsInt().length >= 2 && StressTool.getArgsInt()[0].indexOf("defaults-file") > 0) {
	            
		        for(int i  = 1 ; i < StressTool.getArgsInt().length ; i++)
		        {
		            	String param = null;
		            	String value = null;
		            	param = StressTool.getArgsInt()[i].split("=")[0].trim();
		            	value = StressTool.getArgsInt()[i].split("=")[1].trim();
		            	
				for(int ip = 0 ; ip < CLASS_PARAMETERS.size() ; ip++)
				    try {
					
					String methodName = (String)CLASS_PARAMETERS.get(ip);
					
					if(param.equals(methodName)){
	        				methodName = methodName.substring(0,1).toUpperCase() + methodName.substring(1,methodName.length()); 
	        				methodName = "set"+methodName;
	        				String valueM =value;
	        				//						System.out.println(methodName + " = " + valueM);
	        
	        				if(valueM != null){
	        				    if(valueM.equals("true") || valueM.equals("false")){
	        					MethodUtils.invokeMethod(this,methodName,Boolean.parseBoolean(valueM));
	        				    }
	        				    else if(Utils.isNumeric(valueM)){
	        
	        					MethodUtils.invokeMethod(this,methodName,Integer.parseInt(valueM));
	        				    }
	        				    else
	        					//							PropertyUtils.setProperty(this,methodName,valueM);
	        					//							MethodUtils.setCacheMethods(false);
	        					MethodUtils.invokeMethod(this,methodName,valueM);
	        				}
					}

				    } catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				    } catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				    } catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				    }
		                
		                
		           }
	        }		
	}

	public boolean isUseAutoIncrement() {
		return useAutoIncrement;
	}

	public void setUseAutoIncrement(boolean useAutoIncrement) {
		this.useAutoIncrement = useAutoIncrement;
	}

	/**
	 * @return the sleepWrite
	 */
	public int getSleepWrite() {
		return sleepWrite;
	}

	/**
	 * @param sleepWrite the sleepWrite to set
	 */
	public void setSleepWrite(int sleepWrite) {
		this.sleepWrite = sleepWrite;
	}

	/**
	 * @return the yearstart
	 */
	public int getYearstart() {
	    return yearstart;
	}

	/**
	 * @param yearstart the yearstart to set
	 */
	public void setYearstart(int yearstart) {
	    this.yearstart = yearstart;
	}

	/**
	 * @return the monthstart
	 */
	public int getMonthstart() {
	    return monthstart;
	}

	/**
	 * @param monthstart the monthstart to set
	 */
	public void setMonthstart(int monthstart) {
	    this.monthstart = monthstart;
	}

	/**
	 * @return the daystart
	 */
	public int getDaystart() {
	    return daystart;
	}

	/**
	 * @param daystart the daystart to set
	 */
	public void setDaystart(int daystart) {
	    this.daystart = daystart;
	}

	/**
	 * @return the daystotal
	 */
	public int getDaystotal() {
	    return daystotal;
	}

	/**
	 * @param daystotal the daystotal to set
	 */
	public void setDaystotal(int daystotal) {
	    this.daystotal = daystotal;
	}

	/**
	 * @return the partitionType
	 */
	public String getPartitionType() {
	    return partitionType;
	}

	/**
	 * @param partitionType the partitionType to set
	 */
	public void setPartitionType(String partitionType) {
	    this.partitionType = partitionType;
	}

	
	/**
	 * @return the debug
	 */
	public boolean isDebug() {
	    return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
	    this.debug = debug;
	}

	public int getLazyInterval() {
		return lazyInterval;
	}

	public void setLazyInterval(int lazyInterval) {
		this.lazyInterval = lazyInterval;
	}

	@Override
	public void setOnDuplicateKey(int onDuplicateKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOnDuplicateKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the jdbcUrlMap
	 */
	public Map getJdbcUrlMap() {
		return jdbcUrlMap;
	}

	/**
	 * @param jdbcUrlMap the jdbcUrlMap to set
	 */
	public void setJdbcUrlMap(Map jdbcUrlMap) {
		this.jdbcUrlMap = jdbcUrlMap;
	}

	/**
	 * @return the iBatchInsert
	 */
	public int getiBatchInsert() {
		return iBatchInsert;
	}

	/**
	 * @param iBatchInsert the iBatchInsert to set
	 */
	public void setiBatchInsert(int iBatchInsert) {
		this.iBatchInsert = iBatchInsert;
	}
}


/**
 * @author tusa
 * This class Object represent the lock generated by a running thread
 *  with a given Thread ID and record ID
 *  Do not support PK with multiple attributes
 */
class IdLock{
		Long recordId = (long)0;
		int THID = 0;
//		String tableName = null;
		/**
		 * @return the recordId
		 */
		public synchronized Long getRecordId() {
		    return recordId;
		}
		/**
		 * @param recordId the recordId to set
		 */
		public synchronized void setRecordId(Long recordId) {
		    this.recordId = recordId;
		}
		public synchronized int getTHID() {
			return THID;
		}
		public synchronized void setTHID(int tHID) {
			THID = tHID;
		}
		public IdLock(Long recordId, int tHID) {
			this.recordId = recordId;
			THID = tHID;
		}
}

/*
 * TODO: create class for PK with multiple fields
 */
//class IdMultiLock extends IdLock{
//    @SuppressWarnings("unchecked")
//    Map<String, Object> recordId = new SynchronizedMap(0);
//    int THID = 0;
//    public IdMultiLock(Map recordId, int tHID) {
//	if(recordId !=null
//		&& recordId instanceof Map
//		&& recordId.size() > 0
//		&& tHID > 0){
//	    this.recordId = recordId;
//	    this.THID = tHID;
//	}
//    }
//    
//    public IdMultiLock(Long recordId2, int tHID, Map<String, Object> recordIdIn,
//	    int tHID2) {
//	super(recordId2, tHID);
//	recordId = recordIdIn;
//	THID = tHID2;
//    }
//
//    public IdMultiLock(Long recordId, int tHID) {
//	super(recordId, tHID);
//	// TODO Auto-generated constructor stub
//    }
//}

/**
 *  * @author tusa
 * This class keep an object Map of locks for each Table
 * each table has its own list of IDLock
 * All methods are static and synchronized 
 */
	
class  TablesLock extends SynchronizedMap{
    	@SuppressWarnings("rawtypes")
	static Map <String,Map> tables = null;
	
        /**
         * 
         * @param tableName
         * @param id
         * @return IdLock
         * This method return an object of type IdLock or NULL if not present
         * The presence indicate a lock NULL means instead no Lock 
         * 
         */
	@SuppressWarnings("unchecked")
	public static synchronized IdLock getLockbyId(String tableName, Long id){
	    	Map<Long,IdLock> locks = null;
	    	
	    	if(tableName != null 
		&& !tableName.equals("")
		&& id  !=null
		&& id.longValue() > 0
		){ 
        	    if (tables == null) {
        		tables = new SynchronizedMap(0);
        		return null;
        	    } else {
        		locks = tables.get(tableName);
        	    }
		    
		    if(locks.size() > 0){
			return locks.get(id);
		    }
		}  
			return null;
		
	}
	/**
	 * 
	 * @param tableName
	 * @param lock
	 * @return true or false
	 * 
	 * This method is used to SET a lock on a ID for a given Table
	 * if lock is successfully set return true else false 
	 */
	@SuppressWarnings("unchecked")
	public static synchronized boolean setempIdLOCK(String tableName,IdLock lock){
	    Map<Long,IdLock> locks = null;

	    if(lock != null
		    && lock.recordId != null
		    && tableName != null 
		    && !tableName.equals("")){
		if(tables == null){
		    tables = new SynchronizedMap(0);
		}
		else{
		    locks = tables.get(tableName);
		}
		
		if(locks == null){
		    locks = new SynchronizedMap(0);
		    tables.put(tableName, locks);
		}
		    
		if(!locks.containsKey(lock.recordId)){
			locks.put(lock.getRecordId(), lock);
			tables.put(tableName, locks);
			return true;
		    }
		
	    }
	    return false;
	}
	/**
	 * 
	 * @param tableName
	 * @param id
	 * @return true or false
	 * 
	 * This method is used to remove a lock on a record on a given Table
	 */
	public static synchronized boolean deleteIdLockById(String tableName, Long id){
	    	if(tableName != null 
		&& !tableName.equals("")
		&& tables !=null
		&& tables.get(tableName) != null
		&& id  !=null
		&& id.longValue() > 0
		){
	    	    
	    	    if( tables.get(tableName)!=null
	    		    &&tables.get(tableName).get(id)!=null ){
	    		tables.get(tableName).remove(id);
	    		return true;
	    	    }
		}
	    return false;
	}
	/**
	 * 
	 * @param tableName
	 * @param id
	 * @return true or false
	 * 
	 * This method is used to remove a lock on a record on a given Table
	 */
	public static synchronized boolean deleteIdLockByThreadId(int id, boolean debug){
	    	if(
		tables !=null
		&& tables.size() > 0
		&& id > 0
		){
	    	    Iterator<String> it = tables.keySet().iterator();
	    	    while(it.hasNext()){
	    		String tableName = it.next();
	    		if(tables.get(tableName)!= null && tables.get(tableName).size() > 0){
	    		    Map<Long,IdLock> locks = tables.get(tableName);
	    		    Iterator<Long> itLock = locks.keySet().iterator();
	    		    while(itLock.hasNext()){
	    			Long Id = itLock.next();
	    			if(locks.get(Id).getTHID() == id){
	    			    TablesLock.deleteIdLockById(tableName, Id);
	    			}
	    		    }
			    if (debug)
				System.out.println(" [REMOVE LOCK] Thread_ID = "+ id + " TABLE LOCK MAP TABLE = " + tableName + " LOCKS PRESENT = " + TablesLock.size(tableName)  );

	    		}
	    	    }
	    	    return true;
	    }
		
	    return false;
	}
	
	/**
	 * 
	 * @param tableName
	 * @return true or false
	 * 
	 * This method reset ALL locks for the given table
	 */
	public static synchronized boolean deleteTable(String tableName){
	    	if(tableName != null 
		&& !tableName.equals("")
		&& tables !=null
		&& tables.get(tableName) != null
		){
	    	    
	    	    if( tables.get(tableName)!=null){
	    		tables.remove(tableName);
	    		tables.put(tableName,  new SynchronizedMap(0));
	    		return true;
	    	    }
		}
	    return false;
	}
	public static synchronized int size(String tableName){
	    if(tables !=null
		&& tables.get(tableName) != null
		){
			return tables.get(tableName).size();
	    }
	    else
		return 0;
	    
	}
	
}