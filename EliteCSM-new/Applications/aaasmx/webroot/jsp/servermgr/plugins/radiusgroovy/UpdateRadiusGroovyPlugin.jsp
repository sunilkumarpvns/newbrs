 <!-- Import Parameters -->
 <%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.plugins.forms.UpdateRadiusGroovyPluginForm"%>
 <%@ page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>

 <!--Import  Stylesheet  -->
 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/font/font-awesome.css" />
 <link href="<%=request.getContextPath()%>/js/fileupload/filer/font/jquery-filer.css" type="text/css" rel="stylesheet" />
 <link href="<%=request.getContextPath()%>/js/fileupload/filer/jquery.filer-dragdropbox-theme.css" type="text/css" rel="stylesheet" />
 <LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
 <link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.ui.all.css" />
 
 <!-- Import Javascript -->
 <script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js" ></script> 
 
 <style type="text/css">
 	.ui-state-hover, .ui-widget-content .ui-state-hover, .ui-widget-header .ui-state-hover, .ui-state-focus, .ui-widget-content .ui-state-focus, .ui-widget-header .ui-state-focus {/*  border: 1px solid #999999{borderColorHover}; background: #dadada{bgColorHover} url(images/ui-bg_glass_75_dadada_1x400.png){bgImgUrlHover} 50%{bgHoverXPos} 50%{bgHoverYPos} repeat-x{bgHoverRepeat}; font-weight: normal{fwDefault}; color: #212121{fcHover};  */}
 
 	.ui-state-highlight {
		margin: 10px !important;
		background-color: white;
	}
	
	.ui-button {
		background-color: transparent !important;
		border: none;
		background: none;
	}
	
	.ui-dialog-titlebar-close {
		background-color: transparent !important;
		border: none !important;
		background: none !important;
	}
	
	.ui-dialog-titlebar-close:hover {
		background-color: transparent;
		border: none;
		background: none;
	}
	
	.ui-button .ui-widget .ui-state-default .ui-corner-all .ui-button-icon-only .ui-dialog-titlebar-close {
		background-color: transparent;
		border: none;
		background: none;
	}
 </style>
 
<% 
UpdateRadiusGroovyPluginForm updateRadiusGroovyPluginForm = (UpdateRadiusGroovyPluginForm) request.getSession().getAttribute("updateRadiusGroovyPluginForm");%>
<html:form action="/updateRadiusGroovyPlugin" styleId="radiusGroovyPlugin" method="post" enctype="multipart/form-data">

	<html:hidden name="updateRadiusGroovyPluginForm" property="action" styleId="action" value="update" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="pluginType" styleId="pluginType" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="auditUId" styleId="auditUId" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="pluginInstanceId" styleId="pluginInstanceId" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="groovyDatas" styleId="groovyDatas" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="groovyFileName" styleId="groovyFileName" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="redirectUrlForViewFile" styleId="redirectUrlForViewFile" />
							
	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
		<tr>
			<td class="tblheader-bold" height="20%" colspan="3">
				<bean:message bundle="pluginResources" key="plugin.update.plugininstancedetails" />
			</td>
		</tr>
		<tr>
			<td class="captiontext padding-top" width="30%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.instname" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.name" header="plugin.name"/>
			</td>
			<td class="labeltext padding-top" width="22%" height="20%" >
				<html:text styleId="pluginName" onkeyup="verifyName();" property="pluginName" size="40" maxlength="70" style="width:250px" tabindex="1" />
				<font color="#FF0000"> *</font>
				<div id="verifyNameDiv" class="labeltext"></div>
			</td>
			<td class="labeltext padding-top" width="40%" valign="top">
				<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
			</td>
		</tr>
		<tr>
			<td class="captiontext" width="30%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.description" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.description" header="plugin.description"/>
			</td>
			<td class="labeltext padding-bottom" width="*" height="20%">
				<html:textarea styleId="description" property="description" cols="40" rows="4" style="width:250px" tabindex="2" />
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" width="25%" height="20%" colspan="3">
				<bean:message bundle="pluginResources" key="plugin.radius.groovyplugin.uploadedfiles" />
			</td>
		</tr>
		<tr>
			<td width="25%" height="20%" colspan="3">
				<div class="plugin-box" align="center">
					<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;">Upload Radius Groovy Files</div>
					<div class="labeltext" style="width: 100%; background: #f7f8fa; padding-top: 20px; padding-bottom: 20px;">
						<div>
							<input type="file" multiple="multiple" name="groovyFile" id="groovyFilePlugin" class="groovyFile" onchange="onchnage()" />
						</div>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="3">
				<input type="button" id="submitGroovy" value="Update" class="light-btn" onclick="validateGroovyPlugin()"/>
				<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchPlugin.do?'" />
			</td>
		</tr>
	</table>
	
