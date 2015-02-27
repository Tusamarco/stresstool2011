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

//import com.mongodb.MongoClient;
//import com.mongodb.MongoException;
//import com.mongodb.WriteConcern;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import com.mongodb.DBCursor;
//import com.mongodb.ServerAddress;

import java.util.Arrays;

import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.UUID;

import org.apache.commons.beanutils.MethodUtils;


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
public  class RunnableMongodbQueryInsert extends RunnableInsertBasic implements Runnable, RunnableQueryInsertInterface {

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
	private String lazyInsert1 = "" ;
	private String lazyInsert2 = "";
	private String lazyLongText= "";
	private Map classConfig = null;
	private int numberOfprimaryTables = 1;
	private int numberOfSecondaryTables = 1;
	private boolean useAutoIncrement = false;
	private int sleepWrite = 0;
//	MongoClient mongoClient  = null;

	private static final ArrayList <String> CLASS_PARAMETERS = new ArrayList(Arrays.asList(
			"numberOfprimaryTables","numberOfSecondaryTables","useAutoIncrement","sleepWrite"));
	

//	public RunnableMongodbQueryInsert() {
//		try {
//		      mongoClient = new MongoClient( "localhost" , 27017 );
//		    
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//		}
//		catch (Exception ex)
//		{
//			// handle the error
//		}

