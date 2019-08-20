<%--
  Created by IntelliJ IDEA.
  User: arpit
  Date: 7/9/18
  Time: 1:59 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">
var regExp = new RegExp("^((\\d{0,9}(\\.\\d{0,6})?))$");
function verifyUniquenessForName(mode,entityId) {
    if( verifyUniquenessOnSubmit('name', mode, entityId , 'com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData', '', '')==false){
        return false;
    }
    return true;
}

function verifyUniquenessForPrice(mode,entityId) {
    if( verifyUniquenessOnSubmit('price', mode, entityId, 'com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData', '', 'price')==false){
        return false;
    }
    return true;
}

function verifyPrice() {
    if(regExp.test($('#price').val()) == false) {
        setError('price','<s:text name="plan.price.precision" />');
        return false;
    }
    return true;
}

function verifyAmount() {
    if(regExp.test($('#amount').val()) == false) {
        setError('amount','<s:text name="plan.price.precision" />');
        return false;
    }
    return true;
}

function verifyAmountValidityCombo() {
    var amount = $('#amount').val();
    var validity = $('#validity').val();
    if( (isNullOrEmpty(amount) || new Number(amount) == 0) && (isNullOrEmpty(validity) || new Number(validity) == 0)) {
        setError('amount', '<s:text name="plan.amountvalidity.provided" />');
        setError('validity', '<s:text name="plan.amountvalidity.provided" />');
        return false;
    }
    return true;
}

function setDefaults() {
    if(isNullOrEmpty(amount)) {
        $('#amount').val(0);
    }
    if(isNullOrEmpty(validity)) {
        $('#validity').val(0);
    }
}
</script>