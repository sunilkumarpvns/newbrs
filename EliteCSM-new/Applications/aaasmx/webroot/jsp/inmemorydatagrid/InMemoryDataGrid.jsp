<%@page import="com.elitecore.aaa.core.conf.impl.RadiusInMemorySessionClosureAndOverrideProperties.SessionCloseAndOverrideAction"%>
<%@page import="com.elitecore.aaa.core.conf.impl.RadiusInMemorySessionClosureAndOverrideProperties.SessionCloseAndOverrideAction"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="com.elitecore.commons.base.Strings"%>
<%@page import="com.elitecore.elitesm.web.inmemorydatagrid.form.InMemoryDataGridForm"%>
<%@page import="com.elitecore.aaa.core.conf.impl.ImdgConfigData"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.hazelcast.config.InMemoryFormat" %>
<%@ page import="com.elitecore.aaa.core.conf.impl.RadiusInMemorySessionClosureAndOverrideProperties" %>


<%
	InMemoryDataGridForm inMemoryDataGridForm = (InMemoryDataGridForm) request.getAttribute("inMemoryDataGridForm");

	String imdgConfig = inMemoryDataGridForm.getImdgConfig();

%>

<link rel="stylesheet" href="<%=basePath %>/font/fontawesome/fontawesome_css/font-awesome.min.css" />
<link rel="stylesheet" href="<%=basePath %>/css/inmemorydatagrid/vis.min.css" />
<link rel="stylesheet" href="<%=basePath %>/css/inmemorydatagrid/inmemorydatagrid.css" /> 
<link rel="stylesheet" href="<%=basePath %>/css/history/jquery-ui.css" />

<script type="text/javascript" src="<%=basePath %>/js/history/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/history/jquery-ui.js"></script>

<script type="text/javascript" src="<%=basePath %>/js/inmemorydatagrid/in-memory-data-grid.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/inmemorydatagrid/vis.min.js"></script>


<script>

	setTitle('<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.title"/>');
	
	$(document).ready(function(){
		
		/* Get Array Of Radius Attributes */
		retriveRadiusAttribute();
		
		<% if(Strings.isNullOrBlank(imdgConfig)) {%>
			addDefaultGroup();
			$('#active').val("false");
			$('#inMemoryDataGridEnabledDisabled').show();
		<%} else{ %>
			var imdgConfigJSON = JSON.parse('<%=imdgConfig%>');
			addUpdatedValue(imdgConfigJSON);
		<%}%>
		
		setInstanceNameData(getServerInstanceData());
		
	});
	
 	$( function() {
	
	    $('#topology-menu').click(function(){
	    	 var clusterInfoArray = fetchClusterGroup();
	    	 var  node ;
			  nodes = [];
			  edges = [];
			  data = {};
			  
			  $.each(clusterInfoArray, function(key, value){
				  nodes.push({ 
	        		   id : value.name,
	        		   label : value.name,
	        		   group:'group'});
				  if( value.memberDatas.length > 0){
					  $.each(value.memberDatas, function(memberKey, memberValue){
						  nodes.push({ 
   	        			   id : memberValue.name,
 	    	        		   label : memberValue.name,
 	    	        		   group:'instances'});
   	        		  edges.push({
   	        				from: memberValue.name, 
   	        				to: value.name
   	        		  }); 
					  });
				  }
			  });
			  
			  data = {
		    			nodes: nodes,
		    			edges: edges
		    		  };
			  
			network.destroy();
			network = new vis.Network(container, data, options);
			
			getServerNodeDataList(network);
		 
			setInterval(function() {
		    	getServerNodeDataList(network);
		    }, 60000);
		    
		    });
 	}); 
	
	function getServerInstanceData(){
		var serverList = [];
		<logic:iterate id="obj" name="inMemoryDataGridForm" property="instanceDataList" type="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData">
			serverList.push({'id':'<bean:write name="obj" property="name"/>','name':'<bean:write name="obj" property="name"/>'});
		</logic:iterate>
		return serverList;
	}
	
	function getServerNodeDataList(network){
		var nodeDetailList = [];
		var nodeFound = false;
		$.ajax({ 
		    type: 'GET',
		    url: '<%=request.getContextPath()%>/DownServerNodeDataServlet', 
		    success: function (data) {
		    	 $.each(data, function(key, value){
		    		 nodeDetailList.push(value);
		    	 });
	    			 $.each(network.clustering.body.nodes, function(key, value){
	    				 if(nodeDetailList.indexOf(key) > -1){
	    					 value.options.icon.color = '#ff3333'; //red color
	    				 }  else {
	    					 $.each(nodes, function(nodeKey, nodeValue){
	    						  if(nodeValue.id == key && nodeValue.group == 'instances'){
	    							  value.options.icon.color = '#60A917';// green color
	    						 } 
	    					 });
	    				 } 
	    			 network.redraw();
		    		 });
		    }
		})
	}
	
	$(document).ready(function(){
		$('#mappingtbl td img.delete').live('click',function() {	
			 $(this).parent().parent().remove(); 
		});	
	}); 

	
