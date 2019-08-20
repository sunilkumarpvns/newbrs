<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="java.util.List"%>

<%
	String basePath = request.getContextPath();
	List classicCsvFeildMap = (List)session.getAttribute("classicCsvFeildMap");	
	List classicCsvStipMap = (List)session.getAttribute("classicCsvStipMap");
%>

<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript">

	$(document).ready(function(){
		$('#mappingtbl td img.delete').live('click',function() {	
				 $(this).parent().parent().remove(); 
			});
		
		$('#mappingtbl1 td img.delete').live('click',function() {	
			 $(this).parent().parent().remove(); 
		});
		
		 $("#enclosingCharacter").keydown(function(event) {
		     if (event.keyCode == 32) {
		         event.preventDefault();
		     }
		     $("#enclosingCharacter").val($.trim($("#enclosingCharacter").val()));
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
 			alert('Location must be specified');
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
	 				if(isValidAttributeMapping("mappingtbl","attributeids")){
	 					if(isValidAttributeMapping("mappingtbl1","attributestripid")){
		 						document.forms[0].action.value='create';	
		 				 		document.forms[0].submit();
	 					}
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
 			if(isValidAttributeMapping("mappingtbl","attributeids")){
					if(isValidAttributeMapping("mappingtbl1","attributestripid")){
							document.forms[0].action.value='create';	
					 		document.forms[0].submit();
					}
			}
 	 	}		
	}


 	function validateIP()
 	{	 		    		   
 			var ipre=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/; 			
 			if(document.forms[0].ipaddress.value != null && document.forms[0].ipaddress.value!="" && !ipre.test(document.forms[0].ipaddress.value)){ 					 	 									
					document.forms[0].ipaddress.focus();
 					return false;
 			}
 			else {
 				return true;
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
 	
 	
	function validate(){
		alert(document.getElementByName('attributeidval').value);			
	}

	function setColumnsOnPrefixFileTextFields(){
		var prefixVal = document.getElementById("prefixFileName").value;
		retriveRadiusDictionaryAttributes(prefixVal,"prefixFileName");
	}

	function setColumnsOnfolderNameTextFields(){
		var folderNameAttrVal = document.getElementById("foldername").value;
		retriveRadiusDictionaryAttributes(folderNameAttrVal,"foldername");
	}
	var indexOfStripId=0;
	function setColumnsOnAttrIdStripFields(stripObj){
		stripObj.id = "attributestripid"+indexOfStripId;
		var attrIdValStrip = document.getElementById("attributestripid").value;
		retriveRadiusDictionaryAttributes(attrIdValStrip,"attributestripid"+indexOfStripId);
		indexOfStripId++;
	}	
	var indexOfId = 0 ;
	function setColumnsOnAttrIdFields(obj){
		obj.id = "attributeids"+indexOfId;
		var attrIdVal = document.getElementById("attributeids").value;
		retriveRadiusDictionaryAttributes(attrIdVal,"attributeids"+indexOfId);
		indexOfId++;
	}
	
</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/createRadiusClassicCSVAcctDriver" styleId="mainform">

	<html:hidden name="createRadiusClassicCSVAcctDriverForm" property="action" />
	<html:hidden name="createRadiusClassicCSVAcctDriverForm" property="driverInstanceName" />
	<html:hidden name="createRadiusClassicCSVAcctDriverForm" property="driverInstanceDesp" />
	<html:hidden name="createRadiusClassicCSVAcctDriverForm" property="driverRelatedId" />
	<html:hidden name="createRadiusClassicCSVAcctDriverForm" property="feildmapcount" />
	<html:hidden name="createRadiusClassicCSVAcctDriverForm" property="patterncount" />
	<html:hidden property="itemIndex" />

	<div id="backgroundPopup">
		<table cellpadding="0" cellspacing="0" border="0"
			width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
			<tr>
				<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
				<td>
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="table-header">
										<bean:message bundle="driverResources" key="driver.createclscsvDriver" />
										</td>
									</tr>
									<tr>
										<td>
											<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
												<tr>
													<td align="left" class="tblheader-bold" valign="top" colspan="4">
														<bean:message bundle="driverResources" key="driver.cdrdetails" />
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="15%">
														<bean:message bundle="driverResources" key="driver.header" />
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.header" header="driver.header"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3" width="73%">
														<html:select property="header" styleId="headerMain" value="false" style="width: 130px" tabindex="1">
															<html:option value="true">True</html:option>
															<html:option value="false">False</html:option>
														</html:select>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.delimeter" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.delimiter" header="driver.delimeter"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<html:text styleId="delimeter" property="delimeter" size="20" tabindex="2" maxlength="50" style="width:250px" />
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top">
															<bean:message bundle="driverResources" key="driver.multivaluedelimeter" />
																<ec:elitehelp headerBundle="driverResources" 
																	text="classiccsvdriver.multivaluedelimiter" header="driver.multivaluedelimeter"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text styleId="multivaluedelimeter" tabindex="3" property="multivaluedelimeter" size="20" maxlength="50" style="width:250px" />
													</td>
												</tr>

												<!-- CDR Timestamp Header  -->
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="classiccsvdriver.cdrtimestampheader" />
														<ec:elitehelp headerBundle="driverResources" text="classiccsvdriver.cdrtimestampheader" header="classiccsvdriver.cdrtimestampheader"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<html:text tabindex="4" styleId="cdrTimestampHeader" property="cdrTimestampHeader" style="width:250px" />
													</td>
												</tr>
												
												<!-- CDR Timestamp Format  -->
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.putcdrtimestamp" />
														<ec:elitehelp headerBundle="driverResources" text="classiccsvdriver.cdrtimestamp" header="driver.putcdrtimestamp"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<html:text tabindex="4" styleId="cdrtimestampFormat" property="cdrtimestampFormat" size="30"  maxlength="50" style="width:250px" />
													</td>
												</tr>
												
												<!-- CDR Timestamp Position  -->
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="classiccsvdriver.cdrtimestampposition" />
														<ec:elitehelp headerBundle="driverResources" text="classiccsvdriver.cdrtimestampposition" header="classiccsvdriver.cdrtimestampposition"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<html:select property="cdrTimestampPosition" styleId="cdrTimestampPosition" style="width: 130px" tabindex="17">
															<html:option value="SUFFIX">Suffix</html:option>
															<html:option value="PREFIX">Prefix</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.enclosingcharacter" />
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.enclosingcharacter" header="driver.enclosingcharacter"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text  styleId="enclosingCharacter" property="enclosingCharacter" size="30" maxlength="8" tabindex="5" style="width:250px" onkeydown="nospaces(this);"/>
													</td>
												</tr>
												
												<tr>
													<td align="left" class="tblheader-bold" valign="top" colspan="4">
														<bean:message bundle="driverResources" key="driver.filedetail" />
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.filename" /> 
													 		<ec:elitehelp headerBundle="driverResources" 
													 			text="classiccsvdriver.filename" header="driver.filename"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text styleId="filename" property="filename" size="20" tabindex="6" maxlength="50" style="width:250px" />
														<font color="#FF0000"> *</font>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.location" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.location" header="driver.location"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text styleId="location" property="location" size="29" tabindex="7" maxlength="50" style="width:250px" />
														<font color="#FF0000"> *</font>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.createblankfile" />
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.createblankfile" header="driver.createblankfile"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:select property="createBlankFile" styleId="createBlankFile" tabindex="8" style="width: 130px">
															<html:option value="true">True</html:option>
															<html:option value="false">False</html:option>
														</html:select>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.prefixFileName" />
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.prefixfilename" header="driver.prefixFileName"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<%-- 	<html:text styleId="prefixFileName" property="prefixfilename" size="20" maxlength="50" />  --%>
														<input type="text" tabindex="9" name="prefixfilename" id="prefixFileName" size="30" autocomplete="off" onkeyup="setColumnsOnPrefixFileTextFields();" style="width: 250px" />
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.defaultdirname" />
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.defaultfoldername" header="driver.defaultdirname"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text styleId="defaultdirname" property="defaultdirname" tabindex="10" size="20" maxlength="50" style="width:250px" />
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.foldername" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.foldername" header="driver.foldername"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<%-- 	<html:text styleId="foldername" property="foldername" size="20" maxlength="50" />    --%>
														<input type="text" tabindex="11" name="foldername" id="foldername" size="30" autocomplete="off" onkeyup="setColumnsOnfolderNameTextFields();" style="width: 250px" />
													</td>
												</tr>

												<!-- <tr>
							<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="driverResources" key="driver.eventDateFormat" /></td>
							<td align="left" class="labeltext" valign="top"  colspan="3">
								<html:text styleId="eventDateFormat" property="eventdateformat" size="20"
								maxlength="50" />
							</td>							
						</tr-->
												<tr>
													<td align="left" class="tblheader-bold" valign="top" colspan="4">
														<bean:message bundle="driverResources" key="driver.filerollingdetail" />
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.timeboundry" /> 
															<ec:elitehelp headerBundle="driverResources"
																text="classiccsvdriver.timeboundry" header="driver.timeboundry"/>
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
																text="classiccsvdriver.sizebasedrollingunit" header="driver.sizebasedrollingunit"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text property="sizeBasedRollingUnit" styleId="sizeBasedRollingUnit" size="30" maxlength="8" tabindex="13" style="width:250px"></html:text>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.timebasedrollingunit" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.timebasedrollingunit" header="driver.timebasedrollingunit"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text property="timeBasedRollingUnit" styleId="timeBasedRollingUnit" size="30" maxlength="8" tabindex="14" style="width:250px"></html:text>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.recordbasedrollingunit" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.recordbasedrollingunit" header="driver.recordbasedrollingunit"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text property="recordBasedRollingUnit" styleId="recordBasedRollingUnit" size="30" maxlength="8" tabindex="15" style="width:250px"></html:text>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.range" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.sequencerange" header="driver.range"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text styleId="range" property="range" size="20" tabindex="16" maxlength="50" style="width:250px" />
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.pos" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.sequenceposition" header="driver.pos"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:select property="pattern" styleId="pattern" style="width: 130px" tabindex="17">
															<html:option value="suffix">Suffix</html:option>
															<html:option value="prefix">Prefix</html:option>
														</html:select>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.global" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.sequenceglob" header="driver.global"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:select property="globalization" styleId="global" style="width: 130px" tabindex="18">
															<html:option value="false">False</html:option>
															<html:option value="true">True</html:option>
														</html:select>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblheader-bold" valign="top" colspan="4">
														<bean:message bundle="driverResources" key="driver.filetransferdetails" />
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.detaillocal.allocatingprotocol" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.protocol" header="driver.detaillocal.allocatingprotocol"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:select property="allocatingprotocol" styleId="allocatingProtocol" tabindex="19" style="width: 130px">
															<html:option value="NONE">None</html:option>
															<html:option value="LOCAL">Local</html:option>
															<html:option value="FTP">FTP</html:option>
															<html:option value="SMTP">SMTP</html:option>
														</html:select>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.ipaddress" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.ipaddress" header="driver.ipaddress"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:text styleId="ipaddress" property="ipaddress" size="20" tabindex="20" maxlength="50" style="width:250px" /><font color="#999999"> Host : Port </font>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.remoteLocation" />
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.remoteloc" header="driver.remoteLocation"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text styleId="remoteLocation" property="remotelocation" size="29" tabindex="21" maxlength="50" style="width:250px" />
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.username" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.username" header="driver.username"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<html:text styleId="username" property="username" size="20" tabindex="22" maxlength="50" style="width:250px" />
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.password" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.password" header="driver.password"/>
													</td>
													<td align="left" class="labeltext" valign="top">
														<html:password styleId="password" property="password" size="20" tabindex="23" maxlength="50" style="width:250px"></html:password>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.postoperation" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.postoperation" header="driver.postoperation"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:select property="postoperation" styleId="postOperation" value="archive" tabindex="24"  style="width: 130px">
															<html:option value="rename">Rename</html:option>
															<html:option value="archive">Archive</html:option>
															<html:option value="delete">Delete</html:option>
														</html:select>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.archiveloc" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.archiveloc" header="driver.archiveloc"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text property="archivelocation" styleId="archiveLocation" tabindex="25" style="width:250px" maxlength="1024" />
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="driverResources" key="driver.failovertime" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="classiccsvdriver.failovertime" header="driver.failovertime"/> 
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
														<html:text styleId="failOverTime" property="failovertime" size="20" tabindex="26" maxlength="50" style="width:250px" />
													</td>
												</tr>
												<tr>
													<td align="left" class="tblheader-bold" valign="top" colspan="4">
														<bean:message bundle="driverResources" key="driver.csvfeildmap" />
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" colspan="3" id="button">
														<input type="button" onclick='addRow("dbMappingTable","mappingtbl");' tabindex="27" value=" Add Mapping" class="light-btn" style="size: 140px">
													</td>
												</tr>
												<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td width="100%" colspan="4" valign="top" class="captiontext">
														<table cellSpacing="0" cellPadding="0" width="90%"
															border="0" id="mappingtbl" class="box">
															<tr>
																<td align="left" class="tblheader" valign="top" id="tbl_attrid">
																	<bean:message bundle="driverResources" key="driver.attributeids" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="classiccsvdriver.Attid" header="driver.attributeids"/>
																</td>
																<td align="left" class="tblheader" valign="top">
																	<bean:message bundle="driverResources" key="driver.tblheader" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="classiccsvdriver.header" header="driver.tblheader"/>
																</td>
																<td align="left" class="tblheader" valign="top" width="15%">
																	<bean:message bundle="driverResources" key="driver.defaultvalue" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="classiccsvdriver.defaultvalue" header="driver.defaultvalue"/>
																</td>
																<td align="left" class="tblheader" valign="top"> 
																	<bean:message bundle="driverResources" key="driver.dictionary" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="classiccsvdriver.dicvalue" header="driver.dictionary"/>
																</td>
																<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
															</tr>
															
															<logic:iterate id="obj" name="defaultMapping" type="com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAttrRelationData">
															<tr>
																<td class="tblrows">
																	<input class="noborder" onfocus="setColumnsOnAttrIdFields(this);" type="text" name="attributeids" maxlength="1000" size="28" style="width: 100%" tabindex="28" value='<bean:write name="obj" property="attributeids"/>' />
																</td>
																<td class="tblrows">
																	<input type="text" name="headerval" id="headerval"  maxlength="1000" tabindex="29" class="noborder" size="28" style="width: 100%" value='<bean:write name="obj" property="header"/>'/>
																</td>	
																<td class="tblrows">
																	<input class="noborder" type="text" id="defaultValue" name="defaultValue" maxlength="1000" size="28" tabindex="30" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="defaultvalue"/>'/>
																</td>
																<td class="tblrows">
																	<select class="noborder" name="usedictionaryvalue" id="usedictionaryvalue" style="width: 100%" tabindex="31">
																		<option value='true' <logic:equal value="true" name="obj" property="usedictionaryvalue">selected</logic:equal>>true</option>
																		<option value='false' <logic:equal value="false" name="obj" property="usedictionaryvalue">selected</logic:equal>>false</option>		
																	</select> 
																	</td>
																<td class="tblrows" align="center" colspan="3"><img
																	value="top" src="<%=basePath%>/images/minus.jpg" 
																	class="delete"
																	style="padding-right: 5px; padding-top: 5px;"
																	height="14" tabindex="32" /></td>
															</tr>
														</logic:iterate>
														</table>
													</td>
												</tr>
												<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="4"><bean:message bundle="driverResources"
															key="driver.csvstripmapping" /></td>
												</tr>

												<tr>
													<td align="left" valign="top" colspan="4"
														class="captiontext"><input type="button"
														onclick='addRow("dbMappingTable1","mappingtbl1");' value=" Add Strip Pattern Mapping"
														tabindex="33" class="light-btn"></td>
												</tr>
												<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td align="left" width="70%" colspan="3" valign="top">
														<table cellSpacing="0" cellPadding="0" width="70%"
															border="0" id="mappingtbl1" class="captiontext">
															<tr>

																<td align="left" class="tblheader" valign="top" id="tbl_attrid">
																	<bean:message bundle="driverResources" key="driver.pattern.attributeid" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="classiccsvdriver.stripattributeid" 
																				header="driver.pattern.attributeid" />
																</td>
																<td align="left" class="tblheader" valign="top" width="15%">
																	<bean:message bundle="driverResources" key="driver.pattern" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="classiccsvdriver.pattern" header="driver.pattern"/>
																</td>
																<td align="left" class="tblheader" valign="top">
																	<bean:message bundle="driverResources" key="driver.pattern.separator" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="classiccsvdriver.separator" header="driver.pattern.separator"/>
																</td>
																<td align="left" class="tblheader" valign="top"
																	width="5%">Remove</td>
															</tr>
														</table>
													</td>
												</tr>


												<tr>
													<td align="left" class="labeltext" colspan="4" valign="top">
														&nbsp;</td>
												</tr>



												<tr>
													<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
												</tr>
											</table>
										</td>
									</tr>


									<tr>
										<td class="btns-td" valign="middle" colspan="4"><input
											type="button" value="Previous " onclick="history.go(-1)"
											class="light-btn" tabindex="43" /> <input type="button"
											name="c_btnCreate" id="c_btnCreate2" tabindex="44"
											value="Create" class="light-btn" 
											onclick="validateForm()"> <input type="reset"
											name="c_btnDeletePolicy" tabindex="45"
											onclick="javascript:window.location.href='<%=basePath%>/initSearchDriver.do?'"
											value="Cancel" class="light-btn"  /></td>
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

<!-- Mapping Table Row template for Classic CSV field Mapping -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder">
			<input type="text" name="attributeids" id="attributeids" class="noborder" tabindex="34" maxlength="1000" size="28" style="width: 100%" onfocus="setColumnsOnAttrIdFields(this);" />
		</td>
		<td class="tblrows">
			<input type="text" name="headerval" tabindex="35" id="headerval"  maxlength="1000" class="noborder" size="28" style="width: 100%" />
			
		</td>
		<td class="tblrows">
			<input type="text" name="defaultValue" id="defaultvalue"  maxlength="1000" class="noborder" tabindex="36" size="28" style="width: 100%" />
		</td>
		<td class="tblrows">
	 		<select style="width: 100%" class="noborder" id="usedictionaryvalue" tabindex="37" name="usedictionaryvalue">
				<option value="true"> true</option>
				<option value="false" selected="selected" >false</option>
			</select> 
		</td> 
		<td class="tblrows" align="center" colspan="3">
			<img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="38" />
		</td>
	</tr>
</table>

<!-- Mapping Table Row template for Classic CSV Strip Pattern Relation Mapping -->
<table style="display: none;" id="dbMappingTable1">
	<tr>
		<td class="allborder">
			<input type="text" name="attributestripid" id="attributestripid" tabindex="39" class="noborder" maxlength="1000" size="28" style="width: 100%" onfocus="setColumnsOnAttrIdStripFields(this);" />
		</td>
		<td class="tblrows">
	
			 <select style="width: 100%" class="noborder" id="patt" name="patt" tabindex="40">
			<option value="suffix">suffix</option>
			<option value="prefix">prefix</option>
		</select> 
		</td>
		<td class="tblrows">
			<input type="text" name="separator" id="separator"  maxlength="1000" tabindex="41" class="noborder" size="28" style="width: 100%" />
		</td> 
		<td class="tblrows" align="center" colspan="3"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="42" /></td>
	</tr>
</table>
<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 	
</script>



