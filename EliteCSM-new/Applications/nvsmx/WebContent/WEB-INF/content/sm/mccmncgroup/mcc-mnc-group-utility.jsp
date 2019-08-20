<script>
    var brandID = null;
    function addNetworkMccMncData() {
        $('.selectMccMncCODE:checked').each(function () {
            $("#networkMccMncListingTableBody").append("<tr>" + $(this).parent().parent().html() + "<td><a style='cursor:pointer'><span onClick='removeTableRow(this);' class='glyphicon glyphicon-trash'/></a></td></tr>");

        });
        $("#networkMccMncListingTableBody").find("td[id='networkCounter']").remove();
        $("#networkMccMncListingTableBody").find("input:checkbox").parent().remove();
        clearDialog();
        beforeSubmit();
    }

    function removeTableRow(this_) {
        $(this_).parent().parent().parent().remove();
    }

    function clearDialog() {
        $('table').find('input:checkbox').prop('checked', false).closest('tr').removeClass('selected');
        removeGeneralErrors();
    }

    function removeGeneralErrors() {
        $(".generalError").removeClass("bg-danger");
        $(".generalError").text("");
        $('#addNetworkMCCMNC').modal('hide');
    }

    function selectBrand() {
        $("#networkMccMncListingTableBody").html("");
    }

    function addingMCCMNCCode() {
        $("#Network-MCC-MNC-Model").html("");
        $("#Network-MCC-MNC-Model").wrap("<div style='position:relative;overflow:auto;height:400px;'/>");
        brandID = $("#selectBrandName option:selected").val();
        if(isNullOrEmpty(brandID)){
            brandID = '<s:property value="brandData.id"/>';
        }
        $.ajax({
            async: true,
            type: "POST",
            dataType: "html",
            url: "${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/networkData",
            data: {"id": brandID},
            success: function (data) {
                console.log("success---> : " + JSON.stringify(data))
                $("#Network-MCC-MNC-Model").html(data);
                $('.selectMccMncCODE').each(function () {
                    var _this = $(this);
                    $("#networkMccMncListingTableBody").find("input:hidden").each(function () {
                        if ($(this).val() == _this.parent().parent().find("input:hidden").val()) {
                            $(_this).prop("disabled", true);
                        }
                    })
                });
            }, error: function (data) {
                console.log("error---> : " + JSON.stringify(data))
            }
        });
    }

    function beforeSubmit(){
        var tableRows = $("#networkMccMncListingTableBody").find("tr");
        for (var i = 0; i < tableRows.length; i++) {
            $($(tableRows[i]).find("input")).attr("name", "networkDatas[" + i + "].id");
        }
    }


</script>
