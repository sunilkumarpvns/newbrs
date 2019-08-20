<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<style type="text/css">
    
    .label-bold{
    	font-weight: bold;
    	margin-bottom: 0 !important;
    }
</style>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="filelocation.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/filelocation" action="file-location" id="fileLocationCreateFormId" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-sm-12 col-lg-6">
                    <s:textfield name="name" key="filelocation.name" id="fileLocationName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
                    <s:textarea name="description" key="filelocation.description" id="description" cssClass="form-control" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}" maxlength="2000" tabindex="2"/>
                    <s:select name="ratingType" key="filelocation.ratingtype" id="ratingType" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.RatingType@values()" listKey="name()" listValue="getValue()" tabindex="3"/>
                    <s:select name="sortingType"  key="filelocation.sortingtype" id="sortingType" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.SortingType@values()" listKey="name()" listValue="getValue()" tabindex="4" />
                    <s:select name="sortingCriteria" key="filelocation.sortingcriteria" id="sortingCriteria" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.SortingCriteria@values()" listKey="name()" listValue="getValue()" tabindex="5" />
                </div>
                 <div class="col-sm-12 col-lg-6">
                    <s:select  name="fileMappingId" key="filelocation.fileMapping" id = "fileMappingId"  list="fileMappingDataList" listKey="getId()" listValue="getName()" cssClass="form-control" tabindex="6"/>
                    <s:textfield name="inputPath" key="filelocation.inputpath" id="inputPath" cssClass="form-control"  maxlength="200" tabindex="7"/>
                    <s:textfield name="errorPath" key="filelocation.errorpath" id="errorPath" cssClass="form-control" maxlength="200" tabindex="8"/>
                    <s:textfield name="archivePath" key="filelocation.archivepath" id="archivePath" cssClass="form-control" maxlength="200" tabindex="9"/>
                 </div>
                 
                
	                <fieldset class="fieldSet-line">
	                    <legend> <s:text name="filelocation.output.config"/> </legend>
	                     <div class="row">
			                 <div id="fileOutputConfigurationDiv">
			                    <div class="col-xs-12 col-sm-12">
			                        <table id='fileOutputConfigTable'  class="table table-blue table-bordered">
			                            <caption class="caption-header">
			                            <s:text name="filelocation.output" />
			                                <div align="right" class="display-btn">
			                                    <span class="btn btn-group btn-group-xs defaultBtn" onclick="addFileOutputConfigRow();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
			                                </div>
			                            </caption>
			                            <thead>
				                            <th><s:text name="filelocation.mapping"/></th>
				                            <th><s:text name="filelocation.extension"/></th>
				                            <th><s:text name="filelocation.field.separator"/></th>
				                            <th><s:text name="filelocation.location"/></th>
			                            <th style="width:35px;">&nbsp;</th>                           
			                            </thead>
			                            
			                        </table>
			                        <div class="col-xs-12" id="generalError"></div>
			                    </div>
			                </div>
		                </div>
                	</fieldset>
                
	                <fieldset class="fieldSet-line">
	                    <legend> <s:text name="filelocation.database.config"/> </legend>
	                    <div class="row">
		                    <div class="col-sm-12 col-lg-6">
	                    		<s:select name="databaseId" key="filelocation.db.datasource" id = "dbDataSourceId"  list="dbDataSourceList" listKey="getId()" listValue="getName()" cssClass="form-control" tabindex="6"/>
	                    	</div>
	                    	<div class="col-sm-12 col-lg-6">
	                    		<s:textfield name="tableName" key="filelocation.table.name" id="tableNameId" cssClass="form-control"  maxlength="200" tabindex="7"/>
	                    	</div>
	                    
			                 <div id="columnMappingDiv">
			                    <div class="col-xs-12 col-sm-12">
			                        <table id='columnMappingTable'  class="table table-blue table-bordered">
			                            <caption class="caption-header">
			                            <s:text name="filelocation.column.mapping" />
			                                <div align="right" class="display-btn">
			                                    <span class="btn btn-group btn-group-xs defaultBtn" onclick="addColumnMappingRow();" id="addRow"> <span class="glyphicon glyphicon-plus"></span>
			                                </div>
			                            </caption>
			                            <thead>
				                            <th><s:text name="filelocation.source.key"/></th>
				                            <th><s:text name="filelocation.column.name"/></th>
				                            <th><s:text name="filelocation.value.mapping"/></th>
				                            <th><s:text name="filelocation.default.value"/></th>
				                            <th style="width:35px;">&nbsp;</th>                           
			                            </thead>
			                        </table>
			                        <div class="col-xs-12" id="columnMappingGeneralError"></div>
			                    </div>
			                </div>
		                </div>
                	</fieldset>
                 
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <s:submit type="button" cssClass="btn  btn-sm btn-primary" id="btnSave" role="button" tabindex="11"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/filelocation/file-location'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
        	</div>
        </s:form>
    </div>
</div>

<table id="tableTemplateFileOutputConfiguration" style="display: none">
        <tr name="fileOutputConfiguration">
        	 <td style="border: 0px !important;"><s:select  id = "mapping" headerValue="Select File Mapping" list="fileMappingList" listKey="id" listValue="name" cssClass="form-control mapping" tabindex="10"/></td>
             <td><s:textfield  id="extension" value=".csv" cssClass="form-control extension" elementCssClass="col-xs-12" maxlength="50" tabindex="11"/></td>
             <td><s:textfield  id="separator" value="," cssClass="form-control separator"  elementCssClass="col-xs-12" maxlength="50" tabindex="12"/></td>
             <td><s:textfield  id="location" value="rated/CDR" cssClass="form-control location"  elementCssClass="col-xs-12" maxlength="50" tabindex="13"/></td>
             <td><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
         </tr>
