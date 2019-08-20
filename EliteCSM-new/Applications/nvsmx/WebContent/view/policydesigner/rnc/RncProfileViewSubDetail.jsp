<%--
  Created by IntelliJ IDEA.
  User: dhyani
  Date: 4/8/17
  Time: 9:20 AM
  To change this template use File | Settings | File Templates.
--%>

<style type="text/css">

    .usage-qouta-th{
        font-weight: bold;
        width:150px;
        text-align:right;
    }
    .usage-qouta-td{
        text-align:right;
        width:150px;
        word-break: break-all;
    }

</style>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<s:if test="rncProfileDetailWrapperMap.size != 0">
  <div class="col-xs-12" style="padding-top: 20px">
    <s:iterator value="rncProfileDetailWrapperMap">
        <table class="table table-blue-stripped table-condensed table-bordered">
          <caption class="caption-header">
              <s:if test="key==0">
                <s:text name="rnc.highspeed"/> <s:text name="rnc.configuration"/>
              </s:if>
              <s:else>
                <s:text name="rnc.fuplevel"/> <s:property value="key"/> <s:text name="rnc.configuration"/>
              </s:else>
            </div>
          </caption>
        <tr>
          <th><s:text name="rnc.profile.detail.servicetype" /></th>
          <th><s:text name="rnc.profile.detail.ratinggroup" /></th>
          <th class="usage-qouta-th"><s:text name="rnc.quota" /></th>
          <th class="usage-qouta-th"><s:text name="rnc.profile.detail.pulse" /></th>
            <s:set var="priceTag">
                <s:property value="getText('rnc.profile.detail.rate')"/> <s:property value="getText('opening.braces')"/><s:text name="%{currency}"/><s:property value="getText('closing.braces')"/>
            </s:set>
          <th class="usage-qouta-th"><s:text name="priceTag" /></th>
          <th class="usage-qouta-th"><s:text name="rnc.profile.detail.dailyusagelimit" /></th>
          <th class="usage-qouta-th"><s:text name="rnc.profile.detail.weeklyusagelimit" /></th>
        </tr>
        <s:iterator value="value" var="list">
          <tr>
            <td>${serviceTypeName}</td>
            <td>${ratingGroupName}</td>
            <td class="usage-qouta-td">${quota}</td>
            <td class="usage-qouta-td">${pulse}</td>
            <td class="usage-qouta-td">${rate}</td>
            <td class="usage-qouta-td">${dailyUsageLimit}</td>
            <td class="usage-qouta-td">${weeklyUsageLimit}</td>
          </tr>
        </s:iterator>
      </table>
    </s:iterator>
  </div>
</s:if>
<s:else>
  <table class="table table-bordered" style="border: 0px; border-spacing: 0 !important; margin-bottom: 0px;">
    <tr role="row" style="background: #ecf1f8 !important;">
        <td style="text-align: center;" colspan="7"><s:text name="rnc.service.empty"/></td>
    </tr>
  </table>
</s:else>
