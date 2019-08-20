<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.aaa.core.util.constant.CommonConstants"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>
<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions"%>
<%@page import="com.elitecore.diameterapi.core.translator.TranslatorConstants" %>

<%
		DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)request.getAttribute("diameterRoutingConfForm");
		DiameterRoutingConfData diameterRoutingConfData = (DiameterRoutingConfData)request.getAttribute("diameterRoutingConfData");
%>

<script>
	
	$(document).ready(function(){
		setFailureActionOptions();
		$("#translationType").change(function(){
			$("#configId").val($(this).val());
		});
		
		var subscriberModeval = '<%=diameterRoutingConfData.getSubsciberMode()%>';
		
		if(subscriberModeval != 'null'){
			if(subscriberModeval == '<%=CommonConstants.IMSI_MSISDN%>'){
				
				$("#subscriberRouting2 option").each(function(){
					if($(this).attr('class') == 'subscriber2-msisdn'){
						$('.subscriber2-msisdn').each(function(){
							$(this).show();
						});
					}
				});
				
				$('.subscriber2-imsi').each(function(){
					$(this).hide();
				});
				$('#subscriberMode').val('<%=CommonConstants.IMSI_MSISDN%>');
				
			}else if(subscriberModeval == '<%=CommonConstants.MSISDN_IMSI%>'){
				
				$("#subscriberRouting2 option").each(function(){
					if($(this).attr('class') == 'subscriber2-imsi'){
						$('.subscriber2-imsi').each(function(){
							$(this).show();
						});
					}
				});
				
				$('.subscriber2-msisdn').each(function(){
					$(this).hide();
				});
				$('#subscriberMode').val('<%=CommonConstants.MSISDN_IMSI%>');
			}
		}else{
			$('#subscriberMode').val('');
			$('#subscriberRouting1').val('');
			$('#subscriberRouting2').val('');
		}
	});
	
	var isValidName;
	
	function customValidate(form)
	{
		if(validateDiameterRoutingConfForm(form))
	    {
			if(!isValidName) {
				alert('Enter Valid Name.');
				document.forms[0].name.focus();
				return false;
			}
			if(!isValidFailurErrorCode()){
				return false;
			}
			return true; 
	    }
		else
		{
			return false;
		}	
	}
	
	function verifyName() {
 		var searchName = document.getElementById("name").value;
 		isValidName = verifyInstanceName('<%=InstanceTypeConstants.ROUTING_CONFIG%>',searchName,'update','<%=diameterRoutingConfData.getRoutingConfigId()%>','verifyNameDiv');
 	}
	
	var failureActions ="";
	function addFailureActionRow() {
		var imgpath ="<%=basePath%>"+"/images/minus.jpg";
		$("#failureActionTable tr:last").after("<tr>"+
				"<td class='tblfirstcol'><input type='text'  name='errorCode' style='width: 100%;'></td>"+
				"<td class='tblrows'><select style='width: 100%;'  name='failureAction' >"+failureActions+"</select></td>"+
				"<td class='tblrows'> <input  name='failureArgument' type='text'  style='width: 100%;'  value='<%=diameterRoutingConfForm.getDefaultFailureArgument()%>'/> </td>"+
				"<td class='tblrows'><img src='"+imgpath+"' onclick='javascript:$(this).parent().parent().remove();' height='15'/></td>"+
				"</tr>");
	}
	
	function setFailureActionOptions(){
		<logic:iterate id="obj" property='failureActionMap' name='diameterRoutingConfForm' >
			var key = "<bean:write property='key' name='obj'/>" ;
			var value = "<bean:write property='value' name='obj'/>";
			var selected ="";
			<logic:equal value='<%=diameterRoutingConfForm.getDefaultFailureAction()%>' property='key' name='obj'>
				selected = "selected";
			</logic:equal>
			failureActions += "<option value="+key+"  "+selected+">"+value+" </option>";
		</logic:iterate>
	}

	function isValidFailurErrorCode(){
		var isValid = true;
		$("input[name='errorCode']").each(function(){
			var tmpErrorCode = $(this).val();
			$(this).val($.trim(tmpErrorCode));// set trim value
			if(!isValidErrorCode($(this).val())){
				alert("Error Code must be comma or semicolon seperated numeric value.");
				$(this).focus();
				isValid = false;
				return false;
			}
		});
		return isValid;
	}

	function isValidErrorCode(str){
		var regulrExpression = /[^\d;,]+/g ;
		var isValid = true;
		var matches = (str.match(regulrExpression));
		//alert("str="+str+"\tmatch="+matches);
		return matches == null;
	}
	function changeSubscriberRouting(){
		var selectedClass =  $('#subscriberRouting1').find('option:selected').attr('class');
		
		$('#subscriberRouting2').val('0');
		
		if(selectedClass == 'imsi'){
			
			$("#subscriberRouting2 option").each(function(){
				if($(this).attr('class') == 'subscriber2-msisdn'){
					$('.subscriber2-msisdn').each(function(){
						$(this).show();
					});
				}
			});
			
			
			$('.subscriber2-imsi').each(function(){
				$(this).hide();
			});
			
			$('#subscriberMode').val('<%=CommonConstants.IMSI_MSISDN%>');
			
		}else if(selectedClass == 'msisdn'){
			
			$("#subscriberRouting2 option").each(function(){
				if($(this).attr('class') == 'subscriber2-imsi'){
					$('.subscriber2-imsi').each(function(){
						$(this).show();
					});
				}
			});
			
			$('.subscriber2-msisdn').each(function(){
				$(this).hide();
			});
			
			$('#subscriberMode').val('<%=CommonConstants.MSISDN_IMSI%>');
		}else{
			
			$('.subscriber2-imsi').each(function(){
				$(this).hide();
			});
			
			$('.subscriber2-msisdn').each(function(){
				$(this).hide();
			});
			
			$('#subscriberMode').val('');
		}
	}
 </script>
