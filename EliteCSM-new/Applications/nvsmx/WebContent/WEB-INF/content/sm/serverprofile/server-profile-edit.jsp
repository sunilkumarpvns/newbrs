<%@taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="serverprofile.update" />
        </h3>
    </div>

    <div class="panel-body">

        <s:form namespace="/sm/serverprofile" action="server-profile"
                id="serverProfileEditForm"
                cssClass="form-horizontal form-group-sm "
                labelCssClass="col-xs-4 col-sm-5 text-right"
                elementCssClass="col-xs-8 col-sm-7"
                theme="bootstrap" validate="true">

            <s:hidden name="_method" value="put" />

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.pcrfService"/> </legend>
                    <div class="row">

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield id="pcrfServiceMinThreads"
                                         name="pcrfServiceMinThreads"
                                         key="serverprofile.pcrfServiceMinThreads"
                                         type="number"
                                         cssClass="form-control focusElement"
                                         maxlength="3" />

                            <s:textfield name="pcrfServiceQueueSize"
                                         key="serverprofile.pcrfServiceQueueSize"
                                         type="number"
                                         cssClass="form-control"
                                         maxlength="3"  />
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:textfield id="pcrfServiceMaxThreads"
                                         name="pcrfServiceMaxThreads"
                                         key="serverprofile.pcrfServiceMaxThreads"
                                         type="number"
                                         cssClass="form-control"
                                         maxlength="3"
                                         onblur="validateMinMaxThreadsRange('pcrfServiceMinThreads','pcrfServiceMaxThreads');"/>

                            <s:textfield id="pcrfServiceWorkerThreadPriority"
                                         name="pcrfServiceWorkerThreadPriority"
                                         key="serverprofile.pcrfServiceWorkerThreadPriority"
                                         type="number"
                                         cssClass="form-control"
                                         maxlength="3" />
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.loggig"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:select name="logLevel"
                                      key="serverprofile.logLevel"
                                      cssClass="form-control"
                                      listKey="name()"
                                      listValue="name()"
                                      list="@com.elitecore.commons.logging.LogLevel@values()"/>

                            <s:select name="rollingType"
                                      id="rollingType"
                                      key="serverprofile.rollingType"
                                      cssClass="form-control"
                                      list="@com.elitecore.corenetvertex.constants.RollingType@values()"
                                      listKey="value"
                                      listValue="label"
                                      onchange="displayRollingUnit();"/>
                        </div>
                        <div class="col-xs-12 col-sm-6 col-lg-6" id="rolledUnitComponentSection">


                            <s:textfield name="maxRolledUnits"
                                         type="number"
                                         key="serverprofile.maxRolledUnits"
                                         cssClass="form-control" maxlength="1" />

                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.notification.services"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:textfield name="notificationServiceExecutionPeriod"
                                         key="serverprofile.notificationServiceExecutionPeriod"
                                         type="number"
                                         cssClass="form-control" maxlength="5" />

                            <s:textfield name="batchSize"
                                         key="serverprofile.batchSize"
                                         type="number"
                                         cssClass="form-control" maxlength="5" />
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield name="maxParallelExecution"
                                         key="serverprofile.maxParallelExecution"
                                         type="number"
                                         cssClass="form-control" maxlength="5" />
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.diameter.stack"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:select name="diameterDuplicateReqCheckEnabled"
                                      id="diameterDuplicateReqCheckEnabled"
                                      key="serverprofile.diameterDuplicateReqCheckEnabled"
                                      list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                      listKey="getStringNameBoolean()"
                                      listValue="getStringNameBoolean()"
                                      cssClass="form-control"/>

                            <s:textfield id="diameterMinThreads"
                                         name="diameterMinThreads"
                                         key="serverprofile.diameterMinThreads"
                                         type="number"
                                         cssClass="form-control"
                                         maxlength="3"  />

                            <s:textfield name="diameterSessionTimeout"
                                         id="diameterSessionTimeout"
                                         key="serverprofile.diameterSessionTimeout"
                                         type="number"  cssClass="form-control"
                                         maxlength="100"  />

                            <s:textfield name="diameterQueueSize"
                                         id="diameterQueueSize"
                                         key="serverprofile.diameterQueueSize"
                                         type="number"
                                         cssClass="form-control" maxlength="7"  />
                        </div>


                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:textfield name="diameterDuplicateReqPurgeInterval"
                                         id="diameterDuplicateReqPurgeInterval"
                                         key="serverprofile.diameterDuplicateReqPurgeInterval"
                                         type="number"
                                         cssClass="form-control" maxlength="5" />

                            <s:textfield id="diameterMaxThreads"
                                         name="diameterMaxThreads"
                                         key="serverprofile.diameterMaxThreads"
                                         type="number"
                                         cssClass="form-control"
                                         maxlength="5"
                                         onblur="validateMinMaxThreadsRange('diameterMinThreads','diameterMaxThreads');"/>

                            <s:textfield name="diameterSessionCleanupInterval"
                                         id="diameterSessionCleanupInterval"
                                         key="serverprofile.diameterSessionCleanupInterval"
                                         type="number"
                                         cssClass="form-control" maxlength="15" />

                            <s:textfield name="diameterDwInterval"
                                         id="diameterDwInterval"
                                         key="serverprofile.diameterDwInterval"
                                         type="number"
                                         cssClass="form-control" maxlength="100" />

                        </div>


                    </div>
                </fieldset>
            </div>

            <%--Radius Listener--%>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>  <s:text name="serverprofile.radius.listener"/> </legend>
                    <div class="row" >

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:select name="radiusDuplicateReqCheckEnabled"
                                      id="radiusDuplicateReqCheckEnabled"
                                      key="serverprofile.radiusDuplicateReqCheckEnabled"
                                      list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                      listKey="getStringNameBoolean()"
                                      listValue="getStringNameBoolean()"
                                      cssClass="form-control" />

                            <s:textfield id="radiusMinThreads"
                                         name="radiusMinThreads"
                                         key="serverprofile.radiusMinThreads"
                                         type="number" cssClass="form-control"
                                         maxlength="3"   />
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:textfield name="radiusDuplicateReqPurgeInterval"
                                         id="radiusDuplicateReqPurgeInterval"
                                         key="serverprofile.radiusDuplicateReqPurgeInterval"
                                         type="number"
                                         cssClass="form-control" maxlength="5" />

                            <s:textfield id="radiusMaxThreads"
                                         name="radiusMaxThreads"
                                         key="serverprofile.radiusMaxThreads"
                                         type="number"
                                         onblur="validateMinMaxThreadsRange('radiusMinThreads','radiusMaxThreads');"
                                         cssClass="form-control" maxlength="5" />
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:textfield name="radiusQueueSize"
                                         id="radiusQueueSize"
                                         key="serverprofile.diameterQueueSize"
                                         type="number"
                                         cssClass="form-control" maxlength="7"  />
                        </div>
                    </div>
                </fieldset>
            </div>

            <div align="center">
                <button class="btn btn-sm btn-primary"
                        type="submit"
                        role="button"
                        formaction="${pageContext.request.contextPath}/sm/serverprofile/server-profile/${id}"
                        onclick="return validateOnSubmit();" >
                    <span class="glyphicon glyphicon-floppy-disk"></span>
                    <s:text name="button.save"/>
                </button>

                <button type="button"class="btn btn-sm btn-primary" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverprofile/server-profile/*'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span>
                    <s:text name="button.back"/>
                </button>
            </div>

        </s:form>

    </div>
</div>


<script type="text/javascript">

    function isValidDwInterval() {
        var dwElement = $('#diameterDwInterval');
        var dwInterval = dwElement.val();

        if (isNullOrEmpty(dwInterval)) {
            return false;
        }

        dwInterval = parseInt(dwInterval);
        if (((dwInterval >= 1 && dwInterval <= 60000) || dwInterval == -1) == false) {
            console.log("dwInterval" + dwInterval);
            setErrorOnElement(dwElement, '<s:text name="serverprofile.invalid.dwInterval"/>');
            return false;
        } else {
            clearErrorMessagesById('diameterDwInterval');
        }
    }

    function validateMinMaxThreadsRange(minThreadFieldId, maxThreadFieldId){
        var minThreadVal = $("#"+minThreadFieldId).val();
        var maxThreadVal = $("#"+maxThreadFieldId).val();
        if(parseInt(maxThreadVal)<parseInt(minThreadVal)){
            setError(maxThreadFieldId,"Must be greater than Minimum Threads");
            return false
        }else{
            clearErrorMessagesById(maxThreadFieldId);
        }
        return true;
    }

    function validateOnSubmit(){
        var flag = validateMinMaxThreadsRange('pcrfServiceMinThreads','pcrfServiceMaxThreads');
        if(flag){

            flag  = validateRollingUnits();
            if(flag) {

                flag = validateDiameterInterval();
                if(flag) {

                    flag = validateMinMaxThreadsRange('diameterMinThreads', 'diameterMaxThreads');
                    if(flag) {

                        flag = validateRadiusInterval();
                        if (flag) {
                            flag = validateMinMaxThreadsRange('radiusMinThreads', 'radiusMaxThreads');

                            if(flag) {
                                flag = isValidDwInterval();
                            }
                        }
                    }
                }

            }
        }
        return flag;
    }

    function validateRadiusInterval(){
        if($("#radiusDuplicateReqCheckEnabled").val()=='true'){
            if(isNullOrEmpty($("#radiusDuplicateReqPurgeInterval").val())){
                setError("radiusDuplicateReqPurgeInterval","Value Required");
                return false;
            }else{
                clearErrorMessagesById("radiusDuplicateReqPurgeInterval");
            }
        }
        return true;
    }

    function validateDiameterInterval(){
        if($("#diameterDuplicateReqCheckEnabled").val()=='true'){

            if(isNullOrEmpty($("#diameterDuplicateReqPurgeInterval").val())){
                setError("diameterDuplicateReqPurgeInterval","Value Required");
                return false;
            }else{
                clearErrorMessagesById("diameterDuplicateReqPurgeInterval");
            }
        }
        return true;
    }

    function validateRollingUnits(){
        var value = $("#rollingType").val();

        var elementId = "timeBasedRollingUnits";
        if(value==1){
            elementId = "timeBasedRollingUnits";
        }else if(value==2){
            elementId = "sizeBasedRollingUnits";
        }

        if(isNullOrEmpty($("#"+elementId).val())){
            setError(elementId,"Value Required");
            return false
        }else{
            clearErrorMessagesById(elementId);
            if(value==2){
                if($("#"+elementId).val()<1) {
                    setError(elementId, "Must be between greater than Zero");
                    return false;
                }
            }
        }
        return true;
    }

    $(function(){
        displayRollingUnit();
    });

    function displayRollingUnit() {
        var value = $("#rollingType").val();
        if(value==1){
            addTimeBasedSelectBox();
        }else if(value==2){
            addSizeBasedComponent();
        }
    }

    function addTimeBasedSelectBox(){
        $("#sizeBasedComponent").remove();
        var timeBasedSelectBox = "<div class=\"form-group\" id=\"timeBasedComponent\" >\n" +
            "\t<label class=\"col-xs-4 col-sm-5 text-right control-label\" id=\"lbl_timeBasedRollingUnits\" for=\"timeBasedRollingUnits\" style=\"display: block;\">\n" +
            "\t\tRolling Units        \n" +
            "\t</label>    \n" +
            "\t<div class=\"col-xs-8 col-sm-7 controls\">\n" +
            "\t\t<select name=\"rollingUnits\" id=\"timeBasedRollingUnits\" class=\"form-control form-control\" style=\"display: block;\">\n" +
            "\t\t    <option value=\"3\">MINUTE</option>\n" +
            "    \t\t<option value=\"4\">HOUR</option>\n" +
            "\t\t    <option value=\"5\">DAILY</option>\n" +
            "\t\t</select>\n" +
            "\t</div>\n" +
            "</div>";

        $("#rolledUnitComponentSection").append(timeBasedSelectBox);

        if('${rollingUnits}'>5 || '${rollingUnits}'<3){
            $("#timeBasedRollingUnits").val('3')
        }else{
            $("#timeBasedRollingUnits").val('${rollingUnits}')
        }
    }

    function addSizeBasedComponent(){
        $("#timeBasedComponent").remove();
        var sizeBasedComponent = "<div class=\"form-group \" id=\"sizeBasedComponent\">\n" +
            "\t<label class=\"col-xs-4 col-sm-5 text-right control-label\" id=\"lbl_sizeBasedRollingUnits\" for=\"sizeBasedRollingUnits\" style=\"display: block;\">\n" +
            "\t\tRolling Size(KB)\n" +
            "\t</label>\n" +
            "\t<div class=\"col-xs-8 col-sm-7 controls\">\n" +
            "\t\t<input type=\"number\" name=\"rollingUnits\" id=\"sizeBasedRollingUnits\" class=\"form-control\" style=\"display: block;\">\n" +
            "\t</div>\n" +
            "</div>";

        $("#rolledUnitComponentSection").append(sizeBasedComponent);
        $("#sizeBasedRollingUnits").val('${rollingUnits}')
    }
</script>
