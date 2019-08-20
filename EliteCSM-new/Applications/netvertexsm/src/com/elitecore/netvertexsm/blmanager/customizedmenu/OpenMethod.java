package com.elitecore.netvertexsm.blmanager.customizedmenu;


public enum OpenMethod {
		SAME_WINDOW("self"),
		NEW_WINDOW("blank"),
		UNKNOWN("unknown");
		
		public String methodType;
		
		private OpenMethod(String method){
			this.methodType = method;
		}
		public static OpenMethod fromString(String method){
			if(method.equalsIgnoreCase(SAME_WINDOW.methodType)){
				return SAME_WINDOW;
			}else if(method.equalsIgnoreCase(NEW_WINDOW.methodType)){
				return NEW_WINDOW;
			}else{
				return UNKNOWN;
			}
		}
}
