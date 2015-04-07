package net.tc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.beans.*;
import java.lang.reflect.*;

import org.apache.commons.beanutils.BeanUtils;

import java.sql.*;
import java.sql.Date;
import java.io.*;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.RenderedImage;
import java.awt.RenderingHints;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.ObjectOutputStream;

import com.mysql.stresstool.StressTool;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

 public  class  Utility
{
    public Utility()
    {
    }

    public ArrayList getListAllFiles(String path)
    {
        File ff = new File(path);
        File[] ffList = ff.listFiles();
//        FaostatPersister.getLogSystem().info("--------------------------");
        ArrayList filesPath = new ArrayList();
        for (int x = 0; x < ffList.length; x++)
        {
            File f = ffList[x];
            filesPath.add(f.getPath());
            if(f.isDirectory())
            {
                ArrayList arF = getListAllFiles(f.getPath());
                filesPath.addAll(arF);
            }
        }

//        FaostatPersister.getLogSystem().info("--------------------------");

        return filesPath;
    }
    public ArrayList getAllFiles(String path,ArrayList files)
        {
            File ff = new File(path);
            File[] ffList = ff.listFiles();
            for (int x = 0; x < ffList.length; x++)
            {
                File f = ffList[x];
                if(f.isFile() && f.exists() && f.length() > 0)
                    files.add(f.getPath());
                if(f.isDirectory())
                    files = getAllFiles(f.getPath(), files);
            }

            return files;
        }




    public static boolean checkFilePath(String path)
    {
        if(path == null || path.length()<1)
            return false;
        java.io.File file = new java.io.File(path);
        if(file.exists())
            return true;
        file.mkdir();
            return true;

    }

    public static String returnFormatExtention(int i)
    {
        switch (i)
        {
            case 1:
                return "bmp";
            case 2:
                return "jpg";
            case 3:
                return "tif";
        }
        return null;

    }
    public static String getYear()
    {
        GregorianCalendar calendar = new GregorianCalendar();
        String year = new Integer(calendar.get(GregorianCalendar.YEAR)).toString();
        return year;
    }

    public static String getHour()
    {
        GregorianCalendar calendar = new GregorianCalendar();
        String hour = new Integer(calendar.get(GregorianCalendar.HOUR_OF_DAY)).toString();
        if(hour.length() ==1) hour = "0" + hour;
        return hour;
    }
    
    public static String getMinute()
    {
        GregorianCalendar calendar = new GregorianCalendar();
        String minute = new Integer(calendar.get(GregorianCalendar.MINUTE)).toString();
        if(minute.length() ==1) minute = "0" + minute; 
        return minute;
    }
    
    public static String getSecond()
    {
        GregorianCalendar calendar = new GregorianCalendar();
        String second = new Integer(calendar.get(GregorianCalendar.SECOND)).toString();
        if(second.length() ==1) second = "0" + second; 
        return second;
    }
    
    public static String getTimestamp(){
    	String timeStamp;
    	timeStamp = Utility.getYear() + "_" + Utility.getMonthNumber() + "_" + Utility.getDayNumber()  
    	+ "_" + Utility.getHour() + "_" + Utility.getMinute() + "_" + Utility.getSecond(); 
    	return timeStamp;
    	
    }

    public static String getTimestampForStats(){
    	String timeStamp;
    	timeStamp = Utility.getYear() + "." + Utility.getMonthNumber() + "." + Utility.getDayNumber()  
    	+ ":" + Utility.getHour() + ":" + Utility.getMinute(); 
    	return timeStamp;
    	
    }
    /**
     * This function return the number of day existing in the given year and month
     * @param year
     * @param month
     * @return
     */
    public static int getDaysInMonth(int year,int month){
	int daysInMonth =1;
	
	if (month == 4 || month == 6 || month == 9 || month == 11)

	    daysInMonth = 30;

	    else 

	    if (month == 2) 

	    daysInMonth = (isLeapYear(year)) ? 29 : 28;

	    else 

	    daysInMonth = 31;	
	
	return daysInMonth;
    }
    
    public static boolean isLeapYear(int year) {
	  Calendar cal = Calendar.getInstance();
	  cal.set(Calendar.YEAR, year);
	  return cal.getActualMaximum(cal.DAY_OF_YEAR) > 365;
    }
    
