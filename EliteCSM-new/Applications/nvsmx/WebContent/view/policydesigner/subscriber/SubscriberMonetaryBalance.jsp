<%--
  Created by IntelliJ IDEA.
  User: arpit
  Date: 14/6/18
  Time: 6:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">
    function updateMonetaryBalanceRender(data, type, thisBean) {
        var packageCurrency = '${currency}';
        if (thisBean.currency == packageCurrency) {
            var updateFunction = "javascript:parepareForm('" + thisBean.id + "');"
            return "<a style='cursor:pointer' href=" + updateFunction + "><span class='glyphicon glyphicon-pencil'></span></a>";
        } else {
            var updateFunction = "javascript:return false;"
            return "<a style='cursor:pointer' href=" + updateFunction + "><span disabled='disabled' class='glyphicon glyphicon-pencil' title='Can not update due to system currency mismatch.'></span></span></a>";
        }
    }
</script>
<%--Monetary Balance Modal jsp--%>
<div class="row">
    <%@ include file="addMonetaryBalance.jsp" %>
    <%@ include file="updateMonetaryBalance.jsp" %>
    <div class="container-fluid" id="subscriptions">
        <div id="sub">
            <nv:dataTable
                    list="${monetaryBalancesJsonArray}"
                    cssClass="table table-blue" id="monetaryBalance"
                    width="100%"
                    showPagination="false" showInfo="false">
                <nv:dataTableColumn title="Service Name" beanProperty="serviceName"/>
                <nv:dataTableColumn title="Currency" beanProperty="currency"/>
                <nv:dataTableColumn title="Available Balance" tdCssClass="text-right" beanProperty="availBalanceStr"/>
                <nv:dataTableColumn title="Initial Balance" tdCssClass="text-right" beanProperty="initialBalanceStr"/>
                <nv:dataTableColumn title="Credit Usage" tdCssClass="text-right" beanProperty="creditUsageStr"/>
                <nv:dataTableColumn title="Reservation" tdCssClass="text-right" beanProperty="totalReservationStr"/>
                <nv:dataTableColumn title="Credit Limit" tdCssClass="text-right" beanProperty="creditLimitStr"/>
                <nv:dataTableColumn title="Start Time" beanProperty="validFromDateStr"/>
                <nv:dataTableColumn title="Expiry Time" beanProperty="validToDateStr"/>
                <nv:dataTableColumn title="" renderFunction="updateMonetaryBalanceRender"
                                    style="width:20px;border-right:0px;"/>
            </nv:dataTable>
        </div>
    </div>
</div>

<script>
    var systemCurrency = '<s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>';
    var tdCurrencyVal;
    var packageCurrency = '${currency}';
    if ($('#monetaryBalance').length) {
        $('#monetaryBalance tbody tr').each(
            function (index) {
                tdCurrencyVal = $.trim($(this).find("td:nth-child(2)").text());
                if (tdCurrencyVal != "" && tdCurrencyVal != packageCurrency)
                    $(this).css('color', 'red');
            }
        )
    }

    if (tdCurrencyVal == '') {
        $('#message').hide();
    }

    var monetaryBalanceJSON = ${monetaryBalancesJsonArray};
    var selectedBalance = undefined;

    function parepareForm(balanceId) {
        $('#updateBalanceForm')[0].reset();
        for (balance in monetaryBalanceJSON) {
            if (monetaryBalanceJSON[balance].id == balanceId) {
                $("#selectedService1").val(monetaryBalanceJSON[balance].serviceId);
                $("#updatedBalance").val((Number.parseFloat(monetaryBalanceJSON[balance].availBalance)).toFixed(<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MONETARY_BALANCE_PRECISION" />));
                $("#selectedBalanceId").val(monetaryBalanceJSON[balance].id);
                $("#availableBalance").val((Number.parseFloat(monetaryBalanceJSON[balance].availBalance)).toFixed(<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MONETARY_BALANCE_PRECISION" />));
                $("#creditLimit").val(monetaryBalanceJSON[balance].creditLimit);
                selectedBalance = monetaryBalanceJSON[balance];

                $('#updateMonetaryBalance').modal('show');
                break;
            }
        }
    }

    function showUpdatedBalance() {
        if (selectedBalance != undefined && isNaN(Number.parseFloat($("#updateBalance").val())) == false) {
            if ($("#balanceOperation").val() == '<s:property value="@com.elitecore.nvsmx.ws.subscription.blmanager.OperationType@DEBIT.getName()"/>') {
                $("#updatedBalance").val((Number.parseFloat((selectedBalance.availBalance)) - Number.parseFloat($("#updateBalance").val())).toFixed(<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MONETARY_BALANCE_PRECISION" />));
            } else {
                $("#updatedBalance").val((Number.parseFloat((selectedBalance.availBalance)) + Number.parseFloat($("#updateBalance").val())).toFixed(<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MONETARY_BALANCE_PRECISION" />));
            }
        } else {
            $("#updatedBalance").val(Number.parseFloat(selectedBalance.availBalance).toFixed(<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MONETARY_BALANCE_PRECISION" />));
        }
    }
</script>