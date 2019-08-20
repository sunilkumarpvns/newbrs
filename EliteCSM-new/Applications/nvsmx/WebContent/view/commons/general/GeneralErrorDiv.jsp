
<div class="col-xs-12">
    <div class="col-xs-12 generalError" ></div>
</div>
<script type="text/javascript">

    function removeGeneralErrors(){
        $(".generalError").removeClass("bg-danger");
        $(".generalError").text("");
    }

    function addGeneralError(errorText){
        $(".generalError").addClass("bg-danger");
        $(".generalError").text(errorText);
    }

</script>