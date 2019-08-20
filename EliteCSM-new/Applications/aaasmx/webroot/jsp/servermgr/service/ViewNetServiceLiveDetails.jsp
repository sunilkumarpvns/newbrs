<%@ page import="com.elitecore.elitesm.web.servermgr.service.forms.ViewNetServiceLiveDetailsForm" %>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServerDetails" %>
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServiceDetails" %>




<% 
	String localBasePath = request.getContextPath();
%>

<script>
    function validateSynchronize(){
        document.forms[0].submit();
    }
</script>

<%
    EliteNetServiceDetails liveDetailsForm = (EliteNetServiceDetails)request.getAttribute("eliteLiveServiceDetails");
    List lstServiceDetails = liveDetailsForm.getBasicDetails();
%>

<html:form action="/viewNetServiceLiveDetails">
<html:hidden name="viewNetServiceLiveDetailsForm" styleId="netServiceId" property="netServiceId"/>
<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
  <tr> 
    <td valign="top" align="right"> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0" >
        <tr>
          <td>&nbsp;</td>
        </tr>
        <tr> 
		  <td class="tblheader-bold" colspan="2"><bean:message bundle="servermgrResources" key="servermgr.liveservicedetails"/></td>
		</tr>
        <logic:equal name="viewNetServiceLiveDetailsForm" property="errorCode" value="0" >                                    
		<tr>
		  <td align="left" class="tblfirstcol" valign="top" width="30%"><bean:message bundle="servermgrResources" key="servermgr.name"/></td>
		  <td align="left" class="tblcol" valign="top" width="70%" ><bean:write name="eliteLiveServiceDetails" property="name"/>&nbsp;</td>
		</tr>								
		<tr>
 		  <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.instancename"/></td>
		  <td align="left" class="tblcol" valign="top" ><bean:write name="eliteLiveServiceDetails" property="instanceName"/>&nbsp;</td>
		</tr>								
		<tr>
		  <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.instanceid"/></td>
		  <td align="left" class="tblcol" valign="top" ><bean:write name="eliteLiveServiceDetails" property="instanceId"/>&nbsp;</td>
		</tr>			
		<tr>
		  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.status"/></td>
 		  <td align="left" class="tblcol" valign="top" ><bean:write name="eliteLiveServiceDetails" property="status"/>&nbsp;</td>
		</tr>	
        </logic:equal>
	  </table>
	</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
  </tr>
    <logic:equal name="viewNetServiceLiveDetailsForm" property="errorCode" value="0" >                            
  <tr>
    <td valign="top" align="right">
      <table width="100%" cols="4" border="0" cellpadding="0" cellspacing="0" >
      <%--
        <tr> 
          <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.configureservices"/></td>
  		</tr> --%>
        <tr>
          <td align="left" class="tblheader-bold" valign="top" width="30%" ><bean:message bundle="servermgrResources" key="servermgr.servicebasicdetails"/></td>
        </tr>
<%
    int iIndex = 0;
    if(lstServiceDetails != null && lstServiceDetails.size() > 0){
    String strBasicDetails = (String)lstServiceDetails.get(iIndex);
    System.out.println("Value of the BasicDetails is :"+strBasicDetails);
    iIndex++;
%>
                        <logic:iterate id="serviceData" name="eliteServiceDetails" property="basicDetails" type="java.lang.String">
                        <tr>
                          <td align="left" class="tblfirstcol"><%=(iIndex) %></td>
                          <td align="left" class="tblrows"><%=strBasicDetails%></td>
                        </tr>
                        </logic:iterate>
<%  }else{  %>
                        <tr>
							  <td align="center" class="tblfirstcol" colspan="4"><bean:message bundle="servermgrResources" key="servermgr.nobasicdetails"/></td>
                        </tr>
<%  }  %>
                      </table>
                    </td>
            </tr>

                <tr>
                    <td>&nbsp;</td>
                </tr>
            </logic:equal>                
			<logic:notEqual name="viewNetServiceLiveDetailsForm" property="errorCode" value="0" >            
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

   	<tr > 
        <td valign="middle"  >
		    <logic:equal name="viewNetServiceLiveDetailsForm" property="errorCode" value="0" >                                        
	    	    <logic:equal name="eliteLiveServiceDetails" property="status" value="Y" >
	        	    <input type="button" name="c_btnStopService"  onclick="javascript:location.href='<%=localBasePath%>/signalServiceShutDown.do?netServerId=<bean:write name="netServerInstanceData" property="netServerId"/>&netServiceName=<bean:write name="eliteLiveServiceDetails" property="name"/>&instanceId=<bean:write name="eliteLiveServiceDetails" property="instanceId"/>'"  id="c_btnStopService"  value="Stop"  class="light-btn" />
	          	</logic:equal>
	          	<logic:notEqual name="eliteLiveServiceDetails" property="status" value="Y" >
	            	<input type="button" name="c_btnStartService"  onclick="javascript:location.href='<%=localBasePath%>/signalServiceStartUp.do?netServerId=<bean:write name="netServerInstanceData" property="netServerId"/>&netServiceName=<bean:write name="eliteLiveServiceDetails" property="name"/>&instanceId=<bean:write name="eliteLiveServiceDetails" property="instanceId"/>'"  id="c_btnStartService"  value="Start"  class="light-btn" />                          
	            </logic:notEqual>
	<%--           	<input type="button" name="c_btnShutdown"  onclick="javascript:location.href='<%=localBasePath%>/signalServerShutDown.do?netServrId=<bean:write name="viewNetServiceLiveDetailsForm" property="netServerId"/>'"  id="c_btnShutdown"  value="Stop Server"  class="light-btn" />                   
	            <input type="button" name="c_btnReload"  onclick="javascript:location.href='<%=localBasePath%>/signalServerReloadConfiguration.do?netServerId=<bean:write name="viewNetServiceLiveDetailsForm" property="netServerId"/>'"  id="c_btnReload"  value="Reload Configuration"  class="light-btn" />                               
	  --%>          																															
			    <input type="button" name="c_btnReloadConfig"  onclick="javascript:location.href='<%=localBasePath%>/signalServiceReloadConfiguration.do?netserviceid=<bean:write name="netServiceInstanceData" property="netServiceId"/>'"  id="c_btnReloadConfig"  value="Reload Configuration"  class="light-btn" />                               
	            <input type="button" name="c_btnReloadCache"  onclick="javascript:location.href='<%=localBasePath%>/signalServiceReloadCache.do?netserviceid=<bean:write name="netServiceInstanceData" property="netServiceId"/>'"  id="c_btnReloadCache"  value="Reload Cache"  class="light-btn" />                                           
	      </logic:equal>    
                <input type="reset" name="c_btnDeletePolicy" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=localBasePath%>/viewNetServiceInstance.do?netserviceid=<bean:write name="viewNetServiceLiveDetailsForm" property="netServiceId"/>'" />
        </td>
	</tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>