<html:javascript formName="diameterRoutingConfForm" />
<html:form action="/updateDiameterRoutingConfig" onsubmit="return customValidate(this);">
	<html:hidden name="diameterRoutingConfForm" styleId="routingConfigId" property="routingConfigId" />
	<html:hidden name="diameterRoutingConfForm" styleId="auditUId" property="auditUId" />
	<html:hidden name="diameterRoutingConfForm" property="subscriberMode" styleId="subscriberMode" />	
	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
		<tr>
			<td class="box" valign="middle" colspan="5" class="table-header">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources" key="routingconf.routingaction.update" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="diameterResources" key="general.name" /> 
							<ec:elitehelp headerBundle="diameterResources" text="general.name" header="general.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="80%">
							<html:text styleId="name" property="name" tabindex="1" onkeyup="verifyName();" size="25" maxlength="50" style="width:250px" /> 
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="general.description" /> 
							<ec:elitehelp headerBundle="diameterResources" text="general.description" header="general.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:textarea styleId="description" tabindex="2" property="description" cols="40" rows="2" style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
							<bean:message bundle="diameterResources" key="routingconf.tablename" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.tablename" header="routingconf.tablename" />
						</td>
						<td align="left" class="labeltext" valign="top">
							<bean:define id="routingTablesList" name="diameterRoutingConfForm" property="diameterRoutingTablesList"></bean:define> 
							<html:select name="diameterRoutingConfForm" tabindex="3" styleId="routingTableId" property="routingTableId" size="1" style="width: 250px;">
								<html:option value=''>--Select--</html:option>
								<html:options collection="routingTablesList" property="routingTableId" labelProperty="routingTableName" />
							</html:select> 
							<font color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.realmname" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.realmname" header="routingconf.realmname" example="routingconf.realmname.example"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="realmName" tabindex="4" property="realmName" size="25" style="width:250px" /> 
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.appids" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.appids" header="routingconf.appids" example="routingconf.appids.example"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="appIds" property="appIds" tabindex="5" size="25" maxlength="50" style="width:250px" /> 
							<font color="#FF0000">*</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.originhost" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.originhost" header="routingconf.originhost"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="originHost" tabindex="6" property="originHost" size="25" maxlength="50" style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.originrealm" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.originrealm" header="routingconf.originrealm"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="originRealm" tabindex="7" property="originRealm" size="25" maxlength="50" style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.ruleset" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.ruleset" header="routingconf.ruleset" example="routingconf.ruleset.example"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:textarea styleId="ruleset" tabindex="8" name="diameterRoutingConfForm" property="ruleset" cols="50" rows="2" style="width:250px" />
						</td>
					</tr>


					<tr>
						<td align="left" class="captiontext" valign="top" colspan="1"
							width="25%"><bean:message bundle="diameterResources"
								key="routingconf.transmapconf" /> 
								<ec:elitehelp headerBundle="diameterResources" text="routingconf.transmapconf" header="routingconf.transmapconf"/>
						</td>
						<td align="left" class="labeltext" valign="top">
						<html:select name="diameterRoutingConfForm" styleId="translationType" property="configId" tabindex="3" style="width:250px">
								<html:option value="">--select--</html:option>
								<optgroup label="Translation Mapping"
									class="labeltext">
									<logic:iterate id="translationMapping"
										name="diameterRoutingConfForm"
										property="translationMappingConfDataList"
										type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
										<html:option value="<%=ConfigConstant.TRANSLATION_MAPPING + translationMapping.getTranslationMapConfigId()%>" 
										styleClass="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></html:option>
										
									</logic:iterate>
								</optgroup>
								<optgroup label="Copy Packet Mapping" class="labeltext">
									<logic:iterate id="copyPacketMapping" name="diameterRoutingConfForm" property="copyPacketMappingConfDataList"
										type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
										<html:option value="<%=ConfigConstant.COPY_PACKET_MAPPING + copyPacketMapping.getCopyPacketTransConfId()%>"
											styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></html:option>
									</logic:iterate>
								</optgroup>
							</html:select> 
							
							<html:hidden property="configId" styleId="configId" style="configId" /> 
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.routingaction" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.routingaction" header="routingconf.routingaction"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							
							<html:select name="diameterRoutingConfForm" property="routingAction" style="width:250px" tabindex="10" value="<%=String.valueOf(diameterRoutingConfForm.getRoutingAction())%>">
								<logic:iterate id="routingActionInst"  collection="<%=RoutingActions.values() %>">
										<%String displayText=((RoutingActions)routingActionInst).routingActionStr;%>
										<html:option value="<%=String.valueOf(((RoutingActions)routingActionInst).routingAction)%>"><%=displayText%></html:option>
								 </logic:iterate>
							</html:select>
							
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.statefulrouting" />
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.statefulrouting" header="routingconf.statefulrouting"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:select property="statefulRouting" styleId="statefulRouting" style="width:250px" tabindex="11">
								<html:option value="1">Enabled </html:option>
								<html:option value="0">Disabled </html:option>
							</html:select>
						</td>
					</tr>

 					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources" key="routingconf.attachedredirection" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.attachedrouting" header="routingconf.attachedredirection"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:select property="attachedRedirection" styleId="attachedRedirection" style="width:250px" tabindex="11">
								<html:option value="true">Enabled</html:option>
								<html:option value="false">Disabled</html:option>
							</html:select>
						</td>
					</tr> 

					<tr>
						<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
							<bean:message bundle="diameterResources" key="routingconf.transactiontimeout" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.transactiontimeout" header="routingconf.transactiontimeout"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text styleId="transactionTimeout" tabindex="12" property="transactionTimeout" size="25" maxlength="256" style="width:250px" /> 
							<font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
							<bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing1" /> 
							<ec:elitehelp headerBundle="diameterResources" text="diameterrouting.subscriber.routing1" header="diameterrouting.subscriber.routing1"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<bean:define id="imsiBasedRoutingTableDataList" name="diameterRoutingConfForm" property="imsiBasedRoutingTableDataList"></bean:define> 
							<bean:define id="msisdnBasedRoutingTableDataList" name="diameterRoutingConfForm" property="msisdnBasedRoutingTableDataList"></bean:define> 
					
							<html:select name="diameterRoutingConfForm" tabindex="3" styleId="subscriberRouting1" property="subscriberRouting1" size="1" style="width: 250px;" onchange="changeSubscriberRouting();">
								<html:option value='' styleClass="selectClass">--Select--</html:option>
								<optgroup label="IMSI Based Routing Table" >
									<html:options collection="imsiBasedRoutingTableDataList" property="routingTableName" labelProperty="routingTableName" styleClass="imsi"/>
								</optgroup>
								<optgroup label="MSISDN Based Routing Table">
									<html:options collection="msisdnBasedRoutingTableDataList" property="routingTableName" labelProperty="routingTableName" styleClass="msisdn"/>
								</optgroup>
							</html:select> 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
							<bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing2" /> 
							<ec:elitehelp headerBundle="diameterResources" text="diameterrouting.subscriber.routing2" header="diameterrouting.subscriber.routing2"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:select name="diameterRoutingConfForm" tabindex="3" styleId="subscriberRouting2" property="subscriberRouting2" size="1" style="width: 250px;">
								<html:option value='' styleClass="selectClass">--Select--</html:option>
								<optgroup label="IMSI Based Routing Table" class="subscriber2-imsi" style="display: none;">
									<html:options collection="imsiBasedRoutingTableDataList" property="routingTableName" labelProperty="routingTableName" styleClass="subscriber2-imsi" style="display: none;"/>
								</optgroup>
								<optgroup label="MSISDN Based Routing Table" class="subscriber2-msisdn" style="display: none;">
									<html:options collection="msisdnBasedRoutingTableDataList" property="routingTableName" labelProperty="routingTableName" styleClass="subscriber2-msisdn" style="display: none;"/>
								</optgroup>
							</html:select> 
						</td> 
					</tr>
					<tr>
						<td colspan="2"></td>
					</tr>
					<tr>
						<td colspan="100%">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td align="left" class="tblheader-bold" valign="top" width="100%" colspan="100%">
										<bean:message bundle="diameterResources" key="routingconf.failureactions" />
										<ec:elitehelp headerBundle="diameterResources" text="routingconf.failureactions" 
										header="routingconf.failureactions" example="routingconf.failureactions.example"/>
									</td>
								</tr>
								<tr>
									<td>
										<table width="100%" id="failureActionTable" cellpadding="0" cellspacing="0">
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="captiontext" class="table-header">
													<input type="button" tabindex="13" class="light-btn" value="Add Failure Action" onclick="addFailureActionRow()" />
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr class="captiontext">
												<td class="tblheader-bold" width="25%">Error Code</td>
												<td class="tblheader-bold" width="25%">Failure Action</td>
												<td class="tblheader-bold" width="25%">Failure Argument</td>
												<td class="tblheader-bold" width="6%">Remove</td>
											</tr>

											<logic:iterate id="diameterRoutingConfigFailureParam" name="diameterRoutingConfData" property="diameterRoutingConfigFailureParamSet" type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfigFailureParam">
												<tr>
													<td class='tblfirstcol' width="25%">
														<input type="text" name="errorCode" tabindex="14" value='<bean:write name="diameterRoutingConfigFailureParam" property="errorCodes" />' style="width: 100%";>
													</td>
													<td class="tblrows" width="25%">
														<select name="failureAction" style="width: 100%" tabindex="15">
															<logic:iterate id="obj" property='failureActionMap' name='diameterRoutingConfForm'>
																<option value="<bean:write name='obj' property='key'/>"
																	<logic:equal value='<%=diameterRoutingConfigFailureParam.getFailureAction().toString()%>' property='key' name='obj'>
	 																	selected
	 																</logic:equal>>
																	<bean:write name='obj' property='value' />
																</option>
															</logic:iterate>
													</select></td>
													<td class='tblrows' width="25%">
														<input type="text" tabindex="16" name="failureArgument" value='<bean:write name="diameterRoutingConfigFailureParam" property="failureArgument" />' style="width: 100%"></td>
													<td class='tblrows' width="6%">
														<img src="<%=basePath%>/images/minus.jpg" onclick='javascript:$(this).parent().parent().remove();' height='15' />
													</td>
												</tr>
											</logic:iterate>
										</table>
									</td>
								</tr>
								<%-- <tr>
											<td align="left" class="labeltext" valign="top" width="25%">
											   <bean:message bundle="diameterResources" key="routingconf.protocolfailure" />	
											</td>
											<td align="left" class="labeltext" valign="top" width="15%">
												<html:select name="diameterRoutingConfForm" styleId="protocolFailureAction" property="protocolFailureAction" >
													<html:option value="1" ><bean:message bundle="diameterResources" key="routingconf.drop"/></html:option>
													<html:option value="2" ><bean:message bundle="diameterResources" key="routingconf.failover"/></html:option>												
													<html:option value="3" ><bean:message bundle="diameterResources" key="routingconf.redirect"/></html:option>
													<html:option value="4" ><bean:message bundle="diameterResources" key="routingconf.passthrough"/></html:option>
												</html:select>
											</td>
											<td align="left" class="labeltext" valign="top" >
												<html:text styleId="protocolFailureArguments" property="protocolFailureArguments" size="25" maxlength="256" style="width:250px"/>	
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top">
											   <bean:message bundle="diameterResources" key="routingconf.transientfailure" />	
											</td>
											<td align="left" class="labeltext" valign="top">
												<html:select name="diameterRoutingConfForm" styleId="transientFailureAction" property="transientFailureAction" >
													<html:option value="1" ><bean:message bundle="diameterResources" key="routingconf.drop"/></html:option>
													<html:option value="2" ><bean:message bundle="diameterResources" key="routingconf.failover"/></html:option>												
													<html:option value="3" ><bean:message bundle="diameterResources" key="routingconf.redirect"/></html:option>
													<html:option value="4" ><bean:message bundle="diameterResources" key="routingconf.passthrough"/></html:option>
												</html:select>
											</td>
											<td align="left" class="labeltext" valign="top">
												<html:text styleId="transientFailureArguments" property="transientFailureArguments" size="25" maxlength="256" style="width:250px"/>	
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top">
											   <bean:message bundle="diameterResources" key="routingconf.permanentfailure" />	
											</td>
											<td align="left" class="labeltext" valign="top">
												<html:select name="diameterRoutingConfForm" styleId="permanentFailureAction" property="permanentFailureAction" >
													<html:option value="1" ><bean:message bundle="diameterResources" key="routingconf.drop"/></html:option>
													<html:option value="2" ><bean:message bundle="diameterResources" key="routingconf.failover"/></html:option>												
													<html:option value="3" ><bean:message bundle="diameterResources" key="routingconf.redirect"/></html:option>
													<html:option value="4" ><bean:message bundle="diameterResources" key="routingconf.passthrough"/></html:option>
												</html:select>
											</td>
											<td align="left" class="labeltext" valign="top">
												<html:text styleId="permanentFailureArguments" property="permanentFailureArguments" size="25" maxlength="256" style="width:250px"/>	
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top">
											   <bean:message bundle="diameterResources" key="routingconf.timeoutaction" />	
											</td>
											<td align="left" class="labeltext" valign="top">
												<html:select name="diameterRoutingConfForm" styleId="timeOutAction" property="timeOutAction" >
													<html:option value="1" ><bean:message bundle="diameterResources" key="routingconf.drop"/></html:option>
													<html:option value="2" ><bean:message bundle="diameterResources" key="routingconf.failover"/></html:option>												
													<html:option value="3" ><bean:message bundle="diameterResources" key="routingconf.redirect"/></html:option>
													<html:option value="4" ><bean:message bundle="diameterResources" key="routingconf.passthrough"/></html:option>
												</html:select>
											</td>
											<td align="left" class="labeltext" valign="top">
												<html:text styleId="timeOutArguments" property="timeOutArguments" size="25" maxlength="256" style="width:250px"/>	
											</td>
										</tr> --%>
							</table>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2">
							<input type="submit" name="c_btnCreate" id="c_btnCreate2" value="Update" class="light-btn" tabindex="17"> 
							<input type="reset" name="c_btnDeletePolicy" tabindex="18" onclick="javascript:location.href='<%=basePath%>/initSearchDiameterRoutingConfig.do?routingConfigId=<%=diameterRoutingConfForm.getRoutingConfigId()%>'" value="Cancel" class="light-btn">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>