<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<script type="text/javascript">
function validateChildTable(tableId, firstchild, secondChild) {
	var isValidDetailValue = true;
	$("#" + tableId + " tbody tr").each(
			function() {
				var sourceKey = $(this).children().find('.' + firstchild);
				var destinationKey = $(this).children().find('.' + secondChild);
				if (isNullOrEmpty(sourceKey.val())) {
					setErrorOnElement(sourceKey,"<s:text name='error.filemapping.sourcekey'/>");
					isValidDetailValue = false;
				}
				if (isNullOrEmpty(destinationKey.val())) {
					setErrorOnElement(destinationKey,"<s:text name='error.filemapping.destinationkey'/>");
					isValidDetailValue = false;
				}
			});
	if (isValidDetailValue == false) {
		return false;
	} else {
		return true
	}
}

function createTable(rowNum) {
	var tableRow = "<tr name='detailRow'>"
			+ "<td><input maxlength='300'  class='col-xs-12 col-sm-12 form-control source-key' id='sourceKey"+rowNum+"' name='fileMappingDetail["+rowNum+"].sourceKey' type='text'></td>"
			+ "<td><input maxlength='300'  class='col-xs-12 col-sm-12 form-control destination-key' id='destinationKey"+rowNum+"' name='fileMappingDetail["+rowNum+"].destinationKey'  type='text'></td>"
			+ "<td><input maxlength='300'  class='col-xs-12 col-sm-12 form-control value-mapping'  name='fileMappingDetail["+rowNum+"].valueMapping' style='border: 1px solid #ccc;' type='text'></td>"
			+ "<td><input maxlength='300'  class='col-xs-12 col-sm-12 form-control default-value'  name='fileMappingDetail["+rowNum+"].defaultValue' style='border: 1px solid #ccc;' type='text'></td>"
			+ "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"
			+ "</tr>";
	$(tableRow).appendTo('#fileMappingDetailTable');
	autoCompleteForFileMapping("sourceKey"+rowNum);
	autoCompleteForFileMapping("destinationKey"+rowNum);
} 


function autoCompleteForFileMapping(id){
	$('#'+id).autocomplete();
	var list = [ <s:iterator value="@com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants@values()" >
	'<s:property value="name"/>',
	</s:iterator> ];
	commonAutoComplete(id,list);
	}; 

	
</script>

