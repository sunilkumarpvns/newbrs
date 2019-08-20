package com.elitecore.aaa.core.conf.context;

public enum AAAConfigurationState {
	NORMAL, //context starts with this state and reads fresh configuration
	FALLBACK_CONFIGURATION, //context switches to this state if DB error
	UNRECOVERABLE; //context switches to this state if unrecoverable situation occurs
}
