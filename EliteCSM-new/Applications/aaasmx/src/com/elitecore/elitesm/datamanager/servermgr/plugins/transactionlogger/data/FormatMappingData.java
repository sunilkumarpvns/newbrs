package com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * @author nayana.rathod
 * 
 */

public class FormatMappingData extends BaseData implements IFormatMappingData, Serializable, Differentiable {

	private static final long serialVersionUID = 1L;
	private String formatMappingId;
	private String pluginId;
	private String key;
	private String format;
	private Integer orderNumber;

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@XmlTransient
	public String getFormatMappingId() {
		return formatMappingId;
	}

	public void setFormatMappingId(String formatMappingId) {
		this.formatMappingId = formatMappingId;
	}

	@XmlTransient
	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@NotEmpty(message = "Key must be specified")
	@XmlElement(name = "key")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@NotEmpty(message = "Format must be specified")
	@XmlElement(name = "format")
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		JSONObject innerJsonObject = new JSONObject();
		
		if( key != null)
			innerJsonObject.put("Key", key);
		
		if( format != null)
			innerJsonObject.put("Format", format);
		
		if( key != null )
			jsonObject.put(key, innerJsonObject);
		
		return jsonObject;
	}

}
