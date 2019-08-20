<%@taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="offlineserverprofile.update" />
        </h3>
    </div>

    <div class="panel-body">

        <s:form namespace="/sm/serverprofile" action="offline-rnc"
                id="offlineRnCProfileEditForm"
                cssClass="form-horizontal form-group-sm "
                labelCssClass="col-xs-4 col-sm-5 text-right"
                elementCssClass="col-xs-8 col-sm-7"
                theme="bootstrap" validate="true">
            <s:hidden name="_method" value="put" />

			<div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="offlineserverprofile.offline.service"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                           <s:textfield name="minThread" 
                            			key="offlineserverprofile.offlinernc.minthread"  
                            			cssClass="form-control focusElement"
										type="number" 
										id="minThread"
										maxLength="3" />
							<s:textfield name="maxThread" 
										 key="offlineserverprofile.offlinernc.maxthread"
										 type="number" 
										 cssClass="form-control" 
										 id="maxThread"  
										 maxLength="3"
										 onblur="validateMinMaxThreadsRange('minThread','maxThread');"/>
							<s:textfield name="threadPriority" 
										 type="number"
										 key="offlineserverprofile.offlinernc.threadpriority" 
										 cssClass="form-control"
										 id="threadPriority"
										 maxLength="3" />
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                           <s:textfield name="scanInterval" 
                           				type="number"
										key="offlineserverprofile.offlinernc.scaninterval" 
										cssClass="form-control"
										id="scanInterval" 
										maxLength="7"/>
							<s:textfield name="fileBatchSize" 
										 type="number" 
										 key="offlineserverprofile.offlinernc.filebatchsize"
										 cssClass="form-control"
									 	 labelCssClass="col-sm-5 col-xs-4"
										 elementCssClass="col-sm-7 col-xs-8" 
										 id="fileBaseSize"
										 maxLength="6" />
									
							<s:textfield name="fileBatchQueue" 
										 type="number" 
										 key="offlineserverprofile.offlinernc.filebatchqueue"
										 cssClass="form-control"
										 id="fileBatchQueue"
										 maxLength="5" />
                        </div>
                    </div>
                </fieldset>
            </div>
            
            
            
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="offlineserverprofile.offlinernc.logging"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:select name="logLevel"
                                      key="offlineserverprofile.offlinernc.loglevel"
                                      cssClass="form-control"
                                      listKey="name()"
                                      listValue="name()"
                                      list="@com.elitecore.commons.logging.LogLevel@values()"/>

                            <s:select name="rollingType"
                                      id="rollingType"
                                      key="offlineserverprofile.offlinernc.rollingtype"
                                      cssClass="form-control"
                                      list="@com.elitecore.corenetvertex.constants.RollingType@values()"
                                      listKey="value"
                                      listValue="label"
                                      onchange="displayRollingUnit();" />
                        </div>
                        <div class="col-xs-12 col-sm-6 col-lg-6" id="rolledUnitComponentSection">


                            <s:textfield name="maxRolledUnits"
                                         type="number"
                                         key="offlineserverprofile.offlinernc.maxrolledunits"
                                         cssClass="form-control" 
                                         maxlength="1" />
                                         
                                         
                                         
						</div>
                </fieldset>
            </div>

          <div align="center">
                <button class="btn btn-sm btn-primary"
                        type="submit"
                        role="button"
                        formaction="${pageContext.request.contextPath}/sm/serverprofile/offline-rnc/${id}"
                        onclick="return validateOnSubmit();" >
                    <span class="glyphicon glyphicon-floppy-disk"></span>
                    <s:text name="button.save"/>
                </button>

                <button type="button"class="btn btn-sm btn-primary" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverprofile/offline-rnc/show'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span>
                    <s:text name="button.back"/>
                </button>
            </div>

        </s:form>

    </div>
</div>

<div style="display: none"  id="sizeBasedComponent">
  <div id="sizeBaseDiv">
	<label class="col-xs-4 col-sm-5 text-right control-label" id="test" for="sizeBasedRollingUnits" style="display: block;"> Rolling Size(KB) </label>
	<div class="col-xs-8 col-sm-7 controls">
		<input type="number" name="rollingUnits" id="sizeBasedRollingUnits" class="form-control" style="display: block;">
	</div>
  </div>
</div>


<div style="display: none" id="timeBasedComponent">
  <div id = "timeBaseDiv">
	<label class="col-xs-4 col-sm-5 text-right control-label" id="lbl_timeBasedRollingUnits" for="timeBasedRollingUnits" style="display: block;"> Rolling Units </label>
	<div class="col-xs-8 col-sm-7 controls">
		<select name="rollingUnits" id="timeBasedRollingUnits" class="form-control form-control" style="display: block;">
			<option value="3">MINUTE</option>
			<option value="4">HOUR</option>
			<option value="5">DAILY</option>
		</select>
	</div>
  </div>
</div>


<script type="text/javascript">
	function validateMinMaxThreadsRange(minThreadFieldId, maxThreadFieldId) {
		var minThreadVal = $("#" + minThreadFieldId).val();
		var maxThreadVal = $("#" + maxThreadFieldId).val();
		if (parseInt(maxThreadVal) < parseInt(minThreadVal)) {
			setError(maxThreadFieldId,
					"<s:text name='offlineserverprofile.offlinernc.compare_min_max'/>");
			return false
		} else {
			clearErrorMessagesById(maxThreadFieldId);
		}
		return true;
	}

	function validateOnSubmit() {
		var flag = validateMinMaxThreadsRange('minThread', 'maxThread');
		if (flag) {
			flag = validateRollingUnits();
		}
		return flag;
	}

	function validateRollingUnits() {
		var value = $("#rollingType").val();

		var elementId = "timeBasedRollingUnits";
		if (value == 1) {
			elementId = "timeBasedRollingUnits";
		} else if (value == 2) {
			elementId = "sizeBasedRollingUnits";
		}

		if (isNullOrEmpty($("#" + elementId).val())) {
			setError(elementId, "Value Required");
			return false;
		} else {
			clearErrorMessagesById(elementId);
			if (value == 2) {
				if (!isNaturalNumber($("#" + elementId).val())) {
					setError(elementId,
							"<s:text name='offlineserverprofile.offlinernc.greater_than_zero'/>");
					return false;
				}
			}
		}
		return true;
	}

	$(function() {
		displayRollingUnit();
	});

	function displayRollingUnit() {
		var value = $("#rollingType").val();
		if (value == 1) {
			addTimeBasedSelectBox();
		} else if (value == 2) {
			addSizeBasedComponent();
		}
	}
	function addTimeBasedSelectBox() {
		$("#rolledUnitComponentSection").find('#sizeBaseDiv').remove();
		var timeBasedSelectBox = $("#timeBasedComponent").html();
		$("#rolledUnitComponentSection").append(timeBasedSelectBox);
		if ('${rollingUnits}' > 5 || '${rollingUnits}' < 3) {
			$("#timeBasedRollingUnits").val('3')
		} else {
			$("#timeBasedRollingUnits").val('${rollingUnits}')
		}
	}

	function addSizeBasedComponent() {
		$("#rolledUnitComponentSection").find('#timeBaseDiv').remove();
		var sizeBasedComponent = $("#sizeBasedComponent").html();
		$("#rolledUnitComponentSection").append(sizeBasedComponent);
		$("#sizeBasedRollingUnits").val('${rollingUnits}')
	}
</script>

