Calling HTTP webservice
==================================

(1) For calling a Subscriber Profile related method:

	//service end-point to webservice wsdl url
	String serviceEndPoint = "http://localhost:8080/aaasmx/services/SubscriberProfileWS";	
	
	SubscriberProfileWSServiceLocator ws = new SubscriberProfileWSServiceLocator();

	ws.setSubscriberProfileWSEndpointAddress(serviceEndPoint);

	SubscriberProfileWS manager = ws.getSubscriberProfileWS();
	
	// calling method to search profile by user identity
	Map resultMap =    manager.findByUserIdentity("user");


(2) For calling a Session Manager related method:

	//service end-point to webservice wsdl url
	String serviceEndPoint = "http://localhost:8080/aaasmx/services/SessionManagerWS";	
	
	SessionManagerWSServiceLocator ws = new SessionManagerWSServiceLocator();

	ws.setSessionManagerWSEndpointAddress(serviceEndPoint);

	SessionManagerWS manager = ws.getSessionManagerWS();
		
	// calling method to search sessions by framed ip address
	Map resultMap =    manager.findByFramedIPAddress("127.0.0.1");


Calling HTTPS webservice
==================================

(1) For calling a Subscriber Profile related method:

		System.setProperty("javax.net.ssl.trustStore",".../serverkeys");
		
		//service end-point to webservice wsdl url
		String serviceEndPoint = "https://localhost:8443/aaasmx/services/SubscriberProfileWS";	
		
		SubscriberProfileWSServiceLocator ws = new SubscriberProfileWSServiceLocator();

		ws.setSubscriberProfileWSEndpointAddress(serviceEndPoint);

		SubscriberProfileWS manager = ws.getSubscriberProfileWS();
		
		// calling method to search profile by user identity
		Map resultMap = manager.findByUserIdentity("user");

		