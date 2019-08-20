<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.IVendorSpecificCertificateData"%>
<%@page import="com.elitecore.elitesm.web.servermgr.eap.forms.CreateEAPConfigDetailForm"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.coreeap.cipher.providers.constants.CipherSuites"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.core.commons.tls.TLSVersion"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData" %>

<%
    String basePath = request.getContextPath();
	Set<CipherSuites> lstCipherSuits=(Set<CipherSuites>) request.getAttribute("cipherSuiteSet");
	List<ServerCertificateData> serverCertificateDataList=(List<ServerCertificateData>)request.getAttribute("serverCertificateDataList");

%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.EAPConfigConstant"%>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>
$(document).ready(function(){
	$('#mappingtbl1 td img.delete').live('click',function() {	
		 $(this).parent().parent().remove(); 
	});
	
	 $('#expiryDate').attr('checked','checked');
	 $('#revokedCertificate').attr('checked','checked');
	 $('#missingClientCertificate').attr('checked','checked');
	 $('#macValidation').attr('checked','checked');
}); 
var selectedArray=new Array();
var availableArray=new Array();
var localIndex=0;
var lastArrays=new Array();
var cipherSuitArray=new Array();
var myFlag=0;
var isValidAjax;
var ajaxFlag = false; 
var postan = "HELLO";
$(document).ready(function() {
	var valuesOfCipherSuites="";
	<%for(CipherSuites strCipherSuit :lstCipherSuits){%>
		if(valuesOfCipherSuites !=""){
			valuesOfCipherSuites=valuesOfCipherSuites+",\n"+'<%=strCipherSuit.name()%>';
		}else{
			valuesOfCipherSuites='<%=strCipherSuit.name()%>';
		}
	<%}%>
	$("#cipherSuitesId").val(valuesOfCipherSuites);
	$("#cipherSuitesId").attr("spellcheck",false);
});
function validateCreate(){
    if(isNull(document.forms[0].certificateRequest.value)){
		document.forms[0].certificateRequest.focus();
		alert('TLS Certificate Request be specified');
	} else if (document.forms[0].serverCertificateId.value == 0) {
		document.forms[0].serverCertificateId.focus();
		alert("Server Certificate Profile must be specified.");
	} else if(isNull(document.forms[0].sessionResumptionLimit.value)){
		document.forms[0].sessionResumptionLimit.focus();
		alert('Session Resumption Limit must be specified');
	}else if(isNull(document.forms[0].sessionResumptionDuration.value)){
		document.forms[0].sessionResumptionDuration.focus();
		alert('Session Resumption Duration must be specified');
	}else if(document.forms[0].dss.checked==false && document.forms[0].dss_dh.checked== false && document.forms[0].rsa.checked== false && document.forms[0].rsa_dh.checked == false){
		alert('At least one Certificate Type must be selected');
	}else if(!isNumber(document.forms[0].sessionResumptionLimit.value)){
		  document.forms[0].sessionResumptionLimit.focus();
		  alert('Session Resumption Limit must be Numeric.');
	}else if(!isNumber(document.forms[0].sessionResumptionDuration.value)){
		  document.forms[0].sessionResumptionDuration.focus();
		  alert('Session Resumption Duration must be Numeric.');
	}else{
		  if(document.forms[0].dss.checked){
			  document.forms[0].dss.value='2';
		  }
		  if(document.forms[0].dss_dh.checked){
			  document.forms[0].dss_dh.value='4';
		  }
		 	
		  if(document.forms[0].rsa.checked){
			  document.forms[0].rsa.value='1';
		  }
		  if(document.forms[0].rsa_dh.checked){
			  document.forms[0].rsa_dh.value='3';
		  }
		  
		  if(document.forms[0].expiryDate.checked){
			  document.forms[0].expiryDate.value="true";
		  }else{
			  document.forms[0].expiryDate.value="false";
		  }
		  
		  if(document.forms[0].revokedCertificate.checked){
			  document.forms[0].revokedCertificate.value="true";
		  }else{
			  document.forms[0].revokedCertificate.value="false";
		  }
		  
		  if(document.forms[0].missingClientCertificate.checked){
			  document.forms[0].missingClientCertificate.value="true";
		  }else{
			  document.forms[0].missingClientCertificate.value="false";
		  }
		  
		  if(document.forms[0].macValidation.checked){
			  document.forms[0].macValidation.value="true";
		  }else{
			  document.forms[0].macValidation.value="false";
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
		  var newValArray=finalCipherSuit.replace(/(\r\n|\n|\r)/gm,"");
			 
		  var minTLSVersionValue=$('#minTlsVersion').val();
		  var maxTLSVersionValue=$('#maxTlsVersion').val();
		  $("#cipherSuitesId").val(newValArray);
		  postans=checkAjaxNew(newValArray,minTLSVersionValue,maxTLSVersionValue);
			if(postans==true){
				if(isValidAttributeMapping("mappingtbl1","oui")){
					document.forms[0].checkAction.value='Save';
		       		document.forms[0].submit();
				}
			}else{
				return false;
			} 
   	}

 }  	
function checkAjaxNew(newValArray,minTLSVersionValue,maxTLSVersionValue){
    var data;
    $.ajax({url:'<%=request.getContextPath()%>/FindInvalidEapCipherSuit',
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
		     ajaxFlag=true; 					
		}else{
			var newTable="\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
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
  function validatePrevious()
  {

	 document.forms[0].checkAction.value="Previous";  
	 document.forms[0].submit();
	
  }


function addVendorSpecificCertificate(){

	if(isNull(document.forms[0].oui.value)){
		document.forms[0].oui.focus();
		alert('Vendor Identifier must be specified');
		   
	}else if(isNull(document.forms[0].serverCertificateName.value)){
		document.forms[0].serverCertificateName.focus();
		alert('Server Certificate Name must be specified');
	}else if(isNull(document.forms[0].serverPrivatekeyName.value)){
		document.forms[0].serverPrivatekeyName.focus();
		alert('Server Privatekey Name must be specified');
	}else if(isNull(document.forms[0].cacertificateName.value)){
		document.forms[0].cacertificateName.focus();
		alert('CA Certificate Name must be specified');
	}else{
	document.forms[0].checkAction.value='Add';
    document.forms[0].submit();
	}
}

function initialized(){
	
	document.forms[0].rsa.checked=true;
	
}

function setPeapNegotiationMethod(){

	var peapVersion =  document.getElementById("peapVersion");
	var peapNegotiationMethod =  document.getElementById("peapNegotiationMethod");

	if(peapVersion.value==0){
		clearDropDownList(peapNegotiationMethod);
		peapNegotiationMethod.options[0] = new Option('EAP-MsCHAPv2','26');
	}else if(peapVersion.value==1){
		clearDropDownList(peapNegotiationMethod);
		peapNegotiationMethod.options[0] = new Option('EAP-MD5','4');
		peapNegotiationMethod.options[1] = new Option('EAP-GTC','6');
		peapNegotiationMethod.options[2] = new Option('EAP-MsCHAPv2','26');
		peapNegotiationMethod.options[2].selected = "selected";
	}
}
function clearDropDownList(peapNegotiationMethod){
	var len = peapNegotiationMethod.options.length;
    for (i=0; i<len; i++) {
    	peapNegotiationMethod.remove(0); 
    }
}
function retriveCipherSuitList(){
	var minTLSVersionValue=$('#minTlsVersion').val();
	var maxTLSVersionValue=$('#maxTlsVersion').val();
		var dbFieldStr;
		var dbFieldArray;
		var valuesOfCipherSuites="";
		$("#cipherSuitesId").val("");
		$.post("RetriveEAPCipherSuites", {minTLSVersion:minTLSVersionValue,maxTLSVersion:maxTLSVersionValue}, function(data){
			dbFieldStr = data.substring(1,data.length-2);
			dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			for(var i=0;i<dbFieldArray.length;i++){
				if(valuesOfCipherSuites !=""){
					valuesOfCipherSuites=valuesOfCipherSuites+",\n"+dbFieldArray[i];
				}else{
					valuesOfCipherSuites=dbFieldArray[i];
				}
			}
			$("#cipherSuitesId").val(valuesOfCipherSuites);
		});	
		
}
setTitle('<bean:message bundle="servermgrResources" key="servermgr.eapconfig.tls.header"/>');	
</script>

<html:form action="/createEAPConfigDetail">
                   

<html:hidden name="createEAPConfigDetailForm" styleId="checkAction" property="checkAction"/>
<html:hidden name="createEAPConfigDetailForm" styleId="itemIndex" property="itemIndex"/>
<html:hidden name="createEAPConfigDetailForm" styleId="cipherSuites" property="cipherSuites" value="" />
<html:hidden name="createEAPConfigDetailForm" styleId="listCipherSuites" property="listCipherSuites" value="" />
		  					
<div id="backgroundPopup">
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
		  <tr>
			<td colspan="4">
			   <table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
			  <tr>                                                                                
				<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="diameterResources" key="diameterpeerprofile.mintlsversion" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="diameterpeerprofile.mintlsversion"/>','<bean:message bundle="diameterResources" key="diameterpeerprofile.mintlsversion"/>')"/>
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					<html:select property="minTlsVersion" name="createEAPConfigDetailForm"  styleClass="labeltext" styleId="minTlsVersion" style="width: 130px;" tabindex="1" onchange="retriveCipherSuitList();">
						<logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>" >
							<html:option value="<%=((TLSVersion)tlsVersionInst).version%>"><%=((TLSVersion)tlsVersionInst).version%></html:option>
						</logic:iterate>
					</html:select> 
					<font color="#FF0000"> *</font>
				</td>
				</tr>
				<tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="diameterResources" key="diameterpeerprofile.maxtlsversion" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="diameterpeerprofile.maxtlsversion"/>','<bean:message bundle="diameterResources" key="diameterpeerprofile.maxtlsversion"/>')"/>
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					<html:select property="maxTlsVersion"  name="createEAPConfigDetailForm"  styleClass="labeltext" styleId="maxTlsVersion" style="width: 130px;" tabindex="2" value="TLSv1.2" onchange="retriveCipherSuitList();">
						<logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>" >
							<html:option value="<%=((TLSVersion)tlsVersionInst).version%>"><%=((TLSVersion)tlsVersionInst).version%></html:option>
						</logic:iterate>
					</html:select> 
					<font color="#FF0000"> *</font>
				</td>
			  </tr>
			 <tr>
				  <td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="diameterResources" key="diameterpeerprofile.servercertificate" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="diameterpeerprofile.servercertificate"/>','<bean:message bundle="diameterResources" key="diameterpeerprofile.servercertificate"/>')"/>
					</td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						 <html:select property="serverCertificateId" styleClass="labeltext" styleId="serverCertificateId"  tabindex="3" style="width: 130px;">
							    <html:option value="0">NONE</html:option>
							    <%for(ServerCertificateData serverCertificateInst:serverCertificateDataList){ %>
									<html:option value="<%=serverCertificateInst.getServerCertificateId()%>"><%=serverCertificateInst.getServerCertificateName()%></html:option>
								<%} %>
						</html:select><font color="#FF0000"> *</font>  
					</td>
			</tr>
			<tr>
					<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.tlscertificaterequest"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.tlscertificaterequest"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.tlscertificaterequest"/>')"/>										
		
					</td>
					<td align="left" class="labeltext" valign="top" width="32%" >
					    <html:select name="createEAPConfigDetailForm" styleId="certificateRequest" property="certificateRequest" size="1" tabindex="4">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select><font color="#FF0000"> *</font>	      
					</td>
					
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.ciphersuitelist"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.ciphersuite"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.ciphersuitelist"/>')"/>										
				</td>
				<td align="left" class="labeltext" valign="top" width="32%">
					<textarea rows="5" cols="20" name="cipherSuitesId" id="cipherSuitesId" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;width:340px;" tabindex="5"></textarea>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="servermgrResources" key="servermgr.eapconfig.certificateexception" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="diameterpeerprofile.certificateexception"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.certificateexception"/>')"/>
				</td>
				<td class="labeltext">
					<table class="labeltext" nowrap="nowrap" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;width:340px">
						<tr>
							<td >
								<label for="expiryDate">
									<html:checkbox property="expiryDate" name="createEAPConfigDetailForm" styleClass="labeltext" styleId="expiryDate" tabindex="6">
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.expirydate" />
									</html:checkbox>
					   		   </label>
							</td>
						</tr>
						<tr>
							<td>
								<label for="revokedCertificate">
									<html:checkbox property="revokedCertificate" name="createEAPConfigDetailForm" styleId="revokedCertificate" tabindex="7" >
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.revokedcertificate" />
									</html:checkbox>
								</label>
							</td>
						</tr>
						<tr>
							<td>
								<label for="missingClientCertificate">
									<html:checkbox property="missingClientCertificate" name="createEAPConfigDetailForm" styleId="missingClientCertificate" tabindex="8">
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.missingclientcertificate" />
									</html:checkbox>
								</label>
							</td>
						</tr>
						<tr>
							<td>
								<label for="macValidation">
									<html:checkbox property="macValidation" name="createEAPConfigDetailForm" styleId="macValidation" tabindex="9" >
											<bean:message bundle="servermgrResources" key="servermgr.eapconfig.macvalidation" />
									</html:checkbox>
								</label>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			  <tr>
				<td align="left" class="captiontext" valign="top" width="10%">
				<bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessionresumptionlimit"/>
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.sessionresumptionlimit"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessionresumptionlimit"/>')"/>										
		
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					<html:text styleId="sessionResumptionLimit" property="sessionResumptionLimit" size="15" maxlength="60" tabindex="10" style="width: 100px;"/><font color="#FF0000"> *</font>
				</td>
			  </tr>	
			  <tr>
			  	<td align="left" class="captiontext" valign="top" width="10%">
				<bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessionresumptionduration"/>
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.sessionresumptionduration"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessionresumptionduration"/>')"/>										
		
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					<html:text styleId="sessionResumptionDuration" property="sessionResumptionDuration" size="15" maxlength="60" tabindex="11" style="width: 100px;"/><font color="#FF0000"> *</font>
				</td>
				
			  </tr>
			  
		 <!-- header for certificate type list  -->
		 <tr> 
             <td class="tblheader-bold" colspan="4" height="20%">
            	 <bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.certificatetypelist"/>
             </td>
         </tr>    
		     
		    <tr>
					<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.certificatetypelist"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.certtype"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.certificatetypelist"/>')"/>										
					</td>
					 
					<!-- multiple Check box -->
					
					<td align="left" class="labeltext" valign="top" colspan="4" > 
					
						 <html:checkbox property="rsa" value="" styleId="rsa" tabindex="12"></html:checkbox>
                         <label for="rsa"> 
                         	<bean:message bundle="servermgrResources" key="servermgr.eapconfig.rsa"/>
                         </label> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						
                         <html:checkbox property="rsa_dh" value="" styleId="rsa_dh" tabindex="913"></html:checkbox>
                         <label for="rsa_dh">
                        	<bean:message bundle="servermgrResources" key="servermgr.eapconfig.rsa_dh"/>
                         </label> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 		 
						 <html:checkbox property="dss" value="" styleId="dss" tabindex="14"></html:checkbox>
                         <label for="dss">
                        	 <bean:message bundle="servermgrResources" key="servermgr.eapconfig.dss"/>
                          </label>
                          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="dss_dh" value="" styleId="dss_dh" tabindex="15"></html:checkbox>
                         <label for="dss_dh">
                        	 <bean:message bundle="servermgrResources" key="servermgr.eapconfig.dss_dh"/>
                         </label>
					 </td>
					
					<!-- End -->
					
			</tr> 
		     
		   <logic:match name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.TTLS_STR%>">
		    <tr> 
             	<td class="tblheader-bold" colspan="4" height="20%">
             		<bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.ttlscertificaterequest"/>
	            </td>
        	 </tr> 
		     
		     <tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.tltscertificaterequest"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.ttlscertificaterequest"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.ttlscertificaterequest"/>')"/>										
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					 <html:select name="createEAPConfigDetailForm" tabindex="16" styleId="ttlscertificateRequest" property="ttlscertificateRequest" value="false" size="1" style="width :125px">
						 <html:option value="true" >True</html:option>
						  <html:option value="false" >False</html:option> 
					</html:select><font color="#FF0000"> *</font>	      
				</td>
			</tr>
		      
		     <tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.ttlsnegotiationmethod"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.negotiationmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.ttlsnegotiationmethod"/>')"/>										
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					<html:select name="createEAPConfigDetailForm" tabindex="17" styleId="ttlsNegotiationMethod" property="ttlsNegotiationMethod" size="1" style="width :125px">
						<html:option value="4" >EAP-MD5</html:option>
						<html:option value="6" >EAP-GTC</html:option>
						<html:option value="26" >EAP-MsCHAPv2</html:option> 
					</html:select><font color="#FF0000"> *</font>	      
				</td>
			</tr>
		  </logic:match>
		      
		   <logic:match name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.PEAP_STR%>">
		     <tr> 
	             <td class="tblheader-bold" colspan="4" height="20%">
    		         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.peapcertificaterequest"/>
	             </td>
        	 </tr> 
		     
		     <tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.peapcertificaterequest"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.peapcertificaterequest"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.peapcertificaterequest"/>')"/>										
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					 <html:select name="createEAPConfigDetailForm" tabindex="18" styleId="peapCertificateRequest" property="peapCertificateRequest" value="false" size="1" style="width :125px">
						 <html:option value="true" >True</html:option>
						  <html:option value="false" >False</html:option> 
					</html:select>	      
				</td>
			</tr>
		     
		    <tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.peapversion"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.peapversion"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.peapversion"/>')"/>										
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					<html:select name="createEAPConfigDetailForm" tabindex="19" styleId="peapVersion" property="peapVersion" size="1" style="width :125px" onchange="setPeapNegotiationMethod()">
						<html:option value="0" >0</html:option>
						 <html:option value="1" >1</html:option> 
					</html:select>	      
				</td>
			</tr>
		    
		    <tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.eapconfig.peapnegotiationmethod"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.negotiationmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.peapnegotiationmethod"/>')"/>										
				</td>
				<td align="left" class="labeltext" valign="top" width="32%" >
					 <html:select name="createEAPConfigDetailForm" tabindex="20" styleId="peapNegotiationMethod" property="peapNegotiationMethod" size="1" style="width :125px">
						<html:option value="26" >EAP-MsCHAPv2</html:option> 
					</html:select><font color="#FF0000"> *</font>	      
				</td>
			</tr>
		   </logic:match>
				 
				 <!-- vendor specific certificate header -->
				 <tr> 
                    <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vendorspecificcertificate"/></td>
                 </tr>
                 
                 <tr>                      		 
                       		<td align="left" class="captiontext" valign="top" colspan="2" id="button">
                      		 <input type="button" onclick='addRow("dbMappingTable","mappingtbl1");'  tabindex="21" value="  Add  " class="light-btn" style="size: 140px"> </td>                      		 
                 </tr>   
                 <!--  -->
				</table>  
			</td>
            
			</tr> 
			
			<tr>
			   
			   <td  colspan="4" valign="top" class="captiontext">
				<table cellSpacing="0" cellPadding="0" width="54%" border="0" id="mappingtbl1" class="box">
				<tr>
								<td align="left" class="tblheader" valign="top" width="23%" >
									<bean:message bundle="servermgrResources" key="servermgr.eapconfig.vendoridentifier"/>
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.vendoridentifier"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.vendoridentifier"/>')"/>										
								</td>								
								<td align="left" class="tblheader" valign="top" width="76.5%">
									<bean:message bundle="diameterResources" key="diameterpeerprofile.servercertificate" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.servercertname"/>','<bean:message bundle="diameterResources" key="diameterpeerprofile.servercertificate"/>')"/>										
								</td>
                                <td align="left" class="tblheader" valign="top" width="4%">Remove</td>
                		
                </tr>
                
            
				</table>
				</td>	
		</tr>
							
          <tr>
        <td width="10" class="small-gap">&nbsp;</td>
        <td class="small-gap" colspan="4">&nbsp;</td>
    	</tr>
        <tr > 
	        <td class="btns-td" valign="middle" >&nbsp;</td>
            <td class="btns-td" valign="middle"  >
                
                <input  type="button" name="c_btnPrev" onclick="history.back()" value="Previous" class="light-btn" tabindex="22">
                <logic:match name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.SIM_STR%>">
                	<input type="button" name="c_btnCreate" onclick="validateCreate();" id="c_btnCreate2"  value="Next"  class="light-btn" tabindex="23">
                </logic:match>
               	<logic:notMatch name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.SIM_STR%>">
                	<logic:notMatch name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.AKA_STR%>">
                	
                		<logic:match name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.AKA_PRIME_STR%>">
                			<input type="button" name="c_btnCreate" tabindex="24" onclick="validateCreate();" id="c_btnCreate2"  value="Next"  class="light-btn" >
                		</logic:match>
                		<logic:notMatch name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.AKA_PRIME_STR%>">
                			<input type="button" name="c_btnCreate" tabindex="25" onclick="validateCreate();" id="c_btnCreate2"  value="Create"  class="light-btn" >
                		</logic:notMatch>	
               		</logic:notMatch>
               		<logic:match name="eapConfigData" scope="session" property="enabledAuthMethods" value="<%=EAPConfigConstant.AKA_STR%>">
                			<input type="button" name="c_btnCreate" tabindex="24" onclick="validateCreate();" id="c_btnCreate2"  value="Next"  class="light-btn" >
               		</logic:match>
               	</logic:notMatch>
                <input type="reset" name="c_btnDeletePolicy" tabindex="26" onclick="javascript:location.href='<%=basePath%>/initSearchEAPConfig.do?/>'" value="Cancel" class="light-btn"> 
	        </td>
   		  </tr>
		</table>
	  </td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footer.jsp" %>  
</table>
</td>
</tr>
</table>
</div>


</html:form>

<!-- Template of Vendor Specific Certificate List -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder" width="23%">
			<input type="text" id="oui" name="oui" tabindex="18" size="30" maxlength="60"  class="noborder" value="000000*"/>
		</td>
		<td class="tblrows" width="79%">
			<select id="serverCerticateIdForVSC" name="serverCerticateIdForVSC" tabindex="19" styleClass="labeltext" class="noborder" style="width:250px;">
				<option value="0">NONE</option>
				<%for(ServerCertificateData serverCertificateInst:serverCertificateDataList){ %>
					<option value="<%=serverCertificateInst.getServerCertificateId()%>"><%=serverCertificateInst.getServerCertificateName()%></option>
				<%} %>
			</select>
		</td>
		<td class="tblrows" align="center" colspan="3" width="4%">
			<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="20" />
		</td>
	</tr>
</table>

<script>
initialized();
</script>
	
	
	