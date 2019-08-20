<%@page import="com.elitecore.corenetvertex.constants.CommonConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="sy.quotaprofile.update" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/"  action="" method="post" cssClass="form-horizontal" validate="true" onsubmit="return validateForm()" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
			<s:token/>
			<div class="row">
				<div class="col-sm-9 col-lg-7">
					<s:hidden name="syQuotaProfileData.id" />
					<s:hidden name="groupIds" value="%{syQuotaProfileData.pkgData.groups}"/>
					<s:hidden name="entityOldGroups" value="%{syQuotaProfileData.pkgData.groups}"/>
					<s:hidden id="syQuotaProfileDetailDatasSize" value="%{syQuotaProfileData.syQuotaProfileDetailDatas.size}"/>
					<s:textfield 	name="syQuotaProfileData.name" 		key="sy.quotaprofile.name" 	id="syQuotaProfileName"		cssClass="form-control focusElement"
						onblur="verifyUniqueness('syQuotaProfileName','update','%{syQuotaProfileData.id}','com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData','%{syQuotaProfileData.pkgData.id}','');"  />
					<s:textarea name="syQuotaProfileData.description" 	key="sy.quotaprofile.description" cssClass="form-control" rows="2" />
				</div>
			</div>
			<div  class="row">
				<div id="syQuotaProfileDetailData">
					<div class="col-xs-12">
						<table id='syServiceWiseTable'  class="table table-blue table-bordered">
							<caption class="caption-header">
								<s:text name="sy.service.wise.counter.definition"/>
								<div align="right" class="display-btn">
									<span class="btn btn-group btn-group-xs defaultBtn" onclick="addSyQuotaProfileDetail();" id="addRow"> <span class="glyphicon glyphicon-plus"/></span>
								</div>
							</caption>
							<thead>
								<tr>
									<th><s:text name="sy.quotaprofile.detail.servicetype" /></th>
									<th><s:text name="sy.quotaprofile.detail.countername" /></th>
									<th><s:text name="sy.quotaprofile.detail.hsqvalue" /></th>
									<th><s:text name="sy.quotaprofile.detail.fup1value" /></th>
									<th><s:text name="sy.quotaprofile.detail.fup2value" /></th>
									<th style='width: 15%;'><s:text name="sy.quotaprofile.detail.counterpresent" /></th>
									<th style="width: 35px;">&nbsp;</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="syQuotaProfileData.syQuotaProfileDetailDatas" status="i" var="syQuotaProfileDetailDatas">
									<tr>
										<td><s:select value="%{#syQuotaProfileDetailDatas.dataServiceTypeData.id}" name="syQuotaProfileDetailDatas[%{#i.count-1}].dataServiceTypeData.id" list="dataServiceTypeData" listValue="name" listKey="id" cssClass="form-control" elementCssClass="col-xs-12" ></s:select> </td>
										<td><s:textfield value="%{#syQuotaProfileDetailDatas.counterName}" name="syQuotaProfileDetailDatas[%{#i.count-1}].counterName" cssClass="form-control" elementCssClass="col-xs-12"/> </td>
										<td><s:textfield value="%{#syQuotaProfileDetailDatas.hsqValue}" name="syQuotaProfileDetailDatas[%{#i.count-1}].hsqValue" cssClass="form-control" elementCssClass="col-xs-12" placeholder="Any" /> </td>
										<td><s:textfield value="%{#syQuotaProfileDetailDatas.fup1Value}" name="syQuotaProfileDetailDatas[%{#i.count-1}].fup1Value" cssClass="form-control" elementCssClass="col-xs-12" placeholder="Any"/> </td>
										<td><s:textfield value="%{#syQuotaProfileDetailDatas.fup2Value}" name="syQuotaProfileDetailDatas[%{#i.count-1}].fup2Value" cssClass="form-control" elementCssClass="col-xs-12" placeholder="Any"/> </td>
										<td><s:select value="%{#syQuotaProfileDetailDatas.counterPresent}" name="syQuotaProfileDetailDatas[%{#i.count-1}].counterPresent" elementCssClass="col-xs-12" list="@com.elitecore.corenetvertex.constants.CounterPresence@values()" listValue="getDisplayVal()" listKey="getVal()"/></td>
										<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
									</tr>
								
								</s:iterator>
							
							</tbody>
							
							
							
						</table>
					</div>
				</div>
			</div>
			<div class="col-xs-12" id="generalError"></div>
			<div class="row">
				<div class="col-xs-12" align="center">
					<button class="btn btn-primary btn-sm" type="submit" role="submit" formaction="update" id="btnUpdate">
						<span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
					</button>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${syQuotaProfileData.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg" /></button>
				</div>
			</div>
		</s:form>
	</div>
</div>


<table id="tempsyServiceWiseTable" style="display: none;">
	<tr>
		<td><s:select list="dataServiceTypeData" listValue="name" listKey="id" cssClass="form-control" elementCssClass="col-xs-12"></s:select></td>
		<td><s:textfield cssClass="form-control" elementCssClass="col-xs-12"/> </td>
		<td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" placeholder="Any" /> </td>
		<td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" placeholder="Any" /> </td>
		<td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" placeholder="Any" /> </td>
		<td><s:select elementCssClass="col-xs-12" list="@com.elitecore.corenetvertex.constants.CounterPresence@values()" listValue="getDisplayVal()" listKey="getVal()" value="0"/></td>
		<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
	</tr>
</table>
<script>
var i = $("#syQuotaProfileDetailDatasSize").val();
function addSyQuotaProfileDetail() {
	$("#syServiceWiseTable tbody").append("<tr>" + $("#tempsyServiceWiseTable").find("tr").html() + "</tr>");
	
	var NAME = "name";
	$("#syServiceWiseTable").find("tr:last td:nth-child(1)").find("select").attr(NAME,'syQuotaProfileDetailDatas['+i+'].dataServiceTypeData.id');
	$("#syServiceWiseTable").find("tr:last td:nth-child(2)").find("input").attr(NAME,'syQuotaProfileDetailDatas['+i+'].counterName');
	$("#syServiceWiseTable").find("tr:last td:nth-child(3)").find("input").attr(NAME,'syQuotaProfileDetailDatas['+i+'].hsqValue');
	$("#syServiceWiseTable").find("tr:last td:nth-child(3)").find("input").attr("id",'hsqValue'+i);
	$("#syServiceWiseTable").find("tr:last td:nth-child(4)").find("input").attr(NAME,'syQuotaProfileDetailDatas['+i+'].fup1Value');
	$("#syServiceWiseTable").find("tr:last td:nth-child(5)").find("input").attr(NAME,'syQuotaProfileDetailDatas['+i+'].fup2Value');
	$("#syServiceWiseTable").find("tr:last td:nth-child(6)").find("select").attr(NAME,'syQuotaProfileDetailDatas['+i+'].counterPresent');
	i++;
}
function validateForm(){
	var isValidName = verifyUniquenessOnSubmit('syQuotaProfileName','update','<s:text name="syQuotaProfileData.id" />','com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData','<s:text name= "syQuotaProfileData.pkgData.id" />','');
	var isAllServiceDefined = false;
	$("[name*='dataServiceTypeData.id']").each(function(){
		var serviceTypeValue = $(this).val();
		if(serviceTypeValue == '<%=CommonConstants.ALL_SERVICE_ID%>'){
			isAllServiceDefined = true;
			return false;
		}
	});
	if(isAllServiceDefined == false){
		$("#generalError").addClass("bg-danger");
		$("#generalError").text("Data Service Type \"All-Service\" must be defined");
	}
   	return (isValidName && isAllServiceDefined);
}
</script>

