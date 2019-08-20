<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%
	TGPPAAAPolicyData tgppAAAPolicyDataObj = (TGPPAAAPolicyData) request.getAttribute("tgppAAAPolicyData");
	TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm) request.getAttribute("tgppAAAPolicyForm");
	List<DriverInstanceData> listOfAcctDriver = (List<DriverInstanceData>)request.getAttribute("listOfAcctDriver");	
	List<DriverTypeData> driverTypeList =(List<DriverTypeData>)request.getAttribute("driverTypeList");
	List<PluginInstData> pluginList = (List<PluginInstData>)request.getAttribute("pluginList");
%>
		
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/togglebutton.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tgpp-aaa-policy/tgpp-aaa-policy.css">
<script  src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.dialog.js"></script>
<script  src="<%=request.getContextPath()%>/js/history/jquery-2.0.3.min.js"></script>
<script  src="<%=request.getContextPath()%>/js/servicepolicy/tgppaaapolicy/bootstrap.js"></script>
<script  src="<%=request.getContextPath()%>/js/calender/jquery-ui.min.js"></script>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>

<script language="javascript" src="<%=request.getContextPath()%>/js/servicepolicy/cdr-mapping-table.js"></script>
<script type="text/javascript">

var driverScriptList = [];
var externalScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(tgppAAAPolicyForm.getDriverScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : tgppAAAPolicyForm.getDriverScriptList()){ %>
		driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
if( Collectionz.isNullOrEmpty(tgppAAAPolicyForm.getExternalScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : tgppAAAPolicyForm.getExternalScriptList()){ %>
		externalScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

var isView = '<%= ("true".equals(request.getAttribute("isViewPage").toString())) %>';
	$(document).ready(function(){
		$('#editCCFlow').hide();
		setAutocompleteOnCommandCode();
		setAutocompleteOnInterface();

		loadConfirmDialog();
		setExpressionData("TGPP AAA Policy");
		getPrimaryServerData();
		setproxyESIServerData(getProxyCommunicationData());
		fetchHandlers();

		if(isView == "false"){
			doSortable();
		}
		
		/* Get pre plugin list*/
		<%for(PluginInstData pluginInstData : pluginList){%>
			pluginList.push({"id":"<%=pluginInstData.getPluginInstanceId()%>","value":"<%=pluginInstData.getPluginTypeName()%>","label":"<%=pluginInstData.getName()%>"});
		<%}%>
	 
		hideButtons();
		/*  Authentication Handlers  */
		if(isView == 'false'){
			$(".service_handler_table tbody.parent").sortable({
				placeholder: 'ui-state-highlight',
				helper: fixHelper,
				update: function(event, ui) {
				},
				start: function(e, ui){
					var height = ui.item.height();
					ui.placeholder.height(ui.item.height());
					height =  height - 10;
					ui.placeholder.html("<div style='border:1px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;margin-right:0px;color:gray;'>Drag & Drop Handler</div>");
				}
			});
		}
	 
		/* Script Autocomplete */
		setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
		setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
		
		$('#editCCFlow').show();
		
		if(isView == "false"){
			$(".nav-tabs li").each(function() {
				$(this).children('a').click(function(){
					setFocusAfterTrigger();
				});
			});
		}
		$('.nav-tabs li:nth-child(1) a').click();
	});
   
   function changeDiameterPolicyVal(checkboxObj){
  	 if($(checkboxObj).is(':checked')){
  		 $(checkboxObj).val('true');
  	 }else{
  		 $(checkboxObj).val('false');
  	 }
  }
   
   function getHandlerInfo(serviceHandlerTableId,handlerValue){
	    if( handlerValue.handlerType == 'DiameterAuthenticationHandlerData'){
	  		addAuthenticationHandler(serviceHandlerTableId,handlerValue);
	  	}else if( handlerValue.handlerType == 'DiameterAuthorizationHandlerData'){
	  		addAuthorizationHandler(serviceHandlerTableId,handlerValue);
	  	}else if( handlerValue.handlerType == 'DiameterSubscriberProfileRepositoryDetails'){
	  		addProfileLookupHandler(serviceHandlerTableId,handlerValue);
	  	}else if( handlerValue.handlerType == 'DiameterPluginHandlerData'){
	  		addPluginHandler(serviceHandlerTableId,handlerValue,'commandcode-flow-table');
		}else if( handlerValue.handlerType == 'DiameterSynchronousCommunicationHandlerData'){
	  		addProxyCommunicationHandler(serviceHandlerTableId,handlerValue);
		}else if( handlerValue.handlerType == 'DiameterBroadcastCommunicationHandlerData'){
	  		addBroadcastCommunicationHandler(serviceHandlerTableId,handlerValue);
		}else if( handlerValue.handlerType == 'DiameterCDRGenerationHandlerData'){
	  		addCDRGenHandler(serviceHandlerTableId,handlerValue,'commandcode-flow-table');
		}else if( handlerValue.handlerType == 'DiameterConcurrencyHandlerData'){
	  		addConcurrencyHandler(serviceHandlerTableId,handlerValue,'commandcode-flow-table');
	  	
		}
   }
 
 function getPostHandlerInfo (serviceHandlerTableId,handlerValue){
	    if( handlerValue.handlerType == 'DiameterCDRGenerationHandlerData'){
	  		addCDRGenHandler(serviceHandlerTableId,handlerValue,'post-response-commandcode-flow-table');
		}else if( handlerValue.handlerType == 'DiameterPluginHandlerData'){
	  		addPluginHandler(serviceHandlerTableId,handlerValue,'post-response-commandcode-flow-table');
		} 
 }
   
  function getProxyCommunicationData() {
		var peerGroupDataList = [];
		<logic:iterate id="obj" name="tgppAAAPolicyForm" property="diameterPeerGroupDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup">
			peerGroupDataList.push({
					'id' : '<bean:write name="obj" property="peerGroupId"/>',
					'peerGroupName' : '<bean:write name="obj" property="peerGroupName"/>'
			});
		</logic:iterate>
		return peerGroupDataList;
	}

	/* This function is required for getting accounting driver
	 * "var driverDataJson" is declared in cdr-mapping-table.js
	 */
	function getPrimaryServerData() {
		<%for(DriverTypeData driverTypeData:driverTypeList){ %>
			driverDataJson['<%=driverTypeData.getName()%>'] = [];
			<%for(DriverInstanceData driverInstanceData:listOfAcctDriver){ 
				if(driverTypeData.getDriverTypeId() == driverInstanceData.getDriverTypeId()){%>
					driverDataJson['<%=driverTypeData.getName()%>'].push(
						{id:'<%=driverInstanceData.getDriverInstanceId()%>'
						,name:'<%=driverInstanceData.getName()%>'});
				<%}%>
			<%}%>
		<%}%>  
  	}

	 /* This function is required for reload list of accounting driver
	  * "var driverDataJson" is declared in cdr-mapping-table.js
	  */
	 function reloadDriverList(){
	  	 var response;
	  	 $.ajax({url:"GetTGPPDriverDataServlet",
				type:"POST",
				async:false,
				success: function(transport){
					response=transport;
					<%for(DriverTypeData driverTypeData:driverTypeList){ %>
						driverDataJson['<%=driverTypeData.getName()%>'] = [];
						var typeid=<%=driverTypeData.getDriverTypeId()%>;
						$.each(response, function(index, item) { 
							var innerTypeId= item.driverTypeId;
							if( parseInt(innerTypeId) == parseInt(typeid)){
								driverDataJson['<%=driverTypeData.getName()%>']
									.push({id : item.driverInstanceId,name: item.Name});
							} 
						});
					<%}%>
				}
		});
	  	setOtherDriverDropDown('form_cdrHandler');
	 }
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<html:form action="updateTGPPAAAServicePolicyCommandCodeFlow" styleId="tgppForm">
		<html:hidden name="tgppAAAPolicyForm" styleId="tgppAAAPolicyId" property="tgppAAAPolicyId" value="<%=tgppAAAPolicyDataObj.getTgppAAAPolicyId()%>" />
		<html:hidden name="tgppAAAPolicyForm" styleId="action" property="action" value="update" />
		<html:hidden name="tgppAAAPolicyForm" styleId="auditUid" property="auditUid" />
		<html:hidden property="tgppCommandCodeJSON" styleId="tgppCommandCodeJSON" name="tgppAAAPolicyForm"/>
		<html:hidden property="sessionManagement" styleId="sessionManagement" name="tgppAAAPolicyForm" />
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" width="30%" colspan="6">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.update.flow" />
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td>
							<table name="c_tblCrossProductList" width="100%" border="0">
								<tr>
									<td>
										<div class="container" style="padding: 10px;">
										    
										    <!-- Nav tabs -->
										    <ul class="nav nav-tabs" role="tablist">
										      <li class="active"  style="float: right;">
										      	  <%if("true".equals(request.getAttribute("isViewPage").toString()) == false){ %>
											          <a href="#add_command_code" role="tab" data-toggle="tab" id="editCCFlow">
											              <i class="fa fa-plus" style="color:#005197;"></i>
											          </a>
											      <%} %>
										      </li>
										    </ul>
										    
										    <!-- Tab panes -->
										    <div class="tab-content">
										    <%if("true".equals(request.getAttribute("isViewPage").toString()) == false){ %>
										      <div class="tab-pane fade" id="add_command_code">
										       		
										       		<table cellspacing="0" cellpadding="0" border="0" width="100%">
										       			<tr>
										       				<td>&nbsp;</td>
										       			</tr>
										       			<tr>
										       				<td class="box" style="border: 1px solid #D9E6F6;">
										       					<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;">
																	<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.commandcode.configuration" />
																</div>
																<div class="captiontext"  style="padding: 5px;">
																	<table width="100%" cellspacing="0" cellpadding="0" border="0">
																		<tr>
																			<td style="padding:5px;padding-left: 20px;" class="labeltext" width="20%">
																				<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.displaylabelname"/>
																				<ec:elitehelp  header="tgppaaapolicy.displaylabelname" headerBundle="servicePolicyProperties" text="tgppaaapolicy.displaylabelname" ></ec:elitehelp>
																			</td>
																			<td width="80%" class="labeltext">
																				<input type="hidden" id="totalCommandCodeLimit" value="<%=ConfigManager.get(ConfigConstant.TGPP_COMMAND_CODE_LIMIT)%>"/>
																				<input type="text" class="textbox_width" id="displaylabel" />
																				<font color="#FF0000">*</font>
																			</td>
																		</tr>
																		<tr>
																			<td style="padding:5px;padding-left: 20px;" class="labeltext" width="20%">
																				<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.commandcode"/>
																				<ec:elitehelp  header="tgppaaapolicy.commandcode.configuration" headerBundle="servicePolicyProperties" text="tgppaaapolicy.commandcode.configuration" ></ec:elitehelp>
																			</td>
																			<td width="80%" class="labeltext">
																				<input type="text" class="textbox_width commandCode textbox-width-class" pattern="[0-9,]*" id="command_code_txt" onkeypress="setAutocompleteOnCommandCode();"  />
																				<font color="#FF0000">*</font>
																			</td>
																		</tr>
																			<tr>
																			<td style="padding:5px;padding-left: 20px;" class="labeltext" width="20%">
																				<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.interface"/>
																				<ec:elitehelp  header="tgppaaapolicy.interface" headerBundle="servicePolicyProperties" text="tgppaaapolicy.interface" ></ec:elitehelp>
																			</td>
																			<td width="80%" class="labeltext">
																				<input type="text" class="textbox_width applicationid textbox-width-class" id="interface" onkeypress="setAutocompleteOnInterface();" />
																				<font color="#FF0000">*</font>
																			</td>
																		</tr>
																		<tr>
														 					<td colspan="2">
																				&nbsp;
																			</td>
																		</tr>
																		<tr>
																			<td>
																				&nbsp;
																			</td>
																			<td>
																				<input type="button" value="Add Command Code Flow" class="light-btn" id="add-command-code" />
																			</td>
																		</tr>
																		<tr>
														 					<td colspan="2">
																				&nbsp;
																			</td>
																		</tr>
																	</table>
																</div>
										       				</td>
										       			</tr>
										       		</table>
										      </div>
										     <%} %>
										    </div>
										</div>
									</td>
								</tr>
								<%if("true".equals(request.getAttribute("isViewPage").toString()) == false){ %>
								<tr>
									<td align="center" id="btns" >
										<input type="button" class="light-btn" value="  Update  " onclick="validateForm()" tabindex="5"/>
										<input type="button" name="c_btnCancelPeerTGPPPolicy" tabindex="6" onclick="javascript:location.href='searchTGPPAAAPolicy.do'" value="  Cancel  " class="light-btn" />
									</td>
								</tr>
								<%} %>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
</table>
</html:form>

<!-- Template Table -->
<table cellspacing="0" cellpadding="0" border="0" width="100%" id="service_handler_table" style="display: none;" class="service_handler_table">
	<tr>
		<td class="commandcode-flow-caption labeltext" >
			<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.commandcode.configuration" />
		</td>
	</tr>
	<tr>
		<td class="command-code-container">
			<div>
				<table width="100%" cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td style="padding:5px;padding-left: 20px;" class="labeltext" width="20%">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.commandcode"/>
							<ec:elitehelp  header="tgppaaapolicy.commandcode.configuration" headerBundle="servicePolicyProperties" text="tgppaaapolicy.commandcode.configuration" ></ec:elitehelp>
						</td>
						<td width="80%" class="labeltext">
							<input type="text" id="commandCode_" class="commandCode textbox-width-class"  name="commandCode" onkeypress="setAutocompleteOnCommandCode();" <%if("true".equals(request.getAttribute("isViewPage").toString())){ %>  disabled=" disabled" <%}%>/>
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td style="padding:5px;padding-left: 20px;" class="labeltext" width="20%">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.interface"/>
							<ec:elitehelp  header="tgppaaapolicy.interface" headerBundle="servicePolicyProperties" text="tgppaaapolicy.interface" ></ec:elitehelp>
						</td>
						<td width="80%" class="labeltext">
							<input type="text" id="interface_" class="interface applicationid textbox-width-class" name="interface" onkeypress="setAutocompleteOnInterface();" <%if("true".equals(request.getAttribute("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td>
							<input type="hidden" id="displayName_" class="displayName" name="displayName" <%if("true".equals(request.getAttribute("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
						</td>
					</tr>
					<tr>
						<td style="line-height: 8px">&nbsp;</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
	<tr>
		<td style="line-height: 10px">&nbsp;</td>
	</tr>
	<tr>
		<td class="commandcode-flow-caption labeltext" >
			<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.commandcodeflowcaption"/>
		</td>
	</tr>
	<tr>
		<td class="command-code-container">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td>
						<input type="button" id="_handleraddbutton_" value="Add Service Handler" class="light-btn add_handler" tabindex="2" onclick="addServiceHandler(this);" <%if("true".equals(request.getAttribute("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
						<input type="hidden" class="uniqueUUID" <%if("true".equals(request.getAttribute("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
					</td>
				</tr>
				<tr>
					<td style="line-height: 10px">&nbsp;</td>
				</tr>
				<tr>
					<td>
						<table id="commandcode-flow-table" class="commandcode-flow-table" cellspacing="0" cellpadding="0" width="100%">
							<tbody class="parent sortableClass">
							</tbody>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td style="line-height: 10px">&nbsp;</td>
	</tr>
	<tr>
		<td class="commandcode-flow-caption labeltext">
			<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.postresponsecommandcodeflowcaption"/>
			<ec:elitehelp header="tgppservicepolicy.postresponseserviceflow" headerBundle="servicePolicyProperties" text="tgppservicepolicy.postresponseserviceflow" ></ec:elitehelp>
		</td>
	</tr>
	<tr>
		<td class="command-code-container">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td>
						<input type="button" id="_posthandleraddbutton_" value="Add Service Handler" class="light-btn add_handler" tabindex="2" onclick="addPostResponseServiceHandler(this);" <%if("true".equals(request.getAttribute("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
					</td>
				</tr>
				<tr>
					<td style="line-height: 10px">&nbsp;</td>
				</tr>
				<tr>
					<td>
						<table id="post-response-commandcode-flow-table" class="post-response-commandcode-flow-table" cellspacing="0" cellpadding="0" width="100%">
							<tbody class="parent sortableClass">
							</tbody>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<div id="confirmDialog" title="Confirmation Required" class="labeltext">
  <b><span id="oldHandlerName"></span></b> has been renamed to <b><span id="newHandlerName"></span></b>.</br></br> Do you want to keep the changes ?
</div>
<div id="driverDiv" style="display: none;" class="driverDiv" title="Create Driver">
	<iframe id='contentIframe' src="<%=basePath%>/initCreateDriver.do?iframeContent=true" style="border: none;height: 100%;width: 100%;"></iframe> 
</div>
<%if("true".equals(request.getAttribute("isViewPage").toString()) == false) { %>
<%@include file="HandlerTemplate.jsp" %>
<%@include file="PostResponseHandlerTemplate.jsp" %>
<% }%>
<script>
 
 function extractLast( term ) {
	return split( term ).pop();
 }

 function setAutocompleteOnCommandCode(){
			
			 $( ".commandCode" ).bind( "keydown", function( event ) {
					if ( event.keyCode === $.ui.keyCode.TAB &&
						$( this ).autocomplete( "instance" ).menu.active ) {
						event.preventDefault();
					}
				 }).autocomplete({
					minLength: 0,
					source: function( request, response ) {
						
						var commandCodeList = [];
						 
						<%for(CommandCode commandCode:CommandCode.VALUES){%>
							commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>'});
					 	<%}%>
						
						response( $.ui.autocomplete.filter(
								commandCodeList, extractLastDbFields( request.term ) ) );
					},
					focus: function() {
						return false;
					},
					select: function( event, ui ) {
						var terms = split( this.value );
						terms.pop();
						terms.push( ui.item.value );
						this.value = terms.join( "," );
						return false;
					}
				});
	}
	    
	function setAutocompleteOnInterface(){
		
		$( ".applicationid" ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
		 }).autocomplete({
			minLength: 0,
			source: function( request, response ) {
				
				var applicationIdList = [];
				 
				<%for(ApplicationIdentifier applicationIdentifier:ApplicationIdentifier.VALUES){%>
					applicationIdList.push({'value':'<%=applicationIdentifier.applicationId%>','label':'[<%=applicationIdentifier.applicationId%>] <%=applicationIdentifier.name()%>'});
				<%}%> 
				
				response( $.ui.autocomplete.filter(
						applicationIdList, extractLastDbFields( request.term ) ) );
			},
			focus: function() {
				return false;
			},
			select: function( event, ui ) {
				var terms = split( this.value );
				terms.pop();
				terms.push( ui.item.value );
				this.value = terms.join( "," );
				return false;
			}
		});
	}
	
</script>	
<!-- External Module specific JS -->
<script type="text/javascript" src="<%=basePath%>/js/servicepolicy/tgppaaapolicy/tgpp-aaa-policy-update.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/servicepolicy/tgppaaapolicy/tgpp-aaa-policy.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/servicepolicy/tgppaaapolicy/tgpp-proxycommunication.js"></script>

