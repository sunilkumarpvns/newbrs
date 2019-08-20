<%@page import="com.elitecore.corenetvertex.constants.AggregationKey"%>
<%@page import="com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>

<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<% 
		List<QuotaProfileDetailData> serviceLevelQuotaProfileDetails = (ArrayList<QuotaProfileDetailData>)request.getAttribute("serviceLevelDataForQuotaProfileDetails");

%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/datatables/bootstrap/commons.dataTables.css" />
<script src="${pageContext.request.contextPath}/js/QuotaProfile.js"></script>
<script type="text/javascript">

	
	var index = 3;
	var fupLevel = 0;
	var fupDiv;
	var fup = 0;
	$(function(){
		fupDiv = $("#hsqFieldSet").clone();
		fupLevel = fup;
		$("#btnAddFup").attr("onclick", "addFUP()");
		if(fup == "2"){
			$("#btnAddFup").attr("disabled", "disabled");
		}
		<%
		for(QuotaProfileDetailData quotaDetail : serviceLevelQuotaProfileDetails){
		%>
		
			var id= '<%= quotaDetail.getId() %>';
			var fupLevelVal ='<%= quotaDetail.getFupLevel() %>';
			var serviceTypeName = '<%= quotaDetail.getServiceId() %>';
			var serviceTypeId = '<%= quotaDetail.getDataServiceTypeData().getId() %>';
			var  aggregationkey = '<%= quotaDetail.getAggregationKey() %>';
			var  total = '<%= quotaDetail.getTotal() %>';
			var  totalUnit = '<%= quotaDetail.getTotalUnit() %>';
			var  upload = '<%= quotaDetail.getUpload() %>';
			var  uploadUnit = '<%= quotaDetail.getUploadUnit() %>';
			var  download = '<%= quotaDetail.getDownload() %>';
			var  downloadUnit = '<%= quotaDetail.getDownloadUnit() %>';
			var  time = '<%= quotaDetail.getTime() %>';
			var  timeUnit = '<%= quotaDetail.getTimeUnit() %>';
			var aggregationkeyData = '<%= AggregationKey.valueOf(quotaDetail.getAggregationKey()).getVal()%>';
			if(fupLevelVal == "2"){
				$("#btnAddFup").attr("disabled", "disabled");
			}
			var tableId = "tbl"+fupLevelVal;
			setRowData(tableId,id,fupLevelVal,serviceTypeId,serviceTypeName,aggregationkey,aggregationkeyData,total,totalUnit,upload,uploadUnit,download,downloadUnit,time,timeUnit);
			index++;
			<%
		}
		

		%>
	});

	function setRowData(tableid,id,fupLevel,serviceTypeId,serviceTypeName,aggregationKey,aggregationkeyData,total,totalUnit,upload,uploadUnit,download,downloadUnit,time,timeUnit){
		var rowId = tableid + "_row" + index;
		var counts = index;
			var fuplvl = fupLevel;
			fuplvl = fupLevel;

			var arrowUpId = "arrowUp" + index;
			var arrowDownId = "arrowDown" + index;
			var serviceDataName = "quotaProfileDetailDatas";
			var fupLevelId = serviceDataName + "[" + counts + "].fupLevel";
			var quotaProfileDetailId = serviceDataName + "[" + counts + "].id";
			var serviceId = serviceDataName + "[" + counts + "].serviceId";
			var aggregationKeyId = serviceDataName + "[" + counts + "].aggregationKey";
			var totalQuotaId = serviceDataName + "[" + counts + "].total";
			var totalQuotaUnitId = serviceDataName + "[" + counts + "].totalUnit";
			var totalUploadId = serviceDataName + "[" + counts + "].upload";
			var totalUploadUnitId = serviceDataName + "[" + counts + "].uploadUnit";
			var totalDownloadId = serviceDataName + "[" + counts + "].download";
			var totalDownloadUnitId = serviceDataName + "[" + counts + "].downloadUnit";
			var totalTimeId = serviceDataName + "[" + counts + "].time";
			var totalTimeUnitId = serviceDataName + "[" + counts + "].timeUnit";

			var totalQuotaDetailId = "totalData" + counts;
			var totalUploadIdForSpan = "uploadData" + counts;
			var totalDownloadIdForSpan = "downloadData" + counts;

			var rowStr = rowId.toString();
			var serviceDataId = rowStr + "_service";
			var aggregationKeyDataId = rowStr + "_aggregationKey";
			var totalId = rowStr + "_total";
			var totalUnitDataId = rowStr + "_totalUnit";
			var uploadId = rowStr + "_upload";
			var uploadUnitDataId = rowStr + "_uploadUnit";
			var downloadId = rowStr + "_download";
			var downloadUnitId = rowStr + "_downloadUnit";
			var timeId = rowStr + "_time";
			var timeUnitId = rowStr + "_timeUnit";
			$("#" + tableid).append('<tr id='+rowId+' ><input type="hidden"  name='+fupLevelId+' value='+fuplvl+'><td style="text-align:left"><input type="hidden"  name='+serviceId+' id='+serviceDataId+' >'
									+ '<label id='+rowId+'lbl0 class="defaultLabel dataServiceType"></label>'
									+ '</td><td><input type="hidden"  name='+aggregationKeyId+' id='+aggregationKeyDataId+'>'
									+ '<label id='+rowId+'lbl1 class="defaultLabel aggregationKey"></label>'
									+ '</td><td style="text-align:right">'
									+ '<input type="hidden" name='+totalQuotaId+'  id='+totalId+'>'
									+ '<input type="hidden" name='+totalQuotaUnitId+'  id='+totalUnitDataId+'>'
									+ '<span class="displayText" id='+totalQuotaDetailId+'>'
									+ '<label id='+rowId+'lbl2 class="defaultLabel" ></label>'
									+ ' '
									+ '<label id='+rowId+'lbl3 class="defaultLabel" ></label>'
									+ ' '
									+ '</span>'
									+ '<input type="hidden" name='+totalDownloadId+' id='+downloadId+'>'
									+ '<input type="hidden" name='+totalDownloadUnitId+' id='+downloadUnitId+'>'
									+ '<span class="displayText" id='+totalDownloadIdForSpan+'>'
									+ '<label id='+rowId+'lbl4 class="defaultLabel" ></label>'
									+ '  '
									+ '<label id='+rowId+'lbl5 class="defaultLabel" ></label>'
									+ '<span class="glyphicon glyphicon-arrow-down up-down-arrow"  id='+arrowDownId+'></span>'
									+ '</span>'
									+ '<input type="hidden" name='+totalUploadId+' id='+uploadId+'>'
									+ '<input type="hidden" name='+totalUploadUnitId+'  id='+uploadUnitDataId+'>'
									+ '<span class="displayText" id='+totalUploadIdForSpan+'><label id='+rowId+'lbl6 class="defaultLabel" ></label>'
									+ ' '
									+ '<label id='+rowId+'lbl7 class="defaultLabel"></label>'
									+ '<span class="glyphicon glyphicon-arrow-up up-down-arrow"  id='+arrowUpId+'></span>'
									+ '  '
									+ '</span>'
									+ '</td><td style="text-align:right" >'
									+ '<label id='+rowId+'lbl8 class="defaultLabel" ></label>'
									+ ' '
									+ '<input type="hidden" name='+totalTimeId+'  id='+timeId+'>'
									+ '<input type="hidden" name='+totalTimeUnitId+' id='+timeUnitId+'>'
									+ '<label id='+rowId+'lbl9 class="defaultLabel" ></label>'
									+ '</td><td><span class="btn defaultBtn"><a><span class="glyphicon glyphicon-pencil" title="edit" onclick=addRow("'
									+ tableid
									+ '","'
									+ index
									+ '");></span></a></span></td>'
									+ '<td><span onclick=removeRow("'+tableid+'",this,'+index+')> <a><span class="glyphicon glyphicon-trash" title="delete"></span></a></span></td></tr>');
		setData(rowId, serviceTypeId,serviceTypeName,aggregationKey,aggregationkeyData,total,totalUnit,upload,uploadUnit,download,downloadUnit,time,timeUnit);
		}
		
		function setData(rowId,serviceTypeId, serviceTypeName,aggregationkey,aggregationkeyData,total,totalUnit,upload,uploadUnit,download,downloadUnit,time,timeUnit){
			var serviceDataId = rowId + "_service";
			var aggregationKeyDataId = rowId + "_aggregationKey";
			var totalId = rowId + "_total";
			var totalUnitDataId = rowId + "_totalUnit";
			var uploadId = rowId + "_upload";
			var uploadUnitDataId = rowId + "_uploadUnit";
			var downloadId = rowId + "_download";
			var downloadUnitId = rowId + "_downloadUnit";
			var timeId = rowId + "_time";
			var timeUnitId = rowId + "_timeUnit";
			$("#" + serviceDataId).val(serviceTypeId);
			$("#" + rowId + "lbl0").val(serviceTypeName);
			$("#" + rowId + "lbl0").text(serviceTypeName);
			$("#" + aggregationKeyDataId).val(aggregationkey);
			$("#" + rowId + "lbl1").val(aggregationkey);
			$("#" + rowId + "lbl1").text(aggregationkeyData);
			if(isNullOrEmpty(total)){
				$("#" + totalId).val("");
				$("#" + totalUnitDataId).val("");
				$("#" + rowId + "lbl2").val("");
				$("#" + rowId + "lbl3").val("");
				$("#" + rowId + "lbl2").text("");
				$("#" + rowId + "lbl3").text("");
			}else{
				$("#" + totalId).val(total);
				$("#" + totalUnitDataId).val(totalUnit);
				$("#" + rowId + "lbl2").val(total);
				$("#" + rowId + "lbl3").val(totalUnit);
				$("#" + rowId + "lbl2").text(total);
				$("#" + rowId + "lbl3").text(totalUnit);
			}
			if(isNullOrEmpty(upload)){
				$("#" + uploadId).val("");
				$("#" + uploadUnitDataId).val("");
				$("#" + rowId + "lbl6").val("");
				$("#" + rowId + "lbl6").text("");
				$("#" + rowId + "lbl7").val("");
				$("#" + rowId + "lbl7").text("");
			}else{
				$("#" + uploadId).val(upload);
				$("#" + uploadUnitDataId).val(uploadUnit);
				$("#" + rowId + "lbl6").val(upload);
				$("#" + rowId + "lbl6").text(upload);
				$("#" + rowId + "lbl7").val(uploadUnit);
				$("#" + rowId + "lbl7").text(uploadUnit);
			}
			if(isNullOrEmpty(download)){
				$("#" + downloadId).val("");
				$("#" + downloadUnitId).val("");
				$("#" + rowId + "lbl4").val("");
				$("#" + rowId + "lbl4").text("");
				$("#" + rowId + "lbl5").val("");
				$("#" + rowId + "lbl5").text("");
			}else{
				$("#" + downloadId).val(download);
				$("#" + downloadUnitId).val(downloadUnit);
				$("#" + rowId + "lbl4").val(download);
				$("#" + rowId + "lbl5").val(downloadUnit);
				$("#" + rowId + "lbl4").text(download);
				$("#" + rowId + "lbl5").text(downloadUnit);
			}
			if(isNullOrEmpty(time)){
				$("#" + timeId).val("");
				$("#" + timeUnitId).val("");
				$("#" + rowId + "lbl8").val("");
				$("#" + rowId + "lbl9").val("");
				$("#" + rowId + "lbl8").text("");
				$("#" + rowId + "lbl9").text("");
			}else{
				$("#" + timeId).val(time);
				$("#" + timeUnitId).val(timeUnit);
				$("#" + rowId + "lbl8").val(time);
				$("#" + rowId + "lbl9").val(timeUnit);
				$("#" + rowId + "lbl8").text(time);
				$("#" + rowId + "lbl9").text(timeUnit);
			}

			var tds = $("#" + rowId).find('td');
			var span = $(tds).find('span[class="displayText"]');
			var innerSpan = $(span).find('span');
			if(isNullOrEmpty($("#" + totalId).val()) == true){
				$(span.eq(0)).attr('style', 'display:none');
			}else{
				$(span.eq(0)).removeAttr('style', 'display:none');
			}
			if(isNullOrEmpty($("#" + downloadId).val()) == true){
				$(span.eq(1)).attr('style', 'display:none');
				$(innerSpan.eq(0)).attr('style', 'display:none');
			}else{
				$(span.eq(1)).removeAttr('style', 'display:none');
				$(innerSpan.eq(0)).removeAttr('style', 'display:none');
			}
			if(isNullOrEmpty($("#" + uploadId).val()) == true){
				$(span.eq(2)).attr('style', 'display:none');
				$(innerSpan.eq(1)).attr('style', 'display:none');
			}else{
				$(span.eq(2)).removeAttr('style', 'display:none');
				$(innerSpan.eq(1)).removeAttr('style', 'display:none');
			}
			
		}
