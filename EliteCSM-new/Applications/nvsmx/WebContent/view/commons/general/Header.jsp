<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<!DOCTYPE html>
	<style type="text/css">
		@media(min-width : 768px){
			#nav-header{
				padding-right: 50px;padding-left: 10px;
			}
		}
        @media(max-width:767px){

			.navbar-default .navbar-nav .open .dropdown-menu > li > a {
				color: black;
				outline: 0;
				text-decoration: none;
				background-color: white;
			}

			.navbar-nav .open .dropdown-menu {
				background: white;
				padding: 0px;
			}

			.nav .open > a, .nav .open > a:focus, .nav .open > a:hover {
				color: #4679BD;
			}

			.navbar-default .navbar-nav .open .dropdown-menu > li > a:focus,
			.navbar-default .navbar-nav .open .dropdown-menu > li > a:hover {
				background-color: #4863A0;
				color: white;
			}

			.navbar-default .navbar-nav > .open > a,
			.navbar-default .navbar-nav > .open > a:hover,
			.navbar-default .navbar-nav > .open > a:focus {
				color: white;
				background-color: #4679BD;
			}
		}

	</style>
	<!-- alert configuration -->
	<%@include file="/view/commons/general/Alert.jsp"%>

	<nav class="navbar navbar-default navbar-fixed-top">
      <div class="container-fluid">
        <div id="nav-header" class="navbar-header" style="">
        	<button id="left-menu" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#nv-navbar" >
        		<span class="sr-only"><s:property value="getText('netvertex.menu.navigation')" /></span>
        		<span class="icon-bar" ></span>
        		<span class="icon-bar" ></span>
        		<span class="icon-bar" ></span>
        	</button>
          <a class="navbar-brand" ><span><img alt="Logo" src="${pageContext.request.contextPath}/images/logo.png" height="30px"></span>&nbsp;<s:property value="getText('sterlite.product.name')" /></a></span>
          
        </div>
        <div id="nv-navbar" class="collapse navbar-collapse"  >
			<ul class="nav navbar-nav">
				<li><a href="${pageContext.request.contextPath}/commons/login/Login/welcome">&nbsp;<span class="glyphicon glyphicon-home"></span>&nbsp;</a></li>

				<jsp:include page="MenuTemplate.jsp"/>
				<%--<s:if test="#session.menuType != null">
					<s:iterator value="@com.elitecore.nvsmx.system.menu.MenuBuilder@getMenuItems(#session.menuType)" var="menuItem">
						<jsp:include page="MenuTemplate.jsp"/>
					</s:iterator>
				</s:if>--%>
				
			</ul>
			
			<ul class="nav navbar-nav navbar-right">        
            	<%-- <s:if test="#session.menuType == @com.elitecore.nvsmx.system.constants.ModuleConstants@PARTNERRNC.getVal() ">
				  <li><a href="${pageContext.request.contextPath}/pd/genericpartnerrnc/partner-rnc-generic-search">&nbsp;<span class="glyphicon glyphicon-search"></span>&nbsp;</a></li>
				</s:if>--%>
				
				<s:if test="#session.menuType == @com.elitecore.nvsmx.system.constants.ModuleConstants@POLICYDESIGNER.getVal() ">
				  <li><a href="${pageContext.request.contextPath}/genericSearch/policydesigner/util/Generic/search">&nbsp;<span class="glyphicon glyphicon-search"></span>&nbsp;</a></li>
				</s:if>
            	
            	
            	<li class="dropdown">
              		<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" title="${staffName}" > 
	              		<s:url action="../../commons/staff/ProfilePicture/execute" var="profilePic" includeParams="true" >
							<s:param name="staffId">${loggedInStaffProfilePictureId}</s:param>
						</s:url>
						<s:url action="commons/staff/ProfilePicture/execute" var="profilePic2" includeParams="true" >
							<s:param name="staffId">${loggedInStaffProfilePictureId}</s:param>
						</s:url>


						<img height="32" width="32" src='<s:property value="#profilePic"/>'  onerror="this.onerror=null;this.src='<s:property value="#profilePic2"/>';"    style="border-radius: 50%;" class="hidden-xs" />
              			${staffUsername} <span class="caret"></span>
              		</a>
              		<ul class="dropdown-menu" role="menu">
				  		<li><a href="${pageContext.request.contextPath}/commons/login/Login/serverManagerWelcome"><s:property value="getText('netvertex.menu.servermanager')" /></a></li>

				 		<s:if test="#session.menuType != null">
				 			<%--<s:if test="#session.menuType == @com.elitecore.nvsmx.system.constants.ModuleConstants@POLICYDESIGNER.getVal() ">
					  	  		<li><a href="${pageContext.request.contextPath}/commons/login/Login/partnerRncWelcome"><s:property value="getText('netvertex.menu.partnerrnc')" /></a></li>
							</s:if>--%>
						
						  	<%--<s:elseif test="#session.menuType == @com.elitecore.nvsmx.system.constants.ModuleConstants@PARTNERRNC.getVal() ">
								<li><a href="${pageContext.request.contextPath}/commons/login/Login/welcome"><s:property value="getText('netvertex.menu.policydesigner')" /></a></li>
						 	</s:elseif>--%>
				 		</s:if>
				 
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
