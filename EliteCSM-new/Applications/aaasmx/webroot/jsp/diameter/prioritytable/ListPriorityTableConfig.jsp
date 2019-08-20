<%@page import="com.elitecore.elitesm.datamanager.diameter.prioritytable.data.PriorityTableData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import ="com.elitecore.diameterapi.core.common.transport.priority.Priority" %>	
<%@page import ="com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.DiameterSessionTypes" %>	
<%@page import="com.elitecore.elitesm.web.diameter.prioritytable.form.PriorityTableForm"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<style type="text/css">
.checkboxClass{
	font-family: Arial;
}
</style>

<%
 PriorityTableForm  priorityTableConfigForm= (PriorityTableForm)request.getAttribute("priorityTableConfigForm"); 
 List<PriorityTableData> lstPriorityTableData = (List<PriorityTableData>)request.getAttribute("lstPriorityTableData");
%>

<script type="text/javascript">
var priorityTableIndex=0;

var commandCodeList = [];
var applicationIdList =[];

<%for(CommandCode commandCode:CommandCode.VALUES){%>
	commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>'});
<%}%>


<%for(ApplicationIdentifier applicationId:ApplicationIdentifier.VALUES){%>
applicationIdList.push({'value':'<%=applicationId.applicationId%>','label':'[<%=applicationId.applicationId%>] <%=applicationId.application%>'});
<%}%>



function checkMappingIndexForUpdate(mappingIndex){
	if(priorityTableIndex!=0){
	document.getElementById("rowSubmit").hidden = false;
	}
	
};

function setSuggestionsForAutoComplete(styleClass,dataArray) {
		$( "."+styleClass).autocomplete({	
		source:function( request, response ) {
			response( $.ui.autocomplete.filter(
					dataArray, extractLastData( request.term ) ) );
		},
			
		focus: function( event, ui ) {
			return false;
		},
		select: function( event, ui ) {
			var val = this.value;
			var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
			var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
			 if(commaIndex == semiColonIndex) {
					val = "";
			}  else if(commaIndex > semiColonIndex) {
					val = val.substring(0,commaIndex+1); 
			} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
				val = val.substring(0,semiColonIndex+1); 
			}	 
			this.value = val + ui.item.value ;
			return false;
		}
	});		
} 


function ListApplcationItem(value,label) {
this.value = value;
this.label = label;
}

function splitData( val ) {
return val.split( /[,;]\s*/ );
}

function extractLastData( term ) {
return splitData( term ).pop();
}

