<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="ratecardgroup.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/ratecardgroup" id="rateCardGroupFormId" action="rate-card-group" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <s:hidden name="rncPackageId"    value="%{rncPackageId}" />
            <div>
                <div class="row">
                    <div class="col-xs-12 col-sm-6">
                        <s:textfield name="name" key="ratecardgroup.name" id="rateCardGroupName" cssClass="form-control focusElement" tabindex="1" maxlength="100"/>
                        <s:textarea name="description" id="description" key="ratecardgroup.description" cssClass="form-control" tabindex="2" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}" maxlength="2000"/>
                        <s:textarea name="advanceCondition" 	key="ratecardgroup.advancecondition"  cssClass="form-control" id="advanceCondition" tabindex="3" />
                   </div>
                </div>

                <div class="row">
                    <fieldset class="fieldSet-line">
                        <legend> <s:text name="ratecardgroup.peak.rate"/> </legend>
                        <div class="row">
                            <div class="col-xs-12 col-sm-12 col-lg-12">
                                <div class="row">
                                    <div class="col-xs-6 col-sm-6">
                                        <s:select name="peakRateRateCardId" key="ratecardgroup.peakRateRateCard"  list="{}" listValue="name" listKey="id"  id="peakRateCardId" cssClass="form-control select2" headerValue="SELECT" headerKey="" tabindex="4">
                                            <s:optgroup label="Monetary Rate Cards" list="monetaryRateCardDataList" listValue="name" listKey="id"/>
                                            <s:optgroup label="Global Monetary Rate Cards" list="globalMonetaryRateCardDataList" listValue="name" listKey="id"/>
                                            <s:optgroup label="Non-Monetary Rate Cards" list="nonMonetaryRateCardDataList" listValue="name" listKey="id" />
                                        </s:select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>

                <div class="row">
                    <fieldset class="fieldSet-line">
                        <legend> <s:text name="ratecardgroup.offPeak.rate"/> </legend>
                        <div class="row">
                            <div class="col-xs-12 col-sm-12 col-lg-12">
                                <div class="row">
                                    <div class="col-xs-6 col-sm-6">
                                        <s:select name="offPeakRateRateCardId" key="ratecardgroup.offPeakRateRateCard"  list="{}" id="offPeakRateCardId" cssClass="form-control select2" tabindex="4" headerValue="SELECT" headerKey="">
                                            <s:optgroup label="Monetary Rate Cards" list="monetaryRateCardDataList" listValue="name" listKey="id" />
                                            <s:optgroup label="Global Monetary Rate Cards" list="globalMonetaryRateCardDataList" listValue="name" listKey="id"/>
                                            <s:optgroup label="Non-Monetary Rate Cards" list="nonMonetaryRateCardDataList" listValue="name" listKey="id" />
                                        </s:select>
                                    </div>
                                </div>

                                <div class="row">
                                    <div id="timeBaseConditionDiv">
                                        <div class="col-xs-12">
                                            <table id='timeBaseConditionTable'  class="table table-blue table-bordered">
                                                <caption class="caption-header">
                                                    <span class="glyphicon glyphicon-check"></span>
                                                    <s:text name="timeperiod.restricions"/>
                                                    <div align="right" class="display-btn">
                                                        <span class="btn btn-group btn-group-xs defaultBtn" onclick="addTimeBasedCondition('timeBaseConditionTable','timeSlotRelationData');" id="addRow">
                                                            <span class="glyphicon glyphicon-plus"></span>
                                                        </span>
                                                    </div>
                                                </caption>
                                                <thead>
                                                <th><s:text name="timeslotrelation.dayOfWeek"/></th>
                                                <th><s:text name="timeslotrelation.timePeriod"/></th>
                                                <th style="width:35px;">&nbsp;</th>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </div>
                        <div class="col-xs-12" id="generalError"></div>
                    </fieldset>
                </div>

                 </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <s:submit  cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="12">
                            <span class="glyphicon glyphicon-floppy-disk"></span>
                            <s:text name="button.save"/>
                        </s:submit>
                        <button type="button"  class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${rncPackageId}'" tabindex="13">
                            <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.cancel" />
                        </button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>
<script type="text/javascript">

function validateForm() {
	return verifyUniquenessOnSubmit('rateCardGroupName','create','','com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData','${rncPackageId}','') && validateTimePeriod();
}

function validateTimePeriod(){
    var validateFlag = true;
    if($('#offPeakRateCardId :selected').val().length != 0){
        if($('#timeBaseConditionTable > tbody > tr').length < 1){
            $("#generalError").addClass("bg-danger");
            $('#generalError').text('<s:text name="error.rate.card.group.timeslotmapping" />');
            validateFlag = false;
        }else{
            $($('#timeBaseConditionTable > tbody > tr')).find('input').each(function(){
                if(!isNullOrEmpty($(this).val())){
                    validateFlag =  true;
                }else{
                    validateFlag = false;
                }
                if(validateFlag){
                    return false;
                }
            });
            if(!validateFlag){
                $("#generalError").addClass("bg-danger");
                $('#generalError').text('<s:text name="error.rate.card.group.timeslotmapping" />');
            }
        }
    }else{
        $("#generalError").removeClass("bg-danger");
        $("#generalError").text("");
    }
    return validateFlag;
}

$(function(){
    $("#advanceCondition").autocomplete();
    var advanceConditionsAutoCompleter = createModel(${advanceConditionAsJson});
    expresssionAutoComplete('advanceCondition',advanceConditionsAutoCompleter);
    $('.select2').select2();
});

var i=0;
function addTimeBasedCondition(tableId, listName){
    createChildTable(tableId, listName, i);
}

</script>
<%@include file="/WEB-INF/content/pd/ratecardgroup/RateCardGroupValidations.jsp"%>