package net.tc.utils.file;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
public interface FileDataReader {

    public void readInitialize(boolean hasHeader);
    public Map getRowAsMap();
    public String getRowAsString();
    public Map getAllRowsAsMap();
    public Vector getHeaders();
}
