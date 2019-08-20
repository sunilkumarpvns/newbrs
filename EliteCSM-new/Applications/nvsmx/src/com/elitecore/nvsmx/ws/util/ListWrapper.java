package com.elitecore.nvsmx.ws.util;


import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "list")
public class ListWrapper<T> {

    private List<T> list;

    public ListWrapper() {
        this.list = new ArrayList<T>();

    }

    @XmlAnyElement(lax = true)
    public List<T> getList() {
        return list;
    }
}
