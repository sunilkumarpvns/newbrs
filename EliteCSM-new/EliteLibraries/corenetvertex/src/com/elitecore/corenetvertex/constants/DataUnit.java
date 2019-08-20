package com.elitecore.corenetvertex.constants;

import java.util.HashMap;


/**
 *  Provides utilities for data unit conversion
 * 
 * @author Jay Trivedi
 *
 */
public enum DataUnit {
	
	
	BYTE(1099511627776L) {

		@Override
		public long toBytes(long usage) {
			return usage;
		}

		@Override
		public long toKB(long usage) {
			return usage / C0;
		}

		@Override
		public long toMB(long usage) {
			return usage / (C0 * C0);
		}

		@Override
		public long toGB(long usage) {
			return usage / (C0 * C0 * C0);
		}

		@Override
		public boolean isInRange(Long usage){
			return usage <= DataUnit.BYTE.maxValueForSlice;
		}

		@Override
		public boolean isSmallerDataUnitThan(DataUnit other) {
			return true;
		}

		@Override
		public long fromBytes(long bytes) {
			return bytes;
		}

	},
	KB(1073741824L) {

		@Override
		public long toBytes(long usage) {
			return usage * C0;
		}

		@Override
		public long toKB(long usage) {
			return usage;
		}

		@Override
		public long toMB(long usage) {
			return usage / C0;
		}

		@Override
		public long toGB(long usage) {
			return usage / (C0 * C0);
		}

		@Override
		public boolean isInRange(Long usage){
            return usage <= DataUnit.KB.maxValueForSlice;

        }

		@Override
		public boolean isSmallerDataUnitThan(DataUnit other) {
			return (other.equals(DataUnit.MB) || other.equals(DataUnit.GB));
		}

		@Override
		public long fromBytes(long bytes) {
			return bytes / C0;
		}

	},
	MB(1048576L) {

		@Override
		public long toBytes(long usage) {
			return usage * C0 * C0;
		}

		@Override
		public long toKB(long usage) {
			return usage * C0;
		}

		@Override
		public long toMB(long usage) {
			return usage;
		}

		@Override
		public long toGB(long usage) {
			return usage / C0;
		}

		@Override
		public boolean isInRange(Long usage){
            return usage <= DataUnit.MB.maxValueForSlice;

        }

		@Override
		public boolean isSmallerDataUnitThan(DataUnit other) {
			return  (other.equals(DataUnit.GB));
		}

		@Override
		public long fromBytes(long bytes) {
			return bytes / C0 / C0;
		}

	},
	GB(1024L) {

		@Override
		public long toBytes(long usage) {
			return usage * C0 * C0 * C0;
		}

		@Override
		public long toKB(long usage) {
			return usage * C0 * C0;
		}

		@Override
		public long toMB(long usage) {
			return usage * C0;
		}

		@Override
		public long toGB(long usage) {
			return usage;
		}
		@Override
		public boolean isInRange(Long usage){
            return usage <= DataUnit.GB.maxValueForSlice;

        }

		@Override
		public boolean isSmallerDataUnitThan(DataUnit other) {
			return false;
		}

		@Override
		public long fromBytes(long bytes) {
			return bytes / C0 / C0 / C0;
		}

	},
	;

    private static final long C0 = 1024;
    private static HashMap<String, DataUnit> unitMap;
    public final Long maxValueForSlice;

    DataUnit(Long maxValueForSlice){
        this.maxValueForSlice = maxValueForSlice;
    }

    public abstract long toBytes(long usage);
    public abstract long toKB(long usage);
    public abstract long toMB(long usage);
    public abstract long toGB(long usage);

	public abstract boolean isInRange(Long  usage);


	static {
		unitMap = new HashMap<String, DataUnit>(1, 1);

		for (DataUnit unit : values()) {
			unitMap.put(unit.name(), unit);
		}
	}


	public static DataUnit fromName(String name){
		return unitMap.get(name);
	}

	public abstract boolean isSmallerDataUnitThan(DataUnit other);


	public abstract long fromBytes(long bytes);

}
