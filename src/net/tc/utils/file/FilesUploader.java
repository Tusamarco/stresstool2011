package net.tc.utils.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FilesUploader extends FileUploader
{
    private File[] in = null;
    private File[] out = null;
    private String outPath = null;
    public String getOutPath()
    {
        return outPath;
    }

    public void setOut(File[] out)
    {
        this.out = out;
    }

    public void setIn(File[] in)
    {
        this.in = in;
    }

    public void setOutPath(String outPath)
    {
        this.outPath = outPath;
    }

    public File[] getOuts()
        throws FileNotFoundException, IOException
    {
        if(in == null)
            return null;

        ArrayList files = new ArrayList();

        for(int x = 0 ; x < in.length ; x++)
        {
            if(outPath != null && (out == null || out.length != in.length))
            {
                files.add(new FileUploader( (File) in[x], (File) out[x],outPath + "\\" + in[x].getName(),true).getOut());
            }
            else
                files.add(new FileUploader( (File) in[x], (File) out[x]).getOut());
        }

         return (File[])files.toArray();

    }

    public FilesUploader(File[] in, File[] out)
        throws FileNotFoundException
    {
        in = in;
        out = out;

    }

    public FilesUploader(File[] in, String outPath)
        throws FileNotFoundException
    {
        in = in;
        outPath = outPath;

    }


}
