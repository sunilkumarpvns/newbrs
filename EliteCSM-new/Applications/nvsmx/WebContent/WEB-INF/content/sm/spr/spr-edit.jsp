<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 13/9/17
  Time: 6:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="spr.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/spr" action="spr" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 " elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="spr.name" id="sprName" cssClass="form-control focusElement" onblur="verifyUniqueness('sprName','update','%{id}','com.elitecore.corenetvertex.sm.spr.SprData','','');" />
                    <s:textarea name="description" id="description" rows="2" key="spr.description" cssClass="form-control"  />
                    <s:select 	name="groups"  value="groupValuesForUpdate" 	key="spr.groups"    cssClass="form-control select2"	list="#session.staffBelongingGroups"  id="groupNamesUpdate" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" />
                    <s:select name="databaseId" id="databaseId" key="spr.database" cssClass="form-control" list="databaseDataList" listValue="getName()" listKey="getId()" />
                    <s:textfield name="alternateIdField" key="spr.alternate.id.field" cssClass="form-control" id="alternateIdField" onfocus="getAlternateIdFieldSuggestions()" />
                    <s:select name="spInterfaceId" key="spr.sp.interface" cssClass="form-control" list="spInterfaceDataList" listValue="getName()" listKey="getId()" headerValue="SELECT" headerKey="" />
                    <s:textfield name="batchSize" id="batchSize" type="number" key="spr.batch.size" cssClass="form-control" onkeypress="return isNaturalInteger(event);"  />
                    </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/spr/spr/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/spr/spr/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>


<script type="text/javascript">
    function validateForm(){

        var isValidName = verifyUniquenessOnSubmit('sprName','update','<s:property value="id"/>','com.elitecore.corenetvertex.sm.spr.SprData','','');
        if(isValidName == false) {
            return isValidName;
        }

    }

</script>
<%@include file="spr-utility.jsp"%>