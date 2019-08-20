<%@page import="com.elitecore.aaa.core.util.constant.CommonConstants"%>
<%@page import="com.elitecore.diameterapi.core.translator.TranslatorConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.diameterapi.core.translator.TranslatorConstants" %>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData"%>
<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions"%>

<%
	String basePath = request.getContextPath();
	DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)request.getAttribute("diameterRoutingConfForm");	
	List<DiameterPeerData> diameterPeerDataList = diameterRoutingConfForm.getDiameterPeersList();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script> 
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
		add();
		return true; 
    }
	else
	{
		return false;
	}	
}
function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val)){
		return false;
	}
	return true;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.ROUTING_CONFIG%>',searchName,'create','','verifyNameDiv');
}

$(document).ready(function(){
	setPeerGroup();
	$("#translationType").change(function(){
		$("#configId").val($(this).val());
	});
});

var peerConfigOptions;
function setPeerGroup(){
	peerConfigOptions = "<option value=0>--Select--</option>";
	<%for (int i=0;i<diameterPeerDataList.size();i++){ 
		DiameterPeerData diameterPeerData = diameterPeerDataList.get(i);
	%>
		peerConfigOptions = peerConfigOptions+"<option value='<%=diameterPeerData.getPeerUUID()%>'><%=diameterPeerData.getName()%>(<%=(diameterPeerData.getRemoteAddress() != null) ? diameterPeerData.getRemoteAddress() : "0.0.0.0"%>)</option>";
	<%}%>
}

function addPeer(){
	$("#peerTable tr:last").after("<tr>"+
									"<td class='tblfirstcol'><select style='width: 100%;' id='peerConfig' name='peerConfigList'>"+peerConfigOptions+"</select></td>"+
									"<td class='tblrows'> <input id='loadFactor' name='loadFectorList' type='text' value='1' style='width: 100%;' /> </td>"+
									"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15'/></td>"+
									"</tr>");
}

var peerGroupCount=0;
function popupPeerGroup() {
	
	$.fx.speeds._default = 1000;
	document.getElementById("peerGroupDiv").style.visibility = "visible";		
	$( "#peerGroupDiv" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 300,
		width: 600,		
		buttons:{					
            'Add': function() {
				peerGroupCount = peerGroupCount+1;
				console.log('Peer Count');
				console.log(peerGroupCount);
				var peerGroupRuleSet = $('#peerGroupRuleSet').val();
				var peerConfigSet = document.getElementsByName('peerConfigList');
				var loadFectorSet = document.getElementsByName('loadFectorList');

				if(peerGroupRuleSet == "" || peerGroupRuleSet == null){
					alert("Rule Set must be specified.");
					return false;
				}else if(document.getElementById('peerTable').getElementsByTagName('tr').length < 2){
					alert("Atleast one peer must be specified.");
					return false;
				}
								
				var peerTable = "<table id='peers' cellpading=0 cellspacing=0 border=0 width='100%'>"+
									"<tr><td class='tblheader' valign='top' width='75%'>Peer</td><td class='tblheader' valign='top' width='19%'>Load Factor</td><td class='tblheader' valign='top' width='6%'>Remove</td></tr>";									

				var peerElementName='peer'+peerGroupCount;
				var loadFactorElementName='loadFactor'+peerGroupCount;	

				var peerValueArr = new Array();
				var loadFactorArr = new Array();
				var count = 0;					
				var loadFactorCount = 0;			
				for(var k=0;k<peerConfigSet.length;k++){		
					var ddl = peerConfigSet[k];
					var peerText = ddl.options[ddl.selectedIndex].text;
					var peerValue = ddl.options[ddl.selectedIndex].value;
					for(var searchIndex=0; searchIndex<peerValueArr.length; searchIndex++){						
							if(peerValueArr[searchIndex] == peerText){
								alert("Duplicate Entries in Peers for '"+peerText+"'");
								return;
							}
					}
					if(peerValue == '0'){
						alert("Select at least one peer at position: " + k);
						return;
					}
					peerValueArr[count++] = peerText;					
					var loadFactor = $(loadFectorSet[k]).val();
					loadFactorArr[loadFactorCount++] = loadFactor;
					peerTable = peerTable +
									"<tr><td class='tblfirstcol' valign='top'>" + peerText + "<input type='hidden' name='"+peerElementName+"' value='"+peerValue+"'></td><td class='tblrows' valign='top'>" + loadFactor + "<input type='hidden' name='"+loadFactorElementName+"' value='"+loadFactor+"'></td><td class='tblrows' valign='top'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td></tr>";																
				}
				
				peerTable = peerTable + "</table>";
				var peerGroupRuleSetName = "peerGroupRuleSet"+peerGroupCount;								
				var strTR = "<tr><td class='tblfirstcol' valign='top'><input type='hidden' name='"+peerGroupRuleSetName+"' value='"+peerGroupRuleSet+"'   /> "+peerGroupRuleSet+"</td>"+
								 "<td class='tblrows' valign='top'>"+peerTable+"</td>"+
								 "<td class='tblrows' valign='top'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
							"</tr>"; 
				$(strTR).appendTo('#peerGroupTable');								
				$(this).dialog('close');														      								
            },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
        	clearPeerTable();
    	},
    	close: function() {
    		
    	}				
	});
	$( "#peerGroupDiv" ).dialog("open");
	
}

