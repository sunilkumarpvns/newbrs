<%@ taglib prefix="s" uri="/struts-tags/ec" %>

<script type="text/javascript">

    function setVolumeType(){
        var quotaType = $("#quotaType").val();
        var pkgMode = $("#packageMode").val();

        if (pkgMode == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()"/>' || pkgMode == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgMode@TEST.name()"/>') {
            if (quotaType == '<s:property value="@com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@VOLUME.name()"/>') {
                $("#timeBalance").attr("readOnly", "true");
                $("#volumeBalance").removeAttr("readOnly");
            } else if (quotaType == '<s:property value="@com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@TIME.name()"/>') {
                $("#volumeBalance").attr("readOnly", "true");
                $("#timeBalance").removeAttr("readOnly");
            }
        }
    }

    function validateQuotaVolume(){
        var quotaType = $("#quotaType").val();
        var volumeBalance = $("#volumeBalance").val();
        var timeBalance = $("#timeBalance").val();
        if (quotaType == '<s:property value="@com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@VOLUME.name()"/>') {
            if (isNullOrEmpty(volumeBalance)) {
                setError("volumeBalance", '<s:text name="data.top.up.invalid.volume.quota"/>');
                return false;
            }
            if(volumeBalance <= 0){
                setError("volumeBalance", '<s:text name="data.top.up.volume.quota.numeric.error"/>');
                return false;
            }

        } else if (quotaType == '<s:property value="@com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@TIME.name()"/>') {
            if (isNullOrEmpty(timeBalance)) {
                setError("timeBalance", '<s:text name="data.top.up.invalid.time.quota"/>');
                return false;
            }
            if(timeBalance <=0 ){
                setError("timeBalance", '<s:text name="data.top.up.time.quota.numeric.error"/>');
                return false;

            }
        }
        return true;
    }

    function validateValidityPeriod(){
        var validityPeriod = $("#validityPeriod").val();
        if (isNullOrEmpty(validityPeriod)) {
            setError("validityPeriod", '<s:text name="error.valueRequired"/>');
            return false;
        }
        return true;
    }


</script>