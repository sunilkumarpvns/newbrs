<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestData" %>
<%@ page import="com.elitecore.elitesm.web.radius.radtest.forms.UpdateRadiusTestForm" %>
<%
    String basePath = request.getContextPath(); 
    String description = "&nbsp;<b>Parameter name: </b><br/><br/>" +
  	      "&nbsp;&nbsp;&nbsp; Select the required parameter name from the available dictionaries.<br/><br/>" +
		  "&nbsp;<b>Parameter value:</b><br/><br/>"+
		   "&nbsp;&nbsp;&nbsp;Specify the value of the selected parameter.<br/>"+
	       "&nbsp;&nbsp;&nbsp;If the selected parameter is a grouped(nested) attribute, then specify the values of sub attributes in the form of<br/>"+
		   "&nbsp;&nbsp;&nbsp;sub-attribute-id-1=value-1;sub-attribute-id-2=value-2;...;sub-attribute-id-n=value-n<br/><br/>"+
		   "&nbsp;&nbsp;&nbsp;e.g.<br/>"+
		   "&nbsp;&nbsp;&nbsp;To add 3GPP2 VSA, Prepaid-Acct-Quota with sub-attributes 1,6 and 8;<br/>"+
		   "&nbsp;&nbsp;&nbsp;1. Select parameter name as 3GPP2-Prepaid-Acct-Quota from the dictionary<br/>"+
		   "&nbsp;&nbsp;&nbsp;2. Specify the value as:<br/>"+
		   "&nbsp;&nbsp;&nbsp;1=11;6=1000;8=3";
		   
	String [] paramNm = (String[])request.getAttribute("paramnames");	
	String [] paramvl = (String[])request.getAttribute("paramvalues");
	UpdateRadiusTestForm radiusTestForm=(UpdateRadiusTestForm)request.getAttribute("radiusTestData");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script> 
	var isValidName;
var mainArray = new Array();
var count = 0;
	
	function enableTextBox() {
		if(document.forms[0].requestType.value == 0) {
			document.forms[0].customRequestType.disabled = false;
		} else {
			document.forms[0].customRequestType.value = 0;
			document.forms[0].customRequestType.disabled = true;
		}
	}
	
  function validateCreate(){ 
  
     var adminHost=document.forms[0].adminHost.value;
     var hostIpAddress=document.forms[0].hostAddress.value;
     var validateAdminHost; 
     var validateHostAddr;
     
     validateAdminHost = validateIpAddress(adminHost);
     validateHostAddr = validateIpAddress(hostIpAddress);
     
	if(isNull(document.forms[0].name.value)){
		alert('Packet Name must be specified');
	}else if(!isValidName) {
		alert('Duplicate Name');
		document.forms[0].name.focus();
		return false;
	}else if(isNull(document.forms[0].adminHost.value)){
		alert('Server Host must be specified');
	}else if(isNull(document.forms[0].adminPort.value) || document.forms[0].adminPort.value==0){
		alert('Server Port must be specified');
	}else if(isNull(document.forms[0].scecretKey.value)){
		alert('Secret Key must be specified');
	}else if(isNull(document.forms[0].userName.value)){
        alert('User Name must be specified');
    }else if(isNull(document.forms[0].userPassword.value)){
		alert('Password must be specified');
	}else if(validateAdminHost == false){
	   alert("Radius Server Ip Address is not valid. Please enter valid data");
	}else if(validateHostAddr == false){
		   alert("Client Interface Ip Address is not valid. Please enter valid data");
	}else {
		var verifyPort=validatePortNumber(document.forms[0].adminPort.value);
		if(verifyPort==false){
			alert('Enter Valid Port');
			document.forms[0].adminPort.focus();
		}else{
    	document.forms[0].checkAction.value = 'Update';
		document.forms[0].submit();
	}
}
 }
  function validateIpAddress(ipaddress){
		var ip=/((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
			if(ip.test(ipaddress)){
				   return true;
			   }else{
				   return false;
			   }
	}
	
	function validatePortNumber(txt){
		// check for valid numeric port	 
		if(IsNumeric(txt) == true){
			if(txt >= 0 && txt<=65535){
				return(true);
			}else{
				return(false);
			}
		}else{
			return(false);
		}
	}
	
	function popUpDesc(desc) {
	
       	var strValue=desc;			
    	if(strValue != null){
        	var varWindow = window.open('<%=basePath%>/jsp/servermgr/server/NetDescriptionPopup.jsp?description='+strValue,'DescriptionWin','top=100, left=200, height=400, width=650, scrollbars=yes, status=1');
        	varWindow.focus();
        }else{
        	alert('Description does not exist');
        }
    }
	function popup(mylink, windowname)
	{
		if (! window.focus)return true;
			var href;
		if (typeof(mylink) == 'string')
			href=mylink;
		else
			href=mylink.href;
   					
		window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
		return false;
	}
	
   $(document).ready(function() {		
		
			$('#mappingtbl td img.delete').live('click',function() {
				 $(this).parent().parent().remove(); 
			});
   });


			
   function mypopup() {	
		$.fx.speeds._default = 1000;
		document.getElementById("popupfeildMapping").style.visibility = "visible";		
		$( "#popupfeildMapping" ).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 700,		
			buttons:{					
               'Add': function() {

						var name = $('#paramName').val();
						var name1 = $('#paramValue').val(); 
					
			      			          		
			      		if(isNull($('#paramName').val())){
			     			alert('Parameter Name must be specified.');
			     		}else{	
			     										
							var flag = true;												 	
							/* $(".paramnm").each(function(){
								if($(this).val() == name){
										alert("This attribute is already present.");
										flag = false;
									}
							}); */
														
			         		if(flag){
			         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name  +"</td><td class='tblfirstcol'>" + name1 + "</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			         			$('<input/>',{type:'hidden',id:name, value:name, name : "paranm" }).appendTo($("#mappingtbl tr:last").find("td:first"));	
			         			$('<input/>',{type:'hidden',id:name1, value:name1, name : "paramvl" }).appendTo($("#mappingtbl tr:last").find("td:eq(1)"));

			         			mainArray[count] = name;				          		
				          		count = count + 1;					          		
				          		$(this).dialog('close');
				         	}	         				    		         			   				         			          				          		
			         	}		         								
               },                
   		    Cancel: function() {
               	$(this).dialog('close');
           	}
	        },
       	open: function() {
           	
       	},
       	close: function() {			 
       	 $('#paramName').val(" ");
		 $('#paramValue').val(" "); 
       	}				
		});
		$( "#popupfeildMapping" ).dialog("open");		
	}
	       
   setTitle('UpdateRadiusTest');
   
   function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_TEST_PACKET%>',searchName,'update','<%=radiusTestForm.getNtradId()%>','verifyNameDiv');
	}
