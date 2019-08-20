<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 31/10/17
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="region.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/region" action="region" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()" id="regionform">
            <s:token/>
            <div class="col-xs-12 col-sm-6">
                <s:select list="countryDataList" id="countryId" name="countryId" key="region.country" listKey="id" listValue="name" cssClass="form-control select2" headerKey="" headerValue="Select"  />
                <s:hidden name="name" value="name" />
            </div>

            <div id="AddRegionName">
                <div class="col-xs-12 col-sm-12">
                    <table id="RegionNameTable"  class="table table-blue table-bordered">
                        <caption class="caption-header">
                            <s:text name="region.name.add" />
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn" onclick="addRegionName();" id="addRegionNameRow"> <span class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th><s:text name="region.name"/></th>
                        <th style="width:35px;">&nbsp;</th>
                        </thead>
                    </table>
                </div>
            </div>


            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/region/region'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.cancel"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">
    var i = 0;

    function addRegionName(){
        var tableRow= "<tr name='FieldMappingRow'>"+
            "<td><input maxlength='100' class='form-control verifyName' id='region-name-id-"+i+"' name='regionNameList["+i+"]' type='text' onblur='checkDuplicateRegion()' /></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
        $(tableRow).appendTo('#RegionNameTable');
        $("#RegionNameTable").find("tr:last td:nth-child(1)").find("input").focus(); // focus on first element
        i++;
    }


    function checkDuplicateRegion(){
        clearErrorMessages();
        var regionField = $(".verifyName");
        var countryId = $("#countryId").val();
        var isValidName = true;
        var nameArray = new Array();
        $(".verifyName").each(function () {
            var id = $(this).attr('id');
            var value = $(this).val();
            if(isNullOrEmpty(value)) {
                setError(id, "Name is Required");
                isValidName = false
                return isValidName;
            }
            if(nameArray.indexOf(value) == -1 ) {
                nameArray.push(value);
            } else {
                setError(id, "name already exist");
                isValidName = false;
                return isValidName;
            }
            isValidName = verifyUniquenessOnSubmit(id,'create','','com.elitecore.corenetvertex.sm.location.region.RegionData',countryId,'');
            return isValidName;
        });
        return isValidName;
    }


    $(function(){
        $( ".select2" ).select2();
    });
    
    function validateForm() {
        return checkDuplicateRegion();
    }

</script>

