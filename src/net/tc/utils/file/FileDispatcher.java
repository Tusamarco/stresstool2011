package net.tc.utils.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileDispatcher extends FileHandler
{
    private OutputStream outS = null;
    private byte b[] = new byte[4096];

    public void setOut(OutputStream outS)
    {
        this.outS = outS;
    }

    public void setIn(File in)
    {
        super.setIn(in);
    }

    public void sendOutStream()
        throws FileNotFoundException, IOException
    {
        if(getIn() == null && outS == null)
            return;

        if(!getIn().exists())
            return;


        BufferedInputStream inb = new BufferdFileInputStream(getIn()).getBufferedInputStream();
        BufferedOutputStream outb = new BufferedOutputStream(outS , b.length);

        int size = 0;
        try
        {
            while (true)
            {
                size = inb.read(b, 0, 4096);
                if (size == -1)
                    break;
                outb.write(b, 0, size);
            }

            outb.flush();
            outb.close();
            inb.close();
        }
        catch(Exception eex)
        {
//                 outb.flush();
//                 outb.close();
            inb.close();
        }


    }
//    public BufferedInputStream sendInStream()
//        throws FileNotFoundException, IOException
//    {
//        if(getIn() == null)
//            return null;
//
//        if(!getIn().exists())
//            return null;
//
//
//        BufferedInputStream inb = new BufferdFileInputStream(getIn()).getBufferedInputStream();
//        return inb;
//    }

    public FileDispatcher()
        throws FileNotFoundException
    {}

    public FileDispatcher(File in, OutputStream outS)
        throws FileNotFoundException
    {
        super.setIn(in);
        this.setOut(outS);
    }

}
