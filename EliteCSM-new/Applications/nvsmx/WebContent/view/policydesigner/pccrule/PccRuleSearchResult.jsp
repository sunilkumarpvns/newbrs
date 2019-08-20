<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@ page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();

	
%> 
<style>
	.subtable td:nth-child(odd) {
  		text-align:left;  		
  		font-weight:bold; 
  		width: 110px;
	}
	
	.subtable td:nth-child(even) {
  		text-align:left;  		  		
  		width: 110px;
	}

</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/import.css"/>
<%
String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>
<script type="text/javascript">

	var timer;
	function showProgressBar(){

		var pleaseWait = $('#pleaseWaitDialog');
		$(pleaseWait).modal({backdrop: 'static', keyboard: false});
		showPleaseWait = function() {
			pleaseWait.modal('show');
			timer = setInterval(isExportCompleted,5000);

		};

		hidePleaseWait = function () {
			pleaseWait.modal('hide');
		};
		showPleaseWait();
	}
	function isExportCompleted(){
		var flag = '<%=request.getServletContext().getAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING)%>';
		if(flag == "false"){
			$('#pleaseWaitDialog').modal('hide');
			$("#exportPkgSuccess").modal('show');
			clearTimeout(timer);
		}
	}




	function removeRecords(obj) {
	var selectVar = getSelectedValues();
	if(selectVar == false){
		return addWarning(".popup","At least select one PCC Rule for delete");
    }else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Delete selected Pcc Rules ?");
    	if(flag==true) {
			removeData();
		}
		return flag;
    }
}

function getSelectedValues(){
	var selectedData = false;
	var selectedDatas = document.getElementsByName("ids");

	for (var i=0; i < selectedDatas.length; i++){
		if(selectedDatas[i].name == 'ids'){
			if(selectedDatas[i].checked == true){
				selectedData = true;
			}
		}
	}
	return selectedData;
}
var scope = '<s:text name="%{@com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL.name()}" />';
function createPackage(){

	if(scope == 'GLOBAL'){
		document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/init?scope="+scope;
	}else{
		document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/init?scope="+scope;
	}
	document.forms["pccRuleSearchForm"].submit();
}

function removeData(){
	if(scope == 'GLOBAL'){
		document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/delete?scope="+scope;
	}else{
		document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/delete?scope="+scope;
	}
	document.forms["pccRuleSearchForm"].submit();
}

var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}

function exportPCCRule(obj) {
	var selectVar = getSelectedValues();
	if(selectVar == false){
		return addWarning(".popup","Select at least one PCC Rule for export");
	}else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Export selected PCC Rule ?");
		if(flag==true) {
			exportData();
		}
		return flag;
	}
}
function exportData(){
	document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/export";
	document.forms["pccRuleSearchForm"].submit();
}
function exportAll(){
	document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/exportAll";
	document.forms["pccRuleSearchForm"].submit();
	showProgressBar();
}


function importPCCRules(){
	if($("#uploadedFile").val().length == 0){
		addWarning(".popup","At least configure file for Import");
		return false;
	}
	document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/importPCCRule";
	document.forms["pccRuleSearchForm"].submit();

}

$(function(){
	$('input[id=importPCCRules]').change(function() {
		$('#uploadedFile').val($(this).val());
	});

});

	function importPccRule(){
		if($("#uploadedFile").val().length == 0){
			addWarning(".popup","At least configure file for Import");
			return false;
		}
		document.forms["pccRuleSearchForm"].action = "${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/importPCCRule";
		document.forms["pccRuleSearchForm"].submit();
	}

</script>

