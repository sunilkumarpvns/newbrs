<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<style>

</style>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="device.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/device" id="deviceFrom" action="device" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                    <s:textfield 	name="tac" 		key="device.tac" 	id="deviceTac" cssClass="form-control focusElement" type="text" title="Please enter exactly 8 digits" tabindex="1" maxLength="8" onkeypress="return isNaturalInteger(event);" />
					<s:textfield name="brand" key="device.brand" cssClass="form-control" id="deviceBrand" type="text" maxlength="512" tabindex="2" />
					<s:textfield name="deviceModel" key="device.tacModel" cssClass="form-control" id="deviceModel" type="text" maxlength="512" tabindex="3" />
					<s:textfield name="hardwareType" 	key="device.hardware" cssClass="form-control" id="deviceHardware" type="text" maxlength="512" tabindex="4" />
					<s:textfield name="os" 	key="device.os" cssClass="form-control" id="deviceOs" type="text" maxlength="512" tabindex="5" />
					<s:textfield name="year" 	key="device.release_year" cssClass="form-control" id="deviceReleaseYear" type="text" tabindex="6" maxlength="40" />
					<s:textfield name="additionalInformation" key="device.additional_info" cssClass="form-control" id="deviceAdditionalInfo" type="text" maxlength="200" tabindex="7" />
                </div>                
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/sm/device/device/${id}" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/device/device/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back" /></button>
                </div>
            </div>
        </s:form>
    </div>
</div>
</div>
<script type="text/javascript">
    function validateForm() {
        return verifyUniquenessOnSubmit('deviceTac','update','<s:property value="id"/>','com.elitecore.corenetvertex.sm.device.DeviceData','','tac');
    }
</script>