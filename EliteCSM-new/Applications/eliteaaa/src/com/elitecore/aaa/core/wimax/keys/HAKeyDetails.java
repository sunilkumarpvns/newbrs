package com.elitecore.aaa.core.wimax.keys;

import java.util.concurrent.TimeUnit;

public class HAKeyDetails {
	
	private byte[] ha_rk_key;
	private int ha_rk_spi;
	private int ha_rk_lifetime_in_seconds;
	private long ha_rk_genereation_time_in_millis;
	
	public byte[] getHa_rk_key() {
		return ha_rk_key;
	}
	public void setHa_rk_key(byte[] ha_rk_key) {
		this.ha_rk_key = ha_rk_key;
	}
	public int getHa_rk_spi() {
		return ha_rk_spi;
	}
	public void setHa_rk_spi(int ha_rk_spi) {
		this.ha_rk_spi = ha_rk_spi;
	}
	public int getHa_rk_lifetime_in_seconds() {
		return ha_rk_lifetime_in_seconds;
	}
	public void setHa_rk_lifetime_in_seconds(int ha_rk_lifetime_in_seconds) {
		this.ha_rk_lifetime_in_seconds = ha_rk_lifetime_in_seconds;
	}
	public long getHa_rk_genereation_time_in_millis() {
		return ha_rk_genereation_time_in_millis;
	}
	public void setHa_rk_genereation_time_in_millis(long ha_rk_genereation_time_in_millis) {
		this.ha_rk_genereation_time_in_millis = ha_rk_genereation_time_in_millis;
	}

	public boolean isKeyExpired(long currentTimeInMillis){
		if(ha_rk_genereation_time_in_millis != 0){			
			if (getElapsedTimeInSeconds(currentTimeInMillis) > ha_rk_lifetime_in_seconds) {
				return true;				
			}			
		}	
		return false;
	}
	
	public long getElapsedTimeInSeconds(long currentTimeInMillis) {
		return TimeUnit.MILLISECONDS.toSeconds(currentTimeInMillis - ha_rk_genereation_time_in_millis);
	}
	
	public long getRemainingLifetimeInSeconds(long currentTimeInMillis) {
		return ha_rk_lifetime_in_seconds - getElapsedTimeInSeconds(currentTimeInMillis);
	}
}
