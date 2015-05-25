package com.mysql.stresstool;

public class RowValue {

	String catalog= null;
	String table = null;
	String fieldName = null;
	Object value = null;
	Long min = new Long(0);
	Long max = new Long(0);
	
	public RowValue(String table, String fieldName) {
		this.table = table;
		this.fieldName = fieldName;
	}
	
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Object getValue() {
		
		return value;
	}
	public void setValue(Object value) {
		
		if(Utils.isNumeric((Object)value) ){
			Long tLong = new Long((Long) value);
			
			if(tLong.longValue() > this.max.longValue()){
				this.setMax(tLong);
			}
			if(tLong.longValue() < this.min.longValue()){
				this.setMin(tLong);
			}
			
		}

		this.value = value;
	}
	public Long getMin() {
		return min;
	}
	public void setMin(Long min) {
		this.min = min;
	}
	public Long getMax() {
		return max;
	}
	public void setMax(Long max) {
		this.max = max;
	}
			
		
			
	
}
