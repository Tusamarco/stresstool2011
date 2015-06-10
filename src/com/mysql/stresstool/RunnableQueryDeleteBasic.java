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
import java.util.Map;
import java.util.Vector;

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
public  class RunnableQueryDeleteBasic implements Runnable, RunnableQueryDeleteInterface {
	private File queriesFileIn;
	Connection conn = null;

	private Map jdbcUrlMap;
	private boolean repeat = false;
	private int repeatNumber = 0;
	private int ID;
	private Map classConfig = null;
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
	private double deleterowmaxpct = 0.1;
	private int deleterowsinterval=300;
	private long totalLineDeleted = 0 ;
	private int SelectLimit = 0;
	
	private int NumberOfSecondaryTables = 0 ; 
	private int NumberOfprimaryTables =0 ;
	private boolean useFK = false;

	private static final ArrayList <String> CLASS_PARAMETERS = new ArrayList(Arrays.asList(
			"numberOfprimaryTables","numberOfSecondaryTables","deleterowmaxpct", "deleterowsinterval","selectLimit","useFK"));


	public RunnableQueryDeleteBasic() {
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

		if(isDoDelete()){
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
					ResultSet rs = null;

					conn.setAutoCommit(false);
					{
						SoftReference sf = new SoftReference(conn.createStatement());
						stmt = (Statement) sf.get(); 
					}


					//                      stmt2 = conn.createStatement();
					stmt.execute("SET AUTOCOMMIT=0");
					long execTime = 0;
					ThreadInfo thInfo ;

					long threadTimeStart = System.currentTimeMillis();
					active = true;


					thInfo = new ThreadInfo();
					thInfo.setId(this.ID);
					thInfo.setType("delete");
					thInfo.setStatusActive(this.isActive());

					StressTool.setInfoDelete(this.ID,thInfo);

					int deletedRows = 0;

					long pkStart = 0;
					long pkEnds = 0;
					String[][] sqlParameterValues;
					int[] iLine ={0,0};


					//                      for(int repeat = 0 ; repeat < repeatNumber ; repeat++ ){
					//						    pkEndsAr[repeat] = StressTool.getNumberFromRandom(2147483647).intValue();
					//						    pkStartAr[repeat] = StressTool.getNumberFromRandom(pkEndsAr[repeat]- 10).intValue();
					//
					//					  }


					for(int repeat = 0 ; repeat < repeatNumber ; repeat++)
					{

						setTotalLineDeleted(0);
						
						try{
							
							Vector<Long> vValues = executeDeleteActions(conn);
							if(vValues != null){
								execTime = ((Long)vValues.get(0)).intValue();
								pkStart = ((Long)vValues.get(1)).intValue();
								pkEnds = ((Long)vValues.get(2)).intValue();
							}	
							
						} catch (SQLException sqle) {
							conn.rollback();
							/** 
				                  Silently skip any deadlock
							 **/ 
							//				    		sqle.printStackTrace();
							if(StressTool.getErrorLogHandler() != null){
								StressTool.getErrorLogHandler().appendToFile(sqle.toString());
							}

							//                                  sqle.printStackTrace();
						} finally {

						}

						

						if (doLog) {
							
							System.out.println("Query Delete TH = " + this.getID()
									+ " IdStart = " + pkStart 
									+ " IdEnd = " + pkEnds + " " 
									+ "Deleted rows " 
									+ (getTotalLineDeleted())   
									+ " Exec Time(ms) = " + execTime
									+ " Running = " + repeat + " of " + repeatNumber + " to go = " + (repeatNumber - repeat)
									);
									
						}


						thInfo.setExecutedLoops(repeat);
						Thread.sleep(sleepFor);
					}

					stmt.close();
					//                      stmt2.close();
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
					else if(Utils.isDouble(valueM)){

						MethodUtils.invokeMethod(this,methodName,Double.parseDouble(valueM));
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
/**
 * This method execute the delete action whatever it will be, and return a Vector with objects
 * the following values
 * @return
 * V1 time taken in system time
 * V2 value of the delete key minor
 * V3 value of the delete key major
 * @throws SQLException 
 */
    public Vector<Long> executeDeleteActions(Connection conn) throws SQLException{
    	
    		/**
    		 * Not implmented in the basic
    		 * must be done in the extended class
    		 */
    	return null;
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
		return isDoDelete();
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
	public boolean doSimplePk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void executeLocalExtensions(Connection conn) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the doDelete
	 */
	public boolean isDoDelete() {
		return doDelete;
	}

	/**
	 * @return the totalLineDeleted
	 */
	public long getTotalLineDeleted() {
		return totalLineDeleted;
	}

	/**
	 * @param totalLineDeleted the totalLineDeleted to set
	 */
	public void setTotalLineDeleted(long totalLineDeleted) {
		this.totalLineDeleted = totalLineDeleted;
	}

	public int getSelectLimit() {
		return SelectLimit;
	}

	public void setSelectLimit(int selectLimit) {
		SelectLimit = selectLimit;
	}

	/**
	 * @return the numberOfSecondaryTables
	 */
	public int getNumberOfSecondaryTables() {
		return NumberOfSecondaryTables;
	}

	/**
	 * @param numberOfSecondaryTables the numberOfSecondaryTables to set
	 */
	public void setNumberOfSecondaryTables(int numberOfSecondaryTables) {
		NumberOfSecondaryTables = numberOfSecondaryTables;
	}

	/**
	 * @return the numberOfprimaryTables
	 */
	public int getNumberOfprimaryTables() {
		return NumberOfprimaryTables;
	}

	/**
	 * @param numberOfprimaryTables the numberOfprimaryTables to set
	 */
	public void setNumberOfprimaryTables(int numberOfprimaryTables) {
		NumberOfprimaryTables = numberOfprimaryTables;
	}

	/**
	 * @return the deleterowmaxpct
	 */
	public double getDeleterowmaxpct() {
		return deleterowmaxpct;
	}

	/**
	 * @param deleterowmaxpct the deleterowmaxpct to set
	 */
	public void setDeleterowmaxpct(double deleterowmaxpct) {
		this.deleterowmaxpct = deleterowmaxpct;
	}

	/**
	 * @return the deleterowsinterval
	 */
	public int getDeleterowsinterval() {
		return deleterowsinterval;
	}

	/**
	 * @param deleterowsinterval the deleterowsinterval to set
	 */
	public void setDeleterowsinterval(int deleterowsinterval) {
		this.deleterowsinterval = deleterowsinterval;
	}

	public boolean isUseFK() {
		return useFK;
	}

	public void setUseFK(boolean useFK) {
		this.useFK = useFK;
	}

}
