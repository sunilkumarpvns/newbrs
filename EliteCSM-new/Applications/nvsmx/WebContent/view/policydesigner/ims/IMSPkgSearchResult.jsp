<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
	String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
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
	.collapsing {
		-webkit-transition: none;
		transition: none;
	}

</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/import.css"/>
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
		var selectVar = getSelectedValuesForDelete();
		if(selectVar == true){
			var flag = false;
			flag = deleteConfirmMsg(obj,"Delete selected ims packages ?");
			if(flag==true) {
				removeData();
			}
			return flag;
		}
	}

	function exportPackage(obj) {
		var selectVar = getSelectedValues();
		if(selectVar == false){
			return addWarning(".popup","At least select one ims package for export");
		}else{
			var flag = false;
			flag = deleteConfirmMsg(obj,"Export selected ims packages ?");
			if(flag==true) {
				exportData();
			}
			return flag;
		}
	}

	function getSelectedValuesForDelete(){
		var selectedData = false;
		var selectedDatas = document.getElementsByName("ids");
		var selectedModes = document.getElementsByName("modes");
		for (var index=0; index < selectedDatas.length; index++){
			if(selectedDatas[index].name == 'ids'){
				if(selectedDatas[index].checked == true){
					selectedData = true;
					if(selectedModes[index].value == 'LIVE2'){
						selectedData = false;
						return addWarning(".popup","LIVE2 IMS Packages can not be deleted");
					}
				}
			}


		}
		if(selectedData == false){
			return addWarning(".popup","At least select one ims package for delete");
		}
		return selectedData;
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


	function exportData() {
		document.forms["imsPkgSearchResult"].action = "export";
		document.forms["imsPkgSearchResult"].submit();
	}
	function createPackage() {
		document.forms["imsPkgSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/init";
		document.forms["imsPkgSearchResult"].submit();
	}

	function removeData() {
		document.forms["imsPkgSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/delete";
		document.forms["imsPkgSearchResult"].submit();
	}
	function exportAll() {
		document.forms["imsPkgSearchResult"].action = "exportAll";
		document.forms["imsPkgSearchResult"].submit();
		showProgressBar();
	}
	function importImsPackage(){
		if($("#uploadedFile").val().length == 0){
			addWarning(".popup","At least configure file for Import");
			return false;
		};
		document.forms["imsPkgSearchResult"].action = "importImsPkg";
		document.forms["imsPkgSearchResult"].submit();

	}

	$(function(){
		$('input[id=importImsPkgs]').change(function() {
			$('#uploadedFile').val($(this).val());
		});

	});
var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}

</script>

<s:form  id="imsPkgSearchResult" method="post" enctype="multipart/form-data"  cssClass="form-vertical">
	<s:hidden value="%{#session.staffBelongingGroupIds}" name="groupIds"/>
	<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<s:text name="ims.search" />
			</h3>
		</div>

		<div class="panel-body">
			<div class="row">
				<div class="col-xs-5">
			<span class="btn-group btn-group-sm">
				<button class="btn btn-default" role="button" id="btnAddPkg" onclick="createPackage();" >
					<span class="glyphicon glyphicon-plus" title="Add"></span>
				</button>
						
				<span class="btn-group btn-group-sm"  onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
					<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
						<span class="glyphicon glyphicon-trash" title="delete"></span>
					</button>
				</span>

			<span class="btn-group btn-group-sm"  id="btnImportGroup">
							<button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse" data-target="#import" aria-expanded="false" aria-controls="import" data-placement="right" title="import" role="button">
								<span class="glyphicon glyphicon-import" title="Import"></span>
							</button>
						</span>
						<span class="btn-group btn-group-sm"  id="btnExportPkg" data-toggle="confirmation-singleton" onclick="return exportPackage(this);" onmousedown="return exportPackage(this);" data-href="javascript:exportData();">
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
			</span></div>
			<div class="col-xs-7 collapse" id="import">
				<div class="input-append form-group">
					<div class="col-xs-10" >
						<div class="input-group">
							<input id="uploadedFile" class="form-control" type="text">
							<a class="input-group-addon" onclick="$('input[id=importImsPkgs]').click();" style="text-decoration:none;cursor : pointer;">Browse</a>
						</div>
					</div>
					<div class="col-xs-2">
						<button type="button" class="btn btn-sm btn-primary" role="button" onclick="return importImsPackage();">Import</button>
					</div>
				</div>
				<s:file id="importImsPkgs" type="file" name="importedFile" cssStyle="display:none;"/>
				<s:hidden name="importAction" id="userAction" />
			</div>
		</div>
        <s:set var="priceTag">
            <s:property value="getText('ims.pkg.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
        </s:set>
		<nv:dataTable 							
						id="imsPkgData"
						actionUrl="/genericSearch/policydesigner/ims/IMSPkg/searchData"																							
						beanType="com.elitecore.corenetvertex.pkg.ims.IMSPkgData" 
						rows="<%=rows %>"						
						subTableUrl=""
						width="100%" 
						showPagination="true"
						cssClass="table table-blue">	
			<nv:dataTableColumn  title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
			<nv:dataTableColumn title="Name" 		 beanProperty="name"  			hrefurl="${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/view?pkgId=id" sortable="true" />
			<nv:dataTableColumn title="Groups"  	 beanProperty="groupNames"  	/>
			<nv:dataTableColumn title="Status" 	 	 beanProperty="status" 			sortable="true" />
			<nv:dataTableColumn title="${priceTag}" 	 	 beanProperty="price" 			sortable="true" style="width:120px;" />
			<nv:dataTableColumn title="Mode" 	 	 beanProperty="packageMode" 	/>
			<nv:dataTableColumn title="" 			icon="<span class='glyphicon glyphicon-pencil'></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/init?pkgId=id&groupIds=groups" style="width:20px;border-right:0px;"  />
			<nv:dataTableColumn title="" 			disableWhen="packageMode==LIVE2"	 icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/delete?ids=id" 	 style="width:20px;"  />
			
		</nv:dataTable>
		
	</div>
</div>
	<!-- Modal -->
	<div class="modal fade" id="pleaseWaitDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" align="center">
		<div class="modal-dialog" style="margin:200px;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title set-title">Exporting Packages...</h4>
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
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
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