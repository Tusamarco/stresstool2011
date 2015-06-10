package net.tc.utils.file;

import java.io.*;
import java.util.*;

import net.tc.utils.SynchronizedMap;
import java.lang.ref.SoftReference;

import com.mysql.stresstool.Utils;

public class FileHandler implements FileDataReader, FileDataWriter
{
    private File in = null;
    private File out = null;
    private byte b[] = new byte[4096];
    String outPath = null;
    boolean filecreate = false;
    long fileSize = 0;
    private InputStream ins = null;
    private BufferedReader dataReader = null;
    private boolean hasHeader = false;
    Vector headersL = new Vector(0);
    FileWriter fw ;
  

    public FileHandler()
        throws FileNotFoundException
    {}

    public FileHandler(File in, File out)
        throws FileNotFoundException
    {
        this.in = in;
        this.out = out;
        setSize(in.length());
    }
    public FileHandler(String filePath)
    {
        SoftReference sf = new SoftReference(new File(filePath));
         this.in = (File)sf.get();

        if(this.in == null)
        {
            new FileNotFoundException();
            return;
        }
        this.out = null;
        setSize(in.length());
        if(!in.exists())
        {
            try{
                String separator = "/";
                if(in.getAbsoluteFile().pathSeparator.endsWith(";"))
                    separator = "\\";
                String aT = in.getAbsolutePath().substring(in.getAbsolutePath().
                        lastIndexOf(separator), in.getAbsolutePath().length());
                if (aT.indexOf(".") > 0) {
                    in.mkdirs();
                    if(in.isDirectory())
                        in.delete();
                } else
                    in.mkdirs();
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }

        }
        try {
			fw = new FileWriter( in );
		} catch (IOException e) {

			e.printStackTrace();
		}
    }

    public FileHandler(File in, File out, String outPath, boolean filecreate)
        throws FileNotFoundException
    {
        in = in;
        out = out;
        outPath = outPath;
        filecreate = filecreate;
    }
    public boolean existIn(){
	return in.exists();
    }

