package com.elitecore.netvertex.gateway.diameter.af;

/*
 * NOTE: This table contains entry in kiloBits (NOT KILOBYTES!!)
 *  1 mb = 1000 kb
 *  1 gb = 1000 mb
 */
public enum EPSQoSRoundingTable {

	/** 1 kbps to 63 kbps in 1 kbps increments */
	TIER_1(63, 1) {
		@Override
		public long roundUpper(long kiloBits) {
			return  EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 64 to 568 kbps in 8 kbps increments */
	TIER_2(568, 8) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 576 kbps to 8640 kbps in 64 kbps increments */ 
	TIER_3(8640, 64) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 8700 kbps to 16000 kbps in 100 kbps increments */
	TIER_4(16000, 100) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 17 Mbps to 128 Mbps in 1 Mbps increments  */
	TIER_5(128000, 1000) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 130 Mbps to 256 Mbps in 2 Mbps increments  */
	TIER_6(256000, 2000) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 260 Mbps to 500 Mbps in 4 Mbps increments  */
	TIER_7(500000, 4000) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 510 Mbps to 1500 Mbps in 10 Mbps increments  */
	TIER_8(1500000, 10000) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	/** 1600 Mbps to 10 Gbps in 100 Mbps increments  */
	TIER_9(10000000, 100000) {
		@Override
		public long roundUpper(long kiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, increment);
		}
	},
	DEFAULT_TIER(1,1) {

		@Override
		public long roundUpper(long kiloBits) {
			return kiloBits;
		}
	};

	public long maxKiloBits;
	public double increment;
	
	private EPSQoSRoundingTable(long maxKiloBitsValue, long increment) {
		this.maxKiloBits = maxKiloBitsValue;
		this.increment = increment;
	}
	
	public static EPSQoSRoundingTable fromBits(long kiloBits) {
		
		if (kiloBits <= DEFAULT_TIER.maxKiloBits) {
			return DEFAULT_TIER;
		} else if (kiloBits <= TIER_1.maxKiloBits) {
			return TIER_1;
		} else if (kiloBits <= TIER_2.maxKiloBits) {
			return TIER_2;
		} else if (kiloBits <= TIER_3.maxKiloBits) {
			return TIER_3;
		} else if (kiloBits <= TIER_4.maxKiloBits) {
			return TIER_4;
		} else if (kiloBits <= TIER_5.maxKiloBits) {
			return TIER_5;
		} else if (kiloBits <= TIER_6.maxKiloBits) {
			return TIER_6;
		} else if (kiloBits <= TIER_7.maxKiloBits) {
			return TIER_7;
		} else if (kiloBits <= TIER_8.maxKiloBits) {
			return TIER_8;
		} else if (kiloBits <= TIER_9.maxKiloBits) {
			return TIER_9;
		} else {
			return DEFAULT_TIER;
		}
	}
	
	/**
	 * @param kiloBits QoS value in kilobits
	 * @return rounded QoS value in kilobits
	 */
	public static long roundOff(long kiloBits) {
		
		if (kiloBits <= DEFAULT_TIER.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, DEFAULT_TIER.increment);
		} else if (kiloBits <= TIER_1.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_1.increment);
		} else if (kiloBits <= TIER_2.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_2.increment);
		} else if (kiloBits <= TIER_3.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_3.increment);
		} else if (kiloBits <= TIER_4.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_4.increment);
		} else if (kiloBits <= TIER_5.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_5.increment);
		} else if (kiloBits <= TIER_6.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_6.increment);
		} else if (kiloBits <= TIER_7.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_7.increment);
		} else if (kiloBits <= TIER_8.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_8.increment);
		} else if (kiloBits <= TIER_9.maxKiloBits) {
			return EPSQoSRoundingTable.roundUpper(kiloBits, TIER_9.increment);
		} else {
			return EPSQoSRoundingTable.roundUpper(kiloBits, DEFAULT_TIER.increment);
		}
	}
	
	private static long roundUpper(long kiloBits, double increment) {
		return (long) (Math.ceil(kiloBits/increment) * increment);
	}
	
	public abstract long roundUpper(long kiloBits);
}
