<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv"%>

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
			<s:property value="fromIsoCode" />
		</h3>
		 <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History" onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/currency/currency/${id}'">
					<span class="glyphicon glyphicon-eye-open"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"  onclick="javascript:location.href='${pageContext.request.contextPath}/pd/currency/currency/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/currency/currency/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
	</div>
	<div class="panel-body">
		<div class="col-sm-6">
          <div class="row">
			<div class="col-sm-12">
			    <s:label key="currency.fromIsoCode" value="%{fromIsoCode}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
				<s:label key="currency.toIsoCode"  value="%{toIsoCode}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
				<s:label key="currency.rate"  value="%{rate}" cssClass="control-label light-text " labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
				<s:label key="currency.effectiveDate" value="%{effectiveDate}" cssClass="control-label light-text effectiveDate" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
			</div>
		   </div>
		</div>
		
		  <div class="col-sm-6 leftVerticalLine">
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

		<div class="row">
			<div class="col-xs-12" align="center">
				<button type="button" class="btn btn-primary btn-sm back-to-list " id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/currency/currency'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list" /></button>
			</div>
		</div>
	</div>
</div>

<script>
  var effectiveDateDates = $(".effectiveDate");
  for(var index = 0 ; index < effectiveDateDates.length ; index++){
	  var effectiveDateDom = effectiveDateDates[index];
	  var effectiveDateDomText = effectiveDateDom.textContent;
	  effectiveDateDom.textContent = effectiveDateDomText.split(" ")[0];
  }
</script>
