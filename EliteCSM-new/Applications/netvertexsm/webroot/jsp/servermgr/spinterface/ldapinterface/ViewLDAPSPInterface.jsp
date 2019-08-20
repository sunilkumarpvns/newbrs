<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@ page import="com.elitecore.corenetvertex.spr.data.SPRFields" %>
<%

LDAPSPInterfaceData ldapSPInterfaceData = null;

	Set<LDAPSPInterfaceData> ldapDriverDataSet = driverInstanceData.getLdapspInterfaceDriverSet();
	if(ldapDriverDataSet!=null && !ldapDriverDataSet.isEmpty()){
		for(Iterator<LDAPSPInterfaceData> iterator= ldapDriverDataSet.iterator();iterator.hasNext();){
			ldapSPInterfaceData = iterator.next();
		}
	}
	if(ldapSPInterfaceData!=null){
		pageContext.setAttribute("ldapSPInterfaceData",ldapSPInterfaceData);
	}
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >

	<%if(ldapSPInterfaceData!=null){%>
    
    <tr> 
      <td valign="top" align="right"> 
      
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
           <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.ldapdsid" /></td>
            <td class="tblcol" width="70%" height="20%">
            <a href="<%=basePath%>/viewLDAPDS.do?ldapDsId=<bean:write name="ldapSPInterfaceData" property="ldapDs.ldapDsId"/>">
            <bean:write name="ldapSPInterfaceData" property="ldapDs.name"/>&nbsp;
            </a>
            </td>
          </tr>  
		   <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.expirydatapattern" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapSPInterfaceData" property="expiryDatePattern"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.passdecrypttype" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapSPInterfaceData" property="passwordDecryptType"/>&nbsp;</td>
          </tr> 
            
           
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.querymaxexectime" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapSPInterfaceData" property="queryMaxExecTime"/>&nbsp;</td>
          </tr>
          <tr>
			<td class="small-gap">&nbsp;</td>
		  </tr>
		  <tr>
			<td class="small-gap">&nbsp;</td>
		  </tr>
          <tr>
          	<td class="tblheader-bold" colspan="2"><bean:message bundle="driverResources" key="driver.ldapfield.mapping" /></td>
          </tr>
          <tr> 
			<td colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="70%" class="box">
					<tr>
					    <td class="tblheader-bold"  height="20%"><bean:message bundle="driverResources" key="driver.ldapfield.logicalname" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="driver.ldapfield.attributevalue" /></td>
            		</tr>
					  <%if(ldapSPInterfaceData.getFieldMapSet()!=null && !ldapSPInterfaceData.getFieldMapSet().isEmpty()){ %>
						<logic:iterate id="fieldBean" name="ldapSPInterfaceData" property="fieldMapSet" scope="page" type="com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData">					  
				 			<tr> 
				            <td class="tblrows"  height="20%"><%=SPRFields.fromSPRFields(fieldBean.getLogicalName()).displayName%>&nbsp;</td>
				            <td class="tblcol"  height="20%"><bean:write name="fieldBean" property="ldapAttribute" />&nbsp;</td>
				          </tr>
						</logic:iterate>
					  <%}else{%>		
						<tr> 
			            <td class="tblfirstcol"  height="20%" colspan="2">No Record Found</td>
			           </tr>
			          <%}%>
				</table>
			</td>	  
          </tr>
          </table>
	</td>
   </tr>
   <%}%>
</table>
	







