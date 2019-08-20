<#--
/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<#--
	Only show message if errors are available.
	This will be done if ActionSupport is used.
-->
<#assign hasFieldErrors = parameters.name?? && fieldErrors?? && fieldErrors[parameters.name]??/>
<div <#rt/><#if parameters.id??>id="wwgrp_${parameters.id}"<#rt/></#if>
class="wwgrp nv-component-div 
<#if parameters.width??>
${parameters.width}
<#else>
col-xs-12
</#if>
">
	
<#if parameters.errorposition?default("bottom") == 'top'>
<#if hasFieldErrors>
<div <#rt/><#if parameters.id??>id="wwerr_${parameters.id}"<#rt/></#if>
<#if parameters.cssErrorClass??>
  class="wwerr ${parameters.cssErrorClass?html} error-div"><#rt/>
<#else>  
 class="wwerr error-div">
</#if>
<#list fieldErrors[parameters.name] as error>
    <div<#rt/>
    <#if parameters.id??>
     errorFor="${parameters.id}"<#rt/>
    </#if>
    class="errorMessage col-xs-12">
             ${error?html}
    </div><#t/>
</#list>
</div><#t/>
</#if>
</#if>

<#if parameters.label??>
<#if parameters.labelposition?default("left") == 'top'>
<div <#rt/>
<#else>
<span <#rt/>
</#if>
<#if parameters.id??>id="wwlbl_${parameters.id}"<#rt/></#if> class="wwlbl nv-label-div">
    <label <#t/>
<#if parameters.id??>
        for="${parameters.id?html}" <#t/>
</#if>


<#if parameters.labelClass??>
	<#if hasFieldErrors>
		class="errorLabel ${parameters.labelClass?html} nv-default-label"<#t/>
	<#else>
		class ="label ${parameters.labelClass?html} nv-default-label" <#t/>
	</#if>
<#else>		
        class="nv-label-default"<#t/>
</#if>
    ><#t/>
<#if parameters.required?default(false)>
        <span class="required">*</span><#t/>
</#if>
        ${parameters.label?html}${parameters.labelseparator!":"?html}
<#include "/${parameters.templateDir}/xhtml/tooltip.ftl" />
	</label><#t/>
<#if parameters.labelposition?default("right") == 'top'>
</div> <br /><#rt/>
<#else>
</span> <#rt/>
</#if>
</#if>
