package com.elitecore.corenetvertex.pkg;

import java.util.HashMap;
import java.util.Map;


/**
 * @author kirpalsinh.raj
 *
 */
public enum PkgMode {

	LIVE2("LIVE2",null,4),
	LIVE("LIVE",LIVE2,3),
	TEST("TEST",LIVE,2),
	DESIGN("DESIGN",TEST,1);

	public String val;
	private PkgMode  nextMode;
	private int order;

	static Map<String,PkgMode> valToMode = null;
	static{
		valToMode = new HashMap<>();
		for(PkgMode mode : values()){
			valToMode.put(mode.val, mode);
		}
	}

	PkgMode(String val,PkgMode nextMode, int order){
		this.val = val;
		this.nextMode = nextMode;
		this.order = order;
	}

	public PkgMode getNextMode(){
		return nextMode;
	}
	public int getOrder(){
		return order;
	}

	public static PkgMode getMode(String val){
		return valToMode.get(val);
	}

	public static int compare(PkgMode pkgMode1, PkgMode pkgMode2) {
		if (pkgMode1.getOrder() < pkgMode2.getOrder()) {
			return -1;
		} else if (pkgMode1.getOrder() > pkgMode2.getOrder()) {
			return 1;
		}
		return 0;

	}

}
