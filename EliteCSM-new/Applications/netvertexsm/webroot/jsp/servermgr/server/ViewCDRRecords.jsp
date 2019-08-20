<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ViewCDRRecordsForm" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>





<%
	String localBasePath = request.getContextPath();
   	Calendar c = Calendar.getInstance();
   	ViewCDRRecordsForm viewCDRRecordsForm = (ViewCDRRecordsForm)request.getAttribute("viewCDRRecordsForm");
%>

<script>
	var dFormat;
	dFormat = '<%=ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT)%>';	
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 
	}
	function validateUpdate()
	{
		document.forms[0].action.value='search';
		document.forms[0].submit();
	}
	function viewCDRDetails(){
		document.forms[0].action.value='View Details';
		window.open('<%=localBasePath%>/viewCDRRecords.do?action='+document.forms[0].action.value+'&netServerId='+document.forms[0].netServerId.value+'&prmSessionId='+document.forms[0].sessionId.value+'&prmCallStartDate='+document.forms[0].strCallStartDate.value+'&prmCallEndDate='+document.forms[0].strCallEndDate.value,'ViewCDRRecord','top=100, left=200, height=300, width=600, scrollbars=yes, status');
	}
</script>
				   
<html:form action="/viewCDRRecords">
<html:hidden name="viewCDRRecordsForm" styleId="netServerId" property="netServerId"/>
<html:hidden name="viewCDRRecordsForm" styleId="action" property="action" />
<table cellpadding="0" cellspacing="0" border="0" width="100%">	
	<tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
		  <tr>
		  	<td>&nbsp;</td>
		  </tr>
		  <tr> 
			<td class="tblheader-bold" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.viewcdrdetails"/></td>
		  </tr>
		  <tr > 
			<td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources" key="servermgr.sessionid"/></td>
			<td align="left" class="labeltext" valign="top" colspan="2" > 
				<html:text styleId="sessionId" property="sessionId" size="20" maxlength="50"/><%--<font color="#FF0000"> *</font> --%>
			</td>
	      </tr>
		  <tr > 
			<td align="left" class="labeltext" valign="top" width="18%%" ><bean:message bundle="servermgrResources" key="servermgr.callstart"/></td>
			<td align="left" class="labeltext" valign="top" colspan="2" > 
				<html:text styleId="strCallStartDate" property="strCallStartDate" size="10" maxlength="15"/><%-- <font color="#FF0000"> *</font>  --%>
	              <a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].strCallStartDate)" >
						<img  src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
			 	  </a>
  			</td>
	      </tr> 
	      <tr > 
			<td align="left" class="labeltext" valign="top" width="18%%" ><bean:message bundle="servermgrResources" key="servermgr.callend"/></td>
			<td align="left" class="labeltext" valign="top" colspan="2" > 
				<html:text styleId="strCallEndDate" property="strCallEndDate" size="10" maxlength="15"/><%-- <font color="#FF0000"> *</font> --%>
    			  <a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].strCallEndDate)" >
						<img  src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
				  </a>
			</td>
	      </tr>					
        </table>
	  </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
   	<tr> 
      <td class="btns-td" valign="middle"  >
       	 <input type="button" name="c_btnUpdate"  onclick="validateUpdate()"  id="c_btnUPdate"  value="  Search  "  class="light-btn" tabindex="4">                   
       </td> 
	</tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
    
    
<%
	if(viewCDRRecordsForm.getAction() != null) {
%>    
    
	<tr> 
      <td width="100%" align="right">
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
        <logic:equal name="viewCDRRecordsForm" property="errorCode" value="0" >   
<%
	List lstCDRRecordsList = ((ViewCDRRecordsForm)request.getAttribute("viewCDRRecordsForm")).getCDRRecordsList();
	List lstCDRColumns = (List)lstCDRRecordsList.get(0);
	List lstCDRColumnContents = new ArrayList();
%>
        
           <tr>
			  <% 
			  for(int i=0;i<lstCDRColumns.size() && i<5;i++){ %>
			  	<td align="center" valign="top" width="5%" 	class="tblheader">																	
			  		<%=lstCDRColumns.get(i).toString()%>
				</td>
			  <% } %>  
<%--			   <td align="center" class="tblheader" valign="top" width="5%"><bean:message bundle="servermgrResources" key="servermgr.viewdetail"/>  --%>
		   </tr>
		   <%

			  	for(int j=1;j<lstCDRRecordsList.size()  && j<100;j++) {
			  		List lstCDR = (List)lstCDRRecordsList.get(j);
		   %>
		   <tr>
		       <% 
		       for(int k=0;k<lstCDRColumns.size() && k<5;k++){ %>
		       <%if(k == 0){ %>
		       			<td align="center" valign="top" width="5%" 	class="tblfirstcol">																	
					  		<%=(String)lstCDR.get(k)%>
						</td>
		       
		       <% } else { %>
						<td align="center" valign="top" width="5%" 	class="tblrows">																	
					  		<%=(String)lstCDR.get(k)%>
						</td>
						
			   <% 
			   		}
			   	  } 
			   %>
		   </tr>
		  <%
				}
		  %>
		</logic:equal> 
			<tr>
		   		<td>&nbsp;</td>
		   </tr>
		   <tr> 
	    	  <td valign="middle"  >
    		   	 <input type="button" name="c_btnUpdate"  onclick="viewCDRDetails()"  id="c_btnUPdate"  value="View Details"  class="light-btn" tabindex="4">                   
	       	  </td> 
		   </tr>
		<logic:notEqual name="viewCDRRecordsForm" property="errorCode" value="0" >            
           <tr>
              <td class="blue-text-bold">
                            <bean:message bundle="servermgrResources" key="servermgr.connectionfailure"/><br>
                            <bean:message bundle="servermgrResources" key="servermgr.admininterfaceip"/> : <bean:write name="netServerInstanceData" property="adminHost"/><br>
                            <bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/> : <bean:write name="netServerInstanceData" property="adminPort"/>                            
                            
                    &nbsp;                          
              </td>
            </tr>
            <tr>
              <td>&nbsp;</td>
            </tr>
        </logic:notEqual>  
        </table>
        </td>
	    </tr>
<% } %>	    
</table>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