</table>

<table id="tableTemplateColumnMapping" style="display: none">
        <tr name="columnMapping">
             <td><s:textfield  id="sourcekey" cssClass="form-control sourcekey" elementCssClass="col-xs-12" maxlength="50" tabindex="14"/></td>
             <td><s:textfield  id="columnName" cssClass="form-control columnName" elementCssClass="col-xs-12" maxlength="50" tabindex="15"/></td>
             <td><s:textfield  id="valueMapping" cssClass="form-control valueMapping"  elementCssClass="col-xs-12" maxlength="50" tabindex="16"/></td>
             <td><s:textfield  id="defaultValue" cssClass="form-control defaultValue"  elementCssClass="col-xs-12" maxlength="50" tabindex="17"/></td>
             <td><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
         </tr>
</table>

 <script type="text/javascript">

 
 function validateForm(){
	 clearErrorMessages();

	  var fileOutputConfigTableLength = $("#fileOutputConfigTable tbody tr").length;
	  var columnMappingTableLength = $("#columnMappingTable tbody tr").length;
		
	  var isValidName = verifyUniquenessOnSubmit('fileLocationName','create','','com.elitecore.corenetvertex.sm.filelocation.FileLocationData','','');        
 	
        if (isValidName == false){
    	  return false;
        }
        var check = true;
        
        if (fileOutputConfigTableLength < 1) {
			$("#generalError").addClass("bg-danger");
			$("#generalError").text("<s:text name='error.filelocation.fileoutputconfig'/>");
			check =  false;
		}
        if (fileOutputConfigTableLength >= 1) {
        	check =  validateFileOutputConfigChildTable();
		}
		if (columnMappingTableLength < 1) {
			$("#columnMappingGeneralError").addClass("bg-danger");
			$("#columnMappingGeneralError").text("<s:text name='error.filelocation.columnmapping'/>");
			check =  false;
		}
		if (columnMappingTableLength >= 1) {
			check =  validateChildTable();
		}
		
		return check;
	 }
 
var i= 0
 
 function addFileOutputConfigRow(){
      var tableRow = $('#tableTemplateFileOutputConfiguration').html();
     
     $(tableRow).appendTo('#fileOutputConfigTable');
     
     $("#fileOutputConfigTable").find("tr:last td:nth-child(1)").find("select").attr("name",'fileOutputConfigurationData['+i+'].fileMappingId');
     $("#fileOutputConfigTable").find("tr:last td:nth-child(2)").find("input").attr("name",'fileOutputConfigurationData['+i+'].extension');
     $("#fileOutputConfigTable").find("tr:last td:nth-child(3)").find("input").attr("name",'fileOutputConfigurationData['+i+'].fieldSeparator');
     $("#fileOutputConfigTable").find("tr:last td:nth-child(4)").find("input").attr("name",'fileOutputConfigurationData['+i+'].location');
     
  i++; 
 }
 
var j= 0

 function addColumnMappingRow(){
	
     var tableRow = $('#tableTemplateColumnMapping').html();
     
     $(tableRow).appendTo('#columnMappingTable');
     
     $("#columnMappingTable").find("tr:last td:nth-child(1)").find("input").attr("name",'columnMappingData['+j+'].sourceKey');
     
     var sourceKeyChild = $("#columnMappingTable").find("tr:last td:nth-child(1)").find("input");
     sourceKeyChild[0].attributes.getNamedItem("id").value = "sourceKey"+j;
     
     $("#columnMappingTable").find("tr:last td:nth-child(2)").find("input").attr("name",'columnMappingData['+j+'].columnName');
     $("#columnMappingTable").find("tr:last td:nth-child(3)").find("input").attr("name",'columnMappingData['+j+'].valueMapping');
     $("#columnMappingTable").find("tr:last td:nth-child(4)").find("input").attr("name",'columnMappingData['+j+'].defaultValue');
     
     autoCompleteForFileMapping("sourceKey"+j);
  j++;
 }

function autoCompleteForFileMapping(id){
	$('#'+id).autocomplete();
	var list = [ <s:iterator value="@com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants@values()" >
	'<s:property value="name"/>',
	</s:iterator> ];
	commonAutoComplete(id,list);
};

 function validateFileOutputConfigChildTable(){
	var isValidMappingValue = true;
	$('#fileOutputConfigTable tbody tr').each(
			function() {
				var mapping = $(this).children().find('.mapping');
				if (isNullOrEmpty(mapping.val())) {
					setErrorOnElement(mapping,"<s:text name='error.filelocation.mapping'/>");
					isValidMappingValue = false;
				}
			});
	if (isValidMappingValue == false) {
		return false;
	} else {
		return true;
	}
 }
	
	function validateChildTable() {
		var isColumnMappingValue = true;
		$('#columnMappingTable tbody tr').each(
				function() {
					var sourceKey = $(this).children().find('.sourcekey');
					var columnName = $(this).children().find('.columnName');
					if (isNullOrEmpty(sourceKey.val())) {
						setErrorOnElement(sourceKey,"<s:text name='error.filelocation.sourcekey'/>");
						isColumnMappingValue = false;
					}
					if (isNullOrEmpty(columnName.val())) {
						setErrorOnElement(columnName,"<s:text name='error.filelocation.columnname'/>");
						isColumnMappingValue = false;
					}
				});
		if (isColumnMappingValue == false) {
			return false;
		} else {
			return true;
		}
	}

 	
</script> 