<%@ page import="com.elitecore.nvsmx.system.constants.Results" %>
<%@ page import="com.elitecore.nvsmx.system.menu.MenuBuilder" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style>
        .dropdown-menu{
            min-width: 180px;
        }
        .dropdown-submenu {
            position: relative;
        }

        .dropdown-submenu > .dropdown-menu {
            top: 0;
            left: 100%;
            margin-top: 0px;
            margin-left: -1px;
        }
        .dropdown-menu > li:hover > a, .dropdown-menu > li:focus > a {
            color:white;
        }
        .dropdown-menu > li:focus , .dropdown-menu > li:hover{
            background-color: #4863A0;
        }

        .dropdown-submenu:hover > .dropdown-menu {
            display: block;
            opacity: 1;
        }

        .dropdown-submenu > a:after {
            display: block;
            content: " ";
            float: right;
            width: 0;
            height: 0;
            border-color: transparent;
            border-style: solid;
            border-width: 5px 0 5px 5px;
            border-left-color: #ccc;
            margin-top: 8px;
            margin-right: -10px;
        }

        .dropdown-submenu:hover > a:after {
            border-left-color: #fff;
        }

        .menu-display .dropdown-menu {
            width: 180px;
            border: none;
            margin: 0px;
            background: #fff;
            padding: 0px;
        }

        .sidebarContent {
            padding: 15px;
            background: #fff;
        }

        .menu-display {
            position: fixed;
            right: 0;
            left: 0;
            z-index: 1030;

        }

    </style>

</head>
<% request.setAttribute(Results.MENU_ITEMS.getValue(), MenuBuilder.getServerManagerMenuItems());%>
<div class="menu-display">
    <div class="dropdown open">
        <ul class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu" style="box-shadow: none;">
            <s:iterator value="#request.menuItems" var="menuItem">
                <s:if test="#menuItem.type.toString() == 'CONTAINER'">
                    <li class="dropdown-submenu">
                        <a href="#"><s:property value="title"/></a>
                        <ul class="dropdown-menu">
                            <s:iterator var="menu" value="#menuItem.menuItemList">
                                <s:if test="#menu.type.toString() == 'CONTAINER'">
                                    <li class="dropdown-submenu">
                                        <a href="#"><s:property value="title"/></a>
                                        <ul class="dropdown-menu">
                                            <s:iterator var="submenu" value="#menu.menuItemList">
                                                <li>
                                                    <s:if test='#submenu.url == @com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@HASH'>
                                                        <a href="${submenu.url}"><s:property value="title"/></a>
                                                    </s:if>
                                                    <s:else>
                                                        <a href="${pageContext.request.contextPath}/${submenu.url}"><s:property
                                                                value="title"/></a>
                                                    </s:else>
                                                </li>
                                            </s:iterator>
                                        </ul>
                                    </li>
                                </s:if>
                                <s:else>
                                    <li>
                                        <s:if test='#menu.url == @com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@HASH'>
                                            <a href="${menu.url}"><s:property value="title"/></a>
                                        </s:if>
                                        <s:else>
                                            <a href="${pageContext.request.contextPath}/${menu.url}"><s:property
                                                    value="title"/></a>
                                        </s:else>
                                    </li>
                                </s:else>
                            </s:iterator>
                        </ul>
                    </li>
                </s:if>
                <s:else>
                    <li>
                        <s:if test='#menuItem.url == @com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@HASH'>
                            <a href="${menuItem.url}"><s:property value="title"/></a>
                        </s:if>
                        <s:else>
                            <a href="${pageContext.request.contextPath}/${menuItem.url}"><s:property value="title"/></a>
                        </s:else>
                    </li>
                </s:else>
            </s:iterator>
        </ul>
    </div>
</div>