    /**
     * Retun a date in string format starting from a given date
     * @param year
     * @param month
     * @param day
     * @param numberOfDays
     * @return
     */
    public static String getDateFromDaysInterval(int year, int month, int day, int numberOfDays){
	String finalDate = null;
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	cal.set(year, month, day);
	cal.add(GregorianCalendar.DATE, numberOfDays);
	
	
	String sMonth = (String)((cal.get(Calendar.MONTH)<10)?0+Integer.toString(cal.get(Calendar.MONTH)):Integer.toString(cal.get(Calendar.MONTH)));
	String sDay = (String)((cal.get(Calendar.DAY_OF_MONTH)<10)?0+Integer.toString(cal.get(Calendar.DAY_OF_MONTH)):Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
	
	if(cal.get(Calendar.DAY_OF_MONTH) > getDaysInMonth(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH))){
	    sDay = Integer.toString(getDaysInMonth(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) ));
	}
	
	finalDate = cal.get(Calendar.YEAR)+ "-"+ sMonth + "-" + sDay;
	return finalDate;
	
    } 
    public static String[] getOrderedDateFromDaysInterval(int year, int month, int day, int interval){
    	String[] strDates = new String[2] ;
    	String tmpDate = getDateFromDaysInterval(year,month,day,StressTool.getNumberFromRandom(interval).intValue());
    	String tmpDate2 = getDateFromDaysInterval(year,month,day,StressTool.getNumberFromRandom(interval).intValue());
    	
    	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    	    java.util.Date date1=null;
    	    java.util.Date date2=null;
			try {
				date1 = format.parse(tmpDate);
				date2 = format.parse(tmpDate2);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
    	    if (date1.compareTo(date2) <= 0) {
    	    	strDates[0]=tmpDate;
    	    	strDates[1]=tmpDate2;
    	    	return strDates; 
    	    }
    	    else{
    	    	strDates[1]=tmpDate;
    	    	strDates[0]=tmpDate2;
    	    	return strDates; 
    	    	
    	    }
    	
    }
    public static String getMonth()
    {
        Calendar calendar = new GregorianCalendar();
        int m = calendar.get(GregorianCalendar.MONTH);
        switch (m)
        {
            case 0:return "Jenuary";
            case 1:return "February";
            case 2:return "March";
            case 3:return "April";
            case 4:return "May";
            case 5:return "June";
            case 6:return "July";
            case 7:return "August";
            case 8:return "September";
            case 9:return "October";
            case 10:return "November";
            case 11:return "December";
        }
        return null;
    }
    public static String formatTime(long nanoSeconds)
    {
        int hours, minutes, remainder, totalSecondsNoFraction;
        double totalSeconds, seconds;


        // Calculating hours, minutes and seconds
        totalSeconds = (double) nanoSeconds / 1000000000.0;
        String s = Double.toString(totalSeconds);
        String [] arr = s.split("\\.");
        totalSecondsNoFraction = Integer.parseInt(arr[0]);
        hours = totalSecondsNoFraction / 3600;
        remainder = totalSecondsNoFraction % 3600;
        minutes = remainder / 60;
        seconds = remainder % 60;
        if(arr[1].contains("E")) seconds = Double.parseDouble("." + arr[1]);
        else seconds += Double.parseDouble("." + arr[1]);


        // Formatting the string that conatins hours, minutes and seconds
        StringBuilder result = new StringBuilder(".");
        String sep = "", nextSep = " and ";
        if(seconds > 0)
        {
            result.insert(0, " seconds").insert(0, seconds);
            sep = nextSep;
            nextSep = ", ";
        }
        if(minutes > 0)
        {
            if(minutes > 1) result.insert(0, sep).insert(0, " minutes").insert(0, minutes);
            else result.insert(0, sep).insert(0, " minute").insert(0, minutes);
            sep = nextSep;
            nextSep = ", ";
        }
        if(hours > 0)
        {
            if(hours > 1) result.insert(0, sep).insert(0, " hours").insert(0, hours);
            else result.insert(0, sep).insert(0, " hour").insert(0, hours);
        }
        return result.toString();
    }
    
    public boolean deleteFile(String[] filesName, String objectPath)
    {
        for (int i = 0 ; i < filesName.length ; i++ )
        {
            deleteFile(filesName[i],objectPath);
        }
        return true;
    }
    public boolean deleteFile(String fileName, String objectPath)
    {
        java.io.File ptPathTocheck = new java.io.File(objectPath + "/" + fileName);
        if ( (ptPathTocheck.exists()))
        {
            try
            {
                ptPathTocheck.delete();
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return true;
    }
    public boolean createBackupCopy(String fileName, boolean deleteOriginal)
    {
        java.io.File ptPathTocheck = new java.io.File(fileName);
        if ( (ptPathTocheck.exists()))
        {
            try
            {
                copyFile(fileName, ptPathTocheck.getPath(), "bck_" +
                         this.getDayNumber() + "_" +
                         this.getMonthNumber() + "_" +
                         this.getYear() +
                         ptPathTocheck.getName());
                if(deleteOriginal)
                    deleteFile(fileName);
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return true;

    }
    public boolean deleteFile(String fileName)
    {
        java.io.File ptPathTocheck = new java.io.File(fileName);
        if ( (ptPathTocheck.exists()))
        {
            try
            {
                ptPathTocheck.delete();
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return true;
    }

    public static String getMonthNumber()
  {
      Calendar calendar = new GregorianCalendar();
      int m = calendar.get(GregorianCalendar.MONTH);
      String month = new Integer(m + 1).toString();
      if(month.length()<2)
          month = "0"+month;

      return  month;
  }
  public static String getDayNumber()
  {
    Calendar calendar = new GregorianCalendar();
    int d = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    if(d == 0)
	 d = 1;
    
    String day = new Integer(d).toString();
    if(day.length()<2)
        day = "0"+day;
    return day;
  }

  public boolean copyFile(String source, String destinationPath, String fileDestName)
  {
      try
      {

          if (checkFilePath(source) && checkPath(destinationPath))
          {
              java.io.File fileDest = new java.io.File(destinationPath + fileDestName);
              java.io.File file = new java.io.File(source);
              FileOutputStream fw = new FileOutputStream(fileDest);
              FileInputStream fr = new FileInputStream(file);
              int size = fr.available();
              int i = 0;
              byte[] b = new byte[size];
              do{
                  i = fr.read(b);
                  if(i > -1)
                      fw.write(b);
              }while(i > -1);
              fw.flush();
              fw.close();
              fr.close();


          }
          return true;
      }
      catch (IOException ex)
      {return false;
      }

  }


  public boolean checkPath(String objectPath)
  {
     try{
         java.io.File ptPathTocheck = new java.io.File(objectPath);
         if (! (ptPathTocheck.exists()))
             ptPathTocheck.mkdirs();
         return true;
     }catch(Exception ex){return false;}
  }

//  public void sendFile(String filename, HttpServletRequest req, HttpServletResponse res)
//      throws ServletException
//  {
//      try
//      {
//          byte b[] = new byte[4096];
//
//          File f = new File(filename);
//          BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
//          ServletOutputStream out = res.getOutputStream();
//
//          int size = 0;
//          while (true)
//          {
//              size = in.read(b, 0, 4096);
//              if (size == -1)
//                  break;
//              out.write(b, 0, size);
//          }
//
//          out.flush();
//          out.close();
//          in.close();
//      }
//      catch (Exception e)
//      {
//          throw new ServletException(e.getMessage());
//      }
//  }

//  public void sendFileStream(File file, HttpServletRequest req, HttpServletResponse res)
//      throws ServletException
//  {
//      try
//      {
//          byte b[] = new byte[4096];
//
//          if(file !=null && file.exists())
//          {
//              ServletOutputStream out = res.getOutputStream();
//              BufferedInputStream inF = new BufferedInputStream(new FileInputStream(file),4096);
//              BufferedOutputStream outF = new BufferedOutputStream(out,4096);
//              int i = 0;
//              do
//              {
//                  i = inF.read(b);
//                  if (i > -1)
//                      outF.write(b);
//              }
//              while (i > -1);
//              out.flush();
//              out.close();
//          }
//
//      }
//      catch (Exception e)
//      {
//          e.printStackTrace();
//      }
//  }
//
}