</script>
<html:form action="/inMemoryDataGrid.do?method=updateBasicDetails" styleId="inMemoryDataGridId">
	
	<html:hidden property="imdgConfig" styleId="imdgConfigBasicDetail"/>
	
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td>
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td>
										<table name="c_tblCrossProductList" width="100%" cellpadding="0" cellspacing="0">
											<tr>
												<td class="captiontext" style="padding-top: 10px;" width="70%">
													<table width="100%" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td class="labeltext" width="30%">
																<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.status" />
																<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.status" header="inmemorydatagrid.status" />
															</td>
															<td>
																<select id="active">
																	<option value="true" selected="selected">Enabled</option>
																	<option value="false">Disabled</option>
																</select>
															</td>
														</tr>
													</table>
												</td>
												<td class="labeltext" width="30%" valign="bottom" colspan="2">
													<div class="inmemory-enable-div-container">
														<div id="inMemoryDataGridEnabledDisabled" class="inmemory-enable-div">
															<i class="fa fa-exclamation-triangle margin-css"></i> <bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.disabled" />
														</div>
													</div>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td  class="captiontext" align="left" style="padding-top: 10px;padding-right:10px;">
							<div class="container" >
								<div id="tabs" class="tabs">
									<nav>
										<ul>
											<li><a href="#configuration_section" class="text-decoration-css"><i class="fa fa-cog margin-css"></i><span>Basic Details</span></a></li>
											<li><a href="#radius-field-mapping-section" class="text-decoration-css"> <i class="fa fa-cog margin-css"> </i><span>Radius Session</span></a></li>
											<li><a href="#diameter-field-mapping-section" class="text-decoration-css"> <i class="fa fa-cog margin-css"> </i><span>Diameter Session</span></a></li>
											<li id="topology-menu"><a href="#topology_section" class="text-decoration-css"><i class="fa fa-sitemap margin-css"></i><span>Topology</span></a></li>
										</ul>
									</nav>
									<div class="content" style="border-left : 1px solid #47A3DA;border-right:1px solid #47A3DA;border-bottom : 1px solid #47A3DA;">
										<section id="configuration_section">
										 	<table cellpadding="0" cellspacing="0" border="0" width="100%">
												<tr>
													<td class="captiontext">
														<ul class="add-group-ul">
															<li class="add-group-li">
																<a id="add-group-btn" class="add-group-a text-decoration-css">
																	<i class="fa fa-plus" style=""></i> 
																	<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.addgroup" />
																</a>
															</li>
														</ul>
													</td>
												</tr>
												<tr>
													<td>
														<table cellpadding="0" cellspacing="0" border="0" width="100%" id="group-table">
														</table>
													</td>
												</tr>
												<tr>
													<td class="captiontext" valign="top">
														<table width="60%">
															<tr>
																<td class="captiontext" style="padding-bottom: 10px;">
																	<table width="100%" cellspacing="0" cellpadding="0" border="0" id="string-property-table" >
																		<tr>
																			<td colspan="3" class="" style="padding-bottom: 10px;">
																				<input type="button" name="addproperty" value="Add Property" class="light-btn add-property"/>
																			</td>
																		</tr>
																		<tr>
																			<td class="tblheader" width="45%">
																				<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.key" />
																				<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.key" header="inmemorydatagrid.key" />
																			</td>
																			<td class="tblheader" width="45%">
																				<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.value" />
																				<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.value" header="inmemorydatagrid.value" />
																			</td>
																			<td class="tblheader last-td-css" width="10%" align="center">
																				<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.remove" />
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td class="tblheader-bold">
														<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.advanced" />
													</td>
												</tr>
												<tr>
													<td colspan="3" class="captiontext" style="padding-top: 10px;">
														<table width="100%" cellspacing="0" cellpadding="0" border="0">
															<tr>
																<td class="captiontext" width="20%">
																	<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.inmemoryformat" />
																	<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.inmemoryformat" header="inmemorydatagrid.inmemoryformat" />
																</td>
																<td class="labeltext">
																	<select name="inMemoryFormat" id="inMemoryFormat" class="input-width">
																		<logic:iterate id="inMemoryFormatObj" collection="<%=InMemoryFormat.values() %>">
																			<option value="<%=inMemoryFormatObj.toString()%>"><%=inMemoryFormatObj.toString()%></option>
																		</logic:iterate>
																	</select>
																	<font color="#FF0000">*</font>
																</td>
															</tr>
															<tr>
																<td class="captiontext">
																	<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.startport" />
																	<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.startport" header="inmemorydatagrid.startport" />
																</td>
																<td class="labeltext">
																	<input type="text" name="startPort" id="startPort" class="input-box startPort input-width" value="5701" />
																	<font color="#FF0000">*</font>
																</td>
															</tr>
															<tr>
																<td class="captiontext">
																	<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.startportcount" />
																	<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.startportcount" header="inmemorydatagrid.startportcount" />
																</td>
																<td class="labeltext">
																	<input type="text" name="startPortCount" id="startPortCount" class="input-box startPortCount input-width" value="500"/>
																	<font color="#FF0000">*</font>
																</td>
															</tr>
															<tr>
																<td class="captiontext">
																	<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.outboundports" />
																	<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.outboundports" header="inmemorydatagrid.outboundports" />
																</td>
																<td class="labeltext">
																	<input type="text" name="outboundPorts" id="outboundPorts" class="input-box outboundPorts"/>
																</td>
															</tr>
															<tr>
																<td class="captiontext">
																	<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.mancenterurl" />
																	<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.mancenterurl" header="inmemorydatagrid.mancenterurl" />
																</td>
																<td class="labeltext">
																	<input type="text" name="mancenterUrl" id="mancenterUrl" class="input-box mancenterUrl"/>
																</td>
															</tr>
															<tr>
																<td>&nbsp;</td>
															</tr>
															<tr>
																<td align="left" colspan="3" style="padding-left: 300px;">
																	<input type="button" class="light-btn" value=" Configure " onclick="validateForm()" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</section>
										<section id="radius-field-mapping-section">	
										</section>
										<section id="diameter-field-mapping-section">
										</section>
										<section id="topology_section">
											 <div id="topology" align="center"></div>
										</section>
									</div><!-- /content -->
								</div><!-- /tabs -->
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>

