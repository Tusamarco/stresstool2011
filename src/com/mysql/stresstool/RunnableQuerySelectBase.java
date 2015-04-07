package com.mysql.stresstool;

import java.io.File;
import java.util.Map;

public class RunnableQuerySelectBase implements RunnableQueryInsertInterface {

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setQueriesFileIn(File queriesFileIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJdbcUrl(Map props) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRepet(boolean repeat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRepeatNumber(int repeatNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDoLog(boolean doLog) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSleepFor(int sleepFor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOperationShort(boolean operationShort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDbType(String dbType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEngine(String engine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExecutionTime(long executionTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDoDelete(boolean doDelete) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setID(int ID) {
		// TODO Auto-generated method stub

	}

	@Override
	public File getQueriesFileIn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJdbcUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRepeat() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRepeatNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDoLog() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSleepFor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOperationShort() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDbType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getExecutionTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDoDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUseBatchInsert() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MySQLStats getMySQLStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMySQLStatistics(MySQLStats mySQLStatistics) {
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

	@Override
	public void setDoSimplePk(boolean doSimplePk) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doSimplePk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getIBatchInsert() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIBatchInsert(int iBatchInsert) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isIgnoreBinlog() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIgnoreBinlog(boolean ignoreBinlog) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean createSchema(StressTool sTool) {
		// TODO Auto-generated method stub
		return false;
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
	

}
