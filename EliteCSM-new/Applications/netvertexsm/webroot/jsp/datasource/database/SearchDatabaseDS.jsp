<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.netvertexsm.web.datasource.database.form.SearchDatabaseDSForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.DatabaseDSConstant"%>
<%@ page import="com.elitecore.netvertexsm.hibernate.datasource.database.HDatabaseDSDataManager"%>

<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData"%>
<%@ page import="java.sql.Timestamp"%>

<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>

<%    
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
%>
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
function show(){

	document.miscDatabaseDSForm.action.value = 'show';
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
				alert("Please select atleast one Database Datasource to Show.")
			}else{
					document.miscDatabaseDSForm.submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}


}
function hide(){

document.miscDatabaseDSForm.action.value = 'hide';
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
				alert("Please select atleast one Database Datasource to Hide.")
			}else{
			    	document.miscDatabaseDSForm.submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
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
        alert('At least select one Database Datasource for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchdatabasedsjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.miscDatabaseDSForm.action.value = 'delete';
        	document.miscDatabaseDSForm.submit();
        }
    }
}
 
$(document).ready(function(){
	setTitle('<bean:message bundle="datasourceResources" key="database.datasource"/>');
	$("#name").focus();
	checkAll(document.getElementsByName("toggleAll")[0]);
});
</script>
<%
     SearchDatabaseDSForm searchDatabaseDSForm = (SearchDatabaseDSForm)request.getAttribute("searchDatabaseDSForm");
     List lstDatabaseDS = searchDatabaseDSForm.getLstDatabaseDS();
     String strName = searchDatabaseDSForm.getName();
     
     long pageNo = searchDatabaseDSForm.getPageNumber();
     long totalPages = searchDatabaseDSForm.getTotalPages();
     long totalRecord = searchDatabaseDSForm.getTotalRecords();
	 int count=1;
     
     String strPageNumber = String.valueOf(pageNo);     
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     
     
     
     
