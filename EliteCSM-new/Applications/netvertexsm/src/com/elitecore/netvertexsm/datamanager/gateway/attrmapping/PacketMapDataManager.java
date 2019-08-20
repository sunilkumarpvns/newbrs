package com.elitecore.netvertexsm.datamanager.gateway.attrmapping;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;

public interface PacketMapDataManager extends DataManager{

    /**
     * This method returns all the packetMappingData matching the give criteria with Paging.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param  packetMappingData, pageNo, pageSize
     * @return List
     */
    public PageList search(PacketMappingData packetMappingData, int pageNo, int pageSize) throws DataManagerException;
    
    /** 
     * This method creates a packetMappingData.
     * @param  packetMappingData
     * @return
     */
    public void create(PacketMappingData packetMappingData) throws DataManagerException;
    
    public void update(PacketMappingData packetMappingData) throws DataManagerException;
    
    public PacketMappingData getPacketMappingData(PacketMappingData packetMappingData) throws DataManagerException; 

    public void delete(List<Long> ids) throws DataManagerException;

	public List<PacketMappingData> getPacketMappingList(String commProtocol) throws DataManagerException;
	
	public void changeMappingOrder(Long[] order, long packetMapId) throws DataManagerException;
	
	public List searchPacketMap(long profileId) throws DataManagerException;

	public List<PacketMappingData> getPCRFToDiameterPacketMappingList() throws DataManagerException;
	public List<PacketMappingData> getDiameterToPCRFPacketMappingList() throws DataManagerException;
	
	public List<PacketMappingData> getPCRFToRadiusPacketMappingList() throws DataManagerException;
	public List<PacketMappingData> getRadiusToPCRFPacketMappingList() throws DataManagerException;
	public boolean isPacketMappingConfiguredWithGatewayProfile(Long[] packetMapIds)  throws DataManagerException;
	public GatewayProfileData getGatewayProfileList(long profileId)  throws DataManagerException ;
	public List<GatewayProfileData> getPacketMapConfiguredProfileList(long packetMapId)  throws DataManagerException ;
    
}
