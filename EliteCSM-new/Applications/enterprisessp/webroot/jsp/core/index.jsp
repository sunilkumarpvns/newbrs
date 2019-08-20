<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<%
	if(!request.getSession().isNew()){
		request.getRequestDispatcher("/jsp/login.jsp").forward(request,response);
	}
	
%>

<tiles:insert page="/jsp/core/baseLayout.jsp" flush="true">
    <tiles:put name="title" value="Tiles Example" />
    <tiles:put name="header" value="/jsp/core/header.jsp" />
    <tiles:put name="menu" value="/jsp/core/menu.jsp" />
    <tiles:put name="rightpanel" value="/jsp/core/rightpanel.jsp" />
    <tiles:put name="body" value="/jsp/core/body.jsp" />
    <tiles:put name="footer" value="/jsp/core/footer.jsp" />
</tiles:insert>