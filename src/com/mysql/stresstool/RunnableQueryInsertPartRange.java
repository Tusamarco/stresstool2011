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
public  class RunnableQueryInsertPartRange extends RunnableQueryInsertBasic{

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

						insert1Str ="\"" + (this.isDoSimplePk()?longtextFld.substring(0, 6):longtextFld.substring(0, 99)) + "\",";
						insert1Str = insert1Str  + "\"" + (this.isDoSimplePk()?longtextFld.substring(0, 10):longtextFld.substring(0, 199)) + "\",";
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
							setLazyInsert2("\"" + longtextFld + "\")");
						}
						else{
							setLazyInsert2("\"" + longtextFld + "\") ON DUPLICATE KEY UPDATE partitionid=0");
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
	
}
