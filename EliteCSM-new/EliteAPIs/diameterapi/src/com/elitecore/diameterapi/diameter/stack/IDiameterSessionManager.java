package com.elitecore.diameterapi.diameter.stack;

import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public interface IDiameterSessionManager {
	
	public static final String MARK_FOR_DROP_REQUEST = "markForDropRequest";
	public static final String FURTHER_PROCESSING_REQUIRED = "isFurtherProcessingRequired";
	public static final String PROCESSING_COMPLETED = "processingCompleted";
	
	public void init() throws InitializationFailedException;
	
	public List<SessionData> locate(DiameterRequest diameterRequest,@Nullable DiameterAnswer diameterAnswer);
	public int save(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer);
	public int update(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer);
	public int delete(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer);
	public int updateOrSave(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer, List<SessionData> list);
	public int delete(List<SessionData> sessionData);
	public int truncate();
}
