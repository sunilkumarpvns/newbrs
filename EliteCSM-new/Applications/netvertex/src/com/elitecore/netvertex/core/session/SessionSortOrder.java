package com.elitecore.netvertex.core.session;

import com.elitecore.core.serverx.sessionx.SessionData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public enum SessionSortOrder {

	ASCENDING {
		@Override
		public void sort(List<SessionData> sessionDatas) {
			Collections.sort(sessionDatas, ascComparator);
		}
	},

	DESCENDING {
		@Override
		public void sort(List<SessionData> sessionDatas) {
			Collections.sort(sessionDatas, descComparator);
		}
	},

	LAST_UPDATE_TIME_ASCENDING {
		@Override
		public void sort(List<SessionData> sessionDatas) {
	        Collections.sort(sessionDatas,lastUpdateTimeAscComparator);
		}
	},

	LAST_UPDATE_TIME_DESCENDING {
		@Override
		public void sort(List<SessionData> sessionDatas) {
			Collections.sort(sessionDatas,lastUpdateTimeDescComparator);
		}
	};



	private static Comparator<SessionData> ascComparator = new Comparator<SessionData>() {

		@Override
		public int compare(SessionData o1, SessionData o2) {
			if (o1.getCreationTime().before(o2.getCreationTime())) {
				return -1;
			} else if (o1.getCreationTime().after(o2.getCreationTime())) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	private static Comparator<SessionData> descComparator = new Comparator<SessionData>() {

		@Override
		public int compare(SessionData o1, SessionData o2) {
			if (o1.getCreationTime().before(o2.getCreationTime())) {
				return 1;
			} else if (o1.getCreationTime().after(o2.getCreationTime())) {
				return -1;
			} else {
				return 0;
			}
		}
	};

	private static Comparator<SessionData> lastUpdateTimeAscComparator = new Comparator<SessionData>() {

		@Override
		public int compare(SessionData o1, SessionData o2) {
			if(Objects.isNull(o1.getLastUpdateTime()) && Objects.isNull(o2.getLastUpdateTime())){
				return 0;
			}
			if (o1.getLastUpdateTime().before(o2.getLastUpdateTime())) {
				return -1;
			} else if (o1.getLastUpdateTime().after(o2.getLastUpdateTime())) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	private static Comparator<SessionData> lastUpdateTimeDescComparator = new Comparator<SessionData>() {

		@Override
		public int compare(SessionData o1, SessionData o2) {
			if(Objects.isNull(o1.getLastUpdateTime()) && Objects.isNull(o2.getLastUpdateTime())){
				return 0;
			}

			if (o1.getLastUpdateTime().before(o2.getLastUpdateTime())) {
				return 1;
			} else if (o1.getLastUpdateTime().after(o2.getLastUpdateTime())) {
				return -1;
			} else {
				return 0;
			}
		}
	};


	public abstract void sort(List<SessionData> sessionDatas);
}
