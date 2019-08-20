<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
    table.sorting-table {cursor: move;}
</style>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="product.offer.auto.subscription.manage.order" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="" action="" id="productOfferAutoSubscriptionManageOrderForm" method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10"  >
            <table id="productOfferAutoSubscriptionManageOrderTable" style="width: 100%;" class="table table-blue dataTable no-footer" role="grid" width="100%">
                <thead>
                <tr role="row">
                    <th style="width: 10px"> Order# </th>
                    <th> <s:text name="product.offer.auto.sub.addon.product.offer"/> </th>
                    <th> <s:text name="product.offer.description"/> </th>
                </tr>
                </thead>
                <tbody>
                <s:iterator value="productOfferAutoSubscriptionList" var="productOfferAutoSubscriptionData">
                <tr>
                    <td class="dataTables_empty" > <s:property value="#productOfferAutoSubscriptionData.orderNo"/> </td>
                    <td class="dataTables_empty" >
                        <s:property value="#productOfferAutoSubscriptionData.addOnProductOfferData.name"/>
                        <input type="hidden" name="productOfferAutoSubscriptionIds" value="${productOfferAutoSubscriptionData.id}"/>
                    </td>
                    <td class="dataTables_empty" > <s:property value="#productOfferAutoSubscriptionData.addOnProductOfferData.description"/> </td>
                </tr>
                </s:iterator>
            </table>
        </s:form>
        <div id="autoSubscriptionRowOrder"></div>
        <div class="row">
            <div class="col-xs-12" align="center">
                <button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="submitForm();" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="Product Offer"/>
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Modal HTML -->
<div id="alertFailure" class="modal fade" >
    <div class="modal-dialog">
        <div class="modal-content" style="width: 300px" >
            <div class="modal-body modal-sm">
                <span><s:text name="product.offer.auto.subscription.manage.order.no.change"/></span>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-danger" data-dismiss="modal" ><s:text name="button.ok"/></button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/sort/jquery.rowsorter.js"></script>

<script type="text/javascript" >

    $(document).ready(function(){

        var hasOrderChanged = false;

        function enableDnD(){
            $("#productOfferAutoSubscriptionManageOrderTable").rowSorter({
                onDrop: function(tbody, row, index, oldIndex) {
                    $('#productOfferAutoSubscriptionManageOrderTable tr').each(function() {
                        hasOrderChanged = true;
                    });
                    var headerKey = $(row).children(":eq(1)").text();
                    $("#autoSubscriptionRowOrder").html(headerKey + "Auto Subscription order changed to " + (index + 1)+" from "+(oldIndex + 1));
                }
            });
        };

        enableDnD();

        submitForm = function (){
            if(hasOrderChanged){
                $("#productOfferAutoSubscriptionManageOrderForm").attr("action","${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/manageOrder")
                $( "#productOfferAutoSubscriptionManageOrderForm" ).submit();
            }else{
                $("#alertFailure").modal('show');
            }
        };
    });
</script>
