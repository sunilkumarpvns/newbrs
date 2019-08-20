<%@page import="com.ckeditor.EventHandler"%>
<%@page import="com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData"%>
<%@page import="com.ckeditor.CKEditorConfig"%>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<%@page import="com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="ISO-8859-1"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script src="${pageContext.request.contextPath}/ckeditor/config.js"></script>
<script src="${pageContext.request.contextPath}/ckeditor/adapters/jquery.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>

<script type="text/javascript">

     function copyValue(){
    	document.getElementById("templateData").value = CKEDITOR.instances.templateData.getData();
    }
    
     function validateForm(){
          return verifyUniquenessOnSubmit('notificationTemplateName','update','<s:text name="notificationTemplateData.id" />','com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData','','');
	}
</script>
<script type="text/javascript">
$( document ).ready(function() {
	$(".select2").select2();
	if(templateType.options[templateType.selectedIndex].value == 'SMS'){
		$("#subject").attr("disabled", "disabled"); 
		CKEDITOR.config.startupMode = 'source';
		 CKEDITOR.replace( 'templateData', {
		    removeButtons: 'Source',
		} );
	}else{
		CKEDITOR.replace( 'templateData');
	}
});
</script>
<%
request.setCharacterEncoding("UTF-8");
response.setContentType("text/html;charset=UTF-8");	
%>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"> <s:text name="Update Template" /> </h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" acceptcharset="UTF-8" action="policydesigner/notification/NotificationTemplate/update" id="notificationTemplateUpdate" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()" onsubmit="copyValue()">
			<s:token />
			<div class="row">
				<div class="col-xs-12 col-sm-10">
					<s:hidden 		name="notificationTemplateData.id" 	/>
					<s:textfield 	name="notificationTemplateData.name"  key="notification.name" 	id="notificationTemplateName"		cssClass="form-control focusElement" elementCssClass="col-xs-12 col-sm-8" 
					    onblur="verifyUniqueness('notificationTemplateName','update','%{notificationTemplateData.id}','com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData','','');"  />
					<s:hidden name="notificationTemplateData.templateType" />
					<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{notificationTemplateData.groups}"/>
					<s:select 		name="notificationTemplateData.templateType" id="templateType" key="notification.type" cssClass="form-control"	elementCssClass="col-xs-6 col-sm-4 col-md-2"	list="@com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType@values()" 	onchange="changeEditiorMode()" disabled="true"/>

					<div class="form-group ">

						<label class="col-xs-12 col-sm-4 col-lg-3 control-label" id="lbl_pkgGroup">Groups </label>

						<div class="col-xs-12 col-sm-8 col-lg-9 controls">
							<select name="groupIds" key="ratingGroupData.groups" class="form-control select2" style="width:100%" multiple="true">
								<s:iterator value="groupInfoList">
									<option locked="<s:property value="locked"/>" <s:property value="selected"/>
											value="<s:property value="id"/>" id="<s:property value="id"/>">
										<s:property value="name"/></option>
								</s:iterator>
							</select>
						</div>

					</div>

					<s:textfield 	name="notificationTemplateData.subject" id="subject"	 key="notification.subject" 		cssClass="form-control" elementCssClass="col-xs-12 col-sm-8"/>
					<s:textarea name="notificationTemplateData.templateData" key="notification.body" id="templateData" cssClass="form-control" elementCssClass="col-xs-12 col-sm-8"></s:textarea>
				</div>
			</div>

			<div class="row">
				<div class="col-xs-12" align="center">
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"  style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/view?notificationTemplateId=${notificationTemplateData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="Template" /></button>
				</div>
			</div>

		</s:form>
	</div>
</div>