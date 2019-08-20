<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.text.*,java.util.*"%>

<%@ page import="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.SearchConcurrentLoginPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConcurrentLoginPolicyConstant"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.hibernate.rm.concurrentloginpolicy.HConcurrentLoginPolicyDataManager"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.util.Collection"%>
<%@ page import="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.MiscConcurrentLoginPolicyForm"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>
<script>
function  validateSearch(){
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
function navigatePageWithStatus(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")
	$("#newFormData").append("<input type='hidden' name='action' value='new'>")
	var name = $("#status").attr("name");
	var val=$('input[name=status]:radio:checked').val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
	
	var name = $("#concurrentLoginPolicyId").attr("name");
	var val=$('input[name=concurrentLoginPolicyId]:radio:checked').val();
	
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
					 .submit();
}

function show(){
	document.miscConcurrentLoginPolicyForm.action.value = 'show';
	
	
		var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
	
			var b = true;
			for (i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
			alert("Please select atleast one Conc. Login Policies for Activation.")
			
			}else{
			
			    	document.miscConcurrentLoginPolicyForm.submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}

}
function hide(){

	document.miscConcurrentLoginPolicyForm.action.value = 'hide';
	
	var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
	
			var b = true;
			for (i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
			alert("Please select atleast one Conc. Login Policies for DeActivation.")
			
			}else{
			
    				document.miscConcurrentLoginPolicyForm.submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
	}

}

function removeData(){

	var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Selection Is Required For Delete Operation');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchconcurentloginpolicyjsp.delete.query"/>';        
        //msg = "All the selected Conc. Login Policies would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
        	document.miscConcurrentLoginPolicyForm.action.value = 'delete';
        	document.miscConcurrentLoginPolicyForm.submit();
        }
    }
 
}	
function  checkAll(){
var arrayCheck = document.getElementsByName('select');
	 	if( document.forms[1].toggleAll.checked == true) {
		 	for (i = 0; i < arrayCheck.length;i++)
				arrayCheck[i].checked = true ;
	    } else if (document.forms[1].toggleAll.checked == false){
			for (i = 0; i < arrayCheck.length; i++)
				arrayCheck[i].checked = false ;
		}
}

function navigatePageWithAction(action,name) {
	createNewForm("newFormData",action);
	var name = $("#"+name).attr("name");
	var val = $("#"+name).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")
					 .append("<input type='hidden' name='action' value='new'>")
					 .submit();
	
}
setTitle('<bean:message bundle="radiusResources" key="concurrentloginpolicy.concurrentloginpolicy"/>');
</script>



<%
 
     SearchConcurrentLoginPolicyForm searchConcurrentLoginPolicyForm = (SearchConcurrentLoginPolicyForm)request.getAttribute("searchConcurrentLoginPolicyForm");

     Collection lstConcurrentLoginPolicy = searchConcurrentLoginPolicyForm.getConcurrentLoginPolicyList();
  
     String strName = searchConcurrentLoginPolicyForm.getName();
     String strPolicyType = searchConcurrentLoginPolicyForm.getConcurrentLoginPolicyId();
     String strStatus =searchConcurrentLoginPolicyForm.getStatus();
     long pageNo = searchConcurrentLoginPolicyForm.getPageNumber();
     long totalPages = searchConcurrentLoginPolicyForm.getTotalPages();
     long totalRecord = searchConcurrentLoginPolicyForm.getTotalRecords();
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW")); 
    
	 int count=1;
    
     String strPageNumber = String.valueOf(pageNo);     
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
   
%>

<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%"
						class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">

							<tr>
								<td class="table-header" colspan="5"><bean:message
										bundle="radiusResources" key="concurrentloginpolicy.search" />
								</td>
							</tr>
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblCrossProductList"
										id="c_tblCrossProductList" align="right" border="0">
										<html:form action="/searchConcurrentLoginPolicy.do">
											<input type="hidden" name="c_strActionMode" value="102231105" />
											<html:hidden name="searchConcurrentLoginPolicyForm"
												styleId="action" property="action" />
											<html:hidden name="searchConcurrentLoginPolicyForm"
												styleId="pageNumber" property="pageNumber" />
											<html:hidden name="searchConcurrentLoginPolicyForm"
												styleId="totalPages" property="totalPages"
												value="<%=strTotalPages%>" />
											<html:hidden name="searchConcurrentLoginPolicyForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />
											<html:hidden name="searchConcurrentLoginPolicyForm"
												styleId="auditUId" property="auditUId"/>
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%">
													 <bean:message bundle="radiusResources" key="concurrentloginpolicy.name" />
													 <ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.name" 
													 header="concurrentloginpolicy.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" property="name" size="30"
														maxlength="30" tabindex="1" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%">
													<bean:message bundle="radiusResources" key="concurrentloginpolicy.concurrentloginpolicytype"/>
													<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.policytype" 
													header="concurrentloginpolicy.concurrentloginpolicytype"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<html:radio name="searchConcurrentLoginPolicyForm"
														styleId="concurrentLoginPolicyId"
														property="concurrentLoginPolicyId" value="I" tabindex="2" />Individual
													<html:radio name="searchConcurrentLoginPolicyForm"
														styleId="concurrentLoginPolicyId"
														property="concurrentLoginPolicyId" value="G" tabindex="3" />Group
													<html:radio name="searchConcurrentLoginPolicyForm"
														styleId="concurrentLoginPolicyId"
														property="concurrentLoginPolicyId" value="A" tabindex="4" />All
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%">
													<bean:message bundle="radiusResources" key="concurrentloginpolicy.status"/>
													<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.status" 
													header="concurrentloginpolicy.status"/>	
												</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<html:radio name="searchConcurrentLoginPolicyForm"
														styleId="status" property="status" value="Show"
														tabindex="5" />Show <html:radio
														name="searchConcurrentLoginPolicyForm" styleId="status"
														property="status" value="Hide" tabindex="6" />Hide <html:radio
														name="searchConcurrentLoginPolicyForm" styleId="status"
														property="status" value="All" tabindex="7" />All
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" tabindex="8" name="Search" width="5%"
													name="ConcurrentLoginPolicyName" Onclick="validateSearch()"
													value="   Search   " class="light-btn" /> <!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
													<input type="button" tabindex="9" name="Create"
													value="   Create   " class="light-btn"
													onclick="navigatePageWithStatus('addConcurrentLoginPolicy.do','name');">
												</td>
											</tr>

										</html:form>

										<%
			
				if(searchConcurrentLoginPolicyForm.getAction()!=null && searchConcurrentLoginPolicyForm.getAction().equalsIgnoreCase(ConcurrentLoginPolicyConstant.LISTACTION)){
			
	       	%>
										<html:form action="/miscConcurrentLoginPolicy">
											<html:hidden name="miscConcurrentLoginPolicyForm"
												styleId="action" property="action" />
											<html:hidden name="miscConcurrentLoginPolicyForm"
												styleId="pageNumber" property="pageNumber"
												value="<%=strPageNumber%>" />
											<html:hidden name="miscConcurrentLoginPolicyForm"
												styleId="totalPages" property="totalPages"
												value="<%=strTotalPages%>" />
											<html:hidden name="miscConcurrentLoginPolicyForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />
											<html:hidden name="miscConcurrentLoginPolicyForm"
												styleId="name" property="name" value="<%=strName%>" />
											<html:hidden name="miscConcurrentLoginPolicyForm"
												styleId="auditUId" property="auditUId"/>
											<!-- <html:hidden name="miscConcurrentLoginPolicyForm" styleId="concurrentLoginPolicyId" property="concurrentLoginPolicyId" value="<%=strPolicyType%>"/>  -->
											<!-- 	<html:hidden name="miscConcurrentLoginPolicyForm" styleId="status" 					property="status" value="<%=strStatus%>"/>  -->
											<tr>
												<td width="100%" colspan="2">&nbsp;
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<!-- <tr > 
						<td align="left" class="tblrows" colspan="11" >&nbsp;</td>
						<td class="table-header" width="24%" colspan="2" ><bean:message bundle="radiusResources" key="concurrentloginpolicy.concurrentloginpolicylist" /></td>
					   <tr> -->
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="radiusResources"
																	key="concurrentloginpolicy.concurrentloginpolicylist" /></td>

															<td align="right" class="blue-text" valign="middle"
																width="50%">
																<% if(totalRecord == 0){ %> <% } else if(pageNo == totalPages+1) { %>
																[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
																<% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
																of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
																of <%= totalRecord %> <% } %>
															</td>
														</tr>
														<tr>
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle"><input
																type="button" name="Show" Onclick="show()"
																value="   Show   " class="light-btn"> <input
																type="button" name="Hide" Onclick="hide()"
																value="   Hide   " class="light-btn"> <html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /></td>

															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <!-- 	<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%=pageNo+1%>)"  onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" />
										    <img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
									 --> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>

																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<!--	<img  src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)"   onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
											<img  src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
											<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
											<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >  -->
																<% } else if(pageNo == totalPages){ %> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<!--	<img  src="<%=basePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
											<img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
											<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
											<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"   onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" > -->
																<% } else { %> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<!--<img  src="<%=basePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)"  onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
								  			<img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
											<img  src="<%=basePath%>/images/next.jpg"  name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
											<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" > -->
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <!-- <img  src="<%=basePath%>/images/first.jpg"  name="Image511"  onclick="navigate('first',<%=1%>)"onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
										<img  src="<%=basePath%>/images/previous.jpg"  name="Image5"  onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" > -->
																<% } %> <% } %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
																	
														</tr>
														<tr>
															<td class="btns-td" valign="middle" colspan="2">
																<table width="100%" border="0" cellpadding="0"
																	<tr>
																		<td align="center" class="tblheader" valign="top"
																			width="1%"><input type="checkbox"
																			name="toggleAll" value="checkbox"
																			onclick="checkAll()" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="radiusResources"
																				key="concurrentloginpolicy.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="20%"><bean:message
																				bundle="radiusResources"
																				key="concurrentloginpolicy.name" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="*"><bean:message bundle="radiusResources"
																				key="concurrentloginpolicy.description" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="25%"><bean:message
																				bundle="radiusResources"
																				key="concurrentloginpolicy.concurrentloginpolicytype" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="25%"><bean:message
																				bundle="radiusResources"
																				key="concurrentloginpolicy.lastmodifieddate" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="radiusResources"
																				key="concurrentloginpolicy.status" /></td>
																		<!--<td align="center" class="tblheader" valign="top" width="7%">Edit</td>
											-->
																	</tr>
																	<%if(lstConcurrentLoginPolicy!=null && lstConcurrentLoginPolicy.size()>0){%>
																	<logic:iterate id="concurrentLoginPolicyBean"
																		name="searchConcurrentLoginPolicyForm"
																		property="concurrentLoginPolicyList"
																		type="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData">
																		<bean:define id="policyType"
																			name="concurrentLoginPolicyBean"
																			property="policyType"
																			type="com.elitecore.elitesm.datamanager.radius.system.standardmaster.data.IStandardMasterData" />
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="concurrentLoginPolicyBean" property="concurrentLoginId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewConcurrentLoginPolicy.do?concurrentLoginId=<bean:write name="concurrentLoginPolicyBean" property="concurrentLoginId"/>"><bean:write
																						name="concurrentLoginPolicyBean" property="name" /></a></td>
																			<td align="left" class="tblrows"><%=EliteUtility.formatDescription(concurrentLoginPolicyBean.getDescription()) %>&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="policyType" property="name" />&nbsp;</td>
																			<td align="left" class="tblrows"><%=EliteUtility.dateToString(concurrentLoginPolicyBean.getLastModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%></td>
																			<td align="center" class="tblrows">
																				<%
											String CommonStatusValue = (String)org.apache.struts.util.RequestUtils.lookup(pageContext, "concurrentLoginPolicyBean", "commonStatusId", null);
											if(CommonStatusValue.equals(ConcurrentLoginPolicyConstant.HIDE_STATUS_ID) ||   CommonStatusValue.equals(ConcurrentLoginPolicyConstant.DEFAULT_STATUS_ID)){ 
										%> <img src="<%=basePath%>/images/hide.jpg" border="0">
																				<% }else if(CommonStatusValue.equals(ConcurrentLoginPolicyConstant.SHOW_STATUS_ID)){ %>
																				<img src="<%=basePath%>/images/show.jpg" border="0">
																				<% } %>
																			</td>
																		</tr>
																		<% count=count+1; %>
																	</logic:iterate>
																	<% }else{%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="7">No
																			Records Found.</td>
																	</tr>
																	<%	}%>
																</table>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle"><input
																type="button" name="Show" Onclick="show()"
																value="   Show   " class="light-btn"> <input
																type="button" name="Hide" Onclick="hide()"
																value="   Hide   " class="light-btn"> <html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /></td>

															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } else if(pageNo == totalPages){ %> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<% } else { %> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchConcurrentLoginPolicy.do?resultPolicyType=<%=strPolicyType%>&resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
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
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>
