<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<style type="text/css">
#fileMappingPreserveOtherKeys {
	margin-top: 0px;
}
</style>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="filemapping.update" />
		</h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/sm/filemapping" action="file-mapping" id="fileMapping" method="post" cssClass="form-horizontal" validate="true" validator="validateForm()" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8">
			<s:hidden name="_method" value="put" />
			<s:token />
			<div>
				<div class="col-xs-12 col-sm-6">
					<s:textfield name="name" id="fileMappingName" tabindex="1" key="filemapping.name" maxLength="100" cssClass="form-control focusElement" />
					<s:textarea name="description" id="fileMappingDescripton" tabindex="2" key="filemapping.description" maxLength="2000" cssClass="form-control" value="%{description}"/>
					<s:select name="type" id="fileMappingType" tabindex="3" key="filemapping.type" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.FileType@values()" listKey="name()" listValue="getValue()" />
					<s:checkbox name="preserveOtherKeys" id="fileMappingPreserveOtherKeys" tabindex="4" key="filemapping.preserveOtherKeys" value="%{preserveOtherKeys}"/>
				</div>

				<div id="fileMappingDetailDiv">
					<div class="col-xs-12 col-sm-12">
						<table id='fileMappingDetailTable' class="table table-blue table-bordered">
							<caption class="caption-header"><s:text name="filemapping.details" />
								<div align="right" class="display-btn">
									<span class="btn btn-group btn-group-xs defaultBtn" onclick="addDetail();" id="addRow">
									<span class="glyphicon glyphicon-plus"></span></span>
								</div>
							</caption>
							<thead>
								<th><s:text name="filemapping.sourceKey" /></th>
								<th><s:text name="filemapping.destinationKey" /></th>
								<th><s:text name="filemapping.valuemapping" /></th>
								<th><s:text name="filemapping.defaultValue" /></th>
								<th style="width: 35px;">&nbsp;</th>
							</thead>
							<tbody>
								<s:iterator value="fileMappingDetail" status="i" var="fileMappingDetail">
									<tr name='detailRow'>
										<td><s:textfield value="%{#fileMappingDetail.sourceKey}" name="fileMappingDetail[%{#i.count - 1}].sourceKey" id="sourceKey%{#i.count - 1}" maxLength="300" cssClass="form-control source-key" elementCssClass="col-xs-12" /></td>
										<td><s:textfield value="%{#fileMappingDetail.destinationKey}" name="fileMappingDetail[%{#i.count - 1}].destinationKey" id="destinationKey%{#i.count - 1}" maxLength="300" cssClass="form-control destination-key" elementCssClass="col-xs-12" /></td>
										<td><s:textfield value="%{#fileMappingDetail.valueMapping}" name="fileMappingDetail[%{#i.count - 1}].valueMapping" maxLength="300" cssClass="form-control" elementCssClass="col-xs-12" /></td>
										<td><s:textfield value="%{#fileMappingDetail.defaultValue}" name="fileMappingDetail[%{#i.count - 1}].defaultValue" maxLength="300" cssClass="form-control" elementCssClass="col-xs-12" /></td>
										<td style="width: 35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
						<div class="col-xs-12" id="generalError"></div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12" align="center">
						<button id="btnSubmit" tabindex="5" type="submit" class="btn btn-primary btn-sm" role="submit" formaction="${pageContext.request.contextPath}/sm/filemapping/file-mapping/${id}">
							<span class="glyphicon glyphicon-floppy-disk"></span>
							<s:text name="button.save" />
						</button>
						<button id="btnCancel" tabindex="6" type="button" class="btn btn-primary btn-sm" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/filemapping/file-mapping/${id}'">
							<span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;
							<s:text name="button.back" />
						</button>
					</div>
				</div>
			</div>
		</s:form>
	</div>
</div>

<%@include file="/WEB-INF/content/sm/filemapping/FileMappingValidation.jsp"%>

<script type="text/javascript">
$(document).ready(function(){
	
    var dataCount = JSON.parse('<s:property value="fileMappingDetail.size"/>');
	for(index = 0 ; index < dataCount ; index++){
		autoCompleteForFileMapping('sourceKey'+index);
		autoCompleteForFileMapping('destinationKey'+index);
	}
});

	function validateForm() {
		clearErrorMessages();
	
		var fileMappingDetailTableLegth = $("#fileMappingDetailTable tbody tr").length;
	
		var isValidName = verifyUniquenessOnSubmit('fileMappingName','update','<s:property value="id"/>','com.elitecore.corenetvertex.sm.filemapping.FileMappingData','','');
		
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

	var i = document.getElementsByName("detailRow").length;
	function addDetail(){
		
		createTable(i);
		i++;
	}
</script>
