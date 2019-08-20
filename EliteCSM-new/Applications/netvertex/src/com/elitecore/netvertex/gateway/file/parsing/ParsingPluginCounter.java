package com.elitecore.netvertex.gateway.file.parsing;

import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_CLONE_RECORDS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_FAILED_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_FAILED_RECORDS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_INPROCESS_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_PACKETS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_PENDING_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_RECORDS_RECIEVED;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_SUCCESS_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_SUCCESS_RECORDS;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ParsingPluginCounter {
	
	protected volatile AtomicLong totalFiles;

	protected volatile AtomicLong totalSuccessFiles;

	protected volatile AtomicLong totalPendingFiles;

	protected volatile AtomicLong totalInprocessFiles;

	protected volatile AtomicLong totalFailureFiles;

	protected volatile AtomicLong totalRecords;

	protected volatile AtomicLong totalSuccessRecords;
	
	protected volatile AtomicLong totalPackets;

	protected volatile AtomicLong totalFailureRecords;

	protected volatile AtomicLong totalCloneRecords;

	public ParsingPluginCounter() {

		totalFiles = new AtomicLong(0);
		totalSuccessFiles = new AtomicLong(0);
		totalPendingFiles = new AtomicLong(0);
		totalInprocessFiles = new AtomicLong(0);
		totalFailureFiles = new AtomicLong(0);
		totalRecords = new AtomicLong(0);
		totalSuccessRecords = new AtomicLong(0);
		totalPackets = new AtomicLong(0);
		totalFailureRecords = new AtomicLong(0);
		totalCloneRecords = new AtomicLong(0);
	}

	public AtomicLong getTotalFiles() {
		return totalFiles;
	}

	public void incrementTotalFiles() {
		totalFiles.incrementAndGet();
	}

	public void addTotalFiles(long totalFile) {
		totalFiles.addAndGet(totalFile);
	}

	public void addTotalCloneRecords(long totalCloneRecords) {
		this.totalCloneRecords.addAndGet(totalCloneRecords);
	}

	public AtomicLong getTotalCloneRecords() {
		return totalCloneRecords;
	}

	public AtomicLong getTotalRecords() {
		return totalRecords;
	}

	public void incrementTotalRecords() {
		totalRecords.incrementAndGet();
	}

	public void addTotalRecords(long totalRecords){
		this.totalRecords.addAndGet(totalRecords);
	}
	
	public AtomicLong getTotalSuccessFiles() {
		return totalSuccessFiles;
	}

	public void setTotalSuccessFiles(AtomicLong totalSuccessFiles) {
		this.totalSuccessFiles = totalSuccessFiles;
	}

	public AtomicLong getTotalFailureFiles() {
		return totalFailureFiles;
	}

	public void setTotalFailureFiles(AtomicLong totalFailureFiles) {
		this.totalFailureFiles = totalFailureFiles;
	}

	public AtomicLong getTotalSuccessRecords() {
		return totalSuccessRecords;
	}

	public void setTotalSuccessRecords(AtomicLong totalSuccessRecords) {
		this.totalSuccessRecords = totalSuccessRecords;
	}

	public AtomicLong getTotalPackets() {
		return totalPackets;
	}

	public void setTotalPackets(AtomicLong totalPackets) {
		this.totalPackets = totalPackets;
	}
	
	public AtomicLong getTotalFailureRecords() {
		return totalFailureRecords;
	}

	public void setTotalFailureRecords(AtomicLong totalFailureRecords) {
		this.totalFailureRecords = totalFailureRecords;
	}

	public void incrementTotalSuccessFiles() {
		incrementTotalFiles();
		totalSuccessFiles.incrementAndGet();
	}

	public void incrementTotalFailureFiles() {
		incrementTotalFiles();
		totalFailureFiles.incrementAndGet();
	}

	public void incrementTotalSuccessRecords() {
		totalSuccessRecords.incrementAndGet();
	}

	public void incrementTotalPackets() {
		totalPackets.incrementAndGet();
	}

	public void incrementTotalFailureRecords() {
		totalFailureRecords.incrementAndGet();
	}

	public void decrementTotalSuccessFiles() {
		totalSuccessFiles.decrementAndGet();
	}

	public void decrementTotalFailureFiles() {
		totalFailureFiles.decrementAndGet();
	}

	public void decrementTotalSuccessRecords() {
		totalSuccessRecords.decrementAndGet();
	}

	public void decremenTotalPackets() {
		totalPackets.decrementAndGet();
	}

	public void decrementTotalFailureRecords() {
		totalFailureRecords.decrementAndGet();
	}

	public void addTotalSuccessFiles(long totalFile) {
		totalSuccessFiles.addAndGet(totalFile);
	}

	public void addTotalFailureFiles(long totalFile) {
		totalFailureFiles.addAndGet(totalFile);
	}

	public void subtractTotalSuccessFiles(long totalFile) {
		addTotalSuccessFiles(-1 * totalFile);
	}

	public void subtractTotalFailureFiles(long totalFile) {
		addTotalFailureFiles(-1 * totalFile);
	}

	public void addTotalSuccessRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalSuccessRecords.addAndGet(totalRecords);
	}

	public void addTotalFailureRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalFailureRecords.addAndGet(totalRecords);
	}

	public void addTotalPackets(long totalRecords){
		totalPackets.addAndGet(totalRecords);
	}

	public void subtractTotalSuccessRecords(long totalRecords){
		addTotalSuccessRecords(-1 * totalRecords);
	}

	public void subtractTotalFailureRecords(long totalRecords){
		addTotalFailureRecords(-1 * totalRecords);
	}

	public void incrementTotalPendingFiles(){
		totalPendingFiles.incrementAndGet();
	}

	public void decrementTotalPendingFiles(){
		if(totalPendingFiles.get() > 0)
			totalPendingFiles.decrementAndGet();
	}

	public void incrementTotalInprocessFiles() {
		totalInprocessFiles.incrementAndGet();
	}

	public void decrementTotalInprocessFiles(){
		if(totalInprocessFiles.get() > 0 )
			totalInprocessFiles.decrementAndGet();
	}

	public AtomicLong getTotalPendingFiles() {
		return totalPendingFiles;
	}

	public AtomicLong getTotalInprocessFiles() {
		return totalInprocessFiles;
	}

	public Map<String, Object> toMap(){
		Map<String, Object> counterMap = new HashMap<>();
		counterMap.put(TOTAL_FILES,getTotalFiles());
		counterMap.put(TOTAL_SUCCESS_FILES,getTotalSuccessFiles());
		counterMap.put(TOTAL_FAILED_FILES,getTotalFailureFiles());
		counterMap.put(TOTAL_PENDING_FILES,getTotalPendingFiles());
		counterMap.put(TOTAL_INPROCESS_FILES,getTotalInprocessFiles());
		counterMap.put(TOTAL_SUCCESS_RECORDS,getTotalSuccessRecords());
		counterMap.put(TOTAL_FAILED_RECORDS,getTotalFailureRecords());
		counterMap.put(TOTAL_PACKETS,getTotalPackets());
		counterMap.put(TOTAL_RECORDS_RECIEVED, getTotalRecords());
		counterMap.put(TOTAL_CLONE_RECORDS, getTotalCloneRecords());
		return counterMap;

	}
	/*protected void restoreCounterStatus(JSONObject jsonObject)
			throws JSONException {
		totalFiles.set(jsonObject.getLong(TOTAL_FILES));
		totalSuccessFiles.set(jsonObject.getLong(TOTAL_SUCCESS_FILES));
		totalSuccessRecords.set(jsonObject.getLong(TOTAL_SUCCESS_RECORDS));
		totalFailureFiles.set(jsonObject.getLong(TOTAL_FAILED_FILES));
		totalFailureRecords.set(jsonObject.getLong(TOTAL_FAILED_RECORDS));
		totalRecords.set(jsonObject.getLong(TOTAL_RECORDS_RECIEVED));
		try{
			totalPackets.set(jsonObject.getLong(TOTAL_PACKETS));
		}catch(Exception e){ //NOSONAR
			LogManager.getLogger().warn(MODULE, "Adding default value of " + TOTAL_PACKETS + ", Reason : " + e.getMessage());
			totalPackets.set(0);
		}
		totalPendingFiles.set(jsonObject.getLong(TOTAL_PENDING_FILES));
		totalInprocessFiles.set(jsonObject.getLong(TOTAL_INPROCESS_FILES));
		totalCloneRecords.set(jsonObject.getLong(TOTAL_CLONE_RECORDS));
	}*/


}