		//    Random rnd = new Random();

//	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#run()
	 */
	public void run() {

		BufferedReader d = null;
		Connection conn = null;

		try {
			if(jdbcUrlMap.get("dbType") != null &&  !((String)jdbcUrlMap.get("dbType")).equals("MySQL"))
			{
				conn=DriverManager.getConnection((String)jdbcUrlMap.get("dbType"),"test", "test");
			}
			else
				conn= DriverManager.getConnection((String)jdbcUrlMap.get("jdbcUrl"));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}


		if(conn != null)
		{

			try{

				Statement stmt = null;
				//                ResultSet rs = null;
				//                ResultSet rs2 = null;

				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				stmt.execute("SET AUTOCOMMIT=0");
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
				int lazyInterval =0;

				for(int repeat = 0 ; repeat <= repeatNumber ; repeat++)
				{
					String query = null;
					ArrayList insert1 = null;
					ArrayList insert2 = null;
					int pk = 0 ;

					if(repeat > 0 && lazyInterval < 500){
						lazy = true;
						++lazyInterval;
					}
					else{
						lazy = false;
						lazyInterval =0;
					}


					intBlobInterval++;
					//IMPLEMENTING lazy
					Vector v = this.getTablesValues(lazy);

					insert1 = (ArrayList<String>) v.get(0);
					insert2 = (ArrayList<String>) v.get(1);

					//                    System.out.println(insert1);
					//                    System.out.println(insert2);

					//                    pk = ((Integer) v.get(2)).intValue();

					int[] iLine = {0,0};

					//                    pkStart = StressTool.getNumberFromRandom(2147483647).intValue();
					//                    pkEnds = StressTool.getNumberFromRandom(2147483647).intValue();



					try
					{

						long timeStart = System.currentTimeMillis();

						if(this.ignoreBinlog)
							stmt.execute("SET sql_log_bin=0");
						
//						stmt.execute("SET GLOBAL max_allowed_packet=1073741824");

						if(dbType.equals("MySQL") && !engine.toUpperCase().equals("BRIGHTHOUSE"))
							stmt.execute("BEGIN");
						else
							stmt.execute("COMMIT");
						//                                stmt.execute("SET TRANSACTION NAME 'TEST'");
						{
							Iterator<String> it = insert1.iterator();
							while(it.hasNext()){
								stmt.addBatch(it.next());
							}
						}

						if(!this.doSimplePk){
							if(intBlobInterval > intBlobIntervalLimit )
							{
								Iterator<String> it = insert2.iterator();
								while(it.hasNext()){
									stmt.addBatch(it.next());
								}
								intBlobInterval=0;

							}
						}

						iLine = stmt.executeBatch();
						stmt.clearBatch();
						
						//                            System.out.println("Query1 = " + insert1);
						//                            System.out.println("Query2 = " + insert2);
						//                            stmt.execute("START TRANSACTION");
						//                            stmt.execute(insert1);
						//                            iLine = stmt.executeBatch();
						//                            conn.commit();
						long timeEnds = System.currentTimeMillis();
						execTime = (timeEnds - timeStart);

					}
					catch (Exception sqle)
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
						stmt.execute("COMMIT");
						//                            intDeleteInterval++;
						if(doLog){

							System.out.println("Query Insert TH = " + this.getID()
									+ " Loop N = "+ repeat  + " " +iLine[0]+"|"+((iLine.length > 1)?iLine[1]:0)
									+ " Exec Time(ms) =" + execTime
									+ " Running = " + repeat + " of " + repeatNumber + " to go =" + (repeatNumber - repeat)
									+ " Using Lazy=" + lazy);
						}
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
		ArrayList <String> insertList1 = new ArrayList();
		ArrayList <String> insertList2 = new ArrayList();

		//        String uuid = UUID.randomUUID().toString();
		int pk = StressTool.getNumberFromRandom(2147483647).intValue();

		String insert1Str = "";

		//        INSERT INTO tbtest1 ("A","B","C","RECORDTYPE") VALUES(24351216,'d',14447453184,'mba');
		if(dbType.endsWith("MySQL"))
		{
			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){

				if(this.doSimplePk){
					insert1.append("insert INTO tbtest" + iTable + " (uuid,a,b,c,counter,partitionid,strrecordtype) VALUES");

				}
				else{
					insert1.append("insert INTO tbtest" + iTable + " (uuid,a,b,c,counter,partitionid,strrecordtype) VALUES");
				}
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


					for(int ibatch= 0 ; ibatch <=this.iBatchInsert; ibatch++ )
					{


						//                    uuid = UUID.randomUUID().toString();
						pk = StressTool.getNumberFromRandom(2147483647).intValue();


						if (ibatch > 0){
							insert1.append(",(UUID()," + pk + ",");
						}
						else
						{	
							insert1.append("(UUID()," + pk + ",");
						}

						insert1.append(insert1Str);
					}
				}
				else
				{
					insert1Str = "";
					insert1Str = insert1Str  + "(UUID()," + pk + ",";

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

				for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
					insert2Str = "";

					insert2Str = insert2Str  +"insert INTO tbtest_child" + iTable + " (a,stroperation) VALUES(";
					insert2Str = insert2Str  + pk + ",";
					if(lazy && !lazyInsert2.equals("")){
						insert2bStr = lazyInsert2;
					}
					else{
						lazyInsert2  =  "\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0";
						insert2bStr = lazyInsert2;
						//		        		insert2Str = insert2Str  + "\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0";
						//		        		lazyInsert2 = insert2Str;
					}

					if(!insert2Str.equals("")) 
						insertList2.add(insert2Str + insert2bStr);

				}

			}
		}


		v.add(0,insertList1);
		v.add(1,insertList2);
		//        v.add(2, new Integer(pk));

		return v;

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
		this.jdbcUrlMap = jdbcUrlMap;
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
		return (String)jdbcUrlMap.get("jdbcUrl");
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
		this.iBatchInsert = iBatchInsert;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryInsertInterface#getIBatchInsert()
	 */
	public int getIBatchInsert() {
		return iBatchInsert;
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
			if(jdbcUrlMap.get("dbType") != null &&  !((String)jdbcUrlMap.get("dbType")).equals("MySQL"))
			{
				conn=DriverManager.getConnection((String)jdbcUrlMap.get("dbType"),"test", "test");
			}
			else
				conn= DriverManager.getConnection((String)jdbcUrlMap.get("jdbcUrl"));

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
				sb.append(" `strrecordtype` char(3) NULL");
				if (this.isUseAutoIncrement()){
					sb.append(", PRIMARY KEY  (`autoInc`),  INDEX `IDX_a` (a),  INDEX `IDX_uuid` (uuid) ");
				}
				else{
					if(!this.doSimplePk)
						sb.append(", PRIMARY KEY  (`uuid`),  INDEX `IDX_a` (a) ");
					else 
						sb.append(", PRIMARY KEY  (`a`),  INDEX `IDX_uuid` (uuid) ");
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


				for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
					System.out.println("**** Please wait DROP table tbtest_child" + iTable + " it could take a LOT of time *******");
					stmt.execute(DropTables2+iTable);
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
	}

	public boolean isUseAutoIncrement() {
		return useAutoIncrement;
	}

	public void setUseAutoIncrement(boolean useAutoIncrement) {
		this.useAutoIncrement = useAutoIncrement;
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

	public void setNumberOfSecondaryTables(int numberOfSecondaryTables) {
		this.numberOfSecondaryTables = numberOfSecondaryTables;
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

}
