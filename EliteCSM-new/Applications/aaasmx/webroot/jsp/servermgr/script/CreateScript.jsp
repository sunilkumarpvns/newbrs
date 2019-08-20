<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@ taglib prefix="ec" uri="/elitetags" %>
 
 <!-- Import Parameters -->
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData"%>
 
<%@ page import="com.elitecore.elitesm.web.script.form.ScriptInstanceForm"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
 
<%
 	String basePath = request.getContextPath();
 	ScriptInstanceForm createScriptForm = (ScriptInstanceForm) session.getAttribute("createScriptForm");
%>

 <link rel="stylesheet" href="<%=basePath%>/css/font/font-awesome.css" />
 <link href="<%=basePath%>/js/fileupload/filer/font/jquery-filer.css" type="text/css" rel="stylesheet" />
 <link href="<%=basePath%>/js/fileupload/filer/jquery.filer-dragdropbox-theme.css" type="text/css" rel="stylesheet" />
 <LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/mllnstyles.css" />
 <link rel="stylesheet" href="<%=basePath%>/jquery/development/themes/base/jquery.ui.all.css" />

 <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
 
 <script type="text/javascript">
 
 var isValidName;
 
</script>
 
<html:form action="/script?method=create" styleId="groovyScript" method="post" enctype="multipart/form-data">
	<html:hidden name="scriptInstanceForm" property="action" value="create" /> 
	<html:hidden name="scriptInstanceForm" property="description" styleId="description" />
	<html:hidden name="scriptInstanceForm" property="scriptType" styleId="scriptType"/>
	<html:hidden name="scriptInstanceForm" property="groovyDatas" styleId="groovyDatas" />
	
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
					<tr>
						<td class="tblheader-bold" height="20%" colspan="3">
							<bean:message bundle="scriptResources" key="script.update.scriptinstancedetails" />
						</td>
					</tr>
					<tr>
						<td class="captiontext padding-top" width="30%" height="20%">
							<bean:message bundle="scriptResources" key="script.instname" />
							<ec:elitehelp headerBundle="scriptResources" text="script.name" header="script.name"/>
						</td>
						<td class="labeltext padding-top" width="22%" height="20%" >
						
							<html:text styleId="scriptName" onkeyup="verifyName();" property="scriptName" size="40" maxlength="70" style="width:250px" tabindex="1" />
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
						<td class="labeltext padding-top" width="40%" valign="top">
							<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
						</td>
					</tr>
					<tr>
						<td class="captiontext" width="30%" height="20%">
							<bean:message bundle="scriptResources" key="script.description" />
							<ec:elitehelp headerBundle="scriptResources" text="script.description" header="script.description"/>
						</td>
						<td class="labeltext padding-bottom" width="*" height="20%">
							<html:textarea styleId="description" property="description" cols="40" rows="4" style="width:250px" tabindex="2" />
						</td>
					</tr>
					<tr>
					<td class="captiontext padding-top" width="30%" height="20%">
						<bean:message bundle="scriptResources" key="script.scripttype" />
						<ec:elitehelp headerBundle="scriptResources" text="script.scripttype" header="script.scripttype"/>
					</td>
					<td align="left" class="labeltext" valign="top">
						<html:select name="createScriptForm" styleId="selectedScript" property="selectedScript" tabindex="2" style="width:250px;">
							<html:option value="">Select</html:option>
							<logic:iterate id="objservice" name="createScriptForm" type="ScriptTypeData" property="scriptTypeList">
								<html:option value="<%=objservice.getScriptTypeId()%>"><%=objservice.getDisplayName()%></html:option>
							</logic:iterate>
						</html:select>
					</td>
					</tr>
					<tr>
						<td width="25%" height="20%" colspan="3">
							<div class="plugin-box" align="center">
								<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;">Upload Script Files</div>
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
							<input type="button" id="submitGroovy" value="Create" class="light-btn" onclick="validateScript()"/>
							<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/script.do?method=search'" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
</html:form>

 <!-- View Uploaded Groovy File -->
 <div id="groovyFileDialog" title="View Groovy File " style="display: none;height: 500px;width: 600px;padding: 0px;margin: 0px;overflow: hidden;">
	 <textarea rows="100" cols="400" id="texteditor" wrap="off" autofocus="off" spellcheck="false" readonly="readonly" onfocus="this.blur()" style="font-size: 12px;height: 100%;width: 100%;margin: 0;padding: 0;overflow: v" class="noborder" ></textarea>
  </div>

 
    <!-- Javascript Files -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery-latest.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/fileupload/filer/jquery.filer.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/script/create-script.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>

	<script type="text/javascript">
		var fileArray = [];
		$(document).ready(function() {
	     
			/* Initialize Filer for Groovy file upload*/
			getReadyFiler();
			verifyName();
		});
		
		/** Setting title in main header bar*/
		setTitle('<bean:message bundle="scriptResources" key="script.title"/>');
		$('#status').attr('checked', true);

		 function verifyName() {
			 var searchName = document.getElementById("scriptName").value;
			isValidName = verifyInstanceName('<%=InstanceTypeConstants.SCRIPT%>',searchName,'create','','verifyNameDiv');
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
	   
	   .ui-dialog .ui-dialog-titlebar-close {
	    background: none !important;
	    border: none;
	    cursor:pointer;
	   }
		
    </style>