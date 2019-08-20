<%@page import="com.elitecore.diameterapi.mibs.constants.TransportProtocols"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.core.commons.tls.TLSVersion"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData" %>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.elitesm.web.diameter.peerprofiles.forms.DiameterPeerProfileForm"%>

<% 
	DiameterPeerProfileData diameterPeerProfileData = (DiameterPeerProfileData)request.getAttribute("diameterPeerProfileData");
%>	
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/js/jquery.multiselect.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/js/jquery.multiselect.filter.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/js/jquery.multiselect.filter.min.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.autocomplete.js"></script> 

<%
	DiameterPeerProfileForm diameterPeerProfileForm = (DiameterPeerProfileForm) request.getAttribute("diameterPeerProfileForm");
	List<String> remainingList=diameterPeerProfileForm.getRemainingCipherList();
	List<String> lstCipherSuitList=diameterPeerProfileForm.getLstCipherSuitesList();
	List<String> cipherSuitList=diameterPeerProfileForm.getCipherSuitList();
%>
<script>
var flag=0;
setExpressionData("Diameter");
var isValidName;
var remainigArray=new Array();
var remainigIndex=0;
<%
if(remainingList != null && remainingList.size() >0){
	for(String strRemaining:remainingList){
		%>
		remainigArray[remainigIndex]='<%=strRemaining%>';
		remainigIndex++;
		<%
	}
}
%>

function customValidate(form)
{
	if(validateDiameterPeerProfileForm(form))
    {
		if(!isValidName) {
			alert('Enter Valid Profile Name.');
			document.forms[0].profileName.focus();
			return false;
		}
		if(!isEmpty($("#socketReceiveBufferSize").val()) && isNaN(Number($("#socketReceiveBufferSize").val()))){
			alert("Socket Receive Buffer Size must be Numeric");
			$("#socketReceiveBufferSize").focus();
			return false;
		}
		if(!isEmpty($("#socketSendBufferSize").val()) && isNaN(Number($("#socketSendBufferSize").val()))){
			alert("Socket Send Buffer Size must be Numeric");
			$("#socketSendBufferSize").focus();
			return false;
		}
		
		  if(document.forms[0].validateCertificateExpiry.checked){
			  document.forms[0].validateCertificateExpiry.value="true";
		  }else{
			  document.forms[0].validateCertificateExpiry.value="false";
		  }
		  
		  if(document.forms[0].allowCertificateCA.checked){
			  document.forms[0].allowCertificateCA.value="true";
		  }else{
			  document.forms[0].allowCertificateCA.value="false";
		  }
		  
		  if(document.forms[0].validateCertificateRevocation.checked){
			  document.forms[0].validateCertificateRevocation.value="true";
		  }else{
			  document.forms[0].validateCertificateRevocation.value="false";
		  }
		  
		  if(document.forms[0].validateHost.checked){
			  document.forms[0].validateHost.value="true";
		  }else{
			  document.forms[0].validateHost.value="false";
		  }
		  
		  var minTLSVersion=$('#minTlsVersion').val();
	      var minVersion = minTLSVersion.replace(/[^\d.-]/g, '');
			
		  var maxTLSVersion=$('#maxTlsVersion').val();
		  var maxVersion = maxTLSVersion.replace(/[^\d.-]/g, '');
		  
		  if(minVersion > maxVersion){
				 alert("Minimum TLS Version should be less than or equal to maximum TLS Version");
				 document.getElementById("minTlsVersion").focus();
				 return false;
		  }
		  
		  var finalCipherSuit=$("#cipherSuitesId").val();
		  finalCipherSuit=finalCipherSuit.trim();
			 var newArray=new Array();
			 var newValArray=finalCipherSuit.replace(/(\r\n|\n|\r)/gm,"");
			 
			var minTLSVersionValue=$('#minTlsVersion').val();
			var maxTLSVersionValue=$('#maxTlsVersion').val();
			var dbFieldStr;
			var dbFieldArray;
			var flagValue;
			$("#cipherSuitesId").val(newValArray);
			postans=checkAjaxNew(newValArray,minTLSVersionValue,maxTLSVersionValue);
			
			if(postans==true){
				return true;
			}else{
				return false;
			} 
    }
	else
	{
		return false;
	}	
}

