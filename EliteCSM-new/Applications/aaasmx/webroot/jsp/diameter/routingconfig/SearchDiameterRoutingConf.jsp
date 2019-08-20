<%@page import="com.elitecore.elitesm.util.constants.BaseConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.DiameterConstant"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.util.logger.Logger"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page
	import="com.elitecore.elitesm.util.constants.RadiusPolicyConstant"%>
<%@page
	import="com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm"%>
<%@page
	import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.ResultCode" %>
<%@ page import="com.elitecore.diameterapi.core.stack.constant.OverloadAction" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>

<%
    String basePath = request.getContextPath();
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
     SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = (SearchDiameterRoutingConfForm)request.getAttribute("searchDiameterRoutingConfForm");
	 long pageNo = searchDiameterRoutingConfForm.getPageNumber();
	 long totalPages = searchDiameterRoutingConfForm.getTotalPages();
	 long totalRecord = searchDiameterRoutingConfForm.getTotalRecords();
	 String strPageNumber = String.valueOf(pageNo);     
	 String strTotalPages = String.valueOf(totalPages);
	 String strTotalRecords = String.valueOf(totalRecord);
	 String strAction="";
	 if(searchDiameterRoutingConfForm.getAction()!=null)
	 {
		 strAction=searchDiameterRoutingConfForm.getAction();
	 }		 
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>
<script>

var resultCodeList = [];
var scriptInstanceList = [];

<%for(ResultCode resultCode:ResultCode.VALUES){%>
resultCodeList.push({'value':'<%=resultCode.code%>','label':'[<%=resultCode.code%>] <%=resultCode.name()%>'});
<%}%>

<% 
if( Collectionz.isNullOrEmpty(searchDiameterRoutingConfForm.getScriptDataList()) == false ){
	for( ScriptInstanceData scriptInstData : searchDiameterRoutingConfForm.getScriptDataList()){ %>
		scriptInstanceList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>


function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}

function removeData(){
	document.forms[1].action.value = 'deletetable';  
	var selectArray = document.getElementsByName('select');
	if(selectArray.length>0){
		var b = true;
		for (i=0; i<selectArray.length; i++){
		 	if (selectArray[i].checked == false){  			
		 		 b=false;
		 	}
		 	else{	 		 	
				b=true;
				break;
			}
		}
		
		if(b==false){
			alert("Selection Required To Perform Delete Operation.");
		}else{
			var r=confirm("This will delete the selected items. Do you want to continue ?");
	
			if (r==true){
					document.forms[1].submit();  
	  		}
		}
	}else{
		alert("No Records Found For Delete Operation! ");
	}
}	
function  checkAll(){

var arrayCheck = document.getElementsByName('select');
	if( document.forms[1].toggleAll.checked == true) {
		for (i = 0; i < arrayCheck.length;i++)
			arrayCheck[i].checked = true ;
	} else if (document.forms[1].toggleAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++)
			arrayCheck[i].checked = false ;
	}
}

function showall(){  
	var path = '<%=basePath%>/miscDiameterRoutingConf.do?action=showall';
	window.open(path,'DiameterRoutingTable','resizable=yes,scrollbars=yes');
}

var isValidName;

function customValidate(form)
{
	var overloadAction = $("#ddlOverloadAction").val();
	document.forms[0].actionType.value='create';
	if(validateDiameterRoutingTableForm(form))
    {
	  	if(!isValidName) {
			alert('Enter Valid <bean:message bundle="diameterResources" key="routingconf.tablename"/>.');
			document.forms[0].routingTableName.focus();
			return false;
		}
	  	if(overloadAction == '<%= OverloadAction.REJECT.val%>'){
	  		if(isNull(document.forms[0].resultCode.value)){
	  			alert('Enter Result Code For Overload Action type Reject');
	  			document.forms[0].resultCode.focus();
	  			return false;
	  		}
	  		if(document.forms[0].resultCode.value < 1000 || document.forms[0].resultCode.value > 5999 ){
	  			alert('Invalid Result Code Configured For Reject Overload Action');
	  			document.forms[0].resultCode.focus();
	  			return false;
	  		
	  	}
	  		
	  	}
		return true; 
    }
	else
	{
		return false;
	}	
	return false;
}

