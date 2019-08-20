<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.alert.forms.SearchAlertListenerForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List,com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertFileListenerData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTrapListenerData"%>    
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>

<%
	SearchAlertListenerForm searchAlertListenerForm = (SearchAlertListenerForm)request.getAttribute("searchAlertListenerForm");
	String strName = searchAlertListenerForm.getName();
	String strTypeId = searchAlertListenerForm.getTypeId();
	//Paging related details..
	long pageNo = searchAlertListenerForm.getPageNumber();
    long totalPages = searchAlertListenerForm.getTotalPages();
    long totalRecord = searchAlertListenerForm.getTotalRecords();
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	int count=1;
   
    String strPageNumber = String.valueOf(pageNo);     
    String strTotalPages = String.valueOf(totalPages);
    String strTotalRecords = String.valueOf(totalRecord);
	
	List<AlertListenerData> lstAlertListener = (List<AlertListenerData>)request.getAttribute("lstAlertListener");	
%>

<script>
function validateForm(){
	document.forms[0].pageNumber.value = 1;
	document.forms[0].submit();
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
        alert('At least select one Alert Listener for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchAlertListenerjsp.delete.query"/>';        
        var agree = confirm(msg);
        if(agree){
       	    document.miscAlertListenerForm.action.value = 'delete';
        	document.miscAlertListenerForm.submit();
        }
    }
}
$(document).ready(function(){
	$("#name").focus();
	checkAll(document.getElementsByName("toggleAll")[0]);
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.alert.alertlistener"/>');
});

</script>

   
			
<table cellpadding="0" cellspacing="0" border="0" width="100%">

        <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		

