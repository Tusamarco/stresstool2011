package com.mysql.stresstool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import net.tc.*;
import net.tc.utils.ObjectHandler;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Text;
import net.tc.utils.Utility;
import net.tc.utils.elastic.elasticProvider;
import net.tc.utils.file.FileDataWriter;
import net.tc.utils.file.FileHandler;

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
public class MySQLStats {
    private String testDate;
    private Map statusReportsCollection = new SynchronizedMap(0);
    private StressStatsCollector stressStatsCollector =  new StressStatsCollector();
//    private Map statusReport = new SynchronizedMap(0);
    private Map systemVariables = new SynchronizedMap(0);
    Connection conn = null;
//    private String jdbcUrl;
    private Map startReport;
    private Map endReport;
    private long totalQueryToRun;
    private int numberOfThreads;
    private int numberOfLoops;
    private boolean doBatching;
    private long totalExecutionTime;
    private long totalQueryToRunWrites;
    private long totalQueryToRunReads;
    private long totalQueryToRunDeletes;
    private boolean summaryPrinted;
    private String engineName = "InnoDB";
    private ArrayList ndbinfoNames = new ArrayList();
    private boolean insertStatHeaders = false;
    private boolean noHistory = true;
    private net.tc.utils.file.FileHandler logStatistics;
    private net.tc.utils.file.FileHandler logReport;
    private net.tc.utils.file.FileHandler logPathStatReport;
    private Client clientElastic;
    
	private int test;

