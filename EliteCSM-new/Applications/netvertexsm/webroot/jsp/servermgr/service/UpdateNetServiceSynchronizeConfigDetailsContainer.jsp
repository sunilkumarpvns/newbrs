<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.create.server"/>');
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr height=380px>
    <td width="10" class="small-gap">&nbsp;</td>	
    <td width="85%" valign="top" class="box" >
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
		  <td valign="top">
			<%@ include file="ViewNetServiceInstance.jsp"%>
		  </td>
		</tr>
		<tr>
		  <td valign="top">
			<%@ include file="UpdateNetServiceSynchronizeConfigDetails.jsp"%>
		  </td>
		</tr>
      </table>
    </td>	
	<td class="grey-bkgd" valign="top" width="15%">
		<%@  include file="NetServiceInstanceNavigation.jsp"%>
	</td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

