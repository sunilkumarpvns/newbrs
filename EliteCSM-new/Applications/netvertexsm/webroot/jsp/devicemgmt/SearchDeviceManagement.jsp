<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.*"%>
<%@page import="com.elitecore.netvertexsm.web.devicemgmt.form.DeviceManagementForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
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
    
    var chkBoxElements = document.getElementsByName('tacDetailIds');
    
    for (i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one Device Management Instance for remove process');
    }else{
        var msg;
        msg = 'All selected Device Management Instances would be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/deviceMgmt.do?method=delete';
       	 	document.forms[1].submit();
        }
    }
}
function upload(){
	document.forms[1].action = '<%=request.getContextPath()%>/deviceMgmt.do?method=initUploadCSV';
	document.forms[1].submit();
}
function  checkAll(){

		var dataRows = document.getElementsByName('dataRow');					
	 	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('tacDetailIds');
		 	for (i = 0; i < selectVars.length;i++){
				selectVars[i].checked = true ;
				dataRows[i].className='onHighlightRow';
		 	}
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('tacDetailIds');	    
			for (i = 0; i < selectVars.length; i++){
				selectVars[i].checked = false ;
				dataRows[i].className='offHighlightRow';
			}
		}
}

function exportCSV(){
	    var selectVar = false;
	    
	    var chkBoxElements = document.getElementsByName('tacDetailIds');
	    var selectedDatas = 0;
	    for (i=0; i < chkBoxElements.length; i++){
	        if(chkBoxElements[i].checked == true){
	        		selectedDatas++;
	                selectVar = true;
	        }
	    }
	    if(selectedDatas > 500){
	    	alert('Only 500 selected Device Management Instances will be exported at a time');
	    	return false;
	    }
	    if(selectVar == false){
	        alert('At least select one Device Management Instance for export');
	        return false;
	    }else{
	        var msg;
	        msg = 'All selected Device Management Instances would be exported. Would you like to continue ?';        
	        var agree = confirm(msg);
	        if(agree){
	       	    document.forms[1].action = '<%=request.getContextPath()%>/deviceMgmt.do?method=exportCSV';
	       	 	document.forms[1].submit();
	        }else{
	        	return false;
	        }
	    }
	}
	
function exportAllCSV(){
		var selectVars = document.getElementsByName('tacDetailIds');
		if(selectVars.length ==0){
			alert('At least one Device Management Instance should be configured for Export');
			return false;
		}
		 var msg;
	     msg = 'All the Device Management Instances would be exported. Would you like to continue ?';        
	     var agree = confirm(msg);
	     if(agree){
	    		 document.forms[1].action = '<%=request.getContextPath()%>/deviceMgmt.do?method=exportAllCSV';
	    		document.forms[1].submit();
	     }else{
	    	 return false;
	     }
		
	}

$(document).ready(function(){
	setTitle('<bean:message  bundle="deviceMgmtResources" key="devicemgmt.title"/>');
	$("#tac").focus();
	checkAll(document.getElementsByName("toggleAll")[0]);
});
</script>

