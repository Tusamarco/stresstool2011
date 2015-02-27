package net.tc.utils.file;

import java.io.File;
import java.io.FileNotFoundException;
//import net.tc.isma.request.generic.requestImpl;
//import net.tc.isma.persister.IsmaPersister;
//import net.tc.isma.resources.ConfigResource;
//import net.tc.isma.IsmaException;
import java.lang.ref.SoftReference;

public class uploadableFileImpl
     extends FileDispatcher implements UploadableFile
{


    String name = null;
    String destination = null;
    String resourcetype = null;
    String source = null;
    String extention = null;

    public uploadableFileImpl()
        throws FileNotFoundException
    {}

    public uploadableFileImpl(File f)
        throws FileNotFoundException
    {
        if(f == null && !f.isFile())
            return;

        setName(f.getName());
        setSource(f.getPath());
        setFile(f);
    }

    public String getDestination()
    {
        return destination;
    }

    public String getExtention()
    {
        return extention;
    }

    public String getName()
    {
        return name;
    }

    public String getResourceType()
    {
        return resourcetype;
    }

    public String getSource()
    {
        return source;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public void setExtention(String extention)
    {
        this.extention = extention;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setResourceType(String resourceType)
    {
        this.resourcetype = resourcetype;
    }

    public void setSource(String source)
    {
        this.source = source;
    }
    public java.io.File getFile()
    {
        return getIn();
    }
    public void setFile(java.io.File file)
    {
        setIn(file);
    }
//    public static UploadableFile[] loadFile(requestImpl request, String objectPathReference, String postFixPath) throws IsmaException
//    {
//        String[] fileNames = null;
//        UploadableFile[] upFiles = null;
//        String path = "/download";
//        net.tc.isma.utils.Utility ut = new net.tc.isma.utils.Utility();
//
//
//        ConfigResource pathCfg =  (ConfigResource)IsmaPersister.getConfigParameterValue(objectPathReference);
//        if(pathCfg != null && pathCfg.getValue() != null)
//            path = (String) pathCfg.getValue();
//
//        if(postFixPath != null)
//            path = path  + "/" + postFixPath;
//
//        String objectPath = IsmaPersister.getMAINROOT() + path;
//        String strFileToSave = "";
//        com.jspsmart.upload.Files files = null;
//        java.lang.ref.SoftReference mS = new SoftReference(request.getFiles());
//        files = (com.jspsmart.upload.Files)mS.get();
//
//        if(files !=null)
//        {
//            checkPath(objectPath, true);
//            fileNames = new String[files.getCount()];
//            upFiles = new UploadableFile[files.getCount()];
//
//            for(int ifile = 0 ; ifile <files.getCount(); ifile++)
//            {
//                fileNames[ifile] = files.getFile(ifile).getFileName().replace(' ', '_').toUpperCase();
//                try
//                {
//                    java.lang.ref.SoftReference mSff = new SoftReference(files.getFile(ifile));
//                    com.jspsmart.upload.File file = (com.jspsmart.upload.File)mSff.get();
//
//                    ut.checkPath(file.getFilePathName());
//                    ut.createBackupCopy(file.getFilePathName(),true);
//
//                    file.saveAs(objectPath + "/" + fileNames[ifile]);
//                    file = null;
//
//                    java.lang.ref.SoftReference mSf = new SoftReference(new uploadableFileImpl((java.io.File)new java.io.File(objectPath + "/" + fileNames[ifile])));
//                    UploadableFile upF = (UploadableFile)mSf.get();
//                    upFiles[ifile] = upF;
//
//
//                }
//                catch (Exception ex)
//                {
//
//                }
//            }
//            files = null;
//            Runtime.getRuntime().gc();
//            Runtime.getRuntime().runFinalization();
//
//        }
//
//        return upFiles;
//    }

}
