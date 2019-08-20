	<%@page import="com.elitecore.elitesm.web.plugins.forms.CreateUniversalAuthPluginForm"%>
	<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
	<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
	<%@ page import="java.util.List"%>
	<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

    <% String basePath = request.getContextPath(); %>

	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

	<!--Stylesheets-->
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/font/font-awesome.css" />
	<link href="<%=request.getContextPath()%>/js/fileupload/filer/font/jquery-filer.css" type="text/css" rel="stylesheet" />
	<link href="<%=request.getContextPath()%>/js/fileupload/filer/jquery.filer-dragdropbox-theme.css" type="text/css" rel="stylesheet" />
	<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.ui.all.css" />
	
	<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js" ></script>

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<div id="main">
								<html:form action="createDiameterGroovyPlugin" styleId="diameterGroovyPlugin" method="post" enctype="multipart/form-data">
					
								<html:hidden name="createDiameterGroovyPluginForm" property="action" value="create" /> 
								<html:hidden name="createDiameterGroovyPluginForm" property="pluginName" styleId="pluginName" />
								<html:hidden name="createDiameterGroovyPluginForm" property="description" styleId="description" />
								<html:hidden name="createDiameterGroovyPluginForm" property="pluginType" styleId="pluginType" />
								<html:hidden name="createDiameterGroovyPluginForm" property="groovyDatas" styleId="groovyDatas" />
								<html:hidden name="createDiameterGroovyPluginForm" property="status" styleId="status" />
									
					
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="table-header">
											<bean:message bundle="pluginResources" key="plugin.groovyplugin.title" />
										</td>
									</tr>
									<tr>
										<td width="100%">
											<div class="plugin-box" align="center" >
												<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;">
													Upload Diameter Groovy Files
												</div>
												<div class="labeltext"  style="width: 100%;background: #f7f8fa;padding-top: 20px;padding-bottom:20px;">
													  <div>
													       <input type="file" name="groovyFile" class="groovyFile" id="groovyFilePlugin" onchange="onchnage()"/> 
													  </div>
												</div>
											</div> 
										</td>
									</tr>
									<tr>
										<td align="center">
											<input type="button" id="submitGroovy" value="Create" class="light-btn" onclick="validateGroovyPlugin()"/>
											<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchPlugin.do?'" />
										</td>
									</tr>
									<tr>
										<td>
											&nbsp;
										</td>
									</tr>
								</table>
								</html:form>
							</div>
						</td>
					</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%> 
				</table>
			</td>
		</tr>
	</table>
	
	<!-- View Uploaded Groovy File -->
	 <div id="groovyFileDialog" title="View Groovy File " style="display: none;height: 500px;width: 600px;padding: 0px;margin: 0px;overflow: hidden;">
	 	<textarea rows="100" cols="400" id="texteditor" wrap="off" autofocus="off" spellcheck="false" readonly="readonly" onfocus="this.blur()" style="font-size: 12px;height: 100%;width: 100%;margin: 0;padding: 0;overflow: v" class="noborder" ></textarea>
	 </div>
	 
	<!-- Javascript Files -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery-latest.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery.filer.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/diameter-groovy-plugin.js"></script>
	
	<script type="text/javascript">
		var fileArray = [];
		$(document).ready(function() {
	     
			/* Initialize Filer for Groovy file upload*/
			getReadyFiler();
			
		});
		
		/** Setting title in main header bar*/
		setTitle('Diameter Groovy Plugin'); 

	</script>
	
	<style>
        .file_input{
            display: inline-block;
            padding: 10px 16px;
            outline: none;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            white-space: nowrap;
            font-family: sans-serif;
            font-size: 11px;
            font-weight: bold;
            border-radius: 3px;
            color: #008BFF;
            border: 1px solid #008BFF;
            vertical-align: middle;
            background-color: #fff;
            margin-bottom: 10px;
            box-shadow: 0px 1px 5px rgba(0,0,0,0.05);
            -webkit-transition: all 0.2s;
            -moz-transition: all 0.2s;
            transition: all 0.2s;
        }
        .file_input:hover,
        .file_input:active {
            background: #008BFF;
            color: #fff;
        }
       .ui-button{
			background-color: transparent;
			border: none;
			background: none;
	    }
	
	   .ui-dialog-titlebar-close:hover{
	 		background-color: transparent;
			border: none;
			background: none;
	   }
		
    </style>