$(document).ready(function(){
	$("#ddlOverloadAction").change(function(){
		if($(this).val() ==  '<%= OverloadAction.DROP.val%>'){
			document.getElementById("resultCode").disabled = true;
		}else{
			document.getElementById("resultCode").disabled = false;
			document.getElementById("resultCode").value = "3004";
		}
	});
	setSuggestionForResultCode(resultCodeList);	
	setSuggestionForScript(scriptInstanceList, "scriptInstAutocomplete");
	
});

function setSuggestionForResultCode(resultCodeArray) {
	 $( "#resultCode").autocomplete({	
			source:resultCodeArray
	});
};

setTitle('<bean:message bundle="diameterResources" key="routingconf.title"/>');
</script>
<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblCrossProductList"
										id="c_tblCrossProductList" align="left" border="0"
										cellpadding="0" cellspacing="0">
										<html:javascript formName="diameterRoutingTableForm" />
										<html:form action="/diameterRoutingTable"
											styleId="diameterRoutingTableForm"
											onsubmit="return customValidate(this);">
											<html:hidden name="diameterRoutingTableForm"
												styleId="actionType" property="actionType" />
											<tr>
												<td align="left" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%"
														border="0">
														<tr>
															<td class="table-header" colspan="100%"><bean:message
																	bundle="diameterResources"
																	key="routingconf.create.routingtable.title" /></td>
														</tr>
														<tr>
															<td colspan='100%'>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources"
																	key="routingconf.tablename" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="routingconf.table.name" 
																	header="routingconf.tablename"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:text styleId="routingTableName"
																	property="routingTableName" tabindex="1" size="30"
																	maxlength="128" onkeyup="verifyName();"
																	style="width:250px" /> <font color="#FF0000"> *</font>
																<div id="verifyNameDiv" class="labeltext"></div> </td>
														</tr>
														
														<tr>
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources"
																	key="routingconf.overloadaction" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="routingconf.overloadaction" 
																	header="routingconf.overloadaction"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:select styleId="ddlOverloadAction"
																	property="overloadAction" tabindex="1"
																	style="width:250px" >
																	<% for(OverloadAction overloadAction : OverloadAction.values()) { %>
																	<html:option value="<%=overloadAction.val%>"><%=overloadAction.val%></html:option>
																	<%} %>
																	
																	</html:select> 
																 </td>
														</tr>
														
														<tr id="resultCodeRow">
														
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources"
																	key="routingconf.resultcode" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="routingconf.resultcode" 
																	header="routingconf.resultcode"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:text styleId="resultCode" styleClass="autoSuggestCode" disabled="true"
																	property="resultCode"  tabindex="1" size="30"
																	maxlength="128" 
																	style="width:250px" /> <font color="#FF0000"> *</font>
																 </td>
														</tr>
														<tr>
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources" key="routingconf.routingscript" /> 
																<ec:elitehelp headerBundle="diameterResources" text="routingconf.routingscript" header="routingconf.routingscript"/>
															</td>
															<td align="left" class="labeltext" valign="top" width="22%">
																<html:text styleId="routingScript" property="routingScript" style="width:250px" styleClass="scriptInstAutocomplete"  />
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td style="padding-left: 5px;"><input
																type="submit" name="Create" tabindex="2"
																value="   Create   " class="light-btn"></td>
														</tr>
													</table>
												</td>
											</tr>
										</html:form>
										<tr>
											<td align="left" colspan="100%" valign="top">
												<%
												 	             if(strAction.equalsIgnoreCase(DiameterConstant.LISTACTION)){
												 	            %> <%@ include
													file="ListDiameterRoutingTable.jsp"%>
												<%	 
												 	             }
												              	%>
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

<script language="javascript">
	
  
  function navigate(pageNo){
	  if('<%=strAction%>' == '<%=DiameterConstant.LISTACTION%>')
		 {
		     document.diameterRoutingTableForm.actionType.value='pagination'; 
			 document.diameterRoutingTableForm.action="diameterRoutingTable.do?&pageNo="+pageNo;
			 document.diameterRoutingTableForm.submit();  
		 }
	     else if('<%=strAction%>' == '<%=BaseConstant.LISTACTION%>')
	  	 {
	    	 document.forms[0].action="searchDiameterRoutingConf.do?&pageNo="+pageNo;
	    	 document.forms[0].submit();    		    	 
	  	 } 
  }
  
  function verifyName() { 
	  var searchName = document.getElementById("routingTableName").value;
	  isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_ROUTING_TABLE%>',searchName,'create','','verifyNameDiv');
	}
  
</script>