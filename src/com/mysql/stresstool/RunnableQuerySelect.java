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
public  class RunnableQuerySelect  implements Runnable,RunnableSelectQueryInterface {

	private File queriesFileIn;
//    Connection conn = null;

    private Map jdbcUrlMap;
    private boolean repeat = false;
    private int repeatNumber = 0;
    private int ID;
    private boolean doLog = true;
    private int sleepFor;
    private String dbType="MySQL";
    private String engine;
    private long executionTime;
    private MySQLStats mySQLStatistics;
    private boolean active;
    private boolean doSimplePk = false;    
    private int iBatchSelect=200;
    private String sqlQuery ;
    private PrimaryKeyRangeDefiner pkRange;
    private Map classConfig = null;
	private int numberOfprimaryTables = 1;
	private int numberOfSecondaryTables = 1;
	private boolean useAutoIncrement = false;
	private int numberOfJoinTables = 0;
	private int numberOfIntervalKeys = 0;
	private String selectFilterMethod = "range";
	private String joinField = null;
	private boolean forceIndex=false;
	private String indexName = null;
	private int sleepSelect=0;
	private int selectLimit = 100;

	private static final ArrayList <String> CLASS_PARAMETERS = new ArrayList(Arrays.asList(
			"numberOfprimaryTables","numberOfSecondaryTables","useAutoIncrement", "numberOfJoinTables",
			"numberOfIntervalKeys","selectFilterMethod","joinField","forceIndex","indexName","sleepSelect","selectLimit"));

    
    public int getSelectLimit() {
		return selectLimit;
	}

	public void setSelectLimit(int selectLimit) {
		this.selectLimit = selectLimit;
	}

