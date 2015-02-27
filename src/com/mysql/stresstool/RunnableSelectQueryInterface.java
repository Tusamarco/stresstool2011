package com.mysql.stresstool;

import java.util.Map;

public interface RunnableSelectQueryInterface extends RunnableQueryInterface {

	public void setSqlQuery(String sqlString);
	/**
	 * @return the iBatchInsert
	 */
	public abstract int getIBatchSelect();
	/**
	 * @param iBatchInsert the iBatchInsert to set
	 */
	public abstract void setIBatchSelect(int iBatchInsert);


}
