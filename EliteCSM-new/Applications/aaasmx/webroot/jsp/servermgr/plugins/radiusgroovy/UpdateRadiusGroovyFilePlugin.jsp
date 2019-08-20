 <!-- Import Parameters -->
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
	#texteditor {
    font-family:monospace
}
.ace_editor {
    font-family:monospace!important
}
  </style>
 
<% UpdateRadiusGroovyPluginForm updateRadiusGroovyPluginForm = (UpdateRadiusGroovyPluginForm) request.getSession().getAttribute("updateRadiusGroovyPluginForm");%>
<html:form action="/updateRadiusGroovyPlugin" styleId="radiusGroovyPlugin" method="post" enctype="multipart/form-data">

	<html:hidden name="updateRadiusGroovyPluginForm" property="action" styleId="action" value="update" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="pluginType" styleId="pluginType" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="auditUId" styleId="auditUId" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="pluginInstanceId" styleId="pluginInstanceId" />
	<html:hidden name="updateRadiusGroovyPluginForm" property="groovyDatas" styleId="groovyDatas" />
	
	<html:hidden name="updateRadiusGroovyPluginForm" property="groovyFileName" styleId="groovyFileName" />
							
	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
		<tr>
			<td class="tblheader-bold" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.update.plugininstancedetails" />
			</td>
		</tr>
		   <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="pluginResources" key="plugin.plugininstancedetails" /></td>
		 </tr>  					
	   	 <tr>
			<td class="tblfirstcol" width="20%" height="20%" >
				<bean:message bundle="pluginResources" key="plugin.instname" />
			</td>
			<td class="tblcol" width="30%" height="20%" >
				<bean:write name="updateRadiusGroovyPluginForm" property="pluginName" />
			</td>
		 </tr>   
	 	 <tr>
			<td class="tblfirstcol" width="20%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.instdesc" />
			</td>
			<td class="tblcol" width="30%" height="20%" >
				<bean:write name="updateRadiusGroovyPluginForm" property="description"/>
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" width="25%" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.radius.groovyplugin.uploadedfiles" />
			</td>
		</tr>
		<tr>
			<td colspan="2" width="25%">
				<div style="padding: 10px;">
					 <textarea rows="29" cols="100" id="texteditor" wrap="off" autofocus="off" spellcheck="false" readonly="readonly" style="font-size: 12px;height: 100%;width: 100%;margin: 0;padding: 0;border-top:1px solid #F0F0F0;border-bottom:1px solid #F0F0F0;" class="noborder"></textarea>
				</div>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2">
				<input type="button" id="validate-groovy" value="Validate" class="light-btn" onclick="validateGroovy()"/>
				<input type="button" id="submitGroovy" value="Save" class="light-btn" onclick="validateGroovyPlugin()"/>
				<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/updateRadiusGroovyPlugin.do?pluginInstanceId=<%=updateRadiusGroovyPluginForm.getPluginInstanceId() %>'" />
			</td>
		</tr>
	</table>
	
