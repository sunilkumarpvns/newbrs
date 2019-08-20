<%--
  Created by IntelliJ IDEA.
  User: dhyani
  Date: 7/2/18
  Time: 6:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="row">
    <div class="col-xs-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><strong><s:text name="database.set.up" /></strong></h3>
            </div>

            <div class="panel-body">

                <s:form cssClass="form-vertical" validate="true" id="ServerManagerStartupForm" action="commons/login/Login/initLogin" validator="validateForm()">
                    <s:if test="%{createDatabaseSchema == true}">
                        <div><span class="glyphicon glyphicon-ok"/> <s:text name="database.user.created"/> <b><s:property value="userName"/></b></div>
                        <div style="margin-left: 25px"><s:text name="database.set.up.connection.url"/> :  <b><s:property value="url"/></b></div>
                    </s:if>
                    <s:if test="%{#request.isSqlExecuted == true}">
                        <div><span class="glyphicon glyphicon-ok"/> <s:text name="database.sql.files.executed"/> <b>(<s:property value="sqlFileNames" />)</b> </div>
                    </s:if>
                    <div><span class="glyphicon glyphicon-ok"/> <s:text name="database.properties.updated"/></div>
                    <div><span class="glyphicon glyphicon-ok"/> <s:text name="database.server.manager.startup"/></div>
                    <hr>

                    <div class="col-xs-12" align="center">
                        <s:submit cssClass="btn btn-primary btn-sm"  value="Next" />
                    </div>
                </s:form>
            </div>
        </div>
    </div>
</div>