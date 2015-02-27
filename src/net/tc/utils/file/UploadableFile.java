package net.tc.utils.file;

import java.io.File;
//import net.tc.request.generic.requestImpl;

public interface UploadableFile
{
    static String TYPE_PHOTO = "photo";
    static String TYPE_ZIP = "zip";
    static String TYPE_DOCUMENT = "document";

    public String getResourceType();
    public void setResourceType(String resourceType);
    public String getSource();
    public void setSource(String source);
    public String getDestination();
    public void setDestination(String destination);
    public String getName();
    public void setName(String name);
    public String getExtention();
    public void setExtention(String extention);
    public void setFile(File f);
    public File getFile();
    public boolean deleteFile();


}
