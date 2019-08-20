package com.elitecore.nvsmx.system.keys;

/**
 * @author kirpalsinh.raj
 *
 */
public enum ActionMessageKeys {
	
	DELETE_SUCCESS("delete.success"),
	CREATE_SUCCESS("create.success"),
	UPDATE_SUCCESS("update.success"),
	
	VIEW_FAILURE("view.failure"),
	CREATE_FAILURE("create.failure"),
	UPDATE_FAILURE("update.failure"),
	DELETE_FAILURE("delete.failure"),
	SEARCH_FAILURE("search.failure"),
	REFRESH_FAILURE("refresh.failure"),
	IMPORT_FAILURE("import.failure"),
	EXPORT_FAILURE("export.failure"),
	
	
	UNKNOWN_INTERNAL_ERROR("unknown.internal.error"),
	DATA_NOT_FOUND("data.not.found");
	
	public String key;
	ActionMessageKeys(String key){
		this.key =  key;
	}
}
