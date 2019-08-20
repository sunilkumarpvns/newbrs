<%@page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.SearchCertificateForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>

<script> 
</script>

<%
	SearchCertificateForm searchCertificateForm = (SearchCertificateForm)request.getAttribute("searchCertificateForm");

	String strName 	 = searchCertificateForm.getName();
	String strTypeId = searchCertificateForm.getTypeId();
	long pageNo 	 = searchCertificateForm.getPageNumber();
	long totalPages  = searchCertificateForm.getTotalPages();
	long totalRecord = searchCertificateForm.getTotalRecords();
	
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	int count=1;
	
	String strPageNumber	= String.valueOf(pageNo);     
	String strTotalPages 	= String.valueOf(totalPages);
	String strTotalRecords 	= String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0" width="828">
      <tr>
      <td width="7" >&nbsp;</td>
      <td width="821" colspan="2" >
         
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr> 
          		<td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td> 
          		<td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">
          			<bean:message bundle="servermgrResources" key="servermgr.certificate"/>
          		</td> 
          		<td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td> 
          		<td width="633"></td> 
        	</tr> 
        	<tr> 
          		<td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td> 
        	</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" width="7">&nbsp;</td>
		</tr>
</table>

<% /* --- End of Page Header --- 
      --- Module specific code starts from below.*/ %>
<table cellpadding="0" cellspacing="0" border="0" width="828" >
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0"
				width="100%">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
               
              <html:form action="/searchCertificate" >

				<tr>
					<td class="table-header" colspan="5">
					<bean:message bundle="servermgrResources" key="servermgr.certificate.searchcertificate" /></td>
				</tr>
				<tr>
					<td class="small-gap" colspan="3">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top" width="18%">
					<bean:message bundle="servermgrResources" key="servermgr.certificate.name" /></td>
					<td align="left" class="labeltext" valign="top" width="66%">
					<html:text styleId="name" property="name" size="30" maxlength="60" />
					</td>							
				</tr>
				
				<tr>
							<td align="left" class="captiontext" valign="top" width="10%">
								Type
							</td>
							<td align="left" class="labeltext" valign="top" width="32%">
								Box																						 
							</td>							
				</tr>
				
				<tr>
					<td class="btns-td" valign="middle">&nbsp;</td>
					<td class="btns-td" valign="middle" colspan="2">
						<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Search " class="light-btn" onclick="validateForm()"> 
						<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchCertificate.do?/>'"
					 	value="Cancel" class="light-btn">
					</td>
				</tr>
             
           </html:form>
           
  





<%-- 				<%
				searchCertificateForm.setAction("list");
				if("list".equals(searchCertificateForm.getAction())&& searchCertificateForm.getAction() != null) {%>
 --%>				
				<html:form action="/searchCertificate">
	        	<tr class="vspace">	
					   <td align="left" class="labeltext" colspan="5" valign="top">
						<table cellSpacing="0" cellPadding="0" width="100%" border="0">
							<tr>
									<td class="table-header" width="100%" colspan="2" >
										Certificate List
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
							
							<tr class="vspace">
							<td class="btns-td" valign="middle">
							<input type="button" name="Create" value="   Create   "class="light-btn"onclick="javascript:location.href='<%=basePath%>/initCreateServerCertificate.do?typeId=TYP0001/>'">							
							<input type="button" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn"></td>
                            <td class="btns-td" align="right" >
						  	
						  	<% if(totalPages >= 1) { %>
							  	<% if(pageNo == 1){ %>
									<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							  	<% } %>
								<% if(pageNo>1 && pageNo!=totalPages+1) {%>
									<%  if(pageNo-1 == 1){ %>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else if(pageNo == totalPages){ %>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else { %>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } %>
								<% } %>
							<% if(pageNo == totalPages+1) { %>
								<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
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
								<td align="left" class="tblheaderfirstcol" valign="top" width="4%" >Sr. No.</td>
								<td align="left" class="tblheader" valign="top" width="25%" >Certificate Name  <img src="<%=basePath%>/images/dnarrow.jpg"></td>								
								<td align="left" class="tblheader" valign="top" width="30%">Certificate Type  <img src="<%=basePath%>/images/dnarrow.jpg"></td>								
								<td align="center" class="tblheaderlastcol" valign="top" width="4%"><bean:message key="general.edit" /></td>
							</tr>
					  		<%if(1==1){%>
							<tr id="dataRow" name="dataRow" >
								<td align="center" class="tblfirstcol">
								<input type="checkbox" name="select" value="" />
								</td>
						   		<td align="left" class="tblrows"><%=(pageNo-1)*pageSize+count%></td>
						   		<td align="left" class="tblrows"><a href="">Test Cerfificate</a> </td>
						   		<td align="left" class="tblrows">Server Certificate</td>							   		
						   		<td align="center" class="tblrows">
									<a href="">
										<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
									</a>
								</td>
						  	</tr>
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
							<input type="button" name="Create" value="   Create   "class="light-btn"onclick="javascript:location.href='<%=basePath%>/initCreateServerCertificate.do?typeId=TYP0001/>'">							
							<input type="button" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn"></td>
                            <td class="btns-td" align="right" >
						  	
						  	<% if(totalPages >= 1) { %>
							  	<% if(pageNo == 1){ %>
									<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							  	<% } %>
								<% if(pageNo>1 && pageNo!=totalPages+1) {%>
									<%  if(pageNo-1 == 1){ %>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else if(pageNo == totalPages){ %>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } else { %>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
										<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
									<% } %>
								<% } %>
							<% if(pageNo == totalPages+1) { %>
								<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="searchCertificate.do?action=list&name=<%=strName%>&typeId=<%=strTypeId%>&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
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
				<%-- <%} %> --%>
				
			</table>
	    </td>
	 	</tr>
</table>	
<table>
  	<tr>     	 
    	<td colspan="2" valign="top" align="right"> 
      	<table width="99%" border="0" cellspacing="0" cellpadding="0"> 
        	<tr> 
          		<td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td> 
          		<td width="140" height="23" align="right" rowspan="2" valign="top"><img alt="" src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td> 
        	</tr> 
	        <tr>	 
          		<td width="82%" valign="top" align="right" class="small-text-grey">Copyright &copy; <a href="http://www.elitecore.com" target="_blank">Elitecore Technologies Pvt. Ltd.</a> </td> 
        	</tr> 
      	</table> 
    	</td> 
  	</tr>  
</table> 
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
