<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<%@ page import="com.elitecore.netvertexsm.web.customizedmenu.CustomizedMenuForm"%>


<%
	int iIndex =0;
%>
<script type="text/javascript"><!--

function validateSearch(){
	document.forms[0].pageNumber.value = 1;
	document.forms[0].submit();
}
function navigate(pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].actionName='list';
	document.forms[0].submit();
}
function removeRecord(){
    var selectVar = false;
    
    var chkBoxElements = document.getElementsByName('customizedMenuId');
    
    for (var i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one Menu Instance for remove process.');
    }else{
        var msg;
        msg = 'All selected Menu Instances would be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/customizedMenumgmt.do?method=delete';
       	 	document.forms[1].submit();
        }
    }
}



function selectAllMenu(){
	var dataRows = document.getElementsByName('dataRow');	
 	if( document.forms[1].toggleAll.checked == true) {
 		var selectVars = document.getElementsByName('customizedMenuId');
	 	for (var i=0; i < selectVars.length;i++){
			selectVars[i].checked = true ;
			dataRows[i].className='onHighlightRow';		
	 	}
	 	
    } else if (document.forms[1].toggleAll.checked == false){
 		var selectVars = document.getElementsByName('customizedMenuId');	    
		for (var i=0; i < selectVars.length; i++){
			selectVars[i].checked = false ;
			dataRows[i].className='offHighlightRow';
		}
	}
}

$(document).ready(function(){
	setTitle('<bean:message  bundle="customizedMenuResources" key="csmmenumgmt.title"/>');
	$("#title").focus();
	selectAllMenu();
});
function updateMenu(titleName) {
	document.forms[1].action = '<%=request.getContextPath()%>/customizedMenumgmt.do?method=initUpdate&title='+encodeURI(titleName);
	document.forms[1].submit();
	
}
function viewMenu(titleName) {
	document.forms[1].action = '<%=request.getContextPath()%>/customizedMenumgmt.do?method=view&title='+encodeURI(titleName);
	document.forms[1].submit();
}
</script>

<%
	 CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm) request.getAttribute("customizedMenuForm");
     List CustomizedMenuList = customizedMenuForm.getTitleList();   
             
     long pageNo = customizedMenuForm.getPageNumber();
     long totalPages = customizedMenuForm.getTotalPages();
     long totalRecord = customizedMenuForm.getTotalRecords();
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  
   <tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">				
	 	  <tr> 
			<td class="table-header" colspan="3"><bean:message  bundle="customizedMenuResources" key="csmmenumgmt.search.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" align="left" border="0" >
				   <html:form action="/customizedMenumgmt.do?method=search" >
				   <html:hidden name="customizedMenuForm" styleId="actionName" property="actionName" />
				   <html:hidden name="customizedMenuForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
				   <html:hidden name="customizedMenuForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
				   <html:hidden name="customizedMenuForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />				      			
				  
				  <tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.titlename"/></td>
						<td align="left" valign="top" width="*" >
						<html:text name="customizedMenuForm" property="title" maxlength="60" size="30" styleId="title" tabindex="1" />
					</td>
				  </tr>
  
 				 <tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td valign="middle" >             
		        		<input type="submit" name="Search"  Onclick="validateSearch()" value="   Search   " tabindex="2" class="light-btn" /> 
	        		</td> 
   		  		  </tr>	
				  
				</html:form> 
				
				<%
		if(customizedMenuForm.getActionName()!=null && customizedMenuForm.getActionName().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/customizedMenumgmt.do">
				
					<html:hidden name="customizedMenuForm" styleId="actionName" property="actionName" />
					<html:hidden name="customizedMenuForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="customizedMenuForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="50%" >
								<bean:message bundle="customizedMenuResources" key="csmmenumgmt.search.list" />
							</td>
							<td align="right" class="blue-text" valign="middle" width="50%" >
								<% if(totalRecord == 0) { %>
								<% }else if(pageNo == totalPages+1) { %>
							    	[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
								<% } else if(pageNo == 1) { %>
									[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %>
								<% } else { %>
									[<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %>
								<% } %>
							</td>
						</tr>
						
						<tr class="vspace">
							<td class="btns-td" valign="middle">
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/customizedMenumgmt.do?method=initCreate'"  class="light-btn" /> 
								<input type="button"   value="   Delete   " onclick="removeRecord()" class="light-btn">
							</td>
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
								<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
							 	<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
							<% } %>
						<% } %>
							</td>
						</tr>
	
							<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="2" >
								<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
									<tr>
										<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onClick="selectAllMenu()" /></td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.titlename" /></td>
										<td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.order" /></td>
										<td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.isContainer" /></td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.url" /></td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.parameters" /></td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod" /></td>
																				
										<td align="center" class="tblheaderlastcol" valign="top" width="1%"><bean:message key="general.edit" /></td>
									</tr>
									<%	
										if(CustomizedMenuList!=null && CustomizedMenuList.size()>0) {
											int i=0;
									%>
								<logic:iterate id="customizedMenuBean" name="customizedMenuForm"  property="customizedMenuList" type="com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData">
										<tr id="dataRow" name="dataRow" >	
										<td align="center" class="tblfirstcol"><input type="checkbox" tabindex="8" name="customizedMenuId" value="<bean:write name="customizedMenuBean" property="customizedMenuId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  /></td>
										<td align="left" class="tblrows">
											<a href="#" tabindex="8" onclick="viewMenu('<bean:write name="customizedMenuBean" property="title"/>')">
											<bean:write name="customizedMenuBean" property="title" />&nbsp;</a> 	
										</td>
										<td align="left" class="tblrows"><bean:write name="customizedMenuBean" property="order" />&nbsp;</td>
										<td align="left" class="tblrows"><bean:write name="customizedMenuBean" property="isContainer" />&nbsp;</td>
										<td align="left" class="tblrows"><bean:write name="customizedMenuBean" property="url" />&nbsp;</td>
										<td align="left" class="tblrows"><bean:write name="customizedMenuBean" property="parameters" />&nbsp;</td>
										<logic:equal name="customizedMenuBean" property="openMethod" value="self">
											<td align="left" class="tblrows"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod.samewindow" />&nbsp;</td>
										</logic:equal> 
										<logic:notEqual name="customizedMenuBean" property="openMethod" value="self">
											<td align="left" class="tblrows"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod.newwindow" />&nbsp;</td>
										</logic:notEqual> 
										<td align="center" class="tblrows">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0" onclick="updateMenu('<bean:write name="customizedMenuBean" property="title"/>')" style="cursor:pointer;" tabindex="8"></a>
										</td>																																				
									</tr>
									<% count=count+1; %>
									<% iIndex += 1; %>
								</logic:iterate>												
								<%		}else{	%>
									<tr>
										<td align="center" class="tblfirstcol" colspan="8">No Records Found.</td>
									</tr>
								<%		}	%>
							</table>
							</td>
						</tr>
						 
						<tr class="vspace">
							<td class="btns-td" valign="middle" >
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/customizedMenumgmt.do?method=initCreate'"  class="light-btn" /> 
								<input type="button"   value="   Delete   " onclick="removeRecord()" class="light-btn">
							</td>
							<td class="btns-td" align="right">
							<% if(totalPages >= 1) { %>
				  				<% if(pageNo == 1){ %>
				  	    			<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } %>
							  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
							  		<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							  	<% } %>
							 	<% if(pageNo == totalPages+1) { %>
								 	<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
								<% } %>
							<% } %>
							</td>						
						</tr>
						</table></td>
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

