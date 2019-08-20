<%@taglib uri="/struts-tags/ec" prefix="s" %>
<script>
    function validatePassword(){
        var password = $("#password").val();
        var confirmPassword = $("#confirmPassword").val();
        if(isNullOrEmpty(password)){
            setError("password","Password is Required");
            return false;
        }
        if(isNullOrEmpty(confirmPassword)){
            setError("confirmPassword","Confirm Password is Required");
            return false;
        }
        if(password != confirmPassword){
            setError("confirmPassword","Password And Confirm Password must be same");
            return false;
        }
        return true;
    }
</script>