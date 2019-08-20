package com.elitecore.netvertex.core.session.conf;

import java.util.List;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.corenetvertex.util.ToStringable;

/**
 * @author Harsh Patel
 *
 */
public interface SessionManagerConfiguration extends ToStringable {

	List<FieldMapping> getCoreSessionFieldMappings();
	List<FieldMapping> getSessionRuleFieldMappings();
	long getBatchUpdateIntervalInSec();
	boolean isBatchUpdateEnable();
	long getBatchSize();
	int getBatchQueryTimeout();
	boolean isSaveInBatch();
	boolean isUpdateInBatch();
	boolean isDeleteInBatch();
}
