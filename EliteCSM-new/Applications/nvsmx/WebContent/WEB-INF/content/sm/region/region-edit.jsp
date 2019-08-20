<%@ taglib prefix="s" uri="/struts-tags/ec" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 1/11/17
  Time: 12:21 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="modal col-xs-12" id="regionDialogId" tabindex="-1" role="dialog" aria-labelledby="regionDialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"/>
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title set-title" >
                    <s:text name="region.update" />
                </h4>
            </div>
            <s:form namespace="/sm/region" action="region" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-3" elementCssClass="col-xs-12 col-sm-9" validator="validateForm()" id="regionform">
                <s:hidden name="_method" value="put" />
                <s:token/>
                <div class="modal-body">
                    <s:token />
                    <div class="row">
                        <div class="col-xs-12 col-sm-12">
                            <s:hidden name="id" id="regionId" />
                            <s:hidden name="countryData.id" id="countryId" />
                            <s:textfield maxlength="100" name="name" key="region.name" id="regionName" cssClass="form-control focusElement" onblur="validateName()"  />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary btn-sm"  ><span class="glyphicon glyphicon-floppy-disk" ></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel"><s:text name="button.cancel" /></button>
                </div>
            </s:form>
        </div>
    </div>
</div>

<script>

    var regionIdToUpdate = "";
    function updateRegionName(id){
        clearErrorMessages();
        for(var i in data) {
            regionIdToUpdate = data[i].id;
            if (regionIdToUpdate == id) {
                regionIdToUpdate = regionIdToUpdate;
                $("#regionId").val(regionIdToUpdate);
                $("#countryId").val(data[i].countryData.id);
                $("#regionName").val(data[i].name);
                break;
            }
        }
        $("#regionDialogId").modal('show');
    }


    function validateForm() {
        var countryId = $("#countryId").val();
        var isValidName = verifyUniquenessOnSubmit('regionName','update',regionIdToUpdate,'com.elitecore.corenetvertex.sm.location.region.RegionData',countryId,'');
        if(isValidName == false) {
            return false;
        }
        document.regionform.action = "${pageContext.request.contextPath}/sm/region/region/"+regionIdToUpdate;
        return true;
    }

    function validateName() {
        var countryId = $("#countryId").val();
        return verifyUniquenessOnSubmit('regionName','update',regionIdToUpdate,'com.elitecore.corenetvertex.sm.location.region.RegionData',countryId,'');
    }

</script>
