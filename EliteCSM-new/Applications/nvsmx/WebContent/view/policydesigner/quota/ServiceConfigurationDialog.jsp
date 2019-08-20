<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib uri="/struts-tags/ec" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<style type="text/css">
.bottom-space{
	margin-bottom: 10px;
}
.form-control-feedback{
text-align: inherit;
}
</style>
<div class="modal" id="serviceDialog" tabindex="-1" role="dialog"
	aria-labelledby="serviceDialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" 
					aria-label="Close" onclick="clearDialog()">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title set-title" id="serviceDialogTitle">
					<s:text name="quotaprofile.serviceconfiguration"  />
				</h4>
			</div>
			<div class="modal-body">
				<div class="row" style="margin-bottom:20px">
					<div class="col-xs-12">
					<s:if test="%{quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name() || quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}">
						<s:select name="" key="quotaprofile.servicetype" 
							cssClass="form-control" list="%{dataServiceTypes}" listKey="id"
							listValue="name" id="serviceData" />
					</s:if>
					<s:else>
						<s:select name="" key="quotaprofile.servicetype" disabled="true" 
							cssClass="form-control" list="%{dataServiceTypes}" listKey="id"
							listValue="name" id="serviceData" />
					</s:else>
					</div>
				</div>
				<div class="row" style="margin-bottom:20px">
					<div class="col-xs-12">
					<s:if test="%{quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name() || quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}">
						<s:select name="" key="quotaprofile.aggregationkey"
							cssClass="form-control control-label"
							list="@com.elitecore.corenetvertex.constants.AggregationKey@values()"
							id="aggregationkey" listValue="getVal()" />
					  </s:if>
				     <s:else>
						<s:select name="" key="quotaprofile.aggregationkey" disabled="true"
							cssClass="form-control control-label"
							list="@com.elitecore.corenetvertex.constants.AggregationKey@values()"
							id="aggregationkey" listValue="getVal()"/>
					</s:else>
						</div>
				</div>

				<div class="row" style="margin-bottom:20px">
					<div class="col-xs-12">
						<s:label label="Total Quota"></s:label>
					</div>
					<div class="col-xs-12 bottom-space">
						<div class="row">
							<div class="col-xs-6">
								<s:textfield cssClass="form-control"
									id="totalQuotaDialog" style="width : 100%;" elementCssClass="col-xs-12"
									placeholder="UNLIMITED" maxlength="18" onkeypress="return isNaturalInteger(event);" />
							</div>
							<div class="col-xs-6">
								<s:select cssClass="form-control" elementCssClass="col-xs-12"
									value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
									list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
									id="ddlTotalUnitDialog" />
							</div>
						</div>
					</div>
					<div class="col-xs-12 bottom-space">
						<div class="row">
							<div class="col-xs-6">
								<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
									id="totalDownloadDialog" placeholder="UNLIMITED DOWNLOAD"
									maxlength="18" onkeypress="return isNaturalInteger(event);" />
							</div>
							<div class="col-xs-6">
								<s:select cssClass="form-control" elementCssClass="col-xs-12"
									value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
									list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
									id="ddlDownloadUnitDialog" />
							</div>
						</div>
					</div>
					<div class="col-xs-12 bottom-space">
						<div class="row">
							<div class="col-xs-6">
								<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
									id="totalUploadDialog" placeholder="UNLIMITED UPLOAD"
									maxlength="18" onkeypress="return isNaturalInteger(event);" />
							</div>
							<div class="col-xs-6">
								<s:select cssClass="form-control" elementCssClass="col-xs-12"
									value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
									list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
									id="ddlUploadUnitDialog" />
							</div>
						</div>
					</div>
				</div>
				<div class="row" style="margin-bottom:20px">
					<div class="col-xs-12">
						<s:label label="Total Time"></s:label>
					</div>
					<div class="col-xs-12 bottom-space">
						<div class="row">
							<div class="col-xs-6">
								<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
									id="totalTimeDialog" maxlength="18" onkeypress="return isNaturalInteger(event);" />
							</div>
							<div class="col-xs-6">
								<s:select cssClass="form-control" elementCssClass="col-xs-12"
									value="%{@com.elitecore.corenetvertex.constants.TimeUnit@DAY.name()}"
										  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR,@com.elitecore.corenetvertex.constants.TimeUnit@DAY:@com.elitecore.corenetvertex.constants.TimeUnit@DAY,@com.elitecore.corenetvertex.constants.TimeUnit@WEEK:@com.elitecore.corenetvertex.constants.TimeUnit@WEEK,@com.elitecore.corenetvertex.constants.TimeUnit@MONTH:@com.elitecore.corenetvertex.constants.TimeUnit@MONTH}"
									id="totalTimeUnitDialog" />
							</div>
						</div>
					</div>
				</div>

			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="btnAdd">
					Add</button>
				<button type="button" class="btn btn-default" data-dismiss="modal"
					id="btnCancel" onclick="clearDialog()">Cancel</button>
			</div>
		</div>
	</div>
</div>