<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<s:form  id="progressBar" method="post" cssClass="form-vertical">
 <div class="modal fade" id="ProgressBarWait" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" align="center">
	<div class="modal-dialog" style="margin:200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title set-title">Processing...</h4>
			</div>
			<div class="modal-body">
				<div class="progress">
					<div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 1000%">
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</s:form>
<script type="text/javascript">
		$(function () {
			document.forms["progressBar"].action = "${pageContext.request.contextPath}/"+"<%=request.getAttribute(Attributes.REDIRECT_URL)%>";
			document.forms["progressBar"].submit();	
			showProgressBar();
			
		});
		function showProgressBar(){
			var pleaseWait = $('#ProgressBarWait');
			$(pleaseWait).modal({backdrop: 'static', keyboard: false});
			showPleaseWait = function() {
				pleaseWait.modal('show');
			}
			showPleaseWait();
		}
</script>