<div id="radius-field">
	<html:form action="/inMemoryDataGrid.do?method=updateRadiusSessionIndex" styleId="radiusSessionMappingId">
		<html:hidden property="imdgConfig" styleId="imdgConfigRadiusMapping"/>
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td class="tblheader-bold"><bean:message
						bundle="inMemoryDataGridResources" key="inmemorydatagrid.index" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="captiontext" valign="top">
					<table width="60%">
						<tr>
							<td class="captiontext" style="padding-bottom: 10px;">
								<table width="100%" cellspacing="0" cellpadding="0" border="0" id="radius-field-mapping-table">
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<!-- Session closure and override properties  -->
		<html:hidden property="imdgConfig" styleId="imdgConfigRadiusMapping"/>
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td class="tblheader-bold"><bean:message
						bundle="inMemoryDataGridResources"
						key="inmemorydatagrid.closeAndOverride" /></td>
			</tr>
			<tr>
				<td class="captiontext" valign="top">
					<table width="60%">
						<tr>
							<td class="captiontext" style="padding-bottom: 10px;">
								<table width="100%" cellspacing="0" cellpadding="0" border="0"
									id="radius-session-field-mapping">
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>

		</table>
		<table width="100%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="50%">
					<table width="100%" cellspacing="0" cellpadding="0" border="0" id="closureproperties">
						<tr>
							<td class="captiontext" valign="top"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.batchsize" /> <ec:elitehelp
									headerBundle="inMemoryDataGridResources"
									text="inmemorydatagrid.batchsize"
									header="inmemorydatagrid.batchsize" /></td>
							<td class="labeltext"><input type="text" id="batchsize" name="batchsize" value="1000"
								class="input-box groupname"></input></td>
						</tr>
						<tr>
							<td class="captiontext" valign="top"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.timeout" /> <ec:elitehelp
									headerBundle="inMemoryDataGridResources"
									text="inmemorydatagrid.timeout"
									header="inmemorydatagrid.timeout" /></td>
							<td class="labeltext"><input type="text" id="sessiontimeout" value="2"
								name="sessiontimeout" class="input-box groupname"></input></td>
						</tr>
						<tr>
							<td class="captiontext" valign="top"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.threadsleeptime" /> <ec:elitehelp
									headerBundle="inMemoryDataGridResources"
									text="inmemorydatagrid.threadsleeptime"
									header="inmemorydatagrid.threadsleeptime" /></td>
							<td class="labeltext"><input type="text" id="threadsleeptime" value="100"
								name="threadsleeptime" class="input-box groupname"></input></td>
						</tr>
						<tr>
							<td class="captiontext" valign="top"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.closeaction" /> <ec:elitehelp
									headerBundle="inMemoryDataGridResources"
									text="inmemorydatagrid.closeaction"
									header="inmemorydatagrid.closeaction" /></td>
							<td class="labeltext">
							<select name="closeaction" id="closeaction" style="width: 250;" class="input-width">
									<logic:iterate id="closeactionObj"
										collection="<%=SessionCloseAndOverrideAction.values()%>">
										<option value="<%=String.valueOf(((SessionCloseAndOverrideAction)closeactionObj).action)%>"><%=String.valueOf(((SessionCloseAndOverrideAction)closeactionObj).action) %></option>
									</logic:iterate>
							</select>
							</td>
						</tr>
						<tr>
							<td class="captiontext" valign="top"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.sessionoverridefield" /> <ec:elitehelp
									headerBundle="inMemoryDataGridResources"
									text="inmemorydatagrid.sessionoverridefield"
									header="inmemorydatagrid.sessionoverridefield" /></td>
							<td class="labeltext">
							<input type="text"
								name="sessionoverridefield" id="overridefield" class="input-box groupname"></input></td>
						</tr>
					</table>


					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td class="tblheader-bold"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.radiussessionmanager" /></td>
						</tr>
						<tr>
							<td class="captiontext" valign="top">
								<table width="60%">
									<tr>
										<td class="captiontext" style="padding-bottom: 10px;">
											<table width="100%" cellspacing="0" cellpadding="0"
												border="0" id="radius-session-field-mapping">
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>

					</table> <!-- Radius session manager -->
					<table id="radsessionmappingtbl">
						<tr>
							<td align="left" valign="top" colspan="3" id="button"><input
								type="button" tabindex="0"
								onclick='addRow("dbMappingTable","radsessionmappingtbl");'
								value=" Add Mapping" class="light-btn" style="size: 140px">
							</td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td class="tblheader" width="45%"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.radiussessionfieldname" /> <ec:elitehelp
									headerBundle="inMemoryDataGridResources"
									text="inmemorydatagrid.radiussessionfieldname"
									header="inmemorydatagrid.radiussessionfieldname" /></td>

							<td class="tblheader" width="45%"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.radiussessionattribute" /> <ec:elitehelp
									headerBundle="inMemoryDataGridResources"
									text="inmemorydatagrid.radiussessionattribute"
									header="inmemorydatagrid.radiussessionattribute" /></td>
							<td class="tblheader" width="45%"><bean:message
									bundle="inMemoryDataGridResources"
									key="inmemorydatagrid.remove" /></td>
						</tr>
					</table>

					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>&nbsp
						</tr>
						<tr>
							<td style="padding-left: 265;"><html:button
									property="radiusSessionMappingBtn" styleClass="light-btn"
									value="Configure" onclick="validateRadiusMapping();"></html:button>
							</td>
						</tr>
					</table>
	</html:form>
