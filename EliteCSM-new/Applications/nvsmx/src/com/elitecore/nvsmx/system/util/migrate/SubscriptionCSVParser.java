package com.elitecore.nvsmx.system.util.migrate;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.lang.Long.parseLong;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.subscriberimport.InputType;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionCSVParser implements SubscriberDataParser {
	
	private static final String MODULE = "SUBSCRIPTION-CSV-PARSER";
	private List<ColumnNameData> subscriptionCSVHeaders;
	private final String DATE_FORMAT;
	
	public SubscriptionCSVParser(List<ColumnNameData> subscriptionCSVHeaders, String dateFormat) {
		this.subscriptionCSVHeaders = subscriptionCSVHeaders;
		this.DATE_FORMAT = dateFormat;
	}

	@Override
	public NV648SubscriberInfo parse(String dataString) {

		if (Collectionz.isNullOrEmpty(subscriptionCSVHeaders)) {
			throw new IllegalArgumentException("Subscription CSV Headers not configured");
		}
		
		List<String> values = COMMA_SPLITTER.split(dataString);

		NV648SubscriptionInfo subscriptionInfo = new NV648SubscriptionInfo();
		for (ColumnNameData columnNameData : subscriptionCSVHeaders) {

			if (values.size() < columnNameData.getIndex()) {
				continue;
			}

			String value = values.get(columnNameData.getIndex() - 1);

			SubscriptionCSVHeader subscriptionCSVHeader = SubscriptionCSVHeader.fromVal(columnNameData.getName());
			
			if (subscriptionCSVHeader != null) {
				setValue(subscriptionCSVHeader, subscriptionInfo, value);
			} else {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping not configured column: " + columnNameData.getName());
				}
				continue;
			}
		}

		NV648SubscriberInfo nv648SubscriberInfo = new NV648SubscriberInfo();
		nv648SubscriberInfo.setSubscriptionInfos(Arrays.asList(subscriptionInfo));
		
		return nv648SubscriberInfo;
	}

	private void setValue(SubscriptionCSVHeader subscriptionCSVHeader, NV648SubscriptionInfo subscriptionInfo, String value) {

		switch (subscriptionCSVHeader.type) {
			case Types.VARCHAR:
				if (Strings.isNullOrBlank(value) == false) {
					subscriptionCSVHeader.setValue(subscriptionInfo, value);
				}
				break;
			case Types.NUMERIC:
				if (Strings.isNullOrBlank(value) == false) {
					subscriptionCSVHeader.setValue(subscriptionInfo, parseLong(value));
				}
				break;
			case Types.TIMESTAMP:
				long time;
				try {
					time = new SimpleDateFormat(DATE_FORMAT).parse(value).getTime();
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
				subscriptionCSVHeader.setValue(subscriptionInfo, new Timestamp(time));
				break;
		}
	}

	@Override
	public InputType getInputType() {
		return InputType.CSV_SUBSCRIPTION;
	}

	private enum SubscriptionCSVHeader {

		SUBSCRIPTION_ID("SUBSCRIPTION_ID", Types.VARCHAR) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, String value) {
				subscriptionInfo.setSubscription(value);
			}
		},
		NAME("NAME", Types.VARCHAR) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, String value) {
				subscriptionInfo.setPackageName(value);
			}
		},
		SUBSCRIBERIDENTITY("SUBSCRIBERIDENTITY", Types.VARCHAR) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, String value) {
				subscriptionInfo.setSubscriberid(value);
			}
		},
		STARTTIME("STARTTIME", Types.TIMESTAMP) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, Timestamp value) {
				subscriptionInfo.setStartTime(value);
			}
		},
		ENDTIME("ENDTIME", Types.TIMESTAMP) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, Timestamp value) {
				subscriptionInfo.setEndTime(value);
			}
		},
		UPLOADOCTETS("UPLOADOCTETS", Types.NUMERIC) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, long value) {
				subscriptionInfo.setUploadOctets(value);
			}
		},
		DOWNLOADOCTETS("DOWNLOADOCTETS", Types.NUMERIC) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, long value) {
				subscriptionInfo.setDownloadOctets(value);
			}
		},
		TOTALOCTETS("TOTALOCTETS", Types.NUMERIC) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, long value) {
				subscriptionInfo.setTotalOctets(value);
			}
		},
		USAGETIME("USAGETIME", Types.NUMERIC) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, long value) {
				subscriptionInfo.setUsageTime(value);
			}
		},
		PARAM1("PARAM1", Types.VARCHAR) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, String value) {
				subscriptionInfo.setParam1(value);
			}
		},

		PARAM2("PARAM2", Types.VARCHAR) {
			@Override
			public void setValue(NV648SubscriptionInfo subscriptionInfo, String value) {
				subscriptionInfo.setParam2(value);
			}
		};


		public String val;
		public int type;

		private SubscriptionCSVHeader(String val, int type) {
			this.val = val;
			this.type = type;
		}

		private static Map<String, SubscriptionCSVHeader> map;

		static {
			map = new HashMap<String, SubscriptionCSVHeader>();

			for (SubscriptionCSVHeader header : SubscriptionCSVHeader.values()) {
				map.put(header.val, header);
			}
		}

		public static SubscriptionCSVHeader fromVal(String val) {
			return map.get(val);
		}

		public void setValue(NV648SubscriptionInfo subscriptionInfo, String value) {
			throw new UnsupportedOperationException("set value with string value not supported for " + val);
		}

		public void setValue(NV648SubscriptionInfo subscriptionInfo, long value) {
			throw new UnsupportedOperationException("set value with numeric value not supported for " + val);
		}


		public void setValue(NV648SubscriptionInfo subscriptionInfo, Timestamp value) {
			throw new UnsupportedOperationException("set value with timestamp value not supported for " + val);
		}
	}
}
