package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum QoSUnit {
	Gbps(3L) {
		@Override
		public long toBps(long qos) {
			return DataUnit.GB.toBytes(qos);
		}

		@Override
		public boolean isInRange(Long qos) {
			if(qos > QoSUnit.Gbps.maxValueForQoS){
				return false;
			}
			return true;
		}
	}, 
	Mbps(4095L) {
		@Override
		public long toBps(long qos) {
			return DataUnit.MB.toBytes(qos);
		}

		@Override
		public boolean isInRange(Long qos) {
			if(qos > QoSUnit.Mbps.maxValueForQoS){
				return false;
			}
			return true;
		}
	}, 
	Kbps(4194303L) {
		@Override
		public long toBps(long qos) {
			return DataUnit.KB.toBytes(qos);
		}

		@Override
		public boolean isInRange(Long qos) {
			if(qos > QoSUnit.Kbps.maxValueForQoS){
				return false;
			}
			return true;
		}
	}, 
	Bps(4294967295L) {
		@Override
		public long toBps(long qos) {
			return qos;
		}

		@Override
		public boolean isInRange(Long qos) {
			if(qos > QoSUnit.Bps.maxValueForQoS){
				return false;
			}
			return true;
		}
	};

	public static Map<String, QoSUnit> map = new HashMap<String, QoSUnit>();
	public Long maxValueForQoS;
	
	static{
		for(QoSUnit dataUnit : values()){
			map.put(dataUnit.name(), dataUnit);
		}
	}

	private QoSUnit(Long maxValueForQoS){
		this.maxValueForQoS = maxValueForQoS;
	}

	public static QoSUnit fromVal(String key) {
		return map.get(key);
	}
	
	public abstract long toBps(long qos);

	public abstract boolean isInRange(Long qos);
}
