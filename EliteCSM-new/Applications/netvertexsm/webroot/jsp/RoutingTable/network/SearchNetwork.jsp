<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.network.form.NetworkManagementForm"%>

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
    
    var chkBoxElements = document.getElementsByName('networkID');
    
    for (var i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one network Instance for remove process.');
    }else{
        var msg;
        msg = 'All selected network Instances will be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/networkManagement.do?method=delete';
       	 	document.forms[1].submit();
        }
    }
}

function checkAll(){
	var dataRows = document.getElementsByName('dataRow');
 	if( document.forms[1].toggleAll.checked == true) {
 		var selectVars = document.getElementsByName('networkID');
	 	for (var i=0; i < selectVars.length;i++){
			selectVars[i].checked = true ;
			dataRows[i].className='onHighlightRow';
	 	}
    } else if (document.forms[1].toggleAll.checked == false){
 		var selectVars = document.getElementsByName('networkID');	    
		for (var i=0; i < selectVars.length; i++){
			selectVars[i].checked = false ;
			dataRows[i].className='offHighlightRow';
		}
	}
}
$(document).ready(function(){
	setTitle('<bean:message  bundle="routingMgmtResources" key="network.mgmt.title"/>');
	$("#countryID").focus();
	 checkAll();
});

</script>

<%
	NetworkManagementForm networkManagementForm = (NetworkManagementForm) request.getAttribute("networkManagementForm");
     List networkDataList = networkManagementForm.getMccmncCodesDataList();   
             
     long pageNo = networkManagementForm.getPageNumber();
     long totalPages = networkManagementForm.getTotalPages();
     long totalRecord = networkManagementForm.getTotalRecords();
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
			<td class="table-header" colspan="3"><bean:message  bundle="routingMgmtResources" key="network.search.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" align="left" border="0" >
				   <html:form action="/networkManagement.do?method=search" >
				   <html:hidden name="networkManagementForm" styleId="actionName" property="actionName" />
				   <html:hidden name="networkManagementForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
				   <html:hidden name="networkManagementForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
				   <html:hidden name="networkManagementForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />				      			
				  
				  <tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="routingMgmtResources" key="network.country"/></td>
						<td align="left" valign="top" width="*" >
							<html:select name="networkManagementForm" styleId="countryID" tabindex="3" property="countryID" size="1" style="width: 220px;">
								<html:option value="0">--Select--</html:option>
								<logic:iterate id="country"  name="networkManagementForm" property="countryDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
									<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
								</logic:iterate>
							</html:select>	      
						</td>
				  </tr>
				  
				  <tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="routingMgmtResources" key="network.operator"/></td>
						<td align="left" valign="top" width="*" >
							<html:select name="networkManagementForm" styleId="operatorID" tabindex="3" property="operatorID" size="1" style="width: 220px;">
								<html:option value="0">--Select--</html:option>
								<logic:iterate id="operator" name="networkManagementForm" property="operatorDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData">
									<html:option value="<%=Long.toString(operator.getOperatorID())%>"><bean:write name="operator" property="name"/></html:option>
								</logic:iterate>
							</html:select>	      
						</td>
				  </tr>
				  			 
				  <tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td valign="middle" >             
		        		<input type="submit" name="Search"  Onclick="validateSearch()" value="   Search   " tabindex="4" class="light-btn" /> 
	        		</td> 
   		  		  </tr>	
				  
				</html:form> 
	<%
		if(networkManagementForm.getActionName()!=null && networkManagementForm.getActionName().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/networkManagement.do">
				
					<html:hidden name="networkManagementForm" styleId="actionName" property="actionName" />
					<html:hidden name="networkManagementForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="networkManagementForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="50%" >
								<bean:message bundle="routingMgmtResources" key="network.search.list" />
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
								<input type="button"   value="   Create   "  tabindex="6" onclick="javascript:location.href='<%=basePath%>/networkManagement.do?method=initCreate'"  class="light-btn" /> 
								<input type="button"   value="   Delete   "  tabindex="6" onclick="removeRecord()" class="light-btn">
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
										<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onclick="checkAll()" /></td>
										<td align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="routingMgmtResources" key="network.networkname" /></td>
										<td align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="routingMgmtResources" key="network.operator" /></td>										
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="routingMgmtResources" key="network.country" /></td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="routingMgmtResources" key="network.mcc" /></td>								
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="routingMgmtResources" key="network.mnc" /></td>										
										<td align="center" class="tblheaderlastcol" valign="top" width="3%"><bean:message key="general.edit" /></td>
									</tr>
									<%	
										if(networkDataList!=null && networkDataList.size()>0) {
										int i=0;
									%>
								<logic:iterate id="networkBean" name="networkManagementForm"  property="mccmncCodesDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData">
									<tr id="dataRow" name="dataRow" >
										<td align="center" class="tblfirstcol"><input type="checkbox" tabindex="8" name="networkID" value="<bean:write name="networkBean" property="networkID"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  /></td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/networkManagement.do?method=view&networkID=<bean:write name="networkBean" property="networkID"/>" tabindex="8">
											<bean:write name="networkBean" property="networkName" />	&nbsp;</a>
										</td>
										<td align="left" class="tblrows">
											<bean:write name="networkBean" property="operatorData.name" />
										&nbsp;</td>		
										<td align="left" class="tblrows"><bean:write name="networkBean" property="countryData.name" />&nbsp;</td>		
										<td align="left" class="tblrows"><bean:write name="networkBean" property="mcc" />&nbsp;</td>		
										<td align="left" class="tblrows"><bean:write name="networkBean" property="mnc" />&nbsp;</td>		
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/networkManagement.do?method=initUpdate&networkID=<bean:write name="networkBean" property="networkID"/>" tabindex="8">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></a>
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
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/networkManagement.do?method=initCreate'"  class="light-btn" /> 
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

