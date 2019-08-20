<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="slice.conf" />
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/sliceconfig/slice-config/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
                <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Reload Configuration"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/sliceconfig/slice-config/*/reloadDataSliceConfiguration'">
                <span class="glyphicon glyphicon-refresh" ></span>
              </button>
            </span>
            <span class="btn-group btn-group-xs">
                    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/sliceconfig/slice-config/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>

        </div>
    </div>
    <div class="panel-body" >

        <div class="row">
            <div class="col-sm-6">
                <div class="row">
                    <s:set var="priceTag">
                        <s:property value="getText('slice.monetary.reservation')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                    </s:set>
                    <s:label key="priceTag" value="%{monetaryReservation}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" />
                </div>
            </div>
                <div class="col-sm-6 leftVerticalLine">
                    <div class="row">
                        <s:if test="%{modifiedByStaff !=null}">
                            <s:hrefLabel key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6"
                                         url="/sm/staff/staff/%{notificationTemplateData.modifiedByStaff.id}"/>
                        </s:if>
                        <s:else>
                            <s:label key="getText('modifiedby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" />
                        </s:else>
                        <br>
                        <s:if test="%{modifiedDate != null}">
                            <s:set var="modifiedByDate">
                                <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                            </s:set>
                            <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6"/>
                        </s:if>
                    </div>
                </div>
            </div>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="slice.volume" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="slice.percent" value="%{volumeSlicePercentage}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                            <s:label key="slice.minvalue" value="%{volumeMinimumSlice} %{volumeMinimumSliceUnit}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="slice.threshold" value="%{volumeSliceThreshold}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                            <s:label key="slice.maxvalue" value="%{volumeMaximumSlice} %{volumeMaximumSliceUnit}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="slice.time" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="slice.percent" value="%{timeSlicePercentage}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                            <s:label key="slice.minvalue" value="%{timeMinimumSlice} %{timeMinimumSliceUnit}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="slice.threshold" value="%{timeSliceThreshold}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                            <s:label key="slice.maxvalue" value="%{timeMaximumSlice} %{timeMaximumSliceUnit}" cssClass="control-label light-text" labelCssClass="col-xs-6" elementCssClass="col-xs-6" /><br>
                        </div>
                    </div>
                </div>
            </fieldset>
        </div>
    </div>
</div>
