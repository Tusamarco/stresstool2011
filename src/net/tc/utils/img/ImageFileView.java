package net.tc.utils.img;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/* ImageFileView.java is a 1.4 example used by FileChooserDemo2.java. */
public class ImageFileView extends FileView {
    ImageIcon jpgIcon = PhotoFileUtils.createImageIcon("images/jpgIcon.gif");
    ImageIcon gifIcon = PhotoFileUtils.createImageIcon("images/gifIcon.gif");
    ImageIcon tiffIcon = PhotoFileUtils.createImageIcon("images/tiffIcon.gif");
    ImageIcon pngIcon = PhotoFileUtils.createImageIcon("images/pngIcon.png");


    public String getName(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getTypeDescription(File f) {
        String extension = PhotoFileUtils.getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equals(PhotoFileUtils.jpeg) ||
                extension.equals(PhotoFileUtils.jpg)) {
                type = "JPEG Image";
            } else if (extension.equals(PhotoFileUtils.gif)){
                type = "GIF Image";
            } else if (extension.equals(PhotoFileUtils.tiff) ||
                       extension.equals(PhotoFileUtils.tif)) {
                type = "TIFF Image";
            } else if (extension.equals(PhotoFileUtils.png)){
                type = "PNG Image";
            }
        }
        return type;
    }

    public Icon getIcon(File f) {
        String extension = PhotoFileUtils.getExtension(f);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(PhotoFileUtils.jpeg) ||
                extension.equals(PhotoFileUtils.jpg)) {
                icon = jpgIcon;
            } else if (extension.equals(PhotoFileUtils.gif)) {
                icon = gifIcon;
            } else if (extension.equals(PhotoFileUtils.tiff) ||
                       extension.equals(PhotoFileUtils.tif)) {
                icon = tiffIcon;
            } else if (extension.equals(PhotoFileUtils.png)) {
                icon = pngIcon;
            }
        }
        return icon;
    }
}
