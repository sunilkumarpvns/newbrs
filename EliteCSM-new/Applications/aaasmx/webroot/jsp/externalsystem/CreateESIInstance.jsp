<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.text.*,java.util.*"%>
<%@page import="com.elitecore.elitesm.web.externalsystem.forms.CreateESIInstanceForm"%>
<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.core.systemx.esix.udp.StatusCheckMethod" %>

<%
	String basePath = request.getContextPath();
	String esiTypeId=(String)request.getParameter("esiTypeId");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
var isValidName;

	$(document).ready(function() {		
		setEsiRealms();
		var esiTypeId='<%=esiTypeId%>';
		$('#esiTypeId').val(esiTypeId);
		
		document.forms[0].packetBytes.disabled = "disabled";
		
		 $("#statusCheckMethod").change(function(){ 
			 if($(this).val() == "3" || $(this).val() == "2"){
				 document.forms[0].packetBytes.disabled = "";
			 }else{
				 document.forms[0].packetBytes.value="";
				 document.forms[0].packetBytes.disabled = "disabled";
			 }
		});
	});
	
	function  validateSearch(){
		if(isNull(document.forms[0].name.value)){
 			alert('Name must be specified');
 			document.forms[0].name.focus();
 		}else if(!isValidName) {
 			alert('Enter Valid Name');
 			document.forms[0].name.focus();
 			return false;
 		}else if(isNull(document.forms[0].address.value)){
 			alert('Address must be specified');
 			document.forms[0].ipaddress.focus();
 		}else if(isNull(document.forms[0].sharedsecret.value)){
 			alert('Shared Secret must be specified');
 			document.forms[0].sharedsecret.focus();
 		}else if(document.forms[0].esiTypeId.value == 0){
 	 		alert('Select a proper value External System Type');
 	 		document.forms[0].esiTypeId.focus();
 		}else if((document.forms[0].esiTypeId.value == '<%=ExternalSystemConstants.AUTH_PROXY%>' || document.forms[0].esiTypeId.value == '<%=ExternalSystemConstants.ACCT_PROXY%>') && isNull(document.forms[0].realmNames.value)){
 	 		alert('Realm Names must be specified');
 	 		document.forms[0].realmNames.focus();
 		}else if(isNull(document.forms[0].timeout.value)){
 			alert('Timeout must be specified');
 			document.forms[0].timeout.focus();
 		}else if(isNull(document.forms[0].minLocalPort.value)){
 			alert('Minimum Local Port must be specified');
 			document.forms[0].minLocalPort.focus();
 		}else if(isNaN(document.forms[0].minLocalPort.value)){
 			alert('Minimum local port must be numeric.');
 			document.forms[0].maxLocalPort.focus();
 		}else if(isNull(document.forms[0].expiredRequestLimitCount.value)){
 			alert('Expired Request Limit Count must be specified');
 			document.forms[0].expiredRequestLimitCount.focus();
 		}else if(isNaN(document.forms[0].expiredRequestLimitCount.value)){
 			alert('Expired Request Limit Count must be numeric');
 			document.forms[0].expiredRequestLimitCount.focus();
 		}else if(!isNull(document.forms[0].timeout.value) && !isNumber(document.forms[0].timeout.value)){
 			alert('Timeout must be numeric.');
 			document.forms[0].timeout.focus();
 		}else if(isNull(document.forms[0].retryLimit.value)){
 			alert('Retry Limit must be numeric.');
 			document.forms[0].retryLimit.focus();
 		}else if(!isNumber(document.forms[0].retryLimit.value)){
 			alert('Retry Limit must be numeric.');
 			document.forms[0].retryLimit.focus();
 		}else if(isNull(document.forms[0].statusCheckDuration.value)){
 			alert('Status Check Duration must be numeric.');
 			document.forms[0].statusCheckDuration.focus();
 		}else if(!isNumber(document.forms[0].statusCheckDuration.value)){
 			alert('Status Check Duration must be numeric.');
 			document.forms[0].statusCheckDuration.focus();
 		}else if($("#statusCheckMethod").val() == 3 && isEmpty($(packetBytes).val())){
 			alert('Packet bytes must be specified');
 			document.forms[0].packetBytes.focus();
 		}else{
 			var validIp=validateIpAddress();
 			if(validIp==true){
 			document.forms[0].submit();
 			}else{
 				alert('Please Insert Valid Address(HOST:PORT)');
 				document.forms[0].ipaddress.focus();
 	 	}		
	}
	}
	function validatePort(txt){
		// check for valid numeric port	 
		if(IsNumeric(txt) == true){
			if(txt >= 0 && txt<=65535)
				return(true);
		}else
			return(false);
	}

	function validateIP(ipaddress){
		var ip=/((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
			if(ip.test(ipaddress)){
				   return true;
			   }else{
				   return false;
			   }
	}
	
	function validateIpAddress(){
		var flagIp=false,flagPort=false,validIP=false;
		var ipAddress=document.getElementById("address").value;
		
		var ipAds = ipAddress.split(":").length - 1;
		if(ipAds==1){
			 var strIpAddress=ipAddress.split(":");
			 if(! strIpAddress[0]){
					flag=false;
			 }else if(! strIpAddress[1]){
				    	flag=false;
			 }else{
			    	flagIp=validateIP(strIpAddress[0]);
			    	flagPort=validatePort(strIpAddress[1]);
			 }
		}else if(ipAds>1){
			var firstCut=null,secondCut=null,finalResult=null,validPort=null;
			
			   firstCut = ipAddress.split('[');
			    
			if(typeof firstCut[1] != 'undefined'){
			   	 secondCut = firstCut[1].split(']:'); 
			   	 finalResult = secondCut[0],validPort=secondCut[1];
		    }else{
			   	flagIp=false;
				flagPort=false;
			}
			
			 if(typeof firstCut[0] != 'undefined' && typeof firstCut[1] != 'undefined'  && typeof secondCut[0] != 'undefined' && typeof secondCut[1] != 'undefined'){
				flagIp=validateIP(finalResult);
		    	flagPort=validatePort(validPort);
			}else{
				flagIp=false;
				flagPort=false;
			}
		}
		 if(flagIp==false && flagPort==false){
		     alert('Please Enter Valid Address (HOST:PORT)');
		     document.forms[0].ipaddress.focus();
		}else{
		    if(flagIp==true && flagPort==true){
		    	validIP=true;
		    }
	     }
		   return validIP;
	}
		
	function setEsiRealms() {
		var compId = document.forms[0].esiTypeId.value;
		if(compId == <%=ExternalSystemConstants.AUTH_PROXY%> || compId == <%=ExternalSystemConstants.ACCT_PROXY%>) {
			$('#realmNames').removeAttr("disabled");
		}else{
			$('#realmNames').attr("disabled","disabled");			
		}
	}

	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.EXTENDED_RADIUS_SYSTEM%>',searchName,'create','','verifyNameDiv');
	}
	
	setTitle('<bean:message bundle="externalsystemResources" key="externalsystem.externalsystem"/>');
</script>


<html:form action="/createESIInstance">


	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH)%>">
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
											bundle="externalsystemResources"
											key="create.externalinterface" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="3">
													<bean:message bundle="externalsystemResources" key="esi.details" />
												</td>
											</tr>
											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>
											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>
											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.name" /> 
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.name" header="esi.name"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.name"/>','<bean:message bundle="externalsystemResources" key="esi.name"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="1" styleId="name" property="name" onkeyup="verifyName();" size="30" maxlength="30" style="width:250px" /><font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.description" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.description" header="esi.description"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.description"/>','<bean:message bundle="externalsystemResources" key="esi.description"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:textarea tabindex="2" styleId="desc" property="desc" style="width:250px" />
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
												  <bean:message bundle="externalsystemResources" key="esi.esitype" /> 
												  <ec:elitehelp headerBundle="externalsystemResources" text="esi.esitype" header="esi.esitype"/>
												  <%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.esitype"/>','<bean:message bundle="externalsystemResources" key="esi.esitype"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select tabindex="3" property="esiTypeId" onchange="setEsiRealms();" styleId="esiTypeId" style="width:130px">
														<html:option value="0">Select</html:option>
														<html:optionsCollection name="createESIForm" property="esiTypeList" label="name" value="esiTypeId" />
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.address" /> 
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.address" header="esi.address"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.address"/>','<bean:message bundle="externalsystemResources" key="esi.address"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="60%">
													<html:text styleId="address" property="address" size="40" tabindex="4" maxlength="60" style="width:250px" />
													<font color="#FF0000"> *</font> 
													<font color="#999999">Host : Port </font>
												</td>	
											</tr>
											
																						
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.sharedsecret" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.sharedsecret" header="esi.sharedsecret"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.sharedsecret"/>','<bean:message bundle="externalsystemResources" key="esi.sharedsecret"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="sharedsecret" property="sharedSecret" size="20" tabindex="5" maxlength="255" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.realmnames" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.realmnames" header="esi.realmnames"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.realmnames"/>','<bean:message bundle="externalsystemResources" key="esi.realmnames"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="60%">
													<html:text styleId="realmNames" property="realmNames" size="40" tabindex="6" maxlength="200" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>	
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.timeout" /> 
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.timeout" header="esi.timeout"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.timeout"/>','<bean:message bundle="externalsystemResources" key="esi.timeout"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="7" styleId="timeout" property="timeout" size="10" maxlength="10" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.retrylimit" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.retrycount" header="esi.retrylimit"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.retrycount"/>','<bean:message bundle="externalsystemResources" key="esi.retrylimit" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="8" styleId="retryLimit" property="retryLimit" size="10" maxlength="10" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" /> 
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.expiredrequestlimitcount" header="esi.expiredrequestlimitcount"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.expiredrequestlimitcount"/>','<bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="9" styleId="expiredRequestLimitCount" property="expiredRequestLimitCount" size="10" maxlength="10" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.statuscheckduration" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.statuscheckduration" header="esi.statuscheckduration"/> 
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.statuscheckduration"/>','<bean:message bundle="externalsystemResources" key="esi.statuscheckduration" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="10" styleId="statusCheckDuration" property="statusCheckDuration" size="10" maxlength="10" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td> 
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.statuscheckmethod" /> 
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.statuscheckmethod" header="esi.statuscheckmethod"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.statuscheckmethod"/>','<bean:message bundle="externalsystemResources" key="esi.statuscheckmethod" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select property="statusCheckMethod" styleId="statusCheckMethod" tabindex="10" style="width:250px">
														<logic:iterate id="statusCheckMethodInst"  collection="<%=StatusCheckMethod.VALUES %>" >
															<html:option value="<%=String.valueOf(((StatusCheckMethod)statusCheckMethodInst).id)%>"><%=((StatusCheckMethod)statusCheckMethodInst).name%></html:option>
														</logic:iterate>
													</html:select> 
												</td> 
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.packetbytes" /> 
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.packetbytes" header="esi.packetbytes"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.packetbytes"/>','<bean:message bundle="externalsystemResources" key="esi.packetbytes" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="packetBytes" property="packetBytes" style="width:250px;display:invisible;" tabindex="11" />
												</td> 
											</tr>
											
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.minlocalport" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.minlocalport" header="esi.minlocalport"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.minlocalport"/>','<bean:message bundle="externalsystemResources" key="esi.minlocalport"/>')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="12" styleId="minLocalPort" property="minLocalPort" size="10" maxlength="10" style="width:250px" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.supportedattribute" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.supportedattribute" header="esi.supportedattribute"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.supportedattribute"/>','<bean:message bundle="externalsystemResources" key="esi.supportedattribute" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="60%">
													<html:text  property="supportedAttribute" size="40" tabindex="13" maxlength="250" style="width:250px" />
												</td>	
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="externalsystemResources" key="esi.unsupportedattribute" />
													<ec:elitehelp headerBundle="externalsystemResources" text="esi.unsupportedattribute" header="esi.unsupportedattribute"/>
													<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.unsupportedattribute"/>','<bean:message bundle="externalsystemResources" key="esi.unsupportedattribute" />')" /> --%>
												</td>
												<td align="left" class="labeltext" valign="top" width="60%">
													<html:text  property="unSupportedAttribute" size="40" tabindex="14" maxlength="250" style="width:250px" />
												</td>	
											</tr>

											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>
											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" name="Create" width="5%" tabindex="15" Onclick="validateSearch()" value="Create" class="light-btn" />
													<input type="button" name="Cancel" tabindex="16" onclick="javascript:location.href='<%=basePath%>/initSearchESIInstance.do?/>'" value="Cancel" class="light-btn">
												</td>	
											</tr>
										</table>

									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>



