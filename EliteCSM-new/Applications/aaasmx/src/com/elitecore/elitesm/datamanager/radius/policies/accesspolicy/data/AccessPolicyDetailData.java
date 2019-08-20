package com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.AccessTimeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.WeekNameAdapter;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.elitecore.elitesm.ws.rest.validator.ValidAccessPolicyTime;

import net.sf.json.JSONObject;

@XmlRootElement(name = "timeslap")
@ValidAccessPolicyTime
@XmlType(propOrder = { "startWeekDay", "startHour", "startMinute", "endWeekDay", "stopHour", "stopMinute", "accessStatus" })
public class AccessPolicyDetailData extends BaseData implements IAccessPolicyDetailData, Serializable, Comparable, Differentiable {

	private static final long serialVersionUID = 1L;
	
	
	private String accessPolicyId;
	
	private long serialNumber;
	
	@Contains(allowedValues = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" }, invalidMessage = "Invalid value of start day ", nullMessage = "Start day must be specified ")
	private String startWeekDay;
	
	@Contains(allowedValues = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" }, invalidMessage = "Invalid value of end day ", nullMessage = "End day must be specified ")
	private String endWeekDay;
	
	private Timestamp startTime;
	private Timestamp stopTime;
	
	private String accessStatus;
	
	private String startHour;
	private String startMinute;
	private String stopHour;
	private String stopMinute;
	
	@XmlElement(name = "start-hour")
	@XmlJavaTypeAdapter(AccessTimeAdapter.class)
	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	@XmlElement(name = "start-minute")
	@XmlJavaTypeAdapter(AccessTimeAdapter.class)
	public String getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(String startMinute) {
		this.startMinute = startMinute;
	}

	@XmlElement(name = "stop-hour")
	@XmlJavaTypeAdapter(AccessTimeAdapter.class)
	public String getStopHour() {
		return stopHour;
	}

	public void setStopHour(String stopHour) {
		this.stopHour = stopHour;
	}

	@XmlElement(name = "stop-minute")
	@XmlJavaTypeAdapter(AccessTimeAdapter.class)
	public String getStopMinute() {
		return stopMinute;
	}

	public void setStopMinute(String stopMinute) {
		this.stopMinute = stopMinute;
	}

	private List lstWeeKDay;

	/**
	 * @return Returns the accessPolicyId.
	 */
	
	@XmlTransient
	public String getAccessPolicyId() {
		return accessPolicyId;
	}

	/**
	 * @param accessPolicyId
	 * The accessPolicyId to set.
	 */
	public void setAccessPolicyId(String accessPolicyId) {
		this.accessPolicyId = accessPolicyId;
	}

	/**
	 * @return Returns the accessStatus.
	 */
	
	@XmlElement(name = "access-status")
	public String getAccessStatus() {
		return accessStatus;
	}

	/**
	 * @param accessStatus
	 * The accessStatus to set.
	 */
	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}

	/**
	 * @return Returns the endWeekDay.
	 */
	
	@XmlElement(name = "end-day")
	@XmlJavaTypeAdapter(WeekNameAdapter.class)
	public String getEndWeekDay() {
		return endWeekDay;
	}

	/**
	 * @param endWeekDay
	 * The endWeekDay to set.
	 */
	public void setEndWeekDay(String endWeekDay) {
		this.endWeekDay = endWeekDay;
	}

	/**
	 * @return Returns the startWeekDay.
	 */
	
	@XmlElement(name = "start-day")
	@XmlJavaTypeAdapter(WeekNameAdapter.class)
	public String getStartWeekDay() {
		return startWeekDay;
	}

	/**
	 * @param startWeekDay
	 * The startWeekDay to set.
	 */
	public void setStartWeekDay(String startWeekDay) {
		this.startWeekDay = startWeekDay;
	}

	/**
	 * @param lstParameterValue
	 * The lstParameterValue to set.
	 */

	/**
	 * @return Returns the startTime.
	 */
	
	@XmlTransient
	public Timestamp getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 * The startTime to set.
	 */
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return Returns the stopTime.
	 */
	
	@XmlTransient
	public Timestamp getStopTime() {
		return stopTime;
	}

	/**
	 * @param stopTime
	 * The stopTime to set.
	 */
	public void setStopTime(Timestamp stopTime) {
		this.stopTime = stopTime;
	}

	/**
	 * @return Returns the serialNumber.
	 */
	
	@XmlTransient
	public long getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber
	 * The serialNumber to set.
	 */
	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	@XmlTransient
	public List getLstWeeKDay() {
		return lstWeeKDay;
	}

	public void setLstWeeKDay(List lstWeeKDay) {
		this.lstWeeKDay = lstWeeKDay;
	}

	public int compareTo(Object arg0) {
		int retValue = 0;
		AccessPolicyDetailData netConfArg = (AccessPolicyDetailData) arg0;
		if (this.serialNumber == 0) {
			retValue = 1;
		} else if (this.serialNumber < netConfArg.getSerialNumber())
			retValue = -1;
		else
			retValue = 1;
		return retValue;
	}

	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();

		writer.println("Access Policy Id :" + accessPolicyId);
		writer.println("Serial Number :" + serialNumber);
		writer.println("Start Week Day :" + startWeekDay);
		writer.println("End Week Day :" + endWeekDay);
		writer.println("Start Time :" + startTime);
		writer.println("Stop Time :" + stopTime);
		writer.println("Access Status :" + accessStatus);
		writer.println("Lst WeeK Day :" + lstWeeKDay);

		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		AccessPolicyDetailData accessPolicyDetailData = (AccessPolicyDetailData) super.clone();
		accessPolicyDetailData.startTime = new Timestamp(this.startTime.getTime());
		accessPolicyDetailData.stopTime = new Timestamp(this.stopTime.getTime());
		return accessPolicyDetailData;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		JSONObject innerObject = new JSONObject();
		innerObject.put("Start Hour", startTime.getHours());
		innerObject.put("Start Minute", startTime.getMinutes());
		innerObject.put("End Day", endWeekDay);
		innerObject.put("Stop Hour", stopTime.getHours());
		innerObject.put("Stop Minute", stopTime.getMinutes());

		if (startWeekDay != null) {
			object.put(startWeekDay, innerObject);
		}
		return object;
	}

}
