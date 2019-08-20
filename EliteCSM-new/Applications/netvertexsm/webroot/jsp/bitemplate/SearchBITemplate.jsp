<%@page import="com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@page import="com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData" %>
<%@ page import="java.util.*"%>
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
<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>
<%
	String strDatePattern = "dd MMM,yyyy hh:mm:ss";
	SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
	int iIndex =0;
%>

<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="biTemplateResources" key="bitemplate.header" />');
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
        alert('Please select atleast one Gateway for DeActivation');
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
        alert('At least select one BI Template for remove process');
    }else{
        var msg;
        msg = 'All selected BI template would be deleted. Would you like to continue ?';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action.value = 'delete';
       	 	document.forms[1].submit();
        }
    }
} 

</script>

<%
     BITemplateForm biTemplateForm = (BITemplateForm) request.getAttribute("biTemplateForm");
     List lstBITemplate = biTemplateForm.getListSearchTemplate();    
             
     long pageNo = biTemplateForm.getPageNumber();
     long totalPages = biTemplateForm.getTotalPages();
     long totalRecord = biTemplateForm.getTotalRecords();
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
%>

<style> 
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style> 

<input type="hidden" name="isInSync" value="CST02" id="isInSync"> 
<input type="hidden" name="action" value="next" id="action"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">				
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="biTemplateResources" key="bitemplate.search" /></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" align="right" border="0" >
			   
			   <html:form action="/searchBITemplate" >
			   
			   	<html:hidden name="biTemplateForm" styleId="action" property="action" />
				<html:hidden name="biTemplateForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="biTemplateForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="biTemplateForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			      
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="10%"><bean:message key="general.name"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text name="biTemplateForm" property="name" maxlength="60" tabindex="1" size="30" styleId="name"/>
					</td> 
				  </tr>
				 
				  <tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td class="btns-td" valign="middle" >             
		        		<input type="submit" name="Search" width="5%" Onclick="validateSearch()" value="   Search   " tabindex="2" class="light-btn" /> 
	        		</td> 
   		  		  </tr>	
				  
				</html:form> 
	<%
		if(biTemplateForm.getAction()!=null && biTemplateForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/miscBITemplate">
				
					<html:hidden name="biTemplateForm" styleId="action" property="action" />
					<html:hidden name="biTemplateForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="biTemplateForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="24%" colspan="2">
								<bean:message bundle="biTemplateResources" key="bitemplate.list" />
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
								<input type="button" value="   Create   " name="c_btnCreate" tabindex="3" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateBITemplate.do?/>'"/>
								<input type="button" tabindex="4" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn">								
							</td>
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="searchBITemplate.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="searchBITemplate.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="searchBITemplate.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="searchBITemplate.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
							<% } %>
						<% } %>
							</td>
						</tr>
						
						<tr  class="vspace">
							<td class="btns-td" valign="middle" colspan="9">
							<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onclick="checkAll(this)" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message key="general.name"/></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message key="general.key"/></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message key="general.description"/></td>
									<td align="left" class="tblheaderlastcol" valign="top" width="20%"><bean:message key="general.createddate"/></td>
									<td align="left" class="tblheaderlastcol" valign="top" width="20%"><bean:message key="general.lastmodifieddate"/></td>
									<td align="left" class="tblheaderlastcol" valign="top" width="5%"><bean:message key="general.edit"/></td>								
								</tr>
<%	
		if(lstBITemplate!=null && lstBITemplate.size()>0) { 
			int i=0;
%>
				<logic:iterate id="biBean" name="biTemplateForm"
					property="listSearchTemplate"
					type="com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData">

						<tr id="dataRow" name="dataRow" >	
						<td align="center" class="tblfirstcol"><input type="checkbox" tabindex="8" name="select" value="<bean:write name="biBean" property="id"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  /></td>
						<td align="left" class="tblrows">
							<a href="<%=basePath%>/viewBITemplate.do?id=<bean:write name="biBean" property="id"/>" tabindex="8">
								<bean:write name="biBean" property="name" />&nbsp;</a></td>
						<td align="left" class="tblrows"><bean:write name="biBean" property="key" />&nbsp;</td>		
						<td align="left" class="tblrows"><bean:write name="biBean" property="description" />&nbsp;</td>	
						<td align="left" class="tblrows">
							<%=EliteUtility.dateToString(biBean.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
						<td align="left" class="tblrows">
							<%=EliteUtility.dateToString(biBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>		
						<td align="center" class="tblrows">
							<a href="<%=basePath%>/initEditBITemplate.do?id=<bean:write name="biBean" property="id"/>&url=searchBITemplate" tabindex="8">
								<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></a>
						</td>																																				
					</tr>
					<% count=count+1; %>
					<% iIndex += 1; %>
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
								<input type="button" tabindex="9" value="   Create   " name="c_btnCreate" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateBITemplate.do?/>'"/> 
								<input type="button" tabindex="4" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn">								
							</td>
							<td class="btns-td" align="right">
								<% if(totalPages >= 1) { %>
							  	<% if(pageNo == 1){ %>
							  	    <a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
								<% } %>
							  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
							  		<%  if(pageNo-1 == 1){ %>
							  		    <a  href="searchBITemplate.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
										
							 		<% } else if(pageNo == totalPages){ %>
							 		
							 		    <a  href="searchBITemplate.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
							  		<% } else { %>
							  		    <a  href="searchBITemplate.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchBITemplate.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							  		
									<% } %>
							  	<% } %>
							 	<% if(pageNo == totalPages+1) { %>
							 	
							 	<a  href="searchBITemplate.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="searchBITemplate.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
							 	
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

