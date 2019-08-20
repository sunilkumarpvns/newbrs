<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 27/10/17
  Time: 12:10 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">

    function checkSequenceRange() {
        if($("#sequenceRange") != undefined && $("#sequenceRange").val().trim() != ""){
            var sequenceRange = $("#sequenceRange").val();
            if(sequenceRange.indexOf("-") != -1){
                sequenceRange.replace(/ /g,"");
                var tokens = sequenceRange.split("-");
                if(/^[0-9]+$/.test(tokens[0]) == false || /^[0-9]+$/.test(tokens[1]) == false) {
                    setErrorOnElement($("#sequenceRange"),"Value should be in Range e.g. 5-50");
                    return false;
                }
            } else {
                setErrorOnElement($("#sequenceRange"),"Value should be in Range e.g. 5-50");
                return false;
            }
        }
    }

    function validateMapping() {

        clearErrorMessages();
        if(checkSequenceRange() == false){
            return false;
        }

        var CsvFieldMappingTableBodyLength = $("#CsvFieldMappingTable tbody tr").length;
        var CsvStripMappingTableBodyLength = $("#CsvStripMappingTable tbody tr").length;

        if (CsvFieldMappingTableBodyLength >= 1) {

            //Field Mapping should not be empty
            var isValidFieldMapping = true;
            var fieldMappingValues = [];
            var headerValues = [];

            $("#CsvFieldMappingTable tbody tr").each(function () {
                var inputElementHeader =$(this).children().first().find('[id^=headerField]');
                var inputElementPcrfKey =$(this).children('td:nth-child(2)').find('[id^=FieldMappingPcrfKey]');
                if(isNullOrEmpty(inputElementHeader.val())){
                    setErrorOnElement(inputElementHeader,"Header can not be empty");
                    isValidFieldMapping = false;
                }
                var pcrfKeyVal = inputElementPcrfKey.val();
                if (isNullOrEmpty(pcrfKeyVal)) {
                    setErrorOnElement(inputElementPcrfKey,"PCRF Key can not be empty");
                    isValidFieldMapping = false;
                }

                if(headerValues.indexOf(inputElementHeader.val().toLowerCase()) != -1){
                    setErrorOnElement(inputElementHeader,"Duplicate Mapping found");
                    isValidFieldMapping = false;
                    return;
                }
                headerValues.push(inputElementHeader.val().toLowerCase());

                if(fieldMappingValues.indexOf(pcrfKeyVal.toLowerCase()) != -1){
                    setErrorOnElement(inputElementPcrfKey,"Duplicate Mapping found");
                    isValidFieldMapping = false;
                    return;
                }
                fieldMappingValues.push(pcrfKeyVal.toLowerCase());

            });

            if(isValidFieldMapping == false) {
                return isValidFieldMapping
            }

        }
        if (CsvStripMappingTableBodyLength >= 1) {

            //Strip Mapping should not be empty
            var isValidStripMapping = true;
            var stripAttributesValues = [];

            $("#CsvStripMappingTable tbody tr").each(function () {
                var inputElementPcrfKey =$(this).children().first().find('[id^=StripMappingPcrfKey]');
                var pcrfKeyVal = inputElementPcrfKey.val();
                if (isNullOrEmpty(pcrfKeyVal)) {
                    setErrorOnElement(inputElementPcrfKey,"PCRF Key can not be empty");
                    isValidStripMapping = false;
                }
                var inputElementSeparator =$(this).children('td:nth-child(3)').find('input');
                if (isNullOrEmpty(inputElementSeparator.val())) {
                    setErrorOnElement(inputElementSeparator,"Separator can not be empty");
                    isValidStripMapping = false;
                }

                if(stripAttributesValues.indexOf(pcrfKeyVal.toLowerCase()) != -1){
                    setErrorOnElement(inputElementPcrfKey,"Duplicate Mapping found");
                    isValidStripMapping = false;
                    return;
                }
                stripAttributesValues.push(pcrfKeyVal.toLowerCase());
            });

            if(isValidStripMapping == false) {
                return isValidStripMapping
            }

        }

    }

    function validateNumericFields(){
        var timeBaseRollingUnit = $("#timeBasedRollingUnit").val();
        var sizeBasedRollingUnit = $("#sizeBasedRollingUnit").val();
        var recordBasedRollingUnit = $("#recordBasedRollingUnit").val();
        var failOverTime = $("#failOverTime").val();
        if (isNullOrEmpty(timeBaseRollingUnit) == false && isNaturalNumber(timeBaseRollingUnit) == false) {
            setError("timeBasedRollingUnit", '<s:text name="error.required.positive.numeric"><s:param ><s:text name="csv.time.based.rolling.unit"/></s:param></s:text>');
            return false;
        }
        if (isNullOrEmpty(sizeBasedRollingUnit) == false && isNaturalNumber(sizeBasedRollingUnit) == false) {
            setError("sizeBasedRollingUnit", '<s:text name="error.required.positive.numeric"><s:param><s:text name="csv.size.based.rolling.unit"/></s:param></s:text>');
            return false;
        }
        if (isNullOrEmpty(recordBasedRollingUnit) == false && isNaturalNumber(recordBasedRollingUnit) == false) {
            setError("recordBasedRollingUnit", '<s:text name="error.required.positive.numeric"><s:param><s:text name="csv.record.based.rolling.unit"/></s:param></s:text>');
            return false;
        }
        if (isNullOrEmpty(failOverTime) == false && isNaturalNumber(failOverTime) == false) {
            setError("failOverTime", '<s:text name="error.required.positive.numeric"><s:param><s:text name="csv.fail.over.time"/></s:param></s:text>');
            return false;
        }
        return true;

    }

    function setPcrfKeySuggestions() {
        commonAutoCompleteUsingCssClass('.pcrf-key-suggestions',<s:property value="@com.elitecore.nvsmx.system.util.NVSMXUtil@getPcrfKeySuggestions('request','response')" escapeHtml="false" />);
    }


</script>