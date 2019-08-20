package com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory;

public enum JVMMemoryPoolConstant {

	CODE_CACHE(1,"Code Cache"),
	PS_EDEN_SPACE(2,"PS Eden Space"),
	PS_SURVIVOR_SPACE(3,"PS Survivor Space"),
	PS_OLD_GEN(4,"PS Old Gen"),
	PS_PERM_GEN(5,"PS Perm Gen");

	public final int poolIndex;
	public final String poolName;

	private JVMMemoryPoolConstant(int poolIndex,String poolName){
		this .poolIndex=poolIndex;
		this.poolName=poolName;
	}

	public int getVal(){
		return poolIndex;
	}

	public String getStringVal() {
		return String.valueOf(poolIndex);
	}

	public String getName() {
		return poolName;
	}

	public static JVMMemoryPoolConstant fromValue(int poolIndex) {

		switch(poolIndex){
		case 1: return CODE_CACHE;

		case 2: return PS_EDEN_SPACE;

		case 3: return PS_SURVIVOR_SPACE;

		case 4: return PS_OLD_GEN; 

		case 5: return PS_PERM_GEN;

		default: return null;

		}
	}
}