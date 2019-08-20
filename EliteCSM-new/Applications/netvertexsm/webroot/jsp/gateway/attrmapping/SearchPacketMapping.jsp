<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData" %>
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
	
	Map<String,String> commProtocolTypes= new HashMap<String,String>();
	commProtocolTypes.put("CCR","Credit-Control-Request");
	commProtocolTypes.put("CCA","Credit-Control-Response");
	commProtocolTypes.put("RAR","Re-Auth-Request");
	commProtocolTypes.put("RAA","Re-Auth-Response");
	commProtocolTypes.put("ASR","Abort-Session-Request");
	commProtocolTypes.put("ASA","Abort-Session-Response");	
	commProtocolTypes.put("AAR","Authenticate-Authorize-Request");
	commProtocolTypes.put("AAA","Authenticate-Authorize-Response");
	commProtocolTypes.put("STR","Session-Termination-Request");
	commProtocolTypes.put("STA","Session-Termination-Response");
	commProtocolTypes.put("ACR","Accounting-Request");
	commProtocolTypes.put("COA","Change-Of-Authorization");
	commProtocolTypes.put("DCR","Disconnect-Request");
	commProtocolTypes.put("AR","Access-Request");
	commProtocolTypes.put("AA","Access-Accept");	
%>
<style>

</style>
<script type="text/javascript">


$(document).ready(function(){
	setTitle('<bean:message bundle="gatewayResources" key="mapping.header"/>');
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
        alert('Please select atleast one Packet Mapping for Activation');
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
        alert('Select at least one Packet Mapping for DeActivation');
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
        alert('Select at least one Packet Mapping for remove process');
    }else{
        var msg;
        msg = 'Selected Packet Mapping(s) would be deleted. Would you like to continue?';       
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
     PacketMappingForm packetMappingForm = (PacketMappingForm) request.getAttribute("packetMappingForm"); 
     List lstMapping = packetMappingForm.getListSearchPacketMapping();
     
     long pageNo = packetMappingForm.getPageNumber();
     long totalPages = packetMappingForm.getTotalPages();
     long totalRecord = packetMappingForm.getTotalRecords();
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
			<bean:message bundle="gatewayResources" key="mapping.searchmapping"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" id="c_tblCrossProductList" align="right" border="0" >
			   <html:form action="/searchMapping" >
			   
			   	<html:hidden name="packetMappingForm" styleId="action" property="action" />
				<html:hidden name="packetMappingForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="packetMappingForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="packetMappingForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			   		 
					<tr> 
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="gatewayResources" key="mapping.name"/></td> 
						<td align="left" class="labeltext" valign="top" width="32%" > 
							<html:text property="name" styleId="name" maxlength="60" size="30" styleClass="name" tabindex="1"/>
						</td> 
				  	</tr>		
				  	<tr>
					 	<td align="left" class="captiontext" valign="top" width="20%">
					 		<bean:message bundle="gatewayResources" key="mapping.commprotocol"/></td>
						<td align="left" class="labeltext" valign="top" width="53%">
							<html:radio property="commProtocol" value="ALL" tabindex="2" />Any
							<html:radio property="commProtocol" value="RADIUS" tabindex="2" />RADIUS
							<html:radio property="commProtocol" value="DIAMETER" tabindex="2" />Diameter
						</td>
					</tr>
					<tr>
					 	<td align="left" class="captiontext" valign="top" width="20%">
					 		<bean:message bundle="gatewayResources" key="mapping.conversationtype"/></td>
						<td align="left" class="labeltext" valign="top" width="53%">
							<html:radio property="type" value="ALL" tabindex="2" />Any
							<html:radio property="type" value="GATEWAY TO PCRF" tabindex="2" />GATEWAY TO PCRF
							<html:radio property="type" value="PCRF TO GATEWAY" tabindex="2" />PCRF TO GATEWAY
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
		if(packetMappingForm.getAction()!=null && packetMappingForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/miscPacketMapping">
				
					<html:hidden name="packetMappingForm" styleId="action" property="action" />					
					<html:hidden name="packetMappingForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="packetMappingForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="2">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="24%" colspan="2">
								<bean:message bundle="gatewayResources" key="mapping.list" />
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
								<input type="button" value="   Create   " tabindex="3" name="c_btnCreate" class="light-btn"
									onclick="javascript:location.href='<%=basePath%>/initCreateMapping.do?/>'"/>
								<input type="button" name="Delete" OnClick="removeRecord()" tabindex="4" value="   Delete   " class="light-btn">								
							</td>
							<td class="btns-td" valign="baseline" align="center" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="searchMapping.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="searchMapping.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="searchMapping.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="searchMapping.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
							<% } %>
						<% } %>
						</td>
						</tr>
						<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="9">
							<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onclick="checkAll(this)" />&nbsp;
									</td>
									<td align="left" class="tblheader" valign="top" width="18%">
										<bean:message bundle="gatewayResources" key="mapping.name" />
									</td>
									<td align="center" class="tblheader" valign="top" width="28%">
										<bean:message bundle="gatewayResources" key="mapping.description" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message bundle="gatewayResources" key="mapping.commprotype" />
									</td>
									<td align="center" class="tblheader" valign="top" width="20%">
										<bean:message bundle="gatewayResources" key="mapping.conversationtype" />
									</td>																		
									<td align="center" class="tblheaderlastcol" valign="top" width="4%">
										<bean:message key="general.edit"/>
									</td>
									<td align="center" class="tblheader" valign="top" width="5%">
										Duplicate
									</td>									
								</tr>
							<%	
								if(lstMapping!=null && lstMapping.size()>0) { 
									int i=0;
							%>
								<logic:iterate id="mappingBean" name="packetMappingForm" property="listSearchPacketMapping"
									type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData">

									<tr id="dataRow" name="dataRow" >
										<td align="center" class="tblfirstcol">
											<input type="checkbox" name="select" tabindex="8" value="<bean:write name="mappingBean" property="packetMapId" />" onclick="onOffHighlightedRow(<%=i++%>,this)" />&nbsp;
										</td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/viewPacketMapping.do?mappingId=<bean:write name="mappingBean" property="packetMapId"/>" tabindex="1">
												<bean:write name="mappingBean" property="name" />&nbsp;												
											</a>											
										</td>
										<td align="left" class="tblrows">
											<bean:write name="mappingBean" property="description" />&nbsp;
										</td>
										<td align="left" class="tblrows">
											<bean:write name="mappingBean" property="commProtocol" />,&nbsp;
											<%=commProtocolTypes.get(mappingBean.getPacketType())%>&nbsp;											
										</td>
										<td align="left" class="tblrows">
											<bean:write name="mappingBean" property="type" />&nbsp;
										</td>													
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/initEditMapping.do?mappingId=<bean:write name="mappingBean" property="packetMapId"/>" tabindex="8">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></a>
										</td>
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/createDuplicatePacketMapping.do?mappingId=<bean:write name="mappingBean" property="packetMapId"/>" tabindex="12">
												<img alt="Duplicate" src="<%=basePath%>/images/copy.jpg" >
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
								<input type="button" value="   Create   " tabindex="13" name="c_btnCreate" class="light-btn"
									onclick="javascript:location.href='<%=basePath%>/initCreateMapping.do?/>'"/>
								<input type="button" name="Delete" tabindex="14" OnClick="removeRecord()" value="   Delete   " class="light-btn">							
							</td>
							
							<td class="btns-td" align="center" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="searchMapping.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="searchMapping.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="searchMapping.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchMapping.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="searchMapping.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchMapping.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
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

