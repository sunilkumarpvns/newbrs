package com.elitecore.corenetvertex.sm.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name="com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData")
@Table(name="TBLM_STAFF_PROFILE_PICTURE")
public class StaffProfilePictureData implements Serializable{

    private String id;
    private byte[] profilePicture;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name="PROFILE_PICTURE")
    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

}
