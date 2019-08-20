<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="group.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/group" action="group" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                    <%--<s:hidden name="id" />--%>
                    <s:textfield 	name="name" 		key="group.name" 	id="name"		cssClass="form-control focusElement"
                                    onblur="verifyUniqueness('name','update','%{id}','com.elitecore.corenetvertex.sm.acl.GroupData','','');" tabindex="1" />
                    <s:textarea name="description" 	key="group.description" cssClass="form-control" rows="2" tabindex="2" />
                </div>

                <!-- adding service type associated with Rating Group -->
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/sm/group/group/${id}" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/group/group/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/> </button>
                </div>
            </div>
        </s:form>
    </div>
</div>
</div>
<script type="text/javascript">

    function validateForm(){
        return verifyUniquenessOnSubmit('name','update','<s:text name="id"/>','com.elitecore.corenetvertex.sm.acl.GroupData','','');
    }
</script>