</html:form>
 
 <!-- Javascript Files -->
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery-latest.min.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery.filer.min.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/radius-groovy-plugin-update.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>
  
 <!-- Required Js & css for Ace Editor -->
  <script src="<%=request.getContextPath()%>/js/aceeditor/ace.js"></script>
  <script src="<%=request.getContextPath()%>/js/aceeditor/mode-groovy.js"></script>
 
  <script type="text/javascript">
	var fileArray = [];
	var editDiv;
	
	$(document).ready(function() {
	     
		/* Read Groovy file and display in textarea */
		getReadGroovyMap();
		  
	});
	
	function getReadGroovyMap(){
		
		/* Below loop will iterate and render all the uploaded file and it will display list of file in GUI */
		<logic:iterate id="obj" name="updateRadiusGroovyPluginForm" property="groovyDetailsMap">
	
			<bean:define id="nestedObject" name="obj" property="value" />
	
			var blobdata = '<bean:write name="nestedObject" property="groovyData"/>';
			
		    var groovyData = (JSON.stringify(blobdata));	
		    var groovyDataParsed = JSON.parse(groovyData);
		    groovyDataParsed = groovyDataParsed.replace(/&quot;/g, '"');
			
			var textarea = $('#texteditor');
			   
	        editDiv = $('<div>', {
	               position: 'absolute',
	               width: '80%',
	               height: '80%',
	               'class': textarea.attr('class')
	        }).insertBefore(textarea);

	        groovyDataParsed = groovyDataParsed.trim();
	         
	        var editor = ace.edit(editDiv[0]);
	        editor.container.style.fontFamily = "monospace";
	        editor.renderer.setShowGutter(true);
	        editor.getSession().setUseWorker(false);
	        editor.getSession().setValue(groovyDataParsed);
	        editor.getSession().setMode("ace/mode/groovy");
	        $('#texteditor').height($(editDiv).height() + "px");
 
		</logic:iterate>

	}
	
	function validateGroovy(){
		
		 var editor = ace.edit(editDiv[0]);
		 
		 var fileContent = editor.getSession().getValue();
		 
		 if(isEmpty(fileContent)){
			 alert("Groovy file cannot be empty");
			 return false;
		 }
		
		 $.post("verifyGroovy.do", {"fileContent": fileContent}, function(data){
			
			 if(typeof $('#verifyGroovyDiv') !== undefined &&  $('#verifyGroovyDiv').length > 0){
					$('#verifyGroovyDiv').remove();
				}
				
				$('<div/>', {
				    id: "verifyGroovyDiv"
				}).appendTo(document.body);

				var groovyResult = "<table width='100%' cellspacing='0' cellpadding='0' border='0' style='background-color:white;margin-top:10px;margin-bottom:10px;'>";
				groovyResult +="<tr><td class='labeltext tblrows tblfirstcol' style='border-top:1px solid #ccc;' width='20%'>File Name</td><td class='labeltext tblrows'  style='border-top:1px solid #ccc;'>"+$('#groovyFileName').val()+"</td></tr>";
				
				data=data.trim();
				
				if( data == 'success'){
					groovyResult +="<tr><td class='labeltext tblrows tblfirstcol' width='20%'>Result</td><td class='labeltext tblrows'>Success  <span class='valid-groovy'></span></td></tr>";
				}else{
					groovyResult +="<tr><td class='labeltext tblrows tblfirstcol' width='20%'>Result</td><td class='labeltext tblrows'>Failure  <span class='invalid-groovy'></span></td></tr>";
					groovyResult +="<tr><td class='labeltext tblrows tblfirstcol' colspan='2'>"+data+"</td></tr>";
				}
				
				groovyResult+="</table>";
				
				$("#verifyGroovyDiv").html(groovyResult);
				$("#verifyGroovyDiv").dialog({
					modal: false,
					autoOpen: false,
					maxHeight : 550,
					width : 700,
					minHeight:100,
					position: ['center', 'top'],
	        		show: 'blind',
	        		hide: 'blind',
					title : "Groovy Result",
					close : function(){
						$("#verifyGroovyDiv").remove();
						$(caller).css({'background-color':'#2369A6'});
						$('#texteditor').focus();
					},
					open : function(){
						 $('.ui-dialog-buttonset').children('button').
	            		 removeClass("ui-button ui-widget ui-state-default ui-state-active ui-state-focus ui-corner-all").
	            		 mouseover(function() { $(this).removeClass('ui-state-hover');$(this).removeClass('buttonClass'); }).
	            		 mousedown(function() { $(this).removeClass('ui-state-active');$(this).removeClass('buttonClass'); }).
	            		 focus(function() { $(this).removeClass('ui-state-focus');$(this).removeClass('buttonClass'); });
					}
				}).css("font-size", "12px","background-color","white","position","relative");
				$("#verifyGroovyDiv").css({'z-index':'10000','background-color':'#f0f0f0'});
				$("#verifyGroovyDiv").dialog("open");
				$("#verifyGroovyDiv").parent().css({'z-index':'100000'});
				return false;
			 });	
	}
	
	function validateGroovyPlugin(){
		 fileArray = [];
		 var editor = ace.edit(editDiv[0]);
		 var isErrorExist=true;
		 var responseData='default';
		 
		 var fileContent = editor.getSession().getValue();
		 
		 if(isEmpty(fileContent)){
			 alert("Groovy file cannot be empty");
			 return false;
		 }
		
		 $.post("verifyGroovy.do", {"fileContent": fileContent}, function(data){
			 data=data.trim();
			 
			 if( data == 'success'){
				
				 $('#action').val('updategroovyfile');
				 var fileContent = editor.getSession().getValue();
				 var fileName = $('#groovyFileName').val();
				 
				 var fileObject = new File([ fileContent ], fileName);
				 fileObject.fileName = fileName;
				 fileObject.type = "file";
				 
				 fileArray.push({
						'fileName' 				: 	fileName,
						'fileContent' 			: 	fileContent
				 });

				 $('#groovyDatas').val(JSON.stringify(fileArray));
				 document.forms['radiusGroovyPlugin'].submit();
			 }else{
				 alert( $('#groovyFileName').val() + " File found with Errors.\nKindly Validate and Try again !!! ");
			 }
		 });
		 
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
	
	.ui-dialog-titlebar-close{
		background-color: transparent !important;
		border: none !important;
		background: none !important;
	}
	.buttonClass{
		background-color: transparent !important;
		border: none !important;
		background: none !important;
	}
	
</style>