</html:form>

 <!-- View Uploaded Groovy File -->
 <div id="groovyFileDialog" title="View Groovy File " style="display: none;height: 500px;width: 600px;padding: 0px;margin: 0px;overflow: hidden;">
	 <textarea rows="100" cols="400" id="texteditor" wrap="off" autofocus="off" spellcheck="false" readonly="readonly" onfocus="this.blur()" style="font-size: 12px;height: 100%;width: 100%;margin: 0;padding: 0;overflow: v" class="noborder" ></textarea>
  </div>

 
 <!-- Javascript Files -->
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery-latest.min.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery.filer.min.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/radius-groovy-plugin-update.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>
 
 <script type="text/javascript">
	var fileArray = [];
	var editDiv;
	var isValidName;
	$(document).ready(function() {
		/* Initialize Filer for Groovy file upload*/
		getReadyFiler();
		getReadGroovyMap();
		
		setFileInputIndex("groovyFile");
	});
	
	function verifyName() {
		var searchName = document.getElementById("pluginName").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.PLUGIN%>',searchName,'update','<%=updateRadiusGroovyPluginForm.getPluginInstanceId()%>','verifyNameDiv');
	}
	
	function getReadGroovyMap(){
		
		/* Below loop will iterate and render all the uploaded file and it will display list of file in GUI */
		<logic:iterate id="obj" name="updateRadiusGroovyPluginForm" property="groovyDetailsMap">
	
			<bean:define id="nestedObject" name="obj" property="value" />
	
			var blobdata = '<bean:write name="nestedObject" property="groovyData"/>';
			
		    var groovyData = (JSON.stringify(blobdata));	
		    var groovyDataParsed = JSON.parse(groovyData);
		    groovyDataParsed = groovyDataParsed.replace(/&quot;/g, '"');
		    
			var lastModifiedDate = new Date(<bean:write name="nestedObject" property="date"/>);
	
			/*Instantiate a new file object and copy all the content from uploaded file and create object of File using Javascript File API  */
			var fileObject = new File([ groovyDataParsed ],
					"<bean:write name="nestedObject" property="groovyFileName"/>", {
						lastModified : lastModifiedDate
					});
			
		
			fileObject.fileName = '<bean:write name="nestedObject" property="groovyFileName"/>';
			fileObject.type = "file";
			
			/*This function will trigger filer plugin and will append ito file uploader based on data */
			$('#groovyFilePlugin').trigger("filer.append", {
				files : [ fileObject ]
			}); 
			
			
			fileArray.push({
				'fileName' 				: 	'<bean:write name="nestedObject" property="groovyFileName"/>',
				'fileContent' 			: 	groovyDataParsed,
				'lastModified'			: 	fileObject.lastModified
			});

			</logic:iterate>

		}
	
	function verifyInstanceName(instanceType,searchName,mode,id,divId) {

		if(searchName!='' && searchName.indexOf("%")< 0){
			var data  = $.ajax({
			   type: "POST",
			   url: "<%=request.getContextPath()%>/VerifyInstanceNameServlet",
			   async:false,
			   data: "instanceType="+instanceType+"&searchName="+searchName+"&mode="+mode+"&id="+id
			}).responseText;

			if(data.trim()=='true') {
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/tick.jpg'/> Valid Name.");
				return true;
			}else if(data.trim()=='false'){
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists.</font>");
				return false;
			}else if(data.trim()=='invalid'){
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Not a valid Name. Valid Characters : A-Z, a-z, 0-9, ., -, _ </font>");
				return false;
			}else{
				var temp = "<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>" +data.trim()+"</font>";
				$('#' + divId).html(temp);
			}
		}else if(searchName.indexOf("%") > -1){
			$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/><font color='#FF0000'>Not a valid Name. Valid Characters : A-Z, a-z, 0-9, ., -, _ </font>");
			return false;
		}else{
			$('#' + divId).html("&nbsp;");
		}
	}
	
	</script>

<style type="text/css">
	.ui-state-highlight {
		margin: 10px !important;
		background-color: white;
	}
	
	.file_input {
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
		box-shadow: 0px 1px 5px rgba(0, 0, 0, 0.05);
		-webkit-transition: all 0.2s;
		-moz-transition: all 0.2s;
		transition: all 0.2s;
	}
	
	.file_input:hover,.file_input:active {
		background: #008BFF;
		color: #fff;
	}
	
	.ui-button {
		background-color: transparent;
		border: none;
		background: none;
	}
	
	.ui-dialog-titlebar-close:hover {
		background-color: transparent;
		border: none;
		background: none;
	}
	
	.ace_print_margin{
		background: none;
	}
	
	.ui-button:hover{
		color: #FFF;
		background-color: #005197
	}
	.ui-dialog .ui-dialog-buttonpane{
		margin-top:1;
	}
	
	.ui-button-text-only{
		border: medium none;
		font-family: Arial;
		font-size: 12px;
		color: #FFF;
		background-color: #005197;
		font-weight: bold;
		padding: 3px;
	}
	
	.ui-widget-overlay{background:white !important;}
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