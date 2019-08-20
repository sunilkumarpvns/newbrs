<!DOCTYPE html>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<html>
<head>
	
  <% 
  	String basePath = request.getContextPath(); 
  	String ip = request.getLocalAddr();
  	int port = request.getLocalPort();
  	String uname = (String) request.getAttribute("usrname");
  	String pwd = (String) request.getAttribute("pwd");

  %>	
 	
  <meta charset="utf-8">
  <title>EliteCSM - Schema Form</title>
  <link REL="SHORTCUT ICON" HREF="<%=basePath%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
  
  <%@ include file="init-css.jsp" %>	

</head>
<body ng-app="test" ng-controller="TestCtrl" class="body-background" ng-cloak >
  <nav class="navbar-custom">
  <div class="container-fluid">
    <div id="bs-example-navbar-collapse-1" class="col-md-6 col-md-offset-5">
      <form class="navbar-form " role="search">
        <div class="form-group">
        	<div style="display: inline;font-size: 20px;">
				EliteCSM Schema Form
			</div>
      
          <div class="input-group" ng-show="navbarMode === 'default' && !error"></div>
          <div class="input-group" ng-show="navbarMode === 'loaded' && !error">
            <span ng-show="loadedData.user">Loaded configuration created by {{loadedData.user}} {{loadedData.created}}.</span>
            <div ng-show="!loadedData" class="spinner"></div>
          </div>

          <div class="input-group" ng-show="navbarMode === 'saved' && savedGistData.url && !error">
            <div class="input-group-addon">Link</div>
            <input type="text" class="form-control" value="{{savedGistData.url}}">
            <span ng-if="hasFlash" class="input-group-btn">
              <button tooltip-trigger="focus" tooltip-placement="right" tooltip='Copied to clipboard' clip-copy="savedGistData.url" class="btn btn-default copy" type="button"><span class="glyphicon glyphicon-copy" aria-hidden="true"></span></button>
            </span>
          </div>

          <div class="input-group" ng-show="navbarMode === 'saved' && savedGistData.data.html_url && !error">
            <div class="input-group-addon">Gist</div>
            <input type="text" class="form-control" value="{{savedGistData.data.html_url}}">
            <span ng-if="hasFlash" class="input-group-btn">
              <button tooltip-trigger="focus" tooltip-placement="right" tooltip='Copied to clipboard' clip-copy="savedGistData.data.html_url" class="btn btn-default copy" type="button"><span class="glyphicon glyphicon-copy" aria-hidden="true"></span></button>
            </span>
          </div>

          <div class="input-group" ng-show="navbarMode === 'saved' && !savedGistData && !error">
            <div class="spinner"></div>
          </div>

          <div class="input-group error" ng-show="error">{{error}}</div>
        </div>
      </form>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>