function addNewMapping(tableId){
	priorityTableIndex = priorityTableIndex + 1;
	var priorityTableId = "priorityTableId";
	var applicationId = "applicationId";
	var commandCode = "commandCode";
	var ipAddress = "ipAddress";
	var newSession = "newSession";
	var priority = "priority";
	var newSessionId = "newSessionId" + priorityTableIndex;
	$("#"+tableId+" tr:last").after("<tr>"+
									"<input type='hidden' name="+priorityTableId+" />"+ 
									"<td class='tblfirstcol'><textarea class='applicationClass' name="+applicationId+"  style='min-width:99%;min-height:24px;height:24px;width:100%;' rows='1' /></td>"+
									"<td class='tblrows'> <textarea class='commandCodeClass' name="+commandCode+"  style='min-width:99%;min-height:24px;height:24px;width:100%;' rows='1'/> </td>"+
									"<td class='tblrows'> <textarea  name="+ipAddress+" class='ipAddressClass'  style='min-width:99%;min-height:24px;height:24px;width:100%;' rows='1' /> </td>"+
									"<td class='tblrows'><select name="+newSession+"  style='min-width:99%;min-height:22px;width:100%;'>"+
									'<%for(DiameterSessionTypes diameterSession : DiameterSessionTypes.values()){%>'
									+'<option value="<%=((DiameterSessionTypes)diameterSession).val%>"><%=((DiameterSessionTypes)diameterSession).type%></option>'
									+'<%}%>'
									+"</select></td>"+
									"<td class='tblrows'> <select name="+priority+" style='min-width:99%;min-height:22px;width:100%;'>"+
									'<%for(Priority priorityData : Priority.values()){%>'
									+'<option value="<%=((Priority)priorityData).val%>"><%=((Priority)priorityData).priority%></option>'
									+'<%}%>'
									+"</select></td>"+
									"<td class='tblrows' align='center'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
									"</tr>");
	addMappingIndex();
	checkMappingIndexForUpdate(priorityTableIndex);
	setSuggestionsForAutoComplete("commandCodeClass", commandCodeList);
	setSuggestionsForAutoComplete("applicationClass",applicationIdList);
};

	function validateApplicationId() {
	var flag = true;
	
		$("#priorityTable tr").each(function(){
			var applicationId = $(this).find('.tblfirstcol').find('textarea').val();
			 if( applicationId ){
				if(applicationId.length == 0 && applicationId == '') {
					alert("Application Id must be specified");
					applicationId.focus();
					return false;
					flag = false;
				} else if(!isNumber(applicationId)) {
					alert("Application Id must be numeric");
					applicationId.focus();
					return false;
					flag = false;
				}
			} 
			 
			var commandCode = $(this).find('.tblrows').find('.commandCodeClass').val();
			if(commandCode){
					if(!isNumber(commandCode)) {
						alert("Commnad Code Id must be numeric");
						commandCode.focus();
						flag = false;
						return false;
					}
			}  
			
			var ipAddress = $(this).find('.tblrows').find('.ipAddressClass').val();
			if(ipAddress){
				if(!validateIPAddress(ipAddress)) {
					alert("IP Address "+ipAddress+" Must be Valid");
					ipAddress.focus();
					flag = false;
					return false;
				}
			}
		});
		return flag;
}
	
	function checkDuplicateMapping() {

		var matches = true;
		$("#priorityTable tr").each(function(){

			var parentRowIndex = $('#priorityTable tr').index(this);
			if(parentRowIndex >= 1){
				var appId = $(this).find('.tblfirstcol').find('textarea').val();
				var cmdCode = $(this).find('.tblrows').find('.commandCodeClass').val();
				var ipAdrrs = $(this).find('.tblrows').find('.ipAddressClass').val();

				$("#priorityTable tr").each(function(){

					var childRowIndex = $('#priorityTable tr').index(this);
				
					if(childRowIndex > parentRowIndex){
					
						var applicationId = $(this).find('.tblfirstcol').find('textarea').val();
						var commandCode = $(this).find('.tblrows').find('.commandCodeClass').val();
						var ipAddress = $(this).find('.tblrows').find('.ipAddressClass').val();
					
						if(applicationId && commandCode && ipAddress){
							if(applicationId == appId && commandCode == cmdCode && ipAddress == ipAdrrs){
								alert("Duplicate entry found for ApplicationId "+ appId + " Command Code "+ cmdCode + " IP Address "+ ipAdrrs);
								matches = false;	
							}
						}
					}
				});
			}
		});
		return matches;
	}

	function validateIPAddress(ipaddress){
		
		var ip=/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
			if(ip.test(ipaddress)){
				   return true;
		}else{			
				   return false;
		}				
	}

function submitForm(){
	
	if(validateApplicationId()){
		if(checkDuplicateMapping()){
			document.forms[0].action.value = "update";
			document.forms[0].action = '<%=basePath%>/updatePriorityTableConfig.do';
			document.forms[0].submit();
		}
	} 
};

$(document).ready(function(){
	
	var index = 0;
	<%if(lstPriorityTableData != null && lstPriorityTableData.isEmpty() == false ){%>
			var priorityTableIdData;
			var applicationIdData ;
			var commandCodeData ;
			var ipAddressData ;
			var newSessionData ;
			var priorityData ;
			<%for(PriorityTableData priorityData : lstPriorityTableData){%>
				applicationIdData = '' ;
				commandCodeData = '';
				ipAddressData = '';
				newSessionData = '';
				priorityData = '';	
				priorityTableIdData = '';
				priorityTableIdData = '<%=priorityData.getPriorityTableId()%>';
				<%if(priorityData.getApplicationId()!=null){%>
				applicationIdData = '<%=priorityData.getApplicationId()%>';
				<%}%>
				<%if(priorityData.getCommandCode()!=null){%>
				commandCodeData = '<%=priorityData.getCommandCode()%>';
				<%}%>
				<%if(priorityData.getIpAddress()!=null){%>
				ipAddressData = '<%=priorityData.getIpAddress()%>';
				<%}%>
				newSessionData = '<%=priorityData.getDiameterSession()%>';
				priorityData = '<%=priorityData.getPriority()%>';
				index = index + 1;
				getPriorityTableData(priorityTableIdData,applicationIdData,commandCodeData,ipAddressData,newSessionData,priorityData,index);				
				priorityTableIndex = index;
		<%}%>
			
	<%}%>
	addMappingIndex();
});

function addMappingIndex() {
	$("#mainform").append("<input type='hidden' name='priorityTableIndex' value='"+priorityTableIndex+"' />");
}

