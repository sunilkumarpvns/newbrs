package com.elitecore.netvertexsm.datamanager.servermgr.data.tree;

import java.util.Vector;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.BodyTagSupport;

// Referenced classes of package com.cj.tree:
//            nodeBean, TreeInterface, TreeBean

public class createTree extends BodyTagSupport
    implements TreeInterface
{

    private Vector childs;
    private String treePicture;
    private String nodePicture;
    private String openPicture;
    private boolean defaultConnectors;
    private String verticalConnector;
    private String bottomConnector;
    private String middleConnector;
    private String id;
    private String style;
    private String className;
    private String width;
    private String height;
    private TreeBean modelBean;
    private String modelName;
    private boolean showRoot;
    private boolean dhtml;
    private StringBuffer stringbuffer = new StringBuffer("");
    private static int count=1;
    public createTree()
    {
        childs = null;
        treePicture = null;
        nodePicture = null;
        openPicture = null;
        defaultConnectors = true;
        verticalConnector = null;
        bottomConnector = null;
        middleConnector = null;
        id = null;
        style = null;
        className = null;
        width = null;
        height = null;
        modelBean = null;
        modelName = null;
        showRoot = true;
        dhtml = false;
    }

    public void setTreePicture(String s)
    {
        treePicture = s;
    }

    public String getTreePicture()
    {
        return treePicture;
    }

    public void setOpenPicture(String s)
    {
        openPicture = s;
    }

    public String getOpenPicture()
    {
        return openPicture;
    }

    public void setNodePicture(String s)
    {
        nodePicture = s;
    }

    public String getNodePicture()
    {
        return nodePicture;
    }

    public void setDhtml(boolean flag)
    {
        dhtml = flag;
    }

    public boolean getDhtml()
    {
        return dhtml;
    }

    public void setDefaultConnectors(boolean flag)
    {
        defaultConnectors = flag;
    }

    public boolean getDefaultConnectors()
    {
        return defaultConnectors;
    }

    public void setVerticalConnector(String s)
    {
        verticalConnector = s;
    }

    public String getVerticalConnector()
    {
        return verticalConnector;
    }

    public void setBottomConnector(String s)
    {
        bottomConnector = s;
    }

    public String getBottomConnector()
    {
        return bottomConnector;
    }

    public void setMiddleConnector(String s)
    {
        middleConnector = s;
    }

    public String getMiddleConnector()
    {
        return middleConnector;
    }

    public void setId(String s)
    {
        id = s;
    }

    public String getId()
    {
        return id;
    }

    public void setStyle(String s)
    {
        style = s;
    }

    public String getStyle()
    {
        return style;
    }

    public void setClassName(String s)
    {
        className = s;
    }

    public String getClassName()
    {
        return className;
    }

    public void setWidth(String s)
    {
        width = s;
    }

    public String getWidth()
    {
        return width;
    }

    public void setHeight(String s)
    {
        height = s;
    }

    public String getHeight()
    {
        return height;
    }

    public void setShowRoot(boolean flag)
    {
        showRoot = flag;
    }

    public void setShowRoot(String s)
    {
        if("true".equals(s))
        {
            showRoot = true;
        } else
        {
            showRoot = false;
        }
    }

    public boolean getShowRoot()
    {
        return showRoot;
    }

    public void addChild(nodeBean nodebean)
    {     
    		childs = new Vector();
            childs.addElement(nodebean);     
    }

    public void setModelName(String s)
    {
        modelName = s;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelBean(TreeBean treebean)
    {
        modelBean = treebean;
    }

    public TreeBean getModelBean()
    {
        return modelBean;
    }

    public String getOuptputString(){
    	return stringbuffer.toString();
    }
    
    
    public void doInitBody()
        throws JspException
    {
        childs = new Vector();
        if(modelBean != null)
        {
            addChild(modelBean);
        }
        if(modelName != null)
        {
            Object obj = null;
            try
            {
                PageContext _tmp = pageContext;
                nodeBean nodebean;
                if((nodebean = (nodeBean)pageContext.getAttribute(modelName, 2)) == null)
                {
                    PageContext _tmp1 = pageContext;
                    if((nodebean = (nodeBean)pageContext.getAttribute(modelName, 3)) == null)
                    {
                        PageContext _tmp2 = pageContext;
                        if((nodebean = (nodeBean)pageContext.getAttribute(modelName, 1)) == null)
                        {
                            PageContext _tmp3 = pageContext;
                            if((nodebean = (nodeBean)pageContext.getAttribute(modelName, 4)) == null)
                            {
                                throw new JspException((new StringBuilder()).append("Could not get TreeBean ").append(modelName).toString());
                            }
                        }
                    }
                }
                if(nodebean != null)
                {
                    addChild(nodebean);
                }
            }
            catch(Exception exception)
            {
                throw new JspException((new StringBuilder()).append("Could not get TreeBean ").append(modelName).toString());
            }
        }
    }

    
    public static String getGeneratedTree(String appContext,TreeBean paramNodeBean){
    	try{
    		createTree mytree= new createTree();
    		mytree.setId("mytree");
    		mytree.setClassName("tabletext");
    		mytree.setDhtml(true);
    		mytree.setTreePicture(appContext + "/images/tree/plus_arrow.gif");
    		mytree.setOpenPicture(appContext + "/images/tree/minus_arrow.gif");
    		//mytree.setNodePicture(appContext + "/images/tree/leaf_arrow.gif");
    		mytree.setDefaultConnectors(false);
    		mytree.addChild(paramNodeBean);
    		mytree.doEndTag();
    		return mytree.getOuptputString();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public int doEndTag()
        throws JspException
    {
    	 count = 0;
    	 stringbuffer = new StringBuffer("");
        if(!showRoot && childs.size() == 1)
        {
            nodeBean nodebean1 = (nodeBean)childs.elementAt(0);
            Vector vector1 = nodebean1.getChilds();
            if(vector1 != null && vector1.size() > 0)
            {
                childs = vector1;
            }
        }
        if(childs.size() > 0)
        {
            Object obj;
           if(dhtml && pageContext !=null)
            {
                if((obj = (Integer)pageContext.getAttribute("trtgjvscrptcj2004")) == null)
                {
                    PageContext _tmp = pageContext;
                    pageContext.setAttribute("trtgjvscrptcj2004", new Integer(1), 1);
                    stringbuffer.append(createJavaScript());
                } else
                {
                    PageContext _tmp1 = pageContext;
                    pageContext.setAttribute("trtgjvscrptcj2004", new Integer(((Integer) (obj)).intValue() + 1), 1);
                }
            }
         
            stringbuffer.append(createJavaScript());
            stringbuffer.append("<div");
            if(id != null)
            {
                stringbuffer.append(" id=\"");
                stringbuffer.append(id);
                stringbuffer.append("\"");
            }
            if(className != null)
            {
                stringbuffer.append(" class=\"");
                stringbuffer.append(className);
                stringbuffer.append("\"");
            }
            obj = style;
            if(obj == null)
            {
                obj = "";
            } else
            if((width != null || height != null) && !((String) (obj)).endsWith(";"))
            {
                obj = (new StringBuilder()).append(((String) (obj))).append(";").toString();
            }
            if(width != null || height != null)
            {
                obj = (new StringBuilder()).append(((String) (obj))).append("overflow:auto;border-style:solid;border-width:0px;").toString();
                if(width != null)
                {
                    obj = (new StringBuilder()).append(((String) (obj))).append("width:").toString();
                    obj = (new StringBuilder()).append(((String) (obj))).append(width).toString();
                     obj = (new StringBuilder()).append(((String) (obj))).append(";").toString();
                }
                if(height != null)
                {
                    obj = (new StringBuilder()).append(((String) (obj))).append("height:").toString();
                    obj = (new StringBuilder()).append(((String) (obj))).append(height).toString();
                    obj = (new StringBuilder()).append(((String) (obj))).append(";").toString();
                }
            }
            if(((String) (obj)).length() > 0)
            {
                stringbuffer.append(" style=\"");
                stringbuffer.append(((String) (obj)));
                stringbuffer.append("\"");
            }
            stringbuffer.append(">");
            //stringbuffer.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
            stringbuffer.append(
            //		"<tr width=\"100%\">" +
           // 		"<td align=\"center\" class=\"headertd\" >" +
            		"<table border=\"0\" width=\"100%\" cellSpacing=\"0\" cellPadding=\"0\">" +
            		"<tr >" +
            		"<td align=\"center\"  class=\"tblheader-bold\" >Node Name</td>" +
            		"<td align=\"center\" class=\"tblheader-bold\" >IP Address</td>" +
            		"<td align=\"center\"  class=\"tblheader-bold\" >Status</td>" +
            		"<td align=\"center\"  class=\"tblheader-bold\" >Live Session</td>" +
            		"<td align=\"center\"  class=\"tblheader-bold\" >Actions</td>" +
            		"</tr>" +            		
            		"</table>") ;
            	//	"</td>"+            		
            	//	"</tr>" +            		
            	//	"<tr width=\"100%\"><td colspan=1>");
            for(int i = 0; i < childs.size(); i++)
            {
                nodeBean nodebean = (nodeBean)childs.elementAt(i);
                if(nodebean.getShowAsFolder() && treePicture != null)
                {
                    nodebean.setPicture(treePicture);
                }
                Vector vector = nodebean.getChilds();
                if(dhtml)
                {
                    stringbuffer.append(displayDhtmlNode(nodebean, vector, 0, i == childs.size() - 1, vector.size() == 1, 1));
                    continue;
                }
                if(nodebean.getId() != null || nodebean.getStyle() != null || nodebean.getClassName() != null)
                {
                    getNodeStart(nodebean, stringbuffer);
                }
                //stringbuffer.append("<tr><td colspan=5 width=\"50%\" nowrap >");
                //stringbuffer.append("<td  width=\"50%\" nowrap >"); //added new
                stringbuffer.append(displayNode(nodebean, vector));
                //stringbuffer.append("</td></tr>\n");
                //stringbuffer.append("</td>\n"); //added new
                
                if(vector.size() > 0 && nodebean.getOpened())
                {
                    displayLevel(stringbuffer, vector, 1, i == childs.size() - 1, vector.size() == 1, 0);
                }
                if(nodebean.getId() != null || nodebean.getClassName() != null || nodebean.getStyle() != null)
                {
                    stringbuffer.append("</span>");
                }
            }

            stringbuffer.append("</td>" +
            			//"<td rowspan=4 colspan=2> hi how are u please </td>" +
            		"</tr></table></div>\n");
            try
            {
            	if(pageContext !=null)
            		pageContext.getOut().write(stringbuffer.toString());
            }
            catch(Exception exception)
            {
                throw new JspException("Tree tag: could not write data");
            }
        }
        dropData();
        return 6;
    }

    private void displayLevel(StringBuffer stringbuffer, Vector vector, int i, boolean flag, boolean flag1, int j)
    {
    	//stringbuffer.append("<tr><td colspan=1>\n");
        //stringbuffer.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
    	stringbuffer.append("<td colspan=1>\n"); // Added new        
        for(int k = 0; k < vector.size(); k++)
        {
            nodeBean nodebean = (nodeBean)vector.elementAt(k);
            boolean flag2 = flag1;
            if(i == 1 && k == vector.size() - 1)
            {
                flag2 = true;
            }
            int i1 = j;
            if(flag2 && k == vector.size() - 1 && nodebean.isLastNode())
            {
                i1++;
            }
            if(nodebean.getShowAsFolder() && treePicture != null)
            {
                nodebean.setPicture(treePicture);
            }
            Vector vector1 = nodebean.getChilds();
            if(nodebean.getId() != null || nodebean.getClassName() != null || nodebean.getStyle() != null)
            {
                getNodeStart(nodebean, stringbuffer);
            }
            //stringbuffer.append("<tr><td>\n");
            //stringbuffer.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
            //stringbuffer.append("<tr>");
            for(int l = 1; l < i; l++)
            {
                if(l <= j)
                {
                   // stringbuffer.append("<td nowrap>");
                    if(defaultConnectors)
                    {
                        stringbuffer.append(" ");
                    } else
                    if(verticalConnector != null)
                    {
                    	stringbuffer.append("&nbsp;&nbsp;");
                        stringbuffer.append((new StringBuilder()).append("<img style=\"visibility:hidden\" src=\"").append(verticalConnector).append("\" alt=\"\"/>").toString());
                    }
                    stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    continue;
                }
                //stringbuffer.append("<td nowrap>");
                if(defaultConnectors)
                {
                    stringbuffer.append("|");
                } else
                if(verticalConnector != null)
                {
                	stringbuffer.append("&nbsp;&nbsp;");
                    stringbuffer.append((new StringBuilder()).append("<img src=\"").append(verticalConnector).append("\" alt=\"\"/>").toString());
                }
                stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            }

            //stringbuffer.append("<td nowrap>");
            if(defaultConnectors)
            {
                if(k == vector.size() - 1)
                {
                    stringbuffer.append("\\-");
                } else
                {
                    stringbuffer.append("|-");
                }
            } else
            if(flag && bottomConnector != null && k == vector.size() - 1)
            {
                stringbuffer.append((new StringBuilder()).append("<img src=\"").append(bottomConnector).append("\" alt=\"\"/>&nbsp;\n").toString());
            } else
            if(middleConnector != null)
            {
            	stringbuffer.append("&nbsp;&nbsp;");
                stringbuffer.append((new StringBuilder()).append("<img src=\"").append(middleConnector).append("\" alt=\"\"/>&nbsp;\n").toString());
            } else
            {
                stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            //stringbuffer.append("</td>");
            //stringbuffer.append("<td width=\"50%\" colspan=5 nowrap>");
            stringbuffer.append(displayNode(nodebean, vector1));
            //stringbuffer.append("</td>");
            //stringbuffer.append("</tr>");
            //stringbuffer.append("</table>\n");
            //stringbuffer.append("</td></tr>\n");
            
            if(vector1.size() > 0 && nodebean.getOpened())
            {
                displayLevel(stringbuffer, vector1, i + 1, k == vector.size() - 1, flag2, i1);
            }
            if(nodebean.getId() != null || nodebean.getClassName() != null || nodebean.getStyle() != null)
            {
                stringbuffer.append("</span>\n");
            }
        }

        //stringbuffer.append("</table>\n");
        //stringbuffer.append("</td></tr>\n");
    }

    private String displayDhtmlNode(nodeBean nodebean, Vector vector, int i, boolean flag, boolean flag1, int j)
    {
        StringBuffer stringbuffer = new StringBuffer("");
        String s = getNewId();
        if(nodebean.getId() != null || nodebean.getClassName() != null || nodebean.getStyle() != null)
        {
            getNodeStart(nodebean, stringbuffer);
        }
        stringbuffer.append("\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"table-layout:fixed;\">\n");
        
        stringbuffer.append("<tr class=\"row\">");
        stringbuffer.append("<td  class=\"tabletext\" valign=\"top\" width=\"45%\" style=\"word-wrap: break-word;\">\n");
        if(i > 0)
        {
        //    stringbuffer.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n");
        //    stringbuffer.append("<tr>");
            for(int k = 0; k < i; k++)
            {
                if(k < j)
                {
          //          stringbuffer.append("<td nowrap>");
                    if(defaultConnectors)
                    {
                        stringbuffer.append(" ");
                    } else
                    if(verticalConnector != null)
                    {
                    	stringbuffer.append("&nbsp;&nbsp;");
                        stringbuffer.append((new StringBuilder()).append("<img style=\"visibility:hidden\" src=\"").append(verticalConnector).append("\" alt=\"\"/>").toString());
                    }
                    stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;"); // new added
                    //stringbuffer.append("&nbsp;&nbsp;</td>");
                    continue;
                }
                //stringbuffer.append("<td nowrap>");
                if(defaultConnectors)
                {
                    stringbuffer.append("|");
                } else
                if(verticalConnector != null)
                {
                	stringbuffer.append("&nbsp;&nbsp;");
                    stringbuffer.append((new StringBuilder()).append("<img src=\"").append(verticalConnector).append("\" alt=\"\"/>").toString());
                }
                stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;"); // new added
                //stringbuffer.append("&nbsp;&nbsp;</td>");
            }

            //stringbuffer.append("<td nowrap>");
            if(defaultConnectors)
            {
                if(flag)
                {
                    stringbuffer.append("\\-");
                } else
                {
                    stringbuffer.append("|-");
                }
            } else
            if(flag && bottomConnector != null)
            {
                stringbuffer.append((new StringBuilder()).append("<img src=\"").append(bottomConnector).append("\" alt=\"\"/>&nbsp;").toString());
            } else
            if(middleConnector != null)
            {
            	stringbuffer.append("&nbsp;&nbsp;");
                stringbuffer.append((new StringBuilder()).append("<img src=\"").append(middleConnector).append("\" alt=\"\"/>&nbsp;").toString());
            } else
            {
                stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            
            //stringbuffer.append("<td nowrap valign=\"top\">");
        }
        if(vector.size() > 0)
        {
            stringbuffer.append("<a   style=\"text-decoration:");
            if(nodebean.getOpened() || i == 0)
            {
                stringbuffer.append("none");
            } else
            {
                stringbuffer.append("none");
            }
            stringbuffer.append("\"");
            if(nodebean.getStatusText() != null)
            {
                stringbuffer.append(" onmouseover=\"window.status='");
                stringbuffer.append(nodebean.getStatusText());
                stringbuffer.append("'; return true;\" onmouseout=\"window.status=''; return true;\"");
            }
            stringbuffer.append(" href=\"javascript:");
            if(nodebean.getPicture() != null)
            {
                //comment because of difference between location leaf and node leaf. 
            	/*stringbuffer.append("swapSub('si");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
                stringbuffer.append((new StringBuilder()).append("<img id=\"i").append(s).append("\" src=\"").append(nodebean.getPicture()).append("\" border=\"0\" alt=\"\"/>").toString());
                */
            	stringbuffer.append("swapFolder('i");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
                stringbuffer.append((new StringBuilder()).append("<img id=\"i").append(s).append("\" src=\"").toString());
                if(nodebean.getOpened())
                {
                	stringbuffer.append(openPicture);
                } else
                {
                    stringbuffer.append(treePicture);
                    //here change
                }
                stringbuffer.append("\"  border=\"0\" alt=\"\"/>");
            } else
            if(treePicture == null)
            {
                stringbuffer.append("swapText('i");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
                stringbuffer.append((new StringBuilder()).append("<span id=\"i").append(s).append("\">").toString());
                if(nodebean.getOpened())
                {
                    stringbuffer.append("-");
                } else
                {
                    stringbuffer.append("+");
                }
                stringbuffer.append("</span>");
            } else
            if(openPicture == null)
            {
                stringbuffer.append("swapSub('si");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
                stringbuffer.append((new StringBuilder()).append("<img id=\"i").append(s).append("\" src=\"").append(treePicture).append("\" border=\"0\" alt=\"\"/>").toString());
            } else
            {
                stringbuffer.append("swapFolder('i");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
                stringbuffer.append((new StringBuilder()).append("<img id=\"i").append(s).append("\" src=\"").toString());
                if(nodebean.getOpened())
                {
                    stringbuffer.append(openPicture);
                } else
                {
                    stringbuffer.append(treePicture);
                }
                stringbuffer.append("\" border=\"0\" alt=\"\"/>");
            }
            stringbuffer.append("</a>&nbsp;");
        } else
        {
            if(nodebean.getShowAsFolder())
            {
                nodebean.setPicture(treePicture);
            }
            if(nodebean.getPicture() != null)
            {
                stringbuffer.append(nodebean.getPictureHtml());
            } else
            if(nodePicture != null)
            {
                stringbuffer.append(nodebean.getPictureHtml(nodePicture));
            } else
            {
                stringbuffer.append("&nbsp;");
            }
        }
        //stringbuffer.append("</td>");
        if(i > 0)
        {
           // stringbuffer.append("</td></tr></table>");
        }
        //stringbuffer.append("</td>");
        //stringbuffer.append("<td align=\"left\" valign=\"top\" colspan=5 width=\"100%\" nowrap");
/*        if(nodebean.getClassName() != null)
        {
            stringbuffer.append(" class=\"");
            stringbuffer.append(nodebean.getClassName());
            stringbuffer.append("\"");
        }
        if(nodebean.getStyle() != null)
        {
            stringbuffer.append(" style=\"");
            stringbuffer.append(nodebean.getStyle());
            stringbuffer.append("\"");
        }
        stringbuffer.append(">"); */
        if(nodebean.getCodeLink() && vector.size() > 0)
        {
            stringbuffer.append("<a style=\"text-decoration:");
            if(nodebean.getOpened() || i == 0)
            {
                stringbuffer.append("none");
            } else
            {
                stringbuffer.append("none");
            }
            stringbuffer.append("\"");
            if(nodebean.getStatusText() != null)
            {
                stringbuffer.append(" onmouseover=\"window.status='");
                stringbuffer.append(nodebean.getStatusText());
                stringbuffer.append("'; return true;\" onmouseout=\"window.status=''; return true;\"");
            }
            if(nodebean.getClassName() != null)
            {
                stringbuffer.append(" class=\"");
                stringbuffer.append(nodebean.getClassName());
                stringbuffer.append("\"");
            }
            if(nodebean.getStyle() != null)
            {
                stringbuffer.append(" style=\"");
                stringbuffer.append(nodebean.getStyle());
                stringbuffer.append("\"");
            }
            stringbuffer.append(" href=\"javascript:");
            if(treePicture == null)
            {
                stringbuffer.append("swapText('i");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
            } else
            if(openPicture == null)
            {
                stringbuffer.append("swapSub('si");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
            } else
            {
                stringbuffer.append("swapFolder('i");
                stringbuffer.append(s);
                stringbuffer.append("');\">");
            }
        }
        stringbuffer.append(nodebean.getCode());
        if(nodebean.getCodeLink() && vector.size() > 0)
        {
            stringbuffer.append("</a>");
        }
        //stringbuffer.append("</td></tr></table>");
        if(vector.size() > 0)
        {
            stringbuffer.append((new StringBuilder()).append("<span id=\"si").append(s).append("\"").toString());
            stringbuffer.append(" style=\"display:");
            if(nodebean.getOpened())
            {
                stringbuffer.append("block");
            } else
            {
                stringbuffer.append("none");
            }
            stringbuffer.append("\">\n");
            for(int l = 0; l < vector.size(); l++)
            {
                boolean flag2 = flag1;
                if(i == 1 && nodebean.getNextBrother() == null)
                {
                    flag2 = true;
                }
                int i1 = j;
                if(flag2 && ((nodeBean)vector.elementAt(l)).isLastNode())
                {
                    i1++;
                }
                stringbuffer.append(displayDhtmlNode((nodeBean)vector.elementAt(l), ((nodeBean)vector.elementAt(l)).getChilds(), i + 1, l == vector.size() - 1, flag2, i1));
            }

            stringbuffer.append("</span>\n");
        }
        if(nodebean.getId() != null || nodebean.getClassName() != null || nodebean.getStyle() != null)
        {
            stringbuffer.append("</span>");
        }
        return stringbuffer.toString();
    }

    private String displayNode(nodeBean nodebean, Vector vector)
    {
        StringBuffer stringbuffer = new StringBuffer("");
        if(nodebean.getPicture() != null)
        {
        	//if default location closed
        	stringbuffer.append(nodebean.getPictureHtml());
        } else
        {
            if(treePicture != null && vector.size() > 0)
            {
                stringbuffer.append(nodebean.getPictureHtml(treePicture));
            }
            if(nodePicture != null && vector.size() <= 0)
            {
                stringbuffer.append(nodebean.getPictureHtml(nodePicture));
            }
        }
        stringbuffer.append(nodebean.getCode());
        stringbuffer.append("</td></tr></table>");
        return stringbuffer.toString();
    }

    private void getNodeStart(nodeBean nodebean, StringBuffer stringbuffer)
    {
        stringbuffer.append("<span");
        if(nodebean.getId() != null)
        {
            stringbuffer.append(" id=\"");
            stringbuffer.append(nodebean.getId());
            stringbuffer.append("\"");
        }
        if(nodebean.getClassName() != null)
        {
            stringbuffer.append(" class=\"");
            stringbuffer.append(nodebean.getClassName());
            stringbuffer.append("\"");
        }
        if(nodebean.getStyle() != null)
        {
            stringbuffer.append(" style=\"");
            stringbuffer.append(nodebean.getStyle());
            stringbuffer.append("\"");
        }
        stringbuffer.append(">");
    }

    private String createJavaScript()
    {
        StringBuffer stringbuffer = new StringBuffer("\n<script type=\"text/javascript\" language=\"JavaScript\">\n");
        stringbuffer.append("function getElem(id){\n");
        stringbuffer.append("if (document.layers) return document.layers[id];\n");
        stringbuffer.append("else if (document.all) return document.all[id];\n");
        stringbuffer.append("else if (document.getElementById) return document.getElementById(id);\n");
        stringbuffer.append("}\n");
        if(treePicture != null && openPicture != null)
        {
            stringbuffer.append("function swapFolder(img){\n");
            stringbuffer.append("objImg=getElem(img);\n");
            stringbuffer.append("if(objImg.src.indexOf('");
            stringbuffer.append(treePicture);
            stringbuffer.append("')>-1)\n");
            stringbuffer.append("objImg.src='");
            stringbuffer.append(openPicture);
            stringbuffer.append("';\n");
            stringbuffer.append("else\n");
            stringbuffer.append("objImg.src='");
            stringbuffer.append(treePicture);
            stringbuffer.append("';\n");
            stringbuffer.append("swapSub('s'+img);\n");
            stringbuffer.append("}\n");
        }
        stringbuffer.append("function swapText(img){\n");
        stringbuffer.append("objImg=getElem(img);\n");
        stringbuffer.append("if(objImg.innerText.indexOf('+')>-1)\n");
        stringbuffer.append("objImg.innerText='-';\n");
        stringbuffer.append("else\n");
        stringbuffer.append("objImg.innerText='+';\n");
        stringbuffer.append("swapSub('s'+img);\n");
        stringbuffer.append("}\n");
        stringbuffer.append("function swapSub(img){\n");
        stringbuffer.append("elem=getElem(img);\n");
        stringbuffer.append("objImg=elem.style;\n");
        stringbuffer.append("if(objImg.display=='block')\n");
        stringbuffer.append("objImg.display='none';\n");
        stringbuffer.append("else\n");
        stringbuffer.append("objImg.display='block';\n");
        stringbuffer.append("}\n");
        stringbuffer.append("</script>\n");
        return stringbuffer.toString();
    }

    private String getNewId()
    {
/*        Integer integer = (Integer)pageContext.getAttribute("trtgjvscrptcj2004");
        PageContext _tmp = pageContext;
        pageContext.setAttribute("trtgjvscrptcj2004", new Integer(integer.intValue() + 1), 1);
        */
        return (new StringBuilder()).append("trtcntr").append(count++).toString();
    }

/*    private String getNewId()
    {
        Integer integer = (Integer)pageContext.getAttribute("trtgjvscrptcj2004");
        PageContext _tmp = pageContext;
        pageContext.setAttribute("trtgjvscrptcj2004", new Integer(integer.intValue() + 1), 1);
        return (new StringBuilder()).append("trtcntr").append(integer.intValue()).toString();
    }
*/    
    public void release()
    {
        dropData();
        super.release();
    }

    private void dropData()
    {
        childs = null;
        treePicture = null;
        nodePicture = null;
        openPicture = null;
        defaultConnectors = true;
        verticalConnector = null;
        bottomConnector = null;
        middleConnector = null;
        dhtml = false;
        id = null;
        style = null;
        className = null;
        width = null;
        height = null;
        modelBean = null;
        modelName = null;
        showRoot = true;
    }
}
