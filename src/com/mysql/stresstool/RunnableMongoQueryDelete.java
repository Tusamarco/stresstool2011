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
import java.sql.*;
import java.util.Map;

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
public  class RunnableMongoQueryDelete implements Runnable, RunnableQueryDeleteInterface {
    private File queriesFileIn;
    Connection conn = null;

    private Map jdbcUrlMap;
    private boolean repeat = false;
    private int repeatNumber = 0;
    private int ID;
    /* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getJdbcUrlMap()
	 */
    public Map getJdbcUrlMap() {
		return jdbcUrlMap;
	}

	/* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setJdbcUrlMap(java.util.Map)
	 */
	public void setJdbcUrlMap(Map jdbcUrlMap) {
		this.jdbcUrlMap = jdbcUrlMap;
	}

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

    public RunnableMongoQueryDelete() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (Exception ex)
        {
            // handle the error
        }


    }

    /* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#run()
	 */
    public void run() {

    	if(doDelete){
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
                      Statement stmt2 = null;
                      ResultSet rs = null;
 
                      conn.setAutoCommit(false);
                      stmt = conn.createStatement();
                      stmt2 = conn.createStatement();
                      stmt.execute("SET AUTOCOMMIT=0");
                      long execTime = 0;
                      int pkStart = 0 ;
                      int pkEnds = 0 ;
                      ThreadInfo thInfo ;

                      long threadTimeStart = System.currentTimeMillis();
                      active = true;


                      thInfo = new ThreadInfo();
                      thInfo.setId(this.ID);
                      thInfo.setType("delete");
                      thInfo.setStatusActive(this.isActive());

                      StressTool.setInfoDelete(this.ID,thInfo);

                      int deletedRows = 0;

                      int[] pkStartAr = null;
                      int[] pkEndsAr = null;
                      String[][] sqlParameterValues;
                      int[] iLine ={0,0};
                      

//                      for(int repeat = 0 ; repeat < repeatNumber ; repeat++ ){
//						    pkEndsAr[repeat] = StressTool.getNumberFromRandom(2147483647).intValue();
//						    pkStartAr[repeat] = StressTool.getNumberFromRandom(pkEndsAr[repeat]- 10).intValue();
//
//					  }
                      
                      
                      for(int repeat = 0 ; repeat < repeatNumber ; repeat++)
                      {
                    	  int maxDel = 0;
//                    	  pkStart = pkStartAr[repeat];
//                          pkEnds = pkEndsAr[repeat];

//                              System.gc();
                              
                              String deleteCheck1 ="";

                              try {
                            	  stmt.execute("BEGIN");

	                            	  ResultSet rMax = stmt2.executeQuery("Select max(a) from tbtest1");
	                            	  while (rMax.next()){
	                            		  maxDel = rMax.getInt(1);
                            		  
                            	  }
	      						  pkEnds = StressTool.getNumberFromRandom(maxDel).intValue();
	    						  pkStart = pkEnds - 200;

	                              if(dbType.equals("MySQL"))
	                              {

	                                  deleteCheck1 =
	                                          "Select uuid, a from test.tbtest1 where a > " +
	                                          pkStart +
	                                          " and tbtest1.a <" + pkEnds + " LIMIT 50";
	                              }
                            	  
                            	  
                            	  rs = stmt2.executeQuery(deleteCheck1);

                                  long timeStart = System.currentTimeMillis();

                                  while(rs.next())
                                  {

                                     iLine[0] = stmt.executeUpdate("Delete from test.tbtest2 where a = '"+ rs.getString("a") +"'");
                                     iLine[1] = stmt.executeUpdate("Delete from test.tbtest1 where uuid = '"+ rs.getString("uuid") +"'");
                                      stmt.execute("COMMIT");
                                     deletedRows = deletedRows++;
                                  }

                                  long timeEnds = System.currentTimeMillis();
                                  execTime = (timeEnds - timeStart);

                              } catch (SQLException sqle) {
                                  conn.rollback();
                                  System.out.println("Query Delete1 = " + deleteCheck1);                                     
                                  sqle.printStackTrace();
                              } finally {

                          }

	                      if (doLog) {
									  System.out.println(
									          "Query Delete TH = " + this.getID() +" Id = " + pkStart + " IdEnd = " + pkEnds + " " + "Deleted lines " +
									  (iLine[0]+iLine[1]) + " Exec Time(ms) =" + execTime);
	                      }
                          
                       
                       thInfo.setExecutedLoops(repeat);
                       Thread.sleep(sleepFor);
                      }

                      stmt.close();
                      stmt2.close();
                      conn.close();

                          
                              
                      long threadTimeEnd = System.currentTimeMillis();
                      this.executionTime = (threadTimeEnd - threadTimeStart);
                      this.setExecutionTime(executionTime);
                      active = false;
//                      System.out.println("Query Delete TH = " + this.getID() +" Id = " + pkStart + 
//                    		  " IdEnd = " + pkEnds + " " + "Deleted lines " +
//                              deletedRows + " Exec Time(ms) =" + execTime + " Sec =" + (execTime/1000));

                      thInfo.setExecutionTime(executionTime);
                      thInfo.setStatusActive(false);
                      StressTool.setInfoDelete(this.ID,thInfo);
                      return;



                  }
                  catch(Exception ex)
                  {
                      ex.printStackTrace();
                  }



              }

    	    }
          }



          public static void main(String[] args) {
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setQueriesFileIn(java.io.File)
		 */
        public void setQueriesFileIn(File queriesFileIn) {
              this.queriesFileIn = queriesFileIn;
          }



          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setRepet(boolean)
		 */
        public void setRepet(boolean repeat) {
              this.repeat = repeat;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setRepeatNumber(int)
		 */
        public void setRepeatNumber(int repeatNumber) {
              this.repeatNumber = repeatNumber;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setDoLog(boolean)
		 */
        public void setDoLog(boolean doLog) {
              this.doLog = doLog;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setSleepFor(int)
		 */
        public void setSleepFor(int sleepFor) {
              this.sleepFor = sleepFor;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setOperationShort(boolean)
		 */
        public void setOperationShort(boolean operationShort) {
              this.operationShort = operationShort;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setDbType(java.lang.String)
		 */
        public void setDbType(String dbType) {
              this.dbType = dbType;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setEngine(java.lang.String)
		 */
        public void setEngine(String engine) {
              this.engine = engine;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setExecutionTime(long)
		 */
        public void setExecutionTime(long executionTime) {
              this.executionTime = executionTime;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setDoDelete(boolean)
		 */
        public void setDoDelete(boolean doDelete) {
              this.doDelete = doDelete;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setActive(boolean)
		 */
        public void setActive(boolean active) {
              this.active = active;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setUseBatchInsert(boolean)
		 */
        public void setUseBatchInsert(boolean useBatchInsert) {
              this.useBatchInsert = useBatchInsert;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setID(int)
		 */
        public void setID(int ID) {
              this.ID = ID;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getQueriesFileIn()
		 */
        public File getQueriesFileIn() {
              return queriesFileIn;
          }

//          public String getJdbcUrl() {
//              return jdbcUrl;
//          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#isRepeat()
		 */
        public boolean isRepeat() {
              return repeat;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getRepeatNumber()
		 */
        public int getRepeatNumber() {
              return repeatNumber;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#isDoLog()
		 */
        public boolean isDoLog() {
              return doLog;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getSleepFor()
		 */
        public int getSleepFor() {
              return sleepFor;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#isOperationShort()
		 */
        public boolean isOperationShort() {
              return operationShort;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getDbType()
		 */
        public String getDbType() {
              return dbType;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getEngine()
		 */
        public String getEngine() {
              return engine;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getExecutionTime()
		 */
        public long getExecutionTime() {
              return executionTime;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getDoDelete()
		 */
        public boolean getDoDelete() {
              return doDelete;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#isActive()
		 */
        public boolean isActive() {
              return active;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#isUseBatchInsert()
		 */
        public boolean isUseBatchInsert() {
              return useBatchInsert;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getID()
		 */
        public int getID() {
              return ID;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#getMySQLStatistics()
		 */
        public MySQLStats getMySQLStatistics() {
              return mySQLStatistics;
          }

          /* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setMySQLStatistics(com.mysql.stresstool.MySQLStats)
		 */
        public void setMySQLStatistics(MySQLStats mySQLStatistics) {
              this.mySQLStatistics = mySQLStatistics;
          }

		/* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#setDoSimplePk(boolean)
		 */
		public void setDoSimplePk(boolean doSimplePk) {
			this.doSimplePk = doSimplePk;
		}

		/* (non-Javadoc)
		 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#isDoSimplePk()
		 */
		public boolean isDoSimplePk() {
			return doSimplePk;
		}

		@Override
		public void setJdbcUrl(Map props) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getJdbcUrl() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setClassConfiguration(Map mConfig) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean doSimplePk() {
			// TODO Auto-generated method stub
			return false;
		}
      }
