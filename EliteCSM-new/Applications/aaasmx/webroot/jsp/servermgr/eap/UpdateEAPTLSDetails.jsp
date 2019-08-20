
<%@ include file="/jsp/core/includes/common/Header.jsp"%>




<%@page import="com.elitecore.elitesm.web.servermgr.eap.forms.UpdateEAPTLSDetailsForm"%>
<%
	
    UpdateEAPTLSDetailsForm updateEAPTLSDetailsForm=(UpdateEAPTLSDetailsForm)request.getAttribute("updateEAPTLSDetailsForm");
    
    String[] vendoridentifiers = (String[])request.getAttribute("vendoridentifiers");
    String[] serverCerticateIdForVSC = (String [])request.getAttribute("serverCerticateIdForVSC");
    List<ServerCertificateData> serverCertificateDataList=(List<ServerCertificateData>)request.getAttribute("serverCertificateDataList");
    List<VendorSpecificCertificateData> vendorSpecificList=(List<VendorSpecificCertificateData>)request.getAttribute("vendorSpecificCertList");
    List<String> remainingList=updateEAPTLSDetailsForm.getRemainingCipherList();
    List<String> lstCipherSuitList=updateEAPTLSDetailsForm.getLstCipherSuitesList();
    List<String> cipherSuitList=updateEAPTLSDetailsForm.getCipherSuitList();
    
    System.out.println("TTLS Method:"+updateEAPTLSDetailsForm.getTtlsNegotiationMethod());
    System.out.println("PEAP Method:"+updateEAPTLSDetailsForm.getPeapNegotiationMethod());
    System.out.println("PEAP Version:"+updateEAPTLSDetailsForm.getPeapVersion());
%>



<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>

var mainArray = new Array();
var count = 0;
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
		
		dbFieldStr = data.substring(1,data.length-2);
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
	
	$('#mappingtbl td img.delete').live('click',function() {	
		 $(this).parent().parent().remove(); 
	});
});
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
		$.post("RetriveEAPCipherSuites", {minTLSVersion:minTLSVersionValue,maxTLSVersion:maxTLSVersionValue}, function(data){
			cipherStr = data.substring(1,data.length-2);
			cipherArray = new Array();
			cipherArray = cipherStr.split(", ");
			$.post("FindDifferenceBetweenEapCipherSuites", {preserveValuesArray:newValArray,minTLSVersion:minTLSVersionValue,maxTLSVersion:maxTLSVersionValue}, function(presentData){
				diffCipherStr = presentData.substring(1,presentData.length-2);
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
function validateUpdate()
{

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
		  var minTLSVersion=$('#minTlsVersion').val();
	      var minVersion = minTLSVersion.replace(/[^\d.-]/g, '');
			
		  var maxTLSVersion=$('#maxTlsVersion').val();
		  var maxVersion = maxTLSVersion.replace(/[^\d.-]/g, '');
		  
		  
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
				if(isValidAttributeMapping("mappingtbl","oui")){
					
					document.forms[0].action.value='update';
		       		document.forms[0].submit();
				}
			}else{
				return false;
			} 
   	}
}
function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
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
		<%if(updateEAPTLSDetailsForm.getPeapNegotiationMethod()!=null && updateEAPTLSDetailsForm.getPeapNegotiationMethod().intValue()==4){%>
		peapNegotiationMethod.options[0].selected = "selected";
		<%}else if(updateEAPTLSDetailsForm.getPeapNegotiationMethod()!=null && updateEAPTLSDetailsForm.getPeapNegotiationMethod().intValue()==6){%>
		peapNegotiationMethod.options[1].selected = "selected";
		<%}else{%>
		peapNegotiationMethod.options[2].selected = "selected";
		<%}%>
	}
}
function clearDropDownList(peapNegotiationMethod){
	var len = peapNegotiationMethod.options.length;
    for (i=0; i<len; i++) {
    	peapNegotiationMethod.remove(0); 
    }
}
</script>





