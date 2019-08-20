<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style>
	#count{
		background: linear-gradient(45deg, #ecf1f8 49%, #b0b9c5 51%, #ecf1f8 51%);
		vertical-align: middle;
	}
	.right{
		float: right;
	}
	.left{
		float: left;
	}
</style>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="reload.policy" />
		</h3>
	</div>
	<div class="panel-body">

		<s:if test="reloadPolicyCacheDetail != null">
			<s:label id="reloadStatus"  key="reload.policy.status1" value="%{reloadPolicyCacheDetail.status.status}" cssClass="control-label light-text word-break" labelCssClass="col-sm-3 col-xs-4" elementCssClass="col-sm-9 col-xs-8"  />
			<s:if test="%{reloadPolicyCacheDetail.remark != null && reloadPolicyCacheDetail.remark != ''  }">
				<s:label id="remark"  		key="remarks" value="%{reloadPolicyCacheDetail.remark}" cssClass="control-label light-text word-break" labelCssClass="col-sm-3 col-xs-4" elementCssClass="col-sm-9 col-xs-8 "  />
			</s:if>
		</s:if>

		<s:if test="unreachableInstances !=null && unreachableInstances != '' ">
			<s:label id="unreachabelInstances"  key="reload.policy.unreachable.instance" value="%{unreachableInstances}" cssClass="control-label light-text failure-count word-break" labelCssClass="col-sm-3 col-xs-4" elementCssClass="col-sm-9 col-xs-8 "  />
			<br/>
		</s:if>

		<br/>
		<div class="row" >
		<fieldset class="fieldSet-line margin">
		<legend align="top"><s:text name="reload.summary"/></legend>
		<table id="pkgTypeWisePolicyStatistics-statistics"  class="table table-blue" >
			<thead>
			<tr>
				<th id="count"><span class="right"><s:text name="Package"/></span><span class="left"><s:text name="Status"/></span></th>
				<s:iterator value="pkgTypeWisePolicyStatistics" >
					<th id="type"><s:property value="%{key}"/></th>
				</s:iterator>
				<th id="total" ><s:text name="Total"/></th>
			</tr>
			</thead>
			<tbody id="tbody-statistics">
				<tr>
					<th id="success" ><s:text name="reload.policy.success"/></th>
					<s:iterator value="pkgTypeWisePolicyStatistics" >
						<td class="success-count"><s:property value="%{value.successCounter}"/></td>
					</s:iterator>
					<td class="success-count"><s:property value="%{reloadPolicyCacheDetail.successCounter}"/></td>

				</tr>

				<tr>
					<th id="partialSuccess" ><s:text name="reload.policy.partial.success"/></th>
					<s:iterator value="pkgTypeWisePolicyStatistics" >
						<td class="partial-count"><s:property value="%{value.partialSuccessCounter}"/></td>
					</s:iterator>
					<td class="partial-count"><s:property value="%{reloadPolicyCacheDetail.partialSuccessCounter}"/></td>

				</tr>

				<tr>
					<th id="failure"><s:text name="reload.policy.failure"/></th>
					<s:iterator value="pkgTypeWisePolicyStatistics" >
						<td class="failure-count"><s:property value="%{value.failureCounter}"/></td>
					</s:iterator>
					<td class="failure-count"><s:property value="%{reloadPolicyCacheDetail.failureCounter}"/></td>

				</tr>

				<tr>
					<th id="lastKnownGood"><s:text name="reload.policy.last.known.good"/></th>
					<s:iterator value="pkgTypeWisePolicyStatistics" >
						<td class="last-known-good-count"><s:property value="%{value.lastKnownGoodCounter}"/></td>
					</s:iterator>
					<td class="last-known-good-count"><s:property value="%{reloadPolicyCacheDetail.lastKnownGoodCounter}"/></td>

				</tr>
			</tbody>
		</table>
		</fieldset>
		</div>

		<div class="row" >
			<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="reload.detail"/></legend>
				<div class="container-fluid">
				<ul class="nav nav-tabs tab-headings" id="tabs">
					<s:iterator value="pkgTypeWisePolicy" status="policy">
						<s:if test="%{#policy.count ==1}">
							<li class="active" id="tab-<s:property value='%{#policy.count}'/>">
						</s:if>
						<s:else>
							<li id="tab-<s:property value='key'/>" >
						</s:else>
						<a data-toggle="tab" href="#<s:property value='%{#policy.count}'/>"><s:property value="key"/> <s:if test="%{pkgTypeWisePolicyStatistics[key].failureCounter != 0 || pkgTypeWisePolicyStatistics[key].partialSuccessCounter != 0 || pkgTypeWisePolicyStatistics[key].lastKnownGoodCounter != 0}"><span class="badge fail-count"><s:property value="%{pkgTypeWisePolicyStatistics[key].failureCounter + pkgTypeWisePolicyStatistics[key].partialSuccessCounter + pkgTypeWisePolicyStatistics[key].lastKnownGoodCounter}" /></span></s:if></a>
						</li>
					</s:iterator>
				</ul>
				<div class="tab-content">
					<br/>
					<s:iterator value="pkgTypeWisePolicyJsonMap" status="policy">
					<s:if test="%{#policy.count ==1}">
					<div id="<s:property value='%{#policy.count}'/>" class="tab-pane fade in active">
						</s:if>
						<s:else>
						<div id="<s:property value='%{#policy.count}'/>" class="tab-pane fade">
							</s:else>
							<%@ include file="ReloadPolicyTabContent.jsp"%>
							</s:iterator>
						</div>
					</div>
				</div>
			</div>
			</fieldset>
		</div>
	</div>
</div>



	<script type="text/javascript" >

        $(document).ready(function(){

            var proceed = false;
            $('#tabs a').click(function (e) {
                e.preventDefault();
                $(this).tab('show');

            });
            // store the currently selected tab in the hash value
            $("ul.nav-tabs > li > a").on("shown.bs.tab", function (e) {
                var id = $(e.target).attr("href").substr(1);
                window.location.hash = id;
            });
            // on load of the page: switch to the currently selected tab
            var hash = window.location.hash;
            $('#tabs a[href="' + hash + '"]').tab('show');
        });

	</script>