package com.mysql.stresstool;

import java.io.File;
import java.util.Map;

public interface RunnableQuerySelectInterface {

	public abstract int getSelectLimit();

	public abstract void setSelectLimit(int selectLimit);

	/**
	 * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes the object's <code>run</code> method to be called in
	 * that separately executing thread.
	 *
	 * @todo Implement this java.lang.Runnable method
	 */
	public abstract void run();

	public abstract void setQueriesFileIn(File queriesFileIn);

	public abstract void setJdbcUrl(Map jdbcUrlMapIn);

	public abstract void setRepet(boolean repeat);

	public abstract void setRepeatNumber(int repeatNumber);

	public abstract void setDoLog(boolean doLog);

	public abstract void setSleepFor(int sleepFor);

	public abstract void setDbType(String dbType);

	public abstract void setEngine(String engine);

	public abstract void setExecutionTime(long executionTime);

	public abstract void setID(int ID);

	public abstract File getQueriesFileIn();

	public abstract String getJdbcUrl();

	public abstract boolean isRepeat();

	public abstract int getRepeatNumber();

	public abstract boolean isDoLog();

	public abstract int getSleepFor();

	public abstract String getDbType();

	public abstract String getEngine();

	public abstract long getExecutionTime();

	public abstract int getID();

	public abstract MySQLStats getMySQLStatistics();

	public abstract boolean isActive();

	public abstract void setMySQLStatistics(MySQLStats mySQLStatistics);

	public abstract void setActive(boolean active);

	/**
	 * @param doSimplePk the doSimplePk to set
	 */
	public abstract void setDoSimplePk(boolean doSimplePk);

	/**
	 * @return the doSimplePk
	 */
	public abstract boolean isDoSimplePk();

	/**
	 * @param iBatchSelect the iBatchSelect to set
	 */
	public abstract void setIBatchSelect(int iBatchSelect);

	/**
	 * @return the iBatchSelect
	 */
	public abstract int getIBatchSelect();

	/**
	 * @param sqlQuery the sqlQuery to set
	 */
	public abstract void setSqlQuery(String sqlQuery);

	public abstract boolean doSimplePk();

	public abstract boolean getDoDelete();

	public abstract boolean isOperationShort();

	public abstract boolean isUseBatchInsert();

	public abstract void setDoDelete(boolean doDelete);

	public abstract void setOperationShort(boolean operationShort);

	public abstract void setUseBatchInsert(boolean useBatchInsert);

	/**
	 * @return the joinField
	 */
	public abstract String getJoinField();

	/**
	 * @param joinField the joinField to set
	 */
	public abstract void setJoinField(String joinField);

	/**
	 * @return the numberOfprimaryTables
	 */
	public abstract int getNumberOfprimaryTables();

	/**
	 * @param numberOfprimaryTables the numberOfprimaryTables to set
	 */
	public abstract void setNumberOfprimaryTables(int numberOfprimaryTables);

	/**
	 * @return the numberOfSecondaryTables
	 */
	public abstract int getNumberOfSecondaryTables();

	/**
	 * @param numberOfSecondaryTables the numberOfSecondaryTables to set
	 */
	public abstract void setNumberOfSecondaryTables(int numberOfSecondaryTables);

	/**
	 * @return the numberOfJoinTables
	 */
	public abstract int getNumberOfJoinTables();

	/**
	 * @param numberOfJoinTables the numberOfJoinTables to set
	 */
	public abstract void setNumberOfJoinTables(int numberOfJoinTables);

	/**
	 * @return the numberOfIntervalKeys
	 */
	public abstract int getNumberOfIntervalKeys();

	/**
	 * @param numberOfIntervalKeys the numberOfIntervalKeys to set
	 */
	public abstract void setNumberOfIntervalKeys(int numberOfIntervalKeys);

	/**
	 * @return the selectFilterMethod
	 */
	public abstract String getSelectFilterMethod();

	/**
	 * @param selectFilterMethod the selectFilterMethod to set
	 */
	public abstract void setSelectFilterMethod(String selectFilterMethod);

	/**
	 * @return the forceIndex
	 */
	public abstract boolean isForceIndex();

	/**
	 * @param forceIndex the forceIndex to set
	 */
	public abstract void setForceIndex(boolean forceIndex);

	/**
	 * @return the indexName
	 */
	public abstract String getIndexName();

	/**
	 * @param indexName the indexName to set
	 */
	public abstract void setIndexName(String indexName);

	/**
	 * @return the useAutoIncrement
	 */
	public abstract boolean isUseAutoIncrement();

	/**
	 * @param useAutoIncrement the useAutoIncrement to set
	 */
	public abstract void setUseAutoIncrement(boolean useAutoIncrement);

	/**
	 * @return the sleepSelect
	 */
	public abstract int getSleepSelect();

	/**
	 * @param sleepSelect the sleepSelect to set
	 */
	public abstract void setSleepSelect(int sleepSelect);

	public abstract void setClassConfiguration(Map mConfig);

	public abstract int getYearstart();

	public abstract void setYearstart(int yearstart);

	public abstract int getMonthstart();

	public abstract void setMonthstart(int monthstart);

	public abstract int getDaystart();

	public abstract void setDaystart(int daystart);

	public abstract int getDaystotal();

	public abstract void setDaystotal(int daystotal);

	public abstract String getPartitionType();

	public abstract void setPartitionType(String partitionType);

	public abstract boolean isDebug();

	public abstract void setDebug(boolean debug);

}