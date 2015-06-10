package com.mysql.stresstool;

public class Utils {
	
 public static boolean isNumeric(String str)  
{  
	 try  
	 {  
	    long i = Long.parseLong(str);  
	}  
	catch(NumberFormatException nfe)  
	{  
		return false;  
	}  
	return true;  
}  
 public static boolean isNumeric(Object obj)  
{  
	 try  
	 {  
	    long i = new Long((Long) obj);  
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
