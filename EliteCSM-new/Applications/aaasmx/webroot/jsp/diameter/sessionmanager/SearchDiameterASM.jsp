<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"%>
<%@page import="com.elitecore.elitesm.web.diameter.sessionmanager.form.SearchDiameterASMForm"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup"%>
<%@page import="com.elitecore.elitesm.web.radius.radiuspolicygroup.forms.RadiusPolicyGroupForm"%>
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
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.util.constants.RadiusPolicyConstant"%>

<%
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

    SearchDiameterASMForm searchDiameterASMForm = (SearchDiameterASMForm)request.getAttribute("searchDiameterASMForm");
    List listDiameterActiveSession = searchDiameterASMForm.getAsmList();
     
    long pageNo = searchDiameterASMForm.getPageNumber();
    long totalPages = searchDiameterASMForm.getTotalPages();
    long totalRecord = searchDiameterASMForm.getTotalRecords();
	int count=1;
     
    String strPageNumber = String.valueOf(pageNo);     
    String strTotalPages = String.valueOf(totalPages);
    String strTotalRecords = String.valueOf(totalRecord);
    long iIndex = ((pageNo-1)*pageSize);
%>

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
var databaseId = '<%= diameterSessionManagerDataObj.getDatabaseDatasourceId()%>';
var tablename = '<%= diameterSessionManagerDataObj.getTableName()%>';

function validateSearch(){
	
	if( isNull($('#searchColumns').val() )){
		$('#searchColumns').focus();
		alert('No Viewable columns found');
	}else if( checkForDuplicate()){
		alert('Duplicate column name found in search column');
		return false;
	}else{
		var searchcolumn = $('#searchColumns').val();
		searchcolumn = searchcolumn.replace(/,\s*$/, "");
		$('#searchColumns').val(searchcolumn);
		document.searchDiameterASMForm.pageNumber.value = 1;
		document.searchDiameterASMForm.submit();
	}
}

function checkForDuplicate(array) {
	var array = $('#searchColumns').val().split(",");
    var valuesSoFar = {};
    for (var i = 0; i < array.length; ++i) {
        var value = array[i];
        if (Object.prototype.hasOwnProperty.call(valuesSoFar, value)) {
            return true;
        }
        valuesSoFar[value] = true;
    }
    return false;
}

function closedSelectedSession(){
	document.searchDiameterASMForm.action.value = 'closeSelectedSession';
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
			alert("Selection Required To Perform Close Session Operation.");
		}else{
			var r=confirm("This will delete session from database. Do you want to continue ?");
				if (r==true)
	  			{
	  				document.forms[0].submit();
	  			}
		}
	}else{
		alert("No Records Found For Delete Operation! ");
	}
}

function closeAllSession(){
	document.searchDiameterASMForm.action.value = 'closeAllSession';
	var r=confirm("This will delete all the display session from database. Do you want to continue ?");
	if ( r == true ){
			var arrayCheck = document.getElementsByName('select');
			for (i = 0; i < arrayCheck.length;i++)
				arrayCheck[i].checked = true ;
			document.forms[0].submit();
	}
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(pageNumber){
	document.searchDiameterASMForm.pageNumber.value = pageNumber;
	document.searchDiameterASMForm.submit();
}

function  checkAll(checkbox){

	if($(checkbox).attr('checked')){
		$('input:checkbox[name="select"]').each(function(){
			$(this).attr('checked',true);
		});
	}else{
		$('input:checkbox[name="select"]').each(function(){
			$(this).attr('checked',false);
		});
	}
}
function resetSearchColumns(){
	$('#searchColumns').val('');
}

$(document).ready(function(){
	retriveTableFieldsForSessionManager();
	
	<% if(searchDiameterASMForm.getSearchColumns() != null && searchDiameterASMForm.getSearchColumns().length() > 0) {%>
		$('#searchColumns').val(<%=searchDiameterASMForm.getSearchColumns()%>);
	<%}%>
});

function retriveTableFieldsForSessionManager() {
	var dbFieldStr;
	var dbFieldArray = new Array();
	$.post("FieldRetrievalServlet", {databaseId:databaseId,tblName:tablename}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = dbFieldStr.split(", ");
		var index = dbFieldArray.indexOf('CONCUSERID');
		dbFieldArray.splice(index, 1);
		index = dbFieldArray.indexOf('PROTOCOLTYPE');
		dbFieldArray.splice(index, 1);
		setSearchColumns("searchColumns",dbFieldArray);
	});	
	return dbFieldArray;
}

