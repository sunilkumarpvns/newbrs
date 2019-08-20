<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>

<script type="text/javascript">
	function search(){
		$("#action").val("search");
		$("#searchsubscriberMain").submit();
	}
	function searchAll(){
		$("#action").val("searchAll");
		$("#searchsubscriberMain").submit();
	}
</script>

<html:form action="/searchSubscriberProfiler" method="post" styleId="searchsubscriberMain">
	<html:hidden property="action" styleId="action" value=""/>
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<!-- <tr class="table-org" height="30px">   		
   			<td class="table-org-column" width="100%" colspan="2">Search Subscribe Profiler</td>    		
		</tr> -->
		
		<tr>
			<td  width="100%" colspan="2">
				<table id="searchSubscribeTbl" width="100%">
					<logic:notEmpty name="subscriberProfilerForm" property="propertyFields">	
						<logic:iterate id="obj" name="subscriberProfilerForm" property="propertyFields">
							<tr>
								<td align="left" width="30%">
									<bean:write name="obj"/>
								</td>
								<td>
									<input class="searchtextboxfields" type="text" style="width: 250px" name='<bean:write name="obj"/>' value='' title='<bean:write name="obj"/>'/>
								</td>
							</tr>
						</logic:iterate>
					</logic:notEmpty>
				</table>
			</td>
		</tr>
		
		<tr>   		
   			<td colspan="2">&nbsp;</td>   		
		</tr>
			
		<tr>
			<!-- <td width="30%">&nbsp;</td> -->
			<td align="center">
				<input type="button" onclick="search();" value="Search" class="orange-btn"/>
				<input type="button" onclick="searchAll();" value="Show All" class="orange-btn"/>
			</td>
		</tr>
	</table>
</html:form>


<script>
	$(document).ready(function(){
		$("#searchSubscribeTbl tr:even").addClass("table-gray");
		$("#searchSubscribeTbl tr:odd").addClass("table-white");
		
		/* $(".searchtextboxfields").focus(function(){
			if($(this).attr("title") == $.trim($(this).val())){
				$(this).val("");
			}
		});
		
		$(".searchtextboxfields").blur(function(){
			if($.trim($(this).val()) == ""){
				$(this).val($(this).attr("title"));
			}
		}); */
		
	});
</script>