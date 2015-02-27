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
import java.sql.*;
import java.util.Vector;
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
public  class RunnableQuerySelect_old  implements Runnable,RunnableSelectQueryInterface {
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
    
    public RunnableQuerySelect_old() {
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

        if( conn != null)
        {

            try{
                Statement stmt = null;
                ResultSet rs = null;
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                PreparedStatement pstmt ;
                long execTime = 0;
                ThreadInfo thInfo ;
                int sqlParameterNumbers =0;

                long threadTimeStart = System.currentTimeMillis();
                active = true;

                thInfo = new ThreadInfo();
                thInfo.setId(this.ID);
                thInfo.setType("select");
                thInfo.setStatusActive(this.isActive());

                StressTool.setInfoSelect(this.ID,thInfo);
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
                
                int[] pkStartAr = null;
                int[] pkEndsAr = null;
                String[][] sqlParameterValues;
                
                if(this.sqlQuery == null || this.sqlQuery.equals(""))
                {
                    pkStartAr = new int[repeatNumber];
                    pkEndsAr  = new int[repeatNumber];
                    
                	for(int repeat = 0 ; repeat < repeatNumber ; repeat++ )
                	{
	                    pkStartAr[repeat] = StressTool.getNumberFromRandom(2147483647).intValue();
	                    pkEndsAr[repeat] = StressTool.getNumberFromRandom(2147483647).intValue();
                	}
                //										3351000110
                	sqlParameterValues = null;
                }
                else
                {
                	sqlParameterValues = new String[repeatNumber][sqlParameterNumbers];
                	for(int repeat = 0 ; repeat < repeatNumber ; repeat++ )
                	{
                		for(int pvalue = 0; pvalue < sqlParameterNumbers; pvalue++ )
                		{
                			sqlParameterValues[repeat][pvalue] = "3" + String.valueOf(StressTool.getNumberFromRandom(500000000).intValue());
                		}
                	}
                	
                	
                }

                for(int repeat = 0 ; repeat < repeatNumber ; repeat++)
                {
                    int pkStart = 0 ;
                    int pkEnds = 0 ;
                    int recordFound = 0;

                    if(pkStartAr !=null && pkEndsAr !=null )
                    {
                    	pkStart = pkStartAr[repeat] ;
                    	pkEnds  = pkEndsAr[repeat] ;
                    }
                    
                    if(pkStart > pkEnds)
                    {
                        int dummy = pkStart;
                        pkStart = pkEnds;
                        pkEnds = dummy;
                    }

                    String select ="";
                    
                    
                    if(dbType.equals("MySQL") && !this.isDoSimplePk())
                    {
                        select =
                                "Select tbtest1.a,b,c,counter,tbtest1.time,partitonid,strrecordtype, stroperation,tbtest2.time ";
                        select = select +
                                "from test.tbtest1, test.tbtest2 where tbtest1.a = tbtest2.a and tbtest1.a > " +
                                 pkStart + " and tbtest1.a <" + pkEnds;
                    }
                    else if(this.isDoSimplePk())
                    {
                    	String sPk ="";
                    	int iloop =0; 
                    	for(int iPk = pkStart; iPk <= pkEnds; iPk++ )
                    	{
                    		sPk = sPk + iPk;
                    		if(iloop++ >= this.getIBatchSelect())
                    			break;

                    		if (iPk != pkEnds)
                    			sPk = sPk + ",";
                    	}
                    	
                        select =
                            "Select tbtest1.a,b,c,counter,tbtest1.time,strrecordtype ";
                        select = select +
                            "from test.tbtest1 where tbtest1.a in( " + sPk + " )";
                    	
                    }	

                    else	
                    {
                        select =
                                "Select tbtest1.a,b,c,counter,tbtest1.time,partitonid,strrecordtype, stroperation,tbtest2.time ";
                        select = select +
                                "from test.tbtest1, test.tbtest2 where tbtest1.a = tbtest2.a and tbtest1.a > " +
                                 pkStart + " and tbtest1.a <" + pkEnds;

                    }
//                    System.out.println(select);
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




                        if(sleepFor > 0)
                        {
                            Thread.sleep(sleepFor * 5000);
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


    Vector getTablesValues() {

        String longtextFld = "";
        int afld = 0;
        long counterFld = 0 ;

        longtextFld = StressTool.getStringFromRandom(20000);
//        longtextFld = runThSelect.getStringFromRandom(65535);

        Vector v = new Vector();



        StringBuffer insert1 = new StringBuffer();
        StringBuffer insert2 = new StringBuffer();
        int pk = StressTool.getNumberFromRandom(2147483647).intValue();

        insert1.append("INSERT INTO test.tbtest1 VALUES(");
        insert1.append(pk + ",");
        insert1.append("\"" + longtextFld.substring(0,99) + "\",");
        insert1.append("\"" + longtextFld.substring(0,199) + "\",");
        insert1.append(StressTool.getNumberFromRandom(2147483647)* StressTool.getNumberFromRandom(20) + ",");
        insert1.append("\"NULL\",");
        insert1.append("\"" + StressTool.getStringFromRandom(3) + "\"");
        insert1.append(")");

        insert2.append("INSERT INTO test.tbtest2 VALUES(");
        insert2.append(pk + ",");
        insert2.append("\"" + longtextFld + "\",");
        insert2.append("\"NULL\")");

        v.add(0,insert1.toString());
        v.add(1,insert2.toString());
        v.add(3,pk);

        return v;

     }

    public static void main(String[] args) {
    }

    public void setQueriesFileIn(File queriesFileIn) {
        this.queriesFileIn = queriesFileIn;
    }

    public void setJdbcUrl(Map jdbcUrlMapIn) {
    	this.jdbcUrlMap = (Map)jdbcUrlMapIn;
//        try {
//            this.conn= DriverManager.getConnection(jdbcUrl);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
    }
//    public void setJdbcUrl(String jdbcUrl, String dbType) {
//    this.jdbcUrl = jdbcUrl;
//    try {
//        if(!dbType.equals("MySQL"))
//        {
//            this.conn=DriverManager.getConnection(jdbcUrl,"test", "test");
//        }
//        else
//        this.conn= DriverManager.getConnection(jdbcUrl);
//    } catch (SQLException ex) {
//        ex.printStackTrace();
//    }
//}


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

	@Override
	public void setClassConfiguration(Map mConfig) {
		// TODO Auto-generated method stub
		
	}	
}