<% /* --- End of Page Header --- 
      --- Module specific code starts from below.*/ %>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0"
				width="100%">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
               
              <html:form action="/searchAlertListener" >
    		 <html:hidden name="searchAlertListenerForm" styleId="action" property="action" />
		 	<html:hidden name="searchAlertListenerForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>
		 	<html:hidden name="searchAlertListenerForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>"/>
		 	<html:hidden name="searchAlertListenerForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>"/>

				<tr>
					<td class="table-header" colspan="5">
					<bean:message bundle="servermgrResources" key="servermgr.alert.searchAlertlistener" /></td>
				</tr>
				<tr>
					<td class="small-gap" colspan="3">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top" width="18%">
					<bean:message bundle="servermgrResources" key="servermgr.alert.listenerName" /></td>
					<td align="left" class="labeltext" valign="top" width="66%">
					<html:text styleId="name" property="name" size="30" maxlength="60" />
					</td>							
				</tr>
				
				<tr>
							<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.listenerType"/></td>
							<td align="left" class="labeltext" valign="top" width="32%">														
								<html:select property="typeId" >
								<html:option value="0">Select</html:option>
								<html:optionsCollection name="searchAlertListenerForm" property="typeList" label="typeName" value="typeId"/>
								</html:select>
							</td>							
				</tr>
				
				<tr>
					<td class="btns-td" valign="middle">&nbsp;</td>
					<td class="btns-td" valign="middle" colspan="2">
						<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Search " class="light-btn" onclick="validateForm()"> 
						<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'"
					 	value="Cancel" class="light-btn">
					</td>
				</tr>
             
           </html:form>
           
  





				<%if("list".equals(searchAlertListenerForm.getAction())&& searchAlertListenerForm.getAction() != null) {%>
				
				<html:form action="/miscAlertListener">
				<html:hidden name="miscAlertListenerForm" styleId="pageNumber" 	property="pageNumber" value="<%=strPageNumber%>"/>
				<html:hidden name="miscchAlertListenerForm" styleId="totalPages" 	property="totalPages" value="<%=strTotalPages%>"/>
				<html:hidden name="miscAlertListenerForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>"/>
				
				<html:hidden name="miscAlertListenerForm" styleId="name" property="name" value="<%=strName%>"/>
				<html:hidden name="miscAlertListenerForm" styleId="typeId" property="typeId" value="<%=strTypeId%>"/>
				<html:hidden name="miscAlertListenerForm" styleId="action" property="action"/>
				
	        	<tr class="vspace">	
					   <td align="left" class="labeltext" colspan="5" valign="top">
						<table cellSpacing="0" cellPadding="0" width="100%" border="0">
							<tr>
									<td class="table-header" style="border: none;" width="50%" >
									<bean:message bundle="servermgrResources" key="servermgr.alert.listenerTypeList" />
									</td>
									
									<td align="right" class="blue-text" valign="middle" width="50%">
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
							<tr>
								<td  class="topHLine" style="font-size: 1px" colspan="2" >&nbsp;</td>
							</tr>
							<tr class="vspace">
							<td class="btns-td" valign="middle">
							<input type="button" name="Create" value="   Create   "class="light-btn"onclick="javascript:location.href='<%=basePath%>/initCreateAlertListener.do?typeId=TYP0001/>'">							
							<input type="button" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn"></td>
                            <td class="btns-td" align="right" >
						  	
						  	<% if(totalPages >= 1) { %>
							  	<% if(pageNo == 1){ %>
									<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							  	<% } %>
								<% if(pageNo>1 && pageNo!=totalPages+1) {%>
									<%  if(pageNo-1 == 1){ %>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else if(pageNo == totalPages){ %>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else { %>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } %>
								<% } %>
							<% if(pageNo == totalPages+1) { %>
								<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
							<% } %>
					  <% } %>
					  
				</td>
				</tr>
		 
				
				<tr class="vspace">
				<td class="btns-td" valign="middle" colspan="2" > 
	   				<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable"  > 
							<tr>
								<td align="center" class="tblheader" valign="top" width="1%" >
										<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll(this)" />
								</td>
								<td align="left" class="tblheaderfirstcol" valign="top" width="6%" >Sr. No.</td>
								<td align="left" class="tblheader" valign="top" width="24%" >Listener Name </td>								
								<td align="left" class="tblheader" valign="top" width="30%">Listener Type  </td>
								<td align="left" class="tblheader" valign="top" width="30%"><bean:message key="general.lastmodifieddate"/></td>								
								<td align="center" class="tblheaderlastcol" valign="top" width="5%"><bean:message key="general.edit" /></td>
							</tr>
					  <%if(lstAlertListener != null && lstAlertListener.size() >0){
						  int i=0;
					  %>
						<logic:iterate id="alertListenerBean" name="lstAlertListener" type="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData">
							<tr id="dataRow" name="dataRow" >
								<td align="center" class="tblfirstcol">
								<input type="checkbox" name="select" value="<bean:write name="alertListenerBean" property="listenerId"/>"  onclick="onOffHighlightedRow(<%=i++%>,this)" />
								</td>
						   		<td align="left" class="tblrows"><%=(pageNo-1)*pageSize+count%></td>
						   		<td align="left" class="tblrows"><a href="<%=basePath%>/viewAlertListener.do?listenerId=<bean:write name="alertListenerBean" property="listenerId"/>"><bean:write name="alertListenerBean" property="name"/></a> </td>
						   		<td align="left" class="tblrows"><bean:write name="alertListenerBean" property="alertListenerTypeData.typeName"/></td>
						   		<td align="left" class="tblrows">
						   			 <%=EliteUtility.dateToString(alertListenerBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
						   		</td>							   		
						   		<td align="center" class="tblrows">
									<a href="<%=basePath%>/updateAlertListener.do?listenerId=<bean:write name="alertListenerBean" property="listenerId"/>"> 
										<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></a>
								</td>
						  	</tr>
 	  				<% count=count+1; %>
	 	  		       </logic:iterate>
	 	  		
	 	  		       <%}else{ %>
									<tr>
	                  					<td align="center" class="tblfirstcol" colspan="8">No Records Found.</td>
	                				</tr>
				
                       <%} %>				
						</table>
						</td>
						</tr>	
							
						<tr class="vspace">
							<td class="btns-td" valign="middle">
							<input type="button" name="Create" value="   Create   "class="light-btn"onclick="javascript:location.href='<%=basePath%>/initCreateAlertListener.do?typeId=TYP0001/>'">							
							<input type="button" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn"></td>
                            <td class="btns-td" align="right" >
						  	
						  	<% if(totalPages >= 1) { %>
							  	<% if(pageNo == 1){ %>
									<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							  	<% } %>
								<% if(pageNo>1 && pageNo!=totalPages+1) {%>
									<%  if(pageNo-1 == 1){ %>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else if(pageNo == totalPages){ %>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else { %>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } %>
								<% } %>
							<% if(pageNo == totalPages+1) { %>
								<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="searchAlertListener.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
							<% } %>
					  <% } %>
					  
				</td>
				</tr>
				
				<tr height="2">        
				<td></td>                   
			    </tr>
	       			
					  	</table>
					   </td>	
				</tr>
				</html:form>	
				<%} %>
				
			</table>
	    </td>
	 	

  	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
