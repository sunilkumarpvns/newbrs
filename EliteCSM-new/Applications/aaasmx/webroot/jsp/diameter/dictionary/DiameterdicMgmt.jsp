
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<body>
	<LINK REL="stylesheet" TYPE="text/css"
		HREF="<%=request.getContextPath()%>/css/mllnstyles.css">
	<script type="text/javascript" language="javascript"
		src="<%=request.getContextPath()%>/diameterdicmgmt/diameterdicmgmt.nocache.js"></script>

	<!-- OPTIONAL: include this if you want history support -->
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>

	<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>
	<%
    String basePath = request.getContextPath();
     %>
	<script>
     setTitle('<bean:message bundle="diameterResources" key="dictionary.module.name"/>');
     </script>
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table border="0" width="100%">
								<tr>
									<td id="demo" width="100%"></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="3" class="small-gap">&nbsp;</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
	<!-- style="position:absolute; width:100%; text-align:center; top:300px;" -->
	<div id="loading" class="loading">
		Loading<BR /> <img src="<%=basePath%>/images/loading.gif" />
	</div>



</body>
</html>