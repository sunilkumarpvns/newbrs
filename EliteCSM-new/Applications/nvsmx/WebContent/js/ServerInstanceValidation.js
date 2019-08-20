
var hostPattern = /^(([a-z,A-Z,0-9]+)\.)+([a-z,A-Z,0-9]+)$/;
var urlPattern =  /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$/;
var urlPatternRegex=new RegExp(urlPattern);
var hostPatternRegex = new RegExp(hostPattern);


function verifyMandatoryFieldsForDiameter(){

    var diameterEnabled = $("#diameterEnabled").val();

    if(diameterEnabled=='true' || diameterEnabled=='True'){

        var diameterUrl = $("#diameterUrl").val();

        if(isNullOrEmpty(diameterUrl)){
            setError("diameterUrl","Diameter URL is Required")
            return false;
        }else if(urlPatternRegex.test(diameterUrl) == false){

            setError("diameterUrl","Invalid value of diameter URL")
            return false;
        }
        else{
            clearErrorMessagesById("diameterUrl");
        }

        var diameterOriginHost = $("#diameterOriginHost").val();
        if(isNullOrEmpty(diameterOriginHost)){
            setError("diameterOriginHost","Origin Host is Required")
            return false;
        }else if(hostPatternRegex.test(diameterOriginHost) == false){
            setError("diameterOriginHost","Invalid value of diameter origin host")
            return false;
        }else{
            clearErrorMessagesById("diameterOriginHost");
        }

        var diameterOriginRealm = $("#diameterOriginRealm").val();
        console.log("diameterOriginRealm" +diameterOriginRealm);
        if(isNullOrEmpty(diameterOriginRealm)){
            setError("diameterOriginRealm","Origin Realm is Required")
            return false;
        }else if(hostPatternRegex.test(diameterOriginRealm) == false){
            setError("diameterOriginRealm","Invalid value of diameter origin realm")
            return false;
        }else{
            clearErrorMessagesById("diameterOriginRealm");
        }
    }else{
        $("#diameterUrl").prop("disabled", true);
    }

    return true;
}

function enableDisableDiameterFields(){
    var diameterEnabled = $("#diameterEnabled").val();
    if(diameterEnabled=='false' || diameterEnabled=='False') {
        $("#diameterUrl").prop("disabled", true);
        $("#diameterOriginHost").prop("disabled", true);
        $("#diameterOriginRealm").prop("disabled", true);
    }else{
        $("#diameterUrl").prop("disabled", false);
        $("#diameterOriginHost").prop("disabled", false);
        $("#diameterOriginRealm").prop("disabled", false);
    }
}

function enableDisableRadiusFields(){
    var radiusEnabled = $("#radiusEnabled").val();
    if(radiusEnabled=='false' || radiusEnabled=='False') {
        $("#radiusUrl").prop("disabled", true);
    }else{
        $("#radiusUrl").prop("disabled", false);
    }
}

function verifyMandatoryFieldsForRadius(){

    var radiusEnabled = $("#radiusEnabled").val();

    if(radiusEnabled=='true' || radiusEnabled=='True'){

        var radiusUrl = $("#radiusUrl").val();
        if(isNullOrEmpty(radiusUrl)){
            setError("radiusUrl","RADIUS URL is Required")
            return false;
        }else if (urlPatternRegex.test(radiusUrl) == false){
            setError("radiusUrl","invalid.radius.url")
            return false;
        }else{
            clearErrorMessagesById("radiusUrl");
        }
    }
    return true;
}