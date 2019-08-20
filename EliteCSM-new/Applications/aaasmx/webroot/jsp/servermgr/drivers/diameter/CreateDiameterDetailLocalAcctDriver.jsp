<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.*"%>
<%@page import="java.util.Set"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>

<%
	String basePath = request.getContextPath();
	List detailLocalAcctList = (List)session.getAttribute("detailLocalAcctList");	
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript">
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

		if(isNull(document.forms[0].filename.value)){
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
			 				document.forms[0].action.value = 'create';
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
	 				document.forms[0].action.value = 'create';
	 	 			document.forms[0].submit();
			}
 	 	} 	
 	  	 			 	 			 		
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
		var folderNameAttrVal = document.getElementById("foldername").value;
		retriveDiameterDictionaryAttributes(folderNameAttrVal,"foldername");
	}

	var indexOfId = 0 ;
	function setColumnsOnAttrIdFields(obj){
		obj.id = "attributeId"+indexOfId;
		var attrIdVal = document.getElementById("attributeidval").value;
		retriveDiameterDictionaryAttributes(attrIdVal,"attributeId"+indexOfId);
		indexOfId++;
	}
	function validate(){
		alert(document.getElementByName('attributeidval').value);			
	}
	
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
</script>



<html:form action="/createDiameterDetailLocalAcctDriver">

	<html:hidden name="createDiameterDetailLocalAcctDriverForm"
		property="action" />
	<html:hidden name="createDiameterDetailLocalAcctDriverForm"
		property="driverInstanceName" />
	<html:hidden name="createDiameterDetailLocalAcctDriverForm"
		property="driverInstanceDesp" />
	<html:hidden name="createDiameterDetailLocalAcctDriverForm"
		property="driverRelatedId" />
	<html:hidden name="createDiameterDetailLocalAcctDriverForm"
		property="itemIndex" />

	<div id="backgrounddiv">
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
												bundle="driverResources" key="driver.createdetaillocDriver" /></td>
									</tr>
									<tr>
										<td class="small-gap" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td colspan="4">
											<table cellpadding="0" cellspacing="0" border="0"
												width="100%">
												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.detaillocal.details" /></td>
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
														<html:text styleId="eventDateFormat"
															property="eventDateFormat" size="30" tabindex="1"
															maxlength="50" style="width:250px" />
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
														<html:select property="writeAttributes"
															styleId="writeattributes" style="width:130px"
															tabindex="2">
															<html:option value="All">All</html:option>
															<html:option value="Configured">Configured</html:option>
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
														<html:select property="useDictionaryValue" tabindex="3"
															styleId="useDictionaryValue" value="true"
															style="width:130px">
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
														<html:text styleId="avpairSeperator"
															property="avpairSeperator" size="20" tabindex="4"
															maxlength="50" style="width:250px" />
													</td>
												</tr>
												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="4"><bean:message bundle="driverResources"
															key="driver.filedetail" /></td>
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
														<html:text styleId="filename" property="fileName"
															size="20" tabindex="5" maxlength="50" style="width:250px" /><font
														color="#FF0000"> *</font>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.location" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="detaildriver.location" 
																		header="driver.location"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="location" property="location"
															size="30" tabindex="6" maxlength="50" style="width:250px" /><font
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
														<html:text styleId="defaultdirname"
															property="defaultDirName" size="20" tabindex="7"
															maxlength="50" style="width:250px" />
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
														<input type="text" tabindex="8" name="prefixFileName"
														id="prefixFileName" size="30" autocomplete="off"
														onkeyup="setColumnsOnPrefixFileTextFields();"
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
														<input type="text" tabindex="9" name="foldername"
														id="foldername" size="30" autocomplete="off"
														onkeyup="setColumnsOnfolderNameTextFields();"
														style="width: 250px" />
													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="4"><bean:message bundle="driverResources"
															key="driver.filerollingdetail" /></td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.timeboundry" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.timeboundry" 
																	header="driver.timeboundry"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:select property="timeBoundry" styleId="timeBoundry" style="width: 130px" tabindex="10">
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
														<html:text property="sizeBasedRollingUnit" styleId="sizeBasedRollingUnit" size="30" maxlength="8" tabindex="11" style="width:250px"></html:text>
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
														<html:text property="timeBasedRollingUnit" styleId="timeBasedRollingUnit" size="30" maxlength="8" tabindex="12" style="width:250px"></html:text>
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
														<html:text property="recordBasedRollingUnit" styleId="recordBasedRollingUnit" size="30" maxlength="8" tabindex="13" style="width:250px"></html:text>
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
														<html:text styleId="range" property="range" size="20"
															tabindex="14" maxlength="50" style="width:250px" />
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
														<html:select property="pattern" styleId="pattern"
															style="width:130px" tabindex="15">
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
														<html:select property="globalization" styleId="global"
															style="width:130px" tabindex="16">
															<html:option value="False">False</html:option>
															<html:option value="True">True</html:option>

														</html:select>
													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.filetransferdetails" /></td>
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
														<html:select tabindex="17" property="allocatingProtocol"
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
														<html:text tabindex="18" styleId="ipaddress"
															property="ipaddress" size="20" maxlength="50"
															style="width:250px" /><font color="#999999"> Host : Port </font>
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
														<html:text tabindex="19" styleId="remoteLocation"
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
														<html:text tabindex="20" styleId="username"
															property="userName" size="20" maxlength="50"
															style="width:250px" />
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
														<html:password styleId="password" tabindex="21" property="password" size="20" maxlength="50" style="width:250px"></html:password>
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
														<html:select tabindex="22" property="postOperation"
															styleId="postOperation" value="Archive"
															style="width:130px">
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
														<html:text property="archiveLocation" styleId="archiveLocation" tabindex="23" style="width:250px" maxlength="1024"></html:text>
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
														<html:text tabindex="24" styleId="failOverTime"
															property="failOverTime" size="20" maxlength="50"
															style="width:250px" />
													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.relation" /></td>
												</tr>


												<tr>
													<td class="btns-td" align="left" colspan="2">
													<input tabindex="25" type="button" onclick='addRow("dbMappingTable","mappingtbl");'
														value=" Add Mapping" class="light-btn" style="size: 140px"/>
													</td>
												</tr>
												<tr>
													<td class="small-gap" colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" colspan="3"
														valign="top">
														<table width="98%" id="mappingtbl" cellpadding="0" cellspacing="0">
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
																<td align="left" class="tblheader" valign="top"
																	width="10%">Remove</td>
															</tr>
														</table>
													</td>
												</tr>

												<tr>
													<td class="btns-td" valign="middle">&nbsp;</td>
													<td class="btns-td" valign="middle" colspan="2"><input
														type="button" name="c_btnCreate" tabindex="30"
														id="c_btnCreate2" value=" Create " class="light-btn"
														onclick="validateForm()"> <input type="reset"
														name="c_btnDeletePolicy"
														onclick="javascript:window.location.href='<%=basePath%>/initSearchDriver.do?'"
														value="Cancel" class="light-btn" tabindex="31" /></td>
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
	</div>	
</html:form>
<!-- Mapping Table Row template -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder">
			<input type="text" name="attributeidval" id="attributeidval" tabindex="26" class="noborder" maxlength="1000" size="28" style="width: 100%" onfocus="setColumnsOnAttrIdFields(this);" />
		</td>
		<td class="tblrows">
			<input type="text" name="defaultval" id="defaultval" tabindex="27"  maxlength="1000" class="noborder" size="28" style="width: 100%" />
			
		</td>
		<td class="tblrows">
	 <select style="width: 100%" class="noborder" id="usedicval" name="usedicval" tabindex="28" >
			<option value="True"> True</option>
			<option value="False">False</option>
		</select> 
		</td> 
		<td class="tblrows" align="center" colspan="3"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="29" /></td>
	</tr>
</table>


