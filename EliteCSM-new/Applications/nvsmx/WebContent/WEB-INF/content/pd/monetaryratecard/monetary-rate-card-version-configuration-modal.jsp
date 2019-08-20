<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<style type="text/css">
    .dataTable-button{
        display: none;
    }
</style>
<s:form namespace="/pd/monetaryratecard" action="monetary-rate-card" id="bulkupdate" method="post" cssClass="form-horizontal"
        labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" onSubmit="return validateForm();">
    <s:hidden name="_method" value="put"/>
    <s:token/>
    <div class="modal col-xs-12" id="monetaryRateCardVersionConfigurationBulkUpdate" tabindex="-1" role="dialog"
         aria-labelledby="monetaryRateCardVersionConfigurationBulkUpdate" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title set-title">
                        <s:text name="ratecard.version.bulk.update"/>
                    </h4>
                </div>
                <div class="modal-body" style="overflow:auto;max-height: 500px;">
                    <div class="row">
                        <div class="col-xs-6 col-sm-6">
                            <div class="form-group">
                                <s:textfield name="bulkUpdateRate" key="ratecard.version.rate" id="bulkUpdateRate"
                                             cssClass="form-control" maxlength="21"
                                             />
                            </div>
                        </div>
                        <div class="col-xs-6 col-sm-6">
                            <div class="form-group">
                                <s:select name="rateUnitOperation" key="ratecard.rate.unit.operation"
                                          id="rateUnitOperation" list="@com.elitecore.corenetvertex.pkg.constants.OperationType@values()" listValue="getDisplayValue()"
                                          value="1"/>
                            </div>
                        </div>

                        <div class="col-sm-12">
                            <nv:dataTable
                                    id="monetaryRateCardDetailDataTable"
                                    list="${monetaryRateCardVersionsAsJson}"
                                    width="100%"
                                    rows="<%=rows%>"
                                    showPagination="true"
                                    showInfo="false"
                                    cssClass="table table-blue"
                                    showFilter="true">
                                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  class='selectAllCheckBox' />"  beanProperty="id"  style="width:5px !important;" />
                                <nv:dataTableColumn title="%{monetaryRateCardData.labelKey1}" beanProperty="label1"
                                                    tdCssClass="text-left text-middle word-break"
                                                    tdStyle="width:210px" />
                                <nv:dataTableColumn title="%{monetaryRateCardData.labelKey2}"
                                                    beanProperty="label2"
                                                    tdCssClass="text-left text-middle word-break" tdStyle="width:210px" />
                                <nv:dataTableColumn title="Pulse"
                                                    beanProperty="pulse1"
                                                    tdCssClass="text-left text-middle word-break" tdStyle="width:90px" />
                                <nv:dataTableColumn title="Rate"
                                                    beanProperty="rate1"
                                                    tdCssClass="text-left text-middle word-break" tdStyle="width:90px" />
                                <nv:dataTableColumn title="Rate Type"
                                                    beanProperty="rateType"
                                                    tdCssClass="text-left text-middle word-break" tdStyle="width:100px" />
                            </nv:dataTable>
                        </div>

                    </div>
                </div>
                <div class="col-xs-12">
                    <div class="col-xs-12 generalError" ></div>
                </div>
            <div class="modal-footer">
                <s:if test="%{scope == @com.elitecore.corenetvertex.pd.ratecard.RateCardScope@LOCAL.name()}">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" id="btnSave"
                            formaction="${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/${id}/bulkUpdateMonetaryRate">
                        <span class="glyphicon glyphicon-floppy-disk"></span>
                        <s:text name="button.save"/></button>
                </s:if>
                <s:else>
                    <button class="btn btn-sm btn-primary" type="submit" role="button" id="btnSave"
                            formaction="${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/${id}/bulkUpdateMonetaryRate">
                        <span class="glyphicon glyphicon-floppy-disk"></span>
                        <s:text name="button.save"/></button>
                </s:else>
                <button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel" tabindex="4"
                        onclick="resetForm();"><s:text
                        name="button.cancel"/></button>
            </div>

        </div>
    </div>
    </div>
</s:form>

<script>
    function validateForm() {
        var rate1Element = $("#bulkUpdateRate");
        var rate1ElementValue = rate1Element.val();
        var isValidVersions = true;
        var regExp = new RegExp("^[-]?\\d{0,14}\\.*\\d{0,6}$");
        clearErrorMessages();
        if (isNullOrEmpty(rate1ElementValue)) {
            setErrorOnElement(rate1Element, "Rate value can not be empty");
            isValidVersions = false;
        } else if(regExp.test(rate1ElementValue) == false){
            setErrorOnElement(rate1Element,"<s:text name="ratecard.version.double.length"/>");
            isValidVersions = false;
        }
        if($("input[type=checkbox][name=ids]:checked").length <= 0){
            $(".generalError").addClass("bg-danger");
            $(".generalError").text("At least select one Version Configuration for Update");
            isValidVersions = false;
        } else {
            $(".generalError").removeClass("bg-danger");
            $(".generalError").text("");
        }

        if(isValidVersions == false) {
            return false;
        }else {
            return true
        }
    }
    function setErrorOnElement(element,errorText) {
        var curElement = element;
        var parentElement = curElement.parent();
        if(parentElement.parent().hasClass("has-error has-feedback") == false){
            parentElement.parent().addClass("has-error has-feedback");
            parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
        }
    }
    function clearErrorMessages(){
        $(".generalError").removeClass("bg-danger");
        $(".generalError").text("");
        $( ".has-error" ).removeClass("has-error");
        $( ".has-success" ).removeClass("has-success");
        $( ".has-feedback" ).removeClass("has-feedback");
        $( ".alert" ).remove();	$(".nv-input-error").removeClass("nv-input-error");
        $(".glyphicon-remove").remove();
        $(".alert-danger").remove();
        $(".removeOnReset").remove();
    }
    function resetForm() {
        clearErrorMessages();
        $("#bulkUpdateRate").val("");
        $("input[type=search]").val("");
        $("#monetaryRateCardDetailDataTable").DataTable().search( '' ).columns().search( '' ).draw();
        $("input[type=checkbox][name=ids]:checked").removeAttr('checked');
        $("#monetaryRateCardDetailDataTable tr").removeClass("selected");
        $("#monetaryRateCardVersionConfigurationBulkUpdate").modal('hide');
        $("#rateUnitOperation").find('option:eq(0)').prop('selected', true);;

    }
    $(document).ready(function () {
        $("#monetaryRateCardDetailDataTable_filter").parent().removeClass("col-xs-6 col-sm-6 col-lg-4").addClass("col-xs-12 col-sm-12");
    })
</script>