</script>

<html:form action="/updateRadiusPacket">
	<html:hidden property="checkAction" />
	<html:hidden property="itemIndex" />

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
									<td class="table-header" colspan="4">UPDATE RADIUS PACKET</td>
								</tr>

								<tr>
									<td colspan="3">

										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.packetname" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.packetName" 
																	header="radius.test.packetname"/>												
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text name="updateRadiusTestForm" property="name" onkeyup="verifyName();" styleId="name"
														size="30" tabindex="1" maxlength="30" style="width:206px" />&nbsp;<strong><font
														color="red">*</font></strong>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.serverport" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.RadiusServerPort" 
																	header="radius.test.serverport"/>	
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text property="adminHost" size="20" maxlength="60"
														tabindex="2" name="updateRadiusTestForm"
														style="width:206px" /> <html:text property="adminPort"
														size="5" maxlength="5" tabindex="3"
														name="updateRadiusTestForm" style="width:50px" /> &nbsp;<strong><font
														color="red">*</font></strong>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.replaytimeout" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.ReplayTimeout" 
																	header="radius.test.replaytimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text property="reTimeOut" tabindex="4" size="12"
														maxlength="15" name="updateRadiusTestForm"
														style="width:206px" />&nbsp; Retries: <html:text
														property="retries" tabindex="5" size="5" maxlength="5"
														name="updateRadiusTestForm" style="width:50px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.secretkey" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.RadiusSecretKey" 
																	header="radius.test.secretkey"/>												
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text property="scecretKey" tabindex="6" size="30"
														maxlength="30" name="updateRadiusTestForm"
														style="width:206px" /> &nbsp;<strong><font
														color="red">*</font></strong>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.username" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.UserName" 
																	header="radius.test.username"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text property="userName" size="30" tabindex="7"
														maxlength="255" name="updateRadiusTestForm"
														style="width:206px" /> &nbsp;<strong><font
														color="red">*</font></strong>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.password" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.Password" 
																	header="radius.test.password"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:password property="userPassword" tabindex="8"
														size="30" maxlength="255" name="updateRadiusTestForm"
														style="width:206px" />&nbsp; &nbsp;<strong><font
														color="red">*</font></strong> <html:checkbox property="isChap"
														tabindex="9" value="1" name="updateRadiusTestForm"></html:checkbox>CHAP
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.requesttype" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.RequestType" 
																	header="radius.test.requesttype"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:select property="requestType"
														name="updateRadiusTestForm" tabindex="10"
														onchange="enableTextBox();" style="width:206">
														<html:option value="1">Authentication Request</html:option>
														<html:option value="2">Accounting Start</html:option>
														<html:option value="3">Accounting Stop</html:option>
														<html:option value="4">Accounting Update</html:option>
														<html:option value="5">Accounting On</html:option>
														<html:option value="6">Accounting Off</html:option>
														<html:option value="7">Status Server</html:option>
														<html:option value="0">User Define</html:option>
													</html:select> <html:text property="customRequestType" tabindex="11"
														name="updateRadiusTestForm" size="5" disabled="true"
														style="width:50px"></html:text> <script>
																	   			if(document.forms[0].requestType.value == 0) {
																					document.forms[0].customRequestType.disabled = false;
																				}	
																	   </script>
												</td>
											</tr>



											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="radiusResources" 
														key="radius.test.clientport" />
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.ClientIpPort" 
																	header="radius.test.clientport"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text tabindex="12" property="hostAddress" size="20"
														maxlength="60" name="updateRadiusTestForm"
														style="width:206px" /> <html:text tabindex="13"
														property="hostPort" size="5" maxlength="5"
														name="updateRadiusTestForm" style="width:50px" /> &nbsp;
												</td>

											</tr>






											<tr>
												<td align="left" class="labeltext" colspan="3" valign="top">
													&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" colspan="3" valign="top"><hr /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													colspan="3"><logic:notEqual
														name="updateRadiusTestForm" property="checkAction"
														value="EditParam">
														<input type="button" tabindex="14" onclick="mypopup()"
															value=" Add " class="light-btn">
														
													</logic:notEqual> <logic:equal name="updateRadiusTestForm"
														property="checkAction" value="EditParam">
														<input type="button" tabindex="15" name="update"
															value="Update" onclick="updateParam()" />
													</logic:equal></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" colspan="3"
													valign="top">
													<table width="100%" id="mappingtbl" cellpadding="0"
														cellspacing="0">
														<tr>
															<td align="left" class="tblheader" valign="top"
																width="40%">Parameter Name</td>
															<td align="left" class="tblheader" valign="top"
																width="40%">Parameter Value</td>
															<td align="left" class="tblheader" valign="top"
																width="20%">Remove</td>
															<td>&nbsp;
															<td>
														</tr>
															
															<logic:iterate id="radParamRel" name="updateRadiusTestForm" property="radParamRel" type="RadiusTestParamData" >
																		<tr>
								  												<td align="left" class="tblfirstcol"><bean:write name="radParamRel" property="name"/> <input type=hidden name="paranm" class="paramnm" value='<bean:write name="radParamRel" property="name"/>' /></td>
								  												<td align="left" class="tblfirstcol"><bean:write name="radParamRel" property="value"/> <input type=hidden name="paramvl"  value='<bean:write name="radParamRel" property="value"/>' /></td>
								  												<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td>
								  										</tr>
								  							</logic:iterate>							
	
													</table>
												</td>
											<tr>
												<td align="left" class="labeltext" colspan="3" valign="top">
													&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" tabindex="16" onclick="validateCreate()"
													value="Update" class="light-btn"> <input
													type="reset" tabindex="17" name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initListRadiusPacket.do?/>'"
													value="Cancel" class="light-btn" tabindex="18"></td>
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
	<div id="popupfeildMapping" style="display: none;"
		title="Parameter Mapping">
		<table>
			<tr>
				<td align="left" class="labeltext" valign="top" width="30%">Parameter
					Name</td>
				<td align="left" class="labeltext" valign="top" colspan="2"><html:text
						property="paramName" styleId="paramName" style="width:250px"
						tabindex="19" /> &nbsp;&nbsp;&nbsp;&nbsp;</td>
			</tr>
			<tr>
				<td align="left" class="labeltext" valign="top" width="30%">Parameter
					Value</td>
				<td align="left" class="labeltext" valign="top" colspan="2"><html:textarea
						property="paramValue" styleId="paramValue" tabindex="20" rows="3"
						cols="38" style="width:250px" /> &nbsp;&nbsp;&nbsp;&nbsp; 
						<ec:elitehelp headerBundle="radiusResources" 
							text="radius.test.add.parametername"
								header="radius.test.add.parameterdesc" />
				</td>
			</tr>
		</table>
	</div>


</html:form>