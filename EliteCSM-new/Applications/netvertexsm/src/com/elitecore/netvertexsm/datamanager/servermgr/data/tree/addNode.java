package com.elitecore.netvertexsm.datamanager.servermgr.data.tree;

import java.util.Vector;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

// Referenced classes of package com.cj.tree:
//            createTree, nodeBean, TreeInterface

public class addNode extends BodyTagSupport
    implements TreeInterface
{

    private Vector childs;
    private String code;
    private String picture;
    private String pictureLink;
    private String alt;
    private String id;
    private Tag parent;
    private boolean open;
    private String className;
    private String style;
    private boolean showAsFolder;
    private boolean codeLink;
    private String statusText;
    createTree papa;
    addNode papa1;

    public addNode()
    {
        childs = null;
        id = null;
        open = true;
        showAsFolder = false;
        codeLink = false;
        statusText = null;
        papa = null;
    }

    public void setCode(String s)
    {
        code = s;
    }

    public String getCode()
    {
        return code;
    }

    public void addChild(nodeBean nodebean)
    {
        childs.addElement(nodebean);
    }

    public void setPicture(String s)
    {
        picture = s;
    }

    public void setShowAsFolder(boolean flag)
    {
        showAsFolder = flag;
    }

    public boolean getShowAsFolder()
    {
        return showAsFolder;
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

    public void setOpen(boolean flag)
    {
        open = flag;
    }

    public boolean getOpen()
    {
        return open;
    }

    public void setAlt(String s)
    {
        alt = s;
    }

    public String getAlt()
    {
        return alt;
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

    public int doStartTag()
        throws JspException
    {
        papa = (createTree)findAncestorWithClass(this,com.elitecore.netvertexsm.datamanager.servermgr.data.tree.createTree.class);
        papa1 = (addNode)findAncestorWithClass(this,com.elitecore.netvertexsm.datamanager.servermgr.data.tree.addNode.class);
        if(papa == null)
        {
            throw new JspException("Could not find ancestor createTree");
        } else
        {
            childs = new Vector();
            return 2;
        }
    }

    public int doEndTag()
        throws JspException
    {
        nodeBean nodebean = new nodeBean();
        nodebean.setCode(code);
        nodebean.setOpened(open);
        if(id != null)
        {
            nodebean.setId(id);
        }
        if(picture != null)
        {
            nodebean.setPicture(picture);
        }
        if(pictureLink != null)
        {
            nodebean.setPictureLink(pictureLink);
        }
        if(alt != null)
        {
            nodebean.setAlt(alt);
        }
        if(style != null)
        {
            nodebean.setStyle(style);
        }
        if(className != null)
        {
            nodebean.setClassName(className);
        }
        if(showAsFolder)
        {
            nodebean.setPicture(papa.getTreePicture());
        }
        for(int i = 0; i < childs.size(); i++)
        {
            nodebean.addChild((nodeBean)childs.elementAt(i));
        }

        if(papa1 != null)
        {
            papa1.addChild(nodebean);
        } else
        {
            papa.addChild(nodebean);
        }
        dropData();
        return 6;
    }

    public void setParent(Tag tag)
    {
        parent = tag;
    }

    public Tag getParent()
    {
        return parent;
    }

    public void release()
    {
        dropData();
    }

    private void dropData()
    {
        code = null;
        childs = null;
        picture = null;
        pictureLink = null;
        alt = null;
        id = null;
        open = true;
        papa = null;
        papa1 = null;
        showAsFolder = false;
        style = null;
        className = null;
        codeLink = false;
        statusText = null;
    }
}
