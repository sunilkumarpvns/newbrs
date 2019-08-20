<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.netvertexsm.web.servermgr.drivers.form.SearchDriverInstanceForm"%>

<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData"%>

<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData"%>

<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
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

<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="driverResources" key="driver"/>');
	$("#name").focus();
	checkAll(document.getElementsByName("toggleAll")[0]);
});
function validateSearch(){
	document.forms[0].pageNumber.value = 1;
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

function active(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Please select atleast one Gateway for Activation');
    }else{
    	document.miscStaffForm.action.value = 'active';
    	document.miscStaffForm.submit();
    }
}
function inactive(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Please select atleast one Driver for DeActivation');
    }else{
    	document.miscStaffForm.action.value = 'inactive';
    	document.miscStaffForm.submit();
    }
}
function removeRecord(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one Driver for remove process');
    }else{
        var msg;
        msg = 'All the selected Driver Instance would be deleted. Would you like to continue?';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    //document.miscStaffForm.action.value = 'delete';
        	document.forms[1].submit();
        }
    }
}
 

</script>
<%
     SearchDriverInstanceForm searchDriverInstanceForm = (SearchDriverInstanceForm) request.getAttribute("searchDriverInstanceForm");
     List lstDriver = searchDriverInstanceForm.getListSearchDriver();
     
     long pageNo = searchDriverInstanceForm.getPageNumber();
     long totalPages = searchDriverInstanceForm.getTotalPages();
     long totalRecord = searchDriverInstanceForm.getTotalRecords();
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
%>

<style> 
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style> 

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">				
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="driverResources" key="driver.driverinstance.search"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" align="right" border="0" >
			   
			   <html:form action="/searchDriverInstance" >
			   
			   	<html:hidden name="searchDriverInstanceForm" styleId="action" property="action" />
				<html:hidden name="searchDriverInstanceForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="searchDriverInstanceForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="searchDriverInstanceForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			      
				<tr>
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="driver.driverinstance.name" /></td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<html:text styleId="name" tabindex="1" property="name" size="30" maxlength="60" />
					</td>	
				</tr>					
				<tr>
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="driver.driverinstance.drivertype" /></td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<html:select name="searchDriverInstanceForm" styleId="driverTypeId" property="driverTypeId" tabindex="2">
							<html:option value="0">Select</html:option>
							<logic:iterate id="objservice" name="searchDriverInstanceForm" type="ServiceTypeData" property="serviceTypeList">
								<optgroup  label="<%=objservice.getDisplayName()%>" class="labeltext">
									<logic:iterate id="driverTypes"  name="searchDriverInstanceForm" property="driverTypeList" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData">
										<html:option value="<%=Long.toString(driverTypes.getDriverTypeId())%>"><bean:write name="driverTypes" property="name"/></html:option>
									</logic:iterate>
								</optgroup>	
							</logic:iterate>
						</html:select>
					</td>
				</tr>
						  
				<tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td class="btns-td" valign="middle" >                 
		        		<input type="submit" name="Search" tabindex="3" width="5%" Onclick="validateSearch()" value="   Search   " class="light-btn" /> 
	        		</td> 
   		  		</tr>	
				  </html:form> 
				  <%
					if(searchDriverInstanceForm.getAction()!=null && searchDriverInstanceForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
				  %>

				<html:form action="/miscDriverInstance">
				
					<html:hidden name="miscDriverInstanceForm" styleId="action" property="action" />
					<html:hidden name="miscDriverInstanceForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="miscDriverInstanceForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						
						<tr>
							<td class="table-header" width="24%" colspan="2">
								<bean:message bundle="driverResources" key="driver.driverinstance.searchlist" />
							</td>
							<td align="left" class="blue-text" valign="middle" width="62%" colspan="3">&nbsp;</td>
							<td align="right" class="blue-text" valign="middle" width="14%" colspan="4">
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
							<td class="btns-td" valign="middle" colspan="5">
								<input type="button" value="   Create   " name="c_btnCreate" class="light-btn" tabindex="4"
								onclick="javascript:location.href='<%=basePath%>/initCreateDriverInstance.do?/>'"/>
								<input type="button" name="Delete" OnClick="removeRecord()" value="   Delete   " tabindex="5" class="light-btn">								
							</td>
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="initSearchDriverInstance.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="initSearchDriverInstance.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="initSearchDriverInstance.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="initSearchDriverInstance.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
							<% } %>
						<% } %>
						</td>
						</tr>
						 
						<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="9">
							<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" tabindex="8" value="checkbox" onclick="checkAll(this)" /></td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message bundle="driverResources" key="driver.driverinstance.name" /></td>
									<td align="left" class="tblheader" valign="top" width="15%">
										<bean:message bundle="driverResources" key="driver.driverinstance.drivertype" /></td>
									<td align="left" class="tblheader" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.driverinstance.decription" /></td>
									<td align="left" class="tblheader" valign="top" width="25%">
										<bean:message key="general.lastmodifieddate" /></td>
									<td align="center" class="tblheaderlastcol" valign="top" width="3%"><bean:message key="general.edit" /></td>
								</tr>
<%	
		if(lstDriver!=null && lstDriver.size()>0) { 
			int i=0;
%>
								<logic:iterate id="driverBean" name="searchDriverInstanceForm" property="listSearchDriver" 
									type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData">

									<tr id="dataRow" name="dataRow" >
										<td align="center" class="tblfirstcol">
											<input type="checkbox" name="select" tabindex="9" value="<bean:write name="driverBean" property="driverInstanceId" />" 	onclick="onOffHighlightedRow(<%=i++%>,this)"  />
										</td>
										<td align="left" class="tblrows">
										<a href="<%=basePath%>/viewDriverInstance.do?driverInstanceId=<bean:write name="driverBean" property="driverInstanceId"/>" tabindex="9">
												<bean:write name="driverBean" property="name" />&nbsp;												
										</a>
										</td>
										<td align="left" class="tblrows">
											<bean:write name="driverBean" property="driverTypeData.name" />&nbsp;</td>																													
										<td align="left" class="tblrows">
											<bean:write name="driverBean" property="description" />&nbsp;</td>
										<td align="left" class="tblrows">
											<%=EliteUtility.dateToString(driverBean.getLastModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
										</td>
										<td align="center" class="tblrows">
										<a href="<%=basePath%>/initEditDriverInstance.do?driverInstanceId=<bean:write name="driverBean" property="driverInstanceId"/>&driverTypeId=<bean:write name="driverBean" property="driverTypeId"/>&url=searchDriverInstance" tabindex="9">
											<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></td>									

									</tr>
									<% count=count+1; %>
									
								</logic:iterate>												
<%		}else{	%>
						<tr>
							<td align="center" class="tblfirstcol" colspan="7">No Records Found.</td>
						</tr>
<%		}	%>
							</table>
							</td>
						</tr>
						 
						<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="5">
								<input type="button" value="   Create   " tabindex="10" name="c_btnCreate" class="light-btn"
									onclick="javascript:location.href='<%=basePath%>/initCreateDriverInstance.do?/>'"/>
								<input type="button" name="Delete" tabindex="11" Onclick="removeRecord()" value="   Delete   " class="light-btn">															 
							</td>
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="initSearchDriverInstance.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="initSearchDriverInstance.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="initSearchDriverInstance.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="initSearchDriverInstance.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="initSearchDriverInstance.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
							<% } %>
						<% } %>
						</td>
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
