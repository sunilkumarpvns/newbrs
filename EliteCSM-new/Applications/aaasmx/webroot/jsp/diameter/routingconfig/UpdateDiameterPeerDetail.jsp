<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupRelData"%>

<%
		DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)request.getAttribute("diameterRoutingConfForm");
		DiameterRoutingConfData diameterRoutingConfData = (DiameterRoutingConfData)request.getAttribute("diameterRoutingConfData");
		List<DiameterPeerData> diameterPeerDataList = diameterRoutingConfForm.getDiameterPeersList();
%>

<script type="text/javascript" language="javascript"
	src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript"
	src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
$(document).ready(function(){
	setPeerGroup();
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
function incrementPeerGroup(){
	peerGroupCount = peerGroupCount+1;
}
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
				incrementPeerGroup();
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

function validate(){
	
	if(document.getElementById('peerGroupTable').getElementsByTagName('tr').length < 1){
		alert("Atleast one peer must be specified in Peer Group.");
		return false;
	}else{
		add();
		return true;
	}
}

function add(){
	$("#mainform").append("<input type='hidden' name='peerGroupCount' value='"+peerGroupCount+"' />");
}

setTitle('<bean:message bundle="diameterResources" key="routingconf.title"/>');
	
</script>

<html:form action="/updateDiameterRoutingConfPeer"
	onsubmit="return validate()" styleId="mainform">
	<html:hidden name="diameterRoutingConfForm" styleId="routingConfigId"
		property="routingConfigId" />
	<html:hidden name="diameterRoutingConfForm" styleId="auditUId"
		property="auditUId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%"
		align="right">
		<tr>
			<td class="box" valign="middle" colspan="5" class="table-header">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="30%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources"
								key="routingconf.routingaction.update.peer" />
						</td>
					</tr>

					<tr>
						<td align="left" class="tblheader-bold" valign="top" width="100%"
							colspan="2"><bean:message bundle="diameterResources"
							key="routingconf.peergroup" /> 
							<ec:elitehelp headerBundle="diameterResources" text="routingconf.peergroup" header="routingconf.peergroup" example="routingconf.peergroup.example"/>
						</td>
					</tr>

					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" width="100%"
							colspan="2" class="table-header"><input type="button"
							tabindex="1" class="light-btn" value="Add Peer Group"
							onclick="popupPeerGroup()" /></td>
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
									<td class="tblheader-bold" align="left" valign="top" width="6%">Remove</td>
								</tr>
								<bean:define id="diameterRoutingTableBean" name="diameterRoutingConfData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData" />
								<% 
												int peerGroupCount=1;
												%>
								<logic:iterate id="diameterPeerGroupDataBean" name="diameterRoutingTableBean" property="diameterPeerGroupDataSet" type="DiameterPeerGroupData">
									<script>incrementPeerGroup();</script>
									<%
																String peerElementName = "peer"+peerGroupCount;
																String loadFactorElementName = "loadFactor"+peerGroupCount;
																String peerGroupRulesetName = "peerGroupRuleSet"+peerGroupCount;
															%>
									<tr>
										<td class='tblfirstcol' valign='top'>
											<input type='hidden' name='<%=peerGroupRulesetName%>' value='<%=diameterPeerGroupDataBean.getRuleset()%>' /> <%=diameterPeerGroupDataBean.getRuleset()%>
										</td>
										<td class='tblrows' valign='top'>
											<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
												<tr>
													<td align=left class=tblheader width="75%" valign=top>
														<bean:message bundle="diameterResources" key="routingconf.peergroup.peer" />
													</td>
													<td align=left class=tblheader width="19%" valign=top>
														<bean:message bundle="diameterResources" key="routingconf.peergroup.loadfactor" />
													</td>
													<td class="tblheader" align="left" valign="top" width="6%">Remove</td>
												</tr>
												<%if(diameterPeerGroupDataBean.getDiameterPeerGroupRelDataSet()!=null && !diameterPeerGroupDataBean.getDiameterPeerGroupRelDataSet().isEmpty()){ %>
												<logic:iterate id="peerMappingInstDetailDataBean" name="diameterPeerGroupDataBean" property="diameterPeerGroupRelDataSet" type="DiameterPeerGroupRelData">
													<tr>
														<td align=left class=tblrows valign=top>
															<%if(peerMappingInstDetailDataBean.getDiameterPeerData().getRemoteAddress() != null){%>
															<bean:write name="peerMappingInstDetailDataBean" property="diameterPeerData.name" /> 
																(<bean:write name="peerMappingInstDetailDataBean" property="diameterPeerData.remoteAddress" />) &nbsp; 
															<%}else{%>
																<bean:write name="peerMappingInstDetailDataBean" property="diameterPeerData.name" /> (0.0.0.0) &nbsp; 
															<%}%> 
															<input type='hidden' name='<%=peerElementName%>' value='<%=peerMappingInstDetailDataBean.getPeerUUID()%>' />
														</td>
														<td align=left class=tblrows valign=top>
															<bean:write name="peerMappingInstDetailDataBean" property="loadFector" /> 
															<input type='hidden' name='<%=loadFactorElementName%>' value='<%=peerMappingInstDetailDataBean.getLoadFector()%>' />&nbsp;
														</td>
														<td class='tblrows' valign='top'>
															<img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>
													</tr>
												</logic:iterate>
												<%} %>
											</table>
										</td>
										<td class='tblrows' valign='top'>
											<img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' />
										</td>
									</tr>
									<%peerGroupCount++; %>
								</logic:iterate>
							</table>
						</td>
					</tr>

					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>

					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle"><input type="submit"
							tabindex="2" name="c_btnCreate" id="c_btnCreate2" value="Update"
							class="light-btn"> <input type="reset" tabindex="3"
							name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initSearchDiameterRoutingConfig.do?routingConfigId=<%=diameterRoutingConfForm.getRoutingConfigId()%>'"
							value="Cancel" class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<div id="peerGroupDiv" title="Add Peer Group" style="display: none;">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="labeltext" width="25%"><bean:message
						bundle="diameterResources" key="routingconf.peergroup.ruleset" /></td>
				<td align="left" class="labeltext" width="60%"><html:textarea
						property="peerGroupRuleSet" styleId="peerGroupRuleSet" cols="60"
						rows="4"></html:textarea> <img alt="Expression"
					src="<%=basePath%>/images/lookup.jpg"
					onclick="popupExpression('diameter');" /></td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>

			<tr>
				<td colspan="2" class="labeltext"><input type="button"
					value="Add Peer" onclick="addPeer()" class="light-btn" /></td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>

			<tr>
				<td colspan="2">
					<table id="peerTable" width="100%" cellpadding="0" cellspacing="0"
						border="0">
						<tr>
							<td class="tblheader" align="left" width="75%"><bean:message
									bundle="diameterResources" key="routingconf.peergroup.peer" /></td>
							<td class="tblheader" align="left" width="19%"><bean:message
									bundle="diameterResources"
									key="routingconf.peergroup.loadfactor" /></td>
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