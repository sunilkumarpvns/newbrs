package com.elitecore.netvertexsm.datamanager.servermgr.data.tree;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.*;

// Referenced classes of package com.cj.tree:
//            addNode

public class setCodeTag extends BodyTagSupport
{

    private Tag parent;
    private String sBody;

    public setCodeTag()
    {
        sBody = null;
    }

    public int doAfterBody()
        throws JspException
    {
        BodyContent bodycontent;
        if((bodycontent = getBodyContent()) != null)
        {
            sBody = bodycontent.getString();
        }
        return 0;
    }

    public int doEndTag()
        throws JspException
    {
        addNode addnode = (addNode)findAncestorWithClass(this,com.elitecore.netvertexsm.datamanager.servermgr.data.tree.addNode.class);
        if(addnode == null)
        {
            throw new JspException("Could not find ancestor addNode");
        }
        if(sBody == null)
        {
            sBody = "";
        } else
        {
            sBody = sBody.trim();
        }
        addnode.setCode(sBody);
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
        sBody = null;
    }
}
