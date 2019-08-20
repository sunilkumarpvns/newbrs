<%@taglib uri="/struts-tags/ec" prefix="s" %>
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
            <s:text name="passwordpolicyconfig.view"/>
        </h3>
        <div class="nv-btn-group" align="right">
            <s:set var="defaultPasswordPolicyId" value="@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@DEFAULT_PASSWORD_POLICY_ID" />
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${defaultPasswordPolicyId}?auditableResourceName=PasswordPolicyConfig&refererUrl=/sm/passwordpolicyconfig/password-policy-config/PASSWORD_POLICY_1'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/passwordpolicyconfig/password-policy-config/${defaultPasswordPolicyId}/edit'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-lg-12">
                <s:if test="validPasswordPolicy == false">
                    <div style="color: red">
                        <s:text name="invalid.password.policy" />
                    </div>
                </s:if>
            </div>
        </div>

        <div class="row">
            <div class="col-sm-6">
                <s:label value="%{passwordRange}" key="passwordpolicyconfig.passwordRange"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>

                <s:label value="%{alphabetRange}" key="passwordpolicyconfig.alphabetRange"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>

                <s:label value="%{digitsRange}" key="passwordpolicyconfig.digitsRange"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>

                <s:label value="%{specialCharRange}" key="passwordpolicyconfig.specialCharRange"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>

                <s:label value="%{prohibitedChars}" key="passwordpolicyconfig.prohibitedChars"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>

                <s:label value="%{passwordValidity}" key="passwordpolicyconfig.passwordValidity"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>

                <s:label value="%{changePwdOnFirstLogin}" key="passwordpolicyconfig.changePwdOnFirstLogin"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>

                <s:label value="%{totalHistoricalPasswords}" key="passwordpolicyconfig.totalHistoricalPasswords"
                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-5" elementCssClass="col-sm-7 col-xs-7"/>


            </div>

            <div class="col-sm-6 leftVerticalLine">
                    <s:hrefLabel key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"
                             url="/sm/staff/staff/%{modifiedByStaff.id}"/>
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
    </div>
</div>