<html:form action="/updateEAPTLSDetails">

<html:hidden name="updateEAPTLSDetailsForm" styleId="action" property="action" />
<html:hidden name="updateEAPTLSDetailsForm" styleId="eapId" property="eapId"/>
<html:hidden name="updateEAPTLSDetailsForm" styleId="eaptlsId" property="eaptlsId"/>
<html:hidden name="updateEAPTLSDetailsForm" styleId="typeVal" property="typeVal"/>
<html:hidden name="updateEAPTLSDetailsForm" styleId="cipherSuites" property="cipherSuites" value="" />
<html:hidden name="updateEAPTLSDetailsForm" styleId="listCipherSuites" property="listCipherSuites" value="" />
<html:hidden name="updateEAPTLSDetailsForm" styleId="auditUId" property="auditUId" />
<html:hidden name="updateEAPTLSDetailsForm" styleId="name" property="name" />
<div id="backgroundPopup">
		<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
					
					<tr>
						<td class="small-gap" colspan="2">
							&nbsp;
						</td>
					</tr>
			       <tr>
						<td class="box" valign="middle" colspan="2">
							<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
						        <tr>
									<td class="tblheader-bold" colspan="3">
									<logic:equal name="updateEAPTLSDetailsForm" property="typeVal" value="TTLS">
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.updatettlsconfiguration"/>
									</logic:equal>
									<logic:equal name="updateEAPTLSDetailsForm" property="typeVal" value="TLS">
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.updatetlsconfiguration"/>
									</logic:equal>	
									</td>
						       </tr>
						       <tr > 
						<td class="small-gap" colspan="3" >&nbsp;</td>
			     </tr>
		  <tr>
			<td colspan="4">
			   <table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
			  <tr>                                                                                
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="diameterResources" 
						key="diameterpeerprofile.mintlsversion" />
							<ec:elitehelp headerBundle="diameterResources" 
								text="diameterpeerprofile.mintlsversion" 
									header="diameterpeerprofile.mintlsversion"/>
				</td>
				<td align="left" class="labeltext" valign="top" >
					 <html:select property="minTlsVersion" name="updateEAPTLSDetailsForm"  styleClass="labeltext" styleId="minTlsVersion" style="width: 130px;" tabindex="1" onchange="retriveCipherSuitList();">
						<logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>" >
							<html:option value="<%=((TLSVersion)tlsVersionInst).version%>"><%=((TLSVersion)tlsVersionInst).version%></html:option>
						</logic:iterate>
					</html:select> 
					<font color="#FF0000"> *</font>	 
				</td>
			  </tr>
			  <tr>
			  	<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="diameterResources" 
						key="diameterpeerprofile.maxtlsversion" />
							<ec:elitehelp headerBundle="diameterResources" 
								text="diameterpeerprofile.maxtlsversion" 
									header="diameterpeerprofile.maxtlsversion"/>
				</td>
				<td align="left" class="labeltext" valign="top">
					 <html:select property="maxTlsVersion"  name="updateEAPTLSDetailsForm" styleClass="labeltext" styleId="maxTlsVersion" style="width: 130px;" tabindex="2" onchange="retriveCipherSuitList();">
						<logic:iterate id="tlsVersionInst"  collection="<%=TLSVersion.values() %>" >
							<html:option value="<%=((TLSVersion)tlsVersionInst).version%>"><%=((TLSVersion)tlsVersionInst).version%></html:option>
						</logic:iterate>
					</html:select> 
					<font color="#FF0000"> *</font>		
				</td>
			  </tr>
			  <tr>
				  	<td align="left" class="captiontext" valign="top" width="30%">
				  		<bean:message bundle="diameterResources" 
				  			key="diameterpeerprofile.servercertificate" />
								<ec:elitehelp headerBundle="diameterResources" 
									text="diameterpeerprofile.servercertificate" 
										header="diameterpeerprofile.servercertificate"/>
					</td>
				  	<td align="left" class="labeltext" valign="top">
				  		 <html:select property="serverCertificateId" styleClass="labeltext" name="updateEAPTLSDetailsForm" styleId="serverCertificateId" style="width: 130px;" tabindex="3">
							   <html:option value="0">NONE</html:option>
							    <%for(ServerCertificateData serverCertificateInst:serverCertificateDataList){ %>
									<html:option value="<%=serverCertificateInst.getServerCertificateId()%>"><%=serverCertificateInst.getServerCertificateName()%></html:option>
								<%} %>
						</html:select>  <font color="#FF0000"> *</font>
				  	</td>
			 </tr>
			 <tr>
					<td align="left" class="captiontext" valign="top" width="30%">
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.tlscertificaterequest"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.tlscertificaterequest" 
										header="servermgr.eapconfig.tlscertificaterequest"/>
					</td>
					<td align="left" class="labeltext" valign="top" colspan="3" >
					    <html:select name="updateEAPTLSDetailsForm" styleId="certificateRequest" property="certificateRequest" size="1" tabindex="4">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select><font color="#FF0000"> *</font>	      
					</td>
			  </tr>
			  <tr>
					<td align="left" class="captiontext" valign="top" width="30%">
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.header.ciphersuitelist"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.ciphersuite" 
										header="servermgr.eapconfig.header.ciphersuitelist"/>
					</td>
	 				<td class="labeltext" nowrap="nowrap">
	 					<textarea rows="5" cols="20" name="cipherSuitesId" id="cipherSuitesId" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;width:340px" tabindex="5"></textarea>
	 				</td>
			  </tr>
			  <tr>
				<td align="left" class="captiontext" valign="top" width="10%">
					<bean:message bundle="servermgrResources" 
						key="servermgr.eapconfig.certificateexception" />
							<ec:elitehelp headerBundle="servermgrResources" 
								text="diameterpeerprofile.certificateexception" 
									header="servermgr.eapconfig.certificateexception"/>
				</td>
				<td class="labeltext">
					<table class="labeltext" nowrap="nowrap" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;width:340px">
						<tr>
							<td >
								<label for="expiryDate">
									<html:checkbox property="expiryDate" name="updateEAPTLSDetailsForm" styleClass="labeltext" styleId="expiryDate" tabindex="6">
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.expirydate" />
									</html:checkbox>
					   		   </label>
							</td>
						</tr>
						<tr>
							<td>
								<label for="revokedCertificate">
									<html:checkbox property="revokedCertificate" name="updateEAPTLSDetailsForm" styleId="revokedCertificate" tabindex="7" >
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.revokedcertificate" />
									</html:checkbox>
								</label>
							</td>
						</tr>
						<tr>
							<td>
								<label for="missingClientCertificate">
									<html:checkbox property="missingClientCertificate" name="updateEAPTLSDetailsForm" styleId="missingClientCertificate" tabindex="8">
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.missingclientcertificate" />
									</html:checkbox>
								</label>
							</td>
						</tr>
						<tr>
							<td>
								<label for="macValidation">
									<html:checkbox property="macValidation" name="updateEAPTLSDetailsForm" styleId="macValidation" tabindex="9" >
											<bean:message bundle="servermgrResources" key="servermgr.eapconfig.macvalidation" />
									</html:checkbox>
								</label>
							</td>
						</tr>
					</table>
				</td>
			  </tr>
			  <tr>
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="servermgrResources" 
						key="servermgr.eapconfig.sessionresumptionlimit"/>
							<ec:elitehelp headerBundle="servermgrResources" 
								text="eapconfig.sessionresumptionlimit" 
									header="servermgr.eapconfig.sessionresumptionlimit"/>
				</td>
				<td align="left" class="labeltext" valign="top">
					<html:text styleId="sessionResumptionLimit" property="sessionResumptionLimit" size="15" maxlength="60" tabindex="10" style="width: 100px;"/><font color="#FF0000"> *</font>
				</td>
			  </tr>	
			 <tr>
				  	<td align="left" class="captiontext" valign="top" width="30%">
				  		<bean:message bundle="servermgrResources" 
				  			key="servermgr.eapconfig.sessionresumptionduration"/>
				  				<ec:elitehelp headerBundle="servermgrResources" 
				  					text="eapconfig.sessionresumptionduration" 
				  						header="servermgr.eapconfig.sessionresumptionduration"/>
					</td>
				  	<td align="left" class="labeltext" valign="top">
				  		<html:text styleId="sessionResumptionDuration" property="sessionResumptionDuration" size="15" maxlength="60" tabindex="11"  style="width: 100px;"/><font color="#FF0000"> *</font>
				  	</td>
			 </tr>
		 <!-- header for certificate type list  -->
		 <tr> 
             <td class="tblheader-bold" colspan="4" height="40%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.certificatetypelist"/></td>
         </tr>    
		     
		    <tr>
					<td align="left" class="captiontext" valign="top" >
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.certificatetypelist"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.certtype" 
										header="servermgr.eapconfig.certificatetypelist"/>
					</td>
					<!-- multiple Check box -->
					
					<td align="left" class="labeltext" valign="top" colspan="4" > 
						 <html:checkbox property="rsa" styleId="rsa" tabindex="12"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.rsa"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   
                         <html:checkbox property="rsa_dh" styleId="rsa_dh" tabindex="13"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.rsa_dh"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 		 
						 <html:checkbox property="dss"  styleId="dss" tabindex="14"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.dss"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="dss_dh" styleId="dss_dh" tabindex="15"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.dss_dh"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					 </td>
					
					<!-- End -->
					
			</tr> 
		   <logic:equal name="updateEAPTLSDetailsForm" property="typeVal" value="TTLS">
		  	 <html:hidden name="updateEAPTLSDetailsForm" styleId="peapNegotiationMethod" property="peapNegotiationMethod" />
		     <html:hidden name="updateEAPTLSDetailsForm" styleId="peapVersion" property="peapVersion" />
		  	 <html:hidden name="updateEAPTLSDetailsForm" styleId="peapCertificateRequest" property="peapCertificateRequest"/>
		   <tr> 
	             <td class="tblheader-bold" colspan="4" height="40%">
	            	 <bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.ttlscertificaterequest"/>
	             </td>
        	</tr> 
		     
		     <tr>
					<td align="left" class="captiontext" valign="top" >
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.tltscertificaterequest"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.ttlscertificaterequest" 
										header="servermgr.eapconfig.tltscertificaterequest"/>
					</td>
					<td align="left" class="labeltext" valign="top" colspan="3">
					    <html:select name="updateEAPTLSDetailsForm" styleId="ttlscertificateRequest" property="ttlscertificateRequest" size="1" style="width :125px" tabindex="16">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select><font color="#FF0000"> *</font>	      
					</td>
				  </tr>
				  
				     <tr>
					<td align="left" class="captiontext" valign="top" width="30%">
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.ttlsnegotiationmethod"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.negotiationmethod" 
										header="servermgr.eapconfig.ttlsnegotiationmethod"/>
					</td>
					<td align="left" class="labeltext" valign="top"  >
					    <html:select name="updateEAPTLSDetailsForm" styleId="ttlsNegotiationMethod" property="ttlsNegotiationMethod" size="1" style="width :125px" tabindex="17">
						   <html:option value="4" >EAP-MD5</html:option>
						   <html:option value="6" >EAP-GTC</html:option>
						   <html:option value="26" >EAP-MsCHAPv2</html:option> 
						</html:select><font color="#FF0000"> *</font>	      
					</td>
				  </tr>
		     
		     
		  </logic:equal>  
		  
		  <logic:equal name="updateEAPTLSDetailsForm" property="typeVal" value="PEAP">
		 	  <html:hidden name="updateEAPTLSDetailsForm" styleId="ttlsNegotiationMethod" property="ttlsNegotiationMethod" />
  			  <html:hidden name="updateEAPTLSDetailsForm" styleId="ttlscertificateRequest" property="ttlscertificateRequest"/>
  			<tr> 
             <td class="tblheader-bold" colspan="4" height="20%">
             <bean:message bundle="servermgrResources" key="servermgr.eapconfig.header.peapcertificaterequest"/>
             
             </td>
        	 </tr> 
		     
		     <tr>
					<td align="left" class="captiontext" valign="top" >
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.peapcertificaterequest"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.peapcertificaterequest" 
										header="servermgr.eapconfig.peapcertificaterequest"/>
					</td>
					<td align="left" class="labeltext" valign="top" >
					    <html:select name="updateEAPTLSDetailsForm" styleId="peapCertificateRequest" property="peapCertificateRequest" size="1" style="width :125px" tabindex="18">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select>	      
					</td>
			</tr>
		     
		      <tr>
					<td align="left" class="captiontext" valign="top" >
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.peapversion"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.peapversion" 
										header="servermgr.eapconfig.peapversion"/>
					</td>
					<td align="left" class="labeltext" valign="top"  >
					    <html:select name="updateEAPTLSDetailsForm" styleId="peapVersion" property="peapVersion" size="1" style="width :125px" tabindex="19" onchange="setPeapNegotiationMethod()">
						   <html:option value="0" >0</html:option>
						   <html:option value="1" >1</html:option> 
						</html:select>	      
					</td>
			</tr>		     
			   <tr>
					<td align="left" class="captiontext" valign="top" >
						<bean:message bundle="servermgrResources" 
							key="servermgr.eapconfig.peapnegotiationmethod"/>
								<ec:elitehelp headerBundle="servermgrResources" 
									text="eapconfig.negotiationmethod" 
										header="servermgr.eapconfig.peapnegotiationmethod"/>
					</td>
					<td align="left" class="labeltext" valign="top" >
					    <html:select name="updateEAPTLSDetailsForm" styleId="peapNegotiationMethod" property="peapNegotiationMethod" tabindex="20" size="1" style="width :125px">
						   <html:option value="26" >EAP-MsCHAPv2</html:option> 
						</html:select><font color="#FF0000"> *</font>	      
					</td>
				  </tr>
			 
		     
		  </logic:equal>  
		  <logic:notEqual value="TTLS" name="updateEAPTLSDetailsForm" property="typeVal">
		  		<logic:notEqual value="PEAP" name="updateEAPTLSDetailsForm" property="typeVal">
		  		 	 <html:hidden name="updateEAPTLSDetailsForm" styleId="ttlsNegotiationMethod" property="ttlsNegotiationMethod" />
		  			 <html:hidden name="updateEAPTLSDetailsForm" styleId="peapNegotiationMethod" property="peapNegotiationMethod" />
		  			 <html:hidden name="updateEAPTLSDetailsForm" styleId="peapVersion" property="peapVersion" />
		  			 <html:hidden name="updateEAPTLSDetailsForm" styleId="ttlscertificateRequest" property="ttlscertificateRequest"/>
		  			 <html:hidden name="updateEAPTLSDetailsForm" styleId="peapCertificateRequest" property="peapCertificateRequest"/>
		  		</logic:notEqual>
		  </logic:notEqual>
		  
				 <!-- vendor specific certificate header -->
				 <tr> 
                    <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vendorspecificcertificate"/></td>
                 </tr>
                 
                 <tr>                      		 
                       		<td align="left" class="captiontext" valign="top" colspan="4" id="button">
                      		 <input type="button" onclick='addRow("dbMappingTable","mappingtbl");'  value="  Add  " class="light-btn" style="size: 140px" tabindex="21"> </td>                      		 
                 </tr>   
                 <!--  -->
				</table>  
			</td>
            
			</tr> 
			
			<tr>
			   
			   <td  colspan="4" valign="top" >
				<table cellSpacing="0" cellPadding="0" width="65%" border="0" id="mappingtbl" class="captiontext">
				<tr>
					<td align="left" class="tblheader" valign="top" width="30%" >
							<bean:message bundle="servermgrResources" 
								key="servermgr.eapconfig.vendoridentifier"/>
									<ec:elitehelp headerBundle="servermgrResources" 
										text="eapconfig.vendoridentifier" 
											header="servermgr.eapconfig.vendoridentifier"/>
					</td>								
					<td align="left" class="tblheader" valign="top" width="66%">
						<bean:message bundle="diameterResources" 
							key="diameterpeerprofile.servercertificate" />
								<ec:elitehelp headerBundle="diameterResources" 
									text="eapconfig.servercertname" 
										header="diameterpeerprofile.servercertificate"/>
					</td>
                    <td align="left" class="tblheader" valign="top" width="2%">Remove</td>
                </tr>		  		
			   <%for(VendorSpecificCertificateData vendorSpecificCertificateData:vendorSpecificList){%>
			    <tr>
					<td class="allborder" width="30%">
						<input class="noborder" id="oui" name="oui"  type="text" maxlength="1000" size="28" style="width: 100%" tabindex="22" value='<%=vendorSpecificCertificateData.getOui()%>' />
					</td>
					<td class="tblrows" width="66%">
						<html:select property="serverCerticateIdForVSC" name="serverCerticateIdForVSC" tabindex="23" styleClass="noborder" value="<%=String.valueOf(vendorSpecificCertificateData.getServerCertificateIdForVSC())%>">
							<html:option value="0">NONE</html:option>
							<%for(ServerCertificateData serverCertificateInst:serverCertificateDataList){ %>
								<html:option value="<%=serverCertificateInst.getServerCertificateId()%>"><%=serverCertificateInst.getServerCertificateName()%></html:option>
							<%} %>
						</html:select>
					</td>
                  <td class="tblrows" align="center" colspan="3" width="4%"><img value="top"
					src="<%=basePath%>/images/minus.jpg" class="delete"
					style="padding-right: 5px; padding-top: 5px;" height="14"
					tabindex="24" /></td>
                </tr>
			 <%}%>
			</table>
		</td>
	</tr> 
	<tr height="20px">
	 <td>&nbsp;</td>
	</tr>    
	<tr>
			     	  <td class="small-gap" >&nbsp;</td>
		     	      <td align="left" class="labeltext" valign="top" >
		     	        <input type="button" name="c_btnUpdate"   onclick="validateUpdate()"  value=" Update "  class="light-btn" tabindex="28"/>
		     	        <input type="button" name="c_btnCancel" tabindex="29" onclick="javascript:location.href='<%=basePath%>/viewEAPConfig.do?viewType=basic&eapId=<%=updateEAPTLSDetailsForm.getEapId()%>'" value="   Cancel   "	class="light-btn" /></td>
					  </td>
	</tr>
	<tr class="small-gap">
	 <td>&nbsp;</td>
	</tr>					        			
</table>
</td>
</tr>
</table>
</div>
</html:form>

<!-- Template of Vendor Specific Certificate List -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder" width="30%">
			<input type="text" id="oui" name="oui" tabindex="25" size="30" maxlength="60"  class="noborder" value="000000*"/>
		</td>
		<td class="tblrows" width="66%">
			<select styleId="serverCerticateIdForVSC" name="serverCerticateIdForVSC" tabindex="26" styleClass="labeltext" class="noborder">
				<option value="0">NONE</option>
				<%for(ServerCertificateData serverCertificateInst:serverCertificateDataList){ %>
					<option value="<%=serverCertificateInst.getServerCertificateId()%>"><%=serverCertificateInst.getServerCertificateName()%></option>
				<%} %>
			</select>
		</td>
		<td class="tblrows" align="center" colspan="3" width="4%"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="27" /></td>
	</tr>
</table>	

<script>
setPeapNegotiationMethod();
</script>