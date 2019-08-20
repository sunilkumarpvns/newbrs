<%@ taglib uri="/struts-jquery-tags" prefix="sj"%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>

<script type="text/javascript">
	var defaultRows = <%=ConfigurationProvider.getInstance().getPageRowSize()%> ;
    var deploymentMode = '<s:property value="%{@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getDeploymentMode().name()}"/>';
    function isDeploymentModePCC(){
        return deploymentMode == '<s:property value="%{@com.elitecore.corenetvertex.constants.DeploymentMode@PCC.name()}"/>';
	}

    function isDeploymentModePCRF(){
        return deploymentMode == '<s:property value="%{@com.elitecore.corenetvertex.constants.DeploymentMode@PCRF.name()}"/>';
    }
    function isDeploymentModeOCS(){
        return deploymentMode == '<s:property value="%{@com.elitecore.corenetvertex.constants.DeploymentMode@OCS.name()}"/>';
    }

</script>    

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
   	
   	<sj:head compressed="true" jqueryui="true" loadAtOnce="true" locale="en" jquerytheme="cupertino"/>
   
    <link rel="SHORTCUT ICON" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon" title="NETVERTEXSM">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css"/>
	<%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/jquery/jquery-ui.min.css"/> --%>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/nvcommon.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/datatables/bootstrap/dataTables.bootstrap.css"/>

	<script src="${pageContext.request.contextPath}/jquery/jquery.ui.touch-punch.min.js"></script>
	<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>	
	<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap-tooltip.js"></script>					
	<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap-confirmation.js"></script>
 
	<script src="${pageContext.request.contextPath}/datatables/js/jquery.dataTables.js"></script>	 
	<script src="${pageContext.request.contextPath}/datatables/bootstrap/dataTables.bootstrap.js"></script>
	<script src="${pageContext.request.contextPath}/js/nvcommon.js"></script>
	<script src="${pageContext.request.contextPath}/js/autocompletefunctions.js"></script>
