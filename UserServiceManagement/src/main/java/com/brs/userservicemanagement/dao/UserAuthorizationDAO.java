package com.brs.userservicemanagement.dao;

import java.util.List;

import com.brs.userservicemanagement.entity.UserAuthorization;


/**
 * The Interface UserAuthorizationDAO.
 */
public interface UserAuthorizationDAO {

	/**
	 * Save user token.
	 *
	 * @param userAuth the user auth
	 * @return the integer
	 */
	public Integer saveUserToken(UserAuthorization userAuth);
	public List<Object[]> getAuthorizedUser(Integer userAuthorizationId,String token);

}