</div>

<div id="diameter-field">
	<html:form action="/inMemoryDataGrid.do?method=updateDiameterSessionIndex" styleId="diameterSessionMappingId">
		<html:hidden property="imdgConfig" styleId="imdgConfigDiameterMapping"/>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="tblheader-bold">
						<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.index" />
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="captiontext" valign="top">
						<table width="60%">
							<tr>
								<td class="captiontext" style="padding-bottom: 10px;">
									<table width="100%" cellspacing="0" cellpadding="0" border="0" id="diameter-field-mapping-table" >
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			<tr>
				<td style="padding-left: 265;">
					<html:button property="diameterSessionMappingBtn" styleClass="light-btn" value="Configure" onclick="validateDiameterMapping();"></html:button>
				</td>
			</tr>
			</table>
	</html:form>
</div>

<!-- port-mapping-template-table -->
<table id="port-mapping-template-table" class="hide-table">
	<tr>
		<td class="tblfirstcol" width="80%">
			<input type="text" name="outBound" class="input-box outBound" style="width: 100%;border:none;" />
		</td>
		<td class="tblrows" align="center" width="20%">
			<span class='delete remove-entry' onclick="removeData(this);"/>&nbsp;
		</td>
	</tr>
</table>

<!-- port-mapping-template-table -->
<table id="string-prop-mapping-template-table" class="hide-table">
	<tr>
		<td class="tblfirstcol">
			<input type="text" name="key" class="input-box key" style="width: 100%;border:none;" />
		</td>
		<td class="tblrows">
			<input type="text" name="value" class="input-box value" style="width: 100%;border:none;" />
		</td>
		<td class="tblrows" align="center" width="20%">
			<span class='delete remove-entry' onclick="removeData(this);"/>&nbsp;
		</td>
	</tr>