<div class="col-md-10 col-md-offset-1">
  <h1></h1>
  
  <div class="panel">
    <div class="panel-body" >

	<h4>Module Name</h4>
      <div class="form-group">
        <select class="form-control" id="selectTest"
                ng-model="selectedTest"
                ng-options="obj.name group by obj.type for obj in tests">
        </select>
        
      </div>

		<div class="panel panel-default">
			<div class="panel-heading" id="panel-heading">{{selectedTest.name}} - Schema Form</div>
			<div class="panel-body">
				
				<form name="ngform"  sf-model="modelData" sf-form="form" sf-schema="schema" ng-submit="submitForm(ngform,modelData)"></form>
      			
      			<div ng-show="ngform.$valid"></div>
      			<div ng-show="ngform.$invalid"></div>
				
				<form>
				<div class="panel panel-default">
					<div class="panel-heading">
						<div class="row">
						    <div class="col-xs-2">
						    	<select id = "methodId" ng-model="methodValue" class="form-control">
						    		<option value="GET">GET</option>
						    		<option value="POST">POST</option>
						    		<option value="PUT">PUT</option>
						    		<option value="DELETE">DELETE</option>
						    	</select>
						    	
							</div>
						    <div class="col-xs-7">
						    	<input property="url" type="text" styleId="url"  ng-model = "urlValue" class="form-control" required="required"/>
						    </div>
						    <div class="col-xs-2">
						    	<input id="moduleName" type="text" class="form-control" placeholder="Name" ng-model="moduleName">
						    </div>
						    <div class="col-xs-1">
						    	<button type="button" class="btn btn-default" ng-click="sendData()">Send</button>
						    </div>
						</div>
					</div>
					<div class="panel-body">
						<div class="row">
							<div class="col-md-4"></div>
							<div class="col-md-4">
								<form class="input-group">
									 <div class="input-group">
									    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
									    <input id="username" type="text" class="form-control" name="username" placeholder="Username" ng-model = "userName" required="required"/>
									 </div>
									 <div class="input-group" style="margin-top:5px;">
									    <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
									    <input id="password" type="password" class="form-control" name="password" placeholder="Password" ng-model = "passWord"  required="required"/>
									 </div>
								</form>
							</div>
							<div class="col-md-4"></div>
						</div>
					</div>
				</div>
			</form>
				<h4>Body</h4>
     		
	      		<pre ng-cloak id="clipboard-div">{{pretty()}}</pre>
	      		
				<button class="btn" onclick="copyToClipboard('clipboard-div')">
					 Copy to clipboard
				</button>
				
				<h4>Response</h4>
				
				<pre ng-cloak id="response-div">{{response()}}</pre>
				
			</div>
		</div>
    </div>
  </div>
</div>

<script type="text/ng-template" id="template/tooltip/tooltip-popup.html">
  <div class="tooltip {{placement}} {{class}}" ng-class="{ in: isOpen(), fade: animation() }">
    <div class="tooltip-arrow"></div>
    <div class="tooltip-inner" ng-bind="content"></div>
  </div>
</script>

<!-- Include all common js -->
<%@ include file="init-js.jsp" %>	

<script type="text/javascript">

