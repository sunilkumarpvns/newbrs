<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
 int rows = ConfigurationProvider.getInstance().getPageRowSize();
 String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>


<style>
.btn-file input[type=file] {
	position: absolute;
	top: 0;
	right: 0;
	min-width: 100%;
	min-height: 100%;
	font-size: 100px;
	text-align: right;
	filter: alpha(opacity=0);
	opacity: 0;
	outline: none;
	background: white;
	cursor: inherit;
	display: block;
}

</style>
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
		return addWarning(".popup","At least select one Charging Rule Base Name for delete");
    }else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Delete selected Charging Rule Base Names?");
    	if(flag==true){
    		removeData();
    	}
		return flag;
    }
}

function createChargingRuleBaseName(){
	document.forms["chargingRuleBaseNameSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/init";
	document.forms["chargingRuleBaseNameSearchResult"].submit();
}

function removeData(){
	document.forms["chargingRuleBaseNameSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/delete";
	document.forms["chargingRuleBaseNameSearchResult"].submit();
}

var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}


/*IMPORT EXPORT java script code Begin*/

function exportChargingRuleBaseName(obj) {
	var selectVar = getSelectedValues();
	if(selectVar == false){
		return addWarning(".popup","Select at least one ChargingRuleBaseName for export");
	}else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Export selected ChargingRuleBaseName ?");
		if(flag==true) {
			exportData();
		}
		return flag;
	}
}

function exportData(){
	document.forms["chargingRuleBaseNameSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/export";
	document.forms["chargingRuleBaseNameSearchResult"].submit();
}

function exportAll(){
	document.forms["chargingRuleBaseNameSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/exportAll";
	document.forms["chargingRuleBaseNameSearchResult"].submit();
	showProgressBar();
}

function importChargingRuleBaseNames(){
	if($("#uploadedFile").val().length == 0){
		addWarning(".popup","At least configure file for Import");
		return false;
	}
	document.forms["chargingRuleBaseNameSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/importChargingRuleBaseName";
	document.forms["chargingRuleBaseNameSearchResult"].submit();
}

$(function(){
	$('input[id=importChargingRuleBaseNames]').change(function() {
		$('#uploadedFile').val($(this).val());
	});
});

function importChargingRuleBaseName(){
	if($("#uploadedFile").val().length == 0){
		addWarning(".popup","At least configure file for Import");
		return false;
	}
	document.forms["chargingRuleBaseNameSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/importChargingRuleBaseName";
	document.forms["chargingRuleBaseNameSearchResult"].submit();
}
/*IMPORT EXPORT java script code End*/
</script>

<s:form  id="chargingRuleBaseNameSearchResult" method="post"  enctype="multipart/form-data" cssClass="form-vertical">
	<s:hidden value="%{#session.staffBelongingGroupIds}" name="groupIds"/>
	<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<s:text name="chargingrulebasename.search" />
			</h3>
		</div>

		<div class="panel-body">
			<div class="row">
				<div class="col-xs-5">
					<span class="btn-group btn-group-sm">
						<button class="btn btn-default" role="button" onclick="createChargingRuleBaseName();" >
							<span class="glyphicon glyphicon-plus" title="Add"></span>
						</button>

						<span class="btn-group btn-group-sm" onclick="return removeRecords(this);" onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
							<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
								<span class="glyphicon glyphicon-trash" title="delete"></span>
							</button>
						</span>

						<!-- IMPORT EXPORT BUTTON GROUP- BEGIN-->
						<span class="btn-group btn-group-sm"  id="btnImportGroup">
							<button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse" data-target="#import" aria-expanded="false" aria-controls="import" data-placement="right" title="import" role="button">
								<span class="glyphicon glyphicon-import" title="Import"></span>
							</button>
						</span>
						<span class="btn-group btn-group-sm"  id="btnExportPkg" data-toggle="confirmation-singleton" onclick="return exportChargingRuleBaseName(this);" onmousedown="return exportChargingRuleBaseName(this);" data-href="javascript:exportData();">
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
						<!-- IMPORT EXPORT BUTTON GROUP- END-->
					</span>
				</div>
				<div class="col-xs-7 collapse" id="import">
					<div class="input-append form-group">
						<div class="col-xs-10" >
							<div class="input-group">
								<input id="uploadedFile" class="form-control" type="text" title="upload file">
								<a class="input-group-addon" onclick="$('input[id=importChargingRuleBaseNames]').click();" style="text-decoration:none;cursor : pointer;">Browse</a>
							</div>
						</div>
						<div class="col-xs-2">
							<button type="button" class="btn btn-sm btn-primary" role="button" onclick="return importChargingRuleBaseName()" >Import</button>
						</div>
					</div>
					<s:file id="importChargingRuleBaseNames" type="file" name="importedFile" cssStyle="display:none;"/>
					<s:hidden name="importAction" id="userAction" />
				</div>

			</div>

			<nv:dataTable
				id="chargingRuleBaseNameData"
				beanType="com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData"
				actionUrl="/genericSearch/policydesigner/chargingrulebasename/ChargingRuleBaseName/searchData"
				rows="<%=rows%>"
				showPagination="true"
				cssClass="table table-blue"
				width="100%">

				<nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
				<nv:dataTableColumn title="Name"	beanProperty="name"	hrefurl="${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/view?chargingRuleBaseNameId=id" sortable="true" style="width:200px;"/>
				<nv:dataTableColumn title="Description"			beanProperty="description" />
				<nv:dataTableColumn icon="<span class='glyphicon glyphicon-pencil'></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/init?chargingRuleBaseNameId=id&groupIds=groups" style="width:20px;border-right:0px;"  />
				<nv:dataTableColumn icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/delete?ids=id" 	style="width:20px;"  />

			</nv:dataTable>
		</div>
	</div>
</s:form>