</table>


<!-- Group Mapping -->
<table id="group-mapping" class="hide-table">
	<tr>
		<td align="left" class="labeltext" valign="top" style="padding-left: 15px;">
			<div class="div-container">
				<div class="table-header div-header" style="padding-top: 2px !important;padding-bottom:2px;">
					<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.groupconfiguration" />
					<span class="delete remove-blog" style="float: right;" onclick="removeBlog(this);"></span>
				</div>
				<div class="div-body">
					<table width="100%" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td width="50%">
								<table width="100%" cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td class="captiontext" valign="top">
											<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.groupname" /> 
											<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.groupname" header="inmemorydatagrid.groupname" />
										</td>
										<td class="labeltext">
											<input type="text" name="groupname" class="input-box groupname"></input>
											<font color="#FF0000">*</font>
										</td>
									</tr>
									<tr>
										<td class="captiontext" valign="top">
											<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.grouppassword" /> 
											<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.grouppassword" header="inmemorydatagrid.grouppassword" />
										</td>
										<td class="labeltext">
											<input type="password" name="grouppassword" class="input-box grouppassword"></input>
											<font color="#FF0000">*</font>
										</td>
									</tr>
									<tr>
										<td class="captiontext" valign="top">
											<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.groupdescription" /> 
											<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.groupdescription" header="inmemorydatagrid.groupdescription" />
										</td>
										<td class="labeltext">
											<textarea rows="3" cols="50" name="groupdescription" class="groupdescription textarea-width"></textarea>
										</td>
									</tr>
								</table>
							</td>
							<td width="50%" valign="top">
								<table width="100%" cellspacing="0" cellpadding="0" border="0" class="ip-table-container">
									<tr>
										<td class="labeltext">
											<input type="button" id="ip-table-btn" value="Add" class="light-btn ip-table-mapping-btn" onclick="addMapping(this);"/>
										</td>
									</tr>
									<tr>
										<td>
											<table id="ipTableEntry" class="ip-table-mapping" width="95%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td class="tblheader" width="45%">
														<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.instancename" /></td>
													<td class="tblheader" width="45%">
														<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.ipaddress" />
													</td>
													<td class="tblheader last-td-css" width="10%">
														<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.remove" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</td>
	</tr>
</table>

<!-- IP Table Mapping Template -->
<table id="ipTableMappingTamplate" class="hide-table">
	<tr>
		<td class="tblfirstcol" width="45%">
			<select name="name" id="name" style="width: 100%;border:none;" class="instanceName" onchange='setInstanceDropDown(this);'>
			</select>
		</td>
		<td class="tblrows" width="45%">
			<input type="text" name="ip" id="ip" style="border:none;width:100%" class="ip"/>
		</td>
		<td class="tblrows" width="10%" align="center">
			<span class='delete remove-entry' onclick="removeData(this);"/>&nbsp;
		</td>
	</tr>
