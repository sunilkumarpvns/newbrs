<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="prefixes.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/prefixes"  action="" id="prefixUpdateFormId" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 " elementCssClass="col-xs-12 col-sm-8 " validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:hidden name="accountIds"    		value="%{accountIds}" />
            <s:hidden name="masterPrefixIds"    value="%{masterPrefixIds}" />
            <s:token/>
            <div class="row">
                <div class="col-sm-10">                
               		<s:textfield name="prefix" key="prefixes.prefix"  id="prefix" cssClass="form-control focusElement"  maxlength="100"  tabindex="1"/>
                    <s:textfield name="name" key="prefixes.prefixname" id="name" cssClass="form-control" maxlength="100" tabindex="2"/>
                    <s:textarea name="description" key="prefixes.description" id="description" cssClass="form-control" value="%{description}" maxlength="2000" tabindex="3"/>                    
                    <s:textfield name="countryCode" key="prefixes.countrycode"  type="number" id="countryCode" cssClass="form-control" onkeypress="return isNaturalInteger(event);" maxlength="15"  tabindex="4"/>
                    <s:textfield name="areaCode" key="prefixes.areacode" type="number" id="areaCode" cssClass="form-control" onkeypress="return isNaturalInteger(event);" maxlength="15"  tabindex="5"/>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                    	<s:if test="masterPrefixIds != null">
		          	    	<button type="submit" class="btn btn-primary btn-sm" id="btnSave" tabindex="6" formaction="${pageContext.request.contextPath}/pd/prefixes/prefixes/${id}?masterPrefix=DEFAULT_PREFIX_LIST_MASTER_ID" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        	<button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/prefixes/prefixes/${id}?masterPrefix=DEFAULT_PREFIX_LIST_MASTER_ID'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
			          	</s:if>	  
			          
			          	<s:if test="accountIds != null">
			          		<button type="submit" class="btn btn-primary btn-sm" id="btnSave" tabindex="6" formaction="${pageContext.request.contextPath}/pd/prefixes/prefixes/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
			           		<button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/account/account/${accountIds}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>  
			          </s:if>    
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">

   function validateForm(){
       return verifyUniquenessOnSubmit('name','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.prefixes.PrefixesData','','');      
    } 
 
</script>  