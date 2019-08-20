package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

import java.io.Serializable;

public class SIDDetailData implements Serializable {
	
	private static final long serialVersionUID = 2855893144785617067L;
	private String sidName;
	  private int sidCount;
	  
	  public SIDDetailData(String sidName,int sidCount)
	  { 
		this.sidName=sidName;
		this.sidCount=sidCount;
	  }
	  
		public String getSidName() {
			return sidName;
		}
		public void setSidName(String sidName) {
			this.sidName = sidName;
		}
		public int getSidCount() {
			return sidCount;
		}
		public void setSidCount(int sidCount) {
			this.sidCount = sidCount;
		}
}
