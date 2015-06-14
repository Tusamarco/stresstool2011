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
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.Map;

import net.tc.utils.Utility;

import org.apache.commons.beanutils.MethodUtils;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RunnableQuerySelectBasic implements Runnable,
	RunnableQuerySelectInterface {
    private File queriesFileIn;
    // Connection conn = null;

    private Map jdbcUrlMap;

    /**
     * @return the jdbcUrlMap
     */
    public Map getJdbcUrlMap() {
	return jdbcUrlMap;
    }

    /**
     * @param jdbcUrlMap
     *            the jdbcUrlMap to set
     */
    public void setJdbcUrlMap(Map jdbcUrlMap) {
	this.jdbcUrlMap = jdbcUrlMap;
    }

    private boolean repeat = false;
    private int repeatNumber = 0;
    private int ID;
    private boolean doLog = true;
    private int sleepFor;
    private String dbType = "MySQL";
    private String engine;
    private long executionTime;
    private MySQLStats mySQLStatistics;
    private boolean active;
    private boolean doSimplePk = false;
    private int iBatchSelect = 200;
    private String sqlQuery;
    private PrimaryKeyRangeDefiner pkRange;
    private Map classConfig = null;
    private int numberOfprimaryTables = 1;
    private int numberOfSecondaryTables = 1;
    private boolean useAutoIncrement = false;
    private int numberOfJoinTables = 0;
    private int numberOfIntervalKeys = 0;
    private String selectFilterMethod = "range";
    private String joinField = null;
    private boolean forceIndex = false;
    private String indexName = null;
    private int sleepSelect = 0;
    private int selectLimit = 100;
    private int sleepWrite = 0;
    private int yearstart = 2013;
    private int monthstart = 1;
    private int daystart = 1;
    private int daystotal = 1;
    private String partitionType = "range";
    private boolean debug = false;
    private boolean selectForceAutocommitOff = false;
    private boolean stikyconnection = true;

    static final ArrayList<String> CLASS_PARAMETERS = new ArrayList(
	    Arrays.asList("numberOfprimaryTables", "numberOfSecondaryTables",
		    "useAutoIncrement", "numberOfJoinTables",
		    "numberOfIntervalKeys", "selectFilterMethod", "joinField",
		    "forceIndex", "indexName", "sleepSelect", "selectLimit",
		    "yearstart", "monthstart", "daystart", "daystotal",
		    "partitionType", "stikyconnection",
		    "selectForceAutocommitOff", "debug"));

    /**
     * @return the stikyconnection
     */
    public boolean isStikyconnection() {
	return stikyconnection;
    }

    /**
     * @param stikyconnection
     *            the stikyconnection to set
     */
    public void setStikyconnection(boolean stikyconnection) {
	this.stikyconnection = stikyconnection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getSelectLimit()
     */
    @Override
    public int getSelectLimit() {
	return selectLimit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setSelectLimit(int)
     */
    @Override
    public void setSelectLimit(int selectLimit) {
	this.selectLimit = selectLimit;
    }

    public RunnableQuerySelectBasic() {
	try {
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	} catch (Exception ex) {
	    // handle the error
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#run()
     */

    public void run() {

	Connection conn = null;
	try {
	    if (stikyconnection && getJdbcUrlMap().get("dbType") != null && !((String) getJdbcUrlMap().get("dbType")).equals("MySQL")) {
		conn = DriverManager.getConnection((String) getJdbcUrlMap().get("dbType"), "test", "test");
	    } else if (stikyconnection)
		conn = DriverManager.getConnection((String) getJdbcUrlMap().get("jdbcUrl"));

	} catch (SQLException ex) {
	    ex.printStackTrace();
	}

	// if( conn != null)
	// {
	ThreadInfo thInfo;
	thInfo = new ThreadInfo();
	thInfo.setId(this.ID);
	thInfo.setType("select");
	active = true;
	thInfo.setStatusActive(this.isActive());
	StressTool.setInfoSelect(this.ID, thInfo);

	try {
	    
	    Statement stmt = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt;
	    
	    if (isSelectForceAutocommitOff()&& isStikyconnection()) {
		conn.setAutoCommit(false);
		conn.createStatement().execute("SET AUTOCOMMIT=0");
	    }
	    if(conn != null){
        	    stmt = conn.createStatement();
	    }
	    long execTime = 0;
	    int repeat = 0;
	    int sqlParameterNumbers = 0;

	    long threadTimeStart = System.currentTimeMillis();

	    if (this.sqlQuery != null && !this.sqlQuery.equals("")) {
		try {
		    pstmt = conn.prepareStatement(sqlQuery);
		    sqlParameterNumbers = pstmt.getParameterMetaData().getParameterCount();

		} catch (SQLException sqlex) {
		    sqlex.printStackTrace();
		    conn.close();
		    return;
		}
	    } else {
		pstmt = null;
	    }

	    String[][] sqlParameterValues;
	    sqlParameterValues = null;
	    
	    if (this.sqlQuery == null || this.sqlQuery.equals("")) {
		pkRange = new PrimaryKeyRangeDefiner(repeatNumber);
		pkRange.setLastResetLoop(0);

		// 3351000110
		
	    } else if(this.sqlQuery != null ) {
		sqlParameterValues = new String[repeatNumber][sqlParameterNumbers];
		for (repeat = 0; repeat < repeatNumber; repeat++) {
		    for (int pvalue = 0; pvalue < sqlParameterNumbers; pvalue++) {
			sqlParameterValues[repeat][pvalue] = "3" + String.valueOf(StressTool.getNumberFromRandom(500000000).intValue());
		    }
		}

	    }

	    for (repeat = 0; repeat < repeatNumber; repeat++) {
		if (!stikyconnection) {
		    try {
			if (conn != null && !conn.isClosed()) {
			    conn.close();
			}
			SoftReference sf = new SoftReference(DriverManager.getConnection((String) getJdbcUrlMap().get("jdbcUrl")));
			conn = (Connection) sf.get();
			if (conn != null) {
			    stmt = conn.createStatement();

			    if (isSelectForceAutocommitOff()) {
				conn.setAutoCommit(false);
				stmt.execute("SET AUTOCOMMIT=0");
			    }

			}
		    } catch (SQLException ex) {
			for (int icon = 0; icon <= 3; icon++) {
			    try {
				Thread.sleep(10000);
				SoftReference sf = new SoftReference(DriverManager.getConnection((String) getJdbcUrlMap().get("jdbcUrl")));
				conn = (Connection) sf.get();
			    } catch (SQLException ex2) {
				ex2.printStackTrace();
			    }
			}
			// ex.printStackTrace();
		    }

		}

		int pkStart = 0;
		int pkEnds = 0;
		int recordFound = 0;
		
		if(repeat == 0 )
		    pkRange = setPkRange(conn, pkRange);
		
		if (repeat > 0 && pkRange.getLooprefresh() < (repeat - pkRange.getLastResetLoop())) {
		    pkRange = setPkRange(conn, pkRange);
		    pkRange.setLastResetLoop(repeat);
		}

		if (pkRange.getKeyStart(repeat) > 0
			&& pkRange.getKeyEnd(repeat) > 0) {
		    pkStart = pkRange.getKeyStart(repeat);
		    pkEnds = pkRange.getKeyEnd(repeat);
		}

		if (pkStart > pkEnds) {
		    int dummy = pkStart;
		    pkStart = pkEnds;
		    pkEnds = dummy;
		}

		if (pkEnds == 0)
		    continue;

		String select = "";

		select = generateSelectString(pkStart, pkEnds, select);
		int[] iLine = null;

		try {

		    long timeStart = System.currentTimeMillis();

		    try {
			if (this.sqlQuery != null && !this.sqlQuery.equals("")) {

			    if (this.getIBatchSelect() > 0) {
				for (int pstmtbatch = 0; pstmtbatch < this
					.getIBatchSelect(); pstmtbatch++) {
				    for (int ii = 1; ii <= sqlParameterNumbers; ii++) {
					pstmt.setString(ii,sqlParameterValues[StressTool.getNumberFromRandom(repeatNumber).intValue()][ii - 1]);
					rs = pstmt.executeQuery();
					// System.out.print(this.sqlQuery +
					// " BATCH | " + ii +" | " +
					// sqlParameterValues[repeat][ii - 1] +
					// "\n") ;
				    }
				}
			    } else {
				for (int ii = 1; ii <= sqlParameterNumbers; ii++) {
				    pstmt.setString(ii,sqlParameterValues[repeat][ii - 1]);
				    // System.out.print(this.sqlQuery + " | " +
				    // ii +" | " + sqlParameterValues[repeat][ii
				    // - 1] + "\n") ;
				}
				rs = pstmt.executeQuery();

			    }

			} else {
			    rs = stmt.executeQuery(select);
			    rs.last();
			}

		    } catch (Exception ex) {
			ex.printStackTrace();
		    } finally {
			if (rs != null) {
			    recordFound = rs.getRow();
			    rs.close();
			    rs = null;
			}

		    }
		    long timeEnds = System.currentTimeMillis();

		    execTime = (timeEnds - timeStart);
		    thInfo.setExecutedLoops(repeat);

		} catch (SQLException sqle) {

		    sqle.printStackTrace();
		} finally {
		    if (rs != null) {
			rs.close();
			rs = null;
		    }

		    // intDeleteInterval++;
		    if (doLog) {
			// System.out.println("Query Select TH = " +
			// this.getID() + " Id = "+ pkStart +" IdEnd = " +
			// pkEnds +
			// " Record found = "+recordFound+" Exec Time(ms) =" +
			// execTime);
			System.out.println("Query Select TH = " + this.getID()
				+ " Record found = " + recordFound
				+ " Using search type="
				+ this.selectFilterMethod + " Loop N = "
				+ repeat + " Exec Time(ms) = " + execTime
				+ " Running = " + repeat + " of "
				+ repeatNumber + " to go ="
				+ (repeatNumber - repeat));
		    }

		    Thread.sleep(sleepFor);
		}

		if (sleepFor > 0 || this.getSleepSelect() > 0) {
		    if (this.getSleepSelect() > 0) {
			Thread.sleep(getSleepSelect());
		    } else
			Thread.sleep(sleepFor);
		}
	    }
	    // System.out.println("Query Select/Delete TH = " + this.getID() +
	    // " COMPLETED! ");

	    long threadTimeEnd = System.currentTimeMillis();
	    this.executionTime = (threadTimeEnd - threadTimeStart);
	    // this.setExecutionTime(executionTime);

	    active = false;
	    thInfo.setExecutionTime(executionTime);
	    thInfo.setStatusActive(false);
	    StressTool.setInfoSelect(this.ID, thInfo);
	    stmt = null;

	    return;

	} catch (Exception ex) {
	    ex.printStackTrace();
	    try {
		conn.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	// CONN }

    }

    /**
     * @return the selectForceAutocommitOff
     */
    public boolean isSelectForceAutocommitOff() {
	return selectForceAutocommitOff;
    }

    /**
     * @param selectForceAutocommitOff
     *            the selectForceAutocommitOff to set
     */
    public void setSelectForceAutocommitOff(boolean selectForceAutocommitOff) {
	this.selectForceAutocommitOff = selectForceAutocommitOff;
    }

    /**
     * @param pkStart
     * @param pkEnds
     * @param select
     * @return
     */
    String generateSelectString(int pkStart, int pkEnds, String select) {

	int numberOfRangeKey = this.getNumberOfIntervalKeys();
	String joinAttribute = this.getJoinField();

	String primaryTable = "tbtest"
		+ StressTool.getNumberFromRandomMinMax(1,
			getNumberOfprimaryTables());
	int maxJoinTableIndex = StressTool.getNumberFromRandomMinMax(1,
		getNumberOfJoinTables()).intValue();
	String secondaryChildRetieved = "tbtest_child"
		+ StressTool.getNumberFromRandomMinMax(1, maxJoinTableIndex)
			.intValue();

	String indexHint = "";
	if (this.forceIndex && this.getIndexName() != null) {
	    indexHint = " FORCE INDEX(" + this.getIndexName() + ") ";
	}

	/*
	 * Build the select on the base of condition passed If joins =0 then
	 * build a straight select respecting the filterMethod conditions Like
	 * range|in|equality
	 * 
	 * If Join apply then build the query with the given child tables
	 * respecting the same filterMethod conditions
	 */

	if (dbType.equals("MySQL") && !this.isDoSimplePk()
		&& getNumberOfJoinTables() == 0) {
	    select = "Select " + primaryTable + ".a,b,c,counter,"
		    + primaryTable + ".time," + primaryTable + ".partitionid,"
		    + primaryTable + ".strrecordtype";

	    if (this.getSelectFilterMethod().toLowerCase().equals("range")) {
		String[] arDates = getOrderedRangeDate();

		select = select + "from " + primaryTable + indexHint
			+ " where " + primaryTable + ".date BETWEEN "
			+ arDates[0] + " and " + arDates[1] + " limit "
			+ this.getSelectLimit();
	    } else if (this.getSelectFilterMethod().toLowerCase().equals("in")) {

		String sPk = "";
		sPk = calculaltePK(pkStart, pkEnds, sPk);
		select = select + "from " + primaryTable + indexHint
			+ " where " + primaryTable + ".a IN (" + sPk
			+ ") limit " + this.getSelectLimit();
	    } else {
		select = select + "from " + primaryTable + indexHint
			+ " where " + primaryTable + ".a = " + pkStart
			+ " limit " + this.getSelectLimit();
	    }

	} else if (dbType.equals("MySQL") && !this.isDoSimplePk()
		&& getNumberOfJoinTables() > 0) {
	    String childFieldsToShow = "";
	    String childJoinsConditions = " from " + primaryTable + indexHint;

	    for (int idxf = 1; idxf <= maxJoinTableIndex; idxf++) {
		childFieldsToShow = childFieldsToShow + ", tbtest_child" + idxf
			+ ".a, tbtest_child" + idxf + ".time";
		childJoinsConditions = childJoinsConditions
			+ " LEFT OUTER JOIN tbtest_child" + idxf + " ON  "
			+ primaryTable + "." + joinAttribute
			+ " = tbtest_child" + idxf + ".a";
	    }

	    select = "Select " + primaryTable + ".a,b,c,counter,"
		    + primaryTable + ".time," + primaryTable + ".partitionid,"
		    + primaryTable + ".strrecordtype, "
		    + secondaryChildRetieved + ".stroperation"
		    + childFieldsToShow + childJoinsConditions;

	    if (this.getSelectFilterMethod().toLowerCase().equals("range")) {
		String[] arDates = getOrderedRangeDate();

		select = select + " where " + primaryTable + ".date BETWEEN '"
			+ arDates[0] + "' and '" + arDates[1] + "' limit "
			+ this.getSelectLimit();
	    } else if (this.getSelectFilterMethod().toLowerCase().equals("in")) {
		String sPk = "";
		sPk = calculaltePK(pkStart, pkEnds, sPk);
		select = select + " where " + primaryTable + ".a IN (" + sPk
			+ ") limit " + this.getSelectLimit();
	    } else {
		select = select + " where " + primaryTable + ".a = " + pkStart
			+ " limit " + this.getSelectLimit();
	    }

	} else if (this.isDoSimplePk()) {
	    String sPk = "";
	    sPk = calculaltePK(pkStart, pkEnds, sPk);

	    select = "Select " + primaryTable + ".a,b,c,counter,"
		    + primaryTable + ".time,strrecordtype ";
	    select = select + "from " + primaryTable + " where " + primaryTable
		    + ".a in( " + sPk + " ) Limit " + this.getSelectLimit();

	}
	// System.out.println(select);
	return select;
    }

    /**
     * @param conn
     */
    private PrimaryKeyRangeDefiner setPkRange(Connection conn,
	    PrimaryKeyRangeDefiner pkRange) {

	int repeat = pkRange.getLastResetLoop();
	if (pkRange.getLen() == 0) {

	    int[] values = getMaxSelectValue(conn);

	    for (int repeatLoc = repeat; repeatLoc < repeatNumber; repeatLoc++) {
		pkRange.setKeyStart(values[0]);
		pkRange.setKeyEnd(StressTool.getNumberFromRandomMinMax(
			values[0], values[1]).intValue());
	    }
	} else if (repeat > 0 && pkRange.isRefresh()
		&& pkRange.getLooprefresh() < repeat) {

	    pkRange.reset();
	    int[] values = getMaxSelectValue(conn);
	    for (int repeatLoc = repeat; repeatLoc < repeatNumber; repeatLoc++) {
		pkRange.setKeyStart(values[0]);
		pkRange.setKeyEnd(StressTool.getNumberFromRandomMinMax(
			values[0], values[1]).intValue());
	    }
	}
	return pkRange;
    }

    /**
     * @return check for the max value of the table tbtest1 and for the min
     *         value return an array of int where element 0 is MIN element 1 is
     *         MAX
     */
    int[] getMaxSelectValue(Connection conn) {
	// return StressTool.getNumberFromRandom(2147483647).intValue();
	String primaryTable = "tbtest"
		+ StressTool.getNumberFromRandomMinMax(1,
			getNumberOfprimaryTables());

	Statement stmt = null;
	ResultSet rs = null;
	int[] values = new int[2];

	try {
	    stmt = conn.createStatement();
	    rs = stmt.executeQuery("SELECT MIN(a) mi, MAX(a) ma from "
		    + primaryTable + " FORCE INDEX(IDX_a)");
	    while (rs.next()) {
		values[0] = rs.getInt("mi");
		values[1] = rs.getInt("ma");
	    }

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    try {
		rs.close();
		stmt.close();
	    } catch (Throwable th) {
		th.printStackTrace();
	    }
	    e.printStackTrace();
	} finally {
	    try {
		rs.close();
		stmt.close();
	    } catch (Throwable th) {
		th.printStackTrace();
	    }

	}
	return values;

    }

    /**
     * @param pkStart
     * @param pkEnds
     * @param sPk
     * @return Rerurn the interval of values for an IN syntax comma separated
     */
    protected String calculaltePK(int pkStart, int pkEnds, String sPk) {
	int iloop = 0;
	for (int iPk = pkStart; iPk <= pkEnds; iPk++) {
	    sPk = sPk + iPk;
	    if (iloop++ >= this.getIBatchSelect())
		break;

	    if (iPk != pkEnds)
		sPk = sPk + ",";
	}
	return sPk;
    }

    /**
     * @Generate internal connection for launched thread one per thread no
     *           shared
     */

    private Connection createConnection() {
	Connection conn = null;

	try {
	    if (jdbcUrlMap.get("dbType") != null
		    && !((String) jdbcUrlMap.get("dbType")).equals("MySQL")) {
		conn = DriverManager.getConnection(
			(String) jdbcUrlMap.get("dbType"), "test", "test");
	    } else
		conn = DriverManager.getConnection((String) jdbcUrlMap
			.get("jdbcUrl"));
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setQueriesFileIn(java
     * .io.File)
     */
    @Override
    public void setQueriesFileIn(File queriesFileIn) {
	this.queriesFileIn = queriesFileIn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setJdbcUrl(java.util
     * .Map)
     */
    @Override
    public void setJdbcUrl(Map jdbcUrlMapIn) {
	this.jdbcUrlMap = (Map) jdbcUrlMapIn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setRepet(boolean)
     */
    @Override
    public void setRepet(boolean repeat) {
	this.repeat = repeat;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setRepeatNumber(int)
     */
    @Override
    public void setRepeatNumber(int repeatNumber) {
	this.repeatNumber = repeatNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setDoLog(boolean)
     */
    @Override
    public void setDoLog(boolean doLog) {
	this.doLog = doLog;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setSleepFor(int)
     */
    @Override
    public void setSleepFor(int sleepFor) {
	this.sleepFor = sleepFor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setDbType(java.lang
     * .String)
     */
    @Override
    public void setDbType(String dbType) {
	this.dbType = dbType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setEngine(java.lang
     * .String)
     */
    @Override
    public void setEngine(String engine) {
	this.engine = engine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setExecutionTime(long)
     */
    @Override
    public void setExecutionTime(long executionTime) {
	this.executionTime = executionTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setID(int)
     */
    @Override
    public void setID(int ID) {
	this.ID = ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getQueriesFileIn()
     */
    @Override
    public File getQueriesFileIn() {
	return queriesFileIn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getJdbcUrl()
     */
    @Override
    public String getJdbcUrl() {
	return (String) jdbcUrlMap.get("jdbcUrl");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isRepeat()
     */
    @Override
    public boolean isRepeat() {
	return repeat;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getRepeatNumber()
     */
    @Override
    public int getRepeatNumber() {
	return repeatNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isDoLog()
     */
    @Override
    public boolean isDoLog() {
	return doLog;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getSleepFor()
     */
    @Override
    public int getSleepFor() {
	return sleepFor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getDbType()
     */
    @Override
    public String getDbType() {
	return dbType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getEngine()
     */
    @Override
    public String getEngine() {
	return engine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getExecutionTime()
     */
    @Override
    public long getExecutionTime() {
	return executionTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getID()
     */
    @Override
    public int getID() {
	return ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#getMySQLStatistics()
     */
    @Override
    public MySQLStats getMySQLStatistics() {
	return mySQLStatistics;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isActive()
     */
    @Override
    public boolean isActive() {
	return active;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setMySQLStatistics(
     * com.mysql.stresstool.MySQLStats)
     */
    @Override
    public void setMySQLStatistics(MySQLStats mySQLStatistics) {
	this.mySQLStatistics = mySQLStatistics;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
	this.active = active;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setDoSimplePk(boolean)
     */
    @Override
    public void setDoSimplePk(boolean doSimplePk) {
	this.doSimplePk = doSimplePk;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isDoSimplePk()
     */
    @Override
    public boolean isDoSimplePk() {
	return doSimplePk;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setIBatchSelect(int)
     */
    @Override
    public void setIBatchSelect(int iBatchSelect) {
	this.iBatchSelect = iBatchSelect;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getIBatchSelect()
     */
    @Override
    public int getIBatchSelect() {
	return iBatchSelect;
    }

    /**
     * @return the sqlQuery
     */

    private String getSqlQuery() {
	return sqlQuery;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setSqlQuery(java.lang
     * .String)
     */
    @Override
    public void setSqlQuery(String sqlQuery) {
	this.sqlQuery = sqlQuery;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#doSimplePk()
     */

    @Override
    public boolean doSimplePk() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getDoDelete()
     */

    @Override
    public boolean getDoDelete() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isOperationShort()
     */

    @Override
    public boolean isOperationShort() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isUseBatchInsert()
     */

    @Override
    public boolean isUseBatchInsert() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setDoDelete(boolean)
     */

    @Override
    public void setDoDelete(boolean doDelete) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setOperationShort(boolean
     * )
     */

    @Override
    public void setOperationShort(boolean operationShort) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setUseBatchInsert(boolean
     * )
     */

    @Override
    public void setUseBatchInsert(boolean useBatchInsert) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getJoinField()
     */
    @Override
    public String getJoinField() {
	return joinField;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setJoinField(java.lang
     * .String)
     */
    @Override
    public void setJoinField(String joinField) {
	this.joinField = joinField;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#getNumberOfprimaryTables
     * ()
     */
    @Override
    public int getNumberOfprimaryTables() {
	return numberOfprimaryTables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setNumberOfprimaryTables
     * (int)
     */
    @Override
    public void setNumberOfprimaryTables(int numberOfprimaryTables) {
	this.numberOfprimaryTables = numberOfprimaryTables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#getNumberOfSecondaryTables
     * ()
     */
    @Override
    public int getNumberOfSecondaryTables() {
	return numberOfSecondaryTables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setNumberOfSecondaryTables
     * (int)
     */
    @Override
    public void setNumberOfSecondaryTables(int numberOfSecondaryTables) {
	this.numberOfSecondaryTables = numberOfSecondaryTables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#getNumberOfJoinTables()
     */
    @Override
    public int getNumberOfJoinTables() {
	return numberOfJoinTables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setNumberOfJoinTables
     * (int)
     */
    @Override
    public void setNumberOfJoinTables(int numberOfJoinTables) {
	this.numberOfJoinTables = numberOfJoinTables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#getNumberOfIntervalKeys
     * ()
     */
    @Override
    public int getNumberOfIntervalKeys() {
	return numberOfIntervalKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setNumberOfIntervalKeys
     * (int)
     */
    @Override
    public void setNumberOfIntervalKeys(int numberOfIntervalKeys) {
	this.numberOfIntervalKeys = numberOfIntervalKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#getSelectFilterMethod()
     */
    @Override
    public String getSelectFilterMethod() {
	return selectFilterMethod;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setSelectFilterMethod
     * (java.lang.String)
     */
    @Override
    public void setSelectFilterMethod(String selectFilterMethod) {
	this.selectFilterMethod = selectFilterMethod;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isForceIndex()
     */
    @Override
    public boolean isForceIndex() {
	return forceIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setForceIndex(boolean)
     */
    @Override
    public void setForceIndex(boolean forceIndex) {
	this.forceIndex = forceIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getIndexName()
     */
    @Override
    public String getIndexName() {
	return indexName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setIndexName(java.lang
     * .String)
     */
    @Override
    public void setIndexName(String indexName) {
	this.indexName = indexName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#isUseAutoIncrement()
     */
    @Override
    public boolean isUseAutoIncrement() {
	return useAutoIncrement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setUseAutoIncrement
     * (boolean)
     */
    @Override
    public void setUseAutoIncrement(boolean useAutoIncrement) {
	this.useAutoIncrement = useAutoIncrement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getSleepSelect()
     */
    @Override
    public int getSleepSelect() {
	return sleepSelect;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setSleepSelect(int)
     */
    @Override
    public void setSleepSelect(int sleepSelect) {
	this.sleepSelect = sleepSelect;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setClassConfiguration
     * (java.util.Map)
     */

    @Override
    public void setClassConfiguration(Map mConfig) {
	classConfig = mConfig;

	for (int i = 0; i < CLASS_PARAMETERS.size(); i++) {
	    try {
		String methodName = (String) CLASS_PARAMETERS.get(i);
		methodName = methodName.substring(0, 1).toUpperCase()
			+ methodName.substring(1, methodName.length());
		methodName = "set" + methodName;

		String valueM = (String) classConfig.get(CLASS_PARAMETERS
			.get(i));
		// System.out.println(methodName + " = " + valueM);

		if (valueM != null) {
		    if (valueM.equals("true") || valueM.equals("false")) {
			MethodUtils.invokeMethod(this, methodName,
				Boolean.parseBoolean(valueM));
		    } else if (Utils.isNumeric(valueM)) {

			MethodUtils.invokeMethod(this, methodName,
				Integer.parseInt(valueM));
		    } else
			// PropertyUtils.setProperty(this,methodName,valueM);
			// MethodUtils.setCacheMethods(false);
			MethodUtils.invokeMethod(this, methodName, valueM);
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

    protected String getRangeDate() {
	// Fill the date value with some time date

	int year = this.getYearstart();
	int month = this.getMonthstart();
	int day = this.getDaystart();
	int interval = this.getDaystotal();

	return Utility.getDateFromDaysInterval(year, month, day, StressTool
		.getNumberFromRandom(interval).intValue());

    }

    protected String[] getOrderedRangeDate() {
	// Fill the date value with some time date

	int year = this.getYearstart();
	int month = this.getMonthstart();
	int day = this.getDaystart();
	int interval = this.getDaystotal();

	return Utility.getOrderedDateFromDaysInterval(year, month, day,
		interval);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getYearstart()
     */
    @Override
    public int getYearstart() {
	return yearstart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setYearstart(int)
     */
    @Override
    public void setYearstart(int yearstart) {
	this.yearstart = yearstart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getMonthstart()
     */
    @Override
    public int getMonthstart() {
	return monthstart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setMonthstart(int)
     */
    @Override
    public void setMonthstart(int monthstart) {
	this.monthstart = monthstart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getDaystart()
     */
    @Override
    public int getDaystart() {
	return daystart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setDaystart(int)
     */
    @Override
    public void setDaystart(int daystart) {
	this.daystart = daystart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getDaystotal()
     */
    @Override
    public int getDaystotal() {
	return daystotal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setDaystotal(int)
     */
    @Override
    public void setDaystotal(int daystotal) {
	this.daystotal = daystotal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#getPartitionType()
     */
    @Override
    public String getPartitionType() {
	return partitionType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mysql.stresstool.RunnableQuerySelectInterface#setPartitionType(java
     * .lang.String)
     */
    @Override
    public void setPartitionType(String partitionType) {
	this.partitionType = partitionType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#isDebug()
     */
    @Override
    public boolean isDebug() {
	return debug;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mysql.stresstool.RunnableQuerySelectInterface#setDebug(boolean)
     */
    @Override
    public void setDebug(boolean debug) {
	this.debug = debug;
    }
}