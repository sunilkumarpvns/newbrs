<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 6/11/17
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="city.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/city" action="city" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()" id="cityform">
            <s:token/>
            <div class="col-xs-12 col-sm-6">
                <s:select id="countryId" name="countryId" list="countryDataList" key="city.country" listKey="id" listValue="name" cssClass="form-control select2" onchange="onCountryChange()" tabindex="1" />
                <s:select id="regionId" list="{}" name="regionId" key="city.region" listKey="id" listValue="name" cssClass="form-control select2" tabindex="2" />
                <s:hidden name="name" value="name" />
            </div>

            <div id="AddCityName">
                <div class="col-xs-12 col-sm-12">
                    <table id="CityNameTable"  class="table table-blue table-bordered">
                        <caption class="caption-header">
                            <s:text name="city.name.add" />
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn" onclick="addCityName();"> <span class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th><s:text name="city.name"/></th>
                        <th style="width:35px;">&nbsp;</th>
                        </thead>
                    </table>
                </div>
            </div>


            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/city/city'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.cancel"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">

    var i = 0;
    function addCityName(){
        var tableRow= "<tr name='CityName'>"+
            "<td><input maxlength='100' class='form-control verifyName' id='city-name-id-"+i+"' name='cityNameList["+i+"]' type='text' onblur='checkDuplicateCity()' /></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
        $(tableRow).appendTo('#CityNameTable');
        $("#CityNameTable").find("tr:last td:nth-child(1)").find("input").focus(); // focus on first element
        i++;
    }

    $(function(){
        $( ".select2" ).select2();
        onCountryChange();
    });

    function onCountryChange(){
        var countryIdJS = $("#countryId").val();
        $("#regionId").find('option').remove();

        var data = ${regionDataList};

        var optionsAsString = "";
        for(var i in data) {
            if(countryIdJS == data[i].countryData.id) {
                optionsAsString += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
            }
        }
        $("#regionId").append(optionsAsString);
        $( ".select2" ).select2();//need to recreate


    }

    function checkDuplicateCity(){
        clearErrorMessages();
        var regionField = $(".verifyName");
        var regionId = $("#regionId").val();
        var isValidName = true;
        var nameArray = new Array();
        $(".verifyName").each(function () {
            var id = $(this).attr('id');
            var value = $(this).val();
            if(isNullOrEmpty(value)) {
                setError(id, "Name is Required");
                return isValidName = false;
            }
            if(nameArray.indexOf(value) == -1 ) {
                nameArray.push(value);
            } else {
                setError(id, "name already exist");
                isValidName = false;
                return isValidName;
            }
            isValidName = verifyUniquenessOnSubmit(id,'create','','com.elitecore.corenetvertex.sm.location.city.CityData',regionId,'');
            return isValidName;

        });
        return isValidName;
    }

    function validateForm() {
        return checkDuplicateCity();
    }

</script>