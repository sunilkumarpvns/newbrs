/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service;

import java.text.SimpleDateFormat;

import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceCounters.GTPPrimeClientCounters;


/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeServiceCounterListener {

	private static GTPPrimeServiceCounters gtpServiceCounter;
	/*private Object t1 = new Object();
	private Object t2 = new Object();
	private Object t3 = new Object();
	private Object t4 = new Object();
	private Object t5 = new Object();*/


	public GTPPrimeServiceCounterListener( GTPPrimeServiceCounters serviceCounter ){
		gtpServiceCounter = serviceCounter;
	}

	public void init(){
		gtpServiceCounter.init();
	}

	public static boolean getInitializedState(){
		return gtpServiceCounter.isInitialized;
	}

	public static boolean reInitialize(){
		gtpServiceCounter.reInitialize();
		return true;
	}
	
	public void listenGTPPrimeTotalEchoRequestSentEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalEchoRequestSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeEchoRequestSent();
		}
	}

	public void listenGTPPrimeTotalEchoRequestSentEvent(){
		gtpServiceCounter.gtpPrimeTotalEchoRequestSent.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalEchoRequestSent() {
		return gtpServiceCounter.gtpPrimeTotalEchoRequestSent.get();	

	}
	public static long getGTPPrimeTotalEchoRequestSent(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeEchoRequestSent();
		}
		return value;
	}
	
	public void listenGTPPrimeTotalMalformedEchoResponseReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalMalformedEchoResponseReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeMalformedEchoResponseReceived();
		}
	}

	public void listenGTPPrimeTotalMalformedEchoResponseReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalMalformedEchoResponseReceived.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalMalformedEchoResponseReceived() {
		return gtpServiceCounter.gtpPrimeTotalMalformedEchoResponseReceived.get();	

	}
	public static long getGTPPrimeTotalMalformedEchoResponseReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeMalformedEchoResponseReceived();
		}
		return value;
	}
	
	public void listenGTPPrimeTotalMalformedNodeAliveResponseReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalMalformedNodeAliveResponseReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeMalformedNodeAliveResponseReceived();
		}
	}

	public void listenGTPPrimeTotalMalformedNodeAliveResponseReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalMalformedNodeAliveResponseReceived.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalMalformedNodeAliveResponseReceived() {
		return gtpServiceCounter.gtpPrimeTotalMalformedNodeAliveResponseReceived.get();	

	}
	public static long getGTPPrimeTotalMalformedNodeAliveResponseReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeMalformedNodeAliveResponseReceived();
		}
		return value;
	}
	
	public void listenGTPPrimeTotalEchoRequestRetryEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalEchoRequestRetry.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeEchoRequestRetry();
		}
	}

	public void listenGTPPrimeTotalEchoRequestRetryEvent(){
		gtpServiceCounter.gtpPrimeTotalEchoRequestRetry.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalEchoRequestRetry() {
		return gtpServiceCounter.gtpPrimeTotalEchoRequestRetry.get();	

	}
	public static long getGTPPrimeTotalEchoRequestRetry(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeEchoRequestRetry();
		}
		return value;
	}
	
	public void listenGTPPrimeTotalNodeAliveRequestRetryEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalNodeAliveRequestRetry.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeNodeAliveRequestRetry();
		}
	}

	public void listenGTPPrimeTotalNodeAliveRequestRetryEvent(){
		gtpServiceCounter.gtpPrimeTotalNodeAliveRequestRetry.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalNodeAliveRequestRetry() {
		return gtpServiceCounter.gtpPrimeTotalNodeAliveRequestRetry.get();	

	}
	public static long getGTPPrimeTotalNodeAliveRequestRetry(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeNodeAliveRequestRetry();
		}
		return value;
	}
	
	public void listenGTPPrimeTotalEchoResponseReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalEchoResponseReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeEchoResponseReceived();
		}
	}

	public void listenGTPPrimeTotalEchoResponseReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalEchoResponseReceived.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalEchoResponseReceived() {
		return gtpServiceCounter.gtpPrimeTotalEchoResponseReceived.get();	

	}
	public static long getGTPPrimeTotalEchoResponseReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeEchoResponseReceived();
		}
		return value;
	}
	
	public void listenGTPPrimeTotalNodeAliveResponseReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalNodeAliveResponseReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeNodeAliveResponseReceived();
		}
	}

	public void listenGTPPrimeTotalNodeAliveResponseReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalNodeAliveResponseReceived.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalNodeAliveResponseReceived() {
		return gtpServiceCounter.gtpPrimeTotalNodeAliveResponseReceived.get();	

	}
	public static long getGTPPrimeTotalNodeAliveResponseReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeNodeAliveResponseReceived();
		}
		return value;
	}
	
	public void listenGTPPrimeTotalNodeAliveRequestSentEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalNodeAliveRequestSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeNodeAliveRequestSent();
		}
	}

	public void listenGTPPrimeTotalNodeAliveRequestSentEvent(){
		gtpServiceCounter.gtpPrimeTotalNodeAliveRequestSent.incrementAndGet();
	}
	
	public static long getGTPPrimeTotalNodeAliveRequestSent() {
		return gtpServiceCounter.gtpPrimeTotalNodeAliveRequestSent.get();	

	}
	public static long getGTPPrimeTotalNodeAliveRequestSent(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeNodeAliveRequestSent();
		}
		return value;
	}
	
	public static long getGTPPrimeTotalDroppedRequest() {
		return gtpServiceCounter.gtpPrimeTotalDroppedRequest.get();	

	}
	public static long getGTPPrimeTotalDroppedRequest(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeDroppedRequest();
		}
		return value;
	}
	public static long getGTPPrimeTotalMalformedRequestPacketReceived() {
		return gtpServiceCounter.gtpPrimeTotalMalformedRequestPacketReceived.get();
	}
	public static long getGTPPrimeTotalMalformedRequestPacketReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeMalformedRequestPacketReceived();
		}
		return value;
	}
	public static long getGTPPrimeTotalEchoRequestReceived(){
		return gtpServiceCounter.gtpPrimeTotalEchoRequestReceived.get();
	}
	public static long getGTPPrimeTotalEchoRequestReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeEchoRequestReceived();
		}
		return value;
	}
	public static long getGTPPrimeTotalNodeAliveRequestReceived(){
		return gtpServiceCounter.gtpPrimeTotalNodeAliveRequestReceived.get();
	}
	public static long getGTPPrimeTotalNodeAliveRequestReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeNodeAliveRequestReceived();
		}
		return value;
	}	
	public static long getGTPPrimeTotalRedirectionRequestReceived(){
		return gtpServiceCounter.gtpPrimeTotalRedirectionRequestReceived.get();
	}
	public static long getGTPPrimeTotalRedirectionRequestReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeRedirectionRequestReceived();
		}
		return value;
	}

	public static long getGTPPrimeTotalDataRecordTransferRequestReceived(){
		return gtpServiceCounter.gtpPrimeTotalDataRecordTransferRequestReceived.get();
	}
	public static long getGTPPrimeTotalDataRecordTransferRequestReceived(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeDataRecordTransferRequestReceived();
		}
		return value;
	}

	public static long getGTPPrimeTotalRedirectionResponseSent(){
		return gtpServiceCounter.gtpPrimeTotalRedirectionResponseSent.get();
	}
	public static long getGTPPrimeTotalRedirectionResponseSent(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeRedirectionResponseSent();
		}
		return value;
	}

	public static long getGTPPrimeTotalEchoResponseSent(){
		return gtpServiceCounter.gtpPrimeTotalEchoResponseSent.get();
	}
	public static long getGTPPrimeTotalEchoResponseSent(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeEchoResponseSent();
		}
		return value;
	}

	public static long getGTPPrimeTotalNodeAliveResponseSent(){
		return gtpServiceCounter.gtpPrimeTotalNodeAliveResponseSent.get();
	}
	public static long getGTPPrimeTotalNodeAliveResponseSent(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeNodeAliveResponseSent();
		}
		return value;
	}

	public static long getGTPPrimeTotalDataRecordTransferResponseSent(){
		return gtpServiceCounter.gtpPrimeTotalDataRecordTransferResponseSent.get();
	}
	public static long getGTPPrimeTotalDataRecordTransferResponseSent(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeDataRecordTransferResponseSent();
		}
		return value;
	}
	public static long getGTPPrimeTotalVersionNotSupportedResponseSent(){
		return gtpServiceCounter.gtpPrimeTotalVersionNotSupportedResponseSent.get();
	}
	public static long getGTPPrimeTotalVersionNotSupportedResponseSent(String clientIP){
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeVersionNotSupportedResponseSent();
		}
		return value;
	}
	public static long getInvalidClientRequestReceived(){
		return gtpServiceCounter.gtpPrimeTotalInvalidClientRequestReceived.get();
	}
	public static String getServiceUpTime(){
		return (new SimpleDateFormat("dd:MM:yyyy - HH:mm:ss:SSS").format(gtpServiceCounter.gtpPrimeServiceUpTime)).toString();
	}

	public void listenGTPPrimeTotalRequestReceived(){
		gtpServiceCounter.gtpPrimeTotalRequestReceived.incrementAndGet();
	}


	public void listenGTPPrimeTotalEchoRequestReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalEchoRequestReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeEchoRequestReceived();
		}
	}

	public void listenGTPPrimeTotalEchoRequestReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalEchoRequestReceived.incrementAndGet();
	}

	public void listenGTPPrimeTotalNodeAliveRequestReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalNodeAliveRequestReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeNodeAliveRequestReceived();
		}
	}
	public void listenGTPPrimeTotalNodeAliveRequestReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalNodeAliveRequestReceived.incrementAndGet();

	}
	public void listenGTPPrimeTotalRedirectionRequestReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalRedirectionRequestReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeRedirectionRequestReceived();
		}
	}
	public void listenGTPPrimeTotalRedirectionRequestReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalRedirectionRequestReceived.incrementAndGet();
	}
	public void listenGTPPrimeTotalDataRecordTransferRequestReceivedEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalDataRecordTransferRequestReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeDataRecordTransferRequestReceived();
		}
	}
	public void listenGTPPrimeTotalDataRecordTransferRequestReceivedEvent(){
		gtpServiceCounter.gtpPrimeTotalDataRecordTransferRequestReceived.incrementAndGet();
	}
	public void listenGTPPrimeTotalRedirectionResponseSentEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalRedirectionResponseSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeRedirectionResponseSent();
		}
	}
	public void listenGTPPrimeTotalRedirectionResponseSentEvent(){
		gtpServiceCounter.gtpPrimeTotalRedirectionResponseSent.incrementAndGet();
	}
	public void listenGTPPrimeTotalEchoResponseSentEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalEchoResponseSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeEchoResponseSent();
		}
	}
	public void listenGTPPrimeTotalEchoResponseSentEvent(){
		gtpServiceCounter.gtpPrimeTotalEchoResponseSent.incrementAndGet();
	}
	public void listenGTPPrimeTotalNodeAliveResponseSentEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalNodeAliveResponseSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeNodeAliveResponseSent();
		}
	}
	public void listenGTPPrimeTotalNodeAliveResponseSentEvent(){
		gtpServiceCounter.gtpPrimeTotalNodeAliveResponseSent.incrementAndGet();
	}
	public void listenGTPPrimeTotalDataRecordTransferResponseSentEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalDataRecordTransferResponseSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeDataRecordTransferResponseSent();
		}
	}

	public void listenGTPPrimeTotalDataRecordTransferFailureResponseSentEvent( String clientIP ) {
		gtpServiceCounter.gtpPrimeTotalDataRecordTransferFailureResponseSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeDataRecordTransferFailureResponseSent();
		}
	}
	

	public void listenGTPPrimeTotalRedirectionFailureResponseSentEvent( String clientIP) {
		gtpServiceCounter.gtpPrimeTotalRedirectionFailureResponseSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeRedirectionFailureResponseSent();
		}
	}

	public void listenGTPPrimeTotalDataRecordTransferResponseSentEvent(){
		gtpServiceCounter.gtpPrimeTotalDataRecordTransferResponseSent.incrementAndGet();
	}
	public void listenGTPPrimeTotalVersionNotSupportedResponseSentEvent( String clientIP ){
		gtpServiceCounter.gtpPrimeTotalVersionNotSupportedResponseSent.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeVersionNotSupportedResponseSent();
		}	
	}
	public void listenGTPPrimeDroppedRequest(String clientIP) {
		gtpServiceCounter.gtpPrimeTotalDroppedRequest.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeDroppedRequestReceived();
		}
	}
	public void listenGTPPrimeTotalVersionNotSupportedResponseSentEvent(){
		gtpServiceCounter.gtpPrimeTotalVersionNotSupportedResponseSent.incrementAndGet();
	}

	public void listenGTPPrimeTotalInvalidClientRequest() {
		gtpServiceCounter.gtpPrimeTotalInvalidClientRequestReceived.incrementAndGet();
	}

	public void listenGTPPrimeMalformedRequestPacketCounter() {
		gtpServiceCounter.gtpPrimeTotalMalformedRequestPacketReceived.incrementAndGet();		
	}
	public void listenGTPPrimeMalformedRequestPacketCounter(String clientIP) {
		gtpServiceCounter.gtpPrimeTotalMalformedRequestPacketReceived.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeMalformedRequestPacketReceived();
		}	
	}

	public static long getGTPPrimeTotalRequestReceived() {
		return gtpServiceCounter.gtpPrimeTotalRequestReceived.get();
	}
	
	public static long getGTPPrimeTotalDataRecordTransferFailureResponseSent() {
		return gtpServiceCounter.gtpPrimeTotalDataRecordTransferFailureResponseSent.get();
	}
	
	public static long getGTPPrimeTotalDataRecordTransferFailureResponseSent(String clientIP) {
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeDataRecordTransferFailureResponseSent();
		}
		return value;
	}
	
	public static long getGTPPrimeTotalRedirectionFailureResponseSent() {
		return gtpServiceCounter.gtpPrimeTotalRedirectionFailureResponseSent.get();
	}
	
	public static long getGTPPrimeTotalRedirectionFailureResponseSent(String clientIP) {
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGTPPrimeRedirectionFailureResponseSent();
		}
		return value;
	}

	public void listenGTPPrimeTotalEchoRequestDropped(String hostAddress) {
		gtpServiceCounter.gtpPrimeTotalEchoRequestDropped.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(hostAddress);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeEchoRequestDropped();
		}	

	}

	public void listenGTPPrimeTotalNodeAliveRequestDropped(String hostAddress) {
		gtpServiceCounter.gtpPrimeTotalNodeAliveRequestDropped.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(hostAddress);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeNodeAliveRequestDropped();
		}	
	}

	public void listenGTPPrimeTotalRedirectionRequestDropped(String hostAddress) {
		gtpServiceCounter.gtpPrimeTotalRedirectionRequestDropped.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(hostAddress);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeRedirectionRequestDropped();
		}	
	}

	public void listenGTPPrimeTotalDataRecordTransferRequestDropped(String hostAddress) {
		gtpServiceCounter.gtpPrimeTotalDataRecordTransferRequestDropped.incrementAndGet();
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(hostAddress);
		if (gtpClientCounter != null ){
			gtpClientCounter.incrementGTPPrimeDataRecordTransferRequestDropped();
		}
	}

	public static long getGTPPrimeTotalDataRecordTransferRequestDropped() {
		return gtpServiceCounter.gtpPrimeTotalDataRecordTransferRequestDropped.get();
	}

	public static long getGTPPrimeTotalRedirectionRequestDropped() {
		return gtpServiceCounter.gtpPrimeTotalRedirectionRequestDropped.get();
	}

	public static long getGTPPrimeTotalNodeAliveRequestDropped() {
		return gtpServiceCounter.gtpPrimeTotalNodeAliveRequestDropped.get();
	}

	public static long getGTPPrimeTotalEchoRequestDropped() {
		return gtpServiceCounter.gtpPrimeTotalEchoRequestDropped.get();
	}

	public static long getGTPPrimeTotalDataRecordTransferRequestDropped(String clientIP) {
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGtpPrimeDataRecordTransferRequestDropped();
		}
		return value;
	}

	public static long getGTPPrimeTotalRedirectionRequestDropped(String clientIP) {
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGtpPrimeRedirectionRequestDropped();
		}
		return value;
	}

	public static long getGTPPrimeTotalNodeAliveRequestDropped(String clientIP) {
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGtpPrimeNodeAliveRequestDropped();
		}
		return value;
	}

	public static long getGTPPrimeTotalEchoRequestDropped(String clientIP) {
		long value=0;
		GTPPrimeClientCounters gtpClientCounter = gtpServiceCounter.gtpClientCounterMap.get(clientIP);
		if (gtpClientCounter != null ){
			value = gtpClientCounter.getGtpPrimeEchoRequestDropped();
		}
		return value;
	}
}