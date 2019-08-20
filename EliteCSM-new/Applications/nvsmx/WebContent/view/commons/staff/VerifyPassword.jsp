<%@taglib uri="/struts-tags/ec" prefix="s"%>
	
	<div class="modal fade" id="verification-modal" role="dialog" aria-labelledby="verifyStaffLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="verifyStaffLabel"> <s:text name="staff.password.verification"></s:text> </h4>
					</div>
					<div class="modal-body">
						<s:password cssClass="form-control text-input" name="password" id="password" label="Enter your password" tabindex="1" autofocus="autofocus" ></s:password>
						
					</div>
					<div class="modal-footer">
						<s:submit cssClass="btn btn-primary" name="update" value="OK"  tabindex="2"></s:submit>
						<button type="button" class="btn btn-primary" data-dismiss="modal" tabindex="3"><s:text name="staff.cancel"></s:text> </button>
					</div>
					
				</div>
    		</div>
	</div>
<script type="text/javascript">
	$(document).ready(function(){
		$('#password').attr("autocomplete", "off");
		setTimeout('$("#password").val("");', 1000);
	});

</script>