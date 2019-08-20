<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.netvertexsm.web.datasource.esiradius.form.SearchEsiRadiusForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.EsiRadiusData"%>
<%@ page import="com.elitecore.netvertexsm.hibernate.datasource.esiradius.HEsiRadiusDataManager"%>

<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.IEsiRadiusData"%>
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
function remove(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one Gateway for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchstaffjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.miscStaffForm.action.value = 'delete';
        	document.miscStaffForm.submit();
        }
    }
}
function  checkAll(){
	 	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('select');
		 	for (i = 0; i < selectVars.length;i++)
				selectVars[i].checked = true ;
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('select');	    
			for (i = 0; i < selectVars.length; i++)
				selectVars[i].checked = false ;
		}
}

</script>

<%
     SearchEsiRadiusForm searchEsiRadiusForm = (SearchEsiRadiusForm)request.getAttribute("searchEsiRadiusForm");
     List lstEsiRadius = searchEsiRadiusForm.getListSearchEsiRadius();    
               
     long pageNo = searchEsiRadiusForm.getPageNumber();
     long totalPages = searchEsiRadiusForm.getTotalPages();
     long totalRecord = searchEsiRadiusForm.getTotalRecords();
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
%>

<style> 
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style> 

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">				
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="datasourceResources" key="esiradius.search"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" align="right" border="0" >
			   
			   <html:form action="/searchEsiRadius" >
			   
			   	<html:hidden name="searchEsiRadiusForm" styleId="action" property="action" />
				<html:hidden name="searchEsiRadiusForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="searchEsiRadiusForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="searchEsiRadiusForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			      
				<tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="datasourceResources" key="esiradius.name"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="name" maxlength="60" size="30" styleId="name"/>
					</td> 
				</tr>				  
				<tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td class="btns-td" valign="middle" >             
            			<input type="button" value="   Create   " name="c_btnCreate" class="light-btn"
							onclick="javascript:location.href='<%=basePath%>/initCreateEsiRadius.do?/>'"/>
		        		<input type="submit" name="Search" width="5%" Onclick="validateSearch()" value="   Search   " class="light-btn" /> 
						<input type="reset" name="Cancel" value="   Cancel   " class="light-btn"/>
	        		</td> 
   		  		</tr>	
				  
				</html:form> 
	<%
		if(searchEsiRadiusForm.getAction()!=null && searchEsiRadiusForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/miscDatabaseDS">
				
					<html:hidden name="miscDatabaseDSForm" styleId="action" property="action" />
					<!--<html:hidden name="miscDatabaseDSForm" property="pageNumber" value="<//%=strPageNumber%>"/>  -->
					<html:hidden name="miscDatabaseDSForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="miscDatabaseDSForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td width="10">&nbsp;</td>
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="24%" colspan="2">
								<bean:message bundle="datasourceResources" key="esiradius.searchlist" />
							</td>
							<td align="left" class="blue-text" valign="middle" width="62%" colspan="3">&nbsp;</td>
							<td align="right" class="blue-text" valign="middle" width="14%" colspan="4">
									<% if(pageNo == totalPages+1) { %> 
										[<%=((pageNo-1)*10)+1%>-<%=totalRecord%>] of <%= totalRecord %> 
									<% } else if(pageNo == 1) { %> 
										[<%=(pageNo-1)*10+1%>-<%=(pageNo-1)*10+10%>] of <%= totalRecord %> 
									<% } else { %>
									 	[<%=((pageNo-1)*10)+1%>-<%=((pageNo-1)*10)+10%>] of <%= totalRecord %> 
									<% } %>
							</td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" colspan="6" align="right">
								&nbsp;
							</td>
							<td class="btns-td" align="right">

							</td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" colspan="5">
								
								<input type="button" name="Show" Onclick="active()"	value="   Show   " class="light-btn">
								<input type="button" name="Hide" Onclick="inactive()" value="   Hide   " class="light-btn">
								<input type="button" name="Delete" OnClick="remove()" value="   Delete   " class="light-btn">
							</td>

						</tr>
						<tr height="2">
							<td></td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" colspan="9">
							<table width="100%" border="0" cellpadding="0" cellspacing="0"
								id="listTable">
								<tr>
									<td align="center" class="tblheader" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" />
									</td>
									<td align="right" class="tblheader" valign="top" width="1%">
										<bean:message bundle="datasourceResources" key="esiradius.srno" />
									</td>
									<td align="left" class="tblheader" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="esiradius.name" />
									</td>
									<td align="left" class="tblheader" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="esiradius.description" />
									</td>								
									<td align="left" class="tblheader" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="esiradius.address" />
									</td>
									<td align="left" class="tblheader" valign="top" width="30%">
										Edit</td>
								</tr>
<%	
		if(lstEsiRadius!=null && lstEsiRadius.size()>0) { 
%>
								<logic:iterate id="esiBean" name="searchEsiRadiusForm"
									property="listSearchEsiRadius"
									type="com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.IEsiRadiusData">

									<tr>
										<td align="center" class="tblfirstcol">
											<input type="checkbox" name="select" value="" />
										</td>
										<td align="left" class="tblrows">
											<%=((pageNo-1)*10)+count%>
										</td>										
										<td align="left" class="tblrows">
											<bean:write name="esiBean" property="name" />
										</td>
										<td align="left" class="tblrows">
											<bean:write name="esiBean" property="description" />&nbsp;
										</td>										
										<td align="left" class="tblrows">
											<bean:write name="esiBean" property="address" />&nbsp;
										</td>
										<td align="center" class="tblrows">
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
						<tr height="2">
							<td></td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" colspan="5">								
								<input type="button" name="Show" Onclick="active()" value="   Show   " class="light-btn">
								<input type="button" name="Hide" Onclick="inactive()" value="   Hide   " class="light-btn"> 
								<input type="button" name="Delete" Onclick="remove()" value="   Delete   " class="light-btn">
							</td>
							<td class="btns-td" valign="middle" colspan="2">&nbsp;</td>
							<td class="btns-td" align="right">

							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
				</table>						
				</tr>
						
				</html:form>	
		<% 	} %>								
								  
			   </table>  
			</td> 
		  </tr>	 
		  <tr> 
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
              		     		    		 
		</table> 
	  </td> 
	</tr>
	<tr id="profileres">
	</tr>
 	
  	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table> 

