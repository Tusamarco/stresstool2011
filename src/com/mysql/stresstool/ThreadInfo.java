package com.mysql.stresstool;

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
public class ThreadInfo {
    private int id;
    private boolean statusActive;
    private long executionTime;
    private int executedLoops;
    
    public int getExecutedLoops() {
		return executedLoops;
	}

	public void setExecutedLoops(int executedLoops) {
		this.executedLoops = executedLoops;
	}

	private String type;
    public ThreadInfo() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatusActive(boolean statusActive) {
        this.statusActive = statusActive;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public boolean isStatusActive() {
        return statusActive;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public String getType() {
        return type;
    }
}
