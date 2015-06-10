package net.tc.utils.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;


public class BufferdFileOutputStream
{
    private BufferedOutputStream bf = null;

    public BufferdFileOutputStream(File f)
        throws FileNotFoundException
    {
       bf = new BufferedOutputStream(new FileOutputStream(f));

    }
    public BufferedOutputStream getBufferedOutputStream()
    {
        return bf;
    }
}
