package net.tc.utils.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class BufferdFileInputStream
{
    private BufferedInputStream bf = null;

    public BufferdFileInputStream(File f)
        throws FileNotFoundException
    {
       bf = new BufferedInputStream(new FileInputStream(f));

    }
    public BufferedInputStream getBufferedInputStream()
    {
        return bf;
    }
}
