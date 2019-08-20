<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<script>
$( document ).ready(function() {
    $("#selectAllMccMncCODE").change(function () {
		var checkedProperty = $("#selectAllMccMncCODE").prop('checked');
        $('.selectMccMncCODE').each(function () {
            var _this = $(this);
            if ($(this).attr("disabled") != "disabled") {
                $(_this).prop('checked', checkedProperty);
            }
        });
    });
});
</script>
<table cellpadding="0" cellspacing="0" border="0" id="moduleAction"
	class="table table-blue">
	<thead>
		<tr>
			<th>No.</th>
			<th><input type='checkbox' name='selectAllMccMncCODE' id='selectAllMccMncCODE'/></th>
			<th>Operator Name</th>
			<th>Network Name</th>
			<th>MCC</th>
			<th>MNC</th>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="networkList" status="network" >
		<tr>
			<td hidden><s:hidden name="networkDatas[%{#network.count - 1}].id" id="%{id}" value="%{id}"/><s:property value="id"/></td>
			<td id="networkCounter" align='left' valign='top' style="width: 20px;"><s:property value="%{#network.count}" />&nbsp;&nbsp;</td>
			<td><input type='checkbox' name='selectMccMncCODE' class='selectMccMncCODE'/></td>
			<td><s:property value="operatorData.name"/></td>
			<td><s:property value="name"/></td>
			<td><s:property value="mcc"/></td>
			<td><s:property value="mnc"/></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
