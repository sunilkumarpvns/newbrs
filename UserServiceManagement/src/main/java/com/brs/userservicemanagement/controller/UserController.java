package com.brs.userservicemanagement.controller;

import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brs.beans.common.ChangePasswordRQ;
import com.brs.beans.common.LoginRQ;
import com.brs.beans.common.PassengerProfileRQ;
import com.brs.beans.common.Response;
import com.brs.beans.common.ViewProfileRQ;
import com.brs.userservicemanagement.service.UserService;


/**
 * created by sathish on 26/6/2018
 */
@RestController
@RequestMapping(value = "users")
public class UserController {
	
	/** The user service. */
	@Autowired
	private UserService userService;

	/**
	 * Login.
	 *
	 * @param loginRQ the login RQ
	 * @return the response
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response login(@RequestBody LoginRQ loginRQ) {
		Response response = userService.login(loginRQ);
		return response;
	}
	@RequestMapping(value = "registerPassenger", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response registerPassenger(@RequestBody PassengerProfileRQ passengerProfileRQ){
		return userService.registerPassenger(passengerProfileRQ);
	}
	@RequestMapping(value = "forgotPassword", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response forgotPossword(@QueryParam("email")String email){
		Response response= userService.forgotPassword(email);
		return response;
	}
	
	@RequestMapping(value = "reset", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response verfyURL(@QueryParam("token")String token){
		Response response= userService.verifyURL(token);
		return response;
	}
	
	@RequestMapping(value = "changePassword", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response changePassword(@RequestBody ChangePasswordRQ changePasswordRQ){
		Response response = userService.changePassword(changePasswordRQ);
		return response;
	}
	
	@RequestMapping(value = "validateOTP/{userId}/{otp}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response validateOTP(
			@PathVariable("userId") Long userId,
			@PathVariable("otp") String otp){
		Response response=userService.validateOtp(userId,otp);
	return response;
	}
	
	
	@RequestMapping(value = "resendOTP/{userId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response resendOTP() {
		return null;
		
	}
	
	@RequestMapping(value="viewProfile",method=RequestMethod.POST,consumes="application/json",produces="application/json")
	@ResponseBody
	public Response getProfileDetails(@RequestBody ViewProfileRQ viewProfileRQ) {
		Response response=null;	
		response=userService.viewProfile(viewProfileRQ);
		return response;	
	}//method
	
}//class




