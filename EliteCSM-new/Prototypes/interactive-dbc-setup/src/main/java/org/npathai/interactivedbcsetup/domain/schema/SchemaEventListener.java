package org.npathai.interactivedbcsetup.domain.schema;

import java.util.EventListener;

public interface SchemaEventListener extends EventListener {
	void starting(int count);
	
	void creating(String table);

	void created(String table);

	void error(String table, String message);

	void dropping(String table);

	void dropped(String table);
	
}
