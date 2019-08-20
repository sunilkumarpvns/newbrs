<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
 int rows = ConfigurationProvider.getInstance().getPageRowSize();
 String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>


<link rel="stylesheet" href="${pageContext.request.contextPath}/css/import.css"/>
<script type="text/javascript">

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

function removeRecords(obj) {
	var selectVar = getSelectedValues();
    if(selectVar == false){
		return addWarning(".popup","At least select one Rating Group for delete");
    }else{
		showDeleteConfirmation(null);
    }
} 

function createRatingGroup(){
	document.forms["ratingGroupSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/init";
	document.forms["ratingGroupSearchResult"].submit();
}

function removeData(){
	document.forms["ratingGroupSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/delete";
	document.forms["ratingGroupSearchResult"].submit();
}

var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}
function showProgressBar(){

	var pleaseWait = $('#pleaseWaitDialog');
	$(pleaseWait).modal({backdrop: 'static', keyboard: false});
	showPleaseWait = function() {
		pleaseWait.modal('show');
		timer = setInterval(isExportCompleted,2000);

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
		clearTimeout(timer);
	}
}


function exportRatingGroup(obj) {
	var selectVar = getSelectedValues();
	if(selectVar == false){
		return addWarning(".popup","At least select one Rating Group for export");
	}else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Export selected Rating Groups ?");
		if(flag==true) {
			exportData();
		}
		return flag;
	}
}
function exportData(){
	document.forms["ratingGroupSearchResult"].action = "export";
	document.forms["ratingGroupSearchResult"].submit();
}

function exportAll(){
	document.forms["ratingGroupSearchResult"].action = "exportAll";
	document.forms["ratingGroupSearchResult"].submit();
	showProgressBar();
}


function importRatingGroup(){
	if($("#uploadedFile").val().length == 0){
		addWarning(".popup","At least configure file for Import");
		return false;
	};
	document.forms["ratingGroupSearchResult"].action = "importRatingGroup";
	document.forms["ratingGroupSearchResult"].submit();

}

$(function(){
	$('input[id=importRatingGroups]').change(function() {
		$('#uploadedFile').val($(this).val());
	});

});
</script>
<s:form  id="ratingGroupSearchResult" method="post"  enctype="multipart/form-data" cssClass="form-vertical">
	<s:hidden value="%{#session.staffBelongingGroupIds}" name="groupIds"/>
	<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<s:text name="ratinggroup.search" />
			</h3>
		</div>

		<div class="panel-body">
			<div class="row">
			<div class="col-xs-5">
			<span class="btn-group btn-group-sm">
				<button class="btn btn-default" role="button" onclick="createRatingGroup();" >
					<span class="glyphicon glyphicon-plus" title="Add"></span>
				</button>
						
				<span class="btn-group btn-group-sm" onclick="return removeRecords(this);" onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
					<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
						<span class="glyphicon glyphicon-trash" title="delete"></span>
					</button>
				</span>
				<span class="btn-group btn-group-sm"  id="btnImportGroup">
							<button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse" data-target="#import" aria-expanded="false" aria-controls="import" data-placement="right" title="import" role="button">
								<span class="glyphicon glyphicon-import" title="Import"></span>
							</button>
						</span>
						<span class="btn-group btn-group-sm"  id="btnexportRatingGroup" data-toggle="confirmation-singleton" onclick="return exportRatingGroup(this);" onmousedown="return exportRatingGroup(this);" data-href="javascript:exportData();">
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
							<input id="uploadedFile" class="form-control" type="text">
							<a class="input-group-addon" onclick="$('input[id=importRatingGroups]').click();" style="text-decoration:none;cursor : pointer;">Browse</a>
						</div>
					</div>
					<div class="col-xs-2">
						<button type="button" class="btn btn-sm btn-primary" role="button" onclick="return importRatingGroup();">Import</button>
					</div>
				</div>
				<s:file id="importRatingGroups" type="file" name="importedFile" cssStyle="display:none;"/>
				<s:hidden name="importAction" id="userAction" />
			</div>
			</div>

			<nv:dataTable
				id="ratingGroupData"
				beanType="com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData"
				actionUrl="/genericSearch/policydesigner/ratinggroup/RatingGroup/searchData"
				rows="<%=rows%>"
				showPagination="true"
				cssClass="table table-blue"
				width="100%">
				
				<nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />			
				<nv:dataTableColumn title="Name"	beanProperty="name"	hrefurl="${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/view?ratingGroupId=id" sortable="true" style="width:200px;"/>
				<nv:dataTableColumn title="Identifier"	beanProperty="identifier" />
				<nv:dataTableColumn title="Description" beanProperty="description" />
				<nv:dataTableColumn icon="<span class='glyphicon glyphicon-pencil'></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/init?ratingGroupId=id&groupIds=groups" style="width:20px;border-right:0px;"  />
				<nv:dataTableColumn icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:javascript:showDeleteConfirmation(id)" 	style="width:20px;"  />
			
			</nv:dataTable>
		</div>
	</div>
	<div class="modal fade" id="pleaseWaitDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" align="center">
		<div class="modal-dialog" style="margin:200px;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title set-title">Exporting Rating Groups...</h4>
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
</s:form>
<%@include file="/view/policydesigner/ratinggroup/RatingGroupsCommonValidation.jsp" %>
<script>
	function showDeleteConfirmation (id) {
		if(isNullOrEmpty(id)) {
			document.forms["ratingGroupSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/delete";
		} else {
			$('input[type=checkbox]').each(function () {
				$(this).attr('checked',false);
			});
			document.forms["ratingGroupSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/delete?ids="+id;
		}
		$("#confirmAlert").modal('show');
	}
	function submit(){
		document.forms["ratingGroupSearchResult"].submit();
	}
</script>