</table>

<!--Radius Diameter Field Mapping Table  -->
<table width="100%" cellspacing="0" cellpadding="0" border="0" id="common-field-mapping-table" class="hide-table">
	<tr>
		<td class="tblheader" width="45%">
		<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.key" />
		<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.session.key" header="inmemorydatagrid.key" />
		</td>
		<td class="tblheader" width="45%">
			<bean:message bundle="inMemoryDataGridResources" key="inmemorydatagrid.value" />
			<ec:elitehelp headerBundle="inMemoryDataGridResources" text="inmemorydatagrid.session.value" header="inmemorydatagrid.value" />
		</td>
	</tr>
	<tr>
		<td class="tblfirstcol">
			<input type="text" name="key" class="input-box key" style="width: 100%;border:none;" value="User-Name" readonly/>
		</td>
		<td class="tblrows">
			<input type="text" name="value" class="input-box value attributeValue" style="width: 100%;border:none;" value="0:1" onkeyup="loadAttributesForAutoCompletion();"/>
		</td>
	</tr>
	<tr>
		<td class="tblfirstcol">
			<input type="text" name="key" class="input-box key" style="width: 100%;border:none;" value="Framed-IP-Address" readonly/>
		</td>
		<td class="tblrows">
			<input type="text" name="value" class="input-box value attributeValue" style="width: 100%;border:none;" value="0:8" onkeyup="loadAttributesForAutoCompletion();"/>
		</td>
	</tr>
	<tr>
		<td class="tblfirstcol">
			<input type="text" name="key" class="input-box key" style="width: 100%;border:none;" value="Calling-Station-Id" readonly/>
		</td>
		<td class="tblrows">
			<input type="text" name="value" class="input-box value attributeValue" style="width: 100%;border:none;" value="0:31" onkeyup="loadAttributesForAutoCompletion();" />
		</td>
	</tr>
	<tr>
		<td class="tblfirstcol">
			<input type="text" name="key" class="input-box key" style="width: 100%;border:none;" value="NAS-IP-Address" readonly/>
		</td>
		<td class="tblrows">
			<input type="text" name="value" class="input-box value attributeValue" style="width: 100%;border:none;" value="0:4" onkeyup="loadAttributesForAutoCompletion();"/>
		</td>
	</tr>
	<tr>
		<td class="tblfirstcol">
			<input type="text" name="key" class="input-box key" style="width: 100%;border:none;" />
		</td>
		<td class="tblrows">
			<input type="text" name="value" class="input-box value attributeValue" style="width: 100%;border:none;" onkeyup="loadAttributesForAutoCompletion();"/>
		</td>
	</tr>
	<tr>
		<td class="tblfirstcol">
			<input type="text" name="key" class="input-box key" style="width: 100%;border:none;" />
		</td>
		<td class="tblrows">
			<input type="text" name="value" class="input-box value attributeValue" style="width: 100%;border:none;" onkeyup="loadAttributesForAutoCompletion();"/>
		</td>
	</tr>
</table>

<!-- Radius In-memory Session field mapping table -->
<table style="display: none;" id="dbMappingTable">
			<tr>
				<td class="allborder"><input type="text" name="key"
					id="fieldName" class="noborder" tabindex="0" maxlength="1000"
					size="28" style="width: 100%"
					 /></td>
				<td class="tblrows"><input type="text" name="value"
					id="attributes" tabindex="0" maxlength="1000" class="noborder attributeValue"
					size="28" style="width: 100%" onkeyup="loadAttributesForAutoCompletion();" /></td>
				<td class="tblrows" align="center" colspan="3">
					<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" onclick="removeData(this);" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="0" />
				</td>
			</tr>
		</table>

