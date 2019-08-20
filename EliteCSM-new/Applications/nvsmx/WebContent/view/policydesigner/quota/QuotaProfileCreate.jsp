<%@taglib uri="/struts-tags/ec" prefix="s"%>
<html>
<head>

<link rel="stylesheet" href="${pageContext.request.contextPath}/datatables/bootstrap/commons.dataTables.css" />
<script src="${pageContext.request.contextPath}/js/QuotaProfile.js"></script>
<script type="text/javascript">
	var index = 1;
	var fupLevel = 0;
	var fupDiv;
	$(function(){
		fupDiv = $("#hsqFieldSet").clone();
		$("#btnAddFup").on('click',function(){
			addFUP();
		});
	});
</script>
</head>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="quotaprofile.create" />
		</h3>
	</div>


	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/quota/QuotaProfile/create" validate="true"
			id="quotaProfileCreate" method="post" cssClass="form-horizontal" validator="validateForm()">
			<s:token />
			<div class="row">
				<div class="col-xs-12">
					<div class="col-sm-6 col-xs-12">
						<s:hidden name="pkgId" value="%{quotaProfile.pkgData.id}" />
						<s:hidden name="groupIds" value="%{quotaProfile.pkgData.groups}"/>
						<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{quotaProfile.pkgData.groups}"/>
						<s:textfield name="name" key="quotaprofile.name"
							id="quotaProfileName" cssClass="form-control focusElement" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8" 
							 onblur="verifyUniqueness('quotaProfileName','create','','com.elitecore.corenetvertex.pkg.quota.QuotaProfileData','%{pkgId}','');"  />
						<s:textarea name="description" key="quotaprofile.description" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8"
							cssClass="form-control" rows="2" />


						<s:select name="usagePresence" key="quotaprofile.usagepresence" list="@com.elitecore.corenetvertex.constants.CounterPresence@values()" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8" listKey="getVal()" listValue="getDisplayVal()"></s:select>
							<s:select name="balanceLevel" key="quotaprofile.balance.level" list="@com.elitecore.corenetvertex.constants.BalanceLevel@values()" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8" listKey="name()" listValue="getDisplayVal()" id="balanceLevel"/>

						<s:textfield name="renewalInterval" key="quotaprofile.renewal.interval" cssClass="form-control" type="number" id="renewalInterval" labelCssClass="col-xs-4 col-sm-4 text-right"  elementCssClass="col-xs-8 col-sm-8" onkeypress="return isNaturalInteger(event);" />
						<s:select list="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@values()" key="quotaprofile.renewal.interval.unit" name="renewalIntervalUnit" labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8" cssClass="form-control" id="renewalIntervalUnit" listKey="name()" listValue="value()" />
					</div>
				</div>
				<fieldset class="fieldSet-line" id="hsqFieldSet">
					<legend align="top">
						<a data-toggle="collapse" data-target="#hsqDiv" href="#"
							id="hsqDetail"> <span class="caret"></span> <label
							id="fupName" class="defaultLabel" value="HSQ"><s:text name="quotaprofile.hs"/> <s:text name="quotaprofile"/></label>
						</a>
					</legend>
					<div class="row collapse in" id="hsqDiv">
							<div class="col-xs-12">
								<div class="col-xs-3 col-sm-2 " align="right">
									<b><s:text name="quotaprofile.totalquota" /></b>
								</div>
								<div class="col-xs-9 col-sm-10">
									<div class="row">
										<div class="col-xs-7 col-sm-5 ">
											<s:hidden name="quotaProfileDetailDatas[0].fupLevel" value="0"
												id="fupLevel" cssClass="fupLevel" />
											<s:textfield cssClass="form-control" id="hsqTotal"
												name="quotaProfileDetailDatas[0].total" placeholder="UNLIMITED"
												maxlength="18" elementCssClass="col-xs-12" onkeypress="return isNaturalInteger(event);" ></s:textfield>
										</div>
										<div class="col-xs-5 col-sm-4">
											<s:select cssClass="form-control"
												list="@com.elitecore.corenetvertex.constants.DataUnit@values()" value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
												name="quotaProfileDetailDatas[0].totalUnit" />
										</div>
									</div>
									<div class="row">
										<div class="col-xs-7 col-sm-5">
											<s:textfield cssClass="form-control" id="hsqDownload"
												name="quotaProfileDetailDatas[0].download" elementCssClass="col-xs-12"
												placeholder="UNLIMITED DOWNLOAD" maxlength="18" onkeypress="return isNaturalInteger(event);" ></s:textfield>
										</div>
										<div class="col-xs-5 col-sm-4">
											<s:select cssClass="form-control" 
												list="@com.elitecore.corenetvertex.constants.DataUnit@values()" value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
												name="quotaProfileDetailDatas[0].downloadUnit" />
										</div>
									</div>
									<div class="row">
										<div class="col-xs-7 col-sm-5"> 
											<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
												name="quotaProfileDetailDatas[0].upload" id="hsqUpload"
												placeholder="UNLIMITED UPLOAD" maxlength="18" onkeypress="return isNaturalInteger(event);" ></s:textfield>
										</div>
										<div class="col-xs-5 col-sm-4">
											<s:select cssClass="form-control"
												list="@com.elitecore.corenetvertex.constants.DataUnit@values()" value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
												name="quotaProfileDetailDatas[0].uploadUnit" />
										</div>
									</div>

									
								</div>
							</div>
							<div class="col-xs-12">
								<div class="col-xs-3 col-sm-2 " align="right">
									<b><s:text name="quotaprofile.totaltime" /></b>
								</div>
								<div class="col-xs-9 col-sm-10">
									<div class="row">
										<div class="col-xs-7 col-sm-5">
											<s:textfield cssClass="form-control" id="hsqTime" elementCssClass="col-xs-12"
												name="quotaProfileDetailDatas[0].time" maxlength="18" onkeypress="return isNaturalInteger(event);" ></s:textfield>
										</div>
										<div class="col-xs-5 col-sm-4">
											<s:select cssClass="form-control" value="%{@com.elitecore.corenetvertex.constants.TimeUnit@DAY.name()}"
												list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR,@com.elitecore.corenetvertex.constants.TimeUnit@DAY:@com.elitecore.corenetvertex.constants.TimeUnit@DAY,@com.elitecore.corenetvertex.constants.TimeUnit@WEEK:@com.elitecore.corenetvertex.constants.TimeUnit@WEEK,@com.elitecore.corenetvertex.constants.TimeUnit@MONTH:@com.elitecore.corenetvertex.constants.TimeUnit@MONTH}"
												name="quotaProfileDetailDatas[0].timeUnit" />
										</div>
									</div>
								</div>
							</div>
						<!-- Service Wise Restriction -->
						<div id="serviceConfiguration">
							<div class="col-xs-12">
								<table id='tbl0'  class="table table-blue table-bordered">
									<caption class="caption-header">
										<span class="glyphicon glyphicon-check"></span>
										<s:text name="quotaprofile.servicelvlresrictions" />

										<div align="right" class="display-btn">
											<span class="btn btn-group btn-group-xs defaultBtn"
												role="group" 
												 onclick="openDialogBox('tbl0');" 
												id="addRow"> <span class="glyphicon glyphicon-plus"></span>
											</span>
										</div>

									</caption>
									<thead>
										<th><s:text name="quotaprofile.servicetype" /></th>
										<th><s:text name="quotaprofile.aggregationkey" /></th>
										<th style="text-align:right"><s:text name="quotaprofile.totalquota" /></th>
										<th style="text-align:right"><s:text name="quotaprofile.time" /></th>
										<th style="width: 20px; border-right: 0px;"></th>
										<th style="width: 20px;"></th>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</fieldset>
				<div class="box" id="fup1"></div>
				<div class="box" id="fup2"></div>
					<div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" id="fupbutton" >
						<button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" >
							<span class="glyphicon glyphicon-plus-sign"></span><s:text name="fup.level.quota"/>
						</button>
					</div>
			</div>
			<div class="row">
				<div class="col-xs-12" align="center">
					<s:submit cssClass="btn btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button id="btnBack" class="btn btn-sm btn-primary" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${quotaProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
				</div>
			</div>
	</div>
	</s:form>
</div>
<!-- Dialog Box -->
<%@include file="/view/policydesigner/quota/ServiceConfigurationDialog.jsp"%> 
</div>
<script type="text/javascript">
function validateForm(){
	var isValidName = verifyUniquenessOnSubmit('quotaProfileName','create','','com.elitecore.corenetvertex.pkg.quota.QuotaProfileData','<s:text name="pkgId"/>','');
	var isValidBalanceAndFUPLevel =  validateBalanceAndFUPLevel();

	return isValidName && isValidBalanceAndFUPLevel;
}
</script>
</html>