function getPriorityTableData(priorityTableIdData,applicationIdData,commandCodeData,ipAddressData,newSessionData,priorityData,index){
	priorityTableIndex = index;
	var priorityTableId = "priorityTableId";
	var applicationId = "applicationId";
	var commandCode = "commandCode";
	var ipAddress = "ipAddress";
	var newSession = "newSession";
	var priority = "priority";
	var priorityId = "priorityId" + index; 
	var newSessionId = "newSessionId" + index;
	$("#priorityTable tr:last").after("<tr>"+
									"<input type='hidden' name="+priorityTableId+" value="+priorityTableIdData+" />"+ 
									"<td class='tblfirstcol'><textarea class='applicationClass' name="+applicationId+"  style='min-width:99%;min-height:24px;height:24px;width:100%;' rows='1' >"+applicationIdData+"</textarea></td>"+
									"<td class='tblrows'> <textarea class='commandCodeClass' name="+commandCode+"  style='min-width:99%;min-height:24px;height:24px;width:100%;' rows='1'>"+commandCodeData+"</textarea> </td>"+
									"<td class='tblrows'> <textarea  name="+ipAddress+" class='ipAddressClass' style='min-width:99%;min-height:24px;height:24px;width:100%;' rows='1' >"+ipAddressData+"</textarea> </td>"+
									"<td class='tblrows' align='center'><select name="+newSession+" id="+newSessionId+"  style='min-width:99%;min-height:22px;width:100%;'>"+
									'<%for(DiameterSessionTypes diameterSession : DiameterSessionTypes.values()){%>'
									+'<option value="<%=((DiameterSessionTypes)diameterSession).val%>"><%=((DiameterSessionTypes)diameterSession).type%></option>'
									+'<%} %>'
									+"</select></td>"+
									"<td class='tblrows'> <select name="+priority+" id="+priorityId+" style='min-width:99%;min-height:22px;width:100%;'>"+
									'<%for(Priority priorityData : Priority.values()){%>'
									+'<option value="<%=((Priority)priorityData).val%>"><%=((Priority)priorityData).priority%></option>'
									+'<%} %>'
									+"</select></td>"+
									"<td class='tblrows' align='center'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
									"</tr>");
	$("#" + priorityId).val(priorityData);
	$("#" + newSessionId).val(newSessionData);
	checkMappingIndexForUpdate(priorityTableIndex);
	setSuggestionsForAutoComplete("commandCodeClass", commandCodeList);
	setSuggestionsForAutoComplete("applicationClass",applicationIdList);
};

setTitle('<bean:message bundle="diameterResources" key="prioritytable.title"/>');
</script>
</head>

<html:form action="/updatePriorityTableConfig">
<html:hidden name="priorityTableConfigForm"
			styleId="action" property="action" />
<table cellpadding="0" cellspacing="0" border="0" width="100%">
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
											bundle="diameterResources" key="prioritytable.config" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext"><input type="button"
										class="light-btn" onclick="addNewMapping('priorityTable')"
										value="Add Priority" tabindex="3" /></td>
								</tr>
								<tr>
									<td class="labeltext" width="100%">
										<table id="priorityTable" cellpadding="0" cellspacing="0"
											border="0" width="100%">
											<thead width="100%">
												<tr>

													<td align=left class=tblheader valign=top  width="25%"><bean:message
															bundle="diameterResources"
															key="prioritytable.applicationid" /> <ec:elitehelp
															headerBundle="diameterResources"
															text="prioritytable.applicationid"
															header="prioritytable.applicationid" /></td>
													<td align=left class=tblheader valign=top  width="25%"><bean:message
															bundle="diameterResources"
															key="prioritytable.commandcode" /> <ec:elitehelp
															headerBundle="diameterResources"
															text="prioritytable.commandcode"
															header="prioritytable.commandcode" /></td>
													<td align=left class=tblheader valign=top  width="25%"><bean:message
															bundle="diameterResources" key="prioritytable.ipaddress" />
														<ec:elitehelp headerBundle="diameterResources"
															text="prioritytable.ipaddress"
															header="prioritytable.ipaddress"  /></td>
													<td align=left class=tblheader valign=top  width="10%"><bean:message
															bundle="diameterResources" key="prioritytable.newsession" />
														<ec:elitehelp headerBundle="diameterResources"
															text="prioritytable.newsession"
															header="prioritytable.newsession" /></td>
													<td align=left class=tblheader valign=top  width="10%"><bean:message
															bundle="diameterResources" key="prioritytable.priority" />
														<ec:elitehelp headerBundle="diameterResources"
															text="prioritytable.priority"
															header="prioritytable.priority" /></td>
													<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
												</tr>
											</thead>
											<tbody></tbody>
										</table>
									</td>
								</tr>
								<tr id="rowSubmit" hidden="true">
									<td class="btns-td" align="center" colspan="4"
										style="padding-left: 35"><input type="button"
										name="btnUpdate" onclick="return submitForm();" id="btnUpdate"
										value="Update" class="light-btn" tabindex="6" /> <input
										type="button" name="btnCancel"
										onclick="javascript:window.location.href='<%=basePath%>/updatePriorityTableConfig.do?'"
										value="Reset" class="light-btn" tabindex="7" /></td>
								</tr>

							</table>
						</td>
					</tr>
				</table>
			</td>
	</tr>
</table>
</html:form>

</html>