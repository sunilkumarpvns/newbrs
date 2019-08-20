package com.elitecore.aaa.radius.service.auth.handlers;


public enum BWMode {
	
	Post_Policy(1),  // will be apply after service policy selected.
	Post_Profile(2); //	will be apply after subscriber profile fetched.
	
	public static final BWMode[] VALUES = values();
	
	public final int id;
	BWMode(int id){
		this.id = id;
	}
	
	public static BlackListValidator getHandler(int id) {
		if(id==Post_Policy.id){
			return new PostPolicyValidator();
		}else if (id==Post_Profile.id) {
			return new PostProfileValidator();
		}
		return null;
	}

}
