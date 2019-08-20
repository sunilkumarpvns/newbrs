<%@page import="com.elitecore.nvsmx.system.constants.Results"%>
<%@page import="com.elitecore.nvsmx.system.menu.MenuBuilder"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<% request.setAttribute(Results.MENU_ITEMS.getValue(),MenuBuilder.getServerManagerMenuItems());%>

<!DOCTYPE html>

<style type="text/css">
  @media(min-width : 768px){
    #nav-header{
      padding-right: 50px;padding-left: 10px;
    }
    #sidebar-nav{
      visibility: hidden;
      display: none;
    }
  }

  @media(max-width: 767px){
    #sidebar-nav{
      visibility: visible;

    }
    .navbar-default .navbar-nav> div > li {
      border-top: 1px solid #E7E7E7;
    }
    .navbar-default .navbar-nav > div > li > a:hover,
    .navbar-default .navbar-nav > div > li > a:focus {
      color: white;
      background-color: #4679BD;
    }
    .nav > div > li > a {
      position: relative;
      display: block;
      padding: 10px 15px;
    }
    .navbar-default .navbar-nav > div > li > a {
      color: white;
      outline: 0;
      text-decoration: none;
    }
    .navbar-default .navbar-nav .open .dropdown-menu > li > a {
      color: black;
      outline: 0;
      text-decoration: none;
      background-color: white;
    }
    .navbar-nav .open .dropdown-menu {
      background: white;
      padding:0px;

    }
    .nav .open > a, .nav .open > a:focus, .nav .open > a:hover {
      color:#4679BD;

    }
    .navbar-default .navbar-nav .open .dropdown-menu>li>a:focus, .navbar-default .navbar-nav .open .dropdown-menu>li>a:hover {
      background-color:#4863A0;
      color:white;
    }

  }
</style>

<!-- alert configuration -->
<%@include file="/view/commons/general/Alert.jsp"%>

<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <div id="nav-header" class="navbar-header">
      <button id="left-menu" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#nv-navbar" >
        <span class="sr-only"><s:property value="getText('netvertex.menu.navigation')" /></span>
        <span class="icon-bar" ></span>
        <span class="icon-bar" ></span>
        <span class="icon-bar" ></span>
      </button>
      <a class="navbar-brand" ><span><img alt="Logo" src="${pageContext.request.contextPath}/images/logo.png" height="30px"></span>&nbsp;<s:property value="getText('sterlite.product.name')" /></a></span>

    </div>
    <div id="nv-navbar" class="collapse navbar-collapse">
      <ul class="nav navbar-nav">
        <li><a href="${pageContext.request.contextPath}/commons/login/Login/serverManagerWelcome">&nbsp;<span class="glyphicon glyphicon-home"></span>&nbsp;</a></li>
        <div id="sidebar-nav">
          <s:iterator value="@com.elitecore.nvsmx.system.menu.MenuBuilder@getServerManagerMenuItems()" var="menuItem">
            <s:if test="#menuItem.type.toString() == 'CONTAINER'">
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><s:property value="title"/> </span>  <span class="caret"></span></a>
                <ul class="dropdown-menu" role="menu" >
                  <s:iterator var="menu" value="#menuItem.menuItemList">
                    <s:if test="#menu.type.toString() == 'CONTAINER'">
                      <li>
                        <a href="#"><s:property value="title"/></a>
                        <ul class="dropdown-menu" role="menu">
                          <s:iterator var="submenu" value="#menu.menuItemList">
                            <li>
                              <s:if test='#submenu.url == @com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@HASH'>
                                <a href="${submenu.url}"><s:property value="title"/></a>
                              </s:if>
                              <s:else>
                                <a href="${pageContext.request.contextPath}/${submenu.url}"><s:property value="title"/></a>
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
                          <a href="${pageContext.request.contextPath}/${menu.url}"><s:property value="title"/></a>
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
        </div>
      </ul>

      <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" title="${staffName}" >
            <s:url action="../../commons/staff/ProfilePicture/execute" var="profilePic" includeParams="true" >
              <s:param name="staffId">${loggedInStaffProfilePictureId}</s:param>
            </s:url>
            <img height="32" width="32" src='<s:property value="#profilePic"/>' style="border-radius: 50%;" class="hidden-xs"/>
            ${staffUsername} <span class="caret"></span>
          </a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="${pageContext.request.contextPath}/commons/login/Login/welcome"><s:property value="getText('netvertex.menu.policydesigner')" /></a></li>
	        <%--<li><a href="${pageContext.request.contextPath}/commons/login/Login/partnerRncWelcome"><s:property value="getText('netvertex.menu.partnerrnc')" /></a></li>--%>
          <%--  <s:iterator value="@com.elitecore.nvsmx.system.menu.MenuBuilder@getCommonMenuItems()" var="commonMenuItem">
              <li><a href="${pageContext.request.contextPath}/${commonMenuItem.url}">
                <s:property value="title"/></a></li>
            </s:iterator>--%>
            <s:iterator value="@com.elitecore.nvsmx.system.menu.MenuBuilder@getCommonMenuItems()" var="commonMenuItem">

              <li>
                <s:if test="%{linkType.name() == 'EXTERNAL'}">
                  <a href="${commonMenuItem.url}"><s:property value="title"/></a>
                </s:if>
                <s:else>
                  <a href="${pageContext.request.contextPath}/${commonMenuItem.url}"><s:property value="title"/></a>
                </s:else>

              </li>
            </s:iterator>
          </ul>
        </li>
      </ul>

    </div>
  </div>
</nav>
<br><br>
