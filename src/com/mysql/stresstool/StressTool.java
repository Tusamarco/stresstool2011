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
import java.sql.*;
import java.text.NumberFormat;
import java.util.*;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.node.Node;
import org.ini4j.*;

import net.tc.*;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.utils.elastic.elasticProvider;
import net.tc.utils.file.FileHandler;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 * @version 1.0
 * @author marcotusa
 *
 */
public class StressTool {
    File file = null;
    String sourceSQLPath = null;
    String queryType = "Select";
    static String[] alpha = new String[]{"a","b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","v","z"," "};
    int repeatNumber = 10;
	int sleepFor = 0; //these are mills seconds
    int sleepWrite = 0;
    int sleepRead = 0;
    int sleepDelete = 0;
    String connUrl = null;//"jdbc:mysql://127.0.0.1:3320/test?" +"user=test2&password=test&autoReconnect=true";
    int poolNumber = 10;
    String tableEngine = "MyISAM";
    boolean truncate = false;
    boolean droptable = true;
    boolean dolog = false;
    boolean operationShort = true;
    boolean doDelete = false;
    boolean doReport = true;
    boolean ignoreBinlog = false;
    boolean createtable = true;
    private boolean debug = false;
    String dbType = "MySQL";
    static Random rnd = new Random();
    float pctInsert = 100;
    float pctSelect = 100;
    float pctDelete = 100;
    boolean doBatch = false;
    int iBatchInsert = 0;
    int iBatchSelect = 400;
    boolean doSimplePk= false;
    private int onDuplicateKey=0;
    Map insertThread ;
    Map selectThread ;
    Map deleteThread ;
    private static Map threadInfoInsertMap ;
    private static Map threadInfoSelectMap;
    private static Map threadInfoDeleteMap;
    private MySQLStats mySQLStatistics;
    private String insertDefaultClass = "com.mysql.stresstool.RunnableQueryInsert";
    private String selectDefaultClass = "com.mysql.stresstool.RunnableQuerySelect";
    private String deleteDefaultClass = "com.mysql.stresstool.RunnableQueryDelete";
    private String connectString = "localhost:1186";
    private String databaseDefault = "test" ; 
    private String sqlString = "";
    private int  currentLastLoop =0;
    private int prevPercentLoop =0;
    private final int INSERT_ID_CONST =1000;
    private final int SELECT_ID_CONST =2000; 
    private final int DELETE_ID_CONST =3000;
    private Wini stressSettings;
    private String logPathStats ;
    private String logPathReport ;
    private String logPathStatReport;
    private boolean appendLogStat;
    private boolean appendLogReport;
    private boolean insertStatHeaders;
    private static net.tc.utils.file.FileHandler errorLogHandler =null;
    private String errorLog ="/tmp/error_test_default.log";
    private boolean hasElasticSearch = false;
    private String elasticUrl;
    private Node nodeElastic;
    static String[] argsInt = null;
/*
 * TODO: * Take out all the information about the configration and create a Configurator object
 *  	    
 */
    
    private static final ArrayList  lParameters = new ArrayList(Arrays.asList(
    		"connectString","connUrl","createtable","dbType","doDelete","doBatch","doReport","dolog",
    		"doSimplePk","droptable","ignoreBinlog","operationShort","poolNumber",
    		"pctInsert","pctSelect","pctDelete","repeatNumber","tableEngine","truncate","numberOfprimaryTables","numberOfSecondaryTables",
    		"numberOfJoinTables","numberOfIntervalKeys","SelectClass","InsertClass","DeleteClass","logPathStats","logPathReport","logPathStatReport",
    		"appendLogStat","appendLogReport","insertStatHeaders","errorLog","debug","onDuplicateKey","hasElasticSearch","elasticUrl"));

    
//"sleepWrite","sleepSelect","sleepDelete" 


//    new File("/usr/mysql/tempdata/select_from_allqueries.log");
//    File fileW = new File("/usr/mysql/tempdata/query_out.txt");

private static void showHelp() {
	lParameters.get(0);
//    String[]  arParameters = new String[]{
//    		"connUrl","truncate","droptable","poolNumber",
//            "repeatNumber","tableEngine","sleepFor","dbType",
//            "operationShort","doDelete","doBatch",
//            "pctInsert","pctSelect","dolog","doReport","InsertDefaultClass","SelectDefaultClass","DeleteDefaultClass","ConnectString","DataBase"};

    StringBuffer sb = new StringBuffer();
    sb.append("/**\n");
    sb.append("* connUrl jdbc url //jdbc:mysql://127.0.0.1:3320/test?user=test&password=test&autoReconnect=true\n");
    sb.append("* createtable  [true|false] = Create the tables on test DB\n");
    sb.append("* truncate  [true|false] = truncate tables on test DB\n");
    sb.append("* droptable  [true|false] = drop tables on test DB\n");
    sb.append("* poolNumber (number of threads)\n");
    sb.append("* repeatNumber the cycle the threads will do(cycles)\n");
    sb.append("* tableEngine mysql engine (MyISAM; InnoDb; Memory; ndbcluster; mariaDB)\n");
    sb.append("* sleepFor Thread sleep time (milliseconds default is 0 ms)\n");
    sb.append("* dolog DON'T USE IT write the log on standard output\n");
    sb.append("* dbType [Oracle|MySQL] default MySQL\n");
    sb.append("* doDelete perform Delete operation while inserting\n");
    sb.append("* doBatch operate insert with batch inserting values(1,2,3),(5,2,6) ... fix number of batch=50 \n");
    sb.append("* pctInsert %of the total threads to dedicate to Insert \n");
    sb.append("* pctSelect %of the total threads to dedicate to pctSelect \n");
    sb.append("* pctDelete %of the total threads to dedicate to Delete \n");
    sb.append("* insertDefaultClass/selectDefaultClass/deleteDefaultClass you can specify the custome class by action need to implement \n RunnableQuery(Insert|Select|Delete)Interface \n");
    sb.append("* ignorebinlog instruct the tool to insert the command to IGNORE the flush to binary log,\n this will works only if you a user with SUPER privileages \n");
    sb.append("* e.g. jdbc:mysql://127.0.0.1:3306/test?user=test&password=test&autoReconnect=true --createtable=true --truncate=false");
    sb.append(" --droptable=true --poolNumber=1 --repeatNumber=1 --tableEngine=InnoDB --sleepFor=0 --dbType=MySQL --doDelete=false --doBatch=false ");
    sb.append(" --pctInsert=100 --pctSelect=100 --pctDelete=10 --doReport=true|false (default=true) --doSimplePk=true|false (default=false) --ignorebinlog=true (default = false)\n");
    //sb.append("* e.g. jdbc:oracle:thin:@hostname:1526:orcl\n");

    sb.append("*/\n");
    System.out.println(sb.toString());


    System.exit(0);
    }

    protected StressTool(String[] args) {

        if(args == null || args.length < 1)
            StressTool.showHelp();

        StressTool.setArgsInt(args);
        /* TODO:
         * All the parameters need to be converted into one single map this is horrible.
         */
        java.util.Collections.sort(lParameters);
        
        if(args.length >= 1 && args[0].indexOf("defaults-file") > 0){

        	try {
        			stressSettings = new Wini(new File((String)args[0].split("=")[1]));
        			Map<String, String> mainSettings = stressSettings.get("main");
        			
        			for(int i = 0 ; i < lParameters.size() ; i++)
						try {
							String methodName = (String)lParameters.get(i);
							methodName = methodName.substring(0,1).toUpperCase() + methodName.substring(1,methodName.length()); 
							methodName = "set"+methodName;
							
							
							String valueM = mainSettings.get(lParameters.get(i));
//							System.out.println(methodName + " = " + valueM);
						
						if(valueM != null){
							if(valueM.equals("true") || valueM.equals("false")){
								MethodUtils.invokeMethod(this,methodName,Boolean.parseBoolean(valueM));
							}
							else if(Utils.isNumeric(valueM)){
								
								MethodUtils.invokeMethod(this,methodName,Integer.parseInt(valueM));
							}
							else
//								PropertyUtils.setProperty(this,methodName,valueM);
						//							MethodUtils.setCacheMethods(false);
							MethodUtils.invokeMethod(this,methodName,valueM);
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
        	
        	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
        	
        }

        if( args.length >= 2 && args[0].indexOf("defaults-file") > 0) {
        
	        for(int i  = 1 ; i < args.length ; i++)
	        {
	            	String param = null;
	            	String value = null;
	            	param = args[i].split("=")[0].trim();
	            	value = args[i].split("=")[1].trim();
	            	
			for(int ip = 0 ; ip < lParameters.size() ; ip++)
			    try {
				
				String methodName = (String)lParameters.get(ip);
				
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
        else if( args.length < 1) {
            StressTool.showHelp();
            System.exit(1);
        }

        try {
            stressSettings = new Wini(new File((String)args[0].split("=")[1]));
            Map<String, String> mainSettings = stressSettings.get("main");
            System.out.println(" MAIN SECTION");
            System.out.println("==============");
            for(int i = 0 ; i < lParameters.size() ; i++)
        	{
        	    String methodName = (String)lParameters.get(i);
        	    methodName = methodName.substring(0,1).toUpperCase() + methodName.substring(1,methodName.length()); 
//        	    methodName = "get"+methodName;


//        	    String valueM = mainSettings.get(lParameters.get(i));
        	    String value = null;
        	    try{ value = MethodUtils.invokeMethod(this, "get" + methodName, null).toString();}catch(Throwable th){}
        	    if( value == null)
        		try{value = MethodUtils.invokeMethod(this, "is" + methodName, null).toString();}catch(Throwable th){}
        	    if( value == null)
        	    {
        		Map<String, String> mainSettingsA = stressSettings.get("actionClass");
			value = mainSettingsA.get(lParameters.get(i));
        		
        		
        	    }
        	    
        	    
        	    System.out.println(methodName + " = "  + value);
        	}
            
            System.out.println("\nAPPLICATION SECTION");
            System.out.println("====================");

            Map<String, String> appSettings = stressSettings.get("actionClass");
            Iterator it = appSettings.keySet().iterator();
            while(it.hasNext()){
        	    String methodName = (String) it.next();
        	    String value = null;
        	    
        	    for(int i = 1 ; i < args.length; i++)
        	    {
        		String parName = ((String)(args[i].split("=")[0])).replace("--", ""); 
        		if(parName.equals(methodName)){
                	    methodName = methodName.substring(0,1).toUpperCase() + methodName.substring(1,methodName.length()); 
                	    value = args[i].split("=")[1];
                	    
                	    try{ value = MethodUtils.invokeMethod(this, "get" + methodName, null).toString();}catch(Throwable th){}
                	    if( value == null)
                		try{value = MethodUtils.invokeMethod(this, "is" + methodName, null).toString();}catch(Throwable th){}
        		    
        		}
        		   
        	    }
        	    if(value == null)
        		System.out.println(methodName + " = "  + appSettings.get(methodName));
        	    else
        		System.out.println(methodName + " = "  + value);
        	    //		value = mainSettingsA.get(lParameters.get(i));
            }

//        	} catch (IllegalAccessException e) {
//        	    // TODO Auto-generated catch block
//        	    e.printStackTrace();
//        	} catch (InvocationTargetException e) {
//        	    // TODO Auto-generated catch block
//        	    e.printStackTrace();
//        	} catch (NoSuchMethodException e) {
//        	    // TODO Auto-generated catch block
//        	    e.printStackTrace();
//        	}

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        
    }

    public int getOnDuplicateKey() {
		return onDuplicateKey;
	}

	public void setOnDuplicateKey(int onDuplicateKey) {
		this.onDuplicateKey = onDuplicateKey;
	}

	public StressTool(String dbType) {

    }

    public void setDbType(String dbType) {

        try {
            if (dbType != null && dbType.equals("MySQL")) {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } 
//            else {
//                DriverManager.registerDriver(new OracleDriver());
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ;
    }

    public static void setThreadInfoSelectMap(Map threadInfoSelectMap) {
       threadInfoSelectMap = threadInfoSelectMap;
    }

    public void setMySQLStatistics(MySQLStats mySQLStatistics) {
        this.mySQLStatistics = mySQLStatistics;
    }

    public boolean isHasElasticSearch() {
		return hasElasticSearch;
	}

	public void setHasElasticSearch(boolean hasElasticSearch) {
		this.hasElasticSearch = hasElasticSearch;
	}

	public String getElasticUrl() {
		return elasticUrl;
	}

	public void setElasticUrl(String elasticUrl) {
		this.elasticUrl = elasticUrl;
	}

	public static void setThreadInfoMap(Map threadInfoMap) {
    	threadInfoInsertMap = threadInfoMap;
    }



    public static void main(String[] args) {

        if(args == null ||(args.length >= 1 && args[0].indexOf("help") > -1 ))
            showHelp();

        StressTool runningStress = new StressTool(args);


            if(runningStress.droptable)
                runningStress.truncate = false;

//            threadInfoInsertMap = new HashMap((runningStress.poolNumber));
//            threadInfoSelectMap = new HashMap((runningStress.poolNumber));
//            threadInfoDeleteMap = new HashMap((runningStress.poolNumber));


            StressTool.setErrorLogHandler(StressTool.openLogPathError(runningStress.getErrorLog(), runningStress.isAppendLogReport()));
            StressTool.getErrorLogHandler().appendToFile("Starting test at " + Utility.getTimestamp()+ "\n");
            runningStress.setMySQLStatistics(new MySQLStats(runningStress.connUrl));
            /*
             * If elastic is define then assign a client to the MySQL stat to push data there.
             */
            if(runningStress.isHasElasticSearch()){
            	Map <String,String>confElastic = new SynchronizedMap(0);
            	confElastic.put("URL", runningStress.getElasticUrl());
            	runningStress.getMySQLStatistics().setClientElastic(elasticProvider.getClientTransporter(confElastic));
            }
            runningStress.getMySQLStatistics().setNumberOfLoops(runningStress.repeatNumber);
            runningStress.getMySQLStatistics().setNumberOfThreads(runningStress.poolNumber);
            runningStress.getMySQLStatistics().setDoBatching(runningStress.doBatch);
            runningStress.getMySQLStatistics().setTotalQueryToRunReads(runningStress.repeatNumber,runningStress.pctSelect );
            runningStress.getMySQLStatistics().setTotalQueryToRunWrites(runningStress.repeatNumber,runningStress.pctInsert );
            runningStress.getMySQLStatistics().setTotalQueryToRunDeletes(runningStress.repeatNumber,runningStress.pctDelete);
            runningStress.getMySQLStatistics().setEngineName(runningStress.tableEngine);
            runningStress.getMySQLStatistics().setTotalQueryToRun();
            runningStress.getMySQLStatistics().openLogReport(runningStress.getLogPathReport(), runningStress.appendLogReport);
            runningStress.getMySQLStatistics().openLogStats(runningStress.getLogPathStats(), runningStress.appendLogStat);
            runningStress.getMySQLStatistics().openLogPathStatReport(runningStress.getLogPathStatReport(), runningStress.appendLogReport);
            runningStress.getMySQLStatistics().setInsertStatHeaders((runningStress.insertStatHeaders));
            

//        }

        runningStress.createTestTables();
        if(runningStress.doReport)
        	runningStress.getMySQLStatistics().getStatus();

        runningStress.createThreads();
        runningStress.waitForLoops();
        System.exit(0);

//        if(args.length >= 10 && args[9] != null)
//        {
//            runthselect.createAll();
//            runthselect.waitForLoops();
//        }
//        else
//        {
////        runthselect.startPoolDelete();
//            runthselect.startPoolInsert();
//            runthselect.startPoolSelect();
//            runthselect.waitForLoops();
//        }

    }

    /**
     * waitForLoops
     */
    private void waitForLoops() {

        boolean insertRunning = true;
        boolean stressRunning = false;
        boolean selectRunning = true;
        boolean deleteRunning = true;
        boolean insertReportPrinted = false;
        boolean selectReportPrinted = false;
        boolean deleteReportPrinted = false;
        Console cons =null;
        
        StringBuffer sb = new StringBuffer();
        
        
//        if ((cons = System.console()) != null){
//        	String inSring = cons.readLine();
//        	System.out.print(inSring +" ");
//        	
//        }
        
        stressRunning = true;
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String limit="_";
        for (int ic =0; ic <100;ic++){limit = limit + "_";}
        System.out.println("[" + limit + "]");
        System.out.print("[");
		
        do
        {
            selectRunning = false;
            insertRunning = false;
            deleteRunning = false;
            
            
            try{
//                this.getMySQLStatistics().getStatus();
                Thread.sleep(1000);
                
                if(this.doReport){
                	this.getMySQLStatistics().getStatus();
                	
                }
                
                Map thInfoMapInsert = StressTool.getThreadInfoMap();
                Map thInfoMapSelect = StressTool.getThreadInfoSelectMap();
                Map thInfoMapDelete = StressTool.getThreadInfoDeleteMap();

                if(thInfoMapSelect != null && thInfoMapSelect.size() > 0 && this.poolNumber > 0)
                {
                	
                	for(int i = 0 ; i < thInfoMapSelect.size(); i++)
	                {
                		try
                		{
                		   ThreadInfo thInfoSelect = (ThreadInfo)thInfoMapSelect.get(i+SELECT_ID_CONST);
		                   if(thInfoSelect != null && thInfoSelect.isStatusActive())
		                   {
		                       selectRunning = true;
		                       break;
		                   }
		
	                   }
	                   catch(ArrayIndexOutOfBoundsException arEx)
	                   {
	                	   System.err.print(" WARNING - Size mismatch while comparing Select threads pool current index (" + i + ")" 
	                			   + " Max upperBound should be (" + thInfoMapSelect.size() +")\n");
	                	   selectRunning = false;
	                   }

	                }
                	
                }

                if(thInfoMapInsert != null && thInfoMapInsert.size() > 0 && this.poolNumber > 0)
                {
	                for(int i = 0 ; i < thInfoMapInsert.size(); i++)
	                {
	                   try
	                   {
		                	ThreadInfo thInfoInsert = (ThreadInfo)thInfoMapInsert.get(i+INSERT_ID_CONST);
		
		                    if(thInfoInsert != null && thInfoInsert.isStatusActive())
		                    {
		                        insertRunning = true;
		                        break;
		                        
		                        
		                    }
	                   }
	                   catch(ArrayIndexOutOfBoundsException arEx)
	                   {
	                	   System.err.print(" WARNING - Size mismatch while comparing Insert threads pool current index (" + i + ")" 
	                			   + " Max upperBound should be (" + thInfoMapInsert.size() +")\n");
	                	   selectRunning = false;
	                   }
	                	
	                }
                }

                if(thInfoMapDelete != null && thInfoMapDelete.size() > 0 && this.poolNumber > 0)
                {
                	for(int i = 0 ; i < thInfoMapDelete.size(); i++)
	                {
                		try
                		{
                		   ThreadInfo thInfoDelete = (ThreadInfo)thInfoMapDelete.get(i+DELETE_ID_CONST);
		                   if(thInfoDelete != null && thInfoDelete.isStatusActive())
		                   {
		                	   deleteRunning = true;
		                	   break;
		                   }
		
	                   }
	                   catch(ArrayIndexOutOfBoundsException arEx)
	                   {
	                	   System.err.print(" WARNING - Size mismatch while comparing Delete threads pool current index (" + i + ")" 
	                			   + " Max upperBound should be (" + thInfoMapDelete.size() +")\n");
	                	   deleteRunning = false;
	                   }

	                }
                	
                }


//                if(!deleteRunning &&  !deleteReportPrinted)
                if(!deleteRunning )                	
                {
//                	if(this.doReport)
//                		System.out.println(this.getMySQLStatistics().printFinalSummaryRead(""));
//                    selectReportPrinted = true;
                    if (!insertRunning && !selectRunning)
                        stressRunning = false;

                }
                
                
//                if(!selectRunning &&  !selectReportPrinted)
                if(!selectRunning)
                {
//                	if(this.doReport)
//                		System.out.println(this.getMySQLStatistics().printFinalSummaryRead(""));
                	if(!selectReportPrinted ){
	                	selectReportPrinted = true;
//	                	sb.append(this.getMySQLStatistics().printFinalSummaryRead(""));
                	}
                    if (!insertRunning && !deleteRunning)
                        stressRunning = false;

                }
//                if(!insertRunning &&  !insertReportPrinted)
                if(!insertRunning)
                {
//                	if(this.doReport)
//                		System.out.println(this.getMySQLStatistics().
//                                       printFinalSummaryWrites(""));
                   if(!insertReportPrinted){
                	insertReportPrinted = true;
//                	sb.append(this.getMySQLStatistics().printFinalSummaryWrites(""));
                   }
                    if (!selectRunning && !deleteRunning)
                        stressRunning = false;

                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();

            }
            
            if(!this.dolog)
            {
            	printProgress();
            }

            
        }while(stressRunning);
        
        System.out.print("]\n");

        Map thInfoMapInsert = StressTool.getThreadInfoMap();
        if(this.doReport){
        	this.getMySQLStatistics().getStatus();
        	/*
        	 * Close the elastic client
        	 */
        	if(this.getMySQLStatistics().getClientElastic() != null){
        		this.getMySQLStatistics().getClientElastic().close();
        	}
        }
        
        {
        	Object[] itAll =  thInfoMapInsert.keySet().toArray() ;
	        for(int i = 0 ; i < itAll.length ; i++)
	        {
	            ThreadInfo thInfoInsert = (ThreadInfo)thInfoMapInsert.get(itAll[i]);
	            if(thInfoInsert != null)
	                System.out.println("Query Insert TH = " + thInfoInsert.getId() + " COMPLETED!  TOTAL TIME = " + thInfoInsert.getExecutionTime() + "(ms) Sec =" + (thInfoInsert.getExecutionTime()/1000));
	        }
        }


        {
        	Object[] itAll =  threadInfoSelectMap.keySet().toArray() ;
	        for(int i = 0 ; i < itAll.length; i++)
	        {
	            ThreadInfo thInfoRead = (ThreadInfo)threadInfoSelectMap.get(itAll[i]);
	            if(thInfoRead != null)
	                System.out.println("Query Select TH = " + thInfoRead.getId() + " COMPLETED!  TOTAL TIME = " + thInfoRead.getExecutionTime() + "(ms) Sec =" + (thInfoRead.getExecutionTime()/1000));
	        }
        }
        
        {
        	Object[] itAll =  threadInfoDeleteMap.keySet().toArray() ;
	        for(int i = 0 ; i < itAll.length; i++)
	        {
	            ThreadInfo thInfoRead = (ThreadInfo)threadInfoDeleteMap.get(itAll[i]);
	            if(thInfoRead != null)
	                System.out.println("Query Delete TH = " + thInfoRead.getId() + " COMPLETED!  TOTAL TIME = " + thInfoRead.getExecutionTime() + "(ms) Sec =" + (thInfoRead.getExecutionTime()/1000));
	        }
        }

        
//    	if(this.doReport)
//    		System.out.println(this.getMySQLStatistics().printFinalSummaryRead(""));
//
//    	if(this.doReport)
//		System.out.println(this.getMySQLStatistics().printFinalSummaryWrites(""));

    	if(this.doReport){
    		System.out.println(sb.toString());
    		System.out.println(this.getMySQLStatistics().printFinalSummary(printReport()));
    	}
        	




    }
    

	/**
	 * Print the cursor 
	 */
	private void printProgress() {

		HashMap allTh = new HashMap(0);
		if(StressTool.getThreadInfoMap() != null && StressTool.getThreadInfoMap().size() >0)
			allTh.putAll(StressTool.getThreadInfoMap());
		if(StressTool.getThreadInfoSelectMap() != null && StressTool.getThreadInfoSelectMap().size() > 0)
			allTh.putAll(StressTool.getThreadInfoSelectMap());

		if(StressTool.getThreadInfoDeleteMap() != null && StressTool.getThreadInfoDeleteMap().size() > 0)
			allTh.putAll(StressTool.getThreadInfoDeleteMap());

		
		if (allTh != null && allTh.size() > 0){

		    double currentLastLoop = this.getCurrentLastLoop();
		    //GEt the real status of the execution
		    
		    if(allTh.size() > 0){
		    	Object[] itAll =  allTh.keySet().toArray() ;
			    for(int i = 0; i <= itAll.length -1; i++)
			    {
			    	double tmpMin = 0.0;
			    	if (itAll.length > 1 && i < itAll.length -1){ 
				    	int a = ((ThreadInfo)allTh.get(itAll[i])).getExecutedLoops();
				    	int b = ((ThreadInfo)allTh.get(itAll[i+1])).getExecutedLoops();
				    	tmpMin = Math.min(a, b);
			    	}
			    	else
			    	{
			    		int a = ((ThreadInfo)allTh.get(itAll[i])).getExecutedLoops();
			    		tmpMin = Math.min(a, a);
			    	}
			    	
				    if(i > 0)
			    		currentLastLoop = Math.min(currentLastLoop, tmpMin);
			    	else
			    		currentLastLoop = tmpMin;
			    	
//			    	this.setCurrentLastLoop(currentLastLoop);
//System.out.print("Running min = " + currentLastLoop + "\n");
//			    	int a = ((ThreadInfo)allTh.get(i)).getExecutedLoops();
//			    	int b = ((ThreadInfo)allTh.get(i+1)).getExecutedLoops();
//			    	currentLastLoop = Math.min(a, b);
			    	
			    }
		    }
		    else{
		    	Object[] itAll =  allTh.keySet().toArray() ;
//		    	currentLastLoop = ((ThreadInfo)allTh.get(itAll[0])).getExecutedLoops();

		    }
	    	
		    int perccurrentLastLoop = 1;
		    int curPrevLoop = this.getCurrentLastLoop()>0?this.getCurrentLastLoop():-1;
		    perccurrentLastLoop = new Double(Math.ceil((((double)currentLastLoop/repeatNumber) * 100))).intValue();
//		    System.out.println(perccurrentLastLoop + " " + this.getCurrentLastLoop() + " " + repeatNumber);
		    if(perccurrentLastLoop > curPrevLoop ){
//		    	int toprint = new Double(perccurrentLastLoop - this.getCurrentLastLoop()).intValue();
		    	
		    	int toprint =  (perccurrentLastLoop - curPrevLoop) ;
		    	if(this.debug)
		    		System.out.println(toprint + " " + perccurrentLastLoop + " " + curPrevLoop + " " + repeatNumber);
		    	for(int ic=0 ;ic<toprint;ic++){
		    		System.out.print("*");
		    		
		    	}
		        this.setCurrentLastLoop(perccurrentLastLoop);
//		        this.prevPercentLoop = perccurrentLastLoop;
		    }
		}
	}

    /**
     * createAll
     */
    private  void createThreads() {
        float maxInsert = poolNumber;
        float maxSelect = poolNumber;
        float maxDelete = poolNumber;
        maxSelect=(maxSelect * (pctSelect / 100));
        maxInsert=(maxInsert * (pctInsert / 100));
        maxDelete=(maxDelete * (pctDelete / 100));
        int aInsert =  new Float( maxInsert).intValue();
        int aSelect =  new Float( maxSelect).intValue();
        int aDelete =  new Float( maxDelete).intValue();
        
        threadInfoInsertMap = new HashMap((aInsert > 0? aInsert - 1:0 ));
        threadInfoSelectMap = new HashMap((aSelect > 0? aSelect - 1:0));
        threadInfoDeleteMap = new HashMap((aDelete > 0? aDelete - 1:0));

        
        HashMap connMapcoordinates = new HashMap();
        connMapcoordinates.put("jdbcUrl", connUrl);
        connMapcoordinates.put("dbType", dbType);
        connMapcoordinates.put("connectstring", connectString);
        connMapcoordinates.put("database", databaseDefault);


        if(this.ignoreBinlog){
        	 if(!validatePermission(connMapcoordinates)){
        		 System.out.println("============ ERROR ============");
        		 System.out.println("You ask for skipping mysql Binlog Insert but you don't have the permission to do it");
        		 System.out.println("Change the flag for parameter \" ignorebinlog \" from true to false, OR remove it");
        	 }
        	
        
        }
        
        System.out.println("Thread to run for Insert:" + maxInsert);
        System.out.println("Thread to run for Select:" + maxSelect);
        System.out.println("Thread to run for Delete:" + maxDelete);
        System.out.println("Class handling Insert :" + this.insertDefaultClass);
        System.out.println("Class handling Select :" + this.selectDefaultClass);
        System.out.println("Class handling Delete :" + this.deleteDefaultClass);

        if (aInsert >= 1)
            insertThread =new HashMap(poolNumber);

        if (aSelect >= 1)
            selectThread =new HashMap(poolNumber);

        if (aDelete >= 1)
            deleteThread =new HashMap(poolNumber);
        
        
        for (int i = 0; i < poolNumber; i++) {

            if(aInsert > i)
            {

                System.out.println("Starting Insert:  " + i);
                RunnableQueryInsertInterface qth = null;
				try {
					qth = (RunnableQueryInsertInterface)Class.forName(insertDefaultClass).newInstance();
				} catch (InstantiationException e) {
					System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
					e.printStackTrace();
				}
                qth.setID(i + INSERT_ID_CONST);
                qth.setJdbcUrl(connMapcoordinates);
                qth.setSleepFor(sleepWrite);
                qth.setRepeatNumber(repeatNumber);
                qth.setDoLog(dolog);
                qth.setOperationShort(operationShort);
                qth.setDbType(dbType);
                qth.setDoDelete(doDelete);
                qth.setIgnoreBinlog(ignoreBinlog);
                qth.setEngine(tableEngine);
                qth.setIBatchInsert(iBatchInsert);
                qth.setUseBatchInsert(doBatch);
                qth.setDoSimplePk(doSimplePk);
                qth.setOnDuplicateKey(this.getOnDuplicateKey());
                if(stressSettings.get("actionClass") != null){
                	qth.setClassConfiguration(stressSettings.get("actionClass"));
                }
                
                if(this.doReport)
                	qth.setMySQLStatistics(mySQLStatistics);
                
                Thread th = new Thread((Runnable) qth);
                th.start();
                insertThread.put(i,qth);

            }
            if(aSelect > i)
            {
                System.out.println("Starting Select:  " + i);
                RunnableQuerySelectInterface qths = null;
				try {
					qths = (RunnableQuerySelectInterface)Class.forName(selectDefaultClass).newInstance();
				} catch (InstantiationException e) {
					System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
					e.printStackTrace();
				}
                
                qths.setID(i + SELECT_ID_CONST);
                qths.setJdbcUrl(connMapcoordinates);
                qths.setSleepFor(sleepRead);
                qths.setRepeatNumber(repeatNumber);
                qths.setDoLog(dolog);
                qths.setDbType(dbType);
                qths.setEngine(tableEngine);
                qths.setIBatchSelect(iBatchSelect);
                
                qths.setSqlQuery(sqlString);
                qths.setDoSimplePk(doSimplePk);
                if(stressSettings.get("actionClass") != null){
                	qths.setClassConfiguration(stressSettings.get("actionClass"));
                }

                if(this.doReport)
                	qths.setMySQLStatistics(mySQLStatistics);
                Thread ths = new Thread((Runnable) qths);
                ths.start();
                selectThread.put(i,qths);
            }

            if(doDelete)
            {
            	if (aDelete > i){
	                System.out.println("Starting Delete:  " + i);
	                RunnableQueryDeleteInterface qthd = null;
					try {
						qthd = (RunnableQueryDeleteInterface)Class.forName(deleteDefaultClass).newInstance();
					} catch (InstantiationException e) {
						System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
						e.printStackTrace();
					}

	                qthd.setID(i + DELETE_ID_CONST);
	                qthd.setJdbcUrlMap(connMapcoordinates);
	                qthd.setSleepFor(sleepDelete);
	                qthd.setRepeatNumber(repeatNumber);
	                qthd.setDoLog(dolog);
	                qthd.setOperationShort(operationShort);
	                qthd.setDbType(dbType);
	                qthd.setDoDelete(doDelete);
	                qthd.setEngine(tableEngine);
	                qthd.setUseBatchInsert(doBatch);
	                qthd.setDoSimplePk(doSimplePk);
	                if(stressSettings.get("actionClass") != null){
	                	qthd.setClassConfiguration(stressSettings.get("actionClass"));
	                }

	                if (this.doReport)
	                	qthd.setMySQLStatistics(mySQLStatistics);
	                
	                Thread th = new Thread((Runnable) qthd);
	                th.start();
	                deleteThread.put(i,qthd);
            	}

            }

        }



    }

    private boolean validatePermission(HashMap connMapcoordinates) {
    	
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Map jdbcUrlMap = connMapcoordinates ;
        boolean valid = false;
        
        try {
            if(jdbcUrlMap.get("dbType") != null &&  !((String)jdbcUrlMap.get("dbType")).equals("MySQL"))
            {
                conn=DriverManager.getConnection((String)jdbcUrlMap.get("dbType"),"test", "test");
            }
            else
            conn= DriverManager.getConnection((String)jdbcUrlMap.get("jdbcUrl"));

            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select Super_priv from mysql.user where user=substring(CURRENT_USER(),1,locate('@',CURRENT_USER())-1) and host=substring(CURRENT_USER(),locate('@',CURRENT_USER())+1)");

            while(rs.next()){
            	String permission = rs.getNString(1);
            	if(permission.toLowerCase().equals("y")){
            		valid = true;
            		rs.afterLast();
            	}
            }
        
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally 
        {
        	try {
        		rs.close();
        		stmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        
		// TODO Auto-generated method stub
		return valid;
	}

	String printReport()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println(" jdbc = " + connUrl);
        pw.println(" truncate = " + truncate);
        pw.println(" poolNumber = " + poolNumber);
        pw.println(" repeatNumber = " + repeatNumber);
        pw.println(" tableEngine = " + tableEngine);
        pw.println(" droptable = " + droptable);
        pw.println(" dolog = " + dolog);
        pw.println(" Interval sleepFor = " + sleepFor);
        pw.println(" dbType = " + dbType);
        pw.println(" operation short (true|false) = " + operationShort);
        pw.println(" do Delete each 500 inserts = " + doDelete);
        pw.println(" do batch inserts of 50 records = " + doBatch);

        return sw.toString();

    }

    @Deprecated
    void createTestTables() {

        if(this.droptable && this.truncate)
        {
            this.truncate = false;
            System.out.println("****============================================================================*******");
            System.out.println("It is not advisable to do drop and truncate at the same time \nTRUNCATE will be resetted to false" );
            System.out.println("****============================================================================*******");
        }
        printReport();

        System.out.println(" output log = " );

        System.out.println(" STARTING ON = " + TimeTools.GetCurrent());

        if(dbType.equals("MySQL"))
        {
        	RunnableQueryInsertInterface qth = null;
			try {
				qth = (RunnableQueryInsertInterface)Class.forName(insertDefaultClass).newInstance();
		        HashMap connMapcoordinates = new HashMap();
		        connMapcoordinates.put("jdbcUrl", connUrl);
		        connMapcoordinates.put("dbType", dbType);
		        connMapcoordinates.put("connectstring", connectString);
		        connMapcoordinates.put("database", databaseDefault);
		        qth.setJdbcUrl(connMapcoordinates);
                qth.setJdbcUrl(connMapcoordinates);
                qth.setSleepFor(sleepWrite);
                qth.setRepeatNumber(repeatNumber);
                qth.setDoLog(dolog);
                qth.setOperationShort(operationShort);
                qth.setDbType(dbType);
                qth.setDoDelete(doDelete);
                qth.setIgnoreBinlog(ignoreBinlog);
                qth.setEngine(tableEngine);
                qth.setIBatchInsert(iBatchInsert);
                qth.setUseBatchInsert(doBatch);
                qth.setDoSimplePk(doSimplePk);
                if(stressSettings.get("actionClass") != null){
                	qth.setClassConfiguration(stressSettings.get("actionClass"));
                }

				qth.createSchema(this);	
//            createMysql();
			}
	         catch (InstantiationException e) {
				System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR === CLASS defined in configuration invalid or not correctly loaded \n CLASS = " + insertDefaultClass);
				e.printStackTrace();
			}
        }
        else
            createOracle();

    }



    @Deprecated 
    private void createOracle() {


        String DropTables1 = "Drop table test.tbtest1";
        String DropTables2 = "Drop table test.tbtest2";

        String TruncateTables1 = "Truncate table test.tbtest1";
        String TruncateTables2 = "Truncate table test.tbtest2";


        StringBuffer sb = new StringBuffer("CREATE TABLE \"TEST\".\"TBTEST1\"");
        sb.append(" (\"A\" NUMBER(38,0),\"B\" VARCHAR2(100 BYTE),\"C\" CHAR(200 BYTE),\"COUNTER\" NUMBER,");
        sb.append(" \"TIME\" TIMESTAMP (6) WITH TIME ZONE, \"RECORDTYPE\" CHAR(3 BYTE)) PCTFREE 10 PCTUSED");
        sb.append(" 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645");
        sb.append(" PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) TABLESPACE \"WORK\"");

        String tbts1 = sb.toString();


        sb = new StringBuffer("CREATE TABLE \"TEST\".\"TBTEST2\" (\"A\" NUMBER(38,0),");
        if(operationShort)
        {
            sb.append(" \"OPERATION\" VARCHAR2(254 BYTE),\"TIME\" TIMESTAMP (6) WITH TIME ZONE) ");
            sb.append(" PCTFREE 10 PCTUSED");
            sb.append(" 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645");
            sb.append(" PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) TABLESPACE \"WORK\"");
        }
        else
        {
            sb.append(" \"OPERATION\" CLOB,\"TIME\" TIMESTAMP (6) WITH TIME ZONE) ");
            sb.append(" PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS");
            sb.append(" LOGGING STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)");
            sb.append(" TABLESPACE \"WORK\" LOB (\"OPERATION\") STORE AS BASICFILE (TABLESPACE \"WORK\" ENABLE STORAGE IN ROW CHUNK 8192 NOCACHE LOGGING");
            sb.append(" STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 21 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT))");
        }

        String tbts2 = sb.toString();

        System.out.println(tbts1);
        System.out.println(tbts2);

        Connection conn =null;
        Statement stmt = null;

        try {

//           conn = DriverManager.getConnection
//                              ("jdbc:oracle:thin:@hostname:1526:orcl",
//                               "scott", "tiger");

           System.out.println("opening connection to connection url: " + connUrl);
            conn = DriverManager.getConnection(connUrl,"test", "test");


        // @machineName:port:SID,   userid,  password

            stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(
                    "select BANNER from SYS.V_$VERSION");
            while (rset.next())
                System.out.println(rset.getString(1)); // Print col 1
            stmt.close();

            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            if(this.droptable)
            {
                System.out.println("****============================================================================*******");
                System.out.println("**** Please wait DROPPING table tbtest1 and tbtest2 it could take a LOT of time *******");
                try{stmt.execute(DropTables1);}catch(Exception eex){}
                try{stmt.execute(DropTables2);}catch(Exception eex){}
                System.out.println("**** DROPPING finish *******");
                System.out.println("****============================================================================*******");

            }

            stmt.execute(tbts1);
            stmt.execute(tbts2);

            if(this.truncate)
            {
                System.out.println("****============================================================================*******");
                System.out.println("**** Please wait TRUNCATING table tbtest1 and tbtest2 it could take a LOT of time *******");

                try{stmt.execute(TruncateTables1);}catch(Exception eex){eex.printStackTrace();}
                try{stmt.execute(TruncateTables2);}catch(Exception eex){eex.printStackTrace();}
                System.out.println("**** TRUNCATING finish *******");
                System.out.println("****============================================================================*******");

            }


        } catch (Exception ex) {
            ex.printStackTrace(
                    );
        } finally {

            try {
                conn.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }

        }
    }




    static String getStringFromRandom(int len)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0 ; i <= len ; i++ )
        {
            sb.append(alpha[rnd.nextInt(alpha.length)]);
        }

        return sb.toString();
    }

    public static Long getNumberFromRandom(int index)
    {
//    	System.out.println(index);
        return new Long(rnd.nextInt(index));

    }

    static Long getNumberFromRandomMinMax(int min,int max)
    {
    	if(min == max) return new Long(max);
    	
        if(min == 0 && max == 0){
        	return new Long(0);
        }
        
    	Long maxL = new Long(rnd.nextInt(max - min) + min);
    	if(maxL < new Long(min)){
    		maxL = new Long(min * 2);    		
    	}
    	return maxL;

    }

    static Long getNumberFromRandomMinMaxCeling(int min,int max,int celing)
    {
    	if(min == max) return new Long(max);
    	
        if(min == 0 && max == 0){
        	return new Long(0);
        }
        
    	Long maxL = new Long(rnd.nextInt(max));
    	if(maxL < new Long(min)){
    		maxL = new Long(min * 2);    		
    	}
    	
    	if((maxL - new Long(min)) > celing){
    		return Math.abs(new Long (min + celing));
    	}
    	return Math.abs(maxL);

    }


    
    
    
    public static Map getThreadInfoMap() {
        return threadInfoInsertMap;
    }

    public static ThreadInfo getInfo(int id)
    {
        return (ThreadInfo)threadInfoInsertMap.get(id);
    }
    public static void setInfo(int id, ThreadInfo info)
    {
    	threadInfoInsertMap.put(id,info);
    }

    public static Map getThreadInfoSelectMap() {
        return threadInfoSelectMap;
    }

    public MySQLStats getMySQLStatistics() {
        return mySQLStatistics;
    }

    public static ThreadInfo getInfoSelect(int id) {
        return (ThreadInfo) threadInfoSelectMap.get(id);
    }

    public static void setInfoSelect(int id, ThreadInfo info) {
        threadInfoSelectMap.put(id, info);
    }

    public static void setInfoDelete(int id, ThreadInfo info) {
        threadInfoDeleteMap.put(id, info);
    }

    public static Map getThreadInfoDeleteMap() {
		return threadInfoDeleteMap;
	}

	public static void setThreadInfoDeleteMap(Map threadInfoDeleteMap) {
		StressTool.threadInfoDeleteMap = threadInfoDeleteMap;
	}

	
    public int getRepeatNumber() {
		return repeatNumber;
	}

	public void setRepeatNumber(int repeatNumber) {
		this.repeatNumber = repeatNumber;
	}

	public int getSleepFor() {
		return sleepFor;
	}

	public void setSleepFor(int sleepFor) {
		this.sleepFor = sleepFor;
	}

	public int getSleepWrite() {
		return sleepWrite;
	}

	public void setSleepWrite(int sleepWrite) {
		this.sleepWrite = sleepWrite;
	}

	public int getSleepRead() {
		return sleepRead;
	}

	public void setSleepSelect(int sleepRead) {
		this.sleepRead = sleepRead;
	}

	public int getSleepDelete() {
		return sleepDelete;
	}

	public void setSleepDelete(int sleepDelete) {
		this.sleepDelete = sleepDelete;
	}

	public String getConnUrl() {
		return connUrl;
	}

	public void setConnUrl(String connUrl) {
		this.connUrl = connUrl;
	}

	public int getPoolNumber() {
		return poolNumber;
	}

	public void setPoolNumber(int poolNumber) {
		this.poolNumber = poolNumber;
	}

	public String getTableEngine() {
		return tableEngine;
	}

	public void setTableEngine(String tableEngine) {
		this.tableEngine = tableEngine;
	}

	public boolean isTruncate() {
		return truncate;
	}

	public void setTruncate(boolean truncate) {
		this.truncate = truncate;
	}

	public boolean isDroptable() {
		return droptable;
	}

	public void setDroptable(boolean droptable) {
		this.droptable = droptable;
	}

	public boolean isDolog() {
		return dolog;
	}

	public void setDolog(boolean dolog) {
		this.dolog = dolog;
	}

	public boolean isOperationShort() {
		return operationShort;
	}

	public void setOperationShort(boolean operationShort) {
		this.operationShort = operationShort;
	}

	public boolean isDoDelete() {
		return doDelete;
	}

	public void setDoDelete(boolean doDelete) {
		this.doDelete = doDelete;
	}

	public boolean isDoReport() {
		return doReport;
	}

	public void setDoReport(boolean doReport) {
		this.doReport = doReport;
	}

	public boolean isIgnoreBinlog() {
		return ignoreBinlog;
	}

	public void setIgnoreBinlog(boolean ignoreBinlog) {
		this.ignoreBinlog = ignoreBinlog;
	}

	public boolean isCreatetable() {
		return createtable;
	}

	public void setCreatetable(boolean createtable) {
		this.createtable = createtable;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public float getPctInsert() {
		return pctInsert;
	}

	public void setPctInsert(int pctInsert) {
		float pct = new Float(pctInsert).floatValue();
		this.pctInsert = pct;
	}

	public float getPctSelect() {
		return pctSelect;
	}

	public void setPctSelect(int pctSelect) {
		float pct = new Float(pctSelect).floatValue();
		this.pctSelect = pct;
	}

	public float getPctDelete() {
		return pctDelete;
	}

	public void setPctDelete(int pctDelete) {
		float pct = new Float(pctDelete).floatValue();
		this.pctDelete = pct;
	}

	public boolean isDoBatch() {
		return doBatch;
	}

	public void setDoBatch(int batch) {
        this.iBatchInsert = batch;
        if(iBatchInsert > 0)
        	this.doBatch = true;
        else{
        	this.doBatch = false;
        	this.iBatchInsert = 0;
        }
	}

	public int getiBatchSelect() {
		return iBatchSelect;
	}

	public void setiBatchSelect(int iBatchSelect) {
		this.iBatchSelect = iBatchSelect;
	}

	public boolean isDoSimplePk() {
		return doSimplePk;
	}

	public void setDoSimplePk(boolean doSimplePk) {
		this.doSimplePk = doSimplePk;
	}

//	public void setDoSimple(boolean value){
//		this.doSimplePk = value;
//		
//	}
	public Map getInsertThread() {
		return insertThread;
	}

	public void setInsertThread(Map insertThread) {
		this.insertThread = insertThread;
	}

	public Map getSelectThread() {
		return selectThread;
	}

	public void setSelectThread(Map selectThread) {
		this.selectThread = selectThread;
	}

	public Map getDeleteThread() {
		return deleteThread;
	}

	public void setDeleteThread(Map deleteThread) {
		this.deleteThread = deleteThread;
	}

	public String getInsertClass() {
		return insertDefaultClass;
	}

	public String getInsertDefaultClass() {
		return insertDefaultClass;
	}

	public void setInsertClass(String insertDefaultClass) {
		this.insertDefaultClass = insertDefaultClass;
	}

	public String getDeleteClass() {
		return deleteDefaultClass;
	}

	public String getDeleteDefaultClass() {
		return deleteDefaultClass;
	}

	public String getSelectClass(){
		return this.selectDefaultClass;
		
	}
	public void setSelectClass(String selectClass){
		this.selectDefaultClass = selectClass;
		
	}
	public void setDeleteClass(String deleteDefaultClass) {
		this.deleteDefaultClass = deleteDefaultClass;
	}

	public String getConnectString() {
		return connectString;
	}

	void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public String getSqlString() {
		return sqlString;
	}

	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}

	public String getDbType() {
		return dbType;
	}
	
	/**
	 * @return the logPath
	 */
	public String getLogPathStats() {
		return logPathStats;
	}

	/**
	 * @param logPath the logPath to set
	 */
	public void setLogPathStats(String logPathStats) {
		this.logPathStats = logPathStats;
	}

	/**
	 * @return the logPathReport
	 */
	public String getLogPathReport() {
		return logPathReport;
	}

	/**
	 * @param logPathReport the logPathReport to set
	 */
	public void setLogPathReport(String logPathReport) {
		this.logPathReport = logPathReport;
	}

	/**
	 * @return the appendLogStat
	 */
	public boolean isAppendLogStat() {
		return appendLogStat;
	}

	/**
	 * @param appendLogStat the appendLogStat to set
	 */
	public void setAppendLogStat(boolean appendLogStat) {
		this.appendLogStat = appendLogStat;
	}

	/**
	 * @return the appendLogReport
	 */
	public boolean isAppendLogReport() {
		return appendLogReport;
	}

	/**
	 * @param appendLogReport the appendLogReport to set
	 */
	public void setAppendLogReport(boolean appendLogReport) {
		this.appendLogReport = appendLogReport;
	}

	/**
	 * @return the logPathStatReport
	 */
	public String getLogPathStatReport() {
		return logPathStatReport;
	}

	/**
	 * @param logPathStatReport the logPathStatReport to set
	 */
	public void setLogPathStatReport(String logPathStatReport) {
		this.logPathStatReport = logPathStatReport;
	}

	public void dummy(){
		setConnUrl("");
		
	}

	public boolean isInsertStatHeaders() {
		return insertStatHeaders;
	}

	public void setInsertStatHeaders(boolean insertStatHeaders) {
		this.insertStatHeaders = insertStatHeaders;
	}

  	private static net.tc.utils.file.FileHandler openLogPathError(String path,boolean append){
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
	  				return new FileHandler(null,filenameOut,pathOut,false);
	  			}
	  			else
	  				return new FileHandler(null, filenameOut + "_"+ timestamp +".log",pathOut, true);

			}
			catch (Exception ex)
			{ ex.printStackTrace();}

		}
		return null;
		
	}


  	    private int getCurrentLastLoop() {
  			return currentLastLoop;
  		}

  		private void setCurrentLastLoop(int currentLastLoop) {
  			this.currentLastLoop = currentLastLoop;
  		}


  		/**
  		 * @return the errorLogHandler
  		 */
  		public static net.tc.utils.file.FileHandler getErrorLogHandler() {
  		    return errorLogHandler;
  		}

  		/**
  		 * @param errorLogHandler the errorLogHandler to set
  		 */
  		public static void setErrorLogHandler(
  			net.tc.utils.file.FileHandler errorLogHandler) {
  		    StressTool.errorLogHandler = errorLogHandler;
  		}

  		/**
  		 * @return the errorLog
  		 */
  		public String getErrorLog() {
  		    return errorLog;
  		}

  		/**
  		 * @param errorLog the errorLog to set
  		 */
  		public void setErrorLog(String errorLog) {
  		    this.errorLog = errorLog;
  		}

		public Node getNodeElastic() {
			return nodeElastic;
		}

		public void setNodeElastic(Node nodeElastic) {
			this.nodeElastic = nodeElastic;
		}

		/**
		 * @return the argsInt
		 */
		public static  String[] getArgsInt() {
		    return argsInt;
		}

		/**
		 * @param argsInt the argsInt to set
		 */
		public static void setArgsInt(String[] argsIntIn) {
		    argsInt = argsIntIn;
		}

}


