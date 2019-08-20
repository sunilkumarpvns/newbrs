<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable.form.RoutingTableManagementForm"%>

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
%>


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
    
    var chkBoxElements = document.getElementsByName('routingTableId');
    
    for (var i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Select atleast one Routing Entry Instance for remove process.');
    }else{
        var msg;
        msg = 'All selected Routing Entry Instances would be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/routingTableManagement.do?method=delete';
       	 	document.forms[1].submit();
        }
   } 
}

function  checkAll(){
		var dataRows = document.getElementsByName('dataRow');
	  	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('routingTableId');
		 	for (var i = 0; i < selectVars.length;i++){
				selectVars[i].checked = true ;
				dataRows[i].className='onHighlightRow';
		 	}
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('routingTableId');	    
			for (var i = 0; i < selectVars.length; i++){
				selectVars[i].checked = false ;
				dataRows[i].className='offHighlightRow';
			}
		} 
}
$(document).ready(function() {
	setTitle('<bean:message  bundle="routingMgmtResources" key="routingtable.title"/>');
	checkAll();
	document.forms[0].name.focus();
});

</script>

<%
     RoutingTableManagementForm searchRoutingTableForm = (RoutingTableManagementForm) request.getAttribute("searchRoutingTableForm");
	 List routingEntryList = searchRoutingTableForm.getRoutingTableDataList();
     long pageNo = searchRoutingTableForm.getPageNumber();
     long totalPages = searchRoutingTableForm.getTotalPages();
     long totalRecord = searchRoutingTableForm.getTotalRecords();
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
%>
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
   <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="routingMgmtResources" key="routingtable.search.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3">
			   <table width="100%" id="c_tblCrossProductList" align="right" border="0" >
			   <html:form action="/routingTableManagement.do?method=search">
			   
			   	<html:hidden name="searchRoutingTableForm" styleId="routingAction" property="routingAction" />
				<html:hidden name="searchRoutingTableForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="searchRoutingTableForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="searchRoutingTableForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			   		 
					<tr> 
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="routingMgmtResources" key="routingtable.name"/></td> 
						<td align="left" class="labeltext" valign="top" width="32%" > 
							<html:text property="name" maxlength="60" size="30" styleClass="name" tabindex="1"/>
						</td> 
				  	</tr>
				  	<tr> 
	        			<td class="btns-td" valign="middle" ></td> 
            			<td  align="left"   valign="top"  >             
		        	     &nbsp;<input type="submit" name="Search" width="5%" Onclick="validateSearch()"   value=" Search " class="light-btn" tabindex="3"/> 
		        		 &nbsp;<input  class="light-btn" type="button" name="manage_Order"  width="5%" value="Manage Order" tabindex="4"  onclick="javascript:location.href='<%=basePath%>/routingTableManagement.do?method=initManageOrder'"/>
	        			</td>
	        		</tr>
				</html:form>
	<%
		if(searchRoutingTableForm.getAction()!=null && searchRoutingTableForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/routingTableManagement.do">
				
					<html:hidden name="searchRoutingTableForm" styleId="routingAction" property="routingAction" />				
					<html:hidden name="searchRoutingTableForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="searchRoutingTableForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
					<tr>
						<td class="table-header" width="24%" colspan="2">
							<bean:message bundle="routingMgmtResources" key="routingtable.search.list" />
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
							<input type="button" value="   Create   " onclick="javascript:location.href='<%=basePath%>/routingTableManagement.do?method=initCreate'" tabindex="5" name="c_btnCreate" class="light-btn">
							<input type="button" tabindex="6" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn">							
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
									<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onclick="checkAll()" />
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message bundle="routingMgmtResources" key="routingtable.name" />
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message bundle="routingMgmtResources" key="routingtable.type" />
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message bundle="routingMgmtResources" key="routingtable.action" />										
								</td>
								<td align="center" class="tblheaderlastcol" valign="top" width="5%">
									<bean:message key="general.edit"/>
								</td>
							</tr>
<%	
		if(routingEntryList!=null && routingEntryList.size()>0) { 
			int i=0;
%>
					<logic:iterate id="routingTableBean" name="searchRoutingTableForm"  property="routingTableDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData">
						<tr id="dataRow" name="dataRow" >
							<td align="center" class="tblfirstcol">
								<input type="checkbox" tabindex="8" name="routingTableId" value="<bean:write name="routingTableBean" property="routingTableId" />" onclick="onOffHighlightedRow(<%=i++%>,this)" />
							</td>
							<td align="left" class="tblrows">
							 <a href="<%=basePath%>/routingTableManagement.do?method=view&routingTableId=<bean:write name="routingTableBean" property="routingTableId"/>" tabindex="8">
									<bean:write name="routingTableBean" property="name" />&nbsp;</a>	 										
							</td>
							<logic:equal name="routingTableBean" property="type" value="MCCMNC">
								<td align="left" class="tblrows">MCCMNC-BASED</td>
							</logic:equal>			
							<logic:equal name="routingTableBean" property="type" value="REALM">
								<td align="left" class="tblrows">CUSTOM-REALM-BASED</td>
							</logic:equal> 
							 <logic:equal name="routingTableBean" property="routingAction" value="0">
							    <td align="left" class="tblrows">LOCAL</td>
							</logic:equal>
							<logic:equal name="routingTableBean" property="routingAction" value="2">
								 <td align="left" class="tblrows">PROXY</td>
							</logic:equal>
							<td align="center" class="tblrows">
								<a href="<%=basePath%>/routingTableManagement.do?method=initUpdate&routingTableId=<bean:write name="routingTableBean" property="routingTableId"/>" tabindex="8">
								<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
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
						<input type="button" value="   Create   " tabindex="11" name="c_btnCreate" class="light-btn"
							 onclick="javascript:location.href='<%=basePath%>/routingTableManagement.do?method=initCreate'"/> 
						<input type="button" name="Delete" tabindex="12" OnClick="removeRecord()" value="   Delete   " class="light-btn">													
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

