<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>

<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.web.gateway.profile.form.SearchProfileForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"%>
<%@ page import="com.elitecore.netvertexsm.hibernate.gateway.HGatewayDataManager"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>

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
	int iIndex = 0;
	String deleteProfileIds=(String)request.getAttribute("deleteProfiles");
%>

<style>
.light-btn {
	border: medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold
}
</style>

<script type="text/javascript">

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
        alert('Please select atleast one Gateway Profile for Activation');
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
        alert('Please select atleast one Gateway Profile for DeActivation');
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
        alert('At least select one Gateway Profile for remove process');
    }else{
        var msg;
        msg = 'You are going to delete selected gateway profiles, Do you want to continue ?';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action.value = 'delete';
        	document.forms[1].submit();
        }
    }
}


$(document).ready(function(){
	setTitle('<bean:message bundle="gatewayResources" key="gatewy.profile.header" />');
	$("#profileName").focus();
	checkAll(document.getElementsByName("toggleAll")[0]);
	if('<%=deleteProfileIds%>' == 'no') {
		
	}else if('<%=deleteProfileIds%>' == 'yes') {
		alert("profile can not be deleted because it in use by gateway");
	}
	});
     
</script>

<%

	SearchProfileForm searchProfileForm = (SearchProfileForm) request.getAttribute("searchProfileForm");
	List lstProfile = searchProfileForm.getListSearchProfile();

	long pageNo = searchProfileForm.getPageNumber();
	long totalPages = searchProfileForm.getTotalPages();
	long totalRecord = searchProfileForm.getTotalRecords();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	int count = 1;
	String strPageNo = String.valueOf(pageNo);
	String strTotalPages = String.valueOf(totalPages);
	String strTotalRecords = String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<%@include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
		<td width="10">&nbsp;</td>
		<td width="100%" colspan="2" valign="top" class="box">
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">
			<tr>
				<td class="table-header" colspan="5"><bean:message
					bundle="gatewayResources" key="gateway.profile.search" /></td>
			</tr>
			<tr>
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="3">
				<table width="100%" id="c_tblCrossProductList" align="right" border="0">
					<html:form action="/searchProfile">

						<html:hidden name="searchProfileForm" styleId="action" property="action" />
						<html:hidden name="searchProfileForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>" />
						<html:hidden name="searchProfileForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
						<html:hidden name="searchProfileForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />

						<tr>
							<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="gatewayResources" key="gateway.profile" /></td>
							<td align="left" class="labeltext" valign="top" width="32%">
							<html:text property="profileName" maxlength="60" size="30" styleId="profileName" tabindex="1"/>
						</tr>

						<tr>
							<td class="btns-td" valign="middle">&nbsp;</td>
							<td  valign="middle" align="left" style="padding-left:0.2em">
								<input type="submit" name="Search" tabindex="5" width="5%" Onclick="validateSearch()" value="   Search   " class="light-btn" /> 
							</td>
						</tr>
					</html:form>
