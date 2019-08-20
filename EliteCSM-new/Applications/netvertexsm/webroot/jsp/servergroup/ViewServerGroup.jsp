<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function() {	
	 setTitle('<bean:message  bundle="serverGroupDataMgmtResources" key="group.title"/>');
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top" class="box">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<bean:define id="serverInstanceGroupForm" name="serverInstanceGroupForm" scope="request" type="com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupForm" />
			 <tr>
				<td valign="top" align="right"> 
					<table cellpadding="0" cellspacing="0" border="0" width="97%">
		 			<tr>
						<td class="tblheader-bold"  valign="top" colspan="3"  ><bean:message  bundle="serverGroupDataMgmtResources" key="group.view.title"/> </td>
					</tr>			
						  <tr> 
							<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="serverGroupDataMgmtResources" key="group.name" /></td> 
							<td align="left" class="tblrows" valign="top" colspan="2"> <bean:write name="serverInstanceGroupForm" property="name"/>&nbsp;</td>					
						  </tr>
						  <tr> 
							<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="serverGroupDataMgmtResources" key="group.accessgroups" /></td></td> 
							<td align="left" class="tblrows" valign="top" colspan="2"> <bean:write name="serverInstanceGroupForm" property="groupNames"/>&nbsp;</td>					
						  </tr>
					   	  <tr>
					   	  	<td class="small-gap">&nbsp;</td></tr><tr><td class="small-gap">&nbsp;</td>
					   	  </tr>
					</table></td>
			</tr>
			</tr>
		</table>
	</td>
    <td width="15%" class="grey-bkgd" valign="top">
      	<%@  include file = "ServerGroupNavigation.jsp" %> 
    </td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
		