</script>
</head>
<div class="panel panel-primary">

	<div class="panel-heading">

		<h3 class="panel-title">
			<s:text name="quotaprofile.update" />
		</h3>
	</div>
	
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/quota/QuotaProfile/update"
			id="quotaProfileUpdate" method="post" cssClass="form-horizontal" validate="true" validator="validateForm()">
			<div class="row">
				<div class="col-xs-12">
					<div class="col-sm-6 col-xs-12">
						<s:hidden name="quotaProfile.id" />
						<s:hidden name="quotaProfile.pkgData.id" />
						<s:hidden name="groupIds" value="%{quotaProfile.pkgData.groups}"/>
						<s:hidden name="entityOldGroups" value="%{quotaProfile.pkgData.groups}"/>
						<s:textfield name="quotaProfile.name" key="quotaprofile.name" 
							id="quotaProfileName" cssClass="form-control focusElement" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8" 
							onblur="verifyUniqueness('quotaProfileName','update','%{quotaProfile.id}','com.elitecore.corenetvertex.pkg.quota.QuotaProfileData','%{quotaProfile.pkgData.id}','');"/>
						<s:textarea name="quotaProfile.description" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8"
							key="quotaprofile.description" cssClass="form-control" rows="2" />
						<s:select name="quotaProfile.usagePresence" key="quotaprofile.usagepresence" list="@com.elitecore.corenetvertex.constants.CounterPresence@values()" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8" listKey="getVal()" listValue="getDisplayVal()"></s:select>
						<s:select name="quotaProfile.balanceLevel" key="quotaprofile.balance.level" list="@com.elitecore.corenetvertex.constants.BalanceLevel@values()" labelCssClass="col-xs-3 col-sm-4 text-right" elementCssClass="col-xs-9 col-sm-8" listKey="name()" listValue="getDisplayVal()" id="balanceLevel" />
						<s:textfield name="quotaProfile.renewalInterval" key="quotaprofile.renewal.interval" cssClass="form-control" type="number" id="renewalInterval" labelCssClass="col-xs-4 col-sm-4 text-right"  elementCssClass="col-xs-8 col-sm-8" onkeypress="return isNaturalInteger(event);" />
						<s:select list="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@values()" key="quotaprofile.renewal.interval.unit" name="quotaProfile.renewalIntervalUnit" labelCssClass="col-xs-4 col-sm-4 text-right" elementCssClass="col-xs-8 col-sm-8" cssClass="form-control" id="renewalIntervalUnit" listKey="name()" listValue="value()" />
					</div>
				</div>
				<s:iterator value="quotaProfile.fupLevelMap">
					<fieldset class="fieldSet-line" id="hsqFieldSet">
						<s:if test="key == 0">
							<legend>
								<a data-toggle="collapse" data-target="#fupLevels${key}"
									href="#" id="hsqDetail"> <span class="caret"></span> <label
									id="fupName" class="defaultLabel" value="HSQ"><s:text name="quotaprofile.hs"/> <s:text name="quotaprofile"/></label>
								</a>
							</legend>
						</s:if>
						<s:else>
							<script type="text/javascript">
							fup = ${key};
							</script>
							<legend>
								<a data-toggle="collapse" data-target="#fupLevels${key}"
									href="#" id="fup${key}"> <span class="caret"></span>
									<label id="fupName" class="defaultLabel" value="FUP"><s:text name="quotaprofile.fup"/>-<s:property value="key" /> <s:text name="quotaprofile"/></label>
								</a>
							</legend>
						</s:else>
						<div class="row collapse in" id="fupLevels${key}">
								<div class="col-xs-12">
									<div class="col-xs-3 col-sm-2" align="right">
										<b><s:text name="quotaprofile.totalquota" /></b>
									</div>
									<div class="col-xs-9 col-sm-10">
										<div class="row">
											<div class="col-xs-7 col-sm-5">
												<s:hidden name="quotaProfile.quotaProfileDetailDatas[%{key}].fupLevel"
													 id="fupLevel" cssClass="fupLevel"/>
												<s:textfield cssClass="form-control" id="hsqTotal" elementCssClass="col-xs-12"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].total"
													placeholder="UNLIMITED" maxlength="18" onkeypress="return isNaturalInteger(event);" ></s:textfield>
											</div>
											<div class="col-xs-5 col-sm-4">
												<s:select cssClass="form-control"
													list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].totalUnit" />
											</div>
										</div>
										<div class="row">
											<div class="col-xs-7 col-sm-5">
												<s:textfield cssClass="form-control"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].upload" elementCssClass="col-xs-12"
													id="hsqUpload" placeholder="UNLIMITED UPLOAD"
													maxlength="18" onkeypress="return isNaturalInteger(event);" ></s:textfield>
											</div>
											<div class="col-xs-5 col-sm-4">
												<s:select cssClass="form-control"
													list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].uploadUnit" />
											</div>
										</div>

										<div class="row">
											<div class="col-xs-7 col-sm-5">
												<s:textfield cssClass="form-control" id="hsqDownload" elementCssClass="col-xs-12"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].download"
													placeholder="UNLIMITED DOWNLOAD" maxlength="18" onkeypress="return isNaturalInteger(event);" ></s:textfield>
											</div>
											<div class="col-xs-5 col-sm-4">
												<s:select cssClass="form-control"
													list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].downloadUnit" />
											</div>
										</div>
									</div>
								</div>

								<div class="col-xs-12">
									<div class="col-xs-3 col-sm-2" align="right">
										<b><s:text name="quotaprofile.totaltime" /></b>
									</div>
									<div class="col-xs-9 col-sm-10">
										<div class="row">
											<div class="col-xs-7 col-sm-5">
												<s:textfield cssClass="form-control" id="hsqTime" elementCssClass="col-xs-12"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].time"
													maxlength="18" onkeypress="return isNaturalInteger(event);" ></s:textfield>
											</div>
											<div class="col-xs-5 col-sm-4">
												<s:select cssClass="form-control"
														  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR,@com.elitecore.corenetvertex.constants.TimeUnit@DAY:@com.elitecore.corenetvertex.constants.TimeUnit@DAY,@com.elitecore.corenetvertex.constants.TimeUnit@WEEK:@com.elitecore.corenetvertex.constants.TimeUnit@WEEK,@com.elitecore.corenetvertex.constants.TimeUnit@MONTH:@com.elitecore.corenetvertex.constants.TimeUnit@MONTH}"
													name="quotaProfile.quotaProfileDetailDatas[%{key}].timeUnit" />
											</div>
										</div>
									</div>
								</div>

							<div id="serviceConfiguration">
							<div class="col-xs-12">
							<table  id="tbl${key}" class="table table-blue table-bordered">
									<caption class="caption-header">
										<span class="glyphicon glyphicon-check"></span>
										<s:text name="quotaprofile.servicelvlresrictions" />

										<div align="right" class="display-btn">
										 	<s:if test="%{quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name() || quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}">
											<span class="btn btn-group btn-group-xs defaultBtn" 
												role="group"  onclick="openDialogBox('tbl${key}');"
												id="addRow"> <span class="glyphicon glyphicon-plus"></span>
											</span>
											</s:if>
											<s:else>
											<span class="btn btn-group btn-group-xs defaultBtn" disabled="true"
												role="group" data-target="#serviceDialog"
												data-toggle="modal" onclick="openDialogBox('tbl${key}');"
												id="addRow"> <span class="glyphicon glyphicon-plus"></span>
											</span>
											</s:else>
										</div>

									</caption>
									<thead>
										<th><s:text name="quotaprofile.servicetype" /></th>
										<th><s:text name="quotaprofile.aggregationkey" /></th>
										<th style="text-align:right"><s:text name="quotaprofile.totalquota" /></th>
										<th style="text-align:right"><s:text name="quotaprofile.time"  /></th>
										<th style="width: 20px; border-right: 0px;"></th>
										<th style="width: 20px;"></th>
									</thead>
								</table>
							</div>
							</div></div>
					</fieldset>
				</s:iterator>
				<div class="box" id="fup1"></div>
				<div class="box" id="fup2"></div>
				<div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" id="fupbutton">
						<s:if test="%{quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name() || quotaProfile.pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}">
							<button type="button" class="btn btn-xs btn-primary" id="btnAddFup"
								data-target="fup" onclick="addFUP();">
								<span class="glyphicon glyphicon-plus-sign"></span><s:text name="fup.level.quota"/>
							</button>
						</s:if>
						<s:else>
							<button type="button" class="btn btn-xs btn-primary" id="btnAddFup" disabled="disabled"
								data-target="fup" onclick="addFUP();">
								<span class="glyphicon glyphicon-plus-sign"></span><s:text name="fup.level.quota"/>
							</button>
						</s:else>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12" align="center">
 					<s:submit cssClass="btn btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button id="btnBack" class="btn btn-sm btn-primary" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/view?quotaProfileId=${quotaProfile.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.quotaprofile"></s:text> </button>
					<button id="btnBack" class="btn btn-sm btn-primary" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${quotaProfile.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
				</div>
			</div>
			
		</s:form>
	</div>
</div>
<!-- Dialog Box -->
<%@include file="/view/policydesigner/quota/ServiceConfigurationDialog.jsp"%>
<script type="text/javascript">
function validateForm(){
	var  isValidName = verifyUniquenessOnSubmit('quotaProfileName','update','<s:text name="quotaProfile.id" />','com.elitecore.corenetvertex.pkg.quota.QuotaProfileData','<s:text name= "quotaProfile.pkgData.id" />','');
	var isValidBalanceAndFUPLevel =  validateBalanceAndFUPLevel();
	return isValidName && isValidBalanceAndFUPLevel;
}

</script>
</html>