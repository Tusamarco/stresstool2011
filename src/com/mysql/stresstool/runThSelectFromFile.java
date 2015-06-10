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
public class runThSelectFromFile {
    File file = null;
    String sourceSQLPath = null;
    String queryType = "Select";
    int repeatNumber = 0;
    String connUrl ="jdbc:mysql://10.1.4.44:3301/jmailer_it?" +"user=test2&password=test";
    int poolNumber = 3;
    private int sleepFor;

    //    new File("/usr/mysql/tempdata/select_from_allqueries.log");
//    File fileW = new File("/usr/mysql/tempdata/query_out.txt");



    public runThSelectFromFile() {
    }

    public static void main(String[] args) {


        runThSelectFromFile runthselect = new runThSelectFromFile();

        if(args != null && args.length > 0)
        {
            runthselect.sourceSQLPath = args[0];
            runthselect.queryType = args[1];
            runthselect.poolNumber = Integer.parseInt(args[2]);
            runthselect.repeatNumber = Integer.parseInt(args[3]);

        }
        else
        {
            runthselect.sourceSQLPath = "/usr/mysql/tempdata/query/";
//            runthselect.sourceSQLPath = "/mnt/d/work/mysql/engagements/buongiorno";

            runthselect.queryType = "select";
            runthselect.poolNumber = 41;
            runthselect.repeatNumber = 100;
        }

        runthselect.startPool();

    }


   public void startPool(){

   for(int i=0; i < poolNumber; i++) {


            System.out.println("Starting :  " + i);
            RunnableQuery qth = new RunnableQuery();
            qth.setID(i);
            qth.setJdbcUrl(connUrl);

            if(i < 10)
            {
                file = new File(sourceSQLPath + "/" + queryType + "_00" + i); //+ ".sql");
            }
            else if(i > 10 && i < 100)
            {
                file = new File(sourceSQLPath + "/" + queryType + "_0" + i); //+ ".sql");
            }
            else{
                file = new File(sourceSQLPath + "/" + queryType + "_" + i); //+ ".sql");
            }

            if(!file.exists())
            {
                System.out.println("File Not Found = " + file.getAbsolutePath());

                continue;

            }
            qth.setQueriesFileIn(file);
            qth.setRepeatNumber(repeatNumber);
            qth.setDoLog(false);

            Thread th = new Thread((Runnable) qth );
            th.start();
    }
}

    public void setSleepFor(int sleepFor) {
        this.sleepFor = sleepFor;
    }

    public int getSleepFor() {
        return sleepFor;
    }

}
