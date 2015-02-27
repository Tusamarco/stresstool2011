package com.mysql.stresstool;

import java.util.ArrayList;

/**
 * @author marcotusa
 *This class is use to manage and store the range of PK used in several situations
 */

public class PrimaryKeyRangeDefiner {

private int pkEnd = 0;
private ArrayList pkArrayEnd ;
private ArrayList pkArrayStart ;
private boolean refresh = false;
private int loopsnumber = 0 ;
private int looprefresh = 500 ;
private int lastResetLoop = 0;

public PrimaryKeyRangeDefiner(int loopsnumberIn) {
//	super();
	this.loopsnumber = loopsnumberIn;
	pkArrayEnd =  new ArrayList(loopsnumber) ;
	pkArrayStart = new ArrayList(loopsnumber);

}
public PrimaryKeyRangeDefiner(int loopsnumberIn,int looprefreshIn) {
//	super();
	this.loopsnumber = loopsnumberIn;
	pkArrayEnd =  new ArrayList(loopsnumber) ;
	pkArrayStart = new ArrayList(loopsnumber);
	this.looprefresh = looprefreshIn;
}


private int pkStart = 0;
public int getPkStart() {
	return pkStart;
}
public void setPkStart(int pkStart) {
	this.pkStart = pkStart;
}

public int getLastResetLoop() {
	return lastResetLoop;
}
public void setLastResetLoop(int lastResetLoop) {
	this.lastResetLoop = lastResetLoop;
}
public int getPkEnd() {
	return pkEnd;
}
public void setPkEnd(int pkEnd) {
	this.pkEnd = pkEnd;
}
public boolean isRefresh() {
	return refresh;
}
public void setRefresh(boolean refresh) {
	this.refresh = refresh;
}
public int getLoopsnumber() {
	return loopsnumber;
}
public void setLoopsnumber(int loopsnumber) {
	this.loopsnumber = loopsnumber;
}
public int getLooprefresh() {
	return looprefresh;
}
public void setLooprefresh(int looprefresh) {
	this.looprefresh = looprefresh;
}

public void setKeyStart(int start){
	pkArrayStart.add(new Integer(start));
}
	
public int getKeyStart (int index){
		return ((Integer)pkArrayStart.get(index)).intValue();
		
}
public void setKeyEnd(int end){
	pkArrayEnd.add(new Integer(end));
}
	
public int getKeyEnd (int index){
		return ((Integer)pkArrayEnd.get(index)).intValue();
		
}
public int getLen(){
	if(pkArrayEnd == null){
		return 0;
	}
	return pkArrayEnd.size();
}
public void reset(){
	pkArrayEnd = new ArrayList();
	pkArrayStart = new ArrayList();
}

}