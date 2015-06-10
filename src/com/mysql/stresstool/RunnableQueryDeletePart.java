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

import java.io.BufferedReader;
import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.MethodUtils;

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
public  class RunnableQueryDeletePart extends RunnableQueryDeleteBasic{
       
    /* (non-Javadoc)
	 * @see com.mysql.stresstool.RunnableQueryDeleteInterface#run()
	 */
    public void run() {
    		super.run();
 
    }
    
    /**
    * This method execute the delete action whatever it will be, and return a Vector with objects
    * the following values
    * @return
    * V1 time taken in system time
    * V2 value of the delete key minor
    * V3 value of the delete key major
    * @throws SQLException 
    */
    @Override
	public
       Vector<Long> executeDeleteActions(Connection conn) throws SQLException{
       	
       		long execTime = 0;
   			long pkStart = 0;
   			long pkEnds = 0;

       		Vector vReturn = new Vector();
       		
       		long timeStart = System.currentTimeMillis();
       		Statement stmt = conn.createStatement();
       		stmt.execute("BEGIN");


       		for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){

       			ResultSet rsToDelete = stmt.executeQuery("Select max(a),min(a) from tbtest"+iTable);
       			rsToDelete.next();

       			long maxDelete = rsToDelete.getLong(1);
       			long minDelete = rsToDelete.getLong(2);
       			pkStart = rsToDelete.getLong(1);
       			pkEnds = rsToDelete.getLong(2);

       			rsToDelete.close();

       			long maxToDelete =  new Double( ((double)this.getDeleterowmaxpct()*maxDelete) / 100).longValue();


       			PreparedStatement pstmt = null;
       			{
       				@SuppressWarnings("unchecked")
   					SoftReference sf = new SoftReference(conn.prepareStatement("DELETE FROM tbtest"+iTable + " where a between  ? and ?"));
       				pstmt = (PreparedStatement) sf.get();
       			}

       			if(maxDelete > 0 ){

       				for(long iCdelete = minDelete; iCdelete < maxToDelete; iCdelete += getDeleterowsinterval() ){
       					pstmt.setLong(1,iCdelete);
       					pstmt.setLong(2,iCdelete += getDeleterowsinterval());
       					int rows =  pstmt.executeUpdate();
       					if (rows > 0){
       						setTotalLineDeleted(getTotalLineDeleted()
       								+ rows);
       					}

       					if(getTotalLineDeleted() >= maxToDelete){
       						break;
       					}
       					stmt.execute("COMMIT");
       				}

       			} 
       			stmt.execute("COMMIT");

       		}

       		if(!isDoSimplePk()){
       			for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
       				ResultSet rsToDelete = stmt.executeQuery("Select max(a),min(a) from tbtest_child"+iTable);
       				rsToDelete.next();

       				long maxDelete = rsToDelete.getLong(1);
       				long minDelete = rsToDelete.getLong(2);
       				long maxToDelete =  new Double( ((double)this.getDeleterowmaxpct()*maxDelete) / 100).longValue();

       				rsToDelete.close();

       				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tbtest_child"+iTable + " where a between  ? and ?");

       				if(maxDelete > 0 ){

       					for(long iCdelete = minDelete; iCdelete < maxToDelete; iCdelete += getDeleterowsinterval() ){
       						pstmt.setLong(1,iCdelete);
       						pstmt.setLong(2,iCdelete += getDeleterowsinterval());
       						int rows =  pstmt.executeUpdate();

       						if (rows > 0){
       							setTotalLineDeleted(getTotalLineDeleted()
       									+ rows);
       						}

       						if(getTotalLineDeleted() >= maxToDelete){
       							break;
       						}
       						stmt.execute("COMMIT");
       					}

       				} 
       				stmt.execute("COMMIT");
       			}
       		}

       		long timeEnds = System.currentTimeMillis();
       		execTime = (timeEnds - timeStart);
       		vReturn.add(new Long(execTime));
       		vReturn.add(new Long(pkStart));
       		vReturn.add(new Long(pkEnds));
       		
       		return vReturn; 
       	
       }
    
}
