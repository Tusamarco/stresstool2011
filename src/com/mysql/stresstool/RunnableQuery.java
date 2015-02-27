package com.mysql.stresstool;

import java.io.*;
import java.sql.*;

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
public  class RunnableQuery implements Runnable {
    private File queriesFileIn;
    Connection conn = null;

    private String jdbcUrl;
    private boolean repet = false;
    private int repeatNumber = 0;
    private int ID;
    private boolean doLog;
    public RunnableQuery() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (Exception ex)
        {
            // handle the error
        }

    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes the object's <code>run</code> method to be called in
     * that separately executing thread.
     *
     * @todo Implement this java.lang.Runnable method
     */
    public void run() {

        BufferedReader d = null;

        if(queriesFileIn != null && conn != null)
        {

            try{
                Statement stmt = null;
                ResultSet rs = null;
                conn.setAutoCommit(true);
                stmt = conn.createStatement();
                long execTime = 0;


                for(int repeat = 0 ; repeat <= repeatNumber ; repeat++)
                {
                    String query = null;
                    int iLine = 0;

                    d = new BufferedReader(new FileReader(queriesFileIn));

                    while((query = d.readLine()) != null)
                    {
                        iLine++;
                        if(query.equals("SELECT 1;"))
                            continue;

                        try
                        {
                            long timeStart = System.currentTimeMillis();
                            rs = stmt.executeQuery(query);
                            long timeEnds = System.currentTimeMillis();
                            execTime = (timeEnds - timeStart);

                        }
                        catch (SQLException sqle)
                        {

//                            System.out.println("Error in line=" + line + " " +
//                                               sqle.getMessage());
//                            outb.println("Error in line=" + line + " " +
//                                         sqle.getMessage());
//                            outb.flush();
                        }
                        finally
                        {
                            if(doLog)
                                System.out.println("Query TH = " + this.getID() + " Line Number = " + iLine + " Exec Time(ms) =" + execTime);
                        }
                    }

                }
                System.out.println("Query TH = " + this.getID() + " COMPLETED! ");

                return;



            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }



        }

    }

    public static void main(String[] args) {
    }

    public void setQueriesFileIn(File queriesFileIn) {
        this.queriesFileIn = queriesFileIn;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        try {
            this.conn= DriverManager.getConnection(jdbcUrl);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setRepet(boolean repet) {
        this.repet = repet;
    }

    public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    public void setDoLog(boolean doLog) {
        this.doLog = doLog;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public File getQueriesFileIn() {
        return queriesFileIn;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public boolean isRepet() {
        return repet;
    }

    public int getRepeatNumber() {
        return repeatNumber;
    }

    public boolean isDoLog() {
        return doLog;
    }

    public int getID() {
        return ID;
    }
}
