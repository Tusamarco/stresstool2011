package com.mysql.stresstool;

import java.io.*;
import java.sql.*;

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
public class parseSql {
    static File file = new File(
            "//usr/mysql/tempdata/select_from_allqueries.log");
    static File fileW = new File(
            "//usr/mysql/tempdata/query_out.txt");

    static FileOutputStream fos = null;
    static DataOutputStream dos = null;

    static PrintWriter outb = null;
    static FileWriter fileWr = null;
    static FileInputStream fis = null;

    static BufferedInputStream bis = null;
//    BufferedReader dis
    static DataInputStream dis = null;

    public parseSql() {
    }


    public static void main(String[] args) {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

        init();

    }

    public static void openBuffer() {
        try {
            if (file != null) {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
//                BufferedReader dis= new BufferedReader(new InputStreamReader(fis));
                dis = new DataInputStream(bis);

                outb = new PrintWriter(fileW);

            }
        } catch (Exception ex) {
            ex.printStackTrace(
                    );
        }
    }

    @SuppressWarnings("deprecation")
	public static String getFileLine(DataInputStream dis) {

        try {
            // dis.available() returns 0 if the file does not have more lines.
            String toreturn = dis.readLine();
            if (!toreturn.equals("SELECT 1;"))
                return toreturn;

            return null;
        } catch (Exception ex) {

        }
        return null;
    }

    public static void closeFiles() {
        // dispose all the resources after using them.
        try {
            fis.close();
            bis.close();
            dis.close();
            outb.close();
        } catch (Exception ex) {
        }
    }

    public static void init() {
        int loopi = 500;
        int threadi = 10;

        Connection conn = null;
        try {
//             conn =
//                    DriverManager.getConnection("jdbc:mysql://138.132.158.219:6610/udb?" +
//                                                "user=root&password=root");

//             conn =
//                    DriverManager.getConnection("jdbc:mysql://138.132.171.68:3350/udb?" +
//                                                "user=root&password=root");

//             conn =138.132.158.219
//                    DriverManager.getConnection("jdbc:mysql://127.0.0.1:6610/UDB?" +
//                                                "user=root&password=root");

            conn =
                    DriverManager.getConnection(
                            "jdbc:mysql://10.1.4.44:3301/jmailer_it?" +
                            "user=test&password=test");

            Statement stmt = null;
            ResultSet rs = null;
            conn.setAutoCommit(true);
            stmt = conn.createStatement();

            int line = 0;

            try {
                openBuffer();
                while (dis.available() != 0) {

                    String toRun = getFileLine(dis);
                    if (toRun == null) {
                        line++;
                        continue;
                    }
//                     Thread.sleep(threadi);
                    try {
                        rs = stmt.executeQuery(toRun);
//                rs = stmt.executeQuery(" select * from  udb_usr_profile_t limit 10;");

                        int size = 0;
                        if (rs != null) {
                            System.out.println("Query at line " + line++ + " OK ");

                        }
                    } catch (SQLException sqle) {
                        System.out.println("Error in line=" + line + " " +
                                           sqle.getMessage());
                        outb.println("Error in line=" + line + " " + sqle.getMessage());
                        outb.flush();

                    }
                    // this statement reads the line from the file and print it to
                    // the console.


                }
                closeFiles();
                System.exit(0);
            } catch (Exception eex) {
                eex.printStackTrace(
                        );
            }

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
