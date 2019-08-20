package com.elitecore.corenetvertex.sm.password;

import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Used as model in change password functionality
 * Create by dhyani.raval
 */
public class PasswordData extends DefaultGroupResourceData implements Serializable {

    private String id;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getResourceName() {
        return "Password Data";
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @JsonIgnore
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