var app = angular.module('test', ['schemaForm', 'ui.ace', 'ngClipboard', 'ui.bootstrap.tooltip', 'schemaForm-tinymce']);
app.config(['ngClipProvider', function(ngClipProvider) {
   // ngClipProvider.setPath('//cdnjs.cloudflare.com/ajax/libs/zeroclipboard/2.2.0/ZeroClipboard.swf');
}]);
app.controller('TestCtrl', function($scope, $http, $location) {

  $scope.tests = [
    
    { name: "Radius Service Policy", data: '<%=basePath%>/jsp/schemaform/data/radius-service-policy.json', type : 'Service Policy', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/radiusservicepolicy/'},
    { name: "DynAuth Policy", data: '<%=basePath%>/jsp/schemaform/data/ldap-datasource.json', type : 'Service Policy', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/'},
    { name: "3GPP AAA Policy", data: '<%=basePath%>/jsp/schemaform/data/tgpp-aaa-policy.json', type : 'Service Policy', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
    { name: "NAS Policy", data: '<%=basePath%>/jsp/schemaform/data/nas-policy.json', type : 'Service Policy', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/ '},
    { name: "Credit Control Policy", data: '<%=basePath%>/jsp/schemaform/data/credit-control-policy.json', type : 'Service Policy', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/'},
    { name: "EAP Policy", data: '<%=basePath%>/jsp/schemaform/data/eap-policy.json', type : 'Service Policy', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
    { name: "Charging Policy", data: '<%=basePath%>/jsp/schemaform/data/charging-policy.json', type : 'Service Policy', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
	
	{ name: "EliteAAA Server", data: '<%=basePath%>/jsp/schemaform/data/Eliteaaa-server.json', type : 'Server Instance', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/elitecsmserver/configuration/eliteaaaserver/' },
    { name: "Clients", data: '<%=basePath%>/jsp/schemaform/data/eliteaaa-clients.json', type : 'Server Instance' , url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/elitecsmserver/configuration/clients/'},
	{ name: "Diameter Stack", data: '<%=basePath%>/jsp/schemaform/data/Diameter stack.json', type : 'Server Instance', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/elitecsmserver/configuration/diameterstack/'},
	{ name: "VSA in Class Attribute", data: '<%=basePath%>/jsp/schemaform/data/eliteaaa-vsa-in-class-attribute.json', type : 'Server Instance', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/elitecsmserver/configuration/vsa/' },
	
	{ name: "RAD-AUTH", data: '<%=basePath%>/jsp/schemaform/data/rad-auth-service.json', type : 'Configured Services', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/elitecsmserver/services/radauth/'},
	{ name: "RAD-ACCT", data: '<%=basePath%>/jsp/schemaform/data/rad-acct-service.json', type : 'Configured Services', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/elitecsmserver/services/radacct/'},
	{ name: "RAD-DYNAUTH", data: '<%=basePath%>/jsp/schemaform/data/raddynauth-service.json', type : 'Configured Services', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/elitecsmserver/services/raddynauth/'},
	
	
    { name: "Server Instance", data: '<%=basePath%>/jsp/schemaform/data/server-instance.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/server/'},
    { name: "Session Manager", data: '<%=basePath%>/jsp/schemaform/data/session-manager.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/sessionmanager/ ' },
    { name: "Digest Configuration", data: '<%=basePath%>/jsp/schemaform/data/digest-configurations.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/digestconfiguration/' },
    
	{ name: "File Listener", data: '<%=basePath%>/jsp/schemaform/data/filetrap-listener.json',type : 'Alert Configuration', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/alertconfiguration/filelistener/'},
	{ name: "Trap Listener", data: '<%=basePath%>/jsp/schemaform/data/trap-listener.json',type : 'Alert Configuration', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/alertconfiguration/traplistener/'},
	{ name: "Syslog Listener", data: '<%=basePath%>/jsp/schemaform/data/syslog-listener.json',type : 'Alert Configuration', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/alertconfiguration/sysloglistener/' },
    { name: "EAP Configuration", data: '<%=basePath%>/jsp/schemaform/data/eap-configuration.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/ ' },
    { name: "Access Policy", data: '<%=basePath%>/jsp/schemaform/data/access-policy.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/accesspolicy/' },
    { name: "Grace Policy", data: '<%=basePath%>/jsp/schemaform/data/grace-policy.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/gracepolicy/'},
    { name: "Translation Mapping Config", data: '<%=basePath%>/jsp/schemaform/data/translation-mapping.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/translationmapping/' },
    { name: "Copy Packet Config", data: '<%=basePath%>/jsp/schemaform/data/copy-packet-config.json',type : 'Server/Config' , url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/copypacket/'},
    { name: "SSL Certificate", data: '<%=basePath%>/jsp/schemaform/data/ssl-certificate.json',type : 'Server/Config', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
    
    { name: "Quota Management Plugin", data: '<%=basePath%>/jsp/schemaform/data/quota-management-plugin.json',type : 'Plugins', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
    { name: "Radius Groovy Plugin", data: '<%=basePath%>/jsp/schemaform/data/radius-groovy-plugin.json',type : 'Plugins', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/'},
    { name: "Radius Transaction Logger", data: '<%=basePath%>/jsp/schemaform/data/radius-transaction-logger.json',type : 'Plugins', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/transactionloggerplugin/radius/' },
    { name: "Universal Acct Plugin", data: '<%=basePath%>/jsp/schemaform/data/universal-acct-plugin.json',type : 'Plugins', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/universalplugin/acct/' },
    { name: "Universal Auth Plugin", data: '<%=basePath%>/jsp/schemaform/data/universal-auth-plugin.json',type : 'Plugins', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/universalplugin/auth/' },
    { name: "User Statistic Post Auth Plugin", data: '<%=basePath%>/jsp/schemaform/data/user-stat-post-auth-plugin.json',type : 'Plugins', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/'  },
    { name: "Diameter Groovy Plugin", data: '<%=basePath%>/jsp/schemaform/data/diameter-groovy-plugin.json',type : 'Plugins', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
    { name: "Diameter Transaction Logger", data: '<%=basePath%>/jsp/schemaform/data/diameter-transaction-logger.json',type : 'Plugins' , url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/transactionloggerplugin/diameter/'},
    { name: "Universal Diameter Plugin", data: '<%=basePath%>/jsp/schemaform/data/universal-diameter-plugin.json',type : 'Plugins' , url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/'},
    
    { name: "Radius DB Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-db-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/dbauth/' },
    { name: "Radius Http Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-http-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/httpauth/' },
    { name: "Radius LDAP Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-ldap-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/ldapauth/' },
    { name: "Radius Map Gateway Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-mapgw-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/mapgwauth/' },
    { name: "Radius HSS Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-hss-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/hssauth/' },
    { name: "Radius User File Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-userfile-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/userfileauth/'},
    { name: "Radius Web Service Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-webservice-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/webserviceauth/' },
    { name: "Radius Classic CSV Acct Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-classiccsv-acct-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/classiccsv/' },
    { name: "Radius DB Acct Driver", data: '<%=basePath%>/jsp/schemaform/data/radius-db-acct-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/radius/dbacct/' },
   
    { name: "Diameter DB Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-db-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/dbauth/' },
    { name: "Diameter Http Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-http-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/httpauth/' },
    { name: "Diameter LDAP Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-ldap-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/ldapauth/' },
    { name: "Diameter Map Gateway Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-mapgw-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/mapgwauth/' },
    { name: "Diameter HSS Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-hss-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/hssauth/' },
    { name: "Diameter User File Auth Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-userfile-auth-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/userfileauth/' },
    { name: "Diameter Classic CSV Acct Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-classiccsv-acct-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/classiccsv/'},
    { name: "Diameter DB Acct Driver", data: '<%=basePath%>/jsp/schemaform/data/diameter-db-acct-driver.json',type : 'Service Drivers', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/driver/diameter/dbacct/' },
    
    { name: "Radius Policy", data: '<%=basePath%>/jsp/schemaform/data/radius-policy.json',type : 'RADIUS', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/radiuspolicy/' },
    { name: "Radius Policy Group", data: '<%=basePath%>/jsp/schemaform/data/radius-policy-group.json',type : 'RADIUS', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/radiuspolicygroup/' },
    { name: "Blacklist Candidates", data: '<%=basePath%>/jsp/schemaform/data/blacklist-candidates.json',type : 'RADIUS', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
    { name: "Trusted Client Profile", data: '<%=basePath%>/jsp/schemaform/data/trusted-client-profile.json',type : 'RADIUS', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/clientprofile/'},
    { name: "Radius ESI Group", data: '<%=basePath%>/jsp/schemaform/data/radius-esi-group.json',type : 'RADIUS', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/radiusesigroup/' },
    
    { name: "Authorization Policies", data: '<%=basePath%>/jsp/schemaform/data/authorization-policies.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/authorizationpolicy' },
    { name: "Diameter Policy Group", data: '<%=basePath%>/jsp/schemaform/data/diameter-policy-group.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/diameterpolicygroup/' },
    { name: "Diameter Peer Profiles", data: '<%=basePath%>/jsp/schemaform/data/diameter-peer-profiles.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/diameterpeerprofiles/'},
    { name: "Diameter Peers", data: '<%=basePath%>/jsp/schemaform/data/diameter-peers.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/diameterpeers/' },
    { name: "Diameter Peer Group", data: '<%=basePath%>/jsp/schemaform/data/diameter-peer-group.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/diameterpeergroup/' },
    { name: "Diameter Routing Table", data: '<%=basePath%>/jsp/schemaform/data/diameter-routing-table.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/diameterroutingtable/' },
    { name: "Subscriber Routing Table", data: '<%=basePath%>/jsp/schemaform/data/subscriber-routing-table.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
    { name: "Priority Table", data: '<%=basePath%>/jsp/schemaform/data/priority-table.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/prioritytable' },
    { name: "Diameter Session Manager", data: '<%=basePath%>/jsp/schemaform/data/diameter-session-manager.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/'},
    { name: "Diameter Concurrency", data: '<%=basePath%>/jsp/schemaform/data/diameter-concurrency.json',type : 'Diameter', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/diameterconcurrency/'},
   
    { name: "IP Pool", data: '<%=basePath%>/jsp/schemaform/data/ip-pool.json',type : 'Resource Manager', url :'http://<%=ip%>:<%=port%>/ ' },
    { name: "Concurrent Login Policy", data: '<%=basePath%>/jsp/schemaform/data/concurrent-login-policy.json',type : 'Resource Manager', url :'http://<%=ip%>:<%=port%>/' },
   
    { name: "Database Datasource", data: '<%=basePath%>/jsp/schemaform/data/database-datasource.json', type : 'External System', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/datasource/'},
    { name: "LDAP Datasource", data: '<%=basePath%>/jsp/schemaform/data/ldap-datasource.json', type : 'External System', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/ldapdatasource/' },
    { name: "Extended RADIUS", data: '<%=basePath%>/jsp/schemaform/data/extended-radius.json', type : 'External System', url :'http://<%=ip%>:<%=port%>/aaasmx/cxfservices/restful/v1/' },
  
];

  $scope.navbarMode = 'default';
  $scope.hasFlash = swfobject.hasFlashPlayerVersion('9');
  if ('<%=uname%>' != 'null') {
	  $scope.userName = '<%=uname%>';
  } 
  if ('<%=pwd%>' != 'null') {
  $scope.passWord = '<%=pwd%>'
  }

  // Load data from gist.
  if ($location.path().length > 4) {
    $scope.navbarMode = 'loaded';
    var gistId = $location.path().replace('/','');
    $scope.loading = true;
    $http.get('https://api.github.com/gists/' + gistId).success(function(res) {
      $scope.error = null;
      $scope.tests.unshift({name: 'Gist'});
      $scope.selectedTest = $scope.tests[0];
      $scope.loadedData = {
        created: moment(res.created_at).fromNow(),
        user: res.user !== null ? res.user.login : 'Anonymous'
      }
      $scope.loading = false;
      $scope.schemaJson = res.files['schema.json'].content;
      $scope.formJson   = res.files['form.json'].content;
      $scope.modelData  = JSON.parse(res.files['model.json'].content);
    }).error(function() {
      $scope.loadedData = 'dummy';
      $scope.error = 'Failed to load gist.';
      $scope.selectedTest = $scope.tests[0];
    });
  } else {
    $scope.selectedTest = $scope.tests[0];
  }

  $scope.$watch('selectedTest',function(val){
    if (val && val.data !== undefined) {
    	
    	/* This code will remove selected text */
    	var sel = window.getSelection ? window.getSelection() : document.selection;
    	if (sel) {
    	    if (sel.removeAllRanges) {
    	        sel.removeAllRanges();
    	    } else if (sel.empty) {
    	        sel.empty();
    	    }
    	}
    	console.log(val.url);
    	
    	$scope.urlValue = val.url;
    	$('#url').val(val.url);
    	
    	$http.get(val.data).then(function(res) {setNewData(res.data);});
    }
  });

  $scope.decorator = 'bootstrap-decorator';

  $scope.itParses     = true;
  $scope.itParsesForm = true;


  $scope.$watch('schemaJson',function(val,old){
    if (val && val !== old) {
      try {
        $scope.schema = JSON.parse($scope.schemaJson);
        $scope.itParses = true;
      } catch (e){
        $scope.itParses = false;
      }
    }
  });

  $scope.$watch('formJson',function(val,old){
    if (val && val !== old) {
      try {
        $scope.form = JSON.parse($scope.formJson);
        $scope.itParsesForm = true;
      } catch (e){
        $scope.itParsesForm = false;
      }
    }
  });

  var setNewData = function(data) {
    
	  $scope.schema = data.schema;
	    $scope.form   = data.form;	
	    $scope.schemaJson = JSON.stringify($scope.schema,undefined,2);
	    $scope.formJson   = JSON.stringify($scope.form,undefined,2);
	    $scope.modelData = {};
	    $scope.responseData  = "";
	    $scope.methodValue = "GET";

	    if( data.model ){
	    	 $scope.modelData = data.model;
	    }else {
	    	 $scope.modelData = data.model || {};
	    }
	   
  };
  
  $scope.sendData = function() {
      var method = $scope.methodValue;
      
      if ($scope.urlValue == null) {
		  alert('Enter valid URL to request.');
		  return false;
	  }
	  
	  if (method != "POST") {
	  if ($scope.moduleName == null) {
		  alert('Enter Module Name.');
		  return false;
	  }}
	  
	  if ($scope.userName == null) {
		  alert('Username must be specified.');
		  return false;
	  }
	  
	  if ($scope.passWord == null) {
		  alert('Password must be specified.');
		  return false;
	  }
      
      
      var methodUrl = '<%=basePath%>/schemaformaction.do?method=getXmlData';
      var config = {
          headers: {
              'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
          }
      }
	  
      var module = '';
      
      if ($scope.moduleName != null) {
    	 module = $scope.moduleName.trim(); 
      }
      
      var data = $.param({
    	  Url: $scope.urlValue.trim() + module,
          UserName: $scope.userName,
          PassWord: $scope.passWord,
          Data: JSON.stringify($scope.modelData)

      });
      switch (method) {
          case "GET":
              methodUrl = '<%=basePath%>/schemaformaction.do?method=getModuleData';
              data = $.param({
            	  Url: $scope.urlValue.trim() + module,
                  UserName: $scope.userName,
                  PassWord: $scope.passWord,
              });
			
              break;
          case "DELETE":
              data = $.param({
            	  Url: $scope.urlValue.trim() + module,
                  UserName: $scope.userName,
                  PassWord: $scope.passWord
              });
              methodUrl = '<%=basePath%>/schemaformaction.do?method=deleteModuleData';
              break;
          case "POST":
              methodUrl = '<%=basePath%>/schemaformaction.do?method=createModuleData';
              break;
          case "PUT":
              methodUrl = '<%=basePath%>/schemaformaction.do?method=updateModuleData';
              break;

          default:
        	  $scope.error = 'Invalid Http Method Selection';
              break;
      }
      
      $http.post(methodUrl, data, config).success(function(data) {
          if (method == "GET") {
              $scope.modelData = {};
              $scope.modelData = data;
              $scope.responseData = "";
          } else {
              $scope.responseData = data;
          }

      }).error(function() {
          $scope.error = 'Error occur while requesting.';

      });

  };
  
  $scope.pretty = function(){
    return typeof $scope.modelData === 'string' ? $scope.modelData : JSON.stringify($scope.modelData, undefined, 2);
  };
  
  $scope.response = function(){
	    return typeof $scope.responseData === 'string' ? $scope.responseData : JSON.stringify($scope.responseData, undefined, 2);
	  };
  
  $scope.prettyText = function(){
	    return typeof $scope.modelData === 'string' ? $scope.modelData : JSON.stringify($scope.modelData, undefined, 2);
  };

  $scope.log = function(msg){
    console.log("Simon says",msg);
  };

  $scope.sayNo = function() {
    alert('Noooooooo');
  };

  $scope.say = function(msg) {
    alert(msg);
  };

  $scope.save = function() {
    // To be able to save invalid json and point out errors, we
    // don't save the schema/form but rather the ones in the input.

    $scope.navbarMode = 'saved';

    var gist = {
      "description": "A saved configuration for a schema form example, http://textalk.github.io/angular-schema-form/examples/bootstrap-example.html",
      "public": true,
      "files": {
        "schema.json": {
          "content": $scope.schemaJson
        },
        "form.json": {
          "content": $scope.formJson
        },
        "model.json": {
          "content": JSON.stringify($scope.modelData, undefined, 2)
        }
      }
    };

    $http.post('https://api.github.com/gists', gist).success(function(data) {
      $scope.error = null;
      $location.path('/' + data.id);
      $scope.savedGistData = {
        data: data,
        url: $location.absUrl()
      };
    }).error(function() {
      $scope.error = 'Failed to save gist.';
    });
  };

  $scope.submitForm = function(form) {
    // First we broadcast an event so all fields validate themselves
    $scope.$broadcast('schemaFormValidate');
    // Then we check if the form is valid
    if (form.$valid) {
      alert('You did it!');
    }
  };
});

</script>
</body>
</html>