	public RunnableQuerySelect() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (Exception ex)
        {
            // handle the error
        }

    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes the object's <code>run</code> method to be called in
     * that separately executing thread.
     *
     * @todo Implement this java.lang.Runnable method
     */
    public void run() {

        Connection conn = createConnection();        

        if( conn != null)
        {
            ThreadInfo thInfo ;
            thInfo = new ThreadInfo();
            thInfo.setId(this.ID);
            thInfo.setType("select");
            active = true;
            thInfo.setStatusActive(this.isActive());
            StressTool.setInfoSelect(this.ID,thInfo);

        	try{
                Statement stmt = null;
                ResultSet rs = null;
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                PreparedStatement pstmt ;
                long execTime = 0;
                int repeat = 0;
                int sqlParameterNumbers =0;

                long threadTimeStart = System.currentTimeMillis();
                

               	if(this.sqlQuery != null && !this.sqlQuery.equals(""))
            	{
               		try{
               			
               			pstmt = conn.prepareStatement(sqlQuery);
               			sqlParameterNumbers = pstmt.getParameterMetaData().getParameterCount();
               			
               		}
               		catch(SQLException sqlex)
               		{
               			sqlex.printStackTrace();
               			conn.close();
               			return;
               		}
            	}
               	else
               	{
               		pstmt = null;
               	}
                
                String[][] sqlParameterValues;
                
                
                if(this.sqlQuery == null || this.sqlQuery.equals(""))
                {
                	pkRange = new PrimaryKeyRangeDefiner(repeatNumber);
                    pkRange.setLastResetLoop(0);
                	pkRange = setPkRange(conn,pkRange);
                	
                	//										3351000110
                	sqlParameterValues = null;
                }
                else
                {
                	sqlParameterValues = new String[repeatNumber][sqlParameterNumbers];
                	for(repeat = 0 ; repeat < repeatNumber ; repeat++ )
                	{
                		for(int pvalue = 0; pvalue < sqlParameterNumbers; pvalue++ )
                		{
                			sqlParameterValues[repeat][pvalue] = "3" + String.valueOf(StressTool.getNumberFromRandom(500000000).intValue());
                		}
                	}
                	
                	
                }

                for(repeat = 0 ; repeat < repeatNumber ; repeat++)
                {
                    int pkStart = 0 ;
                    int pkEnds = 0 ;
                    int recordFound = 0;
                    if(repeat > 0 && pkRange.getLooprefresh() < (repeat - pkRange.getLastResetLoop())){
                    	pkRange = setPkRange(conn,pkRange);
                    	pkRange.setLastResetLoop(repeat);
                    }
                    
                    
                    if(pkRange.getKeyStart(repeat) > 0 && pkRange.getKeyEnd(repeat) > 0 )
                    {
                    	pkStart = pkRange.getKeyStart(repeat) ;  
                    	pkEnds  = pkRange.getKeyEnd(repeat) ;
                    }
                    
                    if(pkStart > pkEnds)
                    {
                        int dummy = pkStart;
                        pkStart = pkEnds;
                        pkEnds = dummy;
                    }
                    
                    if(pkEnds ==0)
                    	continue;

                    String select ="";
                    
                    
                    select = generateSelectString(pkStart, pkEnds, select);
                    int[] iLine = null ;



                        try
                        {

                            long timeStart = System.currentTimeMillis();

                            try{
                            	if(this.sqlQuery != null && !this.sqlQuery.equals(""))
                            	{

                        			if(this.getIBatchSelect() > 0){
                        				for(int pstmtbatch = 0; pstmtbatch < this.getIBatchSelect(); pstmtbatch++ )
                        				{
                                    		for( int ii = 1; ii <= sqlParameterNumbers; ii++)
                                    		{
                                    			pstmt.setString(ii, sqlParameterValues[StressTool.getNumberFromRandom(repeatNumber).intValue()][ii - 1]);
                                        		rs = pstmt.executeQuery();
//                                    			System.out.print(this.sqlQuery + " BATCH | " + ii +" | " + sqlParameterValues[repeat][ii - 1] + "\n") ;
                                    		}
                        				}
                        			}
                        			else{
                                		for( int ii = 1; ii <= sqlParameterNumbers; ii++)
                                		{
	                            			pstmt.setString(ii, sqlParameterValues[repeat][ii - 1]);
//	                            			System.out.print(this.sqlQuery + " | " + ii +" | " + sqlParameterValues[repeat][ii - 1] + "\n") ;
                                		}
                                		rs = pstmt.executeQuery();

                        			}

                            	}
                            	else
                            	{
	                                rs = stmt.executeQuery(select);
	                                rs.last();
                            	}
                            }catch(Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            finally
                            {
                            	if(rs != null)
                            	{
                            		rs.close();
                            		rs =null;
                            	}
                            	
                            }
                            long timeEnds = System.currentTimeMillis();
//                            recordFound = rs.getRow();
                            
                            execTime = (timeEnds - timeStart);
                            thInfo.setExecutedLoops(repeat);


                        }
                        catch (SQLException sqle)
                        {

                            sqle.printStackTrace();
                        }
                        finally
                        {
                        	if(rs != null)
                        	{
                        		rs.close();
                        		rs =null;
                        	}
                        	
                        	
//                            intDeleteInterval++;
                            if(doLog)
                                System.out.println("Query Select TH = " + this.getID() + " Id = "+ pkStart +" IdEnd = " + pkEnds + " Record found = "+recordFound+" Exec Time(ms) =" + execTime);
                            Thread.sleep(sleepFor);
                        }

                        if(sleepFor > 0 || this.getSleepSelect() > 0)
                        {
                            if(this.getSleepSelect() > 0)
                            {
                            	Thread.sleep(getSleepSelect());
                            }
                            else
                            	Thread.sleep(sleepFor);
                        }
                }
//                System.out.println("Query Select/Delete TH = " + this.getID() + " COMPLETED! ");

                long threadTimeEnd = System.currentTimeMillis();
                this.executionTime = (threadTimeEnd - threadTimeStart);
//                this.setExecutionTime(executionTime);

                active = false;
                thInfo.setExecutionTime(executionTime);
                thInfo.setStatusActive(false);
                StressTool.setInfoSelect(this.ID,thInfo);
                stmt = null;

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

	/**
	 * @param pkStart
	 * @param pkEnds
	 * @param select
	 * @return
	 */
	private String generateSelectString(int pkStart, int pkEnds, String select) {

		int numberOfRangeKey = this.getNumberOfIntervalKeys();
		String joinAttribute = this.getJoinField();

		String primaryTable = "tbtest" + StressTool.getNumberFromRandomMinMax(1, getNumberOfprimaryTables());
		int maxJoinTableIndex = StressTool.getNumberFromRandomMinMax(1, getNumberOfJoinTables()).intValue();
		String secondaryChildRetieved = "tbtest_child" + StressTool.getNumberFromRandomMinMax(1, maxJoinTableIndex).intValue();

		String indexHint = "";
		if(this.forceIndex && this.getIndexName() != null){
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
		
		if(dbType.equals("MySQL") && !this.isDoSimplePk() && getNumberOfJoinTables() == 0){
			select = "Select "
						+ primaryTable + ".a,b,c,counter,"
						+ primaryTable +".time,"
						+ primaryTable +".partitionid,"
						+ primaryTable +".strrecordtype" ;
			
			if(this.getSelectFilterMethod().toLowerCase().equals("range")){
				select = select 
						+ "from "+ primaryTable 
						+ indexHint 
						+" where "+ primaryTable +".a > " 
						+ pkStart + " and "
						+ primaryTable +".a <" + pkEnds 
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
	    else if(dbType.equals("MySQL") && !this.isDoSimplePk() && getNumberOfJoinTables() > 0)
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
				select = select 
							+ " where "
							+ primaryTable 
							+".a BETWEEN " + pkStart + " and " + pkEnds 
							+ " limit "+ this.getSelectLimit();
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

	/**
	 * @param conn
	 */
	private PrimaryKeyRangeDefiner setPkRange(Connection conn,PrimaryKeyRangeDefiner pkRange) {

		int repeat = pkRange.getLastResetLoop();
		if(pkRange.getLen() == 0){
			
			int[] values = getMaxSelectValue(conn);
			
			for(int repeatLoc = repeat ; repeatLoc < repeatNumber ; repeatLoc++ )
			{
				pkRange.setKeyStart(values[0]);
				pkRange.setKeyEnd(StressTool.getNumberFromRandomMinMax(values[0],values[1]).intValue());
			}
		}
		else if (repeat > 0 && pkRange.isRefresh() && pkRange.getLooprefresh() < repeat ){

			pkRange.reset();
			int[] values = getMaxSelectValue(conn);
			for(int repeatLoc = repeat  ; repeatLoc < repeatNumber ; repeatLoc++ )
			{
				pkRange.setKeyStart(values[0]);
				pkRange.setKeyEnd(StressTool.getNumberFromRandomMinMax(values[0],values[1]).intValue());
			}
		}
		return pkRange;
	}

	/**
	 * @return
	 * check for the max value of the table tbtest1 and for the min value return an array of int where element 0 is MIN element 1 is MAX
	 */
	private int[] getMaxSelectValue(Connection conn) {
		//return StressTool.getNumberFromRandom(2147483647).intValue();
		String primaryTable = "tbtest" + StressTool.getNumberFromRandomMinMax(1, getNumberOfprimaryTables());
		
        Statement stmt = null;
        ResultSet rs = null;
        int[] values = new int[2];
        
        try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT MIN(a) mi, MAX(a) ma from " + primaryTable + " FORCE INDEX(IDX_a)");
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

	/**
	 * @param pkStart
	 * @param pkEnds
	 * @param sPk
	 * @return
	 * Rerurn the interval of values for an IN syntax comma separated
	 */
	private String calculaltePK(int pkStart, int pkEnds, String sPk) {
		int iloop =0; 
		for(int iPk = pkStart; iPk <= pkEnds; iPk++ )
		{
			sPk = sPk + iPk;
			if(iloop++ >= this.getIBatchSelect())
				break;

			if (iPk != pkEnds)
				sPk = sPk + ",";
		}
		return sPk;
	}

	/**
	 * @Generate internal connection for launched thread one per thread no shared
	 */
	private Connection createConnection() {
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
		return conn;
	}


    Vector getTablesValues() {


        return null;

     }

    public static void main(String[] args) {
    }

    public void setQueriesFileIn(File queriesFileIn) {
        this.queriesFileIn = queriesFileIn;
    }

    public void setJdbcUrl(Map jdbcUrlMapIn) {
    	this.jdbcUrlMap = (Map)jdbcUrlMapIn;
    }


    public void setRepet(boolean repeat) {
        this.repeat = repeat;
    }

    public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    public void setDoLog(boolean doLog) {
        this.doLog = doLog;
    }

    public void setSleepFor(int sleepFor) {
        this.sleepFor = sleepFor;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public File getQueriesFileIn() {
        return queriesFileIn;
    }

    public String getJdbcUrl() {
        return (String)jdbcUrlMap.get("jdbcUrl");
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getRepeatNumber() {
        return repeatNumber;
    }

    public boolean isDoLog() {
        return doLog;
    }

    public int getSleepFor() {
        return sleepFor;
    }

    public String getDbType() {
        return dbType;
    }

    public String getEngine() {
        return engine;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getID() {
        return ID;
    }
    public MySQLStats getMySQLStatistics() {
        return mySQLStatistics;
    }

    public boolean isActive() {
        return active;
    }

    public void setMySQLStatistics(MySQLStats mySQLStatistics) {
        this.mySQLStatistics = mySQLStatistics;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

	/**
	 * @param doSimplePk the doSimplePk to set
	 */
	public void setDoSimplePk(boolean doSimplePk) {
		this.doSimplePk = doSimplePk;
	}

	/**
	 * @return the doSimplePk
	 */
	public boolean isDoSimplePk() {
		return doSimplePk;
	}

	/**
	 * @param iBatchSelect the iBatchSelect to set
	 */
	public void setIBatchSelect(int iBatchSelect) {
		this.iBatchSelect = iBatchSelect;
	}

	/**
	 * @return the iBatchSelect
	 */
	public int getIBatchSelect() {
		return iBatchSelect;
	}

	/**
	 * @return the sqlQuery
	 */
	
	private String getSqlQuery() {
		return sqlQuery;
	}

	/**
	 * @param sqlQuery the sqlQuery to set
	 */
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	@Override
	public boolean doSimplePk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDoDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOperationShort() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUseBatchInsert() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDoDelete(boolean doDelete) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOperationShort(boolean operationShort) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUseBatchInsert(boolean useBatchInsert) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the joinField
	 */
	public String getJoinField() {
		return joinField;
	}

	/**
	 * @param joinField the joinField to set
	 */
	public void setJoinField(String joinField) {
		this.joinField = joinField;
	}

	/**
	 * @return the numberOfprimaryTables
	 */
	public int getNumberOfprimaryTables() {
		return numberOfprimaryTables;
	}

	/**
	 * @param numberOfprimaryTables the numberOfprimaryTables to set
	 */
	public void setNumberOfprimaryTables(int numberOfprimaryTables) {
		this.numberOfprimaryTables = numberOfprimaryTables;
	}

	/**
	 * @return the numberOfSecondaryTables
	 */
	public int getNumberOfSecondaryTables() {
		return numberOfSecondaryTables;
	}

	/**
	 * @param numberOfSecondaryTables the numberOfSecondaryTables to set
	 */
	public void setNumberOfSecondaryTables(int numberOfSecondaryTables) {
		this.numberOfSecondaryTables = numberOfSecondaryTables;
	}

	/**
	 * @return the numberOfJoinTables
	 */
	public int getNumberOfJoinTables() {
		return numberOfJoinTables;
	}

	/**
	 * @param numberOfJoinTables the numberOfJoinTables to set
	 */
	public void setNumberOfJoinTables(int numberOfJoinTables) {
		this.numberOfJoinTables = numberOfJoinTables;
	}

	/**
	 * @return the numberOfIntervalKeys
	 */
	public int getNumberOfIntervalKeys() {
		return numberOfIntervalKeys;
	}

	/**
	 * @param numberOfIntervalKeys the numberOfIntervalKeys to set
	 */
	public void setNumberOfIntervalKeys(int numberOfIntervalKeys) {
		this.numberOfIntervalKeys = numberOfIntervalKeys;
	}

	/**
	 * @return the selectFilterMethod
	 */
	public String getSelectFilterMethod() {
		return selectFilterMethod;
	}

	/**
	 * @param selectFilterMethod the selectFilterMethod to set
	 */
	public void setSelectFilterMethod(String selectFilterMethod) {
		this.selectFilterMethod = selectFilterMethod;
	}

    /**
	 * @return the forceIndex
	 */
	public boolean isForceIndex() {
		return forceIndex;
	}

	/**
	 * @param forceIndex the forceIndex to set
	 */
	public void setForceIndex(boolean forceIndex) {
		this.forceIndex = forceIndex;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return indexName;
	}

	/**
	 * @param indexName the indexName to set
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	/**
	 * @return the useAutoIncrement
	 */
	public boolean isUseAutoIncrement() {
		return useAutoIncrement;
	}

	/**
	 * @param useAutoIncrement the useAutoIncrement to set
	 */
	public void setUseAutoIncrement(boolean useAutoIncrement) {
		this.useAutoIncrement = useAutoIncrement;
	}

	/**
	 * @return the sleepSelect
	 */
	public int getSleepSelect() {
		return sleepSelect;
	}

	/**
	 * @param sleepSelect the sleepSelect to set
	 */
	public void setSleepSelect(int sleepSelect) {
		this.sleepSelect = sleepSelect;
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
}