<!-- Default Mapping for Radius In-Memory Session Data -->
<table id="sessionDataMapping" style="display: none;">
	<tr>
							<td class="allborder"><input type="text" name="key" value="Session ID"
								id="fieldName" class="noborder" tabindex="0" maxlength="1000"
								size="28" style="width: 100%" /></td>
							<td class="tblrows"><input type="text" name="value" value="0:44"
								id="attributes" tabindex="0" maxlength="1000" class="noborder attributeValue"
								size="28" style="width: 100%" onkeyup="loadAttributesForAutoCompletion();"/></td>
							<td class="tblrows" align="center" colspan="3"><img
								value="top" src="<%=basePath%>/images/minus.jpg" class="delete"
								onclick="removeData(this);"
								style="padding-right: 5px; padding-top: 5px;" height="14"
								tabindex="0" /></td>
						</tr>
						<tr>
							<td class="allborder"><input type="text" name="key" value="PDP Type"
								id="fieldName" class="noborder" tabindex="0" maxlength="1000"
								size="28" style="width: 100%" /></td>
							<td class="tblrows"><input type="text" name="value" value="0:61"
								id="attributes" tabindex="0" maxlength="1000" class="noborder attributeValue"
								size="28" style="width: 100%" onkeyup="loadAttributesForAutoCompletion();"/></td>
							<td class="tblrows" align="center" colspan="3"><img
								value="top" src="<%=basePath%>/images/minus.jpg" class="delete"
								onclick="removeData(this);"
								style="padding-right: 5px; padding-top: 5px;" height="14"
								tabindex="0" /></td>
						</tr>
						<tr>
							<td class="allborder"><input type="text" name="key" value="Session Timeout"
								id="fieldName" class="noborder" tabindex="0" maxlength="1000"
								size="28" style="width: 100%" /></td>
							<td class="tblrows"><input type="text" name="value" value="$RES(0:27)"
								id="attributes" tabindex="0" maxlength="1000" class="noborder attributeValue"
								size="28" style="width: 100%" onkeyup="loadAttributesForAutoCompletion();"/></td>
							<td class="tblrows" align="center" colspan="3"><img
								value="top" src="<%=basePath%>/images/minus.jpg" class="delete"
								onclick="removeData(this);"
								style="padding-right: 5px; padding-top: 5px;" height="14"
								tabindex="0" /></td>
						</tr>
						<tr>
							<td class="allborder"><input type="text" name="key" value="AAA ID"
								id="fieldName" class="noborder" tabindex="0" maxlength="1000"
								size="28" style="width: 100%" /></td>
							<td class="tblrows"><input type="text" name="value" value="21067:143"
								id="attributes" tabindex="0" maxlength="1000" class="noborder attributeValue"
								size="28" style="width: 100%" onkeyup="loadAttributesForAutoCompletion();"/></td>
							<td class="tblrows" align="center" colspan="3"><img
								value="top" src="<%=basePath%>/images/minus.jpg" class="delete"
								onclick="removeData(this);"
								style="padding-right: 5px; padding-top: 5px;" height="14"
								tabindex="0" /></td>
						</tr>
						<tr>
							<td class="allborder"><input type="text" name="key" value="NAS ID"
								id="fieldName" class="noborder" tabindex="0" maxlength="1000"
								size="28" style="width: 100%" /></td>
							<td class="tblrows"><input type="text" name="value" value="0:32"
								id="attributes" tabindex="0" maxlength="1000" class="noborder attributeValue"
								size="28" style="width: 100%" onkeyup="loadAttributesForAutoCompletion();"/></td>
							<td class="tblrows" align="center" colspan="3"><img
								value="top" src="<%=basePath%>/images/minus.jpg" class="delete"
								onclick="removeData(this);"
								style="padding-right: 5px; padding-top: 5px;" height="14"
								tabindex="0" /></td>
						</tr>
						<tr>
							<td class="allborder"><input type="text" name="key" value="User Identity"
								id="fieldName" class="noborder" tabindex="0" maxlength="1000"
								size="28" style="width: 100%" /></td>
							<td class="tblrows"><input type="text" name="value" value="0:1"
								id="attributes" tabindex="0" maxlength="1000" class="noborder attributeValue"
								size="28" style="width: 100%" onkeyup="loadAttributesForAutoCompletion();"/></td>
							<td class="tblrows" align="center" colspan="3"><img
								value="top" src="<%=basePath%>/images/minus.jpg" class="delete"
								onclick="removeData(this);"
								style="padding-right: 5px; padding-top: 5px;" height="14"
								tabindex="0" /></td>
						</tr>
</table>
<script type="text/javascript" src="<%=basePath %>/js/inmemorydatagrid/cbpFWTabs.js"></script>

<script>
	new CBPFWTabs( document.getElementById( 'tabs' ) );
</script>

<script type="text/javascript">

drawTopology();
initHandlers();

</script>