<%
		if (searchProfileForm.getAction() != null && searchProfileForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)) {
%>
		<html:form action="/miscGatewayProfile">

		<html:hidden name="miscGatewayProfileForm" styleId="action" property="action" />
		<html:hidden name="miscGatewayProfileForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
		<html:hidden name="miscGatewayProfileForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
		<tr>
			<td colspan="2">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0">
					<tr>
						<td class="table-header" width="24%" colspan="2">
							<bean:message bundle="gatewayResources" key="gatewat.prifile.list" /></td>
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
							<input type="button" value="   Create   " tabindex="6" name="c_btnCreate" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateProfile.do?/>'" />
							<input type="button" name="Delete" tabindex="7" OnClick="removeRecord()" value="   Delete   " class="light-btn">							
						</td>
						<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="searchProfile.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="searchProfile.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="searchProfile.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="searchProfile.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
							<% } %>
						<% } %>
						</td>
					</tr>
					
					<tr class="vspace">
						<td class="btns-td" valign="middle" colspan="9">
						<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
						
						<html:hidden name="miscGatewayProfileForm" styleId="action" property="action" />
						<html:hidden name="miscGatewayProfileForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
						<html:hidden name="miscGatewayProfileForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
						
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" tabindex="10" value="checkbox" onclick="checkAll(this)" /></td>
									<td align="left" " class="tblheader" valign="top" width="15%">
										<bean:message bundle="gatewayResources" key="gateway.profile" /></td>
									<td align="left" class="tblheader" valign="top" width="10%">
										<bean:message bundle="gatewayResources" key="gateway.commprotocol" /></td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message bundle="gatewayResources" key="gateway.description" /></td>
									<td align="center" class="tblheader" valign="top" width="5%">
										<bean:message key="general.createddate" />
									</td>
									<td align="center" class="tblheader" valign="top" width="10%">
										<bean:message key="general.lastmodifieddate" />
									</td>	
									<td align="center" class="tblheaderlastcol" valign="top" width="3%">
										<bean:message key="general.edit"/>
									</td>
									
								</tr>
<%
		if (lstProfile != null && lstProfile.size() > 0) {
			int i=0;
%>
						<logic:iterate id="profileBean" name="searchProfileForm" property="listSearchProfile" scope="request"
									   type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData">
								<tr id="dataRow" name="dataRow" >
									<td align="center" class="tblfirstcol">
										<input type="checkbox" tabindex="11" name="select" value="<bean:write name="profileBean" property="profileId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  /></td>
									<td align="left" class="tblrows">
										<a href="<%=basePath%>/viewGatewayProfile.do?profileId=<bean:write name="profileBean" property="profileId"/>" tabindex="11">
											<bean:write name="profileBean" property="profileName" /> 
										</a></td>
									<td align="left" class="tblrows">
									<%=CommunicationProtocol.fromId(profileBean.getCommProtocolId()).name%>&nbsp;</td>
									<td align="left" class="tblrows">
										<bean:write name="profileBean" property="description" />&nbsp;</td>
									<td align="left" class="tblrows">
										<%=EliteUtility.dateToString(profileBean.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
									<td align="left" class="tblrows">
										<%=EliteUtility.dateToString(profileBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>	
									<td align="center" class="tblrows">
										<a href="<%=basePath%>/initEditGatewayProfile.do?profileId=<bean:write name="profileBean" property="profileId"/>&commProtocol=<bean:write name="profileBean" property="commProtocolId"/>&url=searchGatewayProfile" tabindex="11">
											<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0" /></a>
									</td>
								</tr>
								<% count=count+1; %>
								<% iIndex += 1; %>
						</logic:iterate>
<%		} else {	%>
						<tr>
							<td align="center" class="tblfirstcol" colspan="9">No Records Found.</td>
						</tr>
<%		}		%>
					</table>
							</td>
						</tr>
		 
					<tr class="vspace">
						<td class="btns-td" valign="middle" colspan="5">								
							<input type="button" value="   Create   " tabindex="12" name="c_btnCreate" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateProfile.do?/>'" />
							<input type="button" name="Delete" tabindex="13" Onclick="removeRecord()" value="   Delete   " class="light-btn">							
						</td>
						<td class="btns-td" align="right">
				     
				     <!--  <td class="btns-td" valign="middle" colspan="3" >&nbsp; </td>
			    	 <td class="btns-td" align="right" > -->
					
						<% if(totalPages >= 1) { %>
						  	<% if(pageNo == 1){ %>
						  	    <a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="searchProfile.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="searchProfile.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="searchProfile.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchProfile.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="searchProfile.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchProfile.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>						 	
							<% } %>
						<% } %>
						</td>						
						</tr>
						</table>
						</td>
						</tr>										
				 				
		</html:form>
		<%}%>
  </table>  
		</td> 
	</tr>	          
</table>
</td>
</tr>
<%@include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table> 
