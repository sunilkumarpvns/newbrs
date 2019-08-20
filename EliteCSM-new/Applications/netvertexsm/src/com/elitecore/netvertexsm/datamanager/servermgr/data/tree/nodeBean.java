package com.elitecore.netvertexsm.datamanager.servermgr.data.tree;

import java.util.Vector;

public class nodeBean
{

    private String code;
    private String picture;
    private boolean showAsFolder;
    private String statusText;
    private boolean codeLink;
    private String pictureLink;
    private String alt;
    private Vector childs;
    private String id;
    private boolean opened;
    private nodeBean parent;
    private nodeBean nextBro;
    private String className;
    private String style;
    

    public nodeBean()
    {
        childs = new Vector();
        opened = true;
        showAsFolder = false;
        parent = null;
        nextBro = null;
        codeLink = false;
        statusText = null;
    }

    public void setId(String s)
    {
        id = s;
    }

    public String getId()
    {
        return id;
    }

    public void setClassName(String s)
    {
        className = s;
    }

    public String getClassName()
    {
        return className;
    }

    public void setStyle(String s)
    {
        style = s;
    }

    public String getStyle()
    {
        return style;
    }

    public void setParent(nodeBean nodebean)
    {
        parent = nodebean;
    }

    public nodeBean getParent()
    {
        return parent;
    }

    public void setCode(String s)
    {
        code = s;
    }

    public String getCode()
    {
        if(code == null)
        {
            return "";
        } else
        {
            return code;
        }
    }

    public void setPicture(String s)
    {
        picture = s;
    }

    public String getPicture()
    {
        return picture;
    }

    public void setPictureLink(String s)
    {
        pictureLink = s;
    }

    public String getPictureLink()
    {
        return pictureLink;
    }

    public Vector getChilds()
    {
        return childs;
    }

    public void addChild(nodeBean nodebean)
    {
        nodebean.setParent(this);
        int i = childs.size();
        if(i > 0)
        {
            nodeBean nodebean1 = (nodeBean)childs.elementAt(i - 1);
            nodebean1.nextBro = nodebean;
        }
        childs.addElement(nodebean);
    }

    public void setOpened(boolean flag)
    {
        opened = flag;
    }

    public boolean getOpened()
    {
        return opened;
    }

    public void setShowAsFolder(boolean flag)
    {
        showAsFolder = flag;
    }

    public boolean getShowAsFolder()
    {
        return showAsFolder;
    }

    public void setAlt(String s)
    {
        alt = s;
    }

    public String getAlt()
    {
        return alt;
    }

    public nodeBean getNextBrother()
    {
        return nextBro;
    }

    public boolean isLastNode()
    {
        nodeBean nodebean = getParent();
        if(nodebean == null)
        {
            return true;
        }
        nodeBean nodebean1 = nodebean.getNextBrother();
        if(nodebean1 == null)
        {
            return nodebean.isLastNode();
        } else
        {
            return false;
        }
    }

    public void setStatusText(String s)
    {
        statusText = s;
    }

    public String getStatusText()
    {
        return statusText;
    }

    public void setCodeLink(boolean flag)
    {
        codeLink = flag;
    }

    public boolean getCodeLink()
    {
        return codeLink;
    }

    public String getPictureHtml()
    {
        StringBuffer stringbuffer = new StringBuffer("");
        if(getPictureLink() == null)
        {
            stringbuffer.append("<img src=\"");
            stringbuffer.append(getPicture());
            stringbuffer.append("\"");
            if(alt != null)
            {
                stringbuffer.append(" alt=\"");
                stringbuffer.append(alt);
                stringbuffer.append("\"");
            }
            stringbuffer.append(">&nbsp;");
        } else
        {
            stringbuffer.append("<a href=\"");
            stringbuffer.append(getPictureLink());
            stringbuffer.append("\"");
            if(statusText != null)
            {
                stringbuffer.append(" onmouseover=\"window.status='");
                stringbuffer.append(statusText);
                stringbuffer.append("'; return true;\" onmouseout=\"window.status=''; return true;\"");
            }
            stringbuffer.append("><img src=\"");
            stringbuffer.append(getPicture());
            stringbuffer.append("\"");
            stringbuffer.append(" alt=\"");
            if(alt != null)
            {
                stringbuffer.append(alt);
            }
            stringbuffer.append("\"");
            stringbuffer.append(" border=\"0\"></a>&nbsp;");
        }
        return stringbuffer.toString();
    }

    public String getPictureHtml(String s)
    {
        StringBuffer stringbuffer = new StringBuffer("");
        if(getPictureLink() == null)
        {
            stringbuffer.append("<img src=\"");
            stringbuffer.append(s);
            stringbuffer.append("\"");
            stringbuffer.append(" alt=\"");
            if(alt != null)
            {
                stringbuffer.append(alt);
            }
            stringbuffer.append("\"");
            stringbuffer.append(">&nbsp;");
        } else
        {
            stringbuffer.append("<a href=\"");
            stringbuffer.append(getPictureLink());
            stringbuffer.append("\"");
            if(statusText != null)
            {
                stringbuffer.append(" onmouseover=\"window.status='");
                stringbuffer.append(statusText);
                stringbuffer.append("'; return true;\" onmouseout=\"window.status=''; return true;\"");
            }
            stringbuffer.append("><img src=\"");
            stringbuffer.append(s);
            stringbuffer.append("\"");
            stringbuffer.append(" alt=\"");
            if(alt != null)
            {
                stringbuffer.append(alt);
            }
            stringbuffer.append("\"");
            stringbuffer.append(" border=\"0\"></a>&nbsp;");
        }
        return stringbuffer.toString();
    }
}
