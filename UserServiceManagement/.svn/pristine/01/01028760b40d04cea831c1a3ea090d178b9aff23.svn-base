package com.brs.userservicemanagement.service;

import com.brs.beans.common.ChangePasswordRQ;
import com.brs.beans.common.LoginRQ;
import com.brs.beans.common.PassengerProfileRQ;
import com.brs.beans.common.Response;
import com.brs.beans.common.ViewProfileRQ;


/**
 * The Interface UserService.
 */
public interface UserService {
	
/**
 * Login.
 *
 * @param loginRQ the login RQ
 * @return the response
 */
public Response login(LoginRQ loginRQ);

public Response registerPassenger(PassengerProfileRQ passengerProfileRQ);
public Response forgotPassword(String email);
public Response verifyURL(String token);
public Response changePassword(ChangePasswordRQ changePasswordRQ);
public Response validateOtp(Long userId, String otp);
public Response viewProfile(ViewProfileRQ viewProfileRQ);

}
