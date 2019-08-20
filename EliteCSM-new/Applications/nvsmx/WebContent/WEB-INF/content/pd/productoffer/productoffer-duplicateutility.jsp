<script type="text/javascript">
    function validateDuplicate() {
        var isValidName = verifyUniquenessOnSubmit('duplicateEntityName', 'create', '', 'com.elitecore.corenetvertex.pd.productoffer.ProductOfferData', '', '');
        if (isValidName) {
            document.forms['productOfferCloningForm'].submit();
        }
        return false;
    }

    function clearErrorMessages(form) {
        $(".has-error").removeClass("has-error");
        $(".has-success").removeClass("has-success");
        $(".has-feedback").removeClass("has-feedback");
        $(".alert").remove();
        $(".nv-input-error").removeClass("nv-input-error");
        $(".glyphicon-remove").remove();
        $(".alert-danger").remove();
        $(".removeOnReset").remove();
    }

    function setError(elementid, errorText) {
        var curElement = $("#".concat(elementid));
        var parentElement = curElement.parent();
        if (parentElement.parent().hasClass("has-error has-feedback") == false) {
            parentElement.parent().addClass("has-error has-feedback");
            parentElement.append("<span class=\"glyphicon glyphicon-remove form-control-feedback removeOnReset\"></span>");
            parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
        }
    }

    function setSuccess(elementid) {
        var curElement = $("#".concat(elementid));
        var parentElement = curElement.parent();
        parentElement.addClass("has-success has-feedback");
        parentElement.append("<span class=\"glyphicon glyphicon-ok form-control-feedback removeOnReset\"></span>");
    }

    function clearDialog() {
        clearErrorMessages(productOfferCloningForm);
    }
</script>