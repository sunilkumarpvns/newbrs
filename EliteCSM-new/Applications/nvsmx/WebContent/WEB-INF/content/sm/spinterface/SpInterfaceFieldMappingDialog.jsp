<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 12/9/17
  Time: 7:27 PM
  To change this template use File | Settings | File Templates.
--%>
<style>
    .ui-autocomplete
    {
        z-index: 99999;/*required if model contains autocompleter field*/
    }
</style>
<div class="modal col-xs-12" id="spInterfaceFieldMappingDialogId" tabindex="-1" role="dialog" aria-labelledby="spInterfaceFieldMappingDialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="clearModel()" >
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title set-title">
                    <s:text name="sp.interface.field.mapping.add"/>
                </h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-12 col-sm-12">
                        <s:select key="sp.interface.field.mapping.logicalname" list="@com.elitecore.corenetvertex.spr.data.SPRFields@values()" listKey="name()" listValue="displayName" cssClass="form-control focusElement" labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8" id="tempLogicalNameId" cssStyle="margin-bottom:10px" headerKey="" headerValue="SELECT" />
                        <s:textfield key="sp.interface.field.mapping.fieldname" name="tempFieldName"  labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8" cssClass="form-control" id="tempFieldNameId" cssStyle="margin-bottom:10px" />
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-sm btn-primary" type="button" onclick="addSpInterfaceFieldMapping()"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.add"/></button>
                <button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel" onclick="clearModel()" ><s:text name="button.cancel" /></button>
            </div>
        </div>
    </div>
</div>

<script>
    var i = $("#fieldMappingTable tbody tr").length;
    function addSpInterfaceFieldMapping() {
        clearErrorMessages();
        var logicalName = $("#tempLogicalNameId").val();
        var logicalDisplayValue = $("#tempLogicalNameId option:selected").text();

        var fieldName = $("#tempFieldNameId").val();

        if(isNullOrEmpty(logicalName)) {
            setError('tempLogicalNameId',"Logical Name is Required");
            return;
        } else if(isNullOrEmpty(fieldName)) {
            setError('tempFieldNameId',"Field Name is Required");
            return;
        }

        if(checkDuplicateFieldMapping(logicalName)){
            setError('tempLogicalNameId',"Already Exist");
            return;
        }

        var addTr = "";
        if (spInterfaceType == '<%=SpInterfaceType.DB_SP_INTERFACE.name()%>') {
            addTr = "<tr name='FieldMappingRow'><td>"+logicalDisplayValue+"<input type='hidden' id='logicalName["+i+"]' name='dbSpInterfaceData.spInterfaceFieldMappingDatas[" +i+"].logicalName' value="+logicalName+" /></td><td>"+fieldName+"<input type='hidden' id='fieldName["+i+"]' name='dbSpInterfaceData.spInterfaceFieldMappingDatas[" +i+"].fieldName' value="+fieldName+" /></td><td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td></tr>"
        } else {
            addTr = "<tr name='FieldMappingRow'><td>"+logicalDisplayValue+"<input type='hidden' id='logicalName["+i+"]' name='ldapSpInterfaceData.spInterfaceFieldMappingDatas[" +i+"].logicalName' value="+logicalName+" /></td><td>"+fieldName+"<input type='hidden' id='fieldName["+i+"]' name='ldapSpInterfaceData.spInterfaceFieldMappingDatas[" +i+"].fieldName' value="+fieldName+" /></td><td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td></tr>"
        }
        $("#fieldMappingTable tbody").append(addTr);
        i++;
        clearModel();
        $("#spInterfaceFieldMappingDialogId").modal('hide');
    }
    
    function clearModel() {
        clearErrorMessages();
        $("#tempLogicalNameId").val('');
        $("#tempFieldNameId").val('');
    }

    function checkDuplicateFieldMapping(logicalName){
        var isMappingAlreadyExist = false;
        $("#fieldMappingTable tbody tr").each(function () {
            var value = $(this).children().first().find('input').val();
            if(value == logicalName) {
                isMappingAlreadyExist = true;
                return false;
            }
        });
        return isMappingAlreadyExist;
    }
</script>