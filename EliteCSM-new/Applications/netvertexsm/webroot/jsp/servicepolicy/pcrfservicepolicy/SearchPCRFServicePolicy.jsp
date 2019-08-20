<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.SearchPCRFServicePolicyForm"%>
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
$(document).ready(function() {
	$("#name").focus();	
	setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.pcrfpolicy"/>');
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
        alert('Please select atleast one PCRF Service Policy for Activation');
    }else{
    	document.miscPCRFPolicyForm.action.value = 'show';
    	document.miscPCRFPolicyForm.submit();
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
        alert('Please select atleast one PCRF Service Policy for DeActivation');
    }else{
    	document.miscPCRFPolicyForm.action.value = 'hide';
    	document.miscPCRFPolicyForm.submit();
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
        alert('Please select atleast one PCRF Service Policy for remove process');
    }else{
        var msg;
        //msg = '<bean:message bundle="alertMessageResources" key="alert.searchstaffjsp.delete.query"/>';        
        msg = "All the selected PCRF Service Policy would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.miscPCRFPolicyForm.action.value = 'delete';
        	document.miscPCRFPolicyForm.submit();
        }
    }
}
function  checkAll(){
		var dataRows = document.getElementsByName('dataRow');
	 	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('select');
		 	for (i = 0; i < selectVars.length;i++){
				selectVars[i].checked = true ;
				dataRows[i].className='onHighlightRow';				
		 	}
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('select');	    
			for (i = 0; i < selectVars.length; i++){							
				selectVars[i].checked = false ;
				dataRows[i].className='offHighlightRow';
			}
		}
}

</script>

<%
	 SearchPCRFServicePolicyForm searchPCRFServiceForm = (SearchPCRFServicePolicyForm)request.getAttribute("searchPCRFServiceForm"); 
     List lstPolicy = searchPCRFServiceForm.getListSearchPcrfPolicy();
     
     long pageNo = searchPCRFServiceForm.getPageNumber();
     long totalPages = searchPCRFServiceForm.getTotalPages();
     long totalRecord = searchPCRFServiceForm.getTotalRecords();
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
			<td class="table-header" colspan="5">
			<bean:message bundle="servicePolicyProperties" key="servicepolicy.searchservicepolicy"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" id="c_tblCrossProductList" align="right" border="0" >
			   
			   <html:form action="/searchPCRFPolicy" >
			   
			   	<html:hidden name="searchPCRFServiceForm" styleId="action" property="action" />
				<html:hidden name="searchPCRFServiceForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="searchPCRFServiceForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="searchPCRFServiceForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			   		
					<tr> 
						<td align="left" class="labeltext" valign="top" width="10%" style="padding-left: 24px">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.name"/></td> 
						<td align="left" class="labeltext" valign="top" width="32%" > 
							<html:text property="name" styleId="name" maxlength="60" size="30" tabindex="1" styleClass="name"/>
						</td> 
				  	</tr>				  	
				  	<tr> 
	        			<td>&nbsp;</td> 
            			<td align="left"  valign="middle" >             
		        			&nbsp;&nbsp;<input type="submit" name="Search" width="5%" tabindex="2" Onclick="validateSearch()" value="   Search   " class="light-btn" />
 						 	<input type="button" name="c_btn_manage" onclick="javascript:location.href='<%=basePath%>/manageOrderPolicy.do?/>'" value="Manage Order" class="light-btn" tabindex="2">
	        			</td> 
   		  			</tr>
				</html:form>
	<%
		if(searchPCRFServiceForm.getAction()!=null && searchPCRFServiceForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/miscPCRFPolicy">				
					
					<html:hidden name="miscPCRFPolicyForm" styleId="action" property="action" />
					<html:hidden name="miscPCRFPolicyForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="miscPCRFPolicyForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				
				<tr>
					
					<td colspan="2"><table cellSpacing="0" cellPadding="0" width="100%" border="0">
						<tr>
							<td class="table-header" width="24%" colspan="2">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.servicepolicylist" />
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
								<input type="button" value="   Create   " tabindex="3" name="c_btnCreate" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreatePCRFService.do?'"/>
								<input type="button" name="Delete" tabindex="4" OnClick="removeRecord()" value="   Delete   " class="light-btn">
								<input type="button" name="Active" tabindex="5" Onclick="active()"	value="   Active   " class="light-btn">
								<input type="button" name="InActive" tabindex="6" Onclick="inactive()" value="   Inactive   " class="light-btn">
							</td>
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
				
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="initSearchPCRFService.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="initSearchPCRFService.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="initSearchPCRFService.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="initSearchPCRFService.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
							<% } %>
						<% } %>
						</td>
						</tr>
						
						<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="9">
							<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="5%">
										<input type="checkbox" tabindex="7" name="toggleAll" value="checkbox" onclick="checkAll()" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.name" />
									</td>
									<td align="center" class="tblheader" valign="top" width="25%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.ruleset" />
									</td>
									<td align="left" class="tblheader" valign="top" width="25%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.description" />										
									</td>
									<td align="left" class="tblheader" valign="top" width="25%">
										<bean:message key="general.lastmodifieddate" />										
									</td>
									<td align="center" class="tblheader" valign="top" width="5%">
										<bean:message bundle="servermgrResources" key="servermgr.status" />
									</td>
									<td align="center" class="tblheaderlastcol" valign="top" width="5%">
										<bean:message key="general.edit" />
									</td>
								</tr>
