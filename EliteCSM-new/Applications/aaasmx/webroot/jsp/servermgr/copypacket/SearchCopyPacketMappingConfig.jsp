<%@page import="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData"%>
<%@page import="com.elitecore.elitesm.web.servermgr.copypacket.forms.SearchCopyPacketMappingConfigForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="java.util.*"%>

<%
	String basePath=request.getContextPath();
	SearchCopyPacketMappingConfigForm configForm = (SearchCopyPacketMappingConfigForm)request.getAttribute("searchConfigForm");
	List mappingConfigList = configForm.getConfigList();
	String action = configForm.getAction();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	String status = configForm.getStatus();
	
	long pageNo = configForm.getPageNumber();
	long totalPages = configForm.getTotalPages();
	long totalRecord = configForm.getTotalRecords();
	int count = 1;
	
	String strPageNumber = String.valueOf(pageNo);     
	String strTotalPages = String.valueOf(totalPages);
	String strTotalRecords = String.valueOf(totalRecord);
	String strName=configForm.getName() != null ? configForm.getName() :"";
	String paramString="name="+strName;
	
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
function validateSearch(){
	document.forms[0].submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].submit();
}

function removeData(){
    var selectVar = false;
    
    for (i=0; i < document.forms[0].elements.length; i++){
        if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[0].elements[i].checked == true){
            	   selectVar = true;
            }
             
        }
    }
    if(selectVar == false){
        alert('At least select one Copy Packet Configuration for removal');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.copypacket.delete.confirmation"/>';              
        var agree = confirm(msg);
        if(agree){
       	    document.forms[0].action.value = 'delete';
        	document.forms[0].submit();
        }
    }
}
function  checkAll(){
	 	if( document.forms[0].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('select');
		 	for (i = 0; i < selectVars.length;i++)
				selectVars[i].checked = true ;
	    } else if (document.forms[0].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('select');	    
			for (i = 0; i < selectVars.length; i++)
				selectVars[i].checked = false ;
		}
}

	function navigate(){
		window.location.href ='<%=basePath%>'+'/initCreateCopyPacketMappingConfig.do';
	}
	setTitle('<bean:message bundle="servermgrResources" key="copypacket.title"/>');
</script>
<html:form action="/searchCopyPacketMappingconf" >


	<html:hidden name="searchCopyPacketMappingConfigForm" styleId="action"
		property="action" />
	<html:hidden name="searchCopyPacketMappingConfigForm"
		styleId="pageNumber" property="pageNumber" />
	<html:hidden name="searchCopyPacketMappingConfigForm"
		styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="searchCopyPacketMappingConfigForm"
		styleId="totalRecords" property="totalRecords"
		value="<%=strTotalRecords%>" />
	<table cellpadding="0" cellspacing="0" border="0"
		width="100%">
		<%-- width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>"> --%>
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="5"><bean:message bundle="servermgrResources" key="copypacket.search.searchcopypacketmapping"/></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="servermgrResources" 
											key="copypacket.name" />
												<ec:elitehelp headerBundle="servermgrResources" 
													text="copypacket.name" 
														header="copypacket.name"/>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:text styleId="name" property="name" size="30"
											maxlength="60" style="width:250px" />
									</td>
								</tr>

								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext" valign="middle" width="18%">&nbsp;</td>
									<td class="labeltext" colspan="2" align="left">
										<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Search " class="light-btn" onclick="validateSearch()">
										<input type="button" name="Create" value="   Create   "  class="light-btn" onclick="javascript:navigatePage('initCreateCopyPacketMappingConfig.do','name')">
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<%if(action.equals("list")) {%>
								<tr>
									<td align="left" class="labeltext" colspan="5" valign="top">
										<table cellSpacing="0" cellPadding="0" width="99%" border="0">
											<tr>
												<td class="table-header" width="50%">Copy Packet Config List</td>
												<td align="right" class="blue-text" valign="middle" width="50%">
													<% if(totalRecord == 0) { %> <% }else if(pageNo == totalPages+1) { %>
													[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
													<% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
													of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
													of <%= totalRecord %> <% } %>

												</td>
											</tr>
											<tr>
												<td></td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp; 
													<input type="button" name="Delete" onclick="removeData()" value="   Delete   " class="light-btn">
												</td>
											
											</tr>
											<tr height="2">
												<td></td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle" colspan="2">

													<table width="100%" border="0" cellpadding="0"
														cellspacing="0" id="listTable">
														<tr>


															<td align="center" class="tblheader" valign="top"
																width="1%"><input type="checkbox" name="toggleAll"
																value="checkbox" onclick="checkAll()" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Sr. No.</td>
															<td align="left" class="tblheader" valign="top"
																width="25%">Name</td>
															<td align="left" class="tblheader" valign="top" width="*">Description</td>
															<td align="left" class="tblheader" valign="top"
																width="20%">From</td>
															<td align="left" class="tblheader" valign="top"
																width="20%">To</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Edit</td>

														</tr>
														<tr>
															<%if(mappingConfigList != null && mappingConfigList.size() >0){ %>

															<logic:iterate id="obj" name="searchConfigForm" property="configList" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
																<tr>
																	<td align="center" class="tblfirstcol"><input type="checkbox" name="select" value="<bean:write name="obj" property="copyPacketTransConfId"/>" />
																	</td>
																	<td align="center" class="tblrows" width="5%"><%=((pageNo-1)*pageSize)+count%></td>
																	<td align="left" class="tblrows">
																		<a href="<%=basePath%>/viewCopyPacketConfigBasicDetail.do?copyPacketTransConfId=<%=obj.getCopyPacketTransConfId()%>">
																		<bean:write name="obj" property="name" /></a>
																	</td>
																	<td align="left" class="tblrows"><%=EliteUtility.formatDescription(obj.getDescription()) %>&nbsp;</td>
																
																	<td align="left" class="tblrows" valign="top">
																		<bean:write name="obj" property="translatorTypeFrom.name"/>
																	</td>
																	<td align="left" class="tblrows" valign="top"><bean:write
																			name="obj" property="translatorTypeTo.name" /></td>
																	<td align="center" class="tblrows">
																		<a href="<%=basePath%>/updateCopyPacketMappingConfig.do?copyPacketMappingConfigId=<%=obj.getCopyPacketTransConfId()%>">
																		<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"><!-- </a> -->
																	</td>
																</tr>
																<%count ++ ;%>
															</logic:iterate>
															<%}else{ %>
														
														<tr>
															<td align="center" class="tblfirstcol" colspan="8">No
																Records Found.</td>
														</tr>
														<%	}%>
														</tr>
													</table>
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp; <html:button
														property="c_btnDelete" onclick="removeData()"
														value="   Delete   " styleClass="light-btn" />
												</td>
												
											</tr>
										</table>
									</td>
								</tr>
								<%} %>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>

 