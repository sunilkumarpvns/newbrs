<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 7/11/17
  Time: 3:44 PM
  To change this template use File | Settings | File Templates.
--%>
<style type="text/css">
    .select2-container{
        width: 100% !important;
    }

</style>
<div class="modal col-xs-12" id="cityDialogId" tabindex="-1" role="dialog" aria-labelledby="cityDialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"/>
                <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title set-title" >
                    <s:text name="city.update" />
                </h4>
            </div>
            <s:form namespace="/sm/city" action="city" method="post" cssClass="form-horizontal" validate="true" validator="validateForm()" id="cityform">
                <s:hidden name="_method" value="put" />
                <s:token/>
                <div class="modal-body">
                    <s:token />
                    <div class="row">
                        <div class="col-xs-12 col-sm-12">
                            <s:hidden name="id" id="cityId" />
                            <s:hidden id="countryId" name="countryId" />
                            <s:select id="regionId" list="{}" name="regionId" key="city.region" listKey="id" listValue="name" cssClass="form-control select2 focusElement" tabindex="1" />
                            <s:textfield maxlength="100" name="name" key="city.name" id="cityName" cssClass="form-control" onblur="validateName()" tabindex="2" />
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

    $(function(){
        $( ".select2" ).select2();
    });

    var cityIdToUpdate = "";
    function updateCity(id){
        clearErrorMessages();
        for(var i in data) {
            cityIdToUpdate = data[i].id;
            if (cityIdToUpdate == id) {
                cityIdToUpdate = cityIdToUpdate;
                $("#cityId").val(cityIdToUpdate);
                $("#countryId").val(data[i].regionData.countryData.id);
                $("#cityName").val(data[i].name);
                var countryIdJS = $("#countryId").val();
                $("#regionId").find('option').remove();

                var regionData = ${regionDataList};
                var optionsAsString = "";
                for(var j in regionData) {
                    if(countryIdJS == regionData[j].countryData.id) {
                        if(data[i].regionData.id == regionData[j].id ) {
                            optionsAsString += "<option selected ='selected' value='" + regionData[j].id + "'>" + regionData[j].name + "</option>";
                        } else {
                            optionsAsString += "<option value='" + regionData[j].id + "'>" + regionData[j].name + "</option>";
                        }
                    }
                }
                $("#regionId").append(optionsAsString);
                $( ".select2" ).select2();

                break;
            }
        }


        $("#cityDialogId").modal('show');
    }


    function validateForm() {
        var regionId = $("#regionId").val();
        var isValidName = verifyUniquenessOnSubmit('cityName','update',cityIdToUpdate,'com.elitecore.corenetvertex.sm.location.city.CityData',regionId,'');
        if(isValidName == false) {
            return false;
        }
        document.cityform.action = "${pageContext.request.contextPath}/sm/city/city/"+cityIdToUpdate;
        return true;
    }
    function validateName() {
        var regionId = $("#regionId").val();
        return verifyUniquenessOnSubmit('cityName','update',cityIdToUpdate,'com.elitecore.corenetvertex.sm.location.city.CityData',regionId,'');
    }
</script>