function setSearchColumns(txtField,myArray) {
	 $( "#"+ txtField ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
	 }).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			response( $.ui.autocomplete.filter(
				myArray, extractLast( request.term ) ) );
		},
		focus: function() {
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
			 this.value = val + ui.item.label ;
			return false;
		}
	});
}

function openView(sessionId){
	$.ajax({url:'<%=request.getContextPath()%>/viewdiameterASMDetails.do',
         type:'POST',
         data:{sessionId:sessionId,tablename:tablename},
         async:false,
         success: function(activeSessionObject){
        	 $('#viewDiv').html('');
        	 var tableData = "<table width='100%' cellspacing='0' cellpadding='0' border='0'>"+
        					"<tr><td class='tblheader' width='50%'>Field Name</td><td class='tblheader' width='50%'> Field Value</td></tr>";
        	var activeSessionObject = $.parseJSON(activeSessionObject);
        	var testArray = {};
        	$(activeSessionObject).each(function(key,value){
        		$.each( value, function( fieldId, fieldvalue ){
        			tableData +="<tr><td class='labeltext allborder'>"+fieldId+"</td><td class='labeltext tblrows'>"+fieldvalue+"</td></tr>"
    			});
        	});
        	tableData+="</table>"
        	$('#viewDiv').html(tableData);
         }
    });
	openPopup();
	$("#viewDiv").css({height:"300px", overflow:"auto"});
}
function openPopup(){
	document.getElementById('viewDiv').style.visibility = "visible";
	$( "#viewDiv" ).dialog({
		width: 500,
		resizable: false,
		open: function () {
        }					       
    });					
}
setTitle('<bean:message bundle="sessionmanagerResources" key="sessionmanager.header"/>');

