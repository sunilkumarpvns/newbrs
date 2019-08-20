<%@taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">
function onServerGroupTypeChange(value, hideElementId) {
	var type =  $("#"+value).val();
	if (type == "<s:property value='@com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()'/>") {
		$("#"+hideElementId).hide();
	} else {
		$("#"+hideElementId).show();
	}
}
</script>
