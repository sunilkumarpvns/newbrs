<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.elitesm.web.externalsystem.forms.UpdateESIInstanceForm"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.core.systemx.esix.udp.StatusCheckMethod" %>

<%
    String localBasePath = request.getContextPath();
	ESITypeAndInstanceData esiTypeAndInstanceData = (ESITypeAndInstanceData)request.getSession().getAttribute("esiTypeInstance");
%>
<script language="javascript1.2"
	src="<%=localBasePath%>/js/checkpwdstrength.js" type="text/javascript"></script>

<script>
var isValidName;

	$(document).ready(function(){
		var esiTypeId = '<%=esiTypeAndInstanceData.getEsiTypeId()%>';
		if(esiTypeId == <%=ExternalSystemConstants.AUTH_PROXY%> || esiTypeId == <%=ExternalSystemConstants.ACCT_PROXY%>) {
			$('.Show_Rows').show();
		}else{
			$('.Show_Rows').hide();
		}
		
		<%if(esiTypeAndInstanceData.getPacketBytes() == null){%>
				document.forms[0].packetBytes.disabled = "disabled";
		<%}%>
		
		 $("#statusCheckMethod").change(function(){ 
			 if($(this).val() == "3" || $(this).val() == "2"){
				 document.forms[0].packetBytes.disabled = "";
			 }else{
				 document.forms[0].packetBytes.value="";
				 document.forms[0].packetBytes.disabled = "disabled";
			 }
		});
		
	});
	
	function validateUpdate(){
		var esiTypeId = '<%=esiTypeAndInstanceData.getEsiTypeId()%>';
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
 		}else if((esiTypeId == '<%=ExternalSystemConstants.AUTH_PROXY%>' || esiTypeId == '<%=ExternalSystemConstants.ACCT_PROXY%>') && isNull(document.forms[0].realmNames.value)){
 	 		alert('Realm Names must be specified');
 	 		document.forms[0].realmNames.focus();
 		}else if(isNull(document.forms[0].sharedsecret.value)){
 			alert('Shared Secret must be specified');
 			document.forms[0].sharedsecret.focus();
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
 				$('#ipaddress').focus();
 	 		}	
 	 	}	
		
	}
	function validatePort(txt){
		// check for valid numeric port	 
		if(IsNumeric(txt) == true){
			if(txt >= 0 && txt<=65535){
				return(true);
			}else{
				return false;
			}
		}else{
			return(false);
		}
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
		     $('#ipaddress').focus();
		}else{
		    if(flagIp==true && flagPort==true){
		    	validIP=true;
		    }else if(flagIp == false || flagPort == false){
		    	 alert('Please Enter Valid Address (HOST:PORT)');
			     $('#ipaddress').focus();
		    }
	     }
		   return validIP;
	}
	
	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.EXTENDED_RADIUS_SYSTEM%>',searchName,'update','<%=esiTypeAndInstanceData.getEsiInstanceId()%>','verifyNameDiv');
	}
</script>

<html:form action="/updateESI">
	<html:hidden styleId="auditUId" property="auditUId" />
	<html:hidden styleId="esiInstanceId" property="esiInstanceId"/>
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="externalsystemResources" key="esi.details" />
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.name" />
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.name" header="esi.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text styleId="name" tabindex="1" onkeyup="verifyName();" property="name" size="30" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.description" /> 
							<ec:elitehelp headerBundle="externalsystemResources" text="esi.description" 
							header="esi.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:textarea tabindex="2" styleId="desc" property="description" style="width:250px" />
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.address" />
							<ec:elitehelp headerBundle="externalsystemResources" text="esi.address" 
							header="esi.address"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text styleId="address" tabindex="3" property="address" size="30" maxlength="60" style="width:250px" />
							<font color="#FF0000"> *</font> 
							<font color="#999999"> Host : Port </font>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.sharedsecret" /> 
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.sharedsecret" header="esi.sharedsecret"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text styleId="sharedsecret" tabindex="4" property="sharedSecret" size="20" maxlength="255" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					
					<tr class="Show_Rows" id="realm_row">
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.realmnames" /> 
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.realmnames" header="esi.realmnames"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="60%">
							<html:text styleId="realmNames" tabindex="5" property="realmNames" size="40" maxlength="200" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>	
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.timeout" />
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.timeout" header="esi.timeout"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="timeout" tabindex="6" property="timeout" size="10" maxlength="10" style="width:250px" />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.retrylimit" /> 
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.retrycount" header="esi.retrylimit"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="retryLimit" tabindex="7" property="retryLimit" size="10" maxlength="10" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" /> 
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.expiredrequestlimitcount" header="esi.expiredrequestlimitcount"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="expiredRequestLimitCount" tabindex="8" property="expiredRequestLimitCount" size="10" maxlength="10" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.statuscheckduration" /> 
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.statuscheckduration" header="esi.statuscheckduration"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="statusCheckDuration" tabindex="9" property="statusCheckDuration" size="10" maxlength="10" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.statuscheckmethod" /> 
						    <ec:elitehelp headerBundle="externalsystemResources" 
						    text="esi.statuscheckmethod" header="esi.statuscheckmethod"/>
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
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.packetbytes" header="esi.packetbytes"/> 
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="packetBytes" property="packetBytes" style="width:250px;display:invisible;" tabindex="11" />
						</td> 
					</tr>
					
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.minlocalport" /> 
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.minlocalport" header="esi.minlocalport"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="minLocalPort" tabindex="12" property="minLocalPort" size="10" maxlength="10" style="width:250px" />
							<font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.supportedattribute" />
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.supportedattribute" header="esi.supportedattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="60%">
							<html:text  property="supportedAttribute" size="40" tabindex="13" maxlength="250" style="width:250px" />
						</td>	
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="externalsystemResources" key="esi.unsupportedattribute" />
							<ec:elitehelp headerBundle="externalsystemResources" 
							text="esi.unsupportedattribute" header="esi.unsupportedattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="60%">
							<html:text  property="unSupportedAttribute" size="40" tabindex="14" maxlength="250" style="width:250px" />
						</td>	
					</tr>
					
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle">
							<input type="button" tabindex="15" name="c_btnCreate" onclick="validateUpdate()" value="Update" class="light-btn"> 
							<input type="reset" tabindex="16" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/initSearchESIInstance.do?/>'" value="Cancel" class="light-btn">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>