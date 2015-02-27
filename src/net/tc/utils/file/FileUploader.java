package net.tc.utils.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class FileUploader extends FileHandler
{
    private File in = null;
    private File out = null;
    private byte b[] = new byte[4096];
    String outPath = null;
    boolean filecreate = false;

    public void setOut(File out)
    {
        this.out = out;
    }

    public void setIn(File in)
    {
        this.in = in;
    }

    public File getOut()
    {
        if(in == null)
            return null;

        if(!in.exists())
            return null;

        if((!filecreate && out == null) || (!filecreate && !out.exists()))
            return null;

        if(filecreate)
            out = new File(outPath);

        BufferedInputStream inb = null;
        BufferedOutputStream outb = null;
        try
        {
            inb = new BufferdFileInputStream(in).getBufferedInputStream();
            outb = new BufferdFileOutputStream(out).getBufferedOutputStream();

            int size = 0;
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
        catch (Exception ex)
        {
        }

         File fout = new File(out.getAbsolutePath());
         if(!fout.exists() || fout.length() <= 0)
             return null;

         return fout;

    }
    public FileUploader()
        throws FileNotFoundException
    {}

    public FileUploader(File in, File out)
        throws FileNotFoundException
    {
        in = in;
        out = out;
    }
    public FileUploader(File in, File out, String outPath, boolean filecreate)
        throws FileNotFoundException
    {
        in = in;
        out = out;
        outPath = outPath;
        filecreate = filecreate;
    }

}