function checkAjaxNew(newValArray,minTLSVersionValue,maxTLSVersionValue){
    var data;
    $.ajax({url:'<%=request.getContextPath()%>/FindInvalidCipherSuit',
          type:'POST',
          data:{ cipherList:newValArray,minTLSVersion:minTLSVersionValue,maxTLSVersion:maxTLSVersionValue },
          async:false,
          success: function(transport){
             data=transport;
        }
   });
	if(data!=null){
		
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		var newArray=dbFieldArray;
		var startIndex = data.indexOf('[');
		var endIndex = data.indexOf(']');
		if(endIndex === startIndex+1){			
			 $('#listCipherSuites').val($('#cipherSuitesId').val());
	 			  $('#cipherSuites').val($('#cipherSuitesId').val()); 
					if($('#securityStandard').val() != 'NONE'){
						 	if($('#serverCertificateId').val() === 'NONE' || $('#cipherSuitesId').val() == ""){
						 			alert("TLS Connection may not Possible");
						 	 	}
					}
					ajaxFlag=true; 					
					
		}else{
			var newTable="\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
			for(var index=0;index<newArray.length;index++){
				newTable=newTable+"\n"+newArray[index];
			}
			
			alert("Invalid Ciphersuit List !!!  " +newTable);
			ajaxFlag = false;			
		}
		return ajaxFlag;
				
	}else{
    	return false;
	}
};


function verifyName() { 
	var searchName = document.getElementById("profileName").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_PEER_PROFILE%>',searchName,'update','<%=diameterPeerProfileData.getPeerProfileId()%>','verifyNameDiv');
}

$(document).ready(function() {
	var valuesOfCipherSuites="";
	<%
	if(lstCipherSuitList !=null && lstCipherSuitList.size() > 0){
		for(String strCipherSuit :lstCipherSuitList){%>
		if(valuesOfCipherSuites !=""){
			valuesOfCipherSuites=valuesOfCipherSuites+",\n"+'<%=strCipherSuit%>';
		}else{
			valuesOfCipherSuites='<%=strCipherSuit%>';
		}
	<%}}%>
	$("#cipherSuitesId").val(valuesOfCipherSuites);
	$("#cipherSuitesId").attr("spellcheck",false);
});
function  checkAll(){
	 var id="cipherSuitPopUpData";
	        if ($("#cipherTable #toggleAll").is(':checked')) {
	        	
	        	$("#" + id +" input[name='ids']").each(function(){
	                $(this).attr('checked','checked');
	            });

	        } else {
	        	$("#" + id +" input[name='ids']").each(function(){
	                $(this).removeAttr('checked');
	            });
	        }
} 

