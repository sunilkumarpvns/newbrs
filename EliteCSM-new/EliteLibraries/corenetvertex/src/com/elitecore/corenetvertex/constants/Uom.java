package com.elitecore.corenetvertex.constants;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Uom {

	SECOND("SECOND","TIME"){
		@Override
		public long getMinorUnits(long value){
			return value;
		}
	},
	MINUTE("MINUTE","TIME"){
		@Override
		public long getMinorUnits(long value){
			if(CommonConstants.QUOTA_UNLIMITED==value || CommonConstants.QUOTA_UNDEFINED==value){
				return value;
			}
			return TimeUnit.MINUTE.toSeconds(value);
		}
	},
	HOUR("HOUR","TIME"){
		@Override
		public long getMinorUnits(long value){
			if(CommonConstants.QUOTA_UNLIMITED==value || CommonConstants.QUOTA_UNDEFINED==value){
				return value;
			}
			return TimeUnit.HOUR.toSeconds(value);
		}
	}
	,
	BYTE("BYTE","VOLUME"){
		@Override
		public long getMinorUnits(long value){
			return value;
		}
	},
	KB("KB","VOLUME"){
		@Override
		public long getMinorUnits(long value){
			if(CommonConstants.QUOTA_UNLIMITED==value || CommonConstants.QUOTA_UNDEFINED==value){
				return value;
			}
			return DataUnit.KB.toBytes(value);
		}
	},
	MB("MB","VOLUME"){
		@Override
		public long getMinorUnits(long value){
			if(CommonConstants.QUOTA_UNLIMITED==value || CommonConstants.QUOTA_UNDEFINED==value){
				return value;
			}
			return DataUnit.MB.toBytes(value);
		}
	},
	GB("GB","VOLUME"){
		@Override
		public long getMinorUnits(long value){
			if(CommonConstants.QUOTA_UNLIMITED==value || CommonConstants.QUOTA_UNDEFINED==value){
				return value;
			}
			return DataUnit.GB.toBytes(value);
		}
	},
	EVENT("EVENT","EVENT"){
		@Override
		public long getMinorUnits(long value){
			throw new UnsupportedOperationException();
		}
	},
	PERPULSE("PERPULSE","PER PULSE"){
		@Override
		public long getMinorUnits(long value){
			throw new UnsupportedOperationException();
		}
	},
	;

	private String value;
	private String unitType;
	private static Map<String,Uom> valueToUom = new HashMap<>(9);
	private static Map<String,Uom> timeUnitToUom = new HashMap<>(3);
	private static Map<String,Uom> volumeUnitToUom = new HashMap<>(4);
	private static List<Uom> timeUoms = new ArrayList<>(3);
	private static List<Uom> volumeUoms = new ArrayList<>(4);


	static {
		for(Uom uom : values()) {
			valueToUom.put(uom.name(), uom);
			if("TIME".equals(uom.unitType)) {
				timeUnitToUom.put(uom.name(),uom);
				timeUoms.add(uom);
			} else if("VOLUME".equals(uom.unitType)) {
				volumeUnitToUom.put(uom.name(),uom);
				volumeUoms.add(uom);
			}
		}
	}

	Uom(String value, String unitType) {
		this.value = value;
		this.unitType = unitType;
	}

	public String getValue() {
		return this.value;
	}

	public String getUnitType() {
		return unitType;
	}

	public abstract long getMinorUnits(long value);

	public static Uom getUomFromValue(String name) {
		return valueToUom.get(name);
	}

	public static List<Uom> getUomsOfSameTypeFromName(String name) {

		if(Strings.isNullOrBlank(name)) {
			return null;
		}

		Uom uomFromValue = getUomFromValue(name);
		if(uomFromValue == null) {
			return null;
		}

		List<Uom> uomList = Collectionz.newArrayList();
		String umoUnitValue = getUomFromValue(name).unitType;
		for(Uom uom : values()) {
			if(uom.unitType .equals(umoUnitValue)) {
				uomList.add(uom);
			}
		}
		return uomList;
	}

	public static @Nullable	Uom fromVaue(String value){
		for (Uom uom : values()) {
			if (uom.name().equalsIgnoreCase(value)) {
				return uom;
			}
		}
		return null;
	}

	public static Map<String, Uom> getTimeUnitToUom() {
		return timeUnitToUom;
	}
	public static List<Uom> getTimeUoms() {
		return timeUoms;
	}

	public static Map<String, Uom> getVolumeUnitToUom() {
		return volumeUnitToUom;
	}

	public static boolean isVolumeUnit(Uom uom) {
		return "VOLUME".equals(uom.unitType);
	}

	public static boolean isTimeUnit(Uom uom) {
		return "TIME".equals(uom.unitType);
	}

	public static List<Uom> getVolumeUoms() {
		return volumeUoms;
	}
}