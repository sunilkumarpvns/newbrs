<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"%>
<%@page import="java.util.List"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverVendorData"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverRealmsData"%>
<%@page import="java.util.Set"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverPeerData"%>

<%
DiameterChargingDriverData diameterChargingDriverData = (DiameterChargingDriverData)request.getAttribute("diameterChargingDriverData");
List<TranslationMappingConfData> transConfList = (List<TranslationMappingConfData>) request.getAttribute("transConfList");
Set<DiameterChargingDriverRealmsData> diameterChargingDriverRealmDataSet = diameterChargingDriverData.getDriverRealmsRelSet(); 
%>

<script language="javascript">
 	var realmIndex=0;
 	var flag = false;
	var mainArray = new Array();
	var count = 0;
	
	
	function validateForm(){
		for(var i=1;i<=realmIndex;i++){
			if(document.getElementById("rname"+i) != null) {	
				if(document.getElementById("rname"+i).value == null || document.getElementById("rname"+i).value.length <= 0){
					alert("Enter Realm Name for Realm-" + i);
					return;
				}
			}
		}	
			
		add();
	 	document.forms[0].submit();
	}
	
	function add(){
		$("#mainform").append("<input type='hidden' name='realmIndex' value='"+realmIndex+"' />");
	}
	
	
	
	function removeComponent(compId){
		$(compId).remove();
	}
	
	function addRealm() {
		realmIndex++;
		var myArray = new Array();
		
		
		var mapIndex = realmIndex;
		var html  = $.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/servermgr/drivers/radius/RealmsConfig.jsp",
			   async:false,
			   data: "realmIndexParam="+realmIndex
		 }).responseText;
		
		
		var div = document.getElementById("realmDiv");
		
		$(div).append(html);
		
		var tableId = "table"+realmIndex;
		var inMessageId = "inMessageMapping"+realmIndex;
		document.getElementById(tableId).scrollIntoView();
		document.getElementById(inMessageId).focus();
		
	}
	function addNewVendor(tableId){
		newVendor(tableId,'','','');	
	}

	function newVendor(tableId,vendorIdValue,authIdValue,acctIdValue){
		var vendorId = "vendorId"+tableId;
		var authAppId = "authAppId"+tableId;
		var acctAppId = "acctAppId"+tableId;
		$("#"+tableId+" tr:last").after("<tr>"+
										"<td class='tblfirstcol'><input name="+vendorId+" type='text' value='"+vendorIdValue+"' style='width: 100%;' /></td>"+
										"<td class='tblrows'> <input name="+authAppId+" type='text' value='"+authIdValue+"' style='width: 100%;' /> </td>"+
										"<td class='tblrows'> <input name="+acctAppId+" type='text' value='"+acctIdValue+"' style='width: 100%;' /> </td>"+
										"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
										"</tr>");
	}
	function addNewPeer(tableId){
		newPeer(tableId,'','','','');		
	}
	function newPeer(tableId,peerNameValue,commPortValue,attemptConnValue,watchDogIntervalValue){
		var peerName = "peerName"+tableId;
		var communicationPort = "communicationPort"+tableId;
		var attemptConnection = "attemptConnection"+tableId;
		var watchdogInterval = "watchdogInterval"+tableId;

		var attempConnectionOptions  = "";
		var trueSelection = '';
		var falseSelection = '';
		if(attemptConnValue=='true'){
			trueSelection = "selected='selected'";
		}else{
			falseSelection = "selected='selected'";
		}
		
        var strTR = "<tr>"+
				"<td class='tblfirstcol'><input name="+peerName+" type='text' value='"+peerNameValue+"' style='width: 100%;' /></td>"+
				"<td class='tblrows'> <input name="+communicationPort+" type='text' value='"+commPortValue+"' style='width: 100%;' /> </td>"+
				"<td class='tblrows'> <select name="+attemptConnection+" style='width: 100%;'><option value='true' "+trueSelection+">True</option><option value='false' "+falseSelection+">False</option></select></td>"+
				"<td class='tblrows'> <input name="+watchdogInterval+" type='text' value='"+watchDogIntervalValue+"' style='width: 100%;' /> </td>"+
				"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
			"</tr>";

		$("#"+tableId+" tr:last").after(strTR);
	}
	
	function toggleMappingDiv(mappingDivIndex){
		  var imgElement = document.getElementById("toggleImageElement"+mappingDivIndex);
		  if ($("#toggleDivElement"+mappingDivIndex).is(':hidden')) {
	            imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
	       } else {
	            imgElement.src="<%=request.getContextPath()%>/images/bottom-level.jpg";
	       }
	      $("#toggleDivElement"+mappingDivIndex).slideToggle("fast");
	}
	
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
	

	$(document).ready(function(){		
		var realmName;
		var routingAction;
		var authAppId;
		var acctAppId;
	
		<% for(DiameterChargingDriverRealmsData diameterRealmData: diameterChargingDriverData.getDriverRealmsRelSet()){%>
			<%if(diameterRealmData.getRealmName()!=null){%>
				realmName ='<%=(diameterRealmData.getRealmName() != null) ? URLEncoder.encode(diameterRealmData.getRealmName()) : ""%>';
				routingAction ='<%=(diameterRealmData.getRoutingAction() != null) ? diameterRealmData.getRoutingAction() : ""%>';
				authAppId ='<%=(diameterRealmData.getAuthApplicationId() != null) ? URLEncoder.encode(diameterRealmData.getAuthApplicationId()) : ""%>';
				acctAppId ='<%=(diameterRealmData.getAcctApplicationId() != null) ? URLEncoder.encode(diameterRealmData.getAcctApplicationId()) : ""%>';
				realmInstanceId ='<%=diameterRealmData.getRealmRelId()%>';
			<%}%>
			addUpdatedMappingRealmInstance(realmName,routingAction,authAppId,acctAppId,realmInstanceId);
		<%}%>
	});

	function addUpdatedMappingRealmInstance(realmName,routingAction,authAppId,acctAppId,realmInstanceId) {
		realmIndex++;
		var myArray = new Array();
		var mapIndex = realmIndex;
		var html  = $.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/servermgr/drivers/radius/RealmsConfig.jsp",
			   async:false,
			   data:"realmName="+realmName+"&routingActionName="+routingAction+"&authAppIdValue="+authAppId+"&acctAppIdValue="+acctAppId+"&realmIndexParam="+realmIndex
			 }).responseText;
		
		var div = document.getElementById("realmDiv");
		
		$(div).append(html);
		
		
		<% for(DiameterChargingDriverRealmsData diamChargingDriverRealmsData: diameterChargingDriverData.getDriverRealmsRelSet()){%>
				if(realmInstanceId == <%=diamChargingDriverRealmsData.getRealmRelId()%>){
					var requestTableId ;
					var responseTableId;
		
				<%if(diamChargingDriverRealmsData.getRealmVendorRelSet()!=null && !diamChargingDriverRealmsData.getRealmVendorRelSet().isEmpty()){%>
					var vendorId;
					var authAppId;
					var acctAppId;
		
					<% for(DiameterChargingDriverVendorData diamChargingDriverVendorData: diamChargingDriverRealmsData.getRealmVendorRelSet()){%>
						vendorId = '';
						authAppId = '';
						acctAppId = '';

						<%if(diamChargingDriverVendorData.getVendorId()!=null){%>
							vendorId = '<%=diamChargingDriverVendorData.getVendorId()%>';
						<%}%>

						<%if(diamChargingDriverVendorData.getAuthApplicationId()!=null){%>
							authAppId = '<%=diamChargingDriverVendorData.getAuthApplicationId()%>';
						<%}%>

						<%if(diamChargingDriverVendorData.getAcctApplicationId()!=null){%>
							acctAppId = '<%=diamChargingDriverVendorData.getAcctApplicationId()%>';
						<%}%>
						
				 		responseTableId ='vendorTable'+mapIndex;
				 		newVendor(responseTableId,vendorId,authAppId,acctAppId);
				<%}%>
				
				<% for(DiameterChargingDriverPeerData diameterChargingDriverPeerData: diamChargingDriverRealmsData.getRealmPeerRelSet()){%>
					peerName = '';
					communicationPort = '';
					attemptConnection = '';
					watchdogInterval = '';
					
					<%if(diameterChargingDriverPeerData.getName()!=null){%>
						peerName ='<%=StringEscapeUtils.escapeHtml(diameterChargingDriverPeerData.getName())%>';
					<%}%>

						communicationPort = '<%=diameterChargingDriverPeerData.getCommunicationPort()%>';
					
					<%if(diameterChargingDriverPeerData.getAttemptConnection()!=null){%>
						attemptConnection ='<%=StringEscapeUtils.escapeHtml(diameterChargingDriverPeerData.getAttemptConnection())%>';
					<%}%>
					
					<%if(diameterChargingDriverPeerData.getWatchDogInterval()!=null){%>	
						watchdogInterval = '<%=diameterChargingDriverPeerData.getWatchDogInterval()%>';
					<%}%>
						
			 		peerTableId ='peerTable'+mapIndex;
		 			newPeer(peerTableId,peerName,communicationPort,attemptConnection,watchdogInterval);
				<%}%>		
			<%}%>
			}
		<%}%>
}