function retriveCipherSuitList(){
	var minTLSVersionValue=$('#minTlsVersion').val();
	var maxTLSVersionValue=$('#maxTlsVersion').val();
		var cipherStr;
		var cipherArray;
		var diffCipherStr;
		var diffCipherArray;
		var valuesOfCipherSuites="";
		preserveValuesArray=$("#cipherSuitesId").val();
		var newValArray=preserveValuesArray.replace(/(\r\n|\n|\r)/gm,"");
		$.post("RetriveCipherSuit", {minTLSVersion:minTLSVersionValue,maxTLSVersion:maxTLSVersionValue}, function(data){
			cipherStr = data.substring(1,data.length-3);
			cipherArray = new Array();
			cipherArray = cipherStr.split(", ");
			$.post("FindDifferenceBetweenCipherSuits", {preserveValuesArray:newValArray,minTLSVersion:minTLSVersionValue,maxTLSVersion:maxTLSVersionValue}, function(presentData){
				diffCipherStr = presentData.substring(1,presentData.length-3);
				diffCipherArray = new Array();
				diffCipherArray = diffCipherStr.split(", ");
				if(diffCipherArray.length > 1){
					$("#cipherSuitesId").val("");
					for(var i=0;i<diffCipherArray.length;i++){
						if(valuesOfCipherSuites !=""){
							valuesOfCipherSuites=valuesOfCipherSuites+",\n"+diffCipherArray[i];
						}else{
							valuesOfCipherSuites=diffCipherArray[i];
						}
				 	}
					  $("#cipherSuitesId").val(valuesOfCipherSuites);
				}
			});
		});	
}
</script>
<html:javascript formName="diameterPeerProfileForm" /> 
<html:form action="/updateDiameterPeerProfile" onsubmit="return customValidate(this);return false;" >
<html:hidden name="diameterPeerProfileForm" styleId="peerProfileId" property="peerProfileId"/> 
<html:hidden name="createDiameterPeerProfileForm" styleId="strCipherSuite" property="strCipherSuite" value="" /> 
<html:hidden name="diameterPeerProfileForm" styleId="cipherSuites" property="cipherSuites" value="" />
<html:hidden name="diameterPeerProfileForm" styleId="listCipherSuites" property="listCipherSuites" value="" />
<html:hidden name="diameterPeerProfileForm" styleId="auditUId" property="auditUId" />
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
				<tr>
					<td class="small-gap" colspan="2">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td valign="middle" colspan="5" >
					    <table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
					      	<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="3">
									<bean:message bundle="diameterResources" key="diameterpeerprofile.updatepeerprofile" /></td>
							</tr>
							
							<tr>
								<td align="left" class="btns-td captiontext"  valign="top" width="30%">
								  <bean:message bundle="diameterResources" key="diameterpeerprofile.name" />
									  <ec:elitehelp headerBundle="diameterResources" 
									  text="diameterpeerprofile.name" header="diameterpeerprofile.name"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:text name="diameterPeerProfileForm" styleId="profileName" property="profileName" size="30" onkeyup="verifyName();"
									maxlength="64" style="width:250px" tabindex="1"/><font color="#FF0000"> *</font>
									<div id="verifyNameDiv" class="labeltext"></div>
								</td>
							</tr>
						    <tr> 
								<td align="left" class="btns-td captiontext" valign="top" >
									<bean:message bundle="diameterResources" key="diameterpeerprofile.description" />
									<ec:elitehelp headerBundle="diameterResources" 
									text="diameterpeerprofile.description" header="diameterpeerprofile.description"/>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="2" >
                                     <table width="100%" border="0" cellpadding="0" cellspacing="0">
                                       <tr>
                                         <td>
                                         		<html:textarea styleId="description" name="diameterPeerProfileForm" property="description" tabindex="2" cols="50" rows="2" style="width:250px"/>
                                         </td>
                                         </tr>
                                      </table>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top"  width="30%">
									<bean:message bundle="diameterResources" key="diameterpeerprofile.exclusiveauthappid" />
									<ec:elitehelp headerBundle="diameterResources" 
									text="diameterpeerprofile.exclusiveauthappid" header="diameterpeerprofile.exclusiveauthappid"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									  <html:text styleId="exclusiveAuthAppIds" name="diameterPeerProfileForm" tabindex="3" property="exclusiveAuthAppIds" maxlength="255" style="width:250px"/>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top"  width="30%">
									<bean:message bundle="diameterResources" key="diameterpeerprofile.exclusiveacctappid" />
									<ec:elitehelp headerBundle="diameterResources" 
									text="diameterpeerprofile.exclusiveacctappid" header="diameterpeerprofile.exclusiveacctappid"/>  
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									  <html:text styleId="exclusiveAcctAppIds" name="diameterPeerProfileForm" tabindex="4" property="exclusiveAcctAppIds" maxlength="255" style="width:250px"/>
								</td>
							</tr>
								
							<tr>
								<td align="left" class="btns-td captiontext" valign="top" >
								   	<bean:message bundle="diameterResources" key="diameterpeerprofile.ceravps" />
								   	<ec:elitehelp headerBundle="diameterResources" 
								   	text="diameterpeerprofile.ceravps" header="diameterpeerprofile.ceravps"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:textarea styleId="cerAvps" name="diameterPeerProfileForm" property="cerAvps" cols="50" rows="2" style="width:250px" tabindex="5"/>
								</td>
							</tr>
							<tr>
								<td align="left" class="btns-td captiontext" valign="top" >
								  	<bean:message bundle="diameterResources" key="diameterpeerprofile.dpravps" />
								 	<ec:elitehelp headerBundle="diameterResources" 
								  	text="diameterpeerprofile.dpravps" header="diameterpeerprofile.dpravps"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:textarea styleId="dprAvps" name="diameterPeerProfileForm" property="dprAvps" cols="50" rows="2" style="width:250px" tabindex="6"/>
								</td>
							</tr>
							<tr>
								<td align="left" class="btns-td captiontext" valign="top" >
								 	<bean:message bundle="diameterResources" key="diameterpeerprofile.dwravps" />
									<ec:elitehelp headerBundle="diameterResources" 
									text="diameterpeerprofile.dwravps" header="diameterpeerprofile.dwravps"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:textarea styleId="dwrAvps" name="diameterPeerProfileForm" property="dwrAvps" cols="50" rows="2" style="width:250px" tabindex="7"/>
								</td>
							</tr>
							
							<tr>
									<td class="btns-td captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.sessioncleanupon" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.sessioncleanupon" header="diameterpeerprofile.sessioncleanupon"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
										<label for="sessionCleanUpCER">
											<html:checkbox property="sessionCleanUpCER" value="true" tabindex="8" styleId="sessionCleanUpCER"></html:checkbox>
											<bean:message bundle="diameterResources" key="diameterpeerprofile.sessioncleanupcer" />
										</label>
										&nbsp;&nbsp;&nbsp;
										<label for="sessionCleanUpDPR">
										<html:checkbox property="sessionCleanUpDPR" value="true" tabindex="9" styleId="sessionCleanUpDPR"></html:checkbox>
										<bean:message bundle="diameterResources" key="diameterpeerprofile.sessioncleanupdpr" />
										</label>
									</td>
							</tr>
							<tr>
									<td align="left" class="captiontext" valign="top"  width="30%">
									  <bean:message bundle="diameterResources" key="diameterpeerprofile.diameterURI" />
									  <ec:elitehelp headerBundle="diameterResources" 
									  text="diameterpeerprofile.diameterURI" header="diameterpeerprofile.diameterURI"/>
									</td>
									<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
										<html:select property="redirectHostAvpFormat" tabindex="10" style="width: 130px;">
											<html:option value="IP">IP</html:option>
											<html:option value="Host Identity">Host Identity</html:option>
											<html:option value="DiameterURI">DiameterURI</html:option>
											<html:option value="NONE">NONE</html:option>
										</html:select>
									</td>
							</tr>
							<tr>
									<td align="left" class="captiontext" valign="top"  width="30%">
									  	<bean:message bundle="diameterResources" key="diameterpeerprofile.followredirection" />
									  	<ec:elitehelp headerBundle="diameterResources" 
									  	text="diameterpeerprofile.followredirection" header="diameterpeerprofile.followredirection"/>
									</td>
									<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
										<html:select property="followRedirection" tabindex="11" style="width: 134px;">
											<html:option value="true">Enabled</html:option>
											<html:option value="false">Disabled</html:option>
										</html:select>
									</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.hotlinepolicy" /> 
									<ec:elitehelp  header="servicepolicy.authpolicy.hotlinepolicy" headerBundle="servicePolicyProperties" text="diameterpeerprofile.hotlinepolicy" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top">
									<html:text property="hotlinePolicy" name="diameterPeerProfileForm" styleId="hotlinePolicy" style="width: 255;" maxlength="255" tabindex="12"/>
								</td> 
							</tr>
							<tr>
								<td  class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="diameterResources" key="diameterpeerprofile.connectionparameters" />
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" >
								  	<bean:message bundle="diameterResources" key="diameterpeerprofile.transportprotocol" />
									<ec:elitehelp headerBundle="diameterResources" 
									text="diameterpeerprofile.transportprotocol" header="diameterpeerprofile.transportprotocol"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
				   					    <html:select name="diameterPeerProfileForm" styleId="transportProtocol" property="transportProtocol" size="1" style="width: 130px;" tabindex="12">
				   					    	<logic:iterate id="transportProtocol" collection="<%=TransportProtocols.VALUES %>">
												<html:option value="<%=((TransportProtocols)transportProtocol).protocolTypeStr %>"></html:option>
											</logic:iterate>
										</html:select>  
								</td>
							</tr>
							<tr>
									<td align="left" class="captiontext" valign="top"  width="30%">
									  <bean:message bundle="diameterResources" key="diameterpeerprofile.securitystandard" />
									  <ec:elitehelp headerBundle="diameterResources"
									  text="diameterpeerprofile.securitystandard" header="diameterpeerprofile.securitystandard"/>
									</td>
									<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
					   					 <html:select property="securityStandard" styleClass="labeltext" styleId="securityStandard" style="width: 134px;" tabindex="13">
											<logic:iterate id="connectionStandardInst" name="diameterPeerProfileForm" property="lstConnectionStandard">
												<html:option value="<%=connectionStandardInst.toString()%>"><%=connectionStandardInst.toString()%></html:option>
											</logic:iterate>
										</html:select> 
									</td>
							</tr>
							<tr>
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.socketreceivebuffersize" />
										<ec:elitehelp headerBundle="diameterResources"
										text="diameterpeerprofile.socketreceivebuffersize" header="diameterpeerprofile.socketreceivebuffersize"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
										<html:text property="socketReceiveBufferSize" maxlength="8" styleId="socketReceiveBufferSize" tabindex="14"/>
									</td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.socketsendbuffersize" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.socketsendbuffersize" header="diameterpeerprofile.socketsendbuffersize"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
										<html:text property="socketSendBufferSize" maxlength="8" styleId="socketSendBufferSize" tabindex="15"/>
									</td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.tcpnagleAlgorithm" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.tcpnagleAlgorithm" header="diameterpeerprofile.tcpnagleAlgorithm"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
										<html:select property="tcpNagleAlgorithm" tabindex="16">
											<html:option value="true">True</html:option>
											<html:option value="false">False</html:option>
										</html:select>
									</td>
								</tr>   
							<tr>
								<td align="left" class="captiontext" valign="top" >
								  <bean:message bundle="diameterResources" key="diameterpeerprofile.dwrduration" />
								  <ec:elitehelp headerBundle="diameterResources" 
								  text="diameterpeerprofile.dwrduration" header="diameterpeerprofile.dwrduration"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:text name="diameterPeerProfileForm" styleId="dwrDuration" property="dwrDuration" maxlength="10" tabindex="17"/>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" >
								  <bean:message bundle="diameterResources" key="diameterpeerprofile.initconnection" />
								  <ec:elitehelp headerBundle="diameterResources" 
								  text="diameterpeerprofile.initconnection" header="diameterpeerprofile.initconnection"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:text name="diameterPeerProfileForm" styleId="initConnectionDuration" property="initConnectionDuration" maxlength="10" tabindex="18" />
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" >
								  	<bean:message bundle="diameterResources" key="diameterpeerprofile.retrycount" />
									<ec:elitehelp headerBundle="diameterResources" 
									text="diameterpeerprofile.retrycount" header="diameterpeerprofile.retrycount"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:text name="diameterPeerProfileForm" styleId="retryCount" property="retryCount" maxlength="2" tabindex="19"/>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" >
									<bean:message bundle="diameterResources" key="diameterpeerprofile.dproncertimeout" />
								  	<ec:elitehelp headerBundle="diameterResources" 
								  	text="diameterpeerprofile.senddprcloseevent" header="diameterpeerprofile.dproncertimeout"/>
								</td>
								<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
									<html:select name="diameterPeerProfileForm" styleId="sendDPRCloseEvent" property="sendDPRCloseEvent" tabindex="20">
											<html:option value="false">False</html:option>
											<html:option value="true">True</html:option>
										</html:select>
								</td>
							</tr>
								<tr>
									<td  class="tblheader-bold" valign="top" colspan="4">
										<bean:message bundle="diameterResources" key="diameterpeerprofile.securityparameters" />
									</td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.mintlsversion" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.mintlsversion" header="diameterpeerprofile.mintlsversion"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
									 	<html:select property="minTlsVersion" styleClass="labeltext" styleId="minTlsVersion" style="width: 134px;" tabindex="21" onchange="retriveCipherSuitList();">
											<logic:iterate id="tlsVersionInst" name="diameterPeerProfileForm" collection="<%=TLSVersion.values() %>" >
												<html:option value="<%=((TLSVersion)tlsVersionInst).version%>"><%=((TLSVersion)tlsVersionInst).version%></html:option>
											</logic:iterate>
										</html:select> 
									 </td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.maxtlsversion" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.maxtlsversion" header="diameterpeerprofile.maxtlsversion"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
									 	<html:select property="maxTlsVersion" styleClass="labeltext" styleId="maxTlsVersion" style="width: 134px;" tabindex="22" onchange="retriveCipherSuitList();">
											<logic:iterate id="tlsVersionInst" name="diameterPeerProfileForm" collection="<%=TLSVersion.values() %>" >
												<html:option value="<%=((TLSVersion)tlsVersionInst).version%>"><%=((TLSVersion)tlsVersionInst).version%></html:option>
											</logic:iterate>
										</html:select> 
									 </td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.servercertificate" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.servercertificate" header="diameterpeerprofile.servercertificate"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
										 <html:select property="serverCertificateId" styleClass="labeltext" name="diameterPeerProfileForm" styleId="serverCertificateId" style="width: 134px;" tabindex="23">
											<html:option value="0">NONE</html:option>
											<logic:iterate id="serverCertificateInst" name="diameterPeerProfileForm" property="serverCertificateDataList" type="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData">
												<html:option value="<%=serverCertificateInst.getServerCertificateId()%>"><%=serverCertificateInst.getServerCertificateName()%></html:option>
											</logic:iterate>
										</html:select> 
									</td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.clientcertificatevalidation" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.clientcertificatevalidation" header="diameterpeerprofile.clientcertificatevalidation"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
									 <html:select property="clientCertificateRequest" styleClass="labeltext" styleId="clientCertificateRequest"  tabindex="24">
										<html:option value="true">True</html:option>
										<html:option value="false">False</html:option>
									</html:select> 
									</td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" valign="top">
										<bean:message bundle="diameterResources" key="diameterpeerprofile.ciphersuitelist"/>
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.ciphersuitelist" header="diameterpeerprofile.ciphersuitelist"/>
									</td>
	 								<td class="labeltext" nowrap="nowrap">
	 									<textarea rows="5" cols="20" name="cipherSuitesId" id="cipherSuitesId" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;width:340px" tabindex="25"></textarea>
	 								</td>
								</tr>
								
								<tr valign="top">
									<td class="captiontext"  width="30%" >
										<bean:message bundle="diameterResources" key="diameterpeerprofile.certificateexception" />
										<ec:elitehelp headerBundle="diameterResources" 
										text="diameterpeerprofile.certificateexception" header="diameterpeerprofile.certificateexception"/>
									</td>
									<td class="labeltext" nowrap="nowrap">
										<table class="labeltext" nowrap="nowrap" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;width:340px">
										<tr>
											<td>
												<label for="validateCertificateExpiry">
													<html:checkbox property="validateCertificateExpiry" styleClass="labeltext" styleId="validateCertificateExpiry" tabindex="26">
														<bean:message bundle="diameterResources" key="diameterpeerprofile.expirydate" />
													</html:checkbox>
												</label>
											</td>
										</tr>
										<tr>
											<td>
												<label for="allowCertificateCA">
													<html:checkbox property="allowCertificateCA" styleId="allowCertificateCA" tabindex="27">
															<bean:message bundle="diameterResources" key="diameterpeerprofile.unknownca" />
													</html:checkbox>
												</label>
											</td>
										</tr>
										<tr>
											<td>
												<label for="validateCertificateRevocation">
													<html:checkbox property="validateCertificateRevocation" styleId="validateCertificateRevocation" tabindex="28">
														<bean:message bundle="diameterResources" key="diameterpeerprofile.revokedcertificate" />
													</html:checkbox>
												</label>
											</td>
										</tr>
										<tr>
											<td>
												<label for="validateHost">
													<html:checkbox property="validateHost" styleId="validateHost" tabindex="29">
														<bean:message bundle="diameterResources" key="diameterpeerprofile.unknownsubjectca" />
													</html:checkbox>
												</label>
											</td>
										</tr>
										</table>
									 </td>
								</tr>
								<tr>
									<td  class="tblheader-bold" valign="top" colspan="4">
										<bean:message bundle="diameterResources" key="diameterpeerprofile.3gppwimaxparameters" />
									</td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" valign="top">
										<bean:message bundle="diameterResources" key="diameterpeerprofile.haipaddress"/>
										<ec:elitehelp headerBundle="diameterResources" text="diameterpeerprofile.haipaddress" header="diameterpeerprofile.haipaddress"/>
									</td>
	 								<td class="labeltext" nowrap="nowrap">
	 									<html:text property="haIPAddress" styleId="haIPAddress" style="width:250px;"  maxlength="100"></html:text>
	 								</td>
								</tr>
								<tr>
									<td class="captiontext"  width="30%" valign="top">
										<bean:message bundle="diameterResources" key="diameterpeerprofile.dhcpipaddress"/>
										<ec:elitehelp headerBundle="diameterResources" text="diameterpeerprofile.dhcpipaddress" header="diameterpeerprofile.dhcpipaddress"/>
									</td>
	 								<td class="labeltext" nowrap="nowrap">
	 									<html:text property="dhcpIPAddress" styleId="dhcpIPAddress" style="width:250px;" maxlength="100"></html:text>
	 								</td>
								</tr>
							<tr>
								<td colspan="100%">
								 &nbsp;
							    </td>
						   </tr>
							<tr>
								<td class="btns-td" valign="middle">
									&nbsp;
								</td>
								<td class="btns-td" valign="middle" colspan="2">
									<input type="submit" name="c_btnUpdate"  id="c_btnUpdate" value="  Update  " class="light-btn" tabindex="30">
									<input type="button" name="c_btnCancel" tabindex="31" onclick="javascript:location.href='<%=basePath%>/viewDiameterPeerProfile.do?peerProfileId='+document.diameterPeerProfileForm.peerProfileId.value" value="  Cancel  " class="light-btn"> 
								</td>
							</tr>
						</table>
					</td>
				</tr>
	   </table>
</html:form>

<div id="popupExpr" style="display: none;" title="ExpressionBuilder"> 
	<div id="expBuilderId" align="center" ></div>
</div>
<%-- <div id="cipherSuitDiv" style="display: none;" title="CiphersuitList">
	<table class="box" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td width="5%" align="left" class="tblheader-bold-grey" valign="top">
				Select</td>
			<td width="30%" align="left" class="tblheader-bold-grey" valign="top">
				<bean:message key="general.name" />
			</td>
		</tr>
		<tr>
			<logic:iterate id="cipherListId" property="remainingCipherList" name="diameterPeerProfileForm" >
				<tr class="labeltext" id='<bean:write name="cipherListId"/>'>
					<td align="left" class="tblfirstcol" valign="top">
						<input type="checkbox" id="cipherSuitsDetails" name="cipherSuitsDetails" value='<bean:write name="cipherListId"/>' />
					</td>
					<td align="left" class="tblrows" valign="top" id="ciphersuitValues" name="ciphersuitValues"><bean:write name="cipherListId"/>
					</td>
				</tr>
			</logic:iterate>
		</tr>
	</table>
</div> --%>
