<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="com.elitecore.netvertexsm.web.group.form.GroupDataForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.sql.Timestamp"%>

<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>

<%
	String strDatePattern = "dd MMM,yyyy hh:mm:ss";
	SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
	int iIndex =0;
%>
<style>
	.btn-disabled{
		background-image: none;
		background-color: grey;
	}
</style>
<script type="text/javascript">

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
    
    var chkBoxElements = document.getElementsByName('groupDataIds');
    
    for (i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one Group Data for remove process');
    }else{
        var msg;
        msg = 'All selected Group Data Instances would be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/groupManagement.do?method=delete';
       	 	document.forms[1].submit();
        }
    }
}
function  checkAll(){

		var dataRows = document.getElementsByName('dataRow');					
	 	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('groupDataIds');
		 	for (i = 0; i < selectVars.length;i++){
				selectVars[i].checked = true ;
				dataRows[i].className='onHighlightRow';
		 	}
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('groupDataIds');	    
			for (i = 0; i < selectVars.length; i++){
				selectVars[i].checked = false ;
				dataRows[i].className='offHighlightRow';
			}
		}
}


$(document).ready(function(){
	setTitle('<bean:message  bundle="groupDataMgmtResources" key="group.title"/>');
	$("#name").focus();
	checkAll(document.getElementsByName("toggleAll")[0]);
});
</script>

<%
	GroupDataForm groupDataForm = (GroupDataForm) request.getAttribute("groupDataForm");
     List groupDatas = groupDataForm.getGroupDatas();    
             
     long pageNo = groupDataForm.getPageNumber();
     long totalPages = groupDataForm.getTotalPages();
     long totalRecord = groupDataForm.getTotalRecords();
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     int count=1;
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  <tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">				
	 	  <tr> 
			<td class="table-header" colspan="3"><bean:message  bundle="groupDataMgmtResources" key="group.search.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" align="left" border="0" >
				   <html:form action="/groupManagement.do?method=search" >
				   	<html:hidden name="groupDataForm" styleId="actionName" property="actionName" />
					<html:hidden name="groupDataForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
				   	<html:hidden name="groupDataForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
				   	<html:hidden name="groupDataForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				      
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="35%"><bean:message bundle="groupDataMgmtResources" key="group.name" /></td> 
					<td align="left" valign="top" width="*" > 
						<html:text name="groupDataForm" property="name" maxlength="50"  size="30" styleId="name"/>
					</td> 
				  </tr>
				    			 
				  <tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td valign="middle" >             
		        		<input type="submit" name="Search"  Onclick="validateSearch()" value="   Search   "  class="light-btn" /> 
		        	</td>
		        </tr>
				</html:form> 
				
	<%
		if(groupDataForm.getActionName()!=null && groupDataForm.getActionName().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/groupManagement.do">
				
					<html:hidden name="groupDataForm" styleId="actionName" property="actionName" />
					<html:hidden name="groupDataForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="groupDataForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="50%" >
								<bean:message bundle="groupDataMgmtResources" key="group.search.list" /> 
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
							<td class="btns-td" valign="middle" width="100%">
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/groupManagement.do?method=initCreate'"  class="light-btn" /> 
								<input type="button"  disabled="disabled" class="light-btn btn-disabled"  value="   Delete   " onclick="removeRecord()">
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
										<td align="center" class="tblheader" valign="top" width="1%">
											<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onclick="checkAll(this)" /></td>
										<td align="left" class="tblheader" valign="top" width="30%"><bean:message bundle="groupDataMgmtResources" key="group.name" /></td>
										<td align="left" class="tblheader" valign="top" width="68%"><bean:message bundle="groupDataMgmtResources" key="group.description" /></td>
										<td align="left" class="tblheaderlastcol" valign="top" width="1%"><bean:message key="general.edit" /></td>
									</tr>
									<%	
										if(Collectionz.isNullOrEmpty(groupDatas) == false) {
											int i = 0;
									%>
								
									<logic:iterate id="groupData" name="groupDataForm" property="groupDatas" type="com.elitecore.corenetvertex.sm.acl.GroupData">
										<tr id="dataRow" name="dataRow" >
										<td align="center" class="tblfirstcol" valign="top"><input type="checkbox" tabindex="8" name="groupDataIds" value="<bean:write name="groupData" property="id"/>" onclick="onOffHighlightedRow(<%=i++%>,this)" /></td>
										<td align="left" class="tblrows" valign="top">
											<a href="<%=basePath%>/groupManagement.do?method=view&groupId=<bean:write name="groupData" property="id"/>" tabindex="8">
												<bean:write name="groupData" property="name" />&nbsp;</a>
										</td>
										<td align="left" class="tblrows" valign="top"><bean:write name="groupData" property="description" />&nbsp;</td>		
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/groupManagement.do?method=initUpdate&groupId=<bean:write name="groupData" property="id"/>" tabindex="8">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></a>
										</td>																																				
									</tr>
								</logic:iterate>	
								<% count=count+1; %>
									<% iIndex += 1; %>											
								<%		}else{	%>
									<tr>
										<td align="center" class="tblfirstcol" colspan="8">No Records Found.</td>
									</tr>
								<%		}	%>
							</table>
							</td>
						</tr>
						 
						<tr class="vspace">
							<td class="btns-td" valign="middle" width="100%">
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/groupManagement.do?method=initCreate'"  class="light-btn" /> 
								<input type="button" disabled="disabled" class="light-btn btn-disabled"  value="   Delete   " onclick="removeRecord()">
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

