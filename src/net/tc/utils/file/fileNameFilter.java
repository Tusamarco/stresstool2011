package net.tc.utils.file;

import java.io.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class fileNameFilter implements FilenameFilter {
    String filter = null;
    boolean casesensitive = true;

    public fileNameFilter(String filterIn, boolean casesensitiveIn) {
        filter = filterIn;
    }

    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if and only if the name should be included in the file list; <code>false</code> otherwise.
     * @todo Implement this java.io.FilenameFilter method
     */
    public boolean accept(File dir, String name) {
        if (filter == null
            || filter.equals("*.*")
            || filter.equals("*")
                )
            return true;

        String fname = null;
        String fext = null;

        String filterName = null;
        String filterExt = null;

        if (filter.indexOf(".") > 0) {
            int fidx = filter.indexOf(".");
            filterName = filter.substring(0, fidx );
            filterExt = filter.substring(fidx +1, filter.length());
        } else {
            filterName = filter;
        }

        if (name.indexOf(".") > 0) {
            int fidx = name.indexOf(".");
            fname = name.substring(0, fidx );
            fext = name.substring(fidx + 1, name.length());
        } else {
            fname = name;
        }

        if (!casesensitive)
        {
            fname = fname.toLowerCase();
            filterName = filterName.toLowerCase();
            fext = fext.toLowerCase();
            filterExt = filterExt.toLowerCase();
        }

        if (filterExt == null) {
            if (fname.equals(filterName) || filterName.equals("*"))
                return true;
            else
                return false;

        } else {
            if ((fname.equals(filterName) || filterName.equals("*"))
                && (fext.equals(filterExt) || filterExt.equals("*"))
                    )
                return true;
            else
                return false;
        }


    }
}
