package net.tc.utils.file.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.tc.utils.file.*;
import java.util.zip.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ZipFileHandler extends FileHandler
{
    private ZipFile in = null;
//    private OutputStream out = null;
    private byte b[] = new byte[4096];
    String outPath = null;

    public void setIn(ZipFile in)
    {
        this.in = in;
    }

    public ArrayList extractAll()
        throws FileNotFoundException, IOException, DataFormatException, FileNotFoundException, IOException
    {
        ArrayList l = new ArrayList();
        if(in == null && outPath == null )
            return null;

        if(in.size() <= 0 )
            return null;

        List el = getAllEntriesNames();

        if(el == null)
            return null;

        for(int x = 0 ; x < el.size() ; x++)
        {
            File f = getUncopressedEntry((String)el.get(x));
            l.add(f.getPath());
        }

        return l;
    }
    private File getUncopressedEntry(String path)
        throws FileNotFoundException, IOException, DataFormatException
    {
        File f = null;

        if(outPath == null || outPath.equals("")
           || path == null || path.equals("")
           || in == null || in.size() <= 0 )
            return null;

        ZipEntry ent = (ZipEntry)in.getEntry(path);
        f = this.checkPath(outPath, true);
        if(f == null)
            return null;

        f = null;
        f = new File(outPath + "/" + ent.getName());

        BufferedOutputStream outb = new BufferdFileOutputStream(f).getBufferedOutputStream();
        BufferedInputStream inb = new BufferedInputStream(in.getInputStream(ent));

        int size = 0;
//        Inflater decompresser = new Inflater();
//        byte[] result = new byte[b.length];

        while (true)
        {
            size = inb.read(b, 0, 4096);
//            decompresser.setInput(b);
//            decompresser.inflate(result);

            if (size == -1)
                break;
            outb.write(b, 0, size);
        }
//        decompresser.end();

        outb.flush();
        outb.close();
        inb.close();

        return f.getAbsoluteFile();
    }
    public ZipFileHandler()
        throws FileNotFoundException
    {}

    public ZipFileHandler(ZipFile in, String outPath)
        throws FileNotFoundException
    {
        this.in = in;
        this.outPath = outPath;
    }
//    public ZIpFileHandler(File in, File out, String outPath, boolean filecreate)
//        throws FileNotFoundException
//    {
//        in = in;
//        out = out;
//        outPath = outPath;
//        filecreate = filecreate;
//    }
    public ArrayList getAllEntriesNames()
    {
        if(in == null || in.entries() == null || !in.entries().hasMoreElements())
            return null;

        ArrayList names  = new ArrayList();
        for(Enumeration e = in.entries(); e.hasMoreElements();)
        {
            ZipEntry ent = (ZipEntry)e.nextElement();
            if(!ent.isDirectory())
                names .add(ent.getName());

        }

        return names;
    }

}
