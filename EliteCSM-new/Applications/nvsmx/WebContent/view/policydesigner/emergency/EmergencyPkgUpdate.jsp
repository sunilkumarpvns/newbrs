<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">Update Package</h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/emergency/EmergencyPkg/update" id="pkgUpdate" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm();">
			<div class="row">
				<div class="col-xs-6">
					<s:hidden 		name="pkgData.id" 	/>
					<s:textfield id="pkgName"			name="pkgData.name"  		key="pkg.name" 	cssClass="form-control focusElement" onblur="verifyUniqueness('pkgName','update','%{pkgData.id}','com.elitecore.corenetvertex.pkg.PkgData','','');"  />
					<s:textarea  id="pkgDescription"	name="pkgData.description" 	key="pkg.description" 	cssClass="form-control" />


					<div class="form-group "><label class="col-xs-12 col-sm-4 col-lg-3 control-label" id="lbl_pkgGroup">Groups </label>

						<div class="col-xs-12 col-sm-8 col-lg-9 controls">
							<s:if test="%{pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
								<s:hidden name="groupIds" value="%{pkgData.groups}"/>
								<select disabled="disabled" key="pkg.groups" class="form-control select2" style="width:100%" multiple="true">
									<s:iterator value="groupInfoList">
										<option locked="<s:property value="locked"/>" <s:property value="selected"/>
												value="<s:property value="id"/>" id="<s:property value="id"/>">
											<s:property value="name"/></option>
									</s:iterator>
								</select>
							</s:if>
							<s:else>
								<select name="groupIds" key="pkg.groups" class="form-control select2" style="width:100%" multiple="true">
									<s:iterator value="groupInfoList">
										<option locked="<s:property value="locked"/>" <s:property value="selected"/>
												value="<s:property value="id"/>" id="<s:property value="id"/>">
											<s:property value="name"/></option>
									</s:iterator>
								</select>
							</s:else>
						</div>
					</div>

					<s:select 	 name="pkgData.status" 		    key="pkg.status" list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" 		cssClass="form-control" id="pkgStatus" />
					<s:hidden 	 name="pkgData.type"  />
					<s:hidden 	 name="pkgData.orderNumber"  />
					<s:hidden 	 id="packageMode" name="pkgData.packageMode"  />
					<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{pkgData.groups}"/>

				</div>

				<div class="col-xs-6">
					<s:if test="%{pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
						<s:datepicker disabled="true" name="pkgData.availabilityStartDate" key="pkg.availability.start.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"  
								 id="availabilityStartDate" readonly="true" />
								  
						<s:datepicker disabled="true" name="pkgData.availabilityEndDate" key="pkg.availability.end.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"  
								  id="availabilityEndDate" readonly="true" />	
					</s:if>
					<s:else>
						<s:datepicker name="pkgData.availabilityStartDate" key="pkg.availability.start.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"  
								 id="availabilityStartDate" readonly="true" />
								  
						<s:datepicker name="pkgData.availabilityEndDate" key="pkg.availability.end.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"  
								  id="availabilityEndDate" readonly="true" />
					</s:else>
					<s:if test="%{@com.elitecore.commons.base.Strings@isNullOrBlank(@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_1'))}">
						<s:textfield 	name="pkgData.param1"	key="Param 1" 		cssClass="form-control" />
					</s:if>
					<s:else>
						<s:textfield 	name="pkgData.param1" 	key="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_1')" 	cssClass="form-control" />
					</s:else>
					<s:if test="%{@com.elitecore.commons.base.Strings@isNullOrBlank(@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_2'))}">
						<s:textfield 	name="pkgData.param2" 	key="Param 2"  		cssClass="form-control" />
					</s:if>
					<s:else>
						<s:textfield 	name="pkgData.param2" 	key="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_2')"  	cssClass="form-control" />
					</s:else>
				</div>
				
			</div>

			<div class="row">
				<div class="col-xs-12" align="center">
					<s:submit cssClass="btn btn-sm btn-primary" type="button" role="button" id="btnUpdate"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"  style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId=${pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg" /></button>
				</div>
			</div>

		</s:form>
	</div>
</div>
<script type="text/javascript">
function validateForm(){
	var isValidName = verifyUniquenessOnSubmit('pkgName','update','<s:text name="pkgData.id"/>','com.elitecore.corenetvertex.pkg.PkgData','','');
	var pkgtype =$("#pkgType").val();

	var isRetiredStatus = true;

	var status =$("#pkgStatus").val();
    if(status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>'){
    	setError('pkgStatus',"<s:text name='error.status.retired'/>");
    	isRetiredStatus = false;
    }
    return  (isValidName && isRetiredStatus);
}

$(function(){
	$(".select2").select2();
	checkForPackageMode();
})

</script>

<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>

<%@include file="/view/policydesigner/pkg/PackageValidation.jsp"%>
