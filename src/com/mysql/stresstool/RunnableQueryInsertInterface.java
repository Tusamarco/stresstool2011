package com.mysql.stresstool;

import java.util.Map;


public interface RunnableQueryInsertInterface extends RunnableQueryInterface {

  
	/**
	 * @return the iBatchInsert
	 */
	public abstract int getIBatchInsert();
	/**
	 * @param iBatchInsert the iBatchInsert to set
	 */
	public abstract void setIBatchInsert(int iBatchInsert);
	public abstract boolean isIgnoreBinlog() ;
	public abstract void setIgnoreBinlog(boolean ignoreBinlog) ;
	public abstract boolean createSchema(StressTool  sTool);
	public abstract void setOnDuplicateKey(int onDuplicateKey);
	public abstract int getOnDuplicateKey();

}