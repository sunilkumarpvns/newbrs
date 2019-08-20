<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:if test="%{fupLevel == 0}">
				<s:text name="Create "/><s:text name="bod.qos.multiplier.hsqIPCANSessionMultiplier" />
			</s:if>
			<s:elseif test="%{fupLevel == 1}">
				<s:text name="Create "/><s:text name="bod.qos.multiplier.fup1IPCANSessionMultiplier"></s:text>
			</s:elseif>
			<s:elseif test="%{fupLevel == 2}">
				<s:text name="Create "/><s:text name="bod.qos.multiplier.fup2IPCANSessionMultiplier"></s:text>
			</s:elseif>
		</h3>
	</div>
	<div class="panel-body"> 
		<s:form namespace="/pd/bodqosmultiplier" action="bod-qos-multiplier" id="bodQosMultiplierCreate" method="post" cssClass="form-horizontal" validate="true" validator="validateForm()">
			<s:token />
			<div class="row">
				<div class="col-sm-7 col-lg-6">
					<s:hidden name="fupLevel"/>
					<s:hidden name="bodPackageId" value="%{bodPackageId}"/>
					<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{bodData.groups}"/>
					<s:hidden name="groupIds" value="%{bodData.groups}"/>
					<s:textfield tabindex="1" name="multiplier" key="bod.qos.multiplier" cssClass="form-control" type="number" min="1.00" max="10.00" step="0.01"/>
				</div>
			</div>
			<div class="row">
				<fieldset class="fieldSet-line">
					<legend>
						<s:text name="bod.qos.multiplier.servicemultiplier" />
					</legend>
					<div class="row">
						<div class="col-xs-12">
							<table id="serviceMultiplierTable" class="table table-blue table-bordered">
								<div style="text-align: right;margin-bottom: 5px">
									<button type="button" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="addServiceMultiplierDetail();">
										<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
										<s:text name="bod.qos.multiplier.servicemultiplier" />
									</button>
								</div>
								<thead>
									<th><s:text name="bod.qos.multiplier.servicetype" /></th>
									<th><s:text name="bod.service.multiplier" /></th>
									<th style="width: 35px;">&nbsp;</th>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="row">
				<div class="col-xs-12" align="center">					
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/bodpackage/bod-package/${bodPackageId}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.bod" /></button>
				</div>
			</div>
		</s:form>
	</div>
</div>


<table id="tempserviceMultiplierTable" style="display: none;">
	<tbody>
		<tr>
			<td style="width: 50%"><s:select list="serviceTypeDatas" listValue="name" listKey="id" cssClass="form-control" elementCssClass="col-xs-12" /></td>
			<td style="width: 50%"><s:textfield cssClass="form-control" elementCssClass="col-xs-12" type="number" min="1.00" max="10.00" step="0.01" /> </td>
			<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
		</tr>
	</tbody>
</table>
<script>
var i = 0;
function addServiceMultiplierDetail() {
	$("#serviceMultiplierTable tbody").append("<tr>" + $("#tempserviceMultiplierTable").find("tr").html() + "</tr>");
	var NAME = "name";
	$("#serviceMultiplierTable").find("tr:last td:nth-child(1)").find("select").attr(NAME,'bodServiceMultiplierDatas['+i+'].serviceTypeData.id');
	$("#serviceMultiplierTable").find("tr:last td:nth-child(1)").find("select").attr("id",'serviceTypeData'+i);
	$("#serviceMultiplierTable").find("tr:last td:nth-child(2)").find("input").attr(NAME,'bodServiceMultiplierDatas['+i+'].multiplier');
	$("#serviceMultiplierTable").find("tr:last td:nth-child(2)").find("input").attr("id",'serviceMultiplier'+i);
	i++;
}

function validateForm(){
	var isValid = true;
	$("#serviceMultiplierTable").find("tr td:nth-child(2)").find("input").each(function(){
		var value = $(this).val();
		var id = $(this).attr("id");
		if(isNullOrEmpty(value)){
			setError(id,'Multiplier is Required');
			isValid = false;
		}else{ 
			var isDecimalValue = value.indexOf(".");
			if(isDecimalValue != -1){
				var valueParts = value.split(".");
				if(parseInt(valueParts[0]) <  1 || parseInt(valueParts[0]) > 10 || parseFloat(value) > 10 || parseFloat(value) < 1){
					setError(id,'<s:text name="bod.invalid.multiplier"/>');
					isValid = false;
				}
				if(valueParts[1].length > 2 || valueParts.length > 2){
					setError(id,'<s:text name="bod.invalid.multiplier.decimal"/>');
					isValid = false;
				}
			}else{
				if(parseInt(value) <  1 || parseInt(value) > 10){
					setError(id,'<s:text name="bod.invalid.multiplier"/>');
					isValid = false;
				}
			}	
				
		}
	});
	
	//The following selector is checking already defined service types
	var serviceArray = new Array();
	$("#serviceMultiplierTable").find("select").each(function(){
		var serviceTypeValue = $(this).val();
		var serviceTypeId = $(this).attr("id");
		if(serviceArray.indexOf(serviceTypeValue) == -1){
			serviceArray.push(serviceTypeValue);
		}else{
			setError(serviceTypeId,'<s:text name="bod.servicetype.defined.already"/>');
			isValid = false;
		}		
	});
	return isValid;
}
</script>