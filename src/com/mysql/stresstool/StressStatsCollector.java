package com.mysql.stresstool;

import java.util.Map;

import net.tc.utils.SynchronizedMap;

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
public class StressStatsCollector {
    private Map statusReportsCollection;
    private Map startReport;
    private Map endReport;
	
	public StressStatsCollector() {
		statusReportsCollection = new SynchronizedMap();
    }
	public StressStatsCollector(Map statusReportsCollection) {
		if(statusReportsCollection != null){
			this.statusReportsCollection = statusReportsCollection;
		}
    }

	/**
	 * @return the statusReportsCollection
	 */
	public Map getStatusReportsCollection() {
		return statusReportsCollection;
	}

	/**
	 * @param statusReportsCollection the statusReportsCollection to set
	 */
	public void setStatusReportsCollection(Map statusReportsCollection) {
		this.statusReportsCollection = statusReportsCollection;
	}

	/**
	 * @return the startReport
	 */
	public Map getStartReport() {
		if(statusReportsCollection != null)
			return (Map)statusReportsCollection.get(1);
		return null;
	}

//	/**
//	 * @param startReport the startReport to set
//	 */
//	public void setStartReport(Map startReport) {
//		this.startReport = startReport;
//	}

	/**
	 * @return the endReport
	 */
	public Map getEndReport() {
		if(statusReportsCollection != null)
			return (Map)statusReportsCollection.get(statusReportsCollection.size());
		return null;
	}

//	/**
//	 * @param endReport the endReport to set
//	 */
//	public void setEndReport(Map endReport) {
//		this.endReport = endReport;
//	}
	public void setReport(int currVal, Map statusReport) {
		statusReportsCollection.put(currVal,statusReport);
		
	}

   public int size(){
	   		return statusReportsCollection.size();
	   
   }
   public Object get(Object key){
	   return statusReportsCollection.get(key);
	   
   }
	
}
