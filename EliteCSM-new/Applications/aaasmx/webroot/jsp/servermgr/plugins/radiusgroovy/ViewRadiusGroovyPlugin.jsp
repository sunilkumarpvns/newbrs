<%@page import="com.elitecore.elitesm.web.plugins.forms.UpdateRadiusGroovyPluginForm"%>
<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>

<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/plugin/plugin-button.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/filesaver/FileSaver.min.js"></script>

<%
	UpdateRadiusGroovyPluginForm updateRadiusGroovyPluginForm = (UpdateRadiusGroovyPluginForm)request.getAttribute("updateRadiusGroovyPluginForm");
%>
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
			<td class="tblheader-bold" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.view"/>
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
			<td class="tblfirstcol" width="20%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.status" />
			</td>
			<td class="tblcol" width="30%" height="20%" valign="middle">
				<logic:equal name="updateRadiusGroovyPluginForm" property="status" value="1">
				    <img src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" />
				</logic:equal>
				<logic:equal name="updateRadiusGroovyPluginForm" property="status" value="0">
				   <img src="<%=basePath%>/images/deactive.jpg" />&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" />
				</logic:equal>
			</td>
		</tr>
		 <tr>
			<td class="tblheader-bold" width="20%" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="upload.radius.groovyfiles.view.title" />
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center" style="padding-top:20px;">
				<table width="60%" cellspacing="0" cellpadding="0" border="0" id="groovy-mapping-table">
					<tr>
						<td class="tblheader-bold border-right-download" width="80%">
							<bean:message bundle="pluginResources" key="plugin.groovyfilename" />
						</td>
						<td class="tblheader-bold border-right-download" align="center" width="20%">
							<bean:message bundle="pluginResources" key="plugin.downloadgroovyfile" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

</html:form>
<!-- View Uploaded Groovy File -->
<div id="groovyFileDialog" title="View Groovy File " style="display: none;height: 500px;width: 600px;padding: 0px;margin: 0px;overflow: hidden;">
	 <textarea rows="100" cols="400" id="texteditor" wrap="off" autofocus="off" spellcheck="false" readonly="readonly" onfocus="this.blur()" style="font-size: 12px;height: 100%;width: 100%;margin: 0;padding: 0;overflow: v" class="noborder" ></textarea>
</div>

<!-- Template table for Groovy View -->
<table style="display: none;" id="groovy-template-table">
	<tr>
		<td class="plugin-table-firstcol groovyName labeltext" width="80%">
			<span class="groovy-file-name groovy-label-text" onClick="viewGroovyFileNew(this);"></span>
		</td>
		<td class="tbl-plugin-rows border-left-download" align="center" width="20%">
			<span class="downloadElement" onclick="downloadGroovyFile(this);"></span>
		</td>
	</tr>
</table>


<script type="text/javascript">
	
	var fileArray = [];
	
	/* Convert JSON format Data to Universal Plugin List */
	$(document).ready(function() {
	     
		/* Initialize Filer for Groovy file upload*/
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
			
			var lastModifiedDate = new Date(<bean:write name="nestedObject" property="date"/>);
	
			/*Instantiate a new file object and copy all the content from uploaded file and create object of File using Javascript File API  */
			var fileObject = new File([ groovyDataParsed ],
					"<bean:write name="nestedObject" property="groovyFileName"/>", {
						lastModified : lastModifiedDate
					});
	
			fileObject.fileName = '<bean:write name="nestedObject" property="groovyFileName"/>';
			fileObject.type = "file";
	
			var tableRowStr = $("#groovy-template-table").find("tr");
			$("#groovy-mapping-table").find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
			
			var findFileName = $("#groovy-mapping-table").find(' > tbody > tr ').last().find('.groovy-file-name');
			$(findFileName).text(fileObject.fileName);
			
			fileArray.push({
				'fileName' 				: 	'<bean:write name="nestedObject" property="groovyFileName"/>',
				'fileContent' 			: 	groovyDataParsed,
				'lastModified'			: 	fileObject.lastModified
			});
	
		</logic:iterate>
	
	}
	
	function viewGroovyFile(obj){
		var fileName = $(obj).text();
		$('#groovyFileDialog').attr('title', fileName);
		$.each(fileArray, function(key,value) {
			console.log(value);
			 if(value.fileName == fileName){
				  var fileData = value.fileContent;
				  
				  console.log(fileData);
				  
				  var f = new File([fileData], fileName);
			        f.fileName=fileName;
			        f.lastModified=value.lastModified;
			        
			        var reader1 = new  FileReader();
			        reader1.readAsText(f);
			        reader1.onload = function(e1) {
			        	$('#groovyFileDialog').find('#texteditor').text(e1.target.result);
			        	var dialogTitle = value.fileName;
			        	$('.ui-dialog-title').text(dialogTitle);
					    
			        	$('#groovyFileDialog').dialog({
							width : 700,
							height : 500,
							position : [ 'center', 'top' ],
							show : 'blind',
							hide : 'blind',
			        	});
			        };
			        console.log(f);
			 }
		});
	}
	
	function downloadGroovyFile(obj){
		var findObject = $(obj).parent().parent().find('.groovy-label-text');
		var downloadFileName = $(findObject).text();
		
		$.each(fileArray, function(key,value) {
			 if(value.fileName == downloadFileName){
				 var fileData = value.fileContent;
				 
				 var blob = new Blob([fileData], {type: "text/groovy",lastModified : value.lastModified});
				 saveAs(blob, downloadFileName);
			 }
		});
	}

	function viewGroovyFileNew(obj){
		var groovyFileName = $(obj).text();
			
			if(groovyFileName != null && groovyFileName.length > 0){
				$('#action').val('readgroovyfile');
				$('#groovyFileName').val(groovyFileName);
				$('#redirectUrlForViewFile').val('initViewRadiusGroovyPlugin.do');
				
				document.forms['radiusGroovyPlugin'].submit();
			}
		}
	
</script>
