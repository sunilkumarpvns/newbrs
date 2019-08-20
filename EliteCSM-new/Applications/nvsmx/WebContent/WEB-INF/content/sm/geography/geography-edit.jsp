<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script type="text/javascript">
    function  addCountryCode(data,type,thisBean){
        return "<input value="+thisBean.code+"  type='hidden' name='countryData' />" +thisBean.code;
    }
    function removeCountry(data,type,thisBean){
        return '<span class="btn defaultBtn" onclick="removeRow(this);"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span>';

    }
    function removeRow(data){
        $("#countryList").DataTable().row($(data).parents('tr')).remove().draw(false);
    }

    function removeRecords(obj) {
        var selectVar = getSelectedValuesForDelete();
        if(selectVar == true){
            var flag = false;
            flag = deleteConfirmMsg(obj,"Delete selected packages ?");
            if(flag==true) {
                removeData();
            }
            return flag;
        }
    }

    function getSelectedValuesForDelete(){
        var selectedData = false;
        var selectedDatas = document.getElementsByName("ids");
        for (var index=0; index < selectedDatas.length; index++){
            if(selectedDatas[index].name == 'ids'){
                if(selectedDatas[index].checked == true){
                    selectedData = true;
                }
            }


        }
        if(selectedData == false){
            return addWarning(".popup","At least select one country for delete");
        }
        return selectedData;
    }

    function removeData(){
        $("#countryList").DataTable().rows('.selected').remove().draw(false);
    }

</script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="geography.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/geography" action="geography" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
        <s:hidden name="_method" value="put" />
        <s:token/>
        <div class="row">
            <div class="col-sm-9 col-lg-7">
                <s:textfield name="name" key="geography.name" id="name" cssClass="form-control focusElement" tabindex="1" />
                <s:select multiple="true" name="country" key="geography.country"
                          cssClass="form-control select2" cssStyle="width:100%" id="countryId"
                          list="countryList"
                          listKey="code" listValue="name" tabindex="2"/>
                <div class="col-sm-12">
                    <div class="col-xs-12 col-sm-4 col-lg-3"></div>
                    <div class="col-xs-12 col-sm-8 col-lg-9">
                        <strong>Note :- </strong> Country will add new country data. It will not Override the existing configuration
                    </div>
                </div>
            </div>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="geography.country" /></legend>
                <div class="row">
                    <div class="col-sm-12">
                        <div class="panel-body">
                            <div class="dataTable-button-groups">
					            <span class="btn-group btn-group-sm"  id="btnRemove" data-toggle="confirmation-singleton" onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
							<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
								<span class="glyphicon glyphicon-trash" title="delete"></span>
							</button>
						</span>
                            </div>
                        <nv:dataTable
                                id="countryList"
                                list="${countryListAsJson}"
                                width="100%"
                                showPagination="false"
                                showFilter="true"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
                            <nv:dataTableColumn title="Name" beanProperty="name" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="Code" beanProperty="code" renderFunction="addCountryCode" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" renderFunction="removeCountry" style="width:20px;" />
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/sm/geography/geography/${id}" tabindex="3"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="4" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/geography/geography/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/> </button>
                </div>
            </div>
            </s:form>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(function(){
        $("#countryId").select2();
    });
    function validateForm(){
        return setCountryCodes() && verifyUniquenessOnSubmit('name','update','<s:text name="id"/>','com.elitecore.corenetvertex.sm.location.geography.GeographyData','','');
    }

    function setCountryCodes(){
        var countryIds = document.getElementsByName("countryData");
        var countryStr = "";
        if(isNullOrEmpty(countryIds) == false) {
            for (var i = 0; i < countryIds.length; i++){
                var value = countryIds[i].value;
                countryStr = countryStr + value;
                countryStr = countryStr + ",";
            }
            var countryValues = $("#countryId").val();
            countryStr = countryStr + countryValues;
            $('#countryId').val(countryStr.split(','));
        }
        if(isNullOrEmpty($("#countryId").val())){
            setError("countryId",'<s:text name="error.valueRequired" />');
            return false;
        }
        return true;
    }
</script>