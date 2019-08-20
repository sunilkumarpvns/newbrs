<div class="modal fade" id="packageValidationModal" tabindex="-1" role="dialog" aria-labelledby="packageValidationModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="packageValidationModalTitle" >Warning!!!</h4>
            </div>
            <div class="modal-body">
                <div id="packageValidationWarningMessage" style="color: red;"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal" ><s:text name="button.no"></s:text> </button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="submitForm();" ><s:text name="button.yes"></s:text> </button>
            </div>
        </div>
    </div>
</div>