<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .customized-row-margin {
        margin-right: -13px;
    }
    .padding-to-left {
        padding-left: 20px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="update.slice.conf" />
        </h3>
    </div>
    <div class="panel-body" >
        <s:form namespace="/pd/sliceconfig" id="sliceconfig" action="slice-config" method="post" cssClass="form-horizontal"
                validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateData()">
            <s:hidden name="_method" value="put" />
            <div class="row">
                <div class="row">
                    <div class="col-xs-12 col-sm-6">
                        <div class="col-xs-12" style="padding-right: 0px">
                            <s:set var="priceTag">
                                <s:property value="getText('slice.monetary.reservation')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                            </s:set>
                            <s:textfield name="monetaryReservation" id="monetaryReservation" key="priceTag" cssClass="form-control" labelCssClass="col-sm-5" elementCssClass="col-sm-7" maxLength="18" tabindex="1" onkeypress="return isNaturalInteger(event);"/>
                        </div>
                    </div>
                </div>
            </div>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="slice.volume" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="col-sm-12" style="padding-right: 0px">
                            <s:textfield name="volumeSlicePercentage" id="volumeSlicePercentage" key="slice.percent" value = "%{volumeSlicePercentage}" cssClass="form-control" tabindex="2"

                                         maxlength="3" onkeypress="return isNaturalInteger(event);"/>
                        </div>

                        <div class="col-xs-12">
                            <div class="row">
                                <div class="col-xs-8 col-sm-8">
                                    <div class="row customized-row-margin">
                                <s:textfield name="volumeMinimumSlice" id="volumeMinimumSlice"
                                             key="slice.minvalue" labelCssClass="col-sm-6" elementCssClass="col-sm-6 padding-to-left" cssClass="form-control" maxlength="7" tabindex="4" onkeypress="return isNaturalInteger(event);"/>
                                    </div>
                                </div>
                                <div class="col-xs-4 col-sm-4">
                                    <div class="row customized-row-margin ">
                                        <s:textfield value="MB" readonly="true" name="volumeMinimumSliceUnit" cssClass="form-control"  elementCssClass="col-sm-12"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="col-sm-12" style="padding-right: 0px">
                            <s:textfield name="volumeSliceThreshold" id="volumeSliceThreshold" key="slice.threshold" value = "%{volumeSliceThreshold}" cssClass="form-control" tabindex="3"
                                         maxlength="3" onkeypress="return isNaturalInteger(event);"/>
                        </div>
                        <div class="col-xs-12">
                            <div class="row">
                                <div class="col-xs-8 col-sm-8">
                                    <div class="row customized-row-margin">
                                        <s:textfield name="volumeMaximumSlice" id="volumeMaximumSlice"
                                                 key="slice.maxvalue" labelCssClass="col-sm-6" elementCssClass="col-sm-6 padding-to-left" cssClass="form-control" maxlength="7" tabindex="5" onkeypress="return isNaturalInteger(event);"/>
                                    </div>
                                </div>
                                <div class="col-xs-4 col-sm-4">
                                    <div class="row customized-row-margin">
                                        <s:textfield value="MB" readonly="true" name="volumeMaximumSliceUnit" cssClass="form-control"  elementCssClass="col-sm-12"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="slice.time" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="col-sm-12" style="padding-right: 0px">
                            <s:textfield name="timeSlicePercentage" id="timeSlicePercentage" key="slice.percent" value = "%{timeSlicePercentage}" cssClass="form-control" tabindex="6"
                                         maxlength="3" onkeypress="return isNaturalInteger(event);"/>
                        </div>

                        <div class="col-xs-12">
                            <div class="row">
                                <div class="col-xs-8 col-sm-8">
                                    <div class="row customized-row-margin">
                                    <s:textfield name="timeMinimumSlice" id="timeMinimumSlice"
                                                 key="slice.minvalue" labelCssClass="col-sm-6" elementCssClass="col-sm-6 padding-to-left" cssClass="form-control" maxlength="7" tabindex="8" onkeypress="return isNaturalInteger(event);"/>
                                    </div>
                                </div>
                                <div class="col-xs-4 col-sm-4">
                                    <div class="row customized-row-margin">
                                        <s:textfield value="MINUTE" readonly="true" name="timeMinimumSliceUnit" cssClass="form-control"  elementCssClass="col-sm-12"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6  " >
                            <div class="col-sm-12" style="padding-right: 0px">
                                <s:textfield name="timeSliceThreshold" id="timeSliceThreshold" key="slice.threshold" value = "%{timeSliceThreshold}" cssClass="form-control" tabindex="7"
                                             maxlength="3" onkeypress="return isNaturalInteger(event);"/>
                            </div>

                            <div class="col-xs-12">
                                <div class="row">
                                    <div class="col-xs-8 col-sm-8">
                                        <div class="row customized-row-margin">
                                            <s:textfield name="timeMaximumSlice" id="timeMaximumSlice"
                                                     key="slice.maxvalue" labelCssClass="col-sm-6" elementCssClass="col-sm-6 padding-to-left" cssClass="form-control" maxlength="7" tabindex="10" onkeypress="return isNaturalInteger(event);"/>
                                        </div>
                                        </div>
                                    <div class="col-xs-4 col-sm-4">
                                        <div class="row customized-row-margin">
                                                <s:textfield value="MINUTE" readonly="true" name="timeMaximumSliceUnit" cssClass="form-control"  elementCssClass="col-sm-12"/>
                                        </div>
                                        </div>
                                </div>
                            </div>

                        </div>
                </div>
            </fieldset>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/pd/sliceconfig/slice-config/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/sliceconfig/slice-config/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                </div>
            </div>
        </div>
    </s:form>
    </div>
</div>
<script>
    function validateData() {
        if(isValidVolumeSlice() && isValidSliceTime() && isValidMonetaryData() && isValidMinMaxDataRange() && isValidMinMaxTimeRange()) {
            return true;
        }
        return false;
    }

    function isValidVolumeSlice() {
        var flg=true;
        var maxSliceVolumeValue=1048576; //1024GB in MB
        var volumeMinimumSlice = new Number($('#volumeMinimumSlice').val());
        var volumeMaximumSlice = new Number($('#volumeMaximumSlice').val());

        if(volumeMinimumSlice>maxSliceVolumeValue) {
            if(flg==true){flg=false;}
            setError('volumeMinimumSlice',"Max allowed value is "+maxSliceVolumeValue);
        }
        if(volumeMaximumSlice>maxSliceVolumeValue) {
            if(flg==true){flg=false;}
            setError('volumeMaximumSlice',"Max allowed value is "+maxSliceVolumeValue);
        }
        if(volumeMaximumSlice==0){
            if(flg==true){flg=false;}
            setError('volumeMaximumSlice',"Value should be greater than zero");
        }
        if(volumeMinimumSlice==0){
            if(flg==true){flg=false;}
            setError('volumeMinimumSlice',"Value should be greater than zero");
        }
        return flg;
    }

    function isValidSliceTime(){
        var flg=true;
        var maxSliceTimeValueInMinute=1440;//24HR in Min
        var timeMinimumSlice = new Number($('#timeMinimumSlice').val());
        var timeMaximumSlice = new Number($('#timeMaximumSlice').val());

        if(timeMinimumSlice>maxSliceTimeValueInMinute) {
            if(flg==true){flg=false;}
            setError('timeMinimumSlice',"Max allowed value is "+maxSliceTimeValueInMinute);
        }
        if(timeMaximumSlice>maxSliceTimeValueInMinute) {
            if(flg==true){flg=false;}
            setError('timeMaximumSlice',"Max allowed value is "+maxSliceTimeValueInMinute);
        }
        if(timeMaximumSlice==0) {
            if(flg==true){flg=false;}
            setError('timeMaximumSlice',"Value should be greater than zero");
        }
        if(timeMinimumSlice==0){
            if(flg==true){flg=false;}
            setError('timeMinimumSlice',"Value should be greater than zero");
        }
        return flg;
    }

    function isValidMonetaryData(){
        var flg=true;
        if(isNaN($('#monetaryReservation').val())){
            flg=false;
            setError('monetaryReservation',"Only numeric value allowed");
        }
        return flg;
    }

    function isValidMinMaxDataRange() {
        var volumeMinimumSlice = new Number($('#volumeMinimumSlice').val());
        var volumeMaximumSlice = new Number($('#volumeMaximumSlice').val());

        if(volumeMinimumSlice >= volumeMaximumSlice) {
            setError("volumeMaximumSlice", "Max value must be greater than min value.");
            return false;
        }
        return true;
    }

    function isValidMinMaxTimeRange() {
        var timeMaxSliceData = new Number($('#timeMaximumSlice').val());
        var timeMinSliceData = new Number($('#timeMinimumSlice').val());

        if(timeMinSliceData >= timeMaxSliceData) {
            setError("timeMaximumSlice", "Max value must be greater than min value.");
            return false;
        }
        return true;
    }
</script>