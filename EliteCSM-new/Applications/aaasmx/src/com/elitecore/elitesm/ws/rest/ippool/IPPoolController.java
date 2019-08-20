package com.elitecore.elitesm.ws.rest.ippool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.rm.ippool.IPPoolBaseAction;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class IPPoolController extends IPPoolBaseAction{
	
	private static final String IP_POOL = "IP Pool";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
		@QueryParam(value = "name") String ipPoolName) throws DataManagerException {
		
		IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
		
		IPPoolData ipPoolData = (IPPoolData) ipPoolBLManager.getIPPoolByName(ipPoolName);
		
		Set<IPPoolDetailData> ipPoolDetailDatas = new HashSet<IPPoolDetailData>();
		List<IIPPoolDetailData> lstIPPoolDetail = new ArrayList<IIPPoolDetailData>();
		
		IIPPoolDetailData ipPoolDetailData = new IPPoolDetailData();
		ipPoolDetailData.setIpPoolId(ipPoolData.getIpPoolId());
		lstIPPoolDetail = ipPoolBLManager.getDistinctIPPoolDetailByRangeList(ipPoolDetailData);
		
		for (IIPPoolDetailData iipPoolDetailData : lstIPPoolDetail) {
			ipPoolDetailDatas.add((IPPoolDetailData) iipPoolDetailData);
		}
		
		ipPoolData.setIpPoolDetail(ipPoolDetailDatas);
		return Response.ok(ipPoolData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@QueryParam(value = "name") String ipPoolName) throws DataManagerException {
		return getByNameFromQuery(ipPoolName);
	}
	
	@POST
	public Response create(@Valid IPPoolData ipPoolData) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
		
		Set<IPPoolDetailData> ipPoolDetailSet = ipPoolData.getIpPoolDetail(); 
		int ipPoolDetailSize = ipPoolDetailSet.size();
		
		String[] ipPoolRangeID = new String[ipPoolDetailSize];
		String[] ipPoolRangeAddress = new String[ipPoolDetailSize];
		
		int cnt = 0; 
		for(IPPoolDetailData ipPoolDetailData : ipPoolDetailSet){
			ipPoolRangeID[cnt] = ipPoolDetailData.getIpAddressRangeId();
			ipPoolRangeAddress[cnt] = ipPoolDetailData.getIpAddressRange();
			cnt++;
		}
		
		ipPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByIPRange( ipPoolRangeAddress,ipPoolRangeID, null, null));
		
		ipPoolBLManager.create(ipPoolData, staffData);
		
		return Response.ok(RestUtitlity.getResponse("IP Pool created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<IPPoolData> ipPoolDataList, @Context UriInfo uri) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
		
		List<IPPoolData> ipPoolDatas = ipPoolDataList.getList();
		List<IIPPoolData> iIPPoolDatas = new ArrayList<IIPPoolData>();
		
		for(IPPoolData ipPoolData : ipPoolDatas){
			
			Set<IPPoolDetailData> ipPoolDetailSet = ipPoolData.getIpPoolDetail(); 
			int ipPoolDetailSize = ipPoolDetailSet.size();
			
			String[] rangeIdAry = new String[ipPoolDetailSize];
			String[] ipAddressRangeAry = new String[ipPoolDetailSize];
			
			int cnt = 0; 
			for(IPPoolDetailData ipPoolDetailData : ipPoolDetailSet){
				rangeIdAry[cnt] = ipPoolDetailData.getIpAddressRangeId();
				ipAddressRangeAry[cnt] = ipPoolDetailData.getIpAddressRange();
				cnt++;
			}
			
			ipPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByIPRange(ipAddressRangeAry, rangeIdAry, null, null));
			iIPPoolDatas.add(ipPoolData);
		}
		
		Map<String, List<Status>> responseMap = ipPoolBLManager.create(iIPPoolDatas, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(IP_POOL,"(s) created successfully", responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateIPPoolByQueryParam(@Valid IPPoolData ipPoolData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
		IPPoolData oldIPPoolData = (IPPoolData) ipPoolBLManager.getIPPoolByName(name);
		ipPoolData.setIpPoolId(oldIPPoolData.getIpPoolId());
		ipPoolData.setCreatedByStaffId(oldIPPoolData.getCreatedByStaffId());
		ipPoolData.setCommonStatusId(oldIPPoolData.getCommonStatusId());
		ipPoolData.setLastModifiedByStaffId(oldIPPoolData.getLastModifiedByStaffId());
		ipPoolData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		ipPoolData.setCreateDate(oldIPPoolData.getCreateDate());
		ipPoolData.setSystemGenerated(oldIPPoolData.getSystemGenerated());
		ipPoolData.setEditable(oldIPPoolData.getEditable());

		int newIPPoolDetailSize = ipPoolData.getIpPoolDetail().size();
		int oldIPPoolDetailSize = oldIPPoolData.getIpPoolDetail().size();

		String[] newIPAddresRangeIdArray = new String[newIPPoolDetailSize];
		String[] newIPAddressRangeArray = new String[newIPPoolDetailSize];
		String[] oldIPAddresRangeIdArray = new String[oldIPPoolDetailSize];
		String[] oldIPAddressRangeArray = new String[oldIPPoolDetailSize];
		
		Set<IPPoolDetailData> ipPoolDetailDataSet = new LinkedHashSet<IPPoolDetailData>();

		int newCnt = 0; 
		for(IPPoolDetailData ipPoolDetailData : ipPoolData.getIpPoolDetail()){
			newIPAddresRangeIdArray[newCnt] = ipPoolDetailData.getIpAddressRangeId();
			newIPAddressRangeArray[newCnt] = ipPoolDetailData.getIpAddressRange();
			ipPoolDetailDataSet.add(ipPoolDetailData);
			newCnt++;
		}
		
		Set<IPPoolDetailData> ipPoolDetailDatas = new HashSet<IPPoolDetailData>();
		List<IIPPoolDetailData> lstIPPoolDetail = new ArrayList<IIPPoolDetailData>();
		
		IIPPoolDetailData ipPoolDtlData = new IPPoolDetailData();
		ipPoolDtlData.setIpPoolId(ipPoolData.getIpPoolId());
		lstIPPoolDetail = ipPoolBLManager.getDistinctIPPoolDetailByRangeList(ipPoolDtlData);
		
		for (IIPPoolDetailData iipPoolDetailData : lstIPPoolDetail) {
			ipPoolDetailDatas.add((IPPoolDetailData) iipPoolDetailData);
		}

		int oldCnt = 0; 
		for(IPPoolDetailData ipPoolDetailData : ipPoolDetailDatas){
			oldIPAddresRangeIdArray[oldCnt] = ipPoolDetailData.getIpAddressRangeId();
			oldIPAddressRangeArray[oldCnt] = ipPoolDetailData.getIpAddressRange();
			oldCnt++;
		}

		for(IPPoolDetailData ipPoolDetailData : oldIPPoolData.getIpPoolDetailSet()){
			ipPoolDetailDataSet.add(ipPoolDetailData);
		}

		ipPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByIPRange(newIPAddressRangeArray, newIPAddresRangeIdArray, null, null));
		ipPoolData.setIpPoolDetailSet(ipPoolDetailDataSet);
		
		ipPoolBLManager.updateByName(ipPoolData, false,staffData,ConfigConstant.UPDATE_IP_POOL_ACTION);
		return Response.ok(RestUtitlity.getResponse("IP Pool updated successfully")).build();
	}
	
	@PUT
	@Path(value = "/{name}")
	public Response updateIPPoolByPathParam(@Valid IPPoolData ipPoolData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value="name") String name)
					throws DataManagerException, JAXBException {
		return updateIPPoolByQueryParam(ipPoolData, name);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String ipPoolName) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
		
		List<String> ipPoolNames = Arrays.asList(ipPoolName.split(","));
		
		ipPoolBLManager.deleteByName(ipPoolNames,staffData);
		return Response.ok(RestUtitlity.getResponse("IP Pool(s) deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String ipPoolName) throws DataManagerException {
		return deleteByQueryParam(ipPoolName);
	}
	
	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response uploadFile(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name,List<Attachment> attachments) throws DataManagerException, IOException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
		
		IPPoolData oldIPPoolData = (IPPoolData) ipPoolBLManager.getIPPoolByName(name);
		
		for (Attachment attachment : attachments) {
			DataHandler dataHandler = attachment.getDataHandler();
			
			MultivaluedMap<String, String> multivaluedMap = attachment.getHeaders();
			if(multivaluedMap.getFirst("Content-Disposition") == null){
				throw new InvalidValueException("Attachment is missing");
			}
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataHandler.getInputStream()));
			
			if(Strings.isNullOrEmpty(getFileName(multivaluedMap)) == false){
				oldIPPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByCSVFile(getFileName(multivaluedMap),bufferedReader));
				ipPoolBLManager.update(oldIPPoolData, true,staffData,ConfigConstant.UPDATE_IP_POOL_ACTION);
			} 
		}
		return Response.ok(RestUtitlity.getResponse("File uploaded successfully")).build();
	}
	
	@GET
	@Path("/help/")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.IP_POOL);
	}
	
	private String getFileName(MultivaluedMap<String, String> multivaluedMap) {

		String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");
		for (String filename : contentDisposition) {

			if ((filename.trim().startsWith("filename"))) {
				String[] name = filename.split("=");
				String exactFileName = name[1].trim().replaceAll("\"", "");
				return exactFileName;
			}
		}
		return "unknownFile";
	}
}