    public MySQLStats(String jdbcUrl) {
        try {
            this.conn = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        Map systemVariables = new HashMap(0);
        
        Statement stmt = null;
        ResultSet rs = null;
        if(conn == null)
            return;
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SHOW GLOBAL VARIABLES");
            while(rs.next())
            {
                String name = "";
                String value = "";
                name = rs.getString("Variable_name");
                value = rs.getString("Value");
                systemVariables.put(name,value);
            }
            this.setSystemVariables(systemVariables);
        }
        catch (Exception eex)
        {
            eex.printStackTrace();
        }
        finally
        {
            try {
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        
        
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

//    public void setStatusReport(Map statusReport) {
//        this.statusReport = statusReport;
//    }

    public void setSystemVariables(Map systemVariables) {
        this.systemVariables = systemVariables;
    }

    public void setTotalQueryToRun() {
        this.totalQueryToRun = this.getTotalQueryToRunReads() + this.getTotalQueryToRunWrites() + this.getTotalQueryToRunDeletes();
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void setNumberOfLoops(int numberOfLoops) {
        this.numberOfLoops = numberOfLoops;
    }

    public void setDoBatching(boolean doBatching) {
        this.doBatching = doBatching;
    }

    public void setTotalExecutionTime(long totalExecutionTime) {
        this.totalExecutionTime = totalExecutionTime;
    }

    public void setsummaryPrinted(boolean summaryPrinted) {
        this.summaryPrinted = summaryPrinted;
    }

    public void setTotalQueryToRunWrites(long totalQueryToRunWrites, float pctInsert) {
        long maxInsert = totalQueryToRunWrites;
        double perCent = pctInsert / 100;
        maxInsert= new Double(maxInsert * perCent).longValue();

        this.totalQueryToRunWrites = maxInsert;
    }

    public void setTotalQueryToRunReads(long totalQueryToRunReads, float pctSelect) {

        long maxSelect = totalQueryToRunReads;
        double perCent = pctSelect / 100;
        maxSelect= new Double(maxSelect * perCent).longValue();

        this.totalQueryToRunReads = maxSelect;
    }

    public String getTestDate() {
        return testDate;
    }

//    public Map getStatusReport() {
//        return statusReport;
//    }

    public Map getSystemVariables() {
        return systemVariables;
    }

    public long getTotalQueryToRun() {
        return totalQueryToRun;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public int getNumberOfLoops() {
        return numberOfLoops;
    }

    public boolean isDoBatching() {
        return doBatching;
    }

    public long getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public long getTotalQueryToRunWrites() {
        return totalQueryToRunWrites;
    }

    public long getTotalQueryToRunReads() {
        return totalQueryToRunReads;
    }

    public void setTotalQueryToRunDeletes(long totalQueryToRunDeletes, float pctDelete) {
        long maxDelete = totalQueryToRunDeletes;
        double perCent = pctDelete / 100;
        maxDelete= new Double(maxDelete * perCent).longValue();

        this.totalQueryToRunDeletes = maxDelete;
	}

	public long getTotalQueryToRunDeletes() {
		return totalQueryToRunDeletes;
	}

	public boolean issummaryPrinted() {
        return summaryPrinted;
    }

    /**
     * getStatus()
     */
    public void getStatus() {
        Map statusReport = new SynchronizedMap(0);
//        Map statusReport = new HashMap(0);
        
        Statement stmt = null;
        ResultSet rs = null;
        if(conn == null)
            return;
        try{
            int currVal = stressStatsCollector.getStatusReportsCollection().size() + 1;
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SHOW GLOBAL STATUS");

            String time = Utility.getTimestampForStats();
            statusReport.put("AA_TIME",Utility.getTimestampForElastic());
            statusReport.put("TIMEEX",time);
            statusReport.put("TIME",new Long(System.currentTimeMillis()));
            statusReport.put("SERVERID", this.getSystemVariableByName("server_id", true));
            statusReport.put("NumberOfThreads", this.getNumberOfThreads());
            /**
             * @todo 
             * I will move this to an object for statistics that will provide the way information should be access and retrieve
             * This is crap I know I will fix soon.
             */
            
            
            while(rs.next())
            {
                String name = "";
                Object value;
                name = rs.getString("Variable_name");
                value = ObjectHandler.convertString2Object((String)rs.getObject("Value"));
                statusReport.put(name,value);
            }

            
            if(this.engineName.toLowerCase().equals("ndbcluster")){
            	
            	rs = stmt.executeQuery("SELECT * from ndbinfo.counters");
                while(rs.next())
                {
                    String name = "";
                    Object value;
                    String id = "" ;
                    String block="";
                    String instance="";
                    name = rs.getString("counter_name");
                    value = rs.getObject("val");
                    id = rs.getString("node_id"); 
                    block=rs.getString("block_name");
                    instance=rs.getString("block_instance");
                    name = name + "_" + block + "_" + id + "_" + instance;
                    if(statusReportsCollection.size() == 0){    
                    	ndbinfoNames.add(name);
                    }
                    statusReport.put(name,value);
                }
            	
            }
            if(this.logStatistics != null){
            	if(this.isInsertStatHeaders()){
            		((FileDataWriter)this.logStatistics).printHeaders((net.tc.utils.SynchronizedMap) statusReport);
            		this.setInsertStatHeaders(false);
            	}
            		
            	((FileDataWriter)this.logStatistics).setRecordData((net.tc.utils.SynchronizedMap) statusReport);
            	/*
            	 * If Elastic search is active then try to push info there
            	 */
            	try{
            		if(this.getClientElastic() != null){
            			IndexRequestBuilder requestElastic = getClientElastic().prepareIndex("stresstool", "status", ((Long)statusReport.get("TIME")).toString());
            			String headElastic ="";
            			//headElastic ="\"mappings\":{\"status\":{\"properties\":{\"AA_time\":\"type\":\"date\",\"format\":\"basic_date_time\"}}}";
            			
            			requestElastic = elasticProvider.fillClientFromMap(requestElastic, statusReport);
            			IndexResponse response = requestElastic
            									.execute()
            									.actionGet();
            		}
            	  
            	}
            	catch(Throwable th){
            		th.printStackTrace();
            	}
            	
            }
            
            if(currVal > 1 && noHistory)
            	stressStatsCollector.setReport(2,statusReport);
            else
            	stressStatsCollector.setReport(currVal,statusReport);
//            statusReportsCollection.put(currVal,statusReport);
        }
        catch (Exception eex)
        {
            eex.printStackTrace();
        }
        finally
        {
            try {
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * printFinalSummary
     *
     * @return String
     */
    
    /**TODO all the information about what should be monitored needs to go in a separate provider
     * the provider will provide the metrics by generic, connection, buffers, storage engine
     * expandable by storage engine
     * or by extend 
    */
    public String printFinalSummary(String toAdd) {
    	
        this.startReport = (Map) stressStatsCollector.getStartReport();
        this.endReport = (Map) stressStatsCollector.getEndReport();

        getTotalEcecutionTime();
        Map common = new HashMap();
        common.put("Runned on",TimeTools.GetFullDate(((Long)startReport.get("TIME")).longValue()));
        common.put("Execution time",this.totalExecutionTime/1000);
        common.put("query Executed",this.getTotalQueryToRun());
        common.put("query writes,", this.getTotalQueryToRunWrites());
        common.put("query reads", this.getTotalQueryToRunReads());
        common.put("query deletes",this.getTotalQueryToRunDeletes());
        
        StressStatsCollectorReporter StatReporter = new StressStatsCollectorReporter(stressStatsCollector);
        StatReporter.setCommonInfo(common);
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println();
        pw.println();
        pw.println("****============================================================================*******");
        pw.println("****                        FINAL REPORT    *** GENERAL ***                     *******");
        pw.println("****============================================================================*******");
        pw.println("-------------------------------- GENERAL TEST INFORMATION ------------------------------------------");
        if(toAdd != null && !toAdd.equals(""))
            pw.println(toAdd);
        pw.println("Runned on = " + TimeTools.GetFullDate(((Long)startReport.get("TIME")).longValue()));
        pw.println("------------------------------GENERAL DATABASE INFORMATION -----------------------------------------");
        pw.println("Total Number Of query Executed = " + this.getTotalQueryToRun());
        pw.println("Total Number Of query Executed for writes = " + this.getTotalQueryToRunWrites());
        pw.println("Total Number Of query Executed for reads  = " + this.getTotalQueryToRunReads());
        pw.println("Total Number Of query Executed for deletes  = " + this.getTotalQueryToRunDeletes());
        
        pw.println("Total Kbyte IN  = " + this.getResultByName("Bytes_sent",false) + " xsec = " + (((Long)this.getResultByName("Bytes_sent",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Total Kbyte OUT  = " + this.getResultByName("Bytes_received",false) + " xsec = " + (((Long)this.getResultByName("Bytes_received",false)).longValue()/(this.totalExecutionTime/1000)));
        
        pw.println("Total Execution time = " + this.totalExecutionTime/1000);
        pw.println("Inserts = " + this.getResultByName("Com_insert",false) + " xsec = " + (((Long)this.getResultByName("Com_insert",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Replace = " + this.getResultByName("Com_replace",false) + " xsec = " + (((Long)this.getResultByName("Com_replace",false)).longValue()/(this.totalExecutionTime/1000)));        
        pw.println("Updates = " + this.getResultByName("Com_update",false) + " xsec = " + (((Long)this.getResultByName("Com_update",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Deletes = " + this.getResultByName("Com_delete",false) + " xsec = " + (((Long)this.getResultByName("Com_delete",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Selects = " + this.getResultByName("Com_select",false) + " xsec = " + (((Long)this.getResultByName("Com_select",false)).longValue()/(this.totalExecutionTime/1000)));

        pw.println("Begins = " + this.getResultByName("Com_begin",false) + " xsec = " + (((Long)this.getResultByName("Com_begin",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Commits = " + this.getResultByName("Com_commit",false) + " xsec = " + (((Long)this.getResultByName("Com_commit",false)).longValue()/(this.totalExecutionTime/1000)));        
        
        
        pw.println("Number of Connections = " + this.getResultByName("Connections",false) + " xsec = " + (((Long)this.getResultByName("Connections",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Temporary table on disk = " + this.getResultByName("Created_tmp_disk_tables",false));
        pw.println("Number of Questions = " + this.getResultByName("Questions",false) + " xsec = " + (((Long)this.getResultByName("Questions",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Max_used_connections = " + this.getResultByName("Max_used_connections",false) + " xsec = " + (((Long)this.getResultByName("Max_used_connections",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Opened_tables = " + this.getResultByName("Opened_tables",false) + " xsec = " + (((Long)this.getResultByName("Opened_tables",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("------------------------------------ DELAYS ------------------------------------------------");
        pw.println("Delayed insert threads   = " + this.getResultByName("Delayed_insert_threads",false) + " xsec = " + (((Long)this.getResultByName("Delayed_insert_threads",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Delayed Errors  = " + this.getResultByName("Delayed_errors",false) + " xsec = " + (((Long)this.getResultByName("Delayed_errors",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Delayed writes  = " + this.getResultByName("Delayed_writes",false) + " xsec = " + (((Long)this.getResultByName("Delayed_writes",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Not flushed delayed rows = " + this.getResultByName("Not_flushed_delayed_rows",false) + " xsec = " + (((Long)this.getResultByName("Not_flushed_delayed_rows",false)).longValue()/(this.totalExecutionTime/1000)));
        
        pw.println("------------------------------------ LOCKS ------------------------------------------------");
        pw.println("Number of Table_locks_immediate = " + this.getResultByName("Table_locks_immediate",false) + " xsec = " + (((Long)this.getResultByName("Table_locks_immediate",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Table_locks_waited = " + this.getResultByName("Table_locks_waited",false) + " xsec = " + (((Long)this.getResultByName("Table_locks_waited",false)).longValue()/(this.totalExecutionTime/1000)));
        
        if(this.engineName.toLowerCase().equals("innodb")){
		        pw.println("-----------------------------INNODB BUFFER POOL ACTIONS -----------------------------------");
		        pw.println("Number of Innodb_buffer_pool_pages_flushed = " + this.getResultByName("Innodb_buffer_pool_pages_flushed",false) + " xsec = " + (((Long)this.getResultByName("Innodb_buffer_pool_pages_flushed",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_buffer_pool_read_ahead_rnd = " + this.getResultByName("Innodb_buffer_pool_read_ahead_rnd",false) + " xsec = " + (((Long)this.getResultByName("Innodb_buffer_pool_read_ahead_rnd",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_buffer_pool_read_ahead_seq = " + this.getResultByName("Innodb_buffer_pool_read_ahead_seq",false) + " xsec = " + (((Long)this.getResultByName("Innodb_buffer_pool_read_ahead_seq",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_buffer_pool_read_requests = " + this.getResultByName("Innodb_buffer_pool_read_requests",false) + " xsec = " + (((Long)this.getResultByName("Innodb_buffer_pool_read_requests",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_buffer_pool_reads = " + this.getResultByName("Innodb_buffer_pool_reads",false) + " xsec = " + (((Long)this.getResultByName("Innodb_buffer_pool_reads",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_data_fsyncs = " + this.getResultByName("Innodb_data_fsyncs",false) + " xsec = " + (((Long)this.getResultByName("Innodb_data_fsyncs",false)).longValue()/(this.totalExecutionTime/1000)));
		        
		        pw.println("-----------------------------INNODB BUFFER POOL Dirty Page -----------------------------------");
		        pw.println("Number of Innodb_buffer_pool_pages_dirty = " + this.getResultByName("Innodb_buffer_pool_pages_dirty",false) + " xsec = " + (((Long)this.getResultByName("Innodb_buffer_pool_pages_dirty",false)).longValue()/(this.totalExecutionTime/1000)));
		
		//        if((Long)this.getResultByName("Innodb_buffer_pool_pages_dirty",false) == 0){
		//        	pw.println("Number of Innodb_buffer_pool_pages_dirty % = 0");
		//        }
		//        else{
		//        	pw.println("Number of Innodb_buffer_pool_pages_dirty % = " + (((Long)this.getResultByName("Innodb_buffer_pool_pages_dirty",false)/((Long)this.getResultByName("Innodb_buffer_pool_pages_total",false))))*100);
		//        }
		
		        pw.println("--------------------------------- INNODB DATA READS ----------------------------------------");
		        pw.println("Number of Innodb Reads from BPool (not from disk) = " + (((Long)this.getResultByName("Innodb_buffer_pool_read_requests",false)) - (Long)this.getResultByName("Innodb_buffer_pool_reads",false)) + " xsec = " 
		        		+ (((Long)(((Long)this.getResultByName("Innodb_buffer_pool_read_requests",false)) - (Long)this.getResultByName("Innodb_buffer_pool_reads",false))/(this.totalExecutionTime/1000))));		        
		        
		        pw.println("Number of Innodb_data_reads = " + this.getResultByName("Innodb_data_reads",false) + " xsec = " + (((Long)this.getResultByName("Innodb_data_reads",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_rows_read = " + this.getResultByName("Innodb_rows_read",false) + " xsec = " + (((Long)this.getResultByName("Innodb_rows_read",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_pages_read = " + this.getResultByName("Innodb_pages_read",false) + " xsec = " + (((Long)this.getResultByName("Innodb_pages_read",false)).longValue()/(this.totalExecutionTime/1000)));
		
		        pw.println("----------------------------------INNODB DATA WRITES ---------------------------------------");
		        pw.println("Number of Innodb_data_writes = " + this.getResultByName("Innodb_data_writes",false) + " xsec = " + (((Long)this.getResultByName("Innodb_data_writes",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_rows_inserted = " + this.getResultByName("Innodb_rows_inserted",false) + " xsec = " + (((Long)this.getResultByName("Innodb_rows_inserted",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_rows_updated = " + this.getResultByName("Innodb_rows_updated",false) + " xsec = " + (((Long)this.getResultByName("Innodb_rows_updated",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_rows_deleted = " + this.getResultByName("Innodb_rows_deleted",false) + " xsec = " + (((Long)this.getResultByName("Innodb_rows_deleted",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_pages_written = " + this.getResultByName("Innodb_pages_written",false) + " xsec = " + (((Long)this.getResultByName("Innodb_pages_written",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_pages_created = " + this.getResultByName("Innodb_pages_created",false) + " xsec = " + (((Long)this.getResultByName("Innodb_pages_created",false)).longValue()/(this.totalExecutionTime/1000)));
		
		        pw.println("-----------------------------------INNODB DBLBUFFER WRITES ---------------------------------");
		        pw.println("Number of Innodb_dblwr_writes = " + this.getResultByName("Innodb_dblwr_writes",false) + " xsec = " + (((Long)this.getResultByName("Innodb_dblwr_writes",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_dblwr_pages_written = " + this.getResultByName("Innodb_dblwr_pages_written",false) + " xsec = " + (((Long)this.getResultByName("Innodb_dblwr_pages_written",false)).longValue()/(this.totalExecutionTime/1000)));
		        
		        pw.println("-----------------------------------INNODB LOG Activities ---------------------------------");
		        
		        pw.println("Number of Innodb_log_waits = " + this.getResultByName("Innodb_log_waits",false) + " xsec = " + (((Long)this.getResultByName("Innodb_log_waits",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_log_write_requests = " + this.getResultByName("Innodb_log_write_requests",false) + " xsec = " + (((Long)this.getResultByName("Innodb_log_write_requests",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_log_writes = " + this.getResultByName("Innodb_log_writes",false) + " xsec = " + (((Long)this.getResultByName("Innodb_log_writes",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_os_log_fsyncs = " + this.getResultByName("Innodb_os_log_fsyncs",false) + " xsec = " + (((Long)this.getResultByName("Innodb_os_log_fsyncs",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_os_log_pending_fsyncs = " + this.getResultByName("Innodb_os_log_pending_fsyncs",false) + " xsec = " + (((Long)this.getResultByName("Innodb_os_log_pending_fsyncs",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_os_log_pending_writes = " + this.getResultByName("Innodb_os_log_pending_writes",false) + " xsec = " + (((Long)this.getResultByName("Innodb_os_log_pending_writes",false)).longValue()/(this.totalExecutionTime/1000)));
		        pw.println("Number of Innodb_os_log_written = " + this.getResultByName("Innodb_os_log_written",false) + " xsec = " + (((Long)this.getResultByName("Innodb_os_log_written",false)).longValue()/(this.totalExecutionTime/1000)));
        }
     
//        Including handlers
        pw.println("----------------------------------- Handlers  ---------------------------------------");         
        
        String[] handlers = new String[]{"Handler_commit", "Handler_delete","Handler_discover","Handler_prepare","Handler_read_first",
                                          "Handler_read_key","Handler_read_last","Handler_read_next","Handler_read_prev","Handler_read_rnd",
                                          "Handler_read_rnd_next","Handler_rollback","Handler_savepoint","Handler_savepoint_rollback",
                                          "Handler_update","Handler_write"};
        for(int ihandler = 0; ihandler < handlers.length; ihandler++){
        pw.println("Number of " + handlers[ihandler] + " = " + this.getResultByName(handlers[ihandler],false) + " xsec = " + (((Long)this.getResultByName(handlers[ihandler],false)).longValue()/(this.totalExecutionTime/1000)));
        	
        	
        }
        
//      Including cluster
      pw.println("----------------------------------- ndbinfo  ---------------------------------------");         
      
      Object[] ndbinfo = (Object[])this.ndbinfoNames.toArray();
      for(int indbinfo = 0; indbinfo < ndbinfo.length; indbinfo++){
      pw.println("Number of " + ndbinfo[indbinfo] + " = " + this.getResultByName((String)ndbinfo[indbinfo],false) + " xsec = " + (((Long)this.getResultByName((String)ndbinfo[indbinfo],false)).longValue()/(this.totalExecutionTime/1000)));
      	
      	
      }

        
        pw.println("-----------------------------------MyIsam Key Buffer ---------------------------------------");
        pw.println("Number of Key_reads = " + this.getResultByName("Key_reads",false) + " xsec = " + (((Long)this.getResultByName("Key_reads",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Key_writes = " + this.getResultByName("Key_writes",false) + " xsec = " + (((Long)this.getResultByName("Key_writes",false)).longValue()/(this.totalExecutionTime/1000)));
        
        pw.println("Number of Key_read_requests = " + this.getResultByName("Key_read_requests",false) + " xsec = " + (((Long)this.getResultByName("Key_read_requests",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Key_write_requests = " + this.getResultByName("Key_write_requests",false) + " xsec = " + (((Long)this.getResultByName("Key_write_requests",false)).longValue()/(this.totalExecutionTime/1000)));

        pw.println("Number of Key_blocks_not_flushed = " + this.getResultByName("Key_blocks_not_flushed",false) + " xsec = " + (((Long)this.getResultByName("Key_blocks_not_flushed",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Key_blocks_unused = " + this.getResultByName("Key_blocks_unused",false) + " xsec = " + (((Long)this.getResultByName("Key_blocks_unused",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Key_blocks_used = " + this.getResultByName("Key_blocks_used",false) + " xsec = " + (((Long)this.getResultByName("Key_blocks_used",false)).longValue()/(this.totalExecutionTime/1000)));

        
        pw.println("----------------------------------- QUERY CACHE --------------------------------------------");
        pw.println("Number of Qcache_hits = " + this.getResultByName("Qcache_hits",false) + " xsec = " + (((Long)this.getResultByName("Qcache_hits",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Qcache_inserts = " + this.getResultByName("Qcache_inserts",false) + " xsec = " + (((Long)this.getResultByName("Qcache_inserts",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Qcache_not_cached = " + this.getResultByName("Qcache_not_cached",false) + " xsec = " + (((Long)this.getResultByName("Qcache_not_cached",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Qcache_lowmem_prunes = " + this.getResultByName("Qcache_lowmem_prunes",false) + " xsec = " + (((Long)this.getResultByName("Qcache_lowmem_prunes",false)).longValue()/(this.totalExecutionTime/1000)));

        pw.println("Number of Qcache_free_memory = " + this.getResultByName("Qcache_free_memory",false) + " xsec = " + (((Long)this.getResultByName("Qcache_free_memory",false)).longValue()/(this.totalExecutionTime/1000)));        
        pw.println("Number of Qcache_queries_in_cache = " + this.getResultByName("Qcache_queries_in_cache",false) + " xsec = " + (((Long)this.getResultByName("Qcache_queries_in_cache",false)).longValue()/(this.totalExecutionTime/1000)));
        pw.println("Number of Qcache_total_blocks = " + this.getResultByName("Qcache_total_blocks",false) + " xsec = " + (((Long)this.getResultByName("Qcache_total_blocks",false)).longValue()/(this.totalExecutionTime/1000)));
        
        pw.println("------------------------------------ THREADS    --------------------------------------------");
        pw.println("Number of Threads_cached = " + this.getResultByName("Threads_cached",false) );
        pw.println("Number of Threads_connected = " + this.getResultByName("Threads_connected",false) );
        pw.println("Number of Threads_created = " + this.getResultByName("Threads_created",false) );
        pw.println("Number of Threads_running = " + this.getMaxResultByName("Threads_running",false) );


        
        if(this.logReport != null){
        	this.logReport.appendToFile(sw.toString());
        	this.printFinalSummaryReport(toAdd);
        }
        
        
//        if(this.logStatistics.getHeaders() == null){
//        	this.logStatistics.setHeader((Set)headers.keySet());
//        	
//        	
//        }

        this.setsummaryPrinted(true);
        
        return sw.toString();



    }


    
    public void printFinalSummaryReport(String toAdd) {
      	
    	SynchronizedMap report = new SynchronizedMap(0); 
    	    	
//    	this.startReport = (Map) stressStatsCollector.getStartReport();
//        this.endReport = (Map) stressStatsCollector.getEndReport();

        report.put("Runned_on" , (TimeTools.GetFullDate(((Long)startReport.get("TIME")).longValue())).replaceAll("\n", ""));
        report.put("Execution_time", getTotalEcecutionTime());
        report.put("Loops", this.numberOfLoops);
        report.put("Threads", this.numberOfThreads);
        
        report.put("query Executed",this.getTotalQueryToRun());
        report.put("query writes", this.getTotalQueryToRunWrites());
        report.put("query reads", this.getTotalQueryToRunReads());
        report.put("query deletes",this.getTotalQueryToRunDeletes());
        
        String[] handlers = new String[]{"Bytes_sent","Bytes_received","Com_insert","Com_replace","Com_update","Com_delete","Com_select","Com_begin","Com_commit","Connections","Questions","Max_used_connections","Opened_tables","Delayed_insert_threads",
        "Delayed_errors","Delayed_writes","Not_flushed_delayed_rows","Table_locks_immediate","Table_locks_waited","Innodb_buffer_pool_pages_flushed","Innodb_buffer_pool_read_ahead_rnd",
        "Innodb_buffer_pool_read_ahead_seq","Innodb_buffer_pool_read_requests","Innodb_buffer_pool_reads","Innodb_data_fsyncs","Innodb_buffer_pool_pages_dirty","Innodb_buffer_pool_read_requests",
        "Innodb_buffer_pool_reads","Innodb_data_reads","Innodb_rows_read","Innodb_pages_read","Innodb_data_writes","Innodb_rows_inserted","Innodb_rows_updated","Innodb_rows_deleted","Innodb_pages_written",
        "Innodb_pages_created","Innodb_dblwr_writes","Innodb_dblwr_pages_written","Innodb_log_waits","Innodb_log_write_requests","Innodb_log_writes","Innodb_os_log_fsyncs","Innodb_os_log_pending_fsyncs",
        "Innodb_os_log_pending_writes","Innodb_os_log_written","Handler_commit", "Handler_delete","Handler_discover","Handler_prepare","Handler_read_first",
        "Handler_read_key","Handler_read_last","Handler_read_next","Handler_read_prev","Handler_read_rnd",
        "Handler_read_rnd_next","Handler_rollback","Handler_savepoint","Handler_savepoint_rollback",
        "Handler_update","Handler_write",
        "Key_reads","Key_writes","Key_read_requests","Key_write_requests","Key_blocks_not_flushed","Key_blocks_unused","Key_blocks_used","Qcache_hits","Qcache_inserts","Qcache_not_cached",
        "Qcache_lowmem_prunes","Qcache_free_memory","Qcache_queries_in_cache","Qcache_total_blocks"};

        for(int ihandler = 0; ihandler < handlers.length; ihandler++){
        	report.put(handlers[ihandler], this.getResultByName(handlers[ihandler],false));
        	report.put(handlers[ihandler]+"_xsec",(((Long)this.getResultByName(handlers[ihandler],false)).longValue()/(this.totalExecutionTime/1000)));
            }

        Object[] ndbinfo = (Object[])this.ndbinfoNames.toArray();

        report.put("Threads_cached" , this.getResultByName("Threads_cached",false) );
        report.put("Threads_connected", this.getResultByName("Threads_connected",false) );
        report.put("Threads_created", this.getResultByName("Threads_created",false) );
        report.put("Threads_running" ,this.getMaxResultByName("Threads_running",false) );

        
        

        
        if(this.logPathStatReport != null){
        	if (!logPathStatReport.isHasHeaders()){
        		this.logPathStatReport.printHeaders(report);
        		logPathStatReport.setHasHeaders(true);
        	}
        	
        	this.logPathStatReport.setRecordData(report);
        }

    }




    /**
     * getMaxResultByName
     *
     * @param string String
     * @param b boolean
     * @return String
     */
    private long getMaxResultByName(String varName, boolean isString)
    {
        long maxValue = 0;
        if(this.stressStatsCollector != null && this.stressStatsCollector.size() > 1)
        {
            for(int i = 1 ; i < this.stressStatsCollector.size() ; i++)
            {
//               long curVal = Long.parseLong((String)((Map)stressStatsCollector.get(i)).get(varName));
               long curVal = new Long((Long) ((Map)stressStatsCollector.get(i)).get(varName)).longValue();
               if(curVal > maxValue)
                   maxValue = curVal;
            }
        }

        return maxValue;
    }

    private Object getResultByName(String varName,boolean isString)
    {
        if(!isString)
        {
        	Long endValue = null;
        	Long startValue = null;
        	
            if (stressStatsCollector != null &&
            		stressStatsCollector.size() > 0) {
            	try{
            		if(Text.isNumeric(this.endReport.get(varName)))
            			endValue = (Long)this.endReport.get(varName);

            		if(Text.isNumeric(this.startReport.get(varName)))
            			startValue = (Long)this.startReport.get(varName);
            	}
            	catch(NullPointerException e){
            		return "WARNING - NULL VALUE";
            	}
            	finally{
            			try{
            				return (Long)(endValue - startValue);
            			}
            			catch(NullPointerException en){
//            				System.out.println("WARNING -- RETURNING NULL VALUE FOR STATUS KEY NAME =" + varName);
            				return new Long(0);
            			}
            	}
            }
        }
        else
        {
            return "Not yet implemented";
        }
        return "";
    }

    private Object getSystemVariableByName(String varName,boolean isString)
    {
        if(!isString)
        {

        	Long returnValue  = null;

        	try{
	            if (systemVariables != null && systemVariables.size() > 0) {
	            
	            	returnValue = (Long)systemVariables.get(varName);
	            }
        	}
            	catch(NullPointerException e){
    				System.out.println("WARNING -- RETURNING NULL VALUE FOR STATUS KEY NAME =" + varName);
            }
            
        }
        else
        {
            return systemVariables.get(varName);
        }
        return "";
    }

    
    /**
     * getTotalEcecutionTime
     *
     * @return String
     */
    private String getTotalEcecutionTime() {
        if(stressStatsCollector != null && stressStatsCollector.size() > 0)
        {
            long start = 0;
            long end = 0;
            start = ((Long)startReport.get("TIME")).longValue();
            end = ((Long)endReport.get("TIME")).longValue();
            this.totalExecutionTime = end - start;
            if(this.totalExecutionTime <= 0)
                this.totalExecutionTime = 1001;
            return (Long.toString(totalExecutionTime));

        }

        return "";
    }

    public String getEngineName() {
  		return engineName;
  	}

  	public void setEngineName(String engineName) {
  		this.engineName = engineName;
  	}
  	
  	public void openLogStats(String path,boolean append){
  		String pathOut = null;
  		String filenameOut = null;
  		
  		if(path !=null && !path.equals("")){
  			try {
  				String separator = "/";
  				File ftemp = new File(path);
  				String aT = ftemp.getAbsolutePath().substring(0,ftemp.getAbsolutePath().
                        lastIndexOf(separator)+1);

  				pathOut = aT;
				filenameOut = ftemp.getName();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
  			
  			String timestamp = Utility.getTimestamp();
  			try{
  			if(append){
  				this.logStatistics = new FileHandler(null,filenameOut,pathOut,false);
  			}
  			else
  				this.logStatistics = new FileHandler(null, filenameOut + "_"+ timestamp +".csv",pathOut, true);
  			}
  			catch (Exception ex)
  			{ ex.printStackTrace();}
  		}
  	}

  	public void openLogReport(String path,boolean append){
  		String pathOut = null;
  		String filenameOut = null;

  		if(path !=null && !path.equals("")){
  			try {
  				String separator = "/";
  				File ftemp = new File(path);
  				String aT = ftemp.getAbsolutePath().substring(0,ftemp.getAbsolutePath().
                        lastIndexOf(separator)+1);

  				pathOut = aT;
  				filenameOut = ftemp.getName();

  			} catch (Exception e) {
  				e.printStackTrace();
  			}


  			String timestamp = Utility.getTimestamp();
  			try{
  				if(append){
  					this.logReport = new FileHandler(null,filenameOut,pathOut,false);
  				}
  				else{
  					this.logReport = new FileHandler(null, filenameOut + "_"+ timestamp +".csv",pathOut, true);
  				}
  			}
  				catch (Exception ex)
  				{ ex.printStackTrace();}

  			}
  		

  	}
  	
  	public void openLogPathStatReport(String path,boolean append){
  		String pathOut = null;
  		String filenameOut = null;
  		
  		if(path !=null && !path.equals("")){
  			try {
  				String separator = "/";
  				File ftemp = new File(path);
  				String aT = ftemp.getAbsolutePath().substring(0,ftemp.getAbsolutePath().
                        lastIndexOf(separator)+1);

  				pathOut = aT;
  				filenameOut = ftemp.getName();

  			} catch (Exception e) {
  				e.printStackTrace();
  			}

  			String timestamp = Utility.getTimestamp();
  			try{
	  			if(append){
	  			    	boolean setHeaders = FileHandler.checkFilePath(pathOut + "/" + filenameOut);
	  				this.logPathStatReport = new FileHandler(null,filenameOut,pathOut,false);
	  				this.logPathStatReport.setHasHeaders(setHeaders?true:false); 
	  			}
	  			else
	  				this.logPathStatReport = new FileHandler(null, filenameOut + "_"+ timestamp +".csv",pathOut, true);

  			}
			catch (Exception ex)
			{ ex.printStackTrace();}

  		}
  		
  	}
    public boolean isInsertStatHeaders() {
		return insertStatHeaders;
	}

	public void setInsertStatHeaders(boolean insertStatHeaders) {
		this.insertStatHeaders = insertStatHeaders;
	}

	public Client getClientElastic() {
		return clientElastic;
	}

	public void setClientElastic(Client clientElastic) {
		this.clientElastic = clientElastic;
	}


}
