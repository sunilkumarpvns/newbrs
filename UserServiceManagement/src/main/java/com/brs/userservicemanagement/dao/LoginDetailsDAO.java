package com.brs.userservicemanagement.dao;
import com.brs.userservicemanagement.enums.*;
import com.brs.userservicemanagement.entity.LoginDetails;

public interface LoginDetailsDAO {
public LoginDetails login(LoginDetails loginDetails);
public Integer changePassword(String oldPassword, String newPassword,Long userId);
public int updateUserStatus(Long userId, StatusEnum statusEnum);
}
