<%@page import="java.util.ArrayList"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>


<%
    String basePath = request.getContextPath(); 
  	List radParamList = (List)request.getSession().getAttribute("radParamList");  
  	List<String> lstNewString=new ArrayList<String>();
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
    	document.forms[0].checkAction.value = 'Create';
		document.forms[0].submit();
	}
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
   					
   				//alert(mylink)
				window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
				
				return false;
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
			     			var i = 0;							
							var flag = true;												 	
							/* if(document.getElementById('mappingtable').getElementsByTagName('tr').length >= 2){								
								for(i=0;i<mainArray.length;i++){									
									var value = mainArray[i];																		
									if(value == name){
										alert("This attribute is already present.");
										flag = false;
										break;
									}
								}
							}	 */							
			         		if(flag){
			         			
			         			$("#mappingtable tr:last").after("<tr><td class='tblfirstcol'>" + name  +"</td><td class='tblfirstcol'>" + name1 + "</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			         			$('<input/>',{type:'hidden',id:name, value:name, name : "paramnm" }).appendTo($("#mappingtable tr:last").find("td:first"));
			         			$('<input/>',{type:'hidden',id:name1, value:name1, name : "paramval" }).appendTo($("#mappingtable tr:last").find("td:eq(1)"));

			         			$('#mappingtable td img.delete').live('click',function() {
			       				 var removalVal = $(this).closest('tr').find('td:eq(0)').text();					 	
			       				for(var d=0;d<count;d++){
			       					var currentVal = mainArray[d];					
			       					if(currentVal == removalVal){
			       						mainArray[d] = '  ';
			       						break;
			       					}
			       				}								
			       				 $(this).parent().parent().remove(); });	
			       				 		         						         			          		
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
	setTitle('RadiusTest');
	
	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_TEST_PACKET%>',searchName,'create','','verifyNameDiv');
	}
</script>

<html:form action="/createRadiusPacket">
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
									<td class="table-header">CREATE RADIUS PACKET</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
									<input type="hidden" name="paramval1" id="paramval1" val=""/>
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
													<html:text property="name" size="30" maxlength="30" onkeyup="verifyName();" styleId="name"
														tabindex="1" style="width:250px"/>&nbsp;<strong><font
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
														style="width:206px" tabindex="2" /> <html:text
														property="adminPort" size="5" maxlength="5"
														style="width:50px" tabindex="3" /> &nbsp;<strong><font
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
													<html:text property="reTimeOut" size="12" maxlength="15"
														style="width:206px" tabindex="4" />&nbsp; Retries: <html:text
														property="retries" size="5" maxlength="5"
														style="width:50px" tabindex="5" />
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
													<html:text property="scecretKey" size="30" maxlength="30"
														style="width:206px" tabindex="6" /> &nbsp;<strong><font
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
													<html:text property="userName" size="30" maxlength="255"
														style="width:206px" tabindex="7" /> &nbsp;<strong><font
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
													<html:password property="userPassword" size="30"
														maxlength="255" style="width:206px" tabindex="8" />&nbsp;
													&nbsp;<strong><font color="red">*</font></strong> <html:checkbox
														property="isChap" value="1" tabindex="9"></html:checkbox>CHAP
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
														onchange="enableTextBox();" style="width:206"
														tabindex="10">
														<html:option value="1">Authentication Request</html:option>
														<html:option value="2">Accounting Start</html:option>
														<html:option value="3">Accounting Stop</html:option>
														<html:option value="4">Accounting Update</html:option>
														<html:option value="5">Accounting On</html:option>
														<html:option value="6">Accounting Off</html:option>
														<html:option value="7">Status Server</html:option>
														<html:option value="0">User Define</html:option>
													</html:select> <html:text property="customRequestType" size="5" tabindex="11"
														disabled="true" style="width:50px"></html:text> <script>
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
													<html:text property="hostAddress" size="20" maxlength="60"
														style="width:206px" tabindex="12" /> <html:text
														property="hostPort" size="5" maxlength="5"
														style="width:50px" tabindex="13" /> &nbsp;
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
												<td align="left" class="captiontext" valign="top" colspan="3">
													<input type="button" onclick="mypopup()"
														value=" Add " class="light-btn" tabindex="14"> 
												</td>
											</tr>
											<tr>
												<td class="captiontext" colspan="3" valign="top">
													<table width="90%" id="mappingtable" cellpadding="0"
														cellspacing="0">
														<tr>
															<td align="left" class="tblheader" valign="top"
																width="40%">Parameter Name</td>
															<td align="left" class="tblheader" valign="top"
																width="40%">Parameter Value</td>
															<td align="left" class="tblheader" valign="top"
																width="10%">Remove</td>
														</tr>
														<%	int index = 0; %>
														<logic:iterate id="objRadParamData" name="radParamList"
															type="RadiusTestParamData">
															<tr>
																<td align="left" class="tblfirstcol"><%=(index+1)%></td>
																<td align="left" class="tblrows"><bean:write
																		name="objRadParamData" property="name" /></td>
																<td align="left" class="tblrows"><bean:write
																		name="objRadParamData" property="value" /></td>
																<td align="left" class="tblrows"><img
																	src="<%=basePath%>/images/minus.jpg"
																	onclick="submitRemove('<%=index%>')" border="0" tabindex="15/></td>
															</tr>
															<% index++; %>
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
													type="button" onclick="validateCreate()" value="Create"
													class="light-btn" tabindex="16"> <input
													type="reset" name="c_btnDeletePolicy" tabindex="17"
													onclick="javascript:location.href='<%=basePath%>/initListRadiusPacket.do?/>'"
													value="Cancel" class="light-btn"></td>
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
						tabindex="17" /> &nbsp;&nbsp;&nbsp;&nbsp;</td>
			</tr>
			<tr>
				<td align="left" class="labeltext" valign="top" width="30%">Parameter
					Value</td>
				<td align="left" class="labeltext" valign="top" colspan="2"><html:textarea
					property="paramValue" styleId="paramValue" rows="3" cols="38"
					style="width:250px" tabindex="18" /> &nbsp;&nbsp;&nbsp;&nbsp; 
					<ec:elitehelp headerBundle="radiusResources" 
					text="radius.test.add.parametername" header="radius.test.add.parameterdesc" />
				</td>
			</tr>
		</table>
	</div>

</html:form>


