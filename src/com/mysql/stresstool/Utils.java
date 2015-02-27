package com.mysql.stresstool;

public class Utils {
	
 public static boolean isNumeric(String str)  
{  
	 try  
	 {  
	    int i = Integer.parseInt(str);  
	}  
	catch(NumberFormatException nfe)  
	{  
		return false;  
	}  
	return true;  
}  
 public static boolean isDouble(String str)  
 {  
 	 try  
 	 {  
 	    double i = Double.parseDouble(str);  
 	}  
 	catch(NumberFormatException nfe)  
 	{  
 		return false;  
 	}  
 	return true;  
 }  
 
}
