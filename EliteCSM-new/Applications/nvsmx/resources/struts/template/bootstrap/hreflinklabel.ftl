<#--
/*
 *you need to pass url value by which it'll created url tag of struts & pass that url variable to struts anchor tag
-->

<#if parameters.url??>
    <@s.url value="${parameters.url}" var="urlvar"/>
    <@s.a href="%{#urlvar}">
        <#if parameters.nameValue??>
            <@s.property value="parameters.nameValue"/><#t/>
        </#if>
    </@s.a>
</#if>
<#rt/>
</div>
</div>

