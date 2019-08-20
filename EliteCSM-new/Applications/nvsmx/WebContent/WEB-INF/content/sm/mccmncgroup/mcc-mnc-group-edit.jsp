<%@ taglib uri="/struts-tags/ec" prefix="s"%>


<style>
#EDIT_MCC_MNC_CODE{
	margin-bottom: 10px;
}
#Edit-Network-MCC-MNC-Model{
	height: 450px;
    overflow-x: overlay;
}
</style>
<script>
$( document ).ready(function() {
	$("#hiddenBrand").val($("#selectBrandName_edit").val());
});

function validate() {
    return verifyUniquenessOnSubmit('name', 'update', '<s:property value="id"/>', 'com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData', '', '');
}

</script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="mccmncgroup.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/mccmncgroup" action="mcc-mnc-group" id="mccmncgroupUpdateForm" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
        validator="validate()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                	<s:textfield name="name" key="mccmncgroup.groupname" cssClass="form-control" id="name" type="text" maxlength="100" tabindex="1" />
                	<s:textarea name="description" key="mccmncgroup.description" cssClass="form-control" id="mccmncgroupDescription" type="text" maxlength="200" tabindex="2" />
                    <s:textfield name="brandData.name" key="mccmncgroup.brand" readonly="true" cssClass="form-control"/>
                    <s:hidden name="brandData.id"/>
                </div>
            </div>  
			<table name="networkDatas" cellpadding="0" cellspacing="0" border="0" id="networkMccMncListingTable" class="table table-blue">
				<caption class="caption-header"><s:text name="mccmncgroup.fieldMapping" />
               		<div align="right" class="display-btn">
                   		<span class="btn btn-group btn-group-xs defaultBtn" data-toggle="modal" data-target="#addNetworkMCCMNC" onClick="addingMCCMNCCode();" id="addRow"> <span class="glyphicon glyphicon-plus"> </span></span>
               		</div>
            	</caption>
				<thead id="networkMccMncListingTableHead">
					<tr>
						<th><s:text name="mccmncgroup.operator"/></th>
						<th><s:text name="mccmncgroup.network"/></th>
						<th><s:text name="mccmncgroup.mcc"/></th>
						<th><s:text name="mccmncgroup.mnc"/></th>
						<th><s:text name="mccmncgroup.remove"/></th>
					</tr>
				</thead>
				<tbody id="networkMccMncListingTableBody">
					<s:iterator value="networkDatas" status="counter">
                        <tr name='FieldMappingRow'>
				            <td hidden><s:hidden name="networkDatas[%{#counter.count - 1}].id" id="" value="%{id}"/><s:property value="id"/></td>
				            <td><s:property value="operatorData.name"/></td>
				            <td><s:property value="name"/></td>
							<td><s:property value="mcc"/></td>
							<td><s:property value="mnc"/></td>
                            <td><a style='cursor:pointer'><span onClick='removeTableRow(this);' class='glyphicon glyphicon-trash'/></a></td>
                        </tr>
                    </s:iterator>
				</tbody>
			</table>             
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/${id}" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back" /> </button>
                </div>
            </div>
        </s:form>
    </div>
</div>
</div>

<div class="modal fade" id="addNetworkMCCMNC" tabindex="-1" role="dialog" aria-labelledby="editSubscriptionLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="clearDialog();">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="editSubscriptionLabel"><s:text name="mccmncgroup.addingCode"/></h4>
      </div>
       	<div class="modal-body" id="Network-MCC-MNC-Model">
       	
 		</div>
     	<div class="col-xs-12">
        <div class="col-xs-12 generalError" ></div>
     	</div>
        <div class="modal-footer">
          <button class="btn btn-primary" onClick="addNetworkMccMncData();" name="Add" value="Add" type="button">Add</button>
          <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="clearDialog();" >Close</button>
        </div>
    </div></div></div>
<%@include file="mcc-mnc-group-utility.jsp"%>