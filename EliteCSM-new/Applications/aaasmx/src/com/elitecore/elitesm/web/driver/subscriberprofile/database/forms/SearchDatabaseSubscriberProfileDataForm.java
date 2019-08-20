package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;


public class SearchDatabaseSubscriberProfileDataForm  extends ActionForm{
	
	private String driverInstanceId;
	private String dbAuthId;
	
	private String action;
	
	private String firstFieldName;
	//private String secondFieldName;
	
	private String firstFieldData;
	//private String secondFieldData;
	
	private String idFieldName;
	
	
	
	private int previousPage;
	private int numerOfRecordsPerPage; 
	private long totalNumberOfRecord; 
	private int lastPage;
	private long totalNoOfPage;
	private int nextPage=0;
	private int currentPage=0;
	private int totalRow=0;
	private int totalField=0;
	
	
	private List seachRowList ;
	private List lstFieldName = new ArrayList();
	
	public int getCurrentPage(){
		return currentPage;
	}
	public void setCurrentPage(int currentPage){
		this.currentPage=currentPage;
	}
	public long getTotalNumberOfRecord(){
		return totalNumberOfRecord;
	}
	public void setTotalNumberOfRecord(long totalNumberOfRecord){
		this.totalNumberOfRecord=totalNumberOfRecord;
	}

	public int getNumerOfRecordsPerPage(){
		return numerOfRecordsPerPage;
	}
	public void setNumerOfRecordsPerPage(int numerOfRecordsPerPage){
		this.numerOfRecordsPerPage=numerOfRecordsPerPage;
	}
	
	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action=action;
	}
	

	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(String dbAuthId) {
		this.dbAuthId = dbAuthId;
	}
	public String getFirstFieldName(){
		return firstFieldName;
	}
	public void setFirstFieldName(String firstFieldName){
		this.firstFieldName=firstFieldName;
	}
	public String getFirstFieldData(){
		return firstFieldData;
	}
	public void setFirstFieldData(String firstFieldData){
		this.firstFieldData=firstFieldData;
	}
	/*public String getSecondFieldName(){
		return secondFieldName;
	}
	public void setSecondFieldName(String secondFieldName){
		this.secondFieldName=secondFieldName;
	}
	public String getSecondFieldData(){
		return secondFieldData;
	}
	public void setSecondFieldData(String secondFieldData){
		this.secondFieldData=secondFieldData;
	}*/
	
	public int getNextPage(){
		return nextPage;
	}
	public void setNextPage(int nextPage){
		this.nextPage=nextPage;
	}
	public int getPreviousPage(){
		return previousPage;
	}
	public void setPreviousPage(int previousPage){
		this.previousPage=previousPage;
	}
	public int getLastPage(){
		return lastPage;
	}
	public void setLastPage(int lastPage){
		this.lastPage=lastPage;
	}
	public long getTotalNoOfPage(){
		return totalNoOfPage;
	}
	public void setTotalNoOfPage(long totalNoOfPage){
		this.totalNoOfPage=totalNoOfPage;
	}
	public int getTotalRow(){
		return totalRow;
	}
	public void setTotalRow(int totalRow){
		this.totalRow=totalRow;
	}
	public int getTotalField(){
		return totalField;
	}
	public void setTotalField(int totalField){
		this.totalField=totalField;
	}
	public List getSeachRowList(){
		return seachRowList;
	}
	public void setSeachRowList(List seachRowList){
		this.seachRowList=seachRowList;
	}
	public List getLstFieldName(){
		return lstFieldName;
	}
	public void setLstFieldName(List lstFieldName){
		this.lstFieldName=lstFieldName;
	}
	public String getIdFieldName() {
		return idFieldName;
	}
	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}
}
