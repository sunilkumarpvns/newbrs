<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.netvertexsm.web.gateway.gateway.form.SearchGatewayForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@ page import="com.elitecore.netvertexsm.hibernate.gateway.HGatewayDataManager"%>

<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData"%>
<%@ page import="java.sql.Timestamp"%>

<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>


<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData"%>

<%
	String strDatePattern = "dd MMM,yyyy hh:mm:ss";
	SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
	int iIndex =0;
%>

<script type="text/javascript">

$(document).ready(function(){
	setTitle('<bean:message bundle="gatewayResources" key="gateway.header"/>');
	$("#gatewayName").focus();
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
        alert('Select at least  one Gateway for remove process');
    }else{
        var msg;
        msg = 'Selected Gateway(s) would be deleted. Would you like to continue?';       
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
     SearchGatewayForm searchGatewayForm = (SearchGatewayForm)request.getAttribute("searchGatewayForm"); 
     List lstGateway = searchGatewayForm.getListSearchGateway();
     String strAreaName = searchGatewayForm.getAreaName();
     int intLocationId = searchGatewayForm.getLocationId();
     long pageNo = searchGatewayForm.getPageNumber();
     long totalPages = searchGatewayForm.getTotalPages();
     long totalRecord = searchGatewayForm.getTotalRecords();
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
     String paramString = "areaName="+strAreaName + "&locationId="+intLocationId;
%>
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="gatewayResources" key="gateway.searchgateway"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" id="c_tblCrossProductList" align="right" border="0" >
			   <html:form action="/searchGateway" >
			   
			   	<html:hidden name="searchGatewayForm" styleId="action" property="action" />
				<html:hidden name="searchGatewayForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="searchGatewayForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="searchGatewayForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			   		 
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="30%">
						<bean:message bundle="gatewayResources" key="gateway.creategateway"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="70%"> 
						<html:text name="searchGatewayForm" styleId="gatewayName" property="gatewayName" maxlength="40" size="30" tabindex="1" />						
					</td> 
				  </tr>					
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="gatewayResources" key="gateway.commprotocol"/></td> 
					<td align="left" class="labeltext" valign="top" width="70%"> 
						<html:select name="searchGatewayForm" styleId="commProtocolId" property="commProtocolId" size="1"   tabindex="2" >			
							<html:option value="">-- Select --</html:option>				
							<logic:iterate id="commProtocols" name="searchGatewayForm" collection="<%=CommunicationProtocol.values() %>" type="com.elitecore.corenetvertex.constants.CommunicationProtocol">
								<html:option value="<%=commProtocols.id%>"><%=commProtocols.name %></html:option>
							</logic:iterate>
						</html:select>											
					</td>
				  </tr>	
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="30%">						 
						<bean:message bundle="gatewayResources" key="gateway.ipaddress"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="70%"> 
						<html:text name="searchGatewayForm" styleId="connectionUrl" onchange="" property="connectionUrl" maxlength="45" size="30" tabindex="3" />						
					</td> 
				  </tr> 
				  						
				  	<tr> 
	        			<td class="btns-td" valign="middle" >&nbsp;</td> 
            			<td class="btns-td" valign="middle" >             
		        			<input type="submit" name="Search" width="5%" Onclick="validateSearch()" value="   Search   " tabindex="4" class="light-btn" /> 
	        			</td> 
   		  			</tr>
				</html:form>
	<%
		if(searchGatewayForm.getAction()!=null && searchGatewayForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/miscGateway">
				
					<html:hidden name="miscGatewayForm" styleId="action" property="action" />	
					<html:hidden name="miscGatewayForm" styleId="areaName" property="areaName" />
					<html:hidden name="miscGatewayForm" styleId="locationId" property="locationId" />
					<html:hidden name="miscGatewayForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="miscGatewayForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
		
				<tr>
					<td colspan="2">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="24%" colspan="2">
								<bean:message bundle="gatewayResources" key="gateway.gatewaylist" />
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
								<input type="button" value="   Create   " tabindex="7" name="c_btnCreate" class="light-btn"
									onclick="javascript:location.href='<%=basePath%>/initCreateGateway.do?/>'"/>
								<input type="button" name="Delete" OnClick="removeRecord()" tabindex="8" value="   Delete   " class="light-btn">																
							</td>
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
							<% } %>
						<% } %>
						</td>
						</tr>
						 
						<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="9">
							<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" tabindex="11" value="checkbox" onclick="checkAll(this)" />&nbsp;
									</td>
									<td align="left" class="tblheader" valign="top" width="15%">
										<bean:message bundle="gatewayResources" key="gateway.creategateway" />
									</td>
									<td align="left" class="tblheader" valign="top" width="7%">
										<bean:message bundle="gatewayResources" key="gateway.commprotocol" />										
									</td>																		
									<td align="left" class="tblheader" valign="top" width="15%">
										<bean:message bundle="gatewayResources" key="gateway.ipaddress" />
									</td>
									<td align="center" class="tblheader" valign="top" width="18%">
										<bean:message bundle="gatewayResources" key="gateway.area" />
									</td>
									<td align="center" class="tblheader" valign="top" width="20%">
										<bean:message bundle="gatewayResources" key="gateway.description" />
									</td>									
									<td align="center" class="tblheader" valign="top" width="3%">
										<bean:message key="general.edit"/>
									</td>
									<td align="center" class="tblheaderlastcol" valign="top" width="5%">
										<bean:message key="general.duplicate"/>
									</td>
								</tr>
<%	
		if(lstGateway!=null && lstGateway.size()>0) { 
			int i=0;
%>
								<logic:iterate id="gatewayBean" name="searchGatewayForm"
									property="listSearchGateway"
									type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData">

									<tr id="dataRow" name="dataRow" >
										<td align="center" class="tblfirstcol" style="vertical-align: bottom;" >
											<input type="checkbox" name="select" tabindex="12" value="<bean:write name="gatewayBean" property="gatewayId" />" onclick="onOffHighlightedRow(<%=i++%>,this)"  />&nbsp;
										</td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/viewGateway.do?gatewayId=<bean:write name="gatewayBean" property="gatewayId"/>&commProtocolId=<bean:write name="gatewayBean" property="commProtocol" />" tabindex="12">
												<bean:write name="gatewayBean" property="gatewayName" />&nbsp;												
											</a>											
										</td>										

										<td align="left" class="tblrows">
											<%=CommunicationProtocol.fromId(gatewayBean.getCommProtocol()).name%>&nbsp;											
										</td>
										<td align="left" class="tblrows">
												<bean:write name="gatewayBean" property="connectionUrl" />&nbsp;																											
										</td>
										<td align="left" class="tblrows">
											<bean:write name="gatewayBean" property="areaName" />&nbsp;
										</td>	
										<td align="left" class="tblrows">
											<bean:write name="gatewayBean" property="description" />&nbsp;
										</td>
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/initEditGatewayBasicDetails.do?gatewayId=<bean:write name="gatewayBean" property="gatewayId"/>&commProtocol=<bean:write name="gatewayBean" property="commProtocol"/>&url=searchGateway" tabindex="12">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
											</a>
										</td>
										<td align="center" valign="middle" class="tblrows">
											<a href="<%=basePath%>/createDuplicateGateway.do?gatewayId=<bean:write name="gatewayBean" property="gatewayId"/>" tabindex="12">
												<img alt="Duplicate" src="<%=basePath%>/images/copy.jpg" border="0">
											</a>
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
								<input type="button" value="   Create   " tabindex="14" name="c_btnCreate" class="light-btn"
									onclick="javascript:location.href='<%=basePath%>/initCreateGateway.do?/>'"/>
								<input type="button" name="Delete" tabindex="15" OnClick="removeRecord()" value="   Delete   " class="light-btn">															 
							</td>
							
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchGateway.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
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

