<HTML>
	<HEAD>
		<TITLE>CUSTOMER WEB SELF CARE</TITLE>
		<META http-equiv=Content-Type content="text/html; charset=UTF-8">
		<META content="MSHTML 6.00.6000.16735" name=GENERATOR>
		<script>
			function ServiceOpen(target) 
			{
				if(target == 'ssss')
				{
					window.open('Ssss.jsp');
				}
			}
			function getTarget(target) 
			{
				document.getElementById('change').src = target;
			}
		</script>
	</HEAD>
	<frameset rows="20%,60%,20%" frameborder="no" border="0" >
		<frame src="<%=request.getContextPath()%>/jsp/common/header.jsp" scrolling="no"/>
		<frame id="change" src="Home.action" scrolling="auto" />
		<frame src="<%=request.getContextPath()%>/jsp/common/footer.jsp" scrolling="no"/>
	</frameset>
</HTML>