function clearPeerTable(){
	document.getElementById("peerGroupRuleSet").value = '';
    for(var i = document.getElementById("peerTable").rows.length-1; i > 0 ;i--){
    	document.getElementById("peerTable").deleteRow(i);
    }
}


function add(){
	$("#mainform").append("<input type='hidden' id='testPeerGroupCount' name='peerGroupCount' value='"+peerGroupCount+"' />");
}
var failureActions ="";
function addFailureActionRow() {
	var imgpath ="<%=basePath%>"+"/images/minus.jpg";
	$("#failureActionTable tr:last").after("<tr>"+
			"<td class='tblfirstcol'><input type='text'  name='errorCode' style='width: 100%;'></td>"+
			"<td class='tblrows'><select style='width: 100%;'  name='failureAction' >"+failureActions+"</select></td>"+
			"<td class='tblrows'> <input  name='failureArgument' type='text'  style='width: 100%;'  value='<%=diameterRoutingConfForm.getDefaultFailureArgument()%>'/> </td>"+
			"<td class='tblrows' align='center'><img src='"+imgpath+"' onclick='javascript:$(this).parent().parent().remove();' height='15'/></td>"+
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

setFailureActionOptions();

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
	
	$('#subscriberRouting2').val('');
	
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

setTitle('<bean:message bundle="diameterResources" key="routingconf.title"/>');
</script>
<html:javascript formName="diameterRoutingConfForm" />
<html:form action="/createDiameterRoutingConfig" onsubmit="return customValidate(this);" styleId="mainform">
<html:hidden property="subscriberMode" styleId="subscriberMode" value=""/>
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header">
										<bean:message bundle="diameterResources" key="routingconf.create.title" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>

								<tr>
									<td valign="middle" colspan="2" class="table-header">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td align="left" class="labeltext" valign="top" width="20%">
													<bean:message bundle="diameterResources" key="general.name" /> 
													<ec:elitehelp headerBundle="diameterResources" text="general.name" 
													header="general.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="80%">
													<html:text styleId="name" tabindex="1" property="name" onkeyup="verifyName();" size="25" maxlength="50" style="width:250px" /> 
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="general.description" /> 
													<ec:elitehelp headerBundle="diameterResources" text="general.description" 
													header="general.description"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:textarea styleId="description" tabindex="2" property="description" cols="40" rows="2" style="width:250px" />
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" colspan="1" width="25%">
													<bean:message bundle="diameterResources" key="routingconf.tablename" /> 
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.tablename" 
													header="routingconf.tablename"/>
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
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.realmname"/>
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.realmname" 
													header="routingconf.realmname"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="realmName" tabindex="4" property="realmName" size="25" style="width:250px" /> 
													<font color="#FF0000">*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.appids" /> 
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.appids" 
													header="routingconf.appids"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="appIds" property="appIds" tabindex="5" size="25" maxlength="50" style="width:250px" /> 
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.originhost" />
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.originhost" 
													header="routingconf.originhost"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="originHost" tabindex="6" property="originHost" size="25" maxlength="50" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.originrealm" />
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.originrealm" header="routingconf.originrealm"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="originRealm" tabindex="7" property="originRealm" size="25" maxlength="50" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.ruleset" /> 
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.ruleset" 
													header="routingconf.ruleset"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:textarea styleId="ruleset" tabindex="8" name="diameterRoutingConfForm" property="ruleset" cols="50" rows="2" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" colspan="1" width="25%">
													<bean:message bundle="diameterResources" key="routingconf.transmapconf" /> 
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.transmapconf" 
													header="routingconf.transmapconf"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													
													
													<html:select name="diameterRoutingConfForm" styleId ="translationType" property="configId" tabindex="3" style="width:250px">
														<html:option value="">--select--</html:option>
														<optgroup label="Translation Mapping"
																class="labeltext">
																<logic:iterate  id="translationMapping" name="diameterRoutingConfForm" property="translationMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"> 
																	<html:option value="<%=ConfigConstant.TRANSLATION_MAPPING + translationMapping.getTranslationMapConfigId()%>" styleClass="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName() %></html:option>
								 								</logic:iterate>
															
																
														</optgroup>
														<optgroup label="Copy Packet Mapping" class="labeltext">
														<logic:iterate  id="copyPacketMapping" name="diameterRoutingConfForm" property="copyPacketMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData"> 
																	<html:option value="<%=ConfigConstant.COPY_PACKET_MAPPING + copyPacketMapping.getCopyPacketTransConfId()%>" styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></html:option>
								 								</logic:iterate>
														</optgroup>
														
														
														</html:select>	
														<html:hidden property="configId" styleId="configId" style="configId"/>
												</td> 
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.routingaction" />
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.routingaction" 
													header="routingconf.routingaction"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select name="diameterRoutingConfForm" styleId="routingAction" property="routingAction" style="width:250px" tabindex="10" value="<%=String.valueOf(RoutingActions.RELAY.routingAction)%>">
														<logic:iterate id="routingActionInst"  collection="<%=RoutingActions.values() %>">
															<%String displayText=((RoutingActions)routingActionInst).routingActionStr; %>
															<html:option value="<%=String.valueOf(((RoutingActions)routingActionInst).routingAction)%>"><%=displayText%></html:option>
								 						</logic:iterate>
													</html:select>
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.statefulrouting" /> 
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.statefulrouting" 
													header="routingconf.statefulrouting"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select property="statefulRouting" styleId="statefulRouting" style="width:250px" tabindex="11">
														<html:option value="1">Enabled </html:option>
														<html:option value="0">Disabled </html:option>
													</html:select>
												</td>
											</tr>
											 <tr>
												<td align="left" class="labeltext" valign="top">
													<bean:message bundle="diameterResources" key="routingconf.attachedredirection" /> 
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.attachedrouting" 
													header="routingconf.attachedredirection"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select property="attachedRedirection" styleId="attachedRedirection" style="width:250px" tabindex="11">
														<html:option value="true">Enabled </html:option>
														<html:option value="false">Disabled </html:option>
													</html:select>
												</td>
											</tr> 
											<tr>
												<td align="left" class="labeltext" valign="top" colspan="1" width="25%">
													<bean:message bundle="diameterResources" key="routingconf.transactiontimeout" /> 
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.transactiontimeout" 
													header="routingconf.transactiontimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="transactionTimeout" tabindex="12" property="transactionTimeout" size="25" maxlength="256" style="width:250px" /> 
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" colspan="1" width="25%">
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
												<td align="left" class="labeltext" valign="top" colspan="1" width="25%">
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
												<td colspan="4" width="100%">
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
																<table id="failureActionTable" cellpadding="0" cellspacing="0" border="0" width="85%">
																	<tr>
																		<td>&nbsp;</td>
																	</tr>
																	<tr>
																		<td class="labeltext" class="table-header">
																			<input type="button" tabindex="13" class="light-btn" value="Add Failure Action" onclick="addFailureActionRow()" />
																		</td>
																	</tr>
																	<tr>
																		<td>&nbsp;</td>
																	</tr>
																	<tr>
																		<td class="tblheader-bold" width="25%">Error Code</td>
																		<td class="tblheader-bold" width="25%">Failure Action</td>
																		<td class="tblheader-bold" width="25%">Failure Argument</td>
																		<td class="tblheader-bold" width="6%">Remove</td>
																	</tr>

																</table>
															</td>
														</tr>
														<%-- <tr><td>&nbsp;</td></tr>
										<tr>
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
												<td align="left" class="tblheader-bold" valign="top" width="100%" colspan="2">
													<bean:message bundle="diameterResources" key="routingconf.peergroup" />
													<ec:elitehelp headerBundle="diameterResources" text="routingconf.peergroup" 
													header="routingconf.peergroup"/>
												</td>
											</tr>

											<tr>
												<td colspan="2">&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" width="100%" colspan="2" class="table-header">
													<input type="button" tabindex="14" class="light-btn" value="Add Peer Group" onclick="popupPeerGroup()" />
												</td>
											</tr>

											<tr>
												<td colspan="2">&nbsp;</td>
											</tr>

											<tr>
												<td align="left" valign="top" width="100%" colspan="2">
													<table id="peerGroupTable" width="85%" cellpadding="0" cellspacing="0" border="0">
														<tr>
															<td class="tblheader-bold" align="left" width="35%">
																<bean:message bundle="diameterResources" key="routingconf.peergroup.ruleset" />
															</td>
															<td class="tblheader-bold" align="left" width="50%">
																<bean:message bundle="diameterResources" key="routingconf.peergroup.peer" />
															</td>
															<td class="tblheader-bold" align="left" width="6%">
																Remove
															</td>
														</tr>
													</table>
												</td>
											</tr>

											<tr>
												<td colspan="2">&nbsp;</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle" colspan="3" align="left">
													<input type="submit" tabindex="15" name="c_btnCreate" id="c_btnCreate" value="Create" class="light-btn">
													<input type="reset" tabindex="16" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchDiameterRoutingConfig.do?/>'" value="Cancel" class="light-btn">
												</td>
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

	<div id="peerGroupDiv" title="Add Peer Group" style="display: none;">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="labeltext" width="25%">
					<bean:message bundle="diameterResources" key="routingconf.peergroup.ruleset" />
				</td>
				<td align="left" class="labeltext" width="60%">
					<html:textarea property="peerGroupRuleSet" styleId="peerGroupRuleSet" cols="60" rows="4"></html:textarea> 
					<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('diameter');" />
				</td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>

			<tr>
				<td colspan="2" class="labeltext">
					<input type="button" tabindex="17" value="Add Peer" onclick="addPeer()" class="light-btn" />
				</td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>

			<tr>
				<td colspan="2">
					<table id="peerTable" width="100%" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td class="tblheader" align="left" width="75%">
								<bean:message bundle="diameterResources" key="routingconf.peergroup.peer" />
							</td>
							<td class="tblheader" align="left" width="19%">
								<bean:message bundle="diameterResources" key="routingconf.peergroup.loadfactor" />
							</td>
							<td class="tblheader" align="left" width="6%">Remove</td>
						</tr>
					</table>
				</td>
			</tr>

		</table>
	</div>

	<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
		<div id="expBuilderId" align="center"></div>
	</div>

</html:form>
