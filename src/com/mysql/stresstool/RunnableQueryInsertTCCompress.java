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
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.UUID;

import net.tc.utils.Utility;

import org.apache.commons.beanutils.MethodUtils;


/**
 * <p>Title:test </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public  class RunnableQueryInsertTCCompress extends RunnableQueryInsertBasic{

	public void run(){
		super.run();
		
	}
	
	Vector getTablesValues(boolean refresh) {

		String longtextFld = "";
		boolean lazy = false;
		int afld = 0;
		long counterFld = 0 ;

		if(refresh && !getLazyInsert1().equals(""))
		{
			lazy = true;
			longtextFld = getLazyLongText();
		}
		else{
			if(isOperationShort())
				longtextFld = StressTool.getStringFromRandom(254).substring(0,240);
			else
				longtextFld = StressTool.getStringFromRandom(40000);
		}

		Vector v = new Vector();



		StringBuffer insert1 = new StringBuffer();
		StringBuffer insert2 = new StringBuffer();
		//        String uuid = UUID.randomUUID().toString();
		ArrayList <String> insertList1 = new ArrayList();
		ArrayList <String> insertList2 = new ArrayList();

		int pk = StressTool.getNumberFromRandom(2147483647).intValue();
		String insert1Str = "";


		if(getDbType().endsWith("MySQL"))
		{
			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){

				insert1.append("insert INTO tbtest" + iTable + " (uuid,date,a,b,c,counter,partitionid,strrecordtype) VALUES");

				if(this.isUseBatchInsert())
				{

					insert1Str = "";
					if(lazy){
						insert1Str = getLazyInsert1();

					}
					else{

						insert1Str ="compress(\"" + (this.isDoSimplePk()?longtextFld.substring(0, 6):longtextFld.substring(0, 99)) + "\"),";
						insert1Str = insert1Str  + "compress(\"" + (this.isDoSimplePk()?longtextFld.substring(0, 10):longtextFld.substring(0, 199)) + "\"),";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(2147483647) *   StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + "\"" + StressTool.getStringFromRandom(2) + "\"";
						insert1Str = insert1Str  + ")";
						setLazyInsert1(insert1Str);
					}


					for(int ibatch= 0 ; ibatch <=getIBatchInsert(); ibatch++ )
					{
					    	String thisDate = getRangeDate();

						//                    uuid = UUID.randomUUID().toString();
						pk = StressTool.getNumberFromRandom(2147483647).intValue();


						if (ibatch > 0){
							insert1.append(",(UUID(),'"+ thisDate + "'," + pk + ",");
						}
						else
						{	
							insert1.append("(UUID(),'"+ thisDate + "'," + pk + ",");
						}

						insert1.append(insert1Str);
					}
				}
				else
				{
				    	String thisDate = getRangeDate();
					insert1Str = "";
					insert1Str = insert1Str  + "(UUID(),'"+ thisDate + "'," + pk + ",";

					if(lazy){
						insert1Str = getLazyInsert1();

					}
					else{
						insert1Str = insert1Str  + "\"" + (this.isDoSimplePk()?longtextFld.substring(0, 6):longtextFld.substring(0, 99)) + "\",";
						insert1Str = insert1Str  + "\"" + (this.isDoSimplePk()?longtextFld.substring(0, 10):longtextFld.substring(0, 199)) + "\",";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(2147483647) *
						StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + StressTool.getNumberFromRandom(20) + ",";
						insert1Str = insert1Str  + "\"" + StressTool.getStringFromRandom(2) + "\"";
						insert1Str = insert1Str  + ")";
						setLazyInsert1(insert1Str);	                
					}

					insert1.append(insert1Str);
				}
				if(!insertList1.equals(""))  
					insertList1.add(insert1.toString());

				insert1.delete(0, insert1.length());
			}
		}
		if(!this.isDoSimplePk())
		{
			if(getDbType().endsWith("MySQL"))
			{
				String insert2Str = ""; 
				String insert2bStr = "";
				String thisDate = getRangeDate();
				
				for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
					insert2Str = "";
					
					insert2Str = insert2Str  +"insert INTO tbtest_child" + iTable + " (a,date,stroperation) VALUES(";
					insert2Str = insert2Str  + pk + ",'" + thisDate + "',";
					if(lazy && !getLazyInsert2().equals("")){
						insert2bStr = getLazyInsert2();
					}
					else{
						if(this.getOnDuplicateKey() == 0 ){
							setLazyInsert2(" compress(\"" + longtextFld + "\"))");
						}
						else{
							setLazyInsert2(" compress(\"" + longtextFld + "\")) ON DUPLICATE KEY UPDATE partitionid=0");
						}
						insert2bStr = getLazyInsert2();
						//	        		insert2Str = insert2Str  + "\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0";
						//	        		lazyInsert2 = insert2Str;
					}

					if(!insert2Str.equals("")) 
						insertList2.add(insert2Str + insert2bStr);

				}

			}
		}


		v.add(0,insertList1);
		v.add(1,insertList2);
		//    v.add(2, new Integer(pk));

		return v;

	}
	@Override
	public boolean createSchema(StressTool sTool) {

		// Custom schema creation this is the default for the stresstool but can be anything  
		String DropTables1 = "Drop table IF EXISTS tbtest";
		String DropTables2 = "Drop table IF EXISTS tbtest_child";

		String TruncateTables1 = "Truncate table tbtest";
		String TruncateTables2 = "Truncate table tbtest_child";

		Connection conn =null;
		Statement stmt = null;

		try {
			if(getJdbcUrlMap().get("dbType") != null &&  !((String)getJdbcUrlMap().get("dbType")).equals("MySQL"))
			{
				conn=DriverManager.getConnection((String)getJdbcUrlMap().get("dbType"),"test", "test");
			}
			else
				conn= DriverManager.getConnection((String)getJdbcUrlMap().get("jdbcUrl"));

			conn.setAutoCommit(false);
			stmt = conn.createStatement();


			StringBuffer sb = new StringBuffer();

			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
				sb.append("CREATE TABLE IF NOT EXISTS tbtest" + iTable +"(");
				if (this.isUseAutoIncrement()){
					sb.append("`autoInc` bigint(11) AUTO_INCREMENT NOT NULL,");
				}
				sb.append(" `a` int(11) NOT NULL,"); 
				sb.append(" `uuid` char(36) NOT NULL,");
				sb.append(" `b` VARBINARY(100) NOT NULL,");
				sb.append(" `c` VARBINARY(200)  NOT NULL,");
				sb.append(" `counter` bigint(20) NULL, ");
				sb.append(" `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,");
				sb.append(" `partitionid` int NOT NULL DEFAULT 0,");
				sb.append(" `date` DATE NOT NULL,");
				sb.append(" `strrecordtype` char(3) NULL");
				if (this.isUseAutoIncrement()){
				    if(this.getPartitionType().equals("range")){
					sb.append(", PRIMARY KEY  (`autoInc`,`date`),  INDEX `IDX_a` (a),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
				    }
				    else{
					sb.append(", PRIMARY KEY  (`autoInc`,`partitionid`),  INDEX `IDX_a` (a),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
				    }
				}
				else{
					if(!isDoSimplePk())
					    if(getPartitionType().equals("range")){
					    	sb.append(", PRIMARY KEY  (`uuid`,`date`),  INDEX `IDX_a` (a), INDEX `IDX_date` (date) ");
					    }
					    else{
						sb.append(", PRIMARY KEY  (`uuid`,`partitionid`),  INDEX `IDX_a` (a), INDEX `IDX_date` (date)");
					    }
					else{ 
					    if(getPartitionType().equals("range")){
						sb.append(", PRIMARY KEY  (`a`,`date`),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
					    }
					    else{
						sb.append(", PRIMARY KEY  (`a`,`partitionid`),  INDEX `IDX_uuid` (uuid), INDEX `IDX_date` (date) ");
					    }
					}
				}
				sb.append(") ENGINE="+ sTool.tableEngine) ;

				if(!sb.toString().equals(""))
					stmt.addBatch(sb.toString());

				sb.delete(0, sb.length());
			}
			String tbts1 = sb.toString();


			sb = new StringBuffer();
			for(int iTable = 1 ; iTable <= this.getNumberOfSecondaryTables(); iTable++){
				sb.append("CREATE TABLE IF NOT EXISTS tbtest_child" + iTable);
				sb.append("(`a` int(11) NOT NULL,");
				sb.append("`bb` int(11) AUTO_INCREMENT NOT NULL,");
				sb.append(" `date` DATE NOT NULL,");
				sb.append(" `partitionid` int NOT NULL DEFAULT 0,");
				if(isOperationShort())
					sb.append(" `stroperation` VARCHAR(254)  NULL,");
				else
					sb.append(" `stroperation` BLOB(41845)  NULL,");

				sb.append(" `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP");
				sb.append(", PRIMARY KEY  (`a`,`bb`), UNIQUE(`bb`)");
				sb.append(") ENGINE="+ sTool.tableEngine);

				if(!sb.toString().equals(""))
					stmt.addBatch(sb.toString());

				sb.delete(0, sb.length());

			}
			String tbts2 = sb.toString();

			System.out.println(tbts1);
			if(!isDoSimplePk())
				System.out.println(tbts2);


			if(sTool.droptable)
			{
				System.out.println("****============================================================================*******");
				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait DROP table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute(DropTables1+iTable);
				}

				if(!isDoSimplePk()){
					for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
						System.out.println("**** Please wait DROP table tbtest_child" + iTable + " it could take a LOT of time *******");
						stmt.execute(DropTables2+iTable);
					}


				}
				stmt.execute("COMMIT");
				System.out.println("**** DROP finished *******");
				System.out.println("****============================================================================*******");

			}

			if(sTool.createtable)
				stmt.executeBatch();


			if(sTool.truncate)
			{
				System.out.println("****============================================================================*******");

				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait TRUNCATE table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute(TruncateTables1+iTable);
				}

				if(!isDoSimplePk()){
					for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
						System.out.println("**** Please wait TRUNCATE table tbtest_child" + iTable + " it could take a LOT of time *******");
						stmt.execute(TruncateTables2+iTable);
					}
				}
				System.out.println("**** TRUNCATE finish *******");
				System.out.println("****============================================================================*******");

			}


		} catch (Exception ex) {
			ex.printStackTrace(

			);
			return false;
		} finally {

			try {
				conn.close();
				return true;
			} catch (SQLException ex1) {
				ex1.printStackTrace();
				return false;
			}

		}		

	}
	
}
