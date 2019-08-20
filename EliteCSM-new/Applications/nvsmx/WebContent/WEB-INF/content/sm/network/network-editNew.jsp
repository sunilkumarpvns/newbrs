<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<script>
    function validate() {
        return verifyUniquenessOnSubmit('name', 'create', '', 'com.elitecore.corenetvertex.sm.routing.network.NetworkData', '', '');
    }
</script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="network.create"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/network" id="networkCreateForm" action="network" method="post" cssClass="form-horizontal"
                validate="true"
                validator="validate()" labelCssClass="col-xs-12 col-sm-4 col-lg-3"
                elementCssClass="col-xs-12 col-sm-8 col-lg-9">

        <s:token/>
        <div class="row">
            <div class="col-sm-9 col-lg-7">
                <s:select id="countryNames" value="countryData.id" name="countryData.id" key="network.country"
                          cssClass="form-control select2" list="countryList" listKey="id" listValue="name"
                          cssStyle="width:100%" tabindex="1"/>
                <s:select id="brandNames" name="brandData.id" key="network.brand" cssClass="form-control select2"
                          list="%{brandList}" listKey="id" listValue="name" cssStyle="width:100%" tabindex="2"/>
                <s:select id="operatorNames" name="operatorData.id" key="network.operator"
                          cssClass="form-control select2" list="operatorList" listKey="id" listValue="name"
                          cssStyle="width:100%" tabindex="3"/>
                <s:textfield name="name" key="network.name" cssClass="form-control" id="name" type="text"
                             maxlength="100" tabindex="4"/>
                <s:select id="technology" name="technology" key="network.technology" cssClass="form-control select2"
                          list="{'GSM','CDMA'}" cssStyle="width:100%" tabindex="5"/>
                <s:textfield name="mcc" key="network.mcc" cssClass="form-control" id="year"
                             title="Please enter exactly 4 digits" tabindex="6"/>
                <s:textfield name="mnc" key="network.mnc" cssClass="form-control" id="networkAdditionalInfo" type="text"
                             maxlength="200" tabindex="7"/>
            </div>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="8"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="9"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/network/network'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
            </s:form>
        </div>
    </div>
</div>