</script>

<html:form action="/updateRealms" styleId="mainform">

	<html:hidden name="updateRadiusDCDriverRealmsForm" property="action"
		value="save" />
	<html:hidden name="updateRadiusDCDriverRealmsForm"
		property="driverInstanceId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="10">&nbsp;</td>
			<td width="100%" colspan="2" valign="top">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0">
					<tr>
						<td class="tblheader-bold" colspan="4"><bean:message
								bundle="driverResources" key="driver.dc.updatedcdriverrealms" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
					</tr>

					<tr>
						<td colspan="4">

							<table width="100%" name="c_tblCrossProductList"
								id="c_tblCrossProductList" align="right" border="0">
								<tr>
									<td><input type="button" onclick="addRealm()"
										value="Add Realms" class="light-btn" /></td>
								</tr>

								<tr>
									<td align="right">
										<div id="realmDiv"></div>
									</td>
								</tr>
							</table>

						</td>

					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="4"><input
							type="button" name="c_btnCreate" id="c_btnCreate2" value="Update"
							class="light-btn" tabindex="6" onclick="validateForm()">
							<input type="reset" name="c_btnDeletePolicy"
							onclick="javascript:window.location.href='<%=basePath%>/initSearchDriver.do?'"
							value="Cancel" class="light-btn" tabindex="7" /></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

</html:form>

