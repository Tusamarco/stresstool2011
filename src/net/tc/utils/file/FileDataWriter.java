package net.tc.utils.file;

import java.util.*;

import net.tc.utils.SynchronizedMap;

public interface FileDataWriter {

		public void setHeaders(Vector headers);
		public void setRecordDataCSV(String record);
		void setRecordData(SynchronizedMap record);
		void printHeaders(SynchronizedMap record) ;
		
}
