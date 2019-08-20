<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<style>
    .removePadding{
        display: inline;
    }
    .wrap-word{
        word-break: break-all;
        width: 20%;
    }

</style>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="group.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/group" action="group" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:token/>
           <%-- <s:actionerror cssClass="alert alert-error"></s:actionerror>--%>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="group.name" id="name"
                                 cssClass="form-control focusElement"
                                 onblur="verifyUniqueness('name','create','','com.elitecore.corenetvertex.sm.acl.GroupData','','');"
                                 tabindex="1"/>
                    <s:textarea name="description" key="group.description" cssClass="form-control" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"
                                rows="2" tabindex="2"/>

                </div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="5"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="6"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/group/group'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>

</div>
<script type="text/javascript">
    function validateForm(){
        return verifyUniquenessOnSubmit('name','create','','com.elitecore.corenetvertex.sm.acl.GroupData','','');
    }
</script>
