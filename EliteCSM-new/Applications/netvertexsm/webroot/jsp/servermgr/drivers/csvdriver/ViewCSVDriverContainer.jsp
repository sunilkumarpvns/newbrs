<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="driverResources" key="driver"/>');
});
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  <tr> 
   <td width="10" class="small-gap">&nbsp;</td>
    <td colspan="2">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="box">				
			<tr>
				<td valign="top" align="right" width="85%">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">	
						<tr>	
							<td>
								<%@ include file = "../ViewDriverInstance.jsp" %> 
							</td>
						</tr>
						<tr>	
							<td>
								<%@ include file = "ViewCSVDriver.jsp" %>
							</td>
						</tr>
   					</table>
  				</td>	
				<td class="grey-bkgd" valign="top" width="15%">
					   <%@ include file = "../ViewDriverInstanceNavigation.jsp" %> 
				</td>
			</tr>
		</table>
    </td>
  </tr>
  <%@include file="/jsp/core/includes/common/Footerbar.jsp" %>

</table>
