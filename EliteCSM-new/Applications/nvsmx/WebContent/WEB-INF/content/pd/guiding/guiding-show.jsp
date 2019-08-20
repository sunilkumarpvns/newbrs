<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
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
				onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/guiding/guiding/${id}'">				                             
                  <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/guiding/guiding/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/guiding/guiding/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="guiding.guidingname" value="%{guidingName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        	<s:label key="guiding.loblist" value="%{lobData.alias}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        	<s:label key="guiding.servicelist" value="%{serviceData.alias}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="guiding.accountidentifiertype" value="%{accountIdentifierType}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="guiding.accountidentifiervalue" value="%{accountIdentifierValue}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                    	 <div class="row">
                    	 <s:label key="guiding.partnername" value="%{accountData.partnerData.name}"  cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"></s:label>                      
                         <s:label key="guiding.accountnumber" value="%{accountData.name}" id="accountNumberId" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                         <s:label key="guiding.traffictype" value="%{trafficType}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                         <s:label key="guiding.startdate" value="%{startDate}" cssClass="control-label light-text availabilityStartDate" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                         <s:label key="guiding.enddate" value="%{endDate}" cssClass="control-label light-text availabilityStartDate" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                         </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:label key="getText('createdby')" value="%{createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{createdDate != null}">
                                <s:set var="createdByDate">
                                    <s:date name="%{createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                            <s:label key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{modifiedDate != null}">
                                <s:set var="modifiedByDate">
                                    <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                        </div>
                    </div>
                    <div class="row" >
                    	<div class="col-xs-12 back-to-list" align="center">
                        	<button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/guiding/guiding'" tabindex="6"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    	</div>
                	</div>
                </div>
            </fieldset>
        </div>
    </div>
</div>

<script>
  var availabilityStartDateDates = $(".availabilityStartDate");
  for(var index = 0 ; index < availabilityStartDateDates.length ; index++){
	  var availabilityStartDateDom = availabilityStartDateDates[index];
	  var availabilityStartDateDomText = availabilityStartDateDom.textContent;
	  availabilityStartDateDom.textContent = availabilityStartDateDomText.split(" ")[0];
  }
</script>