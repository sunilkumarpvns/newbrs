package com.elitecore.core.driverx.cdr;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.driverx.ValueExpression;
import com.elitecore.core.driverx.ValueProviderExt;

public class CSVLineBuilderImpl implements CSVLineBuilder {

	private List<ValueExpression> lstValueExpression;
	private Map<ValueExpression,String> defaultValueMap;
	private CSVLineBuilderParams csvLineBuilderParams;

	public CSVLineBuilderImpl(CSVLineBuilderParams csvLineBuilderParams, 
			List<ValueExpression> expressions, 
			Map<ValueExpression, String> defaultValueMap){
		this.lstValueExpression = expressions;
		this.defaultValueMap = defaultValueMap;
		this.csvLineBuilderParams = csvLineBuilderParams;

	}


	@Override
	public List<String> getCSVRecords(ValueProviderExt valueProvider){
		List<String> records = new ArrayList<String>();
		StringBuilder record = new StringBuilder(100);
		String enclosingCharacter = csvLineBuilderParams.getEnclosingCharacter();
		for(ValueExpression valueExpression : lstValueExpression){
			String avpValue = getParameterValue(valueExpression,valueProvider);
			if(Strings.isNullOrEmpty(avpValue)){
				avpValue = defaultValueMap.get(valueExpression);
				if(Strings.isNullOrEmpty(avpValue)){
					avpValue = "";
				}
			}

			if (Strings.isNullOrEmpty(enclosingCharacter) == false) {
				if (avpValue.contains(enclosingCharacter)){
					avpValue = avpValue.replaceAll(enclosingCharacter, "\\\\"
							+ enclosingCharacter);
				}
				avpValue = enclosingCharacter + avpValue + enclosingCharacter;
			}
			appendValue(record, avpValue);
		}

		if (Strings.isNullOrEmpty(enclosingCharacter) == false) {
			record.append(enclosingCharacter);
			appendTimestamp(record);
			record.append(enclosingCharacter);
		} else {
			appendTimestamp(record);
		}
		records.add(record.toString());
		return records;
	}
	public String getParameterValue(ValueExpression valueExpression, ValueProviderExt valueProvider){
		List<String> avpValueList = valueExpression.getValues(valueProvider);
		if (Collectionz.isNullOrEmpty(avpValueList) == false) {
			StringBuilder strBuilder = new StringBuilder();
			formatMultiDeliminatorValue(strBuilder,avpValueList.get(0));
			for (int i = 1; i < avpValueList.size(); i++) {
				strBuilder.append(csvLineBuilderParams.getMultiValueDelimiter());
				formatMultiDeliminatorValue(strBuilder,avpValueList.get(i));
			}
			return strBuilder.toString();
		}
		return null;
	}

	private void formatMultiDeliminatorValue(StringBuilder stringBuilder,
			String value) {

		if (value.contains(csvLineBuilderParams.getMultiValueDelimiter()))
			value = value.replaceAll(csvLineBuilderParams.getMultiValueDelimiter(), "\\\\"
					+ csvLineBuilderParams.getMultiValueDelimiter());
		stringBuilder.append(value);
	}

	public void appendValue(StringBuilder record, String value) {
		if(value != null) {
			if(value.contains(csvLineBuilderParams.getDelimiter()))
				value = value.replaceAll(csvLineBuilderParams.getDelimiter(), "\\\\" + csvLineBuilderParams.getDelimiter());
			record.append(value).append(csvLineBuilderParams.getDelimiter());
		} else {
			record.append(csvLineBuilderParams.getDelimiter());
		}
	}

	public void appendTimestamp(StringBuilder record) {
		String strDateFormat = null;
		if(csvLineBuilderParams.getCdrTimeStampFormat() == null){
			strDateFormat =  new SimpleDateFormat().format(new Timestamp(System.currentTimeMillis()));
		}
		if(Strings.isNullOrBlank(strDateFormat)){
			strDateFormat = csvLineBuilderParams.getCdrTimeStampFormat().format(new Timestamp(System.currentTimeMillis()));
		}
		if(strDateFormat.contains(csvLineBuilderParams.getDelimiter())){
			strDateFormat = strDateFormat.replaceAll(csvLineBuilderParams.getDelimiter(), "\\\\" + csvLineBuilderParams.getDelimiter());
		}
		record.append(strDateFormat);
	}

}