</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" >
									<bean:message bundle="diameterResources" key="diameter.searchasm.title"/>
								</td>
							</tr>
							<tr>
								<td class="small-gap" >&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0">
										<html:form action="/searchDiameterASM">
											<html:hidden name="searchDiameterASMForm" styleId="action" property="action" />
											<html:hidden name="searchDiameterASMForm" styleId="pageNumber" property="pageNumber" />
											<html:hidden name="searchDiameterASMForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="searchDiameterASMForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
											<html:hidden name="searchDiameterASMForm" styleId="sessionManagerId" property="sessionManagerId" value="<%=String.valueOf(searchDiameterASMForm.getSessionManagerId()) %>" />
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="diameterResources" key="diameter.searchasm.searchcolumnname" />
													<ec:elitehelp headerBundle="diameterResources" text="diameter.searchasm.searchcolumnname" header="diameter.searchasm.searchcolumnname"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="searchColumns" property="searchColumns" size="30" maxlength="1000" tabindex="1" style="width:250px;"/>
												</td>
											</tr>
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" name="Search" width="5%" name="RadiusPolicyName" tabindex="5" onclick="validateSearch()" value="   Search   " class="light-btn" /> 
													<input type="button" name="Reset" tabindex="6" value="   Reset   " 	onclick="resetSearchColumns()" class="light-btn">
												</td>
											</tr>

											<%
												if(searchDiameterASMForm.getAction()!=null && searchDiameterASMForm.getAction().equalsIgnoreCase(RadiusPolicyConstant.LISTACTION)){
											%>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message bundle="diameterResources" key="diameter.searchasm.header" /></td>
															<td align="right" class="blue-text" valign="middle"
																width="50%">
																<%
																	if(totalRecord == 0) {
																%> <%
																 	}else if(pageNo == totalPages+1) {
																 %>
																[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%=totalRecord%>
																<%
																	} else if(pageNo == 1) {
																%> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
																of <%=totalRecord%> <%
																 	} else {
																 %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
																																of <%=totalRecord%> <%
																 	}
																 %>
															</td>
														</tr>
														<tr>
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" style="padding-top: 5px;padding-bottom: 3px;" width="70%">
																<html:button property="c_btnDelete" onclick="closedSelectedSession()" value="Closed Selected Session" styleClass="light-btn" />
																<html:button property="c_btnDelete" onclick="closeAllSession()" value="Closed All Session" styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right" width="30%">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" colspan="2">
																<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
																	<tr>
																		<td align="center" class="tblheader" valign="top" width="1%">
																			<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll(this);" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="2%">
																			<bean:message bundle="radiusResources" key="radiuspolicy.serialnumber" />
																		</td>
																		<% 
																		    int columnWidth = 90;
																			String searchColumnsHeader = searchDiameterASMForm.getSearchColumns();
																			if( searchColumnsHeader != null && searchColumnsHeader.length() > 0){
																				String[] searchColumnHeaderArray = searchColumnsHeader.split(","); 
																				int totalColumnFound = searchColumnHeaderArray.length;
																				columnWidth = 90 / (totalColumnFound+1);
																				for(String columnName : searchColumnHeaderArray ){ %>
																				<td align="left" class="tblheader" valign="top" width="<%=columnWidth%>%">
																					<%=columnName%>&nbsp;
																				</td>
																			<%}%>
																			<td align="left" class="tblheader" valign="top" width="<%=columnWidth%>%">
																				PROTOCOLTYPE
																			</td>
																			<%}else{%>
																				<td align="center" class="tblheader" valign="top" width="*">
																					&nbsp;
																				</td>
																			<%} %>
																		<td align="center" class="tblheader" valign="top" width="2%">
																			<bean:message bundle="diameterResources" key="diameter.searchasm.view" />
																		</td>
																	</tr>
																	 <%
																		if(listDiameterActiveSession!=null && listDiameterActiveSession.size() >0 ){
																	 %>
																	<logic:iterate id="asmListBean" name="searchDiameterASMForm" property="asmList" >
																		<% String searchColumns = searchDiameterASMForm.getSearchColumns();
																			if( searchColumns != null && searchColumns.length() > 0){
																				String[] searchColumnArray = searchColumns.split(","); %>
																				<tr>
																					<td align="center" class="tblfirstcol" width="1%">
																						<input type="checkbox" name="select" value="<bean:write name="asmListBean" property="CONCUSERID"/>" />
																					</td>
																					<td align="center" class="tblrows" width="2%"><%=((pageNo-1)*pageSize)+count%></td>
																					<%for( String columnName : searchColumnArray){ %>
																						<td align="left" class="tblrows">
																							<% if( columnName != null && columnName.length() > 0){ %>
																									<bean:write name="asmListBean" property="<%=columnName %>"/> &nbsp;
																							<%}else{%>
																								&nbsp;
																							<%} %>
																						</td>
																					<%}%>
																					<td align="left" class="tblrows">
																						<bean:write name="asmListBean" property="PROTOCOLTYPE"/>&nbsp;
																					</td>
																					<td align="center" class="tblrows" valign="top" width="2%">
																						<a href="javascript:void(0)" onclick="openView(<bean:write name="asmListBean" property="CONCUSERID"/>);">
																							<img src="<%=basePath%>/images/view-info.jpg" alt="View Details" border="0"/>
																						</a>
																					</td>
																				</tr> 
																				<% count=count+1; %>
																				<% iIndex += 1; %>
																			<%}
																		%>
																	</logic:iterate>
																	<%	}else{	%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">
																			No Records Found.
																		</td>
																	</tr>
																	<%	}%> 
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" style="padding-top: 5px;" width="70%">
																<html:button property="c_btnDelete" onclick="closedSelectedSession()" value="Closed Selected Session" styleClass="light-btn" />
																<html:button property="c_btnDelete" onclick="closeAllSession()" value="Closed All Session" styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right" width="30%">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="#" onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="#" onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="#" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="#" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
													</table>
											</tr>
										<%}%>
										</html:form>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table style="display: none;" id="viewTemplateTable">
	<tr>
		<td class="allborder first-col" width="50%"></td>
		<td class="tblrows second-col" width="50%"></td>
	</tr>
</table>
<table style="display: none;" id="viewtable">
	<tr>
		<td class="tblheader" width="50%">Name</td>
		<td class="tblheader" width="50%">Value</td>
	</tr>
</table>
<div id="viewDiv" style="display: none;" title="Session Details">
</div>

