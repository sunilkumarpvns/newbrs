<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<script type="text/javascript">
$(document).ready(function() {
	setTitle('<bean:message bundle="driverResources" key="driver"/>');
});
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
   <td width="10" class="small-gap">&nbsp;</td>
    <td colspan="2">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">				
			<tr>
				<td valign="top" align="right" width="85%" class="box">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">	
						<tr>	
							<td>
								<%@ include file = "../ViewSPInterface.jsp" %> 
							</td>
						</tr>
						<tr>	
							<td>
								<%@ include file = "ViewDBSPInterface.jsp" %>
							</td>
						</tr>
   					</table>
  				</td>	
				<td width="15%" class="grey-bkgd" valign="top">
					   <%@ include file = "DBSPInterfaceNavigation.jsp" %> 
				</td>
			</tr>
		</table>
    </td>
  </tr>
 <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

