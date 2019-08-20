<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<script type="text/javascript">

    function validateForm() {
        var totalRow = $("#TOTAL_ROW").val();
        var isValidConfiguration = true;
        clearErrorMessages();
        if (isNaturalNumber(totalRow) == false) {
            setError('TOTAL_ROW', '<s:text name="totalRow.error"/>');
            isValidConfiguration = false;
        } else if(totalRow < 5 || totalRow > 500){
            setError('TOTAL_ROW', '<s:text name="totalRow.range.error"/>');
            isValidConfiguration = false;
        }

        var tax = $("#TAX").val();

        if (isPositiveDecimalNumberInGivenRange('TAX', tax, 0, 100) == false) {
            isValidConfiguration = false;
        }


        var systemCurrency = '<s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>';
        var currentCurrency = $("#currency").val();
        if( isNullOrEmpty(currentCurrency)) {
            setError('currency','<s:text name="currency.error.message"/>');
            isValidConfiguration = false;
        }

        var systemCountry = $("#countryNames").val();

        if( isNullOrEmpty(systemCountry)) {
            setError('countryNames','<s:text name="county.error.message"/>');
            isValidConfiguration = false;
        }

        var systemOperator = $("#operatorNames").val();

        if(isNullOrEmpty(systemOperator)) {
            setError('operatorNames','<s:text name="operator.error.message"/>');
            isValidConfiguration = false;
        }

        if(systemCurrency == currentCurrency){
            return isValidConfiguration;
        }else{
            if(isValidConfiguration)
            {
                $("#warningCurrencyChange").modal('show');
            }
            return false;
        }

    }

</script>