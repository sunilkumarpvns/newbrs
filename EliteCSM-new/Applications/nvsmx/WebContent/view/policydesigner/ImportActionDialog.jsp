<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<div class="modal fade" id="importEntityAction" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" >
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title set-title">Import Operation</h4>
      </div>
      <div class="modal-body">
        <h5><b><s:text name="button.import.replace" /></b>
          <s:text name="import.action.replace.detail">
            <s:param>
              <s:if test="#entityName == null">
                <s:text name="import.entity"/>
              </s:if>
              <s:else>
                <s:text name="entityName"/>
              </s:else>
            </s:param>
          </s:text>
          <br/>
          <b><s:text name="button.import.fail" /></b>
          <s:text name="import.action.fail.detail">
            <s:param>
              <s:if test="#entityName == null">
                <s:text name="import.entity"/>
              </s:if>
              <s:else>
                <s:text name="entityName"/>
              </s:else>
            </s:param>
          </s:text>
            </h5>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal" id="btnSkip" onclick="return setUserAction(this)"><s:text name="button.import.replace" /></button>
        <button type="button" class="btn btn-primary" data-dismiss="modal" id="btnFail" onclick="return setUserAction(this)"><s:text name="button.import.fail" /></button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="pleaseWaitDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" align="center">
  <div class="modal-dialog" style="margin:200px;">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title set-title"><s:text name="import.operation.progress"/></h4>
      </div>
      <div class="modal-body">
        <div class="progress">
          <div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
          </div>
        </div>
      </div>
    </div>
  </div>
</div>