<%	
		if(lstPolicy!=null && lstPolicy.size()>0) {
			int i=0;
%>

								<logic:iterate id="servicePolicyBean" name="searchPCRFServiceForm"
									property="listSearchPcrfPolicy"
									type="com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData">

									<tr id="dataRow" name="dataRow" >
										<td align="center" class="tblfirstcol">
											<input type="checkbox" name="select" tabindex="8"
											value="<bean:write name="servicePolicyBean" property="pcrfPolicyId"/>"  id="select"  onclick="onOffHighlightedRow(<%=i++%>,this)" />
										</td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/viewPCRFPolicy.do?pcrfPolicyId=<bean:write name="servicePolicyBean" property="pcrfPolicyId"/>" tabindex="8">
												<bean:write name="servicePolicyBean" property="name" />&nbsp;												
											</a>											
										</td>
										<td align="left" class="tblrows">
											<bean:write name="servicePolicyBean" property="ruleset" />&nbsp;
										</td>	
										<td align="left" class="tblrows" valign="top">
											<%=EliteUtility.formatDescription(servicePolicyBean.getDescription())%>&nbsp;
										</td>
										<td align="left" class="tblrows" valign="top">
											<%=EliteUtility.dateToString(servicePolicyBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>
										</td>
<% 										if(servicePolicyBean.getStatus().trim().equalsIgnoreCase("CST01")) {						%>
											<td align="center" class="tblrows">
												<img src="<%=basePath%>/images/show.jpg" /></td>
<%										} else {												 %>												
											<td align="center" class="tblrows">
												<img src="<%=basePath%>/images/hide.jpg" /></td>
<%										}											 %>												
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/initEditPCRFServicePolicy.do?pcrfPolicyId=<bean:write name="servicePolicyBean" property="pcrfPolicyId"/>&url=SearchPCRFServicePolicy" tabindex="8">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></a>
										</td>														
									</tr>
									<% count=count+1; %>
									<% iIndex += 1; %>
								</logic:iterate>												
<%		}else{	%>
		<logic:empty  name="searchPCRFServiceForm" property="listSearchPcrfPolicy">
						<tr>
		    	<td colspan="7" align="center" class="tblfirstcol" ><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/> </td>
						</tr>
	    </logic:empty>
<%		}	%>
							</table>
							</td>
						</tr>
						 
						<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="5">	
								<input type="button" value="   Create   " name="c_btnCreate" tabindex="9" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreatePCRFService.do?'"/>
								<input type="button" name="Delete" Onclick="removeRecord()" tabindex="10" value="   Delete   " class="light-btn">							
								<input type="button" name="Active" Onclick="active()" tabindex="11" value="   Active   " class="light-btn">
								<input type="button" name="InActive" Onclick="inactive()" tabindex="12" value="   Inactive   " class="light-btn">
							</td>
							<td class="btns-td" align="right">
					
						<% if(totalPages >= 1) { %>
						  	<% if(pageNo == 1){ %>
						  	    <a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<%  if(pageNo-1 == 1){ %>
						  		    <a  href="initSearchPCRFService.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									
						 		<% } else if(pageNo == totalPages){ %>
						 		
						 		    <a  href="initSearchPCRFService.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								
						  		<% } else { %>
						  		    <a  href="initSearchPCRFService.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="initSearchPCRFService.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  		
								<% } %>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
						 	
						 	<a  href="initSearchPCRFService.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="initSearchPCRFService.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						 	
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
		<td width="1">&nbsp;</td>
</tr>
	
  	<%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
