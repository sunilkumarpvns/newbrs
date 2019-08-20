<s:form namespace="/pd/productoffer" action="product-offer" id="productOfferCloningForm" method="post" cssClass="form-horizontal" validate="false" >
  <s:token />
  <s:hidden name="_method" id="method" />
<div class="modal" id="productOfferCloningDialog" tabindex="-1" role="dialog" aria-labelledby="serviceDialog" aria-hidden="true"  >

  <div class="modal-dialog" >

    <div class="modal-content" >

      <div class="modal-header" >
        <button type="button" class="close" data-dismiss="modal"
                aria-label="Close" onclick="clearDialog()">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title set-title" id="serviceDialogTitle">
            <s:text name="product.offer.duplicate"/>
        </h4>
      </div>

      <div class="modal-body" style="padding-top: 15px !important; padding-bottom: 0px !important;" >
          <s:hidden id="productOfferNotificationId" name="id"/>
          <s:hidden id="groups" name="groups"/>
          <div class="row">
              <div class="col-xs-12">
                  <s:textfield name="name" id="duplicateEntityName" cssClass="form-control" key="product.offer.name"/>
              </div>
          </div>
      </div>

      <div class="modal-footer">
          <div class="col-xs-12">
              <button class="btn btn-primary btn-sm" type="button" id="btnUpdate" onclick="validateDuplicate();">
                  <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
              </button>
              <button type="button" class="btn btn-primary" data-dismiss="modal" id="btnCancel" onclick="clearDialog()">
                  Cancel
              </button>
          </div>
      </div>

    </div>
  </div>
</div>
</s:form>
<%@include file="productoffer-duplicateutility.jsp"%>