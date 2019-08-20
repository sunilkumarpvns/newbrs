<%@page import="com.elitecore.corenetvertex.constants.CommonConstants"%>
<%@page import="com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupRelationForm"%>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupForm"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.List"%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery/css/bootstrap/acl/bootstrap.css"/>

<%
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    int iIndex =0;
%>

<style>
.heading-value{
	color: #fff;
}
.heading-value:HOVER{
	color: #fff;
}
.group_right_btns {
	border-color: 	#428bca ;
}

.instance_delete_btn {
	border: none;
	background-color: inherit;
}
.heading {
	padding-top: 3px; 
	padding-bottom: 5px; 
	padding-right: 0px;
}
.btn-default[disabled]{
	background-color: inherit;
}
#inMemorySession{
	padding: 3px 5px;
	border: 1px solid lightgrey;
}
#inMemorySession:active
{
	-moz-box-shadow:    inset 0 0 0px white;
	-webkit-box-shadow: inset 0 0 0px white;
	box-shadow:         inset 0 0 0px white;
}
#inMemorySession:hover
{
	background-color: white;
	cursor: auto;
}

</style>
<script type="text/javascript">
    $(document).ready(function(){
    	
        setTitle('<bean:message  bundle="serverGroupDataMgmtResources" key="group.title"/>');
        $("#name").focus();

        $(".manageorder").each(function(){
        	$(this).find("tbody tr:first").addClass("grey-bkgd");
        });
        $( "#sortable" ).sortable({
            placeholder: "ui-state-highlight",
            update :function (event, ui) {
				var serverInstanceGroupIds = new Array();
               	$(".ServerGroups").each(function(){
               		serverInstanceGroupIds.push($(this).attr("id"));
                });
               	$.ajax({
                    data: "serverInstanceGroupIds="+serverInstanceGroupIds,
                    type: 'POST',
                    url: "<%=basePath%>/serverGroupManagement.do?method=manageOrder",
                    error: function() {
    		    	    alert("Failed to change order");
    	    	    }
                });
            },
            start: function(e, ui){
            	var height = ui.item.height();
                ui.placeholder.height(ui.item.height());
                height =  height - 10;
                ui.placeholder.html("<div style='border:2px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;margin-right:0px;color:gray;'>Drag Server Group Here</div>");
           } 
        });
        $( "#sortable" ).disableSelection();
		<% Boolean  isRequiredSyncTo=  (Boolean) request.getServletContext().getAttribute("SyncTo");
		 %>

		var isRequiredSyncTo = <%=isRequiredSyncTo%>;

		if(isRequiredSyncTo == true) {
			alert("Please perform server synchronization for all the instances to complete upgrade process.");
		}
		<% request.getServletContext().removeAttribute("SyncTo");%>
        
    });

	function changeMode(obj,constValue,objValue,serverGroupId) {

		if(constValue != objValue) {

			if(confirm("Do you want to change the mode of 'In-Memory Session Synchronization'?")){
				$.ajax({
					async       : true,
					type 		: "POST",
					url  		: "<%=basePath%>/serverGroupManagement.do?method=changeSessionMode",
					data		: {'sessionSyncMode' : constValue,'serverGroupId' : serverGroupId},
					success 	: function(json){
						$(obj).siblings('button').removeClass("btn-primary");
						$(obj).siblings('button').addClass("btn-default");
						$(obj).siblings('button').addClass("active");
						$(obj).removeClass("btn-default");
						$(obj).removeClass("active");
						$(obj).addClass("btn-primary");
						location.reload();
					}, error: function() {
						alert("Failed to change the mode of 'In-Memory Session Synchronization'");
					}
				});
			}
		}

	}



    function swapInstances(serverGroupId){

    	if(confirm("Are you sure?")){
    		$.ajax({
	    	    type: "POST",
	    	    url: "<%=basePath%>/serverGroupManagement.do?method=swapInstances",
	    	    data:"serverInstanceGroupId="+serverGroupId,
	    	    success: function(){
	    	    	var tableId = "#tbl"+serverGroupId;
	    	    	$(tableId).find("tbody tr:last").after($(tableId).find("tbody tr:first"));
	    	    	var tempValue = $(tableId).find("tbody tr:last td:first").html();
	    	    	$(tableId).find("tbody tr:last td:first").html($(tableId).find("tbody tr:first td:first").html());
	    	    	$(tableId).find("tbody tr:first td:first").html(tempValue);
	    	    	
	    	    	$(tableId).find("tbody tr:last").removeClass("grey-bkgd");
	    	    	$(tableId).find("tbody tr:first").addClass("grey-bkgd");
	    	    },
	    	    error: function() {
		    	    alert("Failed to swap instances");
	    	    }
	    	});
    	}
    	
    }
    
    function removeInstance(element,id,serverInstanceGroupId){
    	if(confirm("You are going to delete server instance, Do you want to continue?")){
    		$(element).attr('href', "<%=basePath%>/miscServer.do?method=delete&select="+ id +"&serverInstanceGroupId="+serverInstanceGroupId);
    	}
    }
    
    function removeServerGroup(element,id,groupsize){
		if(groupsize == 0) {
			if (confirm("You are going to delete server group, Do you want to continue?")) {
				$(element).attr('href', "<%=basePath%>/serverGroupManagement.do?method=delete&serverGroupId=" + id);
			}
		} else {
			alert("Failed to delete Server Group. \nReason: Server Insatnces are configured in Server Group.");
		}
    }
  
</script>
<%
	ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm) request.getAttribute("serverInstanceGroupForm");
    List groupDatas = serverInstanceGroupForm.getServerInstanceGroupDataList();
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%" >
    <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
    <tr>
        <td width="10">&nbsp;</td>
        <td width="100%" colspan="2" valign="top" class="box">
            <table cellSpacing="0" cellPadding="0" width="100%" border="0">
                <tr>
                    <td colspan="3">
                        <table width="100%" align="left" border="0" >
                            <%
                                if(serverInstanceGroupForm.getActionName()!=null && serverInstanceGroupForm.getActionName().equalsIgnoreCase(BaseConstant.LISTACTION)){
                            %>
                            <html:form action="/serverGroupManagement.do">
                                <tr>
                                    <td colspan="3">
                                        <table cellSpacing="0" cellPadding="0" width="100%" border="0"  >
                                            <tr>
                                                <td class="table-header" width="50%" ><bean:message bundle="serverGroupDataMgmtResources" key="group.search.list" /></td>
                                            </tr>
                                            <tr>
												<td colspan="4" class=" labeltext">
													<div id="dashboard" class="dashboard">
														<div>
														<div class="col-sm-12" style="padding-bottom: 5px;padding-top: 5px;">
															<a class="btn btn-primary btn-sm" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='<%=basePath%>/serverGroupManagement.do?method=initCreate'">
																<span class="glyphicon glyphicon-plus-sign" title="Add"></span> 
																<b><bean:message bundle="serverGroupDataMgmtResources" key="group.create.title" /></b>
															</a>
														</div>
														</div>
									                     <logic:notEqual value="null" name="groupDatas">
									                     	<div id="sortable" class="col-sm-12">
									                     	<logic:iterate id="groupData" name="serverInstanceGroupForm" property="serverInstanceGroupDataList" type="com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupData">
																<div class="ServerGroups" id="<bean:write name='groupData' property='id' />" >
																	<div class="panel panel-primary">
																		<div class="panel-heading heading">
																			<span style="font-weight: bold;font-size: 12px"> 
																				<a href="<%=basePath%>/serverGroupManagement.do?method=view&serverGroupId=<bean:write name="groupData" property="id"/>" tabindex="8" class="heading-value">
										                                        	<bean:write name="groupData" property="name" />
										                                        </a>
										                                    </span>
																			<div class="btn-group pull-right" style="margin-top: -4px; margin-right: -1px;">
																				<a href="<%=basePath%>/serverGroupManagement.do?method=initUpdate&serverGroupId=<bean:write name='groupData' property='id' />" class="btn btn-default btn-sm group_right_btns"><span class="glyphicon glyphicon-pencil"></span></a>
																				<bean:size id="size" name="groupData" property="serverInstanceGroupRelationForms"/>
																				<a onclick="removeServerGroup(this,'<bean:write name='groupData' property='id' />',<bean:write name="size" />)" class="btn btn-default btn-sm group_right_btns"><span class="glyphicon glyphicon-trash"></span></a>
																			</div>
																		</div>
																		<div class="panel-body">
																			<logic:present name="groupData" property="serverInstanceGroupRelationForms" >
																				<div class="col-sm-12" style="padding-bottom: 2px;">
																					<logic:equal value="2" name="size" >
																						<a class="btn btn-primary btn-xs disabled" style="padding-top: 3px; padding-bottom: 3px" >
																							<span class="glyphicon glyphicon-plus-sign" title="Add"></span> 
																							<b><bean:message bundle="serverGroupDataMgmtResources" key="group.addserver" /></b>
																						</a>
																						<a class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="swapInstances('<bean:write name="groupData" property="id"/>')">
																							<span class="glyphicon glyphicon-sort" title="Swap"></span>
																							<b><bean:message bundle="serverGroupDataMgmtResources" key="group.swapRole" /></b>
																						</a>

																					</logic:equal>
																					<logic:notEqual value="2" name="size" >
																						<a class="btn btn-primary btn-xs " style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='<%=basePath%>/initCreateServer.do?groupId=<bean:write name="groupData" property="id"/>'">
																							<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
																							<b><bean:message bundle="serverGroupDataMgmtResources" key="group.addserver" /></b>
																						</a>
																						<a class="btn btn-primary btn-xs disabled" style="padding-top: 3px; padding-bottom: 3px">
																							<span class="glyphicon glyphicon-sort" title="Swap"></span>
																							<b><bean:message bundle="serverGroupDataMgmtResources" key="group.swapRole" /></b>
																						</a>

																					</logic:notEqual>

																					<%--Session sync is disabled, So commenting it.--%>
																					<%--<div class="btn-group btn-group-xs">

																						<span id="inMemorySession" class="btn btn-default"><bean:message bundle="serverGroupDataMgmtResources" key="group.session.synchronization" /></span>
																						<logic:equal value="true" name="groupData" property="sessionSynchronization">
																							<button style="padding: 3px 5px"  type="button" class="btn btn-primary" onclick="changeMode(this,true,<bean:write name="groupData" property="sessionSynchronization" />,'<bean:write name="groupData" property="id" />');"><bean:message bundle="serverGroupDataMgmtResources" key="group.session.synchronization.on" /></button>
																							<button style="padding: 3px 5px" type="button" class="btn btn-default active" onclick="changeMode(this,false,<bean:write name="groupData" property="sessionSynchronization" />,'<bean:write name="groupData" property="id" />');" ><bean:message bundle="serverGroupDataMgmtResources" key="group.session.synchronization.off" /></button>
 																						</logic:equal>
																						<logic:equal value="false" name="groupData" property="sessionSynchronization">
																							<button style="padding: 3px 5px" type="button" class="btn btn-default active" onclick="changeMode(this,true,<bean:write name="groupData" property="sessionSynchronization" />,'<bean:write name="groupData" property="id" />');"><bean:message bundle="serverGroupDataMgmtResources" key="group.session.synchronization.on" /></button>
																							<button style="padding: 3px 5px" type="button" class="btn btn-primary" onclick="changeMode(this,false,<bean:write name="groupData" property="sessionSynchronization" />,'<bean:write name="groupData" property="id" />');" ><bean:message bundle="serverGroupDataMgmtResources" key="group.session.synchronization.off" /></button>
																						</logic:equal>
																					</div>--%>
																				</div>
																				<logic:greaterThan value="0" name="size">
																					<div class="col-sm-12" style="padding-bottom: 1px;">
																						<table width="100%" border="0" cellpadding="0" cellspacing="0" class="manageorder" id="tbl<bean:write name="groupData" property="id"/>" >
																							<thead>
																								<tr>
																									<th align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="serverGroupDataMgmtResources" key="group.instance.role" /></th>
																									<th align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="serverGroupDataMgmtResources" key="group.name" /></th>
																									<th align="left" class="tblheader" valign="top" width="30%"><bean:message bundle="serverGroupDataMgmtResources" key="group.description" /></th>										
																									<th align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="serverGroupDataMgmtResources" key="group.instance.ip" /></th>
																									<th align="left" class="tblheader" valign="top" width="2%"></th>																				
																								</tr>
																							</thead>
																							<tbody>
																								<logic:iterate name="groupData" property="serverInstanceGroupRelationForms" id="serverInstanceGroupRelationData" type="com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupRelationForm">
																									<tr id="<bean:write name="serverInstanceGroupRelationData" property="serverWeightage"/>">
																										<logic:equal value="<%=String.valueOf(CommonConstants.PRIMARY_INSTANCE)%>" name="serverInstanceGroupRelationData" property="serverWeightage">
																											<td align="left" class="tblfirstcol" valign="top" width="15%">Primary</td>
																										</logic:equal>
																										<logic:equal value="<%=String.valueOf(CommonConstants.SECONDARY_INSATNCE)%>" name="serverInstanceGroupRelationData" property="serverWeightage">
																											<td align="left" class="tblfirstcol" valign="top" width="15%">Secondary</td>
																										</logic:equal>
																										<td align="left" class="tblrows" valign="top" width="25%"><a href="<%=basePath%>/viewNetServerInstance.do?netserverid=<bean:write name="serverInstanceGroupRelationData" property="netServerId"/>"><bean:write name="serverInstanceGroupRelationData" property="serverName"/></a></td>
																										<td align="left" class="tblrows" valign="top" width="30%"><bean:write name="serverInstanceGroupRelationData" property="description"/></td>										
																										<td align="left" class="tblrows" valign="top" width="25%"><bean:write name="serverInstanceGroupRelationData" property="adminHost"/>:<bean:write name="serverInstanceGroupRelationData" property="adminPort"/></td>
																										<td align="left" class="tblrows" valign="top" width="2%"><a onclick="removeInstance(this,'<bean:write name='serverInstanceGroupRelationData' property='netServerId' />','<bean:write name='groupData' property='id' />')" class="btn btn-default btn-xs instance_delete_btn"><span class="glyphicon glyphicon-trash"></span></a></td>
																									</tr>
												 												</logic:iterate>
																							</tbody>
																						</table>
																					</div>
																				</logic:greaterThan>
																			</logic:present>
																		</div>
																	</div>
																</div>
															</logic:iterate>
															</div>
														</logic:notEqual>
													</div>
												</td>
											</tr>
                                            <tr class="vspace">
                                                <td class="btns-td" valign="middle" colspan="2" ></td>
                                            </tr>
                                        </table>
                                     </td>
                                </tr>
                            </html:form>
                            <% 	} %>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>