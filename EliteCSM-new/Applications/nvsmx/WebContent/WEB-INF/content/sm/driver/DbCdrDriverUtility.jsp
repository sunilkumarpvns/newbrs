<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 27/10/17
  Time: 7:12 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">

    $(function(){
        $( ".select2" ).select2();
        setPcrfKeySuggestions();
        var storeAllCdrVal = $("#storeAllCdr");
        disableBatchParameters(storeAllCdrVal);
    });

    function disableBatchParameters(obj) {

        var storeAllCdrVal = $("#storeAllCdr").val();
        var batchMode = $("#batchUpdate");
        if (storeAllCdrVal !== '<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.getStringNameBoolean()}" />') {
            $("#batchUpdate").removeAttr("readOnly");
            $("#batchUpdate").val("true");

        } else {
            $("#batchUpdate").attr("readOnly", "readOnly");
            $("#batchUpdate").val("false");
        }
        disableBatchParametersFromBatchUpdate(batchMode);

    }

    function disableBatchParametersFromBatchUpdate(obj) {
        var batchMode = $("#batchUpdate").val();
        if ( batchMode !== '<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.getStringNameBoolean()}" />') {
            $("#batchSize").removeAttr("readOnly");
            $("#batchUpdateQueryTimeout").removeAttr("readOnly");
        } else {
            $("#batchSize").attr("readOnly", "readOnly");
            $("#batchUpdateQueryTimeout").attr("readOnly", "readOnly");
        }
    }

    function setPcrfKeySuggestions() {
        commonAutoCompleteUsingCssClass('.pcrf-key-suggestions',<s:property value="@com.elitecore.nvsmx.system.util.NVSMXUtil@getPcrfKeySuggestions('request','response')" escapeHtml="false" />);
    }

    var j = document.getElementsByName("FieldMappingTableRow").length;
    if(isNullOrEmpty(j)) {
        j = 0;
    }
    function addFieldMapping(){

        $("#DbCdrFieldMappingTable tbody").append("<tr name='FieldMappingTableRow'>" + $("#tempFieldMappingTable").find("tr").html() + "</tr>");

        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(1)").find("input").focus();
        var NAME = "name";
        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(1)").find("input").attr(NAME,'dbCdrDriverData.dbCdrDriverFieldMappingDataList['+j+'].pcrfKey');
        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(1)").find("input").attr('id','FieldMappingPcrfKey-'+j);
        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(1)").find("input").addClass("pcrf-key-suggestions");
        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(2)").find("input").attr(NAME,'dbCdrDriverData.dbCdrDriverFieldMappingDataList['+j+'].dbField');
        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(2)").find("input").attr('id','FieldMappingDbField-'+j);
        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(3)").find("select").attr(NAME,'dbCdrDriverData.dbCdrDriverFieldMappingDataList['+j+'].dataType');
        $("#DbCdrFieldMappingTable").find("tr:last td:nth-child(4)").find("input").attr(NAME,'dbCdrDriverData.dbCdrDriverFieldMappingDataList['+j+'].defaultValue');
        setPcrfKeySuggestions();
        j++;
    }

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

        var DbCdrFieldMappingTableBodyLength = $("#DbCdrFieldMappingTable tbody tr").length;

        if(DbCdrFieldMappingTableBodyLength < 1) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text("At-least One DB CDR Field Mapping is Required.");
            return false;
        }

        if (DbCdrFieldMappingTableBodyLength >= 1) {

            //Field Mapping should not be empty
            var isValidFieldMapping = true;
            var fieldMappingValues = [];
            var dbFieldValues = [];

            $("#DbCdrFieldMappingTable tbody tr").each(function () {
                var inputElementPcrfKey =$(this).children().first().find('[id^=FieldMappingPcrfKey]');
                var inputElementDbField =$(this).children('td:nth-child(2)').find('[id^=FieldMappingDbField]');
                var pcrfKeyVal = inputElementPcrfKey.val();
                if(isNullOrEmpty(pcrfKeyVal)){
                    setErrorOnElement(inputElementPcrfKey,"PCRF Key can not be empty");
                    isValidFieldMapping = false;
                }
                var dbFieldVal = inputElementDbField.val();
                if (isNullOrEmpty(dbFieldVal)) {
                    setErrorOnElement(inputElementDbField,"DB Field can not be empty");
                    isValidFieldMapping = false;
                }

                if(fieldMappingValues.indexOf(pcrfKeyVal.toLowerCase()) != -1){
                    setErrorOnElement(inputElementPcrfKey,"Duplicate Mapping found");
                    isValidFieldMapping = false;
                    return;
                }
                fieldMappingValues.push(pcrfKeyVal.toLowerCase());

                if(dbFieldValues.indexOf(dbFieldVal.toLowerCase()) != -1){
                    setErrorOnElement(inputElementDbField,"Duplicate Mapping found");
                    isValidFieldMapping = false;
                    return;
                }
                dbFieldValues.push(dbFieldVal.toLowerCase());
            });

            if(isValidFieldMapping == false) {
                return isValidFieldMapping
            }
        }
        return true;
    }

    function validateBatchParameters(){
        var batchMode = $("#batchUpdate").val();

        if(batchMode == '<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@ENABLE.getStringNameBoolean()}" />'){
            var batchSize = $("#batchSize").val();
            var queryTimeout = $("#batchUpdateQueryTimeout").val();
            if(isNumberGreaterThanZero(batchSize) == false){
                setError("batchSize","<s:text name="db.cdr.invalid.batchsize" />");
                return false;
            }else if(batchSize < <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@BATCH_SIZE_MIN" /> || batchSize > <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_BATCH_SIZE" />){
                setError("batchSize","<s:text name="db.cdr.invalid.batchsize.range" />");
                return false;
            }
            if(isNumberGreaterThanZero(queryTimeout) == false){
                setError("batchUpdateQueryTimeout","<s:text name="db.cdr.invalid.querytimeout" />");
                return false;
            } else if(queryTimeout < <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MIN_QUERY_TIMEOUT_IN_SEC" /> || queryTimeout > <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_QUERY_TIMEOUT_IN_SEC" />){
                setError("batchUpdateQueryTimeout","<s:text name="invalid.query.timeout"/>");
                return false;
            }

        }
        return true;

    }
</script>
