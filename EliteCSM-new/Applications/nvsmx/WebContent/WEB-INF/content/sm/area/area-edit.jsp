<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 8/11/17
  Time: 12:03 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="area.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/area" action="area" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()" id="areaForm">
            <s:hidden name="_method" value="put" />
            <s:token/>

            <div class="col-xs-12 col-sm-6">
                <s:textfield name="name" key="area.name" id="areaName" cssClass="form-control focusElement" onblur="checkDuplicateArea();" maxlength="100" />
                <s:select id="countryId" name="countryId" value="cityData.regionData.countryData.id" list="countryDataList" key="area.country" listKey="id" listValue="name" cssClass="form-control select2" onchange="onCountryChange()" />
                <s:select id="regionId" name="regionId" value="cityData.regionData.id" list="{}" key="area.region" listKey="id" listValue="name" cssClass="form-control select2" onchange="onRegionChange()"  />
                <s:select id="cityId" name="cityId" list="{}" key="area.city" listKey="id" listValue="name" cssClass="form-control select2"  />

            </div>
            <div class="col-xs-12 col-sm-6">
                <s:select id="networkId" name="networkId" list="networkDataList" key="area.network" listKey="id" listValue="name" cssClass="form-control select2"  />
                <s:textfield name="param1" key="area.param1" cssClass="form-control" maxlength="256"  />
                <s:textfield name="param2" key="area.param2" cssClass="form-control" maxlength="256" />
                <s:textfield name="param3" key="area.param3" cssClass="form-control" maxlength="256" />
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="area.wifi.information"/> </legend>
                    <div class="col-xs-12 col-sm-6">
                        <s:textarea rows="2" name="calledStations" key="area.called.stations"   cssClass="form-control" maxlength='4000'  />
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <s:textarea rows="2" name="ssids"          key="area.ssids"             cssClass="form-control" maxlength='4000' />
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="area.lac.information"/> </legend>
                    <div id="addLacInformation">
                        <div class="col-xs-12 col-sm-12">
                            <table id="LacInformationTable"  class="table table-blue table-bordered">
                                <caption class="caption-header">
                                    <s:text name="area.lac.information" />
                                    <div align="right" class="display-btn">
                                        <span class="btn btn-group btn-group-xs defaultBtn" id="addLacInformationSpan"> <span class="glyphicon glyphicon-plus"></span></span>
                                    </div>
                                </caption>
                                <thead>
                                    <th><s:text name="area.lac.lac"/></th>
                                    <th><s:text name="area.lac.ci"/></th>
                                    <th><s:text name="area.lac.sac"/></th>
                                    <th><s:text name="area.lac.rac"/></th>
                                    <th style="width:35px;">&nbsp;</th>
                                </thead>
                                <tbody>
                                <s:iterator value="lacInformationDataList" status="i" var="lacInformationData">
                                <tr name='LacInformationRow'>
                                    <td><s:textarea  maxlength="6" rows="2" value="%{#lacInformationData.lac}"	name="lacInformationDataList[%{#i.count - 1}].lac" cssClass="form-control"  elementCssClass="col-xs-12" type="number" onkeypress="return isNaturalInteger(event);" /></td>
                                    <td><s:textarea rows="2" value="%{#lacInformationData.ciList}"	name="lacInformationDataList[%{#i.count - 1}].ciList" cssClass="form-control"  elementCssClass="col-xs-12" maxlength="4000" /></td>
                                    <td><s:textarea rows="2" value="%{#lacInformationData.sacList}"	name="lacInformationDataList[%{#i.count - 1}].sacList" cssClass="form-control"  elementCssClass="col-xs-12" maxlength="4000" /></td>
                                    <td><s:textarea rows="2" value="%{#lacInformationData.racList}"	name="lacInformationDataList[%{#i.count - 1}].racList" cssClass="form-control"  elementCssClass="col-xs-12" maxlength="4000"/></td>
                                    <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                </tr>
                                </s:iterator>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </fieldset>
            </div>


            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/area/area/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/area/area/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script>

    function checkDuplicateArea() {
        var cityId = $("#cityId").val();
        return verifyUniquenessOnSubmit('areaName','update','<s:property value="id"/>','com.elitecore.corenetvertex.sm.location.area.AreaData',cityId,'');
    }

    function validateForm() {
        var isValidName = checkDuplicateArea();

        if(isValidName == false){
            return false;
        }
        return isValidateLacInformation();
    }

    $(function () {
        $(".test").css('display','none');
    })
</script>

<%@include file="area-utility.jsp"%>
