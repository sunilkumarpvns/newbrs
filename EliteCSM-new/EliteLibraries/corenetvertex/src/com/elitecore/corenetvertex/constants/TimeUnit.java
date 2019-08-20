package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ishani.bhatt
 *
 */
public enum TimeUnit {
	 	
	SECOND {
		@Override
		public boolean isSmallerTimeUnitThan(TimeUnit other) {
			return true;
		}

		@Override
		public long toSeconds(long time) {
			return time;
		}

		@Override
		public long fromSeconds(long time) {
			return time;
		}
	},
	MINUTE {
		@Override
		public boolean isSmallerTimeUnitThan(TimeUnit other) {
			return  (other.equals(TimeUnit.YEAR) || other.equals(TimeUnit.MONTH) || other.equals(TimeUnit.WEEK) || other.equals(TimeUnit.DAY)
					|| other.equals(TimeUnit.HOUR));
		}

		@Override
		public long toSeconds(long time) {
			return time * C60;
		}

		@Override
		public long fromSeconds(long time) {
			return time / C60;
		}
	},
	HOUR {
		@Override
		public boolean isSmallerTimeUnitThan(TimeUnit other) {
			return  (other.equals(TimeUnit.YEAR) || other.equals(TimeUnit.MONTH) || other.equals(TimeUnit.WEEK) || other.equals(TimeUnit.DAY));
		}

		@Override
		public long toSeconds(long time) {
			return time * C60 * C60;
		}

		@Override
		public long fromSeconds(long time) {
			return time / C60 / C60;
		}
	},
	DAY {
		@Override
		public boolean isSmallerTimeUnitThan(TimeUnit other) {
			return  (other.equals(TimeUnit.YEAR) || other.equals(TimeUnit.MONTH) || other.equals(TimeUnit.WEEK));
		}

		@Override
		public long toSeconds(long time) {
			return time * C24 * C60 * C60;
		}

		@Override
		public long fromSeconds(long time) {
			return time / C24 / C60 / C60;
		}
	},
	WEEK {
		@Override
		public boolean isSmallerTimeUnitThan(TimeUnit other) {
			return  (other.equals(TimeUnit.YEAR) || other.equals(TimeUnit.MONTH));
		}

		@Override
		public long toSeconds(long time) {
			return time * C7 * C24 * C60 * C60;
		}

		@Override
		public long fromSeconds(long time) {
			return time / C7 / C24 / C60 / C60;
		}
	},
	MONTH {
		@Override
		public boolean isSmallerTimeUnitThan(TimeUnit other) {
			return (other.equals(TimeUnit.YEAR));
		}

		@Override
		public long toSeconds(long time) {
			return time * C30 * C7 * C24 * C60 * C60;
		}

		@Override
		public long fromSeconds(long time) {
			return time / C30 / C7 / C24 / C60 / C60;
		}
	},
	YEAR {
		@Override
		public boolean isSmallerTimeUnitThan(TimeUnit other) {
			return false;
		}

		@Override
		public long toSeconds(long time) {
			return time * C365 * C30 * C7 * C24 * C60 * C60;
		}

		@Override
		public long fromSeconds(long time) {
			return time / C365 / C30 / C7 / C24 / C60 / C60;
		}
	};


	private static final int C60 = 60;
	private static final int C24 = 24;
	private static final int C7 = 7;
	private static final int C30 = 30;
	private static final int C365 = 365;
	
	protected static final Map<String, TimeUnit> map = new HashMap<String, TimeUnit>();
	
	static{
		for(TimeUnit timeUnit : values()){
			map.put(timeUnit.toString(), timeUnit);
		}
	}

	public static TimeUnit fromVal(String key) {
		return map.get(key);
	}

	public abstract boolean isSmallerTimeUnitThan(TimeUnit other);

	public abstract long toSeconds(long time);

    public abstract long fromSeconds(long time);
}