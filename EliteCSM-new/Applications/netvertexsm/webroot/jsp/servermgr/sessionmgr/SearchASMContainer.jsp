<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>


<table cellpadding="0" cellspacing="0" border="0" width="100%" >
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td> 
    <td width="85%" valign="top" class="box">
		<div  style="margin-left: 2.0em;" class="tblheader-bold"><bean:message bundle="sessionMgrResources" key="session.gx.viewsessioninstance" /></div>
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		    <tr>
				<td valign="top" align="right"> 
					<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
						<bean:define id="sessionInstanceBean" name="gxSessionData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.GxSessionInstanceData" />
						<tr>
							<td align="right" class="labeltext" valign="top" class="box" > 
								<table cellpadding="0" cellspacing="0" border="0" width="100%" >	
						          <tr> 
						            <td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="sessionMgrResources" key="session.gx.name" /></td>
						            <td class="tblcol" width="70%" height="20%" ><bean:write name="sessionInstanceBean" property="name"/>&nbsp;</td>
						          </tr>
					    		  <tr>
						          	<td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="sessionMgrResources" key="session.gx.description"/></td>
						          	<td class="tblcol" width="70%" height="20%"><bean:write name="sessionInstanceBean" property="discription" /></td>
						          </tr>
						          
						        </table>
							</td>
						</tr>
					</table>
				</td>
		    </tr>
		</table>
		<br>
		<%@ include file="SearchASM.jsp" %>
	</td>
	<td width="15%" class="grey-bkgd" valign="top">
      <%@  include file = "SessionInstanceNavigation.jsp" %> 
    </td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>


