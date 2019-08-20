<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message  bundle="deviceMgmtResources" key="devicemgmt.title"/>');	
});
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top">
    	<%@  include file = "ViewDeviceManagement.jsp" %>
	</td>
    <td width="15%" class="grey-bkgd" valign="top" rowspan="2">
      	<%@  include file = "DeviceManagementNavigation.jsp" %> 
    </td>
  </tr>
  <tr>
  	<td width="10">&nbsp;</td>
    <td width="85%" valign="top">
		<%@ include file = "EditDeviceManagement.jsp" %> 
	</td>
  </tr>
 <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

