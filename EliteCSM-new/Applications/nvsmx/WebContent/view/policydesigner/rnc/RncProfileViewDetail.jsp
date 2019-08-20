<%@ page import="com.elitecore.nvsmx.system.constants.Attributes" %>
<%@ page import="com.google.gson.JsonArray" %>
<%--
  Created by IntelliJ IDEA.
  User: dhyani
  Date: 19/7/17
  Time: 6:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style type="text/css">
  .form-group {
    width: 100%;
    display: table;
    margin-bottom: 2px;
  }
  .usage-qouta-th{
    font-weight: bold;
    color: #4679bd;
    width:150px;
    text-align:right;
  }
  .usage-qouta-td{
    text-align:right;
    width:150px;
    word-break: break-all;
  }

</style>
<%
  JsonArray rncProfileData = new JsonArray();
  JsonArray rncProfileDetailData = (JsonArray)request.getAttribute(Attributes.RNC_PROFILE_DETAIL_DATA);
%>


<%@include file="RncProfile.jsp"%>

<div class="panel panel-primary">
  <div class="panel-heading" style="padding: 8px 15px">
    <h3 class="panel-title" style="display: inline;">
      <s:property value="rncProfileData.name"/>
    </h3>
    <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${rncProfileData.id}&auditableId=${rncProfileData.auditableId}&auditPageHeadingName=${rncProfileData.name}&refererUrl=/policydesigner/rnc/RncProfile/view?quotaProfileId=${rncProfileData.id}'">
                  <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
                  <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                          onclick="updateRncProfile(0)">
                    <span class="glyphicon glyphicon-pencil"></span>
                  </button>
			</span>
            <s:if test="%{rncProfileData.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
              <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/delete?quotaProfileId=${rncProfileData.id}&groupIds=${rncProfileData.groups}">
                  <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                  </button>
              </span>
            </s:if>
            <s:else>
              <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton">
                <button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                  <span class="glyphicon glyphicon-trash"></span>
                </button>
              </span>
            </s:else>
    </div>
  </div>
  <div class="panel-body" >
    <div class="row">
      <fieldset class="fieldSet-line">
        <legend align="top"><s:text name="basic.detail" /></legend>
        <div class="row">
          <div class="col-sm-4">
            <div class="row">
              <s:label key="rnc.quotaprofile.description" value="%{rncProfileData.description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
              <s:label key="rnc.quotaprofile.groups" value="%{rncProfileData.groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
                <s:label key="rnc.quotaprofile.quota.type" value="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@fromQuotaUsageType(rncProfileData.quotaType).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
                <s:label key="rnc.quotaprofile.unit.type" value="%{@com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType@fromVolumeUnitType(rncProfileData.unitType).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
                <s:label key="rnc.quotaprofile.balancelevel" value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@fromName(rncProfileData.balanceLevel).getDisplayVal()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8 col-sm-9" />
            </div>
          </div>
          <div class="col-sm-4 leftVerticalLine">
            <div class="row">
              <s:label disabled="disabled" key="rnc.quotaprofile.renewal.interval" value="%{rncProfileData.renewalInterval}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
              <s:label disabled="disabled" key="rnc.quotaprofile.renewal.interval.unit" value="%{@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@fromRenewalIntervalUnit(rncProfileData.renewalIntervalUnit).value()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
              <s:label key="rnc.quotaprofile.proration" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(rncProfileData.proration).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
              <s:label key="rnc.quotaprofile.carryForward" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(rncProfileData.carryForward).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
            </div>
          </div>
          <div class="col-sm-4 leftVerticalLine">
            <div class="row">
              <s:set var="createdByDate">
                <s:date name="%{rncProfileData.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
              </s:set>
              <s:if test="%{rncProfileData.createdByStaff !=null}">
                <s:hrefLabel key="getText('createdby')" value="%{rncProfileData.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
                             url="/sm/staff/staff/%{rncProfileData.createdByStaff.id}"/>
              </s:if>
              <s:else>
                <s:label key="getText('createdby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
              </s:else>
              <s:if test="%{rncProfileData.createdDate !=null}">
                <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
              </s:if>
              <s:else>
                <s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
              </s:else>

              <s:if test="%{rncProfileData.modifiedByStaff !=null}">
                <s:hrefLabel key="getText('modifiedby')" value="%{rncProfileData.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
                             url="/sm/staff/staff/%{rncProfileData.modifiedByStaff.id}"/>
              </s:if>
              <s:else>
                <s:label key="getText('modifiedby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
              </s:else>
              <s:if test="%{rncProfileData.modifiedDate != null}">
                <s:set var="modifiedByDate">
                  <s:date name="%{rncProfileData.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                </s:set>
                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
              </s:if>
              <s:else>
                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
              </s:else>
            </div>
          </div>
        </div>
      </fieldset>

      <s:iterator value="rncProfileData.fupLevel" var="fupLvl">
        <s:if test="#fupLvl == 0">
          <fieldset class="fieldSet-line" id="FupLevel${fupLvl}">
              <legend align="top"><s:text name="rnc.highspeed" /> <s:text name="rnc.quota"/></legend>
        </s:if>
        <s:elseif test="rncProfileDetailMap[#fupLvl].size() != 0">
          <fieldset class="fieldSet-line" id="FupLevel${fupLvl}">
            <legend align="top"><s:text name="rnc.fuplevel"/> <s:property value="#fupLvl"/> <s:text name="rnc.quota"/> </legend>
        </s:elseif>
        <s:else>
          <fieldset class="fieldSet-line" id="FupLevel${fupLvl}" style="display: none">
            <legend align="top"><s:text name="rnc.fuplevel"/> <s:property value="#fupLvl"/> <s:text name="rnc.quota"/> </legend>
        </s:else>
          <s:if test="#fupLvl != 0 && #fupLvl == rncProfileDetailMap.size()-1 ">
            <div align="right" class="display-btn" style="margin-bottom: 5px;">
                <div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/deleteDetail?fupLevel=${fupLvl}&quotaProfileId=${rncProfileData.id}">
                  <button type="button"  class="btn btn-default">
                    <span class="glyphicon glyphicon-trash"></span>
                  </button>
                </div>
            </div>
          </s:if>
            <table class="table table-blue table-bordered" style="margin-bottom: -10px">
              <caption class="caption-header">
                <s:text name="rnc.configuration" />
                <div align="right" class="display-btn">
                  <span class="btn btn-group btn-group-xs defaultBtn" role="group" onclick="addRncConfiguration(<s:property value='#fupLvl'/>);" id="addRow">
                    <span class="glyphicon glyphicon-plus"></span>
                  </span>
                </div>
              </caption>
            </table>
              <s:set var="priceTag">
                  <s:property value="getText('rnc.profile.detail.rate')"/> <s:property value="getText('opening.braces')"/><s:property value="rncProfileData.pkgData.currency"/><s:property value="getText('closing.braces')"/>
              </s:set>


            <nv:dataTable
                      id="RnCProfileDetail-${fupLvl}"
                      list="${rncProfileDetailMap[fupLvl]}"
                      width="100%"
                      showPagination="false"
                      showInfo="false"
                      cssClass="table table-blue">

                <nv:dataTableColumn title="Data Service Type" 	 beanProperty="serviceTypeName" style="font-weight: bold;color: #4679bd; " tdStyle="text-align:left;min-width:50px;word-break: break-all" hrefurl="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=serviceTypeId"/>
                <nv:dataTableColumn title="Rating Group" 		 beanProperty="ratingGroupName" style="font-weight: bold;color: #4679bd; " tdStyle="text-align:left;min-width:50px;word-break: break-all" hrefurl="${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/view?ratingGroupId=ratingGroupId"/>
                <nv:dataTableColumn title="Quota" 		         beanProperty="quota" tdCssClass="usage-qouta-td" cssClass="usage-qouta-th"  />
                <nv:dataTableColumn title="Pulse" 		         beanProperty="pulse" tdCssClass="usage-qouta-td" cssClass="usage-qouta-th"  />
                <nv:dataTableColumn title='${priceTag}' beanProperty="rate" tdCssClass="usage-qouta-td" cssClass="usage-qouta-th"  />
                <nv:dataTableColumn title="Daily Usage Limit" 	 beanProperty="dailyUsageLimit" tdCssClass="usage-qouta-td" cssClass="usage-qouta-th"  />
                <nv:dataTableColumn title="Weekly Usage Limit"   beanProperty="weeklyUsageLimit" tdCssClass="usage-qouta-td" cssClass="usage-qouta-th"  />
                <nv:dataTableColumn title="Carry Forward Limit"  beanProperty="carryForwardLimit" tdCssClass="usage-qouta-td" cssClass="usage-qouta-th"  />
                <nv:dataTableColumn title="Revenue Code" beanProperty="revenueDetail" tdCssClass="usage-qouta-td" cssClass="usage-qouta-th" />
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px" tdStyle="width:20px" hrefurl="edit:javascript:updateRncConfiguration(id)"   />
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px" tdStyle="width:20px" hrefurl="delete:${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/deleteDetail?quotaProfileDetailId=id"/>
            </nv:dataTable>
        </fieldset>
      </s:iterator>
      <s:if test="rncProfileDetailMap.size() <= 2">
        <div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" >
          <button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="addFupLevel(<s:property value="rncProfileDetailMap.size()"/>)">
            <span class="glyphicon glyphicon-plus-sign"></span><s:text name="FUP Level Quota"/>
          </button>
        </div>
      </s:if>
    </div>
    <s:if test="@com.elitecore.corenetvertex.constants.BalanceLevel@fromName(rncProfileData.balanceLevel).fupLevel >= rncProfileDetailMap.size()">
      <div class="col-xs-12 bg-danger" id="generalError"><div class="col-xs-12 "><s:text name="rnc.profile.balance.level.note" /></div></div>
    </s:if>
    <div class="row">
      <div class="col-xs-12" align="center">
        <button class="btn btn-primary btn-sm"  onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${rncProfileData.pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"/> </button>
      </div>
    </div>
  </div>
</div>

<%@include file="RncProfileServiceConfiguration.jsp"%>
<script>
  function addRncConfiguration(fupLevel) {
      debugger;
    var balanceLevel = '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@fromName(rncProfileData.balanceLevel).getFupLevel()}"/>';
    $("#fupLevel").val(fupLevel);
    if(fupLevel > balanceLevel){
        $("#volumeCarryForwardLimitModel").attr("disabled", true);
        $("#timeCarryForwardLimit").attr("disabled", true);
    }
    $("#RncServiceConfDialog").modal('show');
  }

  function addFupLevel(level) {
    if(isNullOrEmpty(level) == false) {
      if($("#FupLevel"+level).css('display') == 'block') {
        if(level == 0) {
          addWarning(".popup","Please provide RnC Profile Configuration for High Speed Quota");
        }
        else {
          addWarning(".popup","Please provide RnC Profile for FUP Level " + level + " Quota");
        }
        return;
      }
      $("#FupLevel"+level).css('display','block');
      if(level == 2) {
        $("#btnAddFup").css('display','none');
      }
    }
  }


</script>