<%
	DeviceManagementForm deviceManagementForm = (DeviceManagementForm) request.getAttribute("deviceManagementForm");
     List tacDetailDataList = deviceManagementForm.getTacDetailDataList();    
             
     long pageNo = deviceManagementForm.getPageNumber();
     long totalPages = deviceManagementForm.getTotalPages();
     long totalRecord = deviceManagementForm.getTotalRecords();
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
			<td class="table-header" colspan="3"><bean:message  bundle="deviceMgmtResources" key="devicemgmt.search.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" align="left" border="0" >
				   <html:form action="/deviceMgmt.do?method=search" >
				   	<html:hidden name="deviceManagementForm" styleId="actionName" property="actionName" />
					<html:hidden name="deviceManagementForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
				   	<html:hidden name="deviceManagementForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
				   	<html:hidden name="deviceManagementForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				      
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="35%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.tac" /></td> 
					<td align="left" valign="top" width="*" > 
						<html:text name="deviceManagementForm" property="tac" maxlength="8"  size="8" styleId="tac"/>
					</td> 
				  </tr>
	
				  <tr> 
					<td align="left" class="captiontext" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.brand" /></td> 
					<td align="left" valign="top" > 
						<html:text name="deviceManagementForm" property="brand" maxlength="32"  size="32" styleId="brand"/>
					</td> 
				  </tr>
	
				  <tr> 
					<td align="left" class="captiontext" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.model" /></td> 
					<td align="left" valign="top" > 
						<html:text name="deviceManagementForm" property="model" maxlength="32" size="32" styleId="model"/>
					</td> 
				  </tr>
				  
				  <tr> 
					<td align="left" class="captiontext" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.hardwaretype" /></td> 
					<td align="left" valign="top" > 
						<html:text name="deviceManagementForm" property="hardwareType" maxlength="32" size="32" styleId="hardwareType"/>
					</td> 
				  </tr>
				  	
				  <tr> 
					<td align="left" class="captiontext" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.operatingsystem" /></td> 
					<td align="left" valign="top" > 
						<html:text name="deviceManagementForm" property="operatingSystem" maxlength="32" size="32" styleId="operatingSystem"/>
					</td> 
				  </tr>
				  
				   <tr> 
					<td align="left" class="captiontext" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.additionalinfo" /></td> 
					<td align="left" valign="top" > 
						<html:text name="deviceManagementForm" property="additionalInfo" maxlength="64" size="32" styleId="additionalInfo"/>
					</td> 
				  </tr>
				  
				  			 
				  <tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td valign="middle" >             
		        		<input type="submit" name="Search"  Onclick="validateSearch()" value="   Search   "  class="light-btn" /> 
	        			<input type="button" name="SampleCSV" value="   Sample CSV   "  class="light-btn" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="devicemgmt.samplecsv"/>','<bean:message bundle="deviceMgmtResources" key="devicemgmt.samplecsv" />')"/> 
	        		</td>
   		  		  </tr>	
				  
				</html:form> 
				
	<%
		if(deviceManagementForm.getActionName()!=null && deviceManagementForm.getActionName().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/deviceMgmt.do">
				
					<html:hidden name="deviceManagementForm" styleId="actionName" property="actionName" />
					<html:hidden name="deviceManagementForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="deviceManagementForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="50%" >
								<bean:message bundle="deviceMgmtResources" key="devicemgmt.search.list" /> 
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
							<td class="btns-td" valign="middle" width="100%">
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/deviceMgmt.do?method=initCreate'"  class="light-btn" /> 
								<input type="button"   value="   Delete   " onclick="removeRecord()" class="light-btn">
								<input type="button"   value="   Import   " onclick="upload()" class="light-btn">
								<input type="button"   value="   Export "  onclick="exportCSV()" class="light-btn" id="btnExport">		
								<input type="button"   value="   Export All " onclick="exportAllCSV()" class="light-btn" id="btnExportAll">															
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
										<td align="center" class="tblheader" valign="top" width="1%">
											<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onclick="checkAll(this)" /></td>
										<td align="left" class="tblheaderfirstcol" valign="top" width="10%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.tac" /></td>
										<td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.brand" /></td>
										<td align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.model" /></td>
										<td align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.hardwaretype" /></td>								
										<td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.operatingsystem" /></td>
										<td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.year" /></td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="deviceMgmtResources" key="devicemgmt.additionalinfo" /></td>
										<td align="left" class="tblheaderlastcol" valign="top" width="1%"><bean:message key="general.edit" /></td>
									</tr>
									<%	
										if(tacDetailDataList!=null && tacDetailDataList.size()>0) {
											int i=0;
									%>
								<logic:iterate id="tacDetailBean" name="deviceManagementForm"  property="tacDetailDataList" type="com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData">
										<tr id="dataRow" name="dataRow" >
										<td align="center" class="tblfirstcol" valign="top"><input type="checkbox" tabindex="8" name="tacDetailIds" value="<bean:write name="tacDetailBean" property="tacDetailId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)" /></td>
										<td align="left" class="tblrows" valign="top">
											<a href="<%=basePath%>/deviceMgmt.do?method=view&tacDetailId=<bean:write name="tacDetailBean" property="tacDetailId"/>" tabindex="8">
												<bean:write name="tacDetailBean" property="tac" />&nbsp;</a>
										</td>
										<td align="left" class="tblrows" valign="top"><bean:write name="tacDetailBean" property="brand" />&nbsp;</td>		
										<td align="left" class="tblrows" valign="top"><bean:write name="tacDetailBean" property="model" />&nbsp;</td>		
										<td align="left" class="tblrows" valign="top"><bean:write name="tacDetailBean" property="hardwareType" />&nbsp;</td>		
										<td align="left" class="tblrows" valign="top"><bean:write name="tacDetailBean" property="operatingSystem" />&nbsp;</td>		
										<td align="left" class="tblrows" valign="top"><bean:write name="tacDetailBean" property="year" />&nbsp;</td>
										<td align="left" class="tblrows" valign="top"><bean:write name="tacDetailBean" property="additionalInfo" />&nbsp;</td>
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/deviceMgmt.do?method=initUpdate&tacDetailId=<bean:write name="tacDetailBean" property="tacDetailId"/>" tabindex="8">
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
							<td class="btns-td" valign="middle" width="100%">
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/deviceMgmt.do?method=initCreate'"  class="light-btn" /> 
								<input type="button"   value="   Delete   " onclick="removeRecord()" class="light-btn">
								<input type="button"   value="   Import   " onclick="upload()" class="light-btn">	
								<input type="button"   value="   Export "  onclick="exportCSV()" class="light-btn" id="btnExport">		
								<input type="button"   value="   Export All " onclick="exportAllCSV()" class="light-btn" id="btnExportAll">														
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