<s:form  id="pccRuleSearchForm" method="post" cssClass="form-vertical" validate="false" enctype="multipart/form-data">
	<s:hidden name="pccRule.scope" value="%{@com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL.name()}"/>
	<s:hidden value="%{#session.staffBelongingGroupIds}" name="groupIds"/>
	<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<s:text name="global.pccrule.search" />
			</h3>
		</div>

		<div class="panel-body">
	    <div class="row">
			<div class="col-xs-5">
				<span class="btn-group btn-group-sm" >
					<button class="btn btn-default" role="button" id="btnAddPkg" onclick="createPackage();" >
						<span class="glyphicon glyphicon-plus" title="Add"></span>
					</button>
					<span class="btn-group btn-group-sm"  id="btnRemove" data-toggle="confirmation-singleton" onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
						<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
							<span class="glyphicon glyphicon-trash" title="delete"></span>
						</button>
					</span>
					<span class="btn-group btn-group-sm"  id="btnImportGroup">
								<button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse" data-target="#import" aria-expanded="false" aria-controls="import" data-placement="right" title="import" role="button">
									<span class="glyphicon glyphicon-import" title="Import"></span>
								</button>
							</span>
							<span class="btn-group btn-group-sm"  id="btnExportPkg" data-toggle="confirmation-singleton" onclick="return exportPCCRule(this);" onmousedown="return exportPCCRule(this);" data-href="javascript:exportData();">
								<button id="btnExport" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="export" role="button">
									<span class="glyphicon glyphicon-export" title="export"></span>
								</button>
							</span>

							<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" data-target="#exportAll" aria-expanded="false">
								<span class="caret"></span>
								<span class="sr-only">Toggle Dropdown</span>
							</button>
							<span id="exportAll">
								<ul class="dropdown-menu" >
									<li><a href="javascript:exportAll();">Export All</a></li>
								</ul>
							</span>

				</span>
			</div>
			<div class="col-xs-7 collapse" id="import">
				<div class="input-append form-group">
					<div class="col-xs-10" >
						<div class="input-group">
							<input id="uploadedFile" class="form-control" type="text" title="upload file">
							<a class="input-group-addon" onclick="$('input[id=importPCCRules]').click();" style="text-decoration:none;cursor : pointer;">Browse</a>
						</div>
					</div>
					<div class="col-xs-2">
						<button type="button" class="btn btn-sm btn-primary" role="button" onclick="return importPccRule()" >Import</button>
					</div>
				</div>
				<s:file id="importPCCRules" type="file" name="importedFile" cssStyle="display:none;"/>
				<s:hidden name="importAction" id="userAction" />
			</div>
		</div>
		<%-- id is mandatory --%>
		<%-- beanType is mandatory --%>
		<%-- rows must be greater than zero --%>
			<s:set value="%{@com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL.name()}" var="scope"/>
			<nv:dataTable id="PCCRuleDataGlobalSearch"
				actionUrl="/genericSearch/policydesigner/pccrule/PccRule/searchData?scope=${scope}"
				beanType="com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleWrapper"
				rows="<%=rows%>"  width="100%"
				showPagination="true" cssClass="table table-blue">

				<nv:dataTableColumn
					title="<input type='checkbox' name='selectAll'  id='selectAll' />"
					beanProperty="id" style="width:5px !important;" />
				<nv:dataTableColumn title="PCCRule" beanProperty="name"
					tdCssClass="text-left text-middle word-break"
					hrefurl="${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/view?pccRuleId=id"
					tdStyle="width:200px" />
				<nv:dataTableColumn title="Service" beanProperty="dataServiceTypeData.name"
					tdCssClass="text-left text-middle word-break" tdStyle="width:100px" hrefurl="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=dataServiceTypeData.id"/>
				<nv:dataTableColumn title="Type" beanProperty="type"
					tdCssClass="text-left text-middle word-break" tdStyle="width:100px" />
				<nv:dataTableColumn title="Monitoring Key"
					beanProperty="monitoringKey"
					tdCssClass="text-left text-middle word-break" tdStyle="width:200px" />
				<nv:dataTableColumn title="" sortable="false"
					icon="<span class='glyphicon glyphicon-pencil'></span>"
					style="width:20px;" tdStyle="width:20px;" tdCssClass="text-middle"
					hrefurl="edit:${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/init?pccRuleId=id&scope=${scope}&groupIds=groups" />
				<nv:dataTableColumn title=""
					icon="<span class='glyphicon glyphicon-trash'></span>"
					hrefurl="delete:${pageContext.request.contextPath}/globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/delete?ids=id&scope=${scope}"
					style="width:20px;" tdStyle="width:20px;" tdCssClass="text-middle" />
			</nv:dataTable>
		</div>
</div>
	<!-- Modal -->
	<div class="modal fade" id="pleaseWaitDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" align="center">
		<div class="modal-dialog" style="margin:200px;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title set-title">Exporting PCC Rules...</h4>
				</div>
				<div class="modal-body">
					<div class="progress">
						<div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 1000%">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Success Message for exportAll -->
	<div class="modal fade" id="exportPkgSuccess" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" >
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title set-title">Success</h4>
				</div>
				<div class="modal-body">
					<h5>Your download will begin shortly</h5>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal">Ok</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal-dialog -->
	</div>
</s:form>
