<%@taglib uri="/struts-tags/ec" prefix="s" %>

<li class="dropdown mega-dropdown">
    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
        <s:property value="getText('subscriber.menu.text')"/>
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu mega-dropdown-menu" role="menu">
        <li class="col-sm-4">
            <ul>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/initCreate">
                        <s:property value="getText('add.subscriber.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/search">
                        <s:property value="getText('search.subscriber.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/session/Session/search">
                        <s:property value="getText('search.session.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/searchTestSubscriber">
                        <s:property value="getText('test.subscriber.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/searchDeletedSubscriber">
                        <s:property value="getText('deleted.subscriber.menu.text')"/>
                    </a>
                </li>
            </ul>
        </li>
    </ul>
</li>

<li class="dropdown mega-dropdown">
    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
        <s:property value="getText('configuration.menu.text')"/>
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu mega-dropdown-menu" role="menu">
        <li class="col-sm-4">
            <ul>
                <li class="dropdown-header"><s:property value="getText('packages.menu.text')"/></li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/productoffer/product-offer">
                        <s:property value="getText('product.offer.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/pkg/Pkg/search">
                        <s:property value="getText('data.package.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/rncpackage/rnc-package">
                        <s:property value="getText('rnc.package.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/bodpackage/bod-package">
                        <s:property value="getText('bod.package.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/datatopup/data-topup">
                        <s:property value="getText('data.topup.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/search">
                        <s:property value="getText('ims.package.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/search">
                        <s:property value="getText('emergency.package.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/promotional/policydesigner/pkg/Pkg/PromotionalPkg/search?pkgType=PROMOTIONAL">
                        <s:property value="getText('promotional.package.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan">
                        <s:property value="getText('monetary.recharge.plan.menu.text')"/>
                    </a>
                </li>
            </ul>
        </li>
        <li class="col-sm-4">
            <ul>
                <li class="dropdown-header"><s:property value="getText('global.entities.menu.text')"/></li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/search">
                        <s:property value="getText('global.pcc.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/globalratecard/global-rate-card">
                        <s:property value="getText('global.monetary.ratecard.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/search">
                        <s:property value="getText('charging.rule.base.name.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/search">
                        <s:property value="getText('rating.group.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/sliceconfig/slice-config/*">
                        <s:property value="getText('data.slice.configuration.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/pd/service/service">
                        <s:property value="getText('service.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/search">
                        <s:property value="getText('data.servicetype.menu.text')"/>
                    </a>
                </li>

                <li>
                    <a href="${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail">
                        <s:property value="getText('revenue.menu.text')"/>
                    </a>
                </li>


            </ul>
        </li>
        <li class="col-sm-4">
            <ul>
                <li class="dropdown-header"><s:property value="getText('others.menu.text')"/></li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/util/Generic/showProgressBar?redirectUrl=ajax/pkgReload/reload">
                        <s:property value="getText('reload.policy.menu.text')"/>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/search">
                        <s:property value="getText('notification.template.menu.text')"/>
                    </a>
                </li>
            </ul>
        </li>
    </ul>
</li>