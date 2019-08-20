package com.elitecore.aaa.diameter.conf.sessionmanager;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;

/**
 * 
 * @author malav.desai
 *
 */
@XmlType(propOrder = {})
public class BatchParameters {
	private boolean enabled;
	private int size;
	private int intervalInSec;
	private int queryTimeoutInSec;
	private boolean insertBatched;
	private boolean updateBatched;
	private boolean deleteBatched;
	
	
	@XmlElement(name = "enable", type = boolean.class)
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean batchEnabled) {
		this.enabled = batchEnabled;
	}
	
	@XmlElement(name = "size", type = int.class)
	public int getSize() {
		return size;
	}
	public void setSize(int batchSize) {
		this.size = batchSize;
	}
	
	@XmlElement(name = "interval-sec", type = int.class)
	public int getIntervalInSec() {
		return intervalInSec;
	}
	public void setInterval(int batchIntervalInSec) {
		this.intervalInSec = batchIntervalInSec;
	}
	
	@XmlElement(name = "query-timeout-sec", type = int.class)
	public int getQueryTimeout() {
		return queryTimeoutInSec;
	}
	public void setQueryTimeout(int batchQueryTimeoutInSec) {
		this.queryTimeoutInSec = batchQueryTimeoutInSec;
	}
	
	@XmlElement(name = "insert-batched", type = boolean.class)
	public boolean isInsertBatched() {
		return insertBatched;
	}
	public void setInsertBatched(boolean insertBatched) {
		this.insertBatched = insertBatched;
	}
	
	@XmlElement(name = "update-batched", type = boolean.class)
	public boolean isUpdateBatched() {
		return updateBatched;
	}
	public void setUpdateBatched(boolean updateBatched) {
		this.updateBatched = updateBatched;
	}
	
	@XmlElement(name = "delete-batched", type = boolean.class)
	public boolean isDeleteBatched() {
		return deleteBatched;
	}
	public void setDeleteBatched(boolean deleteBatched) {
		this.deleteBatched = deleteBatched;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("-", 30));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("-", 30));
		writer.incrementIndentation();
		
		writer.println("Batch enabled : " + enabled);
		writer.println("Batch size : " + size);
		writer.println("Batch Interval (sec) : " + intervalInSec);
		writer.println("Batch Query Timeout (sec) : " + queryTimeoutInSec);
		writer.println("Batch Insert :" + insertBatched);
		writer.println("Batch Update : " + updateBatched);
		writer.println("Batch Delete : " + deleteBatched);
		writer.decrementIndentation();
		writer.close();
		return out.toString();
	}
	public void format(IndentingPrintWriter writer) {
		writer.incrementIndentation();
		writer.println("Batch enabled : " + enabled);
		writer.println("Batch size : " + size);
		writer.println("Batch Interval (sec) : " + intervalInSec);
		writer.println("Batch Query Timeout (sec) : " + queryTimeoutInSec);
		writer.println("Batch Insert :" + insertBatched);
		writer.println("Batch Update : " + updateBatched);
		writer.println("Batch Delete : " + deleteBatched);
		writer.decrementIndentation();
	}
}