package com.mysql.stresstool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TimeTools {
    private static String dayFormat = "dd-MM-yyyy";
    private static String timeFormat = "HH:mm:ss";

    public static String getDayFormat()
    {
        return dayFormat;
    }
    public static String getTimeFormat()
    {
        return timeFormat;
    }

    public TimeTools() {
    }
    public static String GetFullDate(long systemTime)
    {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                getDayFormat() + "_" + getTimeFormat() );

        try {
            return sdf.format(systemTime);
        } catch (java.lang.NullPointerException npEx) {
            System.out.println(npEx);
            return "";
        }

    }

    public static String GetCurrentDay() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                getDayFormat());

        try {
            return sdf.format(java.util.Calendar.getInstance().getTime());
        } catch (java.lang.NullPointerException npEx) {
            System.out.println(npEx);
            return "";
        }
    }
    public static String GetCurrentTime() {
     java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat( getTimeFormat() );

     try
     {
         return sdf.format( java.util.Calendar.getInstance().getTime() );
     }
     catch( java.lang.NullPointerException npEx )
     {
         System.out.println(npEx);
         return "";
     }
 }

 public static String GetCurrent()
 {
     java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" );

     try
     {
         return sdf.format( java.util.Calendar.getInstance().getTime() );
     }
     catch( java.lang.NullPointerException npEx )
     {
         System.out.println(npEx);
         return "";
     }


 }
 public static Date getDate(String date, String format)
 {

     DateFormat df = new SimpleDateFormat(format);

       try
       {
           Date dateR = df.parse(date);
           return dateR;
       } catch (ParseException e)
       {
           e.printStackTrace();
       }


     return null;
 }

 public static String getYear()
 {
     GregorianCalendar calendar = new GregorianCalendar();
     String year = new Integer(calendar.get(GregorianCalendar.YEAR)).toString();
     return year;
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
    public static String getDayNumber()
    {
      Calendar calendar = new GregorianCalendar();
      int d = calendar.get(GregorianCalendar.DAY_OF_MONTH);
      String day = new Integer(d + 1).toString();
      if(day.length()<2)
          day = "0"+day;
      return day;
  }
}
