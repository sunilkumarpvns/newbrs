<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 27/11/17
  Time: 2:48 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">

    function isSubscriberIdentityMapped() {
        var isSubscriberIdentityMapped = true ;
        $("tr[name='FieldMappingRow']").each(function () {
            var mappedAttribute = $(this).find("input[type='hidden']").val();
            if('<s:property value="@com.elitecore.corenetvertex.spr.data.SPRFields@SUBSCRIBER_IDENTITY.name()" />' === mappedAttribute) {
                isSubscriberIdentityMapped = true;
                return false
            } else {
                isSubscriberIdentityMapped = false;
            }
        });
        if(isSubscriberIdentityMapped == false) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text("Subscriber Identity required.");
        }
        return isSubscriberIdentityMapped;
    }


    function isProductOfferMapped() {
        var isProductOfferMapped = true ;
        $("tr[name='FieldMappingRow']").each(function () {
            var mappedAttribute = $(this).find("input[type='hidden']").val();
            if('<s:property value="@com.elitecore.corenetvertex.spr.data.SPRFields@PRODUCT_OFFER.name()" />' === mappedAttribute) {
                isProductOfferMapped = true;
                return false
            } else {
                isProductOfferMapped = false;
            }
        });
        if(isProductOfferMapped == false) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text("Product Offer required.");
        }
        return isProductOfferMapped;
    }

    function validateFieldMappings(){
        var fieldMappingTableBodyLength = $("#fieldMappingTable tbody tr").length;
        if(fieldMappingTableBodyLength < 1) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text("<s:text name="sp.interface.field.mapping.required" />");
            return false;
        }
        return true;
    }

</script>
