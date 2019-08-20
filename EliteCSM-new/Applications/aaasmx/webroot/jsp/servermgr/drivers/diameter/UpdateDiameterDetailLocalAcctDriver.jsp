<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@ page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterDetailLocalAcctDriverForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<%
		UpdateDiameterDetailLocalAcctDriverForm updateDiameterDetailLocalAcctDriverForm = (UpdateDiameterDetailLocalAcctDriverForm)request.getAttribute("updateDiameterDetailLocalAcctDriverForm");
		DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstance");
%>
<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>

<script>

	var isValidName;
	$(document).ready(function(){
		$('#mappingtbl td img.delete').live('click',function() {	
				 $(this).parent().parent().remove(); 
			});
		
		
	}); 

	 /* This functions check for entered value is digits or not. */
	 function isValidDigitsInRolling(){
		 if(!isEmpty($("#sizeBasedRollingUnit").val()) && !(isNaN($("#sizeBasedRollingUnit").val())) && ($.trim($("#sizeBasedRollingUnit").val()) < 0)){
				alert('Negative value is not allowed in Size Based Rolling Unit');
				document.forms[0].sizeBasedRollingUnit.focus();
				return false;
			}else if(!isEmpty($("#timeBasedRollingUnit").val()) && !(isNaN($("#timeBasedRollingUnit").val())) && ($.trim($("#timeBasedRollingUnit").val()) < 0)){
				alert('Negative value is not allowed in Time Based Rolling Unit');
				document.forms[0].timeBasedRollingUnit.focus();
				return false;
			}else if(!isEmpty($("#recordBasedRollingUnit").val()) && !(isNaN($("#recordBasedRollingUnit").val())) && ($.trim($("#recordBasedRollingUnit").val()) < 0)){
				alert('Negative value is not allowed in Record Based Rolling Unit');
				document.forms[0].recordBasedRollingUnit.focus();
				return false;
			}else if(!isEmpty($("#sizeBasedRollingUnit").val()) && !isNumber($.trim($("#sizeBasedRollingUnit").val()))){
				alert('Only Numeric allow in Size Based Rolling Unit');
				document.forms[0].sizeBasedRollingUnit.focus();
				return false;
			}else if(!isEmpty($("#timeBasedRollingUnit").val()) && !isNumber($.trim($("#timeBasedRollingUnit").val()))){
				alert('Only Numeric allow in Time Based Rolling Unit');
				document.forms[0].timeBasedRollingUnit.focus();
				return false;
			}else if(!isEmpty($("#recordBasedRollingUnit").val()) && !isNumber($.trim($("#recordBasedRollingUnit").val()))){
				alert('Only Numeric allow in Record Based Rolling Unit');
				document.forms[0].recordBasedRollingUnit.focus();
				return false;
			}else if(isEmpty($("#recordBasedRollingUnit").val()) && isEmpty($("#timeBasedRollingUnit").val()) && isEmpty($("#sizeBasedRollingUnit").val()) && $("#timeBoundry").val() == "0"){
				alert('One Field is Mandatory Out of \n\n1) Time-Boundry\n2) Sized Based Rolling Unit\n3) Time Based Rolling Unit\n4) Record Based Rolling Unit');
				document.forms[0].timeBoundry.focus();
				return false;
			}
			return true;
		}	
	
	function validateForm(){	

		if(isNull(document.forms[0].driverinstname.value)){
			alert('Name must be specified');
		}else if(!isValidName) {
 			alert('Enter Valid Driver Name');
 			document.forms[0].driverinstname.focus();
 			return;
 		}else if(isNull(document.forms[0].filename.value)){
 			alert('File Name must be specified.');
 		}else if(isNull(document.forms[0].location.value)){
 			alert('Location time must be specified');
 		}else if(!(isValidDigitsInRolling())){
			return;
 		}else if(document.forms[0].allocatingProtocol.value.toLowerCase() != "Local".toLowerCase() && document.forms[0].allocatingProtocol.value.toLowerCase() != "None".toLowerCase()){
 			if(isNull(document.forms[0].ipaddress.value)){
 				if(document.forms[0].allocatingProtocol.value.toLowerCase() == "SMTP".toLowerCase()){
					alert('IP Address must be specified');
					document.forms[0].ipaddress.focus();
				}else{
					alert('IP Address : Port must be specified');
					document.forms[0].ipaddress.focus();
				}
	 		}else{
	 			var validIp=false,smtpFlag=false;
	 			if(document.forms[0].allocatingProtocol.value.toLowerCase() == "FTP".toLowerCase()){
	 				validIp=validateIpAddress();
	 			}else if(document.forms[0].allocatingProtocol.value.toLowerCase() == "SMTP".toLowerCase()){
	 				validIp=validateIpAddressForSMTP();
	 				smtpFlag=true;
	 			}
	 			if(validIp==true){
	 				if(isValidAttributeMapping("mappingtbl","attributeidval")){
			 				document.forms[0].action.value = 'Update';
			 	 			document.forms[0].submit();
					}
	 			}else{
	 				if(smtpFlag == true){
 						alert('Please Enter Valid IP Address');
		 				document.forms[0].ipaddress.focus();
 					}else{
	 			   		alert('Please Enter Valid Address (HOST:PORT)');
		 				document.forms[0].ipaddress.focus();
 					}
	 			}
	 			
	 		}
 		}else{ 	 		
 			if(isValidAttributeMapping("mappingtbl","attributeidval")){
		 			document.forms[0].action.value='Update';	
			 		document.forms[0].submit();
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
	function validateIpAddressForSMTP(){
		var flagIp=false,flagPort=false,validIP=false;
		var ipAddress=document.getElementById("ipaddress").value;
		
		var ipAds = ipAddress.split(":").length - 1;
		if(ipAds == 0){
			flagIp=validateIP(ipAddress);
			flagPort=true;
		}else if(ipAds==1){
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
		     document.forms[0].ipaddress.focus();
		}else{
		    if(flagIp==true && flagPort==true){
		    	validIP=true;
		    }
	     }
		   return validIP;
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
		var ipAddress=document.getElementById("ipaddress").value;
		
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
		     document.forms[0].ipaddress.focus();
		}else{
		    if(flagIp==true && flagPort==true){
		    	validIP=true;
		    }
	     }
		   return validIP;
	}

	
	function setColumnsOnPrefixFileTextFields(){
		var prefixVal = document.getElementById("prefixFileName").value;
		retriveDiameterDictionaryAttributes(prefixVal,"prefixFileName");
	}
	
	function setColumnsOnfolderNameTextFields(){
		var folderNameAttrVal = document.getElementById("folderName").value;
		retriveDiameterDictionaryAttributes(folderNameAttrVal,"folderName");
	}
	
	var indexOfId = 0 ;
	function setColumnsOnAttrIdFields(obj){
		obj.id = "attributeId"+indexOfId;
		var attrIdVal = document.getElementById("attributeidval").value;
		retriveDiameterDictionaryAttributes(attrIdVal,"attributeId"+indexOfId);
		indexOfId++;
	}
	var isValidName;
	function verifyName() {
		var searchName = document.getElementById("driverinstname").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
	}
	
</script>
<html:form action="/updateDiameterDetailLocalAcctDriver">

	<html:hidden property="action" />
	<html:hidden property="driverRelatedId" />
	<html:hidden property="auditUId" styleId="auditUId"/>


	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources"
								key="driver.driverinstancedetails" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="35%">
							<bean:message bundle="driverResources" key="driver.instname" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.name" header="driver.instname"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								tabindex="1" styleId="driverinstname" onkeyup="verifyName();"
								property="driverInstanceName" size="30" maxlength="60"
								style="width:250px" /><font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="35%">
							<bean:message bundle="driverResources" key="driver.instdesc" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.description" header="driver.instdesc"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								tabindex="2" styleId="driverinstdesc"
								property="driverInstanceDesp" size="30" maxlength="60"
								style="width:250px" /></td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources"
								key="driver.detaillocal.details" />
						</td>
					</tr>


					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.eventDateFormat" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.eventdate" 
											header="driver.eventDateFormat"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="3" styleId="eventDateFormat"
								property="eventDateFormat" size="30" maxlength="50"
								style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.writeAttributes" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.writeAttributes" 
											header="driver.writeAttributes"/>						
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:select tabindex="4" property="writeAttributes"
								styleId="writeattributes" style="width:130px">
								<html:option value="Configured">Configured</html:option>
								<html:option value="All">All</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.usedicval" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.usedicval" 
											header="driver.usedicval"/>							
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:select tabindex="5" property="useDictionaryValue"
								styleId="useDictionaryValue" style="width:130px">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.avpairSeperator" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.avpairseperator" 
											header="driver.avpairSeperator"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="6" styleId="avpairSeperator"
								property="avpairSeperator" size="20" maxlength="50"
								style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="driverResources" key="driver.filedetail" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.filename" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.filename" 
											header="driver.filename"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="7" styleId="filename" property="fileName"
								size="20" maxlength="50" style="width:250px" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.location" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.location" 
											header="driver.location"/>								
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="8" styleId="location" property="location"
								size="30" maxlength="50" style="width:250px" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.defaultdirname" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.defaultdirname" 
											header="driver.defaultdirname"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="9" styleId="defaultdirname"
								property="defaultDirName" size="20" maxlength="50"
								style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.prefixFileName" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.prefixFileName" 
											header="driver.prefixFileName"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<!--  	<html:text styleId="prefixFileName" property="prefixFileName" size="20" maxlength="50" />    -->
							<input type="text" tabindex="10" name="prefixFileName"
							id="prefixFileName" size="30"
							value="<%=(updateDiameterDetailLocalAcctDriverForm.getPrefixFileName() != null) ? updateDiameterDetailLocalAcctDriverForm.getPrefixFileName() : ""%>"
							autocomplete="off"
							onkeyup="retriveDiameterDictionaryAttributes(this.value,'prefixFileName');"
							style="width: 250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.foldername" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.foldername" 
											header="driver.foldername"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<!--  	<html:text styleId="foldername" property="folderName" size="20" maxlength="50" />    -->
							<input tabindex="11" type="text" name="folderName"
							id="folderName" size="30"
							value="<%=(updateDiameterDetailLocalAcctDriverForm.getFolderName() != null) ? updateDiameterDetailLocalAcctDriverForm.getFolderName() : ""%>"
							autocomplete="off"
							onkeyup="retriveDiameterDictionaryAttributes(this.value,'folderName');"
							style="width: 250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="driverResources"
								key="driver.filerollingdetail" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="driverResources" key="driver.timeboundry" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="classiccsvdriver.timeboundry" 
										header="driver.timeboundry"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="70%">
							<html:select property="timeBoundry" styleId="timeBoundry" style="width: 130px" tabindex="12">
								<html:option value="0">NONE</html:option>
								<html:option value="1">1 Min</html:option>
								<html:option value="2">2 Min</html:option>
								<html:option value="3">3 Min</html:option>
								<html:option value="5">5 Min</html:option>
								<html:option value="10">10 Min</html:option>
								<html:option value="15">15 Min</html:option>
								<html:option value="20">20 Min</html:option>
								<html:option value="30">30 Min</html:option>
								<html:option value="60">Hourly</html:option>
								<html:option value="1440">Daily</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="driverResources" key="driver.sizebasedrollingunit" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="classiccsvdriver.sizebasedrollingunit" 
										header="driver.sizebasedrollingunit"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="70%">
							<logic:equal value="0" property="sizeBasedRollingUnit" name="updateDiameterDetailLocalAcctDriverForm">
								<html:text property="sizeBasedRollingUnit" styleId="sizeBasedRollingUnit" size="30" maxlength="8" tabindex="13" style="width:250px" value=""></html:text>
							</logic:equal>
							<logic:notEqual value="0" property="sizeBasedRollingUnit" name="updateDiameterDetailLocalAcctDriverForm">
								<html:text property="sizeBasedRollingUnit" styleId="sizeBasedRollingUnit" size="30" maxlength="8" tabindex="13" style="width:250px"></html:text>
							</logic:notEqual>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="driverResources" key="driver.timebasedrollingunit" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="classiccsvdriver.timebasedrollingunit" 
										header="driver.timebasedrollingunit"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="70%">
							<logic:equal value="0" property="timeBasedRollingUnit" name="updateDiameterDetailLocalAcctDriverForm">
								<html:text property="timeBasedRollingUnit" styleId="timeBasedRollingUnit" size="30" maxlength="8" tabindex="14" style="width:250px" value=""></html:text>
							</logic:equal>
							<logic:notEqual value="0" property="timeBasedRollingUnit" name="updateDiameterDetailLocalAcctDriverForm">
								<html:text property="timeBasedRollingUnit" styleId="timeBasedRollingUnit" size="30" maxlength="8" tabindex="14" style="width:250px"></html:text>
							</logic:notEqual>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="driverResources" key="driver.recordbasedrollingunit" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="classiccsvdriver.recordbasedrollingunit" 
										header="driver.recordbasedrollingunit"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="70%">
							<logic:equal value="0" property="recordBasedRollingUnit" name="updateDiameterDetailLocalAcctDriverForm">
								<html:text property="recordBasedRollingUnit" styleId="recordBasedRollingUnit" size="30" maxlength="8" tabindex="15" style="width:250px" value=""></html:text>
							</logic:equal>
							<logic:notEqual value="0" property="recordBasedRollingUnit" name="updateDiameterDetailLocalAcctDriverForm">
								<html:text property="recordBasedRollingUnit" styleId="recordBasedRollingUnit" size="30" maxlength="8" tabindex="15" style="width:250px"></html:text>
							</logic:notEqual>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.range" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.range" header="driver.range"/>	
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="16" styleId="range" property="range"
								size="20" maxlength="50" style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.pos" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.pos" header="driver.pos"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:select tabindex="17" property="pattern" styleId="pattern"
								style="width:130px">
								<html:option value="Suffix">Suffix</html:option>
								<html:option value="Prefix">Prefix</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.global" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.global" 
											header="driver.global"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:select tabindex="18" property="globalization"
								styleId="global" style="width:130px">
								<html:option value="False">False</html:option>
								<html:option value="True">True</html:option>

							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources"
								key="driver.filetransferdetails" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.detaillocal.allocatingprotocol" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.protocol" 
											header="driver.detaillocal.allocatingprotocol"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:select tabindex="19" property="allocatingProtocol"
								styleId="allocatingProtocol" style="width:130px">
								<html:option value="NONE">None</html:option>
								<html:option value="Local">Local</html:option>
								<html:option value="FTP">FTP</html:option>
								<html:option value="SMTP">SMTP</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.ipaddress" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.ipaddress" 
											header="driver.ipaddress"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="20" styleId="ipaddress" property="ipaddress"
								size="20" maxlength="50" style="width:250px" />
								<font color="#999999"> Host : Port </font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.remoteLocation" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.remoteLocation" 
											header="driver.remoteLocation"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="21" styleId="remoteLocation"
								property="remoteLocation" size="30" maxlength="50"
								style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.username" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.username" 
											header="driver.username"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="22" styleId="username" property="userName"
								size="20" maxlength="50" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.password" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.password" 
											header="driver.password"/> 
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
								<html:password styleId="password" property="password" tabindex="23" size="20" maxlength="50" style="width:250px"></html:password>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.postoperation" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.postoperation" 
											header="driver.postoperation"/> 	
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:select tabindex="24" property="postOperation"
								styleId="postOperation" style="width:130px">
								<html:option value="Rename">Rename</html:option>
								<html:option value="Archive">Archive</html:option>
								<html:option value="Delete">Delete</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.archiveloc" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.archiveloc" 
											header="driver.archiveloc"/>	
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text property="archiveLocation" styleId="archiveLocation" tabindex="25" style="width:250px" maxlength="1024"></html:text>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="driverResources"
								key="driver.failovertime" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="detaildriver.failovertime" 
											header="driver.failovertime"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:text tabindex="26" styleId="failOverTime"
								property="failOverTime" size="20" maxlength="50"
								style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.relation" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="2"><input
							type="button" tabindex="27" onclick='addRow("dbMappingTable","mappingtbl");'
							value=" Add Mapping " class="light-btn"></td>
					</tr>

				</table>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="left" class="text" colspan="3" valign="top">
							<table width="98%" id="mappingtbl" cellpadding="0"
								cellspacing="0" class="captiontext">
								<tr>
									<td align="left" class="tblheader" valign="top" width="30%">
										<bean:message bundle="driverResources" 
											key="driver.attrids" />
												<ec:elitehelp headerBundle="driverResources" 
													text="classiccsvdriver.Attid" 
														header="driver.attrids"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="30%">
										<bean:message bundle="driverResources" 
											key="driver.defaultvalue" />
												<ec:elitehelp headerBundle="driverResources" 
													text="classiccsvdriver.defaultvalue" 
														header="driver.defaultvalue"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="30%">
										<bean:message bundle="driverResources" 
											key="driver.usedicvalue" />
												<ec:elitehelp headerBundle="driverResources" 
													text="classiccsvdriver.dicvalue" 
														header="driver.usedicvalue"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="10%">Remove</td>
								</tr>
									<logic:iterate id="obj" name="detailLocalDriverDataSet" type="com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAttrRelationData" scope="session">
									<tr>
										<td class="allborder">
											<input type="text" name="attributeidval" tabindex="28" id="attributeidval" class="noborder" maxlength="1000" size="28" style="width: 100%" onfocus="setColumnsOnAttrIdFields(this);" value='<bean:write name="obj" property="attrIds"/>'/>
										</td>
										<td class="tblrows">
											<input type="text" name="defaultval" id="defaultval" tabindex="29"  maxlength="1000" class="noborder" size="28" style="width: 100%" value='<bean:write name="obj" property="defaultValue"/>'/>
										</td>
										<td class="tblrows">
										<select class="noborder"
											name="usedicval" id="usedicval" style="width: 100%"
											tabindex="30">
												<option value='True' <logic:equal value="True" name="obj" property="useDictionaryValue">selected</logic:equal>>True</option>
												<option value='False' <logic:equal value="False" name="obj" property="useDictionaryValue">selected</logic:equal>>False</option>		
										</select> 
										</td>
										<td class="tblrows" align="center" colspan="3"><img
											value="top" src="<%=basePath%>/images/minus.jpg"
											class="delete" style="padding-right: 5px; padding-top: 5px;"
											height="14" tabindex="31" /></td>
									</tr>
					</logic:iterate>
							</table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" name="c_btnCreate" id="c_btnCreate2" tabindex="36"
							value=" Update " class="light-btn" onclick="validateForm()">
							<input type="reset" tabindex="37" name="c_btnDeletePolicy"
							onclick="javascript:window.location.href='<%=basePath%>/initSearchDriver.do?'"
							value="Cancel" class="light-btn" />
					</tr>
				</table>

			</td>
		</tr>
	</table>
</html:form>
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder">
			 <input type="text" name="attributeidval" id="attributeidval" tabindex="32" class="noborder" maxlength="1000" size="28" style="width: 100%" onfocus="setColumnsOnAttrIdFields(this);" />
		</td>
		<td class="tblrows">
			<input type="text" name="defaultval" id="defaultval" tabindex="33" maxlength="1000" class="noborder" size="28" style="width: 100%" />  
		</td>
		<td class="tblrows">
		<select class="noborder"  name="usedicval" id="usedicval" style="width: 100%" tabindex="34">
					<option value='True'>True</option>
					<option value='False'>False</option>									
		</select> 	
		</td>
		<td class="tblrows" align="center" colspan="3"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="35" /></td>
	</tr>
</table>


