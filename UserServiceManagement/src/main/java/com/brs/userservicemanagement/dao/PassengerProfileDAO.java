package com.brs.userservicemanagement.dao;

import com.brs.userservicemanagement.entity.PassengerProfile;

public interface PassengerProfileDAO {
	public Long registerPassengerProfile(PassengerProfile passengerProfile);
	public Object[] getPassengerProfile(Long userId);
}