    public boolean existOut(){
	return out.exists();
    }

    
    public FileHandler(String inS, String outS, String outPath, boolean filecreate)
    throws FileNotFoundException
	{
    	if(inS != null && !inS.equals("")){
    		in = new File(inS);
    	}
    	if(outS != null && !outS.equals("")){
    		out = new File(outPath + outS);
    	}
	    outPath = outPath;
	    filecreate = filecreate;
	    if(filecreate )
			try {
				out.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else{
	    	if(!out.canWrite() && !out.canRead()){
				try {
					out.createNewFile();
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}

	    		
	    		try {
					throw new Exception("File " + out.getCanonicalPath() + " Name " + out.getName() + " now rw;") ;
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    }
	}

    public void setOut(File out)
    {
        this.out = out;
    }

    public void setIn(File in)
    {
        this.in = in;
        this.setSize(in.length());
    }

    public void setIns( InputStream ins )
    {
        this.ins = ins ;
    }

    public static ArrayList getAllFiles(String path,ArrayList files)
    {
        return getAllFiles(path,files, true);
    }
    public static ArrayList getAllFiles(String path, ArrayList files, boolean recursive)
    {
        return     getAllFiles(path, files, recursive , "*");
    }

    public static ArrayList getAllFiles(String path, ArrayList files, boolean recursive , String filter)
    {
        return     getAllFiles(path, files, recursive , "*", true);
    }

    public static ArrayList getAllFiles(String path, ArrayList files, boolean recursive , String filter, boolean casesensitive)
    {

        fileNameFilter fnf = new fileNameFilter(filter, casesensitive);
        File ff = new File(path);
        File[] ffList = ff.listFiles(fnf);
        for (int x = 0; x < ffList.length; x++)
        {
            File f = ffList[x];
            if (f.isFile() && f.exists() && f.length() > 0)
                files.add(f.getPath());
            if (f.isDirectory() && recursive)
                files = getAllFiles(f.getPath(), files, recursive, filter);
        }

        return files;
    }

    public static boolean copyFile(String source, String destinationPath, String fileDestName)
        throws FileNotFoundException, IOException
    {
            if (checkFilePath(source) && checkPath(destinationPath, true) != null)
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

    public boolean copyFile()
    {
        try{
            if ( in != null && in.exists() && out != null )
            {
                if ( out.exists() )
                    out.delete() ;
                FileOutputStream fw = new FileOutputStream( out ) ;
                FileInputStream fr = new FileInputStream( in ) ;
                int size = fr.available() ;
                int i = 0 ;
                byte[] b = new byte[size] ;
                do
                {
                    i = fr.read( b ) ;
                    if ( i > -1 )
                        fw.write( b ) ;
                }
                while ( i > -1 ) ;
                fw.flush() ;
                fw.close() ;
                fr.close() ;

            }
            return true ;
        }
        catch(FileNotFoundException ex){return false;}
        catch(IOException ex){return false;}

    }
    public String readXmlString()
    {
        if(this.in == null || !this.in.exists() || this.in.isDirectory())
            return null;

        StringWriter sw = new StringWriter();

        if(dataReader == null)
            this.readInitialize(false);

        String line = null;
        do {
            try {

                if ((line = dataReader.readLine()) != null)
                    sw.write(new String(line.getBytes(),"UTF-8"));
                else
                    return sw.toString();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } while (line != null);


        return sw.toString();
    }

    public boolean writeToFile(String fr)
    {


        try{
            if ( in != null)// && in.exists())
            {

                FileWriter fw = new FileWriter( in ) ;
                fw.write(fr);

//                BufferedReader inb = new BufferedReader(fr);
//
//                FileWriter fw = new FileWriter( in ) ;
//
//                int size = fr..available() ;
//                int i = 0 ;
//                char[] b = new char[size] ;
//                do
//                {
//                    i = inb.read( b ) ;
//                    if ( i > -1 )
//                        fw.write( b ) ;
//                }
//                while ( i > -1 ) ;
                fw.flush() ;
                fw.close() ;
//                fr.close() ;

            }
            return true ;
        }
        catch(FileNotFoundException ex){return false;}
        catch(IOException ex){return false;}

    }

    public boolean appendToFile(String fr)
    {


        try{
            if ( out != null)// && in.exists())
            {
            	
            	FileOutputStream outS = new FileOutputStream(out,true); 
            	ByteArrayInputStream inb = new ByteArrayInputStream(fr.getBytes());
            	int size = fr.length();
                int i = 0;
                byte[] b = new byte[size];
                do{
                	i = inb.read(b) ;
                    if(i > -1)
                    	outS.write(b);
                }while(i > -1);
                outS.flush();
                outS.close();
                
//                BufferedReader inb = new BufferedReader(fr);
//
//                FileWriter fw = new FileWriter( in ) ;
//
//                int size = fr..available() ;
//                int i = 0 ;
//                char[] b = new char[size] ;
//                do
//                {
//                    i = inb.read( b ) ;
//                    if ( i > -1 )
//                        fw.write( b ) ;
//                }
//                while ( i > -1 ) ;
//                this.fw.flush() ;
                
//                fw.close() ;
//                fr.close() ;

            }
            return true ;
        }
        catch(FileNotFoundException ex){return false;}
        catch(IOException ex){return false;}

    }

    public static File checkPath(String objectPath)
    {
        return checkPath(objectPath, false);
    }
    public static boolean checkFilePath(String path)
    {
        if (path == null || path.length() < 1)
            return false;
        java.io.File file = new java.io.File(path);
        return file.exists();
    }

    public static File checkPath(String objectPath, boolean create)
    {
       try{
           java.io.File ptPathTocheck = new java.io.File(objectPath);
           if(ptPathTocheck.exists())
               return ptPathTocheck;

           if (! (ptPathTocheck.exists()) && create)
           {
               ptPathTocheck.mkdirs();
               return ptPathTocheck;
           }
           return null;
       }catch(Exception ex)
       {
           ex.printStackTrace();
           return null;}
    }
    public File getIn()
    {
        return in;
    }
    public File getOut()
    {
        return out;
    }
    public long getSize()
    {
        return fileSize;
    }
    public void setSize(long fileSize)
    {
        this.fileSize = fileSize;
    }

    public boolean deleteFile()
    {
        if(in != null && in.exists())
        {
            try{
                if(!in.delete())
                 {   in.deleteOnExit();
                     return false;}
            }catch(Exception ex)
            {
                try{
                    in.deleteOnExit();
                    return true;
                }
                catch(Exception eex){ return false;}
            }

        }
        return false;
    }
    public boolean copyFileStream()
    {
        try{
            if ( ins != null && out != null )
            {
                if ( out.exists() )
                    out.delete() ;
                FileOutputStream fw = new FileOutputStream( out ) ;

                int size = ins.available() ;
                if(size ==0 )
                    size = 4;
                int i = 0 ;
                byte[] b = new byte[size] ;
                do
                {
                    i = ins.read( b ) ;
                    if ( i > -1 )
                        fw.write( b ) ;
                }
                while ( i > -1 ) ;
                fw.flush() ;
                fw.close() ;
                ins.close() ;

            }
            return true ;
        }
        catch(FileNotFoundException ex){return false;}
        catch(IOException ex){return false;}

    }
    public void close()
    {
        in = null;
        out = null;
        try
        {
            ins.close() ;
        }
        catch ( IOException ex )
        {
        }
        ins = null;
    }
    public void setHeader(Vector header)
    {
    	String headerString = "";
    	
    	for(int i = 0 ; i <= header.size(); i++){
    		if(headerString.length() > 0)
    			headerString = headerString +","+ (String)header.get(i);
    		else 
    			headerString = (String)header.get(i);
    	}
    	this.writeToFile(headerString);
    	this.headersL = header;
    }
    
    public void readInitialize(boolean hasHeader)
    {
        try {
            // create the object File
//            dataReader = new RandomAccessFile(this.getIn(),"r");
            FileInputStream fs = new FileInputStream(getIn());
            InputStreamReader ir = new InputStreamReader(fs,"UTF-8");
            dataReader = new BufferedReader(ir);

            if(hasHeader)
            {
                String line = null;
                if((line = dataReader.readLine()) != null)
                {
                    if(line != null)
                    {
                        String[] headers =  new String(line.getBytes(), "UTF-8").split(",");
                        for(int i = 0 ; i <= headers.length; i++)
                        {
	                        if(headers != null)
	                            headersL.add(headers[i]);
                        }
                    }

                }


            }

        }catch(Exception ex)
        {
            ex.printStackTrace();
            try {
                dataReader.close();
            } catch (IOException ex1) {
            }

        }


    }
    public Map getRowAsMap()
    {
        String line = null;
        try {

            if((line = dataReader.readLine()) != null)
                return getLineasMap(new String(line.getBytes(), "UTF-8"));
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }


        return null;
    }

    /**
     * getLineasMap
     *
     * @param line String
     * @return Map
     */
    private Map getLineasMap(String line)
    {
    	if(line != null && headersL != null)
    	{
    		Map record = new SynchronizedMap(0);
    		String[] values = line.replaceAll(",,",", ,").split(",");

    		Iterator it = headersL.iterator();
    		if(headersL.size() == values.length )
    		{
    			int i =0;
    			while(it.hasNext())
    			{
    				record.put(it.next(),values[i++]);
    			}

    			return record;
    		}
    	}
    	return null;
    }

    public String getRowAsString()
    {
        String line = null;
        try {

            if((line = dataReader.readLine()) != null)
                return new String(line.getBytes(), "UTF-8");
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }


        return null;
    }
    public Map getAllRowsAsMap()
    {

        Map recordSet = new SynchronizedMap(0);
        Map singleRecord = null;
        int ir = 1;
        do{
            String line = null;
            try {

                if ((line = dataReader.readLine()) != null)
                    singleRecord = getLineasMap(new String(line.getBytes(), "UTF-8"));
                else
                    return recordSet;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                singleRecord = null;
            }
            recordSet.put(new Long(ir++),singleRecord);

        }while(singleRecord != null);

        return recordSet;
    }
    public Vector getHeaders()
    {
        return headersL;
    }

	@Override
	public void setHeaders(Vector headers) {
		if (headers != null)
			this.headersL = headers;
		
	}

	@Override
	public void setRecordData(SynchronizedMap record) {
		if(this.headersL == null || headersL.size()==0)
		{
		
			Object[] ar = record.getKeyasOrderedArray();
			for(int i = 0 ; i<ar.length ; i++){
				this.headersL.add( (String) ar[i]);
				
			}
		}
		
		String line ="";
		for(int i =0 ; i< headersL.size(); i++)
		{
			String value ="";
			if(record.get(headersL.get(i)) instanceof Long ){
				value = ((Long)record.get(headersL.get(i))).toString();
			}
			else if(record.get(headersL.get(i)) instanceof Integer ){
				value = ((Integer)record.get(headersL.get(i))).toString();
			}
			else if(record.get(headersL.get(i)) instanceof Double ){
				value = ((Double)record.get(headersL.get(i))).toString();
			}
			else{
				value = (String)(record.get(headersL.get(i)));
			}
			if(line.length() > 0)
				line = line + "," + value;
			else
				line = value;
		}
		
		this.appendToFile(line + "\n");
		
	}

	@Override
	public void printHeaders(SynchronizedMap record) {
		if(this.headersL == null || headersL.size()==0)
		{
		
			Object[] ar = record.getKeyasOrderedArray();
			for(int i = 0 ; i<ar.length ; i++){
				this.headersL.add( (String) ar[i]);
				
			}
		}
		
		String line ="";
		for(int i =0 ; i< headersL.size(); i++)
		{
			String value ="";
			value = (String)headersL.get(i);
			if(line.length() > 0)
				line = line + "," + value;
			else
				line = value;
		}
		
		this.appendToFile(line + "\n");
		
	}

	
	@Override
	public void setRecordDataCSV(String record) {
		// TODO Auto-generated method stub
		
	}

	public boolean isHasHeaders()
	{
			return this.hasHeader;
	}

	public void setHasHeaders(boolean hasHeaders){
		this.hasHeader =  hasHeaders;
	}
	
}