%>
<% /* --- Start of Page Header --- only edit module name*/ %>
  <table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">				
	 	  <tr> 
			<td class="table-header" colspan="5">
				SEARCH DATABASE DATASOURCE</td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" >
				<html:form action="/searchDatabaseDS">
				<input type="hidden" name="c_strActionMode" id="c_strActionMode" value="102231105" />
				<html:hidden name="searchDatabaseDSForm" styleId="action" 	property="action" />
				<html:hidden name="searchDatabaseDSForm" styleId="pageNumber" property="pageNumber"/>
				<html:hidden name="searchDatabaseDSForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>"/>
				<html:hidden name="searchDatabaseDSForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>"/>
			
		   		<tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						Datasource Name</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text styleId="name" property="name" size="30" maxlength="30"/>
					</td> 
				  </tr>
				 
				  <tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td class="btns-td" valign="middle" >             
            			
		        		<input type="submit" name="DatabaseDSName" width="5%" Onclick="validateSearch()" value="   Search   " tabindex="2" class="light-btn" /> 
	        		</td> 
   		  		  </tr>
			</html:form>	
						
		<%
		    //System.out.println("Every time check the value of pageno and strname : "+pageNo+" str is :"+strName);
			if(searchDatabaseDSForm.getAction()!=null && searchDatabaseDSForm.getAction().equalsIgnoreCase(DatabaseDSConstant.LISTACTION)){%>	
			<html:form action="/miscDatabaseDS">
				<html:hidden name="miscDatabaseDSForm" styleId="action" 		property="action" />
				<html:hidden name="miscDatabaseDSForm" styleId="name" 		property="name" value="<%=strName%>"/>	
				<html:hidden name="miscDatabaseDSForm" styleId="pageNumber" 	property="pageNumber" value="<%=strPageNumber%>"/>
				<html:hidden name="miscDatabaseDSForm" styleId="totalPages" 	property="totalPages" value="<%=strTotalPages%>"/>
				<html:hidden name="miscDatabaseDSForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>"/>
	       	
	<tr>
		<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="24%" colspan="2">DATABASE DATASOURCE LIST</td>  
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
						<input type="button" value="   Create   " name="c_btnCreate" class="light-btn"
							onclick="javascript:location.href='<%=basePath%>/initCreateDatabaseDS.do?/>'"/>
						<input type="button" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn">
					</td>
				
				<td class="btns-td" align="right" >
					  	<% if(totalPages >= 1) {%>
						  	<% if(pageNo == 1){%>
								<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  	<% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%>
								<%  if(pageNo-1 == 1){ %>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else if(pageNo == totalPages){ %>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else { %>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } %>
							<% } %>
						<% if(pageNo == totalPages+1) { %>
							<a  href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						<% } %>
				  <% } %>
				</td>
	    	</tr>	
			
			<tr class="vspace">
				<td class="btns-td" valign="middle" colspan="9">
	   				<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
						<tr>
							<td align="center" class="tblheader" valign="top" width="1%">
	  								<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll(this)"/>
							</td>
						    <td align="left" class="tblheader" valign="top" width="5%"><bean:message bundle="datasourceResources" key="database.datasource.serialnumber"/></td>
							<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="datasourceResources" key="database.datasource.name"/></td>
                            <td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="datasourceResources" key="database.datasource.connectionurl"/></td>
							<td align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="datasourceResources" key="database.datasource.username"/></td>
							<td align="left" class="tblheader" valign="top" width="20%"><bean:message key="general.lastmodifieddate"/></td>
						    <td align="left" class="tblheader" valign="top" width="5%"><bean:message bundle="datasourceResources" key="database.datasource.edit"/></td>
						</tr>
				<%	if(lstDatabaseDS!=null && lstDatabaseDS.size()>0){ int i=0;%>
					<logic:iterate id="databaseDSBean" name="searchDatabaseDSForm" property="lstDatabaseDS" type="com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData">
						<%	IDatabaseDSData sData = (IDatabaseDSData)lstDatabaseDS.get(iIndex);
							Timestamp dtLastUpdate = sData.getModifiedDate();
						%>
	                   		<tr id="dataRow" name="dataRow" >	
							<td align="center" class="tblfirstcol" width="5%"> 
								<input type="checkbox" name="select" value="<bean:write name="databaseDSBean" property="databaseId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  />
							</td>
		 					<td align="left" class="tblrows" width="5%"><%=((pageNo-1)*pageSize)+count%></td>
							<td align="left" class="tblrows" width="25%"><a href="<%=basePath%>/viewDatabaseDS.do?databaseId=<bean:write name="databaseDSBean" property="databaseId"/>"><bean:write name="databaseDSBean" property="name"/></a></td> 
				            <td align="left" class="tblrows" width="20%"><textarea rows="1" cols="45" style="border: none; overflow: hidden; margin: 0; padding: 0;" readonly="readonly"><bean:write name="databaseDSBean" property="connectionUrl"/></textarea> </td> 
				            <td align="left" class="tblrows" width="15%"><bean:write name="databaseDSBean" property="userName"/>&nbsp;&nbsp;</td>
				            <td align="left" class="tblrows" width="20%">
				            	<%=EliteUtility.dateToString(databaseDSBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;&nbsp;</td>
							<td align="center" class="tblrows"  width="5%">
								<a  href="updateDatabaseDS.do?databaseId=<bean:write name="databaseDSBean" property="databaseId"/>" ><img  src="<%=basePath%>/images/edit.jpg"  alt="Edit"  border="0" ></a>
							</td>							
	 	   				</tr> 
	 	  				<% count=count+1; %>
	 	  				<% iIndex += 1; %>
					</logic:iterate>
				<%	}else{
				%>
	                				<tr>
	                  					<td align="center" class="tblfirstcol" colspan="7">No Records Found.</td>
	                				</tr>
				<%	}%>
					</table>
				</td>
				</tr>
				 
				<tr class="vspace">
				<td class="btns-td" valign="middle" colspan="5">
					<input type="button" value="   Create   " name="c_btnCreate" class="light-btn"
							onclick="javascript:location.href='<%=basePath%>/initCreateDatabaseDS.do?/>'"/>
					<input type="button" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn">															
				</td>
				<td class="btns-td" align="right" >
					  	<% if(totalPages >= 1) { %>
						  	<% if(pageNo == 1){ %>
								<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  	<% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%>
								<%  if(pageNo-1 == 1){ %>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else if(pageNo == totalPages){ %>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else { %>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } %>
							<% } %>
						<% if(pageNo == totalPages+1) { %>
							<a  href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						<% } %>
				  <% } %>
				</td>						
						</tr>
						</table></td>
						</tr>										
						 
			</html:form>								
			<%}%>		
	       					  
			  </table>  
		</td> 
	</tr>	          
</table>
</td>
</tr>
<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>			
