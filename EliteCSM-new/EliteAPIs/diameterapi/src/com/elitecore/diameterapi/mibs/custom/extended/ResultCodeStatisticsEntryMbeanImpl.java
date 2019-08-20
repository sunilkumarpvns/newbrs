package com.elitecore.diameterapi.mibs.custom.extended;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.sql.Types;
import java.util.Map;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.ResultCodeStatisticsEntry;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.elitecore.diameterapi.mibs.statistics.ResultCodeTuple;
import com.sun.management.snmp.SnmpStatusException;

public class ResultCodeStatisticsEntryMbeanImpl extends ResultCodeStatisticsEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	transient @Nonnull private final ApplicationStatsIdentifier applicationIdentifier;
	transient @Nonnull private final DiameterStatisticListener diameterStatisticListener;
	@Nonnull private final String peerIdentity;
	private final long resultCode;
	private String compositeIndex;
	
	public ResultCodeStatisticsEntryMbeanImpl(@Nonnull ApplicationStatsIdentifier applicationIdentifier,
			@Nonnull String hostIdentity, long resultCode,
			@Nonnull DiameterStatisticListener diameterStatisticListener) {
		
		this.applicationIdentifier = checkNotNull(applicationIdentifier, "applicationIdentifier is null.");
		this.peerIdentity = checkNotNull(hostIdentity, "hostIdentity is null.");
		this.diameterStatisticListener = checkNotNull(diameterStatisticListener, "diameterStatisticListener is null.");
		this.resultCode = resultCode;
	}
	
	public void setCompositeIndex(String compositeIndex) {
		this.compositeIndex = compositeIndex;
	}

	/**
     * Getter for the "RcwCompstIndexValue" variable.
     */
    public String getRcwCompstIndexValue() throws SnmpStatusException {
        return compositeIndex;
    }

    /**
     * Getter for the "RcwApplicationName" variable.
     */
    @Column(name = "applicationName", type = Types.VARCHAR)
    public String getRcwApplicationName() throws SnmpStatusException {
        return applicationIdentifier.getApplication();
    }

    /**
     * Getter for the "RcwPeerIdentity" variable.
     */
    @Column(name = "peerIdentity", type = Types.VARCHAR)
    public String getRcwPeerIdentity() throws SnmpStatusException {
        return peerIdentity;
    }

    /**
     * Getter for the "RcwTx" variable.
     */
    @Column(name = "rcwTx", type = Types.BIGINT)
    public Long getRcwTx() throws SnmpStatusException {
    	return getResultCodeTuple().getResultCodeOut();
    }

    /**
     * Getter for the "RcwRx" variable.
     */
    @Column(name = "rcwRx", type = Types.BIGINT)
    public Long getRcwRx() throws SnmpStatusException {
        return getResultCodeTuple().getResultCodeIn();
    }

    private ResultCodeTuple getResultCodeTuple() throws SnmpStatusException {
		
		Map<String, GroupedStatistics> appToPeerStatisticsMap = diameterStatisticListener.
				getDiameterStatisticProvider().getApplicationPeerMap().get(this.applicationIdentifier);
		
		if(Maps.isNullOrEmpty(appToPeerStatisticsMap)) {
			throw new SnmpStatusException("Group statistics is not found for application: "+ applicationIdentifier);
		}
		
		ResultCodeTuple resultCodeTuple = appToPeerStatisticsMap.get(this.peerIdentity).
				getResultCodeCountersMap().get((int)resultCode);
		
		if(resultCodeTuple == null) {
			throw new SnmpStatusException("Statistics of result code:" + this.resultCode +" " +
					"not found for peer: "+ this.peerIdentity);
		}
		return resultCodeTuple;
	}
    
    /**
     * Getter for the "ResultCode" variable.
     */
    @Column(name = "resultCode", type = Types.BIGINT)
    public Long getResultCode() throws SnmpStatusException {
        return resultCode;
    }

    /**
     * Getter for the "PeerIndex" variable.
     */
    @Column(name = "peerIndex", type = Types.BIGINT)
    public Long getPeerIndex() throws SnmpStatusException {
        return diameterStatisticListener.getDiameterConfigProvider().getPeerConfig(this.peerIdentity).getDbpPeerIndex();
    }

    /**
     * Getter for the "ApplicationID" variable.
     */
    @Column(name = "applicationID", type = Types.BIGINT)
    public Long getApplicationID() throws SnmpStatusException {
        return applicationIdentifier.getApplicationId();
    }
    
    
    public String getObjectName() {
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_APP_RESULT_CODE_WISE_TABLE 
				+ peerIdentity  
				+ "-" + applicationIdentifier.getApplicationId()+ "(" + applicationIdentifier.getApplication() + ")-"
				+resultCode;
	}
    
    

}
