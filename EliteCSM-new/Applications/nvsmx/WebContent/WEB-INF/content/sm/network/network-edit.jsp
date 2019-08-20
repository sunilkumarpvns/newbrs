<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<script>
    function validate() {
        return verifyUniquenessOnSubmit('name', 'update', '<s:property value="id"/>', 'com.elitecore.corenetvertex.sm.routing.network.NetworkData', '', '');
    }
</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="network.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/network" action="network" method="post" cssClass="form-horizontal" validate="true" validator="validate()"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                	<s:select id="countryNames" value="countryData.id" name="countryData.id" 	key="network.country" cssClass="form-control select2" list="countryList" listKey="id" listValue="name"  cssStyle="width:100%" tabindex="1"/>
                    <s:select id="brandNames" 	value="brandData.id" name="brandData.id" 	key="network.brand" cssClass="form-control select2" list="%{brandList}" listKey="id" listValue="name"  cssStyle="width:100%" tabindex="2" />
					<s:select id="operatorNames" value="operatorData.id" name="operatorData.id" 	key="network.operator" cssClass="form-control select2" list="operatorList" listKey="id" listValue="name"  cssStyle="width:100%" tabindex="3"/>
					<s:textfield name="name" key="network.name" cssClass="form-control" id="name" type="text" maxlength="100" tabindex="4" />
					<s:select id="technology" 	name="technology" 	key="network.technology" cssClass="form-control select2" list="{'GSM','CDMA'}" cssStyle="width:100%" tabindex="5"/>
					<s:textfield name="mcc" 	key="network.mcc" cssClass="form-control" id="year" title="Please enter exactly 4 digits"  tabindex="6" />
					<s:textfield name="mnc" key="network.mnc" cssClass="form-control" id="networkAdditionalInfo" type="text" maxlength="200" tabindex="7" />
                </div>                
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/sm/network/network/${id}" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/network/network/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back" /></button>
                </div>
            </div>
        </s:form>
    </div>
</div>
</div>