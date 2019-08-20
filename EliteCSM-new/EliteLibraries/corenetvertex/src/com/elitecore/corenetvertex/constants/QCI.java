package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;



public enum QCI {
	QCI_GBR_1("1 - GBR- Conversational Voice",1) {
		@Override
		public boolean isHigher(QCI qci) {
			return qci == QCI_GBR_2;
		}

		@Override
		public boolean isGBRQCI() {
			return true;
		}
	},
	QCI_GBR_2("2 - GBR - Conversational Video (Live Streaming) ",2) {
		@Override
		public boolean isHigher(QCI qci) {
			return false;
		}
		
		@Override
		public boolean isGBRQCI() {
			return true;
		}
	},
	QCI_GBR_3("3 - GBR - Real Time Gaming",3) {
		@Override
		public boolean isHigher(QCI qci) {
			return qci == QCI_GBR_2 || qci == QCI_GBR_1 || qci == QCI_GBR_4;
		}
		@Override
		public boolean isGBRQCI() {
			return true;
		}
	},
	QCI_GBR_4("4 - GBR - Non-Conversational Video ( Buffered )",4) {
		@Override
		public boolean isHigher(QCI qci) {
			return qci == QCI_GBR_2 || qci == QCI_GBR_1;
		}
		
		@Override
		public boolean isGBRQCI() {
			return true;
		}
	},
	QCI_NON_GBR_5("5 - Non-GBR - IMS Signalling",5) {
		@Override
		public boolean isHigher(QCI qci) {
			return qci == QCI_GBR_2 || qci == QCI_GBR_1 || qci == QCI_GBR_4 || qci == QCI_GBR_3;
		}
		
		@Override
		public boolean isGBRQCI() {
			return false;
		}
		
	},
	
	QCI_NON_GBR_6("6 - Non-GBR - Video, e-mail, chat, p2p, ftp etc.( Live )",6) {
		@Override
		public boolean isHigher(QCI qci) {
			return qci != QCI_NON_GBR_7 && qci != QCI_NON_GBR_8 && qci != QCI_NON_GBR_9 &&  qci != QCI_NON_GBR_6;
		}
		@Override
		public boolean isGBRQCI() {
			return false;
		}
	},
	
	QCI_NON_GBR_7("7 - Non-GBR - Voice, Video , Interactive Gaming",7) {
		@Override
		public boolean isHigher(QCI qci) {
			return qci != QCI_NON_GBR_8 && qci != QCI_NON_GBR_9 && qci != QCI_NON_GBR_7;
		}
		
		@Override
		public boolean isGBRQCI() {
			return false;
		}
	},
	QCI_NON_GBR_8("8 - Non-GBR - Video, e-mail, chat, p2p, ftp etc. ( Buffered )",8) {
		@Override
		public boolean isHigher(QCI qci) {
			return qci != QCI_NON_GBR_9 && qci != QCI_NON_GBR_8;
		}
		
		@Override
		public boolean isGBRQCI() {
			return false;
		}
	},
	QCI_NON_GBR_9("9 - Non-GBR - Video, e-mail, chat, p2p, ftp etc. ( Buffered )(low)",9) {
		@Override
		public boolean isHigher(QCI qci) {
			return false;
		}
		
		@Override
		public boolean isGBRQCI() {
			return false;
		}
	};
	
	public final String displayValue;
	public final int val;
	public final String stringVal;
	
	
	private static final Map<String, QCI> map = new HashMap<String, QCI>();
	static {
		for (QCI qci : QCI.values()) {
			map.put(qci.stringVal, qci);
		}
	}

	private QCI(String displayValue, int val){
		  this.displayValue= displayValue;
		  this.val = val;
		  this.stringVal = String.valueOf(val);
	}
	
	public String getDisplayValue(){
		return displayValue;
	}
	
	public int getQci(){
		return val;
	}
	
	public static QCI fromId(String qciId) {
		return map.get(qciId);
	}
	
	public static QCI fromId(int qciId) {
		switch (qciId) {
			case 1:
				return QCI_GBR_1;
			case 2:
				return QCI_GBR_2;
			case 3:
				return QCI_GBR_3;
			case 4:
				return QCI_GBR_4;
			case 5:
				return QCI_NON_GBR_5;
			case 6:
				return QCI_NON_GBR_6;
			case 7:
				return QCI_NON_GBR_7;
			case 8:
				return QCI_NON_GBR_8;
			case 9:
				return QCI_NON_GBR_9;
		}
		
		return null;
	}
	
	public abstract boolean isHigher(QCI qci);
	public abstract boolean isGBRQCI();

	public static QCI getDefault(){
		return QCI_NON_GBR_6;
	}

}
