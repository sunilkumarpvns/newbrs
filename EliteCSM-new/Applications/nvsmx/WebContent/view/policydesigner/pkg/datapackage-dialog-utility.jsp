<%@taglib uri="/struts-tags/ec" prefix="s"%>
<table id='qosPCCRule'  class="table table-blue table-bordered">
    <caption class="caption-header">
        <span class="glyphicon glyphicon-check"></span>PCC Rule Name
    </caption>
    <thead>
    <th>PCC Profile Name</th>
    <th>PCC Rule Name</th>
    <th>PCC Monitoring Key</th>
    </thead>
    <tbody>
    <s:iterator value="pkgData.qosProfiles" var="profile">
        <s:iterator value="%{#profile.qosProfileDetailDataList}" var="qosProfileDetail">
            <s:iterator value="%{#qosProfileDetail.pccRules}" var="pccRule">
                <s:if test="#pccRule.scope.name() == 'LOCAL'">
                    <tr>
                        <td><s:property value="%{#profile.name}"/></td>
                        <td><s:hidden name="pccRuleIds" value="%{#pccRule.name}"/> <s:textfield name="%{#pccRule.name}-name" value="CopyOf_%{pkgData.name}_%{#pccRule.name}" id="%{#pccRule.name}-name" elementCssClass="col-sm-12" cssClass="form-control" maxlength="100"/></td>
                        <td><s:textfield name="%{#pccRule.name}-monitoringKey" value="CopyOf_%{pkgData.name}_%{#pccRule.monitoringKey}" id="%{#pccRule.name}-monitoringKey" elementCssClass="col-sm-12" cssClass="form-control" maxlength="100"/></td>
                    </tr>
                </s:if>
            </s:iterator>
        </s:iterator>
    </s:iterator>
    </tbody>
</table>