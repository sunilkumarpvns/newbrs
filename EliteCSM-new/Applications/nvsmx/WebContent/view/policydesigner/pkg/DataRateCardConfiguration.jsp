<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 18/4/18
  Time: 5:40 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="row">
    <fieldset class="fieldSet-line">
        <legend align="top"><s:text name="Rate Card"></s:text></legend>
        <div style="text-align: right;">
            <s:if test="%{(@com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() != pkgData.packageMode) && (@com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name() != pkgData.packageMode) }">
                <s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@BASE.name() == pkgData.type}">
                    <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/dataratecard/data-rate-card/new?pkgId=${pkgData.id}'">
                        <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                        <s:text name="Rate Card" />
                    </button>
                </s:if>
            </s:if>
            <s:else>
                <button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px">
                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                    <s:text name="Rate Card" />
                </button>
            </s:else>
        </div>
        <nv:dataTable
                id="DataRateCard"
                list="${rateCardAsJsonString}"
                showPagination="false"
                showInfo="false"
                cssClass="table table-blue"
                width="100%">

            <nv:dataTableColumn title="Name"
                                beanProperty="name"
                                style="font-weight: bold;color: #4679bd;"
                                tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break"
                                hrefurl="${pageContext.request.contextPath}/pd/dataratecard/data-rate-card/$<id>"/>

            <nv:dataTableColumn title="Pulse Unit"
                                beanProperty="pulseUnit"
                                cssClass="qos-th-col-left"
                                tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break" />

            <nv:dataTableColumn title="Rate Unit"
                                beanProperty="rateUnit"
                                cssClass="qos-th-col"
                                tdCssClass="qos-td-cell"	/>

            <s:if test="%{(@com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() != pkgData.packageMode) && (@com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name() != pkgData.packageMode)}">
                <s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@BASE.name() == pkgData.type}">
                    <nv:dataTableColumn title=""
                                        icon="<span class='glyphicon glyphicon-pencil'></span>"
                                        hrefurl="edit:${pageContext.request.contextPath}/pd/dataratecard/data-rate-card/$<id>/edit?pkgId=${pkgData.id}"
                                        style="width:20px;" tdStyle="width:20px;"  />

                    <nv:dataTableColumn title=""
                                        icon="<span class='glyphicon glyphicon-trash'></span>"
                                        hrefurl="delete:${pageContext.request.contextPath}/pd/dataratecard/data-rate-card/$<id>?_method=DELETE&pkgId=${pkgData.id}"
                                        style="width:20px;" tdStyle="width:20px;"  />
                </s:if>
            </s:if>
            <s:else>
                <nv:dataTableColumn title=""
                                    icon="<span disabled='disabled' class='glyphicon glyphicon-pencil'></span>"
                                    style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />

                <nv:dataTableColumn title=""
                                    icon="<span disabled='disabled' class='glyphicon glyphicon-trash'></span>"
                                    style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
            </s:else>
        </nv:dataTable>
    </fieldset>
</div>