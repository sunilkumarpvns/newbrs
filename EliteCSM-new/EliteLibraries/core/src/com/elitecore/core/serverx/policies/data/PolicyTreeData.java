package com.elitecore.core.serverx.policies.data;



public class PolicyTreeData implements ITreeData {
	private String policiesToBeApplied;
	private String overridedCheckItems;
	private String overridedRejectItems;
	private String overridedReplyItems;
	private String overridedAddItems;
	private PolicyOverrideData policyOverrideData;

	public void setPoliciesToBeApplied(String policiesToBeApplied) {
		this.policiesToBeApplied = policiesToBeApplied;
	}

	public String getPoliciesToBeApplied() {
		return policiesToBeApplied;
	}

	public void setOverridedCheckItems(String overridedCheckItems) {
		this.overridedCheckItems = overridedCheckItems;
	}

	public String getOverridedCheckItems() {
		return overridedCheckItems;
	}

	public void setOverridedRejectItems(String overridedRejectItems) {
		this.overridedRejectItems = overridedRejectItems;
	}

	public String getOverridedRejectItems() {
		return overridedRejectItems;
	}

	public void setOverridedReplyItems(String overridedReplyItems) {
		this.overridedReplyItems = overridedReplyItems;
	}

	public String getOverridedReplyItems() {
		return overridedReplyItems;
	}

	public void setOverridedAddItems(String overridedAddItems) {
		this.overridedAddItems = overridedAddItems;
	}

	public String getOverridedAddItems() {
		return overridedAddItems;
	}

	public PolicyOverrideData getPolicyOverrideData() {
		return policyOverrideData;
	}
	
	public void setPolicyOverrideData(PolicyOverrideData policyOverrideData){
		this.policyOverrideData = policyOverrideData;
	}
}
