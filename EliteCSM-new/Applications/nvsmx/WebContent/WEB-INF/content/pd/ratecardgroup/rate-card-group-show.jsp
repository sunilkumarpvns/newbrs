<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
    #timeBaseConditionTable th{
        width: 50%;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${auditableId}&auditableResourceName=${name}&auditPageHeadingName=${name}&refererUrl=/pd/ratecardgroup/rate-card-group/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <s:if test="%{rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
            <span class="btn-group btn-group-xs">
                    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                            title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/ratecardgroup/rate-card-group/${id}/edit?rncPackageId=${rncPackageData.id}'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
            </span>
            </s:if>
            <s:else>
               <span disabled="disabled" class="btn-group btn-group-xs">
                    <button disabled="disabled" type="button" class="btn btn-default header-btn" data-toggle="tooltip"
                            data-placement="bottom" title="edit">
                    <span class="glyphicon glyphicon-pencil"></span>
                    </button>
                </span>
            </s:else>

            <s:if test="%{rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/ratecardgroup/rate-card-group/${id}?_method=DELETE&rncPackageId=${rncPackageData.id}">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
            </s:if>
            <s:else>
                 <span disabled="disabled" class="btn-group btn-group-xs" data-toggle="confirmation-singleton">
			    <button disabled="disabled"  type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
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
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="ratecardgroup.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecardgroup.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecardgroup.advancecondition" value="%{advanceCondition}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="ratecardgroup.peak.rate"/></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:if test="%{peakRateRateCard.type == @com.elitecore.corenetvertex.constants.RateCardType@MONETARY.name()}">
                                <s:if test="%{peakRateRateCard.type == @com.elitecore.corenetvertex.pd.ratecard.RateCardScope@LOCAL.name()}">
                                    <s:hrefLabel url="/pd/monetaryratecard/monetary-rate-card/%{peakRateRateCard.id}" key="ratecardgroup.peakRateRateCard" name="peakRateRateCard.name"
                                             cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                                </s:if>
                                <s:else>
                                    <s:hrefLabel url="/pd/globalratecard/global-rate-card/%{peakRateRateCard.id}" key="ratecardgroup.peakRateRateCard" name="peakRateRateCard.name"
                                             cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                                </s:else>
                            </s:if>
                            <s:else>
                                <s:hrefLabel url="/pd/nonmonetaryratecard/non-monetary-rate-card/%{peakRateRateCard.id}" key="ratecardgroup.peakRateRateCard" name="peakRateRateCard.name"
                                             cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            </s:else>
                        </div>
                   </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="ratecardgroup.offPeak.rate"/></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:if test="%{offPeakRateRateCard.type == @com.elitecore.corenetvertex.constants.RateCardType@MONETARY.name()}">
                                <s:if test="%{peakRateRateCard.type == @com.elitecore.corenetvertex.pd.ratecard.RateCardScope@LOCAL.name()}">
                                    <s:hrefLabel url="/pd/monetaryratecard/monetary-rate-card/%{offPeakRateRateCard.id}" key="ratecardgroup.offPeakRateRateCard" name="offPeakRateRateCard.name"
                                             cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                                </s:if>
                                <s:else>
                                    <s:hrefLabel url="/pd/globalratecard/global-rate-card/%{peakRateRateCard.id}" key="ratecardgroup.peakRateRateCard" name="peakRateRateCard.name"
                                                 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                                </s:else>
                            </s:if>
                            <s:else>
                                <s:hrefLabel url="/pd/nonmonetaryratecard/non-monetary-rate-card/%{offPeakRateRateCard.id}" key="ratecardgroup.offPeakRateRateCard" name="offPeakRateRateCard.name"
                                             cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            </s:else>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-12">
                        <div id="timeBaseConditionDiv">
                            <table id='timeBaseConditionTable'  class="table table-blue table-bordered">
                                <caption class="caption-header">
                                    <span class="glyphicon glyphicon-check"></span>
                                    <s:text name="timeperiod.restricions"/>
                                </caption>
                                <thead>
                                <th><s:text name="timeslotrelation.dayOfWeek"/></th>
                                <th><s:text name="timeslotrelation.timePeriod"/></th>
                                </thead>
                                <tbody>
                                <s:iterator value="timeSlotRelationData">
                                    <tr name='rateCardGroupRow'>
                                        <td class="text-left text-middle">
                                            <s:property value="%{dayOfWeek}"/>
                                        </td>
                                        <td class="text-left text-middle">
                                            <s:property value="%{timePeriod}"/>
                                        </td>
                                    </tr>
                                </s:iterator>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </fieldset>
        </div>


        <div class="row" >
               	<div class="col-xs-12 back-to-list" align="center">
                   	<button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${rncPackageData.id}'" tabindex="6"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.rncpackage"/></button>
               	</div>
        </div>
          
    </div>

</div>