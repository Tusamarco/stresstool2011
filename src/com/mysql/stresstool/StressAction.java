package com.mysql.stresstool;

import java.io.File;

public interface StressAction {

	/**
	 * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes the object's <code>run</code> method to be called in
	 * that separately executing thread.
	 *
	 * @todo Implement this java.lang.Runnable method
	 */
	public abstract void run();

	public abstract void setQueriesFileIn(File queriesFileIn);

	public abstract void setJdbcUrl(String jdbcUrl);

	public abstract void setJdbcUrl(String jdbcUrl, String dbType);

	public abstract void setRepet(boolean repeat);

	public abstract void setRepeatNumber(int repeatNumber);

	public abstract void setDoLog(boolean doLog);

	public abstract void setSleepFor(int sleepFor);

	public abstract void setDbType(String dbType);

	public abstract void setActive(boolean active);

	public abstract void setUseBatchInsert(boolean useBatchInsert);

	public abstract void setID(int ID);

	public abstract File getQueriesFileIn();

	public abstract boolean isRepeat();

	public abstract int getRepeatNumber();

	public abstract boolean isDoLog();

	public abstract int getSleepFor();

	public abstract boolean isOperationShort();

	public abstract long getExecutionTime();

	public abstract boolean isActive();

	public abstract boolean isUseBatchInsert();

	public abstract int getID();

	public abstract MySQLStats getMySQLStatistics();

	public abstract void setMySQLStatistics(MySQLStats mySQLStatistics);

	/**
	 * @return the doSimplePk
	 */
	public abstract boolean doSimplePk();

	/**
	 * @param iBatchInsert the iBatchInsert to set
	 */
	public abstract void setIBatchInsert(int iBatchInsert);

}