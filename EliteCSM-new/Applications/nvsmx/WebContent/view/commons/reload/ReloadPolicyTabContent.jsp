<style>
	.failure-count{
		color: #d9534f;
		font-weight: bold;
	}
	.partial-count{
		color: #f0ad4e;
		font-weight: bold;
	}

	.success-count{
		color: #3c763d;
		font-weight: bold;
	}

	.last-known-good-count{
		color: #337ab7;
		font-weight: bold;
	}

	.fail-count{
		font-size: 80%;
		background-color: #d9534f;
		color: white;
	}

	.margin{
		margin-bottom: 0%;
	}
</style>

<script type="text/javascript">

    function displayStatus(data, type, thisBean){
        if (data == '<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@SUCCESS" />') {
            return "<span class='success-count' >"+'<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@SUCCESS.status" />'+"</span>"
        } else  if(data == '<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@FAILURE" />') {
            return "<span class='failure-count'>"+'<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@FAILURE.status" />'+"</span>"
        } else  if(data == '<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@LAST_KNOWN_GOOD" />') {
            return "<span class='last-known-good-count'>"+'<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@LAST_KNOWN_GOOD.status" />'+"</span>"
        } else  if(data == '<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@PARTIAL_SUCCESS" />') {
            return "<span class='partial-count'>"+'<s:property value="@com.elitecore.corenetvertex.constants.PolicyStatus@PARTIAL_SUCCESS.status" />'+"</span>"
        }
    }

    function displayLink(data, type, thisBean){
        if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@OFFER.value" />') {
            return "<a href='${pageContext.request.contextPath}/pd/productoffer/product-offer/" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@RNC.value" />') {
            return "<a href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@DATA.value" />') {
            return "<a href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@IMS.value" />') {
            return "<a href='${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/view?pkgId=" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@EMERGENCY.value" />') {
            return "<a href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId=" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@PROMOTIONAL.value" />') {
            return "<a href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@TOPUP.value" />') {
            return "<a href='${pageContext.request.contextPath}/pd/datatopup/data-topup/" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@MONETARYRECHARGEPLAN.value" />') {
            return "<a href='${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/" + thisBean.id  + "'>"+data+"</a>";
        }else if ('${key}' == '<s:property value="@com.elitecore.corenetvertex.constants.EntityType@BOD.value" />') {
            return "<a href='${pageContext.request.contextPath}/pd/bodpackage/bod-package/" + thisBean.id  + "'>"+data+"</a>";
        }
    }

</script>

<div class="row" style="padding: 8px 15px">
	<nv:dataTable
			id="tbody-%{#policy.count}"
			list="${value}"
			width="100%"
			showPagination="true"
			showFilter="true"
			emptyTable="No Policies Defined"
			cssClass="table table-blue">
		<nv:dataTableColumn title="Policy Name" beanProperty="name" sortable="true" tdCssClass="word-break" tdStyle="width:20%;" renderFunction="displayLink" />
		<nv:dataTableColumn title="Type" beanProperty="packageType" sortable="true" tdCssClass="word-break" tdStyle="width:8%;" />
		<nv:dataTableColumn title="Mode" beanProperty="packageMode" sortable="true" tdCssClass="word-break" tdStyle="width:8%;"/>
		<nv:dataTableColumn title="Status" beanProperty="status" sortable="true" tdCssClass="word-break" renderFunction="displayStatus" tdStyle="width:12%;"/>
		<nv:dataTableColumn title="Remark" beanProperty="remark" tdCssClass="word-break" tdStyle="width: 50%;"/>
	</nv:dataTable>

</div>
</div>


