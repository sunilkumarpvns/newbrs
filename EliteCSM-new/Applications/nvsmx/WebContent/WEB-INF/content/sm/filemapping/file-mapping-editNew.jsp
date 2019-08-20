
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<style type="text/css">
#fileMappingPreserveOtherKeys {
	margin-top: 0px;
}
</style>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="filemapping.create" />
		</h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/sm/filemapping" id="formMapping" action="file-mapping" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
			<s:token />
			<div>
				<div class="col-xs-12 col-sm-6">
					<s:textfield name="name" key="filemapping.name" id="fileMappingName" tabindex="1" maxLength="100" cssClass="form-control focusElement"  />
					<s:textarea name="description" key="filemapping.description" id="fileMappingDescripton"  tabindex="2" maxLength="2000" cssClass="form-control" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"/>
					<s:select name="type" key="filemapping.type" id="fileMappingType" tabindex="3" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.FileType@values()" listKey="name()" listValue="getValue()" />
					<s:checkbox name="preserveOtherKeys" key="filemapping.preserveOtherKeys" id="fileMappingPreserveOtherKeys" tabindex="4" value="false" />
				</div>
				<div id="fileMappingDetailDiv">
					<div class="col-xs-12 col-sm-12">
						<table id='fileMappingDetailTable' class="table table-blue table-bordered">
							<caption class="caption-header">
								<s:text name="filemapping.details" />
								<div align="right" class="display-btn">
									<span class="btn btn-group btn-group-xs defaultBtn" onclick="addDetail();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
								</div>
							</caption>
							<thead>
								<th><s:text name="filemapping.sourceKey" /></th>
								<th><s:text name="filemapping.destinationKey" /></th>
								<th><s:text name="filemapping.valuemapping" /></th>
								<th><s:text name="filemapping.defaultValue" /></th>
								<th style="width: 35px;">&nbsp;</th>
							</thead>
						</table>
						<div class="col-xs-12" id="generalError"></div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12" align="center">
						<s:submit id="btnSubmit" tabindex="5" cssClass="btn  btn-sm btn-primary" type="button" role="button">
							<span class="glyphicon glyphicon-floppy-disk"></span>
							<s:text name="button.save" />
						</s:submit>
						<button id="btnCancel" tabindex="6" type="button" class="btn btn-primary btn-sm"  value="Cancel"  onclick="javascript:location.href='${pageContext.request.contextPath}/sm/filemapping/file-mapping'">
							<span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;
							<s:text name="button.list" />
						</button>
					</div>
				</div>
			</div>
		</s:form>
	</div>
</div>

<%@include file="/WEB-INF/content/sm/filemapping/FileMappingValidation.jsp"%>

<script type="text/javascript">

	function validateForm() {
		clearErrorMessages();

		var fileMappingDetailTableLegth = $("#fileMappingDetailTable tbody tr").length;

		var isValidName = verifyUniquenessOnSubmit('fileMappingName', 'create', '', 'com.elitecore.corenetvertex.sm.filemapping.FileMappingData','', '');
			
		if (isValidName == false) 
			return false;
		else if (fileMappingDetailTableLegth < 1) {
			$("#generalError").addClass("bg-danger");
			$("#generalError").text("<s:text name='error.filemapping.filemappingdetail'/>");
			return false;
		} else if (fileMappingDetailTableLegth >= 1) {
			return validateChildTable("fileMappingDetailTable","source-key", "destination-key");
		}
	}
	
	var i = 0;
	function addDetail() {
		createTable(i);
		i++;
	}
	
	
</script>

