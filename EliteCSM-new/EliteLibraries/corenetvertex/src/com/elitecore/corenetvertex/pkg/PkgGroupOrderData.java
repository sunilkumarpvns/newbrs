package com.elitecore.corenetvertex.pkg;


import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Created by aditya on 2/9/17.
 */
@Entity
@Table(name="TBLM_PKG_GROUP_ORDER")
public class PkgGroupOrderData implements Serializable,Comparable<PkgGroupOrderData>{
    private static final long serialVersionUID = 1L;
    private String id;

    private transient PkgData pkgData;
    private String groupId;



    private String groupName;
    private String type;
    private int orderNumber;


    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @XmlElement(name="id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Column(name="GROUP_ID")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name="ORDER_NO")
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Column(name="type")
    public String getType() {
        return type;
    }

    public void setType(String pkgType) {
        this.type = pkgType;
    }


    @ManyToOne
    @JoinColumn(name="PKG_ID")
    @XmlTransient
    public PkgData getPkgData() {
        return pkgData;
    }

    public void setPkgData(PkgData pkgData) {
        this.pkgData = pkgData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof PkgGroupOrderData)) {
            return false;
        }

        PkgGroupOrderData that = (PkgGroupOrderData) o;

        return getId().equals(that.getId());

    }

    @Transient
    @XmlTransient
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public JsonObject toJson(){
        JsonObject childJsonObject = new JsonObject();
        childJsonObject.addProperty("orderNumber", orderNumber);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(groupName, childJsonObject);
        return jsonObject;
    }

    @Override
    public int compareTo(PkgGroupOrderData other) {
        return Integer.valueOf(orderNumber).compareTo(other.getOrderNumber());
    }

    public PkgGroupOrderData copyModel() {
        PkgGroupOrderData newData = new PkgGroupOrderData();
        newData.groupId = this.groupId;
        newData.groupName = this.groupName;
        newData.orderNumber = this.orderNumber;
        newData.type = this.type;
        return  newData;
    }
}