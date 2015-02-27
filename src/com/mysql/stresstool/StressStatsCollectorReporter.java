package com.mysql.stresstool;

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
public class StressStatsCollectorReporter {
    private StressStatsCollector statCollector; 
	private Map statusReportsCollection;
    private Map startReport;
    private Map endReport;
    private Map commonInfo;
    
	
	public StressStatsCollectorReporter() {
    }
    
	public StressStatsCollectorReporter(StressStatsCollector stressStatsCollector) {
		if(stressStatsCollector != null){
			this.statCollector = stressStatsCollector;
			if(statCollector.getStatusReportsCollection() != null)
			{
				this.statusReportsCollection = statCollector.getStatusReportsCollection();
			}
		}
		
    }


	/**
	 * @return the commonInfo
	 */
	public Map getCommonInfo() {
		return commonInfo;
	}

	/**
	 * @param commonInfo the commonInfo to set
	 */
	public void setCommonInfo(Map commonInfo) {
		this.commonInfo = commonInfo;
	}

	
}
