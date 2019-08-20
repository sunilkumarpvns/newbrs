<%--
  Created by IntelliJ IDEA.
  User: ashish
  Date: 26/7/18
  Time: 4:32 PM
  To change this template use File | Settings | File Templates.
--%>
<s:if test="%{createdByStaff !=null}">
    <s:hrefLabel url="/sm/staff/staff/%{createdByStaff.id}" key="createdby" name="createdByStaff.userName"
                 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
</s:if>
<s:else>
    <s:label key="createdby" value="" cssClass="control-label light-text"
             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
</s:else>
<s:if test="%{createdDate != null}">
    <s:set var="createdByDate">
        <s:date name="%{createdDate}"
                format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
    </s:set>
    <s:label key="createdon" value="%{createdByDate}" cssClass="control-label light-text"
             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
</s:if>
<s:else>
    <s:label key="createdon" value="" cssClass="control-label light-text"
             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
</s:else>


<s:if test="%{modifiedByStaff !=null}">
    <s:hrefLabel url="/sm/staff/staff/%{modifiedByStaff.id}" key="modifiedby" name="modifiedByStaff.userName"
                 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
</s:if>
<s:else>
    <s:label key="modifiedby" value="" cssClass="control-label light-text"
             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
</s:else>
<s:if test="%{modifiedDate != null}">
    <s:set var="modifiedByDate">
        <s:date name="%{modifiedDate}"
                format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
    </s:set>
    <s:label key="lastmodifiedon" value="%{modifiedByDate}" cssClass="control-label light-text"
             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
</s:if>
<s:else>
    <s:label key="lastmodifiedon" value="" cssClass="control-label light-text"
             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
</s:else>

