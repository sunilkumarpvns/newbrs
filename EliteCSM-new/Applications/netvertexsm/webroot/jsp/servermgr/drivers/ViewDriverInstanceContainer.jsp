<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function(){
	<bean:message bundle="driverResources" key="driver.driverinstance.summary" />
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
 <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top" class="box">
    	<div style="margin-left: 1.7em;" class="tblheader-bold"><bean:message bundle="driverResources" key="driver.driverinstance.information" /></div>        
		<%@ include file = "ViewDriverInstance.jsp" %> 
	</td>
    <td width="15%" class="grey-bkgd" valign="top">
      <%@ include file = "ViewDriverInstanceNavigation.jsp" %> 
    </td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>

</table>

