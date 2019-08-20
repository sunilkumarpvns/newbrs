<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.List"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"%>

<%
	String basePath = request.getContextPath();
	List<TranslationMappingConfData> transConfList = (List<TranslationMappingConfData>) request.getSession().getAttribute("transConfList");
%>

<script language="javascript">
 	var realmIndex=0;
 	var flag = false;
	var mainArray = new Array();
	var count = 0;
	var transConfigOptions;
	function init(){
		transConfigOptions = "<option value=0>--Select--</option>";
		<%for (int i=0;i<transConfList.size();i++){ 
			TranslationMappingConfData data = (TranslationMappingConfData)	transConfList.get(i);
		%>
			transConfigOptions = transConfigOptions+"<option value='<%=data.getTranslationMapConfigId()%>' ><%=data.getName()%></option>";
		<%}%>
	}
	
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
		document.forms[0].action.value = 'realmsMapping';
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
		var vendorId = "vendorId"+tableId;
		var authAppId = "authAppId"+tableId;
		var acctAppId = "acctAppId"+tableId;
		$("#"+tableId+" tr:last").after("<tr>"+
										"<td class='tblfirstcol'><input name="+vendorId+" type='text' value='' style='width: 100%;' /></td>"+
										"<td class='tblrows'> <input name="+authAppId+" type='text' value='' style='width: 100%;' /> </td>"+
										"<td class='tblrows'> <input name="+acctAppId+" type='text' value='' style='width: 100%;' /> </td>"+
										"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
										"</tr>");
	}

	function addNewPeer(tableId){
		var peerName = "peerName"+tableId;
		var communicationPort = "communicationPort"+tableId;
		var attemptConnection = "attemptConnection"+tableId;
		var watchdogInterval = "watchdogInterval"+tableId;
        var strTR = "<tr>"+
				"<td class='tblfirstcol'><input name="+peerName+" type='text' value='' style='width: 100%;' /></td>"+
				"<td class='tblrows'> <input name="+communicationPort+" type='text' value='' style='width: 100%;' /> </td>"+
				"<td class='tblrows'> <select name="+attemptConnection+" style='width: 100%;'><option value='true'>True</option><option value='false'>False</option></select></td>"+
				"<td class='tblrows'> <input name="+watchdogInterval+" type='text' value='' style='width: 100%;' /> </td>"+
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
	init();
</script>

<html:form action="/createRadiusDCDriver" styleId="mainform">

	<html:hidden name="createRadiusDCDriverForm" property="action"
		value="save" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											bundle="driverResources" key="driver.dc.createdcdriver" /></td>
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
										type="button" value="Previous " onclick="history.go(-1)"
										class="light-btn" /> <input type="button" name="c_btnCreate"
										id="c_btnCreate2" value="Create" class="light-btn"
										tabindex="6" onclick="validateForm()"> <input
										type="reset" name="c_btnDeletePolicy"
										onclick="javascript:window.location.href='<%=basePath%>/initSearchDriver.do?'"
										value="Cancel" class="light-btn" tabindex="7" /></td>
								</tr>
							</table>

						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table> </html:form>