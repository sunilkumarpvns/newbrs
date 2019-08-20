<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 13/4/17
  Time: 3:48 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="modal fade" id="deleteSubscribers" tabindex="-1" role="dialog" aria-labelledby="deleteSubscriberLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="deleteSubscriberLabel" ><s:text name="subscriber.deletesubscriber" /></h4>
      </div>
      <s:form id="deleteSubscriberForm" action="" cssClass="form-vertical form-group-sm " labelCssClass="col-xs-4" elementCssClass="col-xs-8" validate="true" >
        <s:token />
        <div class="modal-body">
          <div>
            <input type="hidden" name="subscriberId" id="subscriberId">
            <s:select name="updateAction" key="Update Action" cssClass="form-control" list="@com.elitecore.nvsmx.ws.util.UpdateActions@values()" listKey="val()" listValue="label()" id="updateAction" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
          </div>
          <hr id="hrForDeleteModel">
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" data-dismiss="modal" ><s:text name="subscription.close"></s:text> </button>
          <button class="btn btn-primary" name="Subscriber" value="Delete" type="button" onclick="deleteSubscriber()"><s:text name="button.delete" /></button>
        </div>
      </s:form>
    </div>
  </div>
</div>

<script type="text/javascript">
  function callDeleteModel(subscriberIdentity) {
    $("#subscriberId").val(subscriberIdentity);
    $("#deleteSubscribers").modal('show');
  }
  function deleteSubscriber() {
    var subscriberIdentity = $("#subscriberId").val();
    document.forms["deleteSubscriberForm"].action = "markedForDeletion?subscriberIdentity="+encodeURIComponent(subscriberIdentity);
    document.forms["deleteSubscriberForm"].submit();
  }
</script>
