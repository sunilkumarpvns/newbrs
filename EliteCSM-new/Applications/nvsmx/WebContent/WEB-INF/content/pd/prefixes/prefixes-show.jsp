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
				onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/prefixes/prefixes/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<s:if test="masterPrefixIds != null">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/prefixes/prefixes/${id}/edit?masterPrefix=DEFAULT_PREFIX_LIST_MASTER_ID'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
			</s:if>
			
			<s:if test="accountIds != null">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/prefixes/prefixes/${id}/edit?accountId=${accountIds}'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
			</s:if>
			
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/prefixes/prefixes/${id}?_method=DELETE&accountId=${accountIds}">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body">      
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                        	<s:label key="prefixes.prefix" value="%{prefix}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        	<s:label key="prefixes.prefixname" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="prefixes.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="prefixes.countrycode" value="%{countryCode}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="prefixes.areacode" value="%{areaCode}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />                           
                  		</div>
                   </div>
                    <div class="col-sm-6 leftVerticalLine">	
                        <div class="row">
                            <s:label key="getText('createdby')" value="%{createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
                            <s:if test="%{createdDate != null}">
                                <s:set var="createdByDate">
                                    <s:date name="%{createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            </s:else>
                            <s:label key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
                            <s:if test="%{modifiedDate != null}">
                                <s:set var="modifiedByDate">
                                    <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            </s:else>
                        </div>
                    </div>
                </div>
            </fieldset>
            <div class="row">
            	<div class="col-xs-12" align="center">  
			          <s:if test="masterPrefixIds != null ">
			          	     <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/prefixes/prefixes'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>           		
			          </s:if>	  
			          
			          <s:if test="accountIds != null ">
			         <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/account/account/${accountIds}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.account"/></button>  
			          </s:if>                  
               </div>
    		</div>
        </div>
    </div>
</div>


