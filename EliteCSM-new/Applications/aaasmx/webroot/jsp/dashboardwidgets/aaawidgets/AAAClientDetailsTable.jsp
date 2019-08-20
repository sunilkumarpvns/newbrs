<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<div id="radAuthClient" style="overflow: auto; ">
</div>

<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditAuthServiceTableWidget.jsp" id="editJsp">

<script>
var interval = <%=request.getParameter("interval") != null ? request.getParameter("interval") : 1%>;
var parentID = "radAuthClient_" + $('#radAuthClient').parent().parent().attr("id");
$('#radAuthClient').attr("id", parentID);
var data = {
		header : {
			id : parentID,
			type : "RAD_AUTH_CLIENT"
		},
		body : {
			interval : interval
		}
};
getDashBoardSocket().sendRequest(data);
getDashBoardSocket().register($("#"+parentID).createDefaultTableWidget(interval));

</script>
