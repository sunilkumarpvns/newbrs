<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<%
		String pageId = request.getParameter("pageId");
	%>
	<script type="text/javascript">
	<%--START : Server Deployment Architecture --%>
	
	/* $(function () {
	    var chart = new Highcharts.Chart({
	        chart: {
	            renderTo: 'deploymentArchitecture',
	            events: {
	                load: function () {
	                    
	                    // Draw the flow chart
	                    var ren = this.renderer,
	                        colors = Highcharts.getOptions().colors,
	                        rightArrow = ['M', 0, 0, 'L', 100, 0, 'L', 95, 5, 'M', 100, 0, 'L', 95, -5],
	                        leftArrow = ['M', 100, 0, 'L', 0, 0, 'L', 5, 5, 'M', 0, 0, 'L', 5, -5];
	                    
	                    
	                    
	                    // Separator, client from service
	                    ren.path(['M', 120, 40, 'L', 120, 330])
	                        .attr({
	                            'stroke-width': 2,
	                            stroke: 'silver',
	                            dashstyle: 'dash'
	                        })
	                        .add();
	                    
	                    // Separator, CLI from service
	                    ren.path(['M', 420, 40, 'L', 420, 330])
	                        .attr({
	                            'stroke-width': 2,
	                            stroke: 'silver',
	                            dashstyle: 'dash'
	                        })
	                        .add();
	                    
	                    // Headers
	                    ren.label('Datasource', 20, 40)
	                        .css({
	                            fontWeight: 'bold'
	                        })
	                        .add();
	                    ren.label('Server Instance', 220, 40)
	                        .css({
	                            fontWeight: 'bold'
	                        })
	                        .add();
	                    ren.label('External Interface', 440, 40)
	                        .css({
	                            fontWeight: 'bold'
	                        })
	                        .add();

	                    ren.label('192.168.8.70', 15, 67)
	                        .css({
	                            fontSize: '10px',
	                            color: colors[0]
	                        }) 
	                        .add();
	                    
	                    // SaaS client label
	                    ren.label('Oracle-DB1', 10, 82)
	                        .attr({
	                            fill: colors[0],
	                            stroke: 'white',
	                            'stroke-width': 2,
	                            padding: 5,
	                            r: 5
	                        })
	                        .css({
	                            color: 'white'
	                        })
	                        .add()
	                        .shadow(true);
	                    
	                    // Arrow from SaaS client to Phantom JS
	                    ren.path(leftArrow)
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[0]
	                         })
	                        .translate(95, 95)
	                        .add();
	                    ren.path(rightArrow)
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[0]
	                         })
	                        .translate(95, 95)
	                        .add();
	                             
	                    ren.label('TCP Connection', 100, 75)
	                        .css({
	                            fontSize: '10px',
	                            color: colors[0]
	                        }) 
	                        .add();

	                    ren.label('192.168.0.11', 220, 67)
	                        .css({
	                            fontSize: '10px',
	                            color: colors[3]
	                        }) 
	                        .add();
	                    
	                    ren.label('1) AAA1<br/>-------------<br/>2) RM1', 210, 82)
	                        .attr({
	                            r: 5,
	                            width: 100,
	                            fill: colors[1]
	                        })
	                        .css({
	                            color: 'white',
	                            fontWeight: 'bold'
	                        })
	                        .add();

	                    ren.label('192.168.0.12', 220, 185)
	                        .css({
	                            fontSize: '10px',
	                            color: colors[3]
	                        }) 
	                        .add();
	                    
	                    ren.label('1) AAA2<br/>-------------<br/>2) RM2', 210, 200)
	                        .attr({
	                            r: 5,
	                            width: 100,
	                            fill: colors[1]
	                        })
	                        .css({
	                            color: 'white',
	                            fontWeight: 'bold'
	                        })
	                        .add();
	                    
	                    ren.label('192.168.1.160', 15, 205)
	                        .css({
	                            fontSize: '10px',
	                            color: colors[0]
	                        }) 
	                        .add();
	                    
	                    // Browser label
	                    ren.label('Oracle-DB2', 10, 220)
	                        .attr({
	                            fill: colors[0],
	                            stroke: 'white',
	                            'stroke-width': 2,
	                            padding: 5,
	                            r: 5
	                        })
	                        .css({
	                            color: 'white',
	                            width: '100px'
	                        })
	                        .add()
	                        .shadow(true);
	                    
	                    
	                    
	                    // Arrow from Browser to Batik
	                    ren.path(leftArrow)
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[0]
	                         })
	                        .translate(95, 230)
	                         .add();
	                    ren.path(rightArrow)
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[0]
	                         })
	                        .translate(95, 230)
	                         .add();                             
	                    ren.label('TCP Connection', 100, 210)
	                        .css({
	                            color: colors[0],
	                            fontSize: '10px'
	                        })
	                        .add();
	                    

	                    ren.label('10.106.1.85', 455, 67)
	                        .css({
	                            color: colors[0],
	                            fontSize: '10px'
	                        })
	                        .add();                    
	                    // Script label
	                    ren.label('MMS-Gateway', 450, 82)
	                        .attr({
	                            fill: colors[2],
	                            stroke: 'white',
	                            'stroke-width': 2,
	                            padding: 5,
	                            r: 5
	                        })
	                        .css({
	                            color: 'white',
	                            width: '100px'
	                        })
	                        .add()
	                        .shadow(true);
	                    // Arrow from Batik to SaaS client
	                    ren.path(['M', 320, 90, 'L', 440, 90, 'L', 435, 95,'M',440,90,'L',435,85])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[2]
	                         })
	                         .add();

	                    ren.path(['M', 320, 210, 'L', 330, 210, 'C', 342, 210, 340, 185, 340, 190,'L',340,90])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[2]
	                         })
	                         .add();                    
	                    ren.label('RADIUS', 340, 70)
	                        .css({
	                            color: colors[2],
	                            fontSize: '10px'
	                        })
	                        .add();
	                    

	                    ren.label('10.106.1.87', 455, 115)
	                        .css({
	                            color: colors[0],
	                            fontSize: '10px'
	                        })
	                        .add();                                        
	                    // Script label
	                    ren.label('WAP-Gateway', 450, 130)
	                        .attr({
	                            fill: colors[4],
	                            stroke: 'white',
	                            'stroke-width': 2,
	                            padding: 5,
	                            r: 5
	                        })
	                        .css({
	                            color: 'white',
	                            width: '100px'
	                        })
	                        .add()
	                        .shadow(true);
	                    ren.path(['M', 320, 100, 'L', 340, 100,'C',352,100,350,125,350,120,'L',350,130,'C',350,147,375,145,370,145,'L', 440, 145, 'L', 435, 150,'M',440,145,'L',435,140])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[4]
	                         })
	                         .add();
	                    ren.path(['M', 320, 220, 'L', 340, 220, 'C', 352, 220, 350, 195, 350, 200,'L',350,155,'C',350,167,345,135,370,145])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[4]
	                         })
	                         .add();                    
	                    ren.label('RADIUS', 375, 125)
	                        .css({
	                            color: colors[4],
	                            fontSize: '10px'
	                        })
	                        .add();

	                    
	                    
	                    ren.label('10.104.1.26', 455, 165)
	                        .css({
	                            color: colors[0],
	                            fontSize: '10px'
	                        })
	                        .add();                                        
	                    // Script label
	                    ren.label('Online-Charning-System', 450, 180)
	                        .attr({
	                            fill: colors[0],
	                            stroke: 'white',
	                            'stroke-width': 2,
	                            padding: 5,
	                            r: 5
	                        })
	                        .css({
	                            color: 'white',
	                            width: '150px'
	                        })
	                        .add()
	                        .shadow(true);
	                    ren.path(['M', 320, 110, 'L', 350, 110,'C',362,110,360,135,360,130,'L',360,180,'C',360,197,385,195,380,195,'L', 440, 195, 'L', 435, 200,'M',440,195,'L',435,190])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[0]
	                         })
	                         .add();
	                    ren.path(['M', 320, 230, 'L', 350, 230, 'C', 362, 230, 360, 205, 360, 210,'L',360,205,'C',360,217,355,185,380,195])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[0]
	                         })
	                         .add();                    
	                    ren.label('RADIUS', 375, 175)
	                        .css({
	                            color: colors[0],
	                            fontSize: '10px'
	                        })
	                        .add();                                      

	                    
	                    
	                    ren.label('10.104.1.34', 455, 215)
	                        .css({
	                            color: colors[0],
	                            fontSize: '10px'
	                        })                        .add();                                                            
	                    ren.label('Cisco-HSS', 450, 230)
	                        .attr({
	                            fill: colors[7],
	                            stroke: 'white',
	                            'stroke-width': 2,
	                            padding: 5,
	                            r: 5
	                        })
	                        .css({
	                            color: 'white',
	                            width: '150px'
	                        })
	                        .add()
	                        .shadow(true);
	                    ren.path(['M', 320, 120, 'L', 360, 120,'C',372,120,370,145,370,140,'L',370,240])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[7]
	                         })
	                         .add();
	                    ren.path(['M', 320, 240, 'L', 440, 240, 'L', 435, 245,'M',440,240,'L',435,235])
	                         .attr({
	                             'stroke-width': 2,
	                             stroke: colors[7]
	                         })
	                         .add();
	                    ren.label('DIAMETER', 370, 215)
	                        .css({
	                            color: colors[7],
	                            fontSize: '10px'
	                        })
	                        .add();                                      
	                    
	                    
	                    
	                }
	            }
	        },
	        title: {
	            text: null,
	            style: {
        	        fontSize: '12px'
           		}
	        }
	            
	    });
	}); */
	
	<%-- END : Primary Memory Usage Info --%>
$(document).ready(function(){
		
	});
	
	function hideProgressBar(){
		$('#totalReqProgressDivId').hide();
	}
	
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/instancewidgets/EditAAADeploymentArchitecture.jsp" id="editJsp">
<%--ProgressBar Div : Start  --%>
 
 <div id="totalReqProgressDivId" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>
 <!-- <div id="deploymentArchitecture" style="height: 300px; min-width: 545px;margin: 0;padding-bottom: 3px;overflow: auto;"></div> -->
</body>
</html>