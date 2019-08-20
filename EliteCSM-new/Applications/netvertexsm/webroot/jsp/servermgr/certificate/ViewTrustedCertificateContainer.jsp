<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import=" com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<script>
$(document).ready(function(){
	setTitle('<bean:message key="server.trustedcertificate"/>');
});
</script>

<table width=100%" border="0" cellspacing="0" cellpadding="0">
  		  		<tr>
					<td width="100%" colspan="2" >
				        <table cellpadding="0" cellspacing="0" border="0" width="100%" >
				          	<tr>
					      		<td width="100%" colspan="2" >
					      		      <table width="100%" border="0" cellspacing="0" cellpadding="0"> 
								        <tr> 
								          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td> 
								          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">
								        	<bean:message bundle="servermgrResources" key="servermgr.certificate.header" />
								          	</td> 
								          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td> 
								          <td width="*"></td> 
								        </tr> 
								        <tr> 
								          <td width="*" valign="bottom"><img src="<%=basePath%>/images/line.jpg" style="width: 100%"  height="7" ></td> 
								        </tr> 
								      </table> 
					      		</td>
							</tr>
				        </table>
				      </td>
				</tr>	
				<tr>
					<td class="small-gap">
						&nbsp;
					</td>
				</tr>
				
					<tr>
						<td width="10" class="small-gap">&nbsp;</td>
						<td valign="top" align="right" width="85%">
						<table width="100%" border="0" cellspacing="0" cellpadding="0" class="box">	
							<tr>	
								<td>
							 		<%@ include file="ViewTrustedCertificate.jsp" %> 
				  				</td>
							</tr>
							<tr>	
								<td>
									<%@ include file="ViewTrustedCertificateDetail.jsp" %>
				  				</td>
							</tr>
    					</table>
    					</td>	
						<td width="15%" class="grey-bkgd" valign="top">
							<%@ include file="TrustedCertificateNavigation.jsp" %> 
						</td>
					</tr>
		<%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>