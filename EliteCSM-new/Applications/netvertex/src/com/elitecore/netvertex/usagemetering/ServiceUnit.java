package com.elitecore.netvertex.usagemetering;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * <code>ServiceUnit</code> is data class. This class holds following informations:
 * <ol>
 * <li> Slice Quota: time, inputOctets, outputOctets, totalOctets</li>
 * <li> Threshold Quota: timeThreshold, inputThreshold, outputThreshold, totalThreshold</li>
 * </ol>     
 * @author Manjil Purohit
 *
 */
public class ServiceUnit implements Cloneable {

	private long time;
	private long inputOctets;
	private long outputOctets;
	private long totalOctets;
	
	public ServiceUnit(){
	}


	public ServiceUnit(long time, long inputOctets, long outputOctets, long totalOctets) {
		this.time = time;
		this.inputOctets = inputOctets;
		this.outputOctets = outputOctets;
		this.totalOctets = totalOctets;
	}

	public long getTime() {
		return time;
	}

	public long getInputOctets() {
		return inputOctets;
	}

	public long getOutputOctets() {
		return outputOctets;
	}

	public long getTotalOctets() {
		return totalOctets;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setInputOctets(long inputOctets) {
		this.inputOctets = inputOctets;
	}

	public void setOutputOctets(long outputOctets) {
		this.outputOctets = outputOctets;
	}

	public void setTotalOctets(long totalOctets) {
		this.totalOctets = totalOctets;
	}
	
	
	@Override
	public Object clone()  {
		return new ServiceUnit(time,inputOctets,outputOctets,totalOctets);
	}
	
	
	public static class ServiceUnitBuilder{
		
		private ServiceUnit serviceUnit;
		
		public ServiceUnitBuilder() {
			serviceUnit = new ServiceUnit();
		}

		public ServiceUnitBuilder withTime(long time) {
			serviceUnit.time = time;
			return this;
		}

		public ServiceUnitBuilder withInputOctets(long inputOctets) {
			serviceUnit.inputOctets = inputOctets;
			return this;
		}

		public ServiceUnitBuilder withOutputOctets(long outputOctets) {
			serviceUnit.outputOctets = outputOctets;
			return this;
		}

		public ServiceUnitBuilder withTotalOctets(long totalOctets) {
			serviceUnit.totalOctets = totalOctets;

			return this;
		}

		
		public ServiceUnit build(){
			return serviceUnit;
		}

		public ServiceUnitBuilder withAllType(long value) {
			return withTime(value).withTotalOctets(value).withInputOctets(value).withOutputOctets(value);
		}
	}

	@Override
	public String toString(){
		
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		if(time>0){
			out.println("        Time = " + time);
		}
			out.println("        Input Octets  = " + inputOctets);
			out.println("        Output Octets = " + outputOctets);
			out.println("        Total Octets = " + totalOctets);
		out.close();
		return writer.toString();
		
	}
	
}
