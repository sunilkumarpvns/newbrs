<%@page import="com.elitecore.netvertexsm.web.servermgr.spr.form.SPRForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="java.util.*"%>


<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.util.Iterator"%>

<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>

<%
	int iIndex =0;
	SPRForm sprForm = (SPRForm) request.getAttribute("searchSPRForm");
	 List sprDataList = sprForm.getSprDataList();
	 
     long pageNo = sprForm.getPageNumber();
     long totalPages = sprForm.getTotalPages();
     long totalRecord = sprForm.getTotalRecords();
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
     String sprName = sprForm.getSprName();
%>


<script type="text/javascript">

function validateSearch(){
	document.forms[0].pageNumber.value = 1;
	document.forms[0].sprName = '<%=sprName%>';
	document.forms[0].submit();
}

function navigate(pageNumber){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].sprName.value = '<%=sprName%>';
	document.forms[0].action.value ='list';
	document.forms[0].submit();
}

function removeRecord(){
    var selectVar = false;
    
    var chkBoxElements = document.getElementsByName('id');
    
    for (var i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Select atleast one SPR for remove process.');
    }else{
        var msg;
        msg = 'All selected SPR would be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/sprData.do?method=delete';
       	 	document.forms[1].submit();
        }
    }
}

function  checkAll(){
		var dataRows = document.getElementsByName('dataRow');					
	 	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('id');
		 	for (var i = 0; i < selectVars.length;i++){
				selectVars[i].checked = true ;
				dataRows[i].className='onHighlightRow';		
		 	}
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('id');	    
			for (var i = 0; i < selectVars.length; i++){
				selectVars[i].checked = false ;
				dataRows[i].className='offHighlightRow';
			}
		}
}
$(document).ready(function() {	
	setTitle('<bean:message  bundle="sprResources" key="spr.title" />');
	checkAll();
	document.forms[0].sprName.focus();
	
	
});

</script>


<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
   <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="sprResources" key="spr.search"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3">
			   <table width="100%" id="c_tblCrossProductList" align="right" border="0" >
			   <html:form action="/sprData.do?method=search">
			   
			   	<html:hidden name="sprForm" styleId="action" property="action" />
				<html:hidden name="sprForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="sprForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="sprForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			   		 
					<tr> 
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="sprResources" key="spr.name"/></td> 
						<td align="left" class="labeltext" valign="top" width="32%" > 
							<html:text property="sprName" maxlength="60" size="30" styleClass="sprName" styleId="sprName" tabindex="1"/>
						</td> 
				  	</tr>
				  	<tr> 
	        			<td class="btns-td" valign="middle" ></td> 
            			<td  align="left" valign="top" class="btns-td" >             
		        			&nbsp;<input type="submit" name="Search" width="5%" Onclick="validateSearch()"  value=" Search " class="light-btn" tabindex="3"/> 
	        			</td> 
   		  			</tr>
				</html:form>
	<%
		if(sprForm.getAction()!=null && sprForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/sprData.do?">
				
					<html:hidden name="sprForm" styleId="action" property="action" />					
					<html:hidden name="sprForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="sprForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
					<tr>
						<td class="table-header" width="24%" colspan="2">
							<bean:message bundle="sprResources" key="spr.search.list" />
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
							<input type="button" value="   Create   " onclick="javascript:location.href='<%=basePath%>/sprData.do?method=initCreate'" tabindex="4" name="c_btnCreate" class="light-btn">
								
							<input type="button" tabindex="5" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn">							
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
						<%}%>
						</td>
					</tr>
					<tr class="vspace">
						<td class="btns-td" valign="middle" colspan="9">
						<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
							<tr>
								<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
									<input type="checkbox" name="toggleAll" tabindex="6" value="checkbox" onclick="checkAll()" />
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message bundle="sprResources" key="spr.name" />
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message bundle="sprResources" key="spr.description" />										
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message  key="general.lastmodifieddate" />										
								</td>
								<td align="center" class="tblheaderlastcol" valign="top" width="5%">
									<bean:message key="general.edit"/>
								</td>
							</tr>
<%	
		if(sprDataList!=null && sprDataList.size()>0) {
			int i=0;
%>
					<logic:iterate id="sprBean" name="sprForm" property="sprDataList" type="com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData">
						<tr id="dataRow" name="dataRow" >	
							<td align="center" class="tblfirstcol">
								<input type="checkbox" tabindex="7" name="id" value="<bean:write name="sprBean" property="id" />" onclick="onOffHighlightedRow(<%=i++%>,this)"  />
							</td>
							<td align="left" class="tblrows">
							<a href="<%=basePath%>/sprData.do?method=view&id=<bean:write name="sprBean" property="id"/>" tabindex="7">
									<bean:write name="sprBean" property="sprName" />&nbsp;</a>											
							</td>
						 	<td align="left" class="tblrows">
								<bean:write name="sprBean" property="description" />&nbsp;
							</td> 
							<td align="left" class="tblrows">
								<%=EliteUtility.dateToString(sprBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;&nbsp;
							</td>
							<td align="center" class="tblrows">
								<a href="<%=basePath%>/sprData.do?method=initUpdate&id=<bean:write name="sprBean" property="id"/>" tabindex="7">
								<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">&nbsp;
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
				 
				<tr  class="vspace">
					<td class="btns-td" valign="middle" colspan="5">	
						<input type="button" value="   Create   " tabindex="10" name="c_btnCreate" class="light-btn"
							 onclick="javascript:location.href='<%=basePath%>/sprData.do?method=initCreate'"/> 
						<input type="button" name="Delete" tabindex="11" OnClick="removeRecord()" value="   Delete   " class="light-btn">													
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
							<%}%>
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

