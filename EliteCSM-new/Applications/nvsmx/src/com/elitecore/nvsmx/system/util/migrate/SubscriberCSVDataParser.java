package com.elitecore.nvsmx.system.util.migrate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.subscriberimport.InputType;

/**
 * 
 * @author Chetan.Sankhala
 */
public class SubscriberCSVDataParser implements SubscriberDataParser {
	
	private static final String UPLOAD_OCTECTS = "UPLOADOCTETS";
	private static final String DOWNLOAD_OCTECTS = "DOWNLOADOCTETS";
	private static final String TOTAL_OCTECTS = "TOTALOCTETS";
	
	private List<ColumnNameData> csvHeaders;
	
	public SubscriberCSVDataParser(List<ColumnNameData> csvHeaders) {
		this.csvHeaders = csvHeaders;
	}

	@Override
	public NV648SubscriberInfo parse(String dataString) {
		
		List<String> values = COMMA_SPLITTER.split(dataString);
		
		Map<String, String> valueByColumnNameMap = new HashMap<String, String>();
		
		NV648BaseUsageInfo baseUsageInfo = new NV648BaseUsageInfo();
		
		
		for (ColumnNameData columnNameData : csvHeaders) {
			
			if (values.size() < columnNameData.getIndex()) {
				continue;
			}
			
			String value = values.get(columnNameData.getIndex()-1);
			
			if (UPLOAD_OCTECTS.equals(columnNameData.getName())) {
				baseUsageInfo.setUploadOctets(getParsedUsage(value));
			} else if (DOWNLOAD_OCTECTS.equals(columnNameData.getName())) {
				baseUsageInfo.setDownloadOctets(getParsedUsage(value));
			} else if (TOTAL_OCTECTS.equals(columnNameData.getName())) {
				baseUsageInfo.setTotalOctets(getParsedUsage(value));
			} else {
				
				if (Strings.isNullOrBlank(value)) {
					valueByColumnNameMap.put(columnNameData.getName(), null);
				} else {
					valueByColumnNameMap.put(columnNameData.getName(), value);
				}
			}
			
		}
		
		baseUsageInfo.setBasePackageName(valueByColumnNameMap.get(SPRFields.PRODUCT_OFFER.columnName));
		
		AccountDataInfo accountDataInfo = new AccountDataInfo(valueByColumnNameMap);
		NV648SubscriberInfo nv648SubscriberInfo = new NV648SubscriberInfo();
		nv648SubscriberInfo.setAccountDataInfo(accountDataInfo);
		nv648SubscriberInfo.setBaseUsageInfos(Arrays.asList(baseUsageInfo));
		
		return nv648SubscriberInfo;
	}

	private long getParsedUsage(String value) {
		return Strings.isNullOrBlank(value) ? 0 : Long.parseLong(value);
	}

	@Override
	public InputType getInputType() {
		return InputType.CSV_SUBSCRIBER;
	}
}
