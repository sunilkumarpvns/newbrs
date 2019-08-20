<%@page import="com.ckeditor.CKEditorConfig"%>
<%@page import="com.ckeditor.EventHandler"%>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script src="${pageContext.request.contextPath}/ckeditor/config.js"></script>
<script src="${pageContext.request.contextPath}/ckeditor/adapters/jquery.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script type="text/javascript">
    function changeEditiorMode() {
		var templateType = document.getElementById('templateType');
		if(templateType.options[templateType.selectedIndex].value == 'SMS'){
			CKEDITOR.instances.templateData.destroy();
			$("#subject").attr("disabled", "disabled"); 
			CKEDITOR.config.startupMode = 'source';
			 CKEDITOR.replace( 'templateData', {
			    removeButtons: 'Source',
			} );
		}else{
			CKEDITOR.instances.templateData.destroy();
			CKEDITOR_BASEPATH='${pageContext.request.contextPath}/ckeditor/';
    		CKEDITOR.replace('templateData');
    		CKEDITOR.config.startupMode = 'wysiwyg';
			$("#subject").removeAttr("disabled"); 
		}
	}
    function copyValue(){
    	document.getElementById("templateData").value = CKEDITOR.instances.templateData.getData();
    }
	function validateForm(){
		return verifyUniquenessOnSubmit('notificationTemplateName','create','','com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData','','');
	}
	$(function(){
    	$(".select2").select2();
    	CKEDITOR_BASEPATH='${pageContext.request.contextPath}/ckeditor/';
    	CKEDITOR.replace( 'templateData');
	});
</script>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"> <s:text name="Create Template" /> </h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" acceptcharset="UTF-8" action="policydesigner/notification/NotificationTemplate/create" id="notificationTemplateCreate" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()" onsubmit="copyValue()">
			<s:token />	
			<div class="row">
				<div class="col-xs-12 col-sm-10">
					<s:textfield 	name="notificationTemplateData.name" 		 key="notification.name" 	id="notificationTemplateName"		cssClass="form-control focusElement" elementCssClass="col-xs-12 col-sm-8" 
					    onblur="verifyUniqueness('notificationTemplateName','create','','com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData','','');"  />
					<s:select 		name="notificationTemplateData.templateType" id="templateType" key="notification.type" cssClass="form-control"	elementCssClass="col-xs-6 col-sm-4 col-md-2"	list="@com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType@values()" 	onchange="changeEditiorMode()" />
					<s:select name="groupIds" key="entity.groups"  id="groups"  list="#session.staffBelongingGroups"  cssClass="form-control select2" listKey="id" listValue="name" multiple="true" cssStyle="width:100%" elementCssClass="col-xs-12 col-sm-8" />
					<s:textfield 	name="notificationTemplateData.subject" key="notification.subject" id="subject"  		cssClass="form-control"  elementCssClass="col-xs-12 col-sm-8"/>
					<s:textarea name="notificationTemplateData.templateData"  key="notification.body" id="templateData" cssClass="form-control" elementCssClass="col-xs-12 col-sm-8" />

				</div>
			</div>

			<div class="row">
				<div class="col-xs-12" align="center">
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
				</div>
			</div>

		</s:form>
	</div>
</div>