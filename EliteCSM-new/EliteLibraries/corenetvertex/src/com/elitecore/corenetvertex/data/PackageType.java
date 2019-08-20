package com.elitecore.corenetvertex.data;

import com.elitecore.corenetvertex.pkg.PkgType;

public enum PackageType {

	    BASE_DATA_PACKAGE("DATA", PkgType.BASE.val),
		ADD_ON_DATA_PACKAGE("DATA", PkgType.ADDON.val),
		BASE_IMS_PACKAGE("IMS", PkgType.BASE.val),
		ADD_ON_IMS_PACKAGE("IMS", PkgType.ADDON.val),
		EMERGENCY("DATA",PkgType.EMERGENCY.val),
		PROMOTIONAL("DATA",PkgType.PROMOTIONAL.val),
		UNKNOWN("UNKNOWN","UNKNOWN");

		private final String type;

		private final String subType;
		PackageType(String type,String subType) {
			this.type = type;
			this.subType = subType;

		}
		public String getType() {
			return type;
		}

		public String getSubType() {
			return subType;
		}


	}