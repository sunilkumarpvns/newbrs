package com.elitecore.diameterapi.mibs.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

public class GroupedStatistics {

	private Map<Integer, CounterTuple> commandCodeCountersMap;
	private Map<Integer, ResultCodeTuple> resultCodeCountersMap;
	private Map<Integer, ConcurrentHashMap<Integer,ResultCodeTuple>> cmdWiseResultCodeCountersMap;
	private CounterTuple totalCounters;
	private Date resetTime;
	private ResultCodeTupleFactory resultCodeTupleFactory;
	private CounterTupleFactory counterTupleFactory;
	
	private static ThreadLocal<SimpleDateFormat> simpleDateFormatPool = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
		};
	};
	
	public GroupedStatistics() {

		commandCodeCountersMap = new ConcurrentHashMap<Integer, CounterTuple>(8, CommonConstants.DEFAULT_LOAD_FACTOR, 4);
		resultCodeCountersMap = new ConcurrentHashMap<Integer, ResultCodeTuple>(8, CommonConstants.DEFAULT_LOAD_FACTOR, 4);
		cmdWiseResultCodeCountersMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer,ResultCodeTuple>>(8, CommonConstants.DEFAULT_LOAD_FACTOR, 4);
		totalCounters = new CounterTuple();
		resetTime = new Date();
		resultCodeTupleFactory = new ResultCodeTupleFactory();
		counterTupleFactory = new CounterTupleFactory();
	}


	public void incrementInputStatistics(DiameterPacket packet){
		if(packet.isRequest()){
			incrementRequestInStatistics(packet);
		}else{
			incrementAnswerInStatistics(packet);
		}
	}

	public void incrementOutputStatistics(DiameterPacket packet){
		if(packet.isRequest()){
			incrementRequestOutStatistics(packet);
		}else{
			incrementAnswerOutStatistics(packet);
		}
	}

	private void incrementAnswerOutStatistics(DiameterPacket answer) {
		
		CounterTuple counterTuple = getCommandCodeCounterTuple(answer);
		counterTuple.incrementAnswerOutCount(answer);
		totalCounters.incrementAnswerOutCount(answer);

		IDiameterAVP resultCodeAvp = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
		if(resultCodeAvp == null){

			AvpGrouped experimentalResultCode = (AvpGrouped) answer.getAVP(DiameterAVPConstants.EXPERIMENTAL_RESULT);
			if(experimentalResultCode == null || experimentalResultCode.getGroupedAvp().size() == 0){
				return;
			}
			resultCodeAvp = experimentalResultCode.getSubAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
			if(resultCodeAvp == null){
				return;
			}
		}
		int resultCode = (int)resultCodeAvp.getInteger();
		ResultCodeTuple resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCode);
		resultCodeTuple.incrementResultCodeOut(answer);
		
		resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCode);
		resultCodeTuple.incrementResultCodeOut(answer);
		
		int resultCodeCategory = ResultCodeCategory.getResultCodeCategory(resultCode).value;
		resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
		resultCodeTuple.incrementResultCodeOut(answer);
		
		resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
		resultCodeTuple.incrementResultCodeOut(answer);
	}

	public void incrementUnknownH2HDropCount(DiameterAnswer answer) {

		CounterTuple counterTuple = getCommandCodeCounterTuple(answer);
		counterTuple.incrementUnknownHbHAnswerDroppedCount();
		totalCounters.incrementUnknownHbHAnswerDroppedCount();
	}

	private void incrementRequestOutStatistics(DiameterPacket request) {
		
		CounterTuple counterTuple = getCommandCodeCounterTuple(request);
		totalCounters.incrementRequestOutCount(request);
		counterTuple.incrementRequestOutCount(request);

		if(request.isReTransmitted()){
			totalCounters.incrementRequestsRetransmittedCount();
			counterTuple.incrementRequestsRetransmittedCount();
		}
	}

	private void incrementAnswerInStatistics(DiameterPacket answer) {

		CounterTuple counterTuple = getCommandCodeCounterTuple(answer);
		totalCounters.incrementAnswerInCount(answer);
		counterTuple.incrementAnswerInCount(answer);
		IDiameterAVP resultCodeAvp = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
		if(resultCodeAvp == null){

			AvpGrouped experimentalResultCode = (AvpGrouped) answer.getAVP(DiameterAVPConstants.EXPERIMENTAL_RESULT);
			if(experimentalResultCode == null || experimentalResultCode.getGroupedAvp().size() == 0){
				return;
			}
			resultCodeAvp = experimentalResultCode.getSubAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
			if(resultCodeAvp == null){
				return;
			}
		}
		int resultCode = (int)resultCodeAvp.getInteger();
		ResultCodeTuple resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCode);
		resultCodeTuple.incrementResultCodeIn(answer);
		
		resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCode);
		resultCodeTuple.incrementResultCodeIn(answer);
		
		int resultCodeCategory = ResultCodeCategory.getResultCodeCategory(resultCode).value;
		resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
		resultCodeTuple.incrementResultCodeIn(answer);
		
		resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
		resultCodeTuple.incrementResultCodeIn(answer);
		
	}

	private void incrementRequestInStatistics(DiameterPacket request) {

		CounterTuple counterTuple = getCommandCodeCounterTuple(request);
		totalCounters.incrementRequestInCount(request);
		counterTuple.incrementRequestInCount(request);
	}
	
	public void incrementMalformedPacketCount(DiameterPacket packet) {

		CounterTuple counterTuple = getCommandCodeCounterTuple(packet);
		counterTuple.incrementMalformedPacketReceivedCount();
		totalCounters.incrementMalformedPacketReceivedCount();
	}
	
	public void incrementTimeoutRequestCount(DiameterRequest request) {
		
		CounterTuple counterTuple = getCommandCodeCounterTuple(request);
		counterTuple.incrementTimeoutRequestStatistics();
		totalCounters.incrementTimeoutRequestStatistics();
	}
	
	public void incrementDuplicatePacketCount(DiameterPacket packet) {

		CounterTuple counterTuple = getCommandCodeCounterTuple(packet);
		if(packet.isRequest()){
			totalCounters.incrementDuplicateRequestCount();
			counterTuple.incrementDuplicateRequestCount();
		}else{
			totalCounters.incrementDuplicateEtEAnswerCount();
			counterTuple.incrementDuplicateEtEAnswerCount();
		}
	}
	
	public void incrementPacketDroppedCount(DiameterPacket packet) {
		
		CounterTuple counterTuple = getCommandCodeCounterTuple(packet);
		if(packet.isRequest()){
			counterTuple.incrementRequestDroppedCount();
			totalCounters.incrementRequestDroppedCount();
		}else{
			counterTuple.incrementAnswerDroppedCount();
			totalCounters.incrementAnswerDroppedCount();
		}
	}

	public long getRequestInCount(int commandCode) {
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getRequestInCount();
		return 0;
	}
	
	public long getTotalPendingRequestsCount() {
		return totalCounters.getPendingRequestCount();
	}
	
	public long getTotalRequestInCount() {
			return totalCounters.getRequestInCount();
	}

	public long getRequestOutCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getRequestOutCount();
		return 0;
	}
	
	public long getPendingRequestsCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getPendingRequestCount();
		return 0;
	}
	
	public long getTotalRequestOutCount(){
		return totalCounters.getRequestOutCount();
	}

	public long getAnswerInCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getAnswerInCount();
		return 0;
	}
	
	public long getTotalAnswerInCount(){
		return totalCounters.getAnswerInCount();
	}

	public long getAnswerOutCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getAnswerOutCount();
		return 0;
	}
	
	public long getTotalAnswerOutCount(){
		return totalCounters.getAnswerOutCount();
	}

	public long getRequestDroppedCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getRequestDroppedCount();
		return 0;
	}
	
	public long getTotalRequestDroppedCount(){
		return totalCounters.getRequestDroppedCount();
	}

	public long getAnswerDroppedCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getAnswerDroppedCount();
		return 0;
	}
	
	public long getTotalAnswerDroppedCount(){
		return totalCounters.getAnswerDroppedCount();
	}
	
	public long getUnknownHbHAnswerDroppedCount(int commandCode) {
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getUnknownHbHAnswerDroppedCount();
		return 0;
	}
	
	public long getTotalUnknownHbHAnswerDroppedCount(){
		return totalCounters.getUnknownHbHAnswerDroppedCount();
	}

	public long getDuplicateEtEAnswerCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getDuplicateEtEAnswerCount();
		return 0;
	}
	
	public long getTotalDuplicateEtEAnswerCount(){
		return totalCounters.getDuplicateEtEAnswerCount();
	}
	
	public long getDuplicateRequestCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getDuplicateRequestCount();
		return 0;
	}
	
	public long getTotalDuplicateRequestCount(){
		return totalCounters.getDuplicateRequestCount();
	}

	public long getMalformedPacketInCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getMalformedPacketReceivedCount();
		return 0;
	}
	
	public long getTotalMalformedPacketInCount(){
		return totalCounters.getMalformedPacketReceivedCount();
	}

	public long getRequestsRetransmittedCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getRequestsRetransmittedCount();
		return 0;
	}
	
	public long getTotalRequestsRetransmittedCount(){
		return totalCounters.getRequestsRetransmittedCount();
	}

	public long getTimeoutRequestCount(int commandCode){
		CounterTuple counter = commandCodeCountersMap.get(commandCode);
		if(counter != null)
			return counter.getTimeoutRequestStatistics();
		return 0;
	}
	
	public long getTotalTimeoutRequestCount(){
		return totalCounters.getTimeoutRequestStatistics();
	}
	
	public Date getLastResetTime(){
		return resetTime;
	}
	
	CounterTuple getCommandCodeCounterTuple(DiameterPacket packet) {

		int commandCode = packet.getCommandCode();
		CounterTuple commandCodeCounter = commandCodeCountersMap.get(commandCode);
		
		if(commandCodeCounter == null){
			synchronized (commandCodeCountersMap) {
				commandCodeCounter = commandCodeCountersMap.get(commandCode);
				if (commandCodeCounter == null) {
					commandCodeCounter = counterTupleFactory.getCounterTuple(packet);
					commandCodeCountersMap.put(commandCode, commandCodeCounter);
				}
			}
		}
		return commandCodeCounter;
	}

	public long getResultCodeInCount(int resultCode){
		ResultCodeTuple counter = resultCodeCountersMap.get(resultCode);
		if(counter != null)
			return counter.getResultCodeIn();
		return 0;
	}
	
	public long getResultCodeOutCount(int resultCode){
		ResultCodeTuple counter = resultCodeCountersMap.get(resultCode);
		if(counter != null)
			return counter.getResultCodeOut();
		return 0;
	}
	
	ResultCodeTuple getResultCodeTuple(int commandCode, int resultCode) {

		ResultCodeTuple resultCodeCouter = resultCodeCountersMap.get(resultCode);
		if(resultCodeCouter == null){
			synchronized (resultCodeCountersMap) {
				resultCodeCouter = resultCodeCountersMap.get(resultCode);
				if(resultCodeCouter == null){
					resultCodeCouter = resultCodeTupleFactory.getResultCodeTuple(commandCode);
					resultCodeCountersMap.put(resultCode, resultCodeCouter);
				}
			}
		}
		return resultCodeCouter;
	}

	private ResultCodeTuple getCmdWiseResultCodeTuple(int commandCode, int resultCode) {

		ConcurrentHashMap<Integer, ResultCodeTuple> resultCodeCounters = cmdWiseResultCodeCountersMap.get(commandCode);
		if(resultCodeCounters == null){
			synchronized (cmdWiseResultCodeCountersMap) {
				resultCodeCounters = cmdWiseResultCodeCountersMap.get(commandCode);
				if(resultCodeCounters == null){
					resultCodeCounters = new ConcurrentHashMap<Integer, ResultCodeTuple>(8, CommonConstants.DEFAULT_LOAD_FACTOR, 4);
					cmdWiseResultCodeCountersMap.put(commandCode, resultCodeCounters);
		}
		}
		}
		ResultCodeTuple resultCodeCouter = resultCodeCounters.get(resultCode);
		if(resultCodeCouter == null){
			synchronized (resultCodeCounters) {
				resultCodeCouter = resultCodeCounters.get(resultCode);
				if(resultCodeCouter == null){
					resultCodeCouter = resultCodeTupleFactory.getResultCodeTuple(commandCode);
					resultCodeCounters.put(resultCode, resultCodeCouter);
		}
		}
		}
		return resultCodeCouter;
		}

	@Override
	public String toString(){
		
		TableFormatter output = new TableFormatter(new String[]{}, new int[]{70}, TableFormatter.NO_BORDERS);
		output.add("Reset Time    : " + simpleDateFormatPool.get().format(resetTime), TableFormatter.LEFT);
		output.addNewLine();
		TableFormatter formatter1 = new TableFormatter(new String[]{
				"CMD", "R-Rx",  "A-Tx", "R-Tx", "A-Rx", "R-Rt", "R-To"}, 
				new int[]{5, 10, 10, 10, 10, 10, 10} , 
				new int[]{TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT,
				TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT},
				TableFormatter.ONLY_HEADER_LINE);
		
		TableFormatter formatter2 = new TableFormatter(new String[]{
				"CMD", "R-Dr", "A-Dr", "A-Un", "R-Du", "A-Du", "Mf-Msg", "R-Pn"}, 
				new int[]{5, 9, 9, 8, 8, 8, 8, 8} , 
				new int[]{TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT,
				TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT},
				TableFormatter.ONLY_HEADER_LINE);
		
		formatter1.addRecord(new String[]{
				"Total", 
				getDisplayValue(totalCounters.getRequestInCount(), 10), 
				getDisplayValue(totalCounters.getAnswerOutCount(), 10),  
				getDisplayValue(totalCounters.getRequestOutCount(), 10), 
				getDisplayValue(totalCounters.getAnswerInCount(), 10), 
				getDisplayValue(totalCounters.getRequestsRetransmittedCount(), 10),  
				getDisplayValue(totalCounters.getTimeoutRequestStatistics(), 10),     });
		
		formatter2.addRecord(new String[]{
				"Total", 
				getDisplayValue(totalCounters.getRequestDroppedCount(), 9), 
				getDisplayValue(totalCounters.getAnswerDroppedCount(), 9), 
				getDisplayValue(totalCounters.getUnknownHbHAnswerDroppedCount(), 8), 
				getDisplayValue(totalCounters.getDuplicateRequestCount(), 8), 
				getDisplayValue(totalCounters.getDuplicateEtEAnswerCount(), 8), 
				getDisplayValue(totalCounters.getMalformedPacketReceivedCount(), 8),
				getDisplayValue(totalCounters.getPendingRequestCount(), 8)  });
		
		for(Map.Entry<Integer, CounterTuple> entry: commandCodeCountersMap.entrySet()){
			formatter1.addRecord(new String[]{
					CommandCode.getDisplayName(entry.getKey()), 
					getDisplayValue(entry.getValue().getRequestInCount(), 10), 
					getDisplayValue(entry.getValue().getAnswerOutCount(), 10),  
					getDisplayValue(entry.getValue().getRequestOutCount(), 10), 
					getDisplayValue(entry.getValue().getAnswerInCount(), 10), 
					getDisplayValue(entry.getValue().getRequestsRetransmittedCount(), 10),  
					getDisplayValue(entry.getValue().getTimeoutRequestStatistics(), 10),   });
			
			formatter2.addRecord(new String[]{
					CommandCode.getDisplayName(entry.getKey()), 
					getDisplayValue(entry.getValue().getRequestDroppedCount(), 9), 
					getDisplayValue(entry.getValue().getAnswerDroppedCount(), 9), 
					getDisplayValue(entry.getValue().getUnknownHbHAnswerDroppedCount(), 8), 
					getDisplayValue(entry.getValue().getDuplicateRequestCount(), 8), 
					getDisplayValue(entry.getValue().getDuplicateEtEAnswerCount(), 8), 
					getDisplayValue(entry.getValue().getMalformedPacketReceivedCount(), 8),
					getDisplayValue(entry.getValue().getPendingRequestCount(), 8)  });
			
		}
		output.add("Counter Details", TableFormatter.CENTER);
		output.add(formatter1.getFormattedValues());
		output.addNewLine();
		output.add("Error Message Details", TableFormatter.CENTER);
		output.add(formatter2.getFormattedValues());
		output.addNewLine();
		
		formatter1 = new TableFormatter(
				new String[]{"Result-Code Category", "",  "Rx", "Tx"},	
				new int[]{20,4, 10, 10}, 
				new int[]{TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT}, 
				TableFormatter.ONLY_HEADER_LINE);
		if(resultCodeCountersMap.size() == 0){
			output.add(formatter1.getFormattedValues());
			output.addRecord(new String[]{"No Statistics Available."});
		}else{
			output.add(getResultCodeCategoryStatisticSummary(formatter1, 19));
		}		
		return output.getFormattedValues();

	}
	
	public String toCSV(){
		
		TableFormatter output = new TableFormatter(new String[]{}, 
				new int[]{286}, 
				TableFormatter.CSV, 
				TableFormatter.COLUMN_SEPARATOR_COMMA);
		output.addRecord(new String[]{"Reset Time: " + simpleDateFormatPool.get().format(resetTime)});
		TableFormatter formatter = new TableFormatter(new String[]{
				"CommandCode", "Req-Recieved",  "Ans-Transmitted", 
				"Req-Transmitted", "Ans-Recieved", "Req-Retransmitted", 
				"Req-Timeout","Req-Dropped", "Ans-Dropped", 
				"Ans-Unknown", "Req-Duplicate", "Ans-Duplicate", 
				"Malformed-Message", "Req-Pending"},
				
				new int[]{20, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19},
				TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		formatter.addRecord(new String[]{
				"Total", 
				String.valueOf(totalCounters.getRequestInCount()), 
				String.valueOf(totalCounters.getAnswerOutCount()),  
				String.valueOf(totalCounters.getRequestOutCount()), 
				String.valueOf(totalCounters.getAnswerInCount()), 
				String.valueOf(totalCounters.getRequestsRetransmittedCount()),
				String.valueOf(totalCounters.getTimeoutRequestStatistics()), 
				String.valueOf(totalCounters.getRequestDroppedCount()), 
				String.valueOf(totalCounters.getAnswerDroppedCount()), 
				String.valueOf(totalCounters.getUnknownHbHAnswerDroppedCount()), 
				String.valueOf(totalCounters.getDuplicateRequestCount()), 
				String.valueOf(totalCounters.getDuplicateEtEAnswerCount()), 
				String.valueOf(totalCounters.getMalformedPacketReceivedCount()),
				String.valueOf(totalCounters.getPendingRequestCount())  });
		
		for(Map.Entry<Integer, CounterTuple> entry: commandCodeCountersMap.entrySet()){
			
			formatter.addRecord(new String[]{
					CommandCode.fromCode(entry.getKey()), 
					String.valueOf(entry.getValue().getRequestInCount()), 
					String.valueOf(entry.getValue().getAnswerOutCount()),  
					String.valueOf(entry.getValue().getRequestOutCount()), 
					String.valueOf(entry.getValue().getAnswerInCount()), 
					String.valueOf(entry.getValue().getRequestsRetransmittedCount()),
					String.valueOf(entry.getValue().getTimeoutRequestStatistics()), 
					String.valueOf(entry.getValue().getRequestDroppedCount()), 
					String.valueOf(entry.getValue().getAnswerDroppedCount()), 
					String.valueOf(entry.getValue().getUnknownHbHAnswerDroppedCount()), 
					String.valueOf(entry.getValue().getDuplicateRequestCount()), 
					String.valueOf(entry.getValue().getDuplicateEtEAnswerCount()), 
					String.valueOf(entry.getValue().getMalformedPacketReceivedCount()),
					String.valueOf(entry.getValue().getPendingRequestCount())  });
		}
		output.add(formatter.getFormattedValues());
		
		formatter = new TableFormatter(
				new String[]{"Result-Code Category", "Category Type",  "Recieved", "Transmitted"},	
				new int[]{20, 4, 19, 19}, 
				TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		output.add(getResultCodeCategoryStatisticSummary(formatter, 19));
		return output.getFormattedValues();
		
	}

	private String getDisplayValue(long counter, int wrapDigts) {
		if(counter > (Math.pow(10, wrapDigts)-1))
			return (counter/1000) + "k";
		return String.valueOf(counter);
	}
	
	private String getResultCodeCategoryStatisticSummary(TableFormatter formatter, int counterShrinkSize) {

		if(resultCodeCountersMap.size() == 0){
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		
		for(ResultCodeCategory category : ResultCodeCategory.values()){
			ResultCodeTuple tuple = resultCodeCountersMap.get(category.value);
			if(tuple != null){
				formatter.addRecord(new String[]{
						category.category, 
						category.categoryType,
						getDisplayValue(tuple.getResultCodeIn(), counterShrinkSize), 
						getDisplayValue(tuple.getResultCodeOut(), counterShrinkSize)  });
			}
		}
		return formatter.getFormattedValues();
	}

	public String getResultCodeStatisticSummary() {
		TableFormatter formatter = new TableFormatter(
				new String[]{"ResultCode", "", "Rx", "Tx"},	
				new int[]{20, 6, 12, 12}, 
				new int[]{TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT}, 
				TableFormatter.NO_BORDERS);
		formatter.addNewLine();
		getResultCodeStatisticSummary(formatter);
		return formatter.getFormattedValues();
	}

	public Map<Integer, ResultCodeTuple> getCmdWiseResultCodeStatistic(int commandCode) {
		return cmdWiseResultCodeCountersMap.get(commandCode);
	}

	private void getResultCodeStatisticSummary(TableFormatter formatter) {
		
		TreeSet<Integer> resultCodeSet= new TreeSet<Integer>(resultCodeCountersMap.keySet());
		int isEmpty = resultCodeSet.size();
		if(isEmpty == 0){
			formatter.add("No Result Code Details Available.", TableFormatter.LEFT);
		}
		int category = 0;
		while(isEmpty != 0){

			if(resultCodeSet.subSet(category+1, category + 1000).size() > 0){
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
				ResultCodeTuple tuple = resultCodeCountersMap.get(category);
				formatter.addRecord(new String[]{
						resultCodeCategory.category, 
						resultCodeCategory.categoryType,
						getDisplayValue(tuple.getResultCodeIn(), 12), 
						getDisplayValue(tuple.getResultCodeOut(), 12)  });
				formatter.add("----------------------------------------------------------\n");
				isEmpty--;
				for(int resultCode : resultCodeSet.subSet(category+1, category + 1000)){

					tuple = resultCodeCountersMap.get(resultCode);
					formatter.addRecord(new String[]{
							String.valueOf(resultCode), 
							"",
							getDisplayValue(tuple.getResultCodeIn(), 12), 
							getDisplayValue(tuple.getResultCodeOut(), 12)  });
					isEmpty--;
				}
				if(isEmpty != 0)
					formatter.addNewLine();
			}
			category += 1000;
		}
	}

	public Map<Integer, CounterTuple> getCommandCodeCountersMap() {
		return commandCodeCountersMap;
	}
	
	public Map<Integer, ResultCodeTuple> getResultCodeCountersMap() {
		return resultCodeCountersMap;
	}
}

