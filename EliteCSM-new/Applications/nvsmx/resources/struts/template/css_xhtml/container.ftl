<#--  
<div class="container-fluid nv-container">
	<div class="row nv-row">
		<@s.include value="/view/commons/Header.jsp" /><#rt/>
		<div class="col-xs-12 nv-center"> <#rt/>
		<#if parameters.leftMenu??>
			<div class="col-xs-3 col-md-2 nv-left">
				<@s.include value="${parameters.leftMenu?html}"/> <#rt/>
			</div>
			<div class="col-xs-9 col-md-10 nv-main-content">  <#rt/>
		<#else>
			<div class="col-xs-12 col-md-12 nv-main-content">  <#rt/>
		</#if>
		<#if parameters.content??>
			<@s.include value="${parameters.content?html}"/> <#rt/>
		</#if>
			</div>
		</div>
			<@s.include value="/view/commons/Footer.jsp"/><#rt/>
	</div>
</div>
 -->


	<div class="col-xs-12 nv-center-content">
		<#if parameters.leftMenu??>
			<div class="col-xs-3 col-md-2 nv-left">
				<@s.include value="${parameters.leftMenu?html}"/> <#rt/>
			</div>
			<div class="col-xs-9 col-md-10 nv-main-content">  <#rt/>
		<#else>
			<div class="col-xs-12 col-md-12 nv-main-content">  <#rt/>
		</#if>
		<#if parameters.content??>
			<@s.include value="${parameters.content?html}"/> <#rt/>
		</#if>
			</div>
	</div>
