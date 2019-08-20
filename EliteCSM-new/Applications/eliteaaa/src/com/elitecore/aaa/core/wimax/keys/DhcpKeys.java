package com.elitecore.aaa.core.wimax.keys;

import java.util.concurrent.TimeUnit;

public class DhcpKeys {
	
	private byte[] dhcp_rk;
	private int dhcp_rk_id;
	private long dhcp_rk_lifetime_in_seconds;
	private long dhcp_rk_genereation_time_in_millis;

	public DhcpKeys() {
	}
	
	public DhcpKeys(byte[] dhcp_rk, int dhcp_rk_id,
			long dhcp_rk_lifetime_in_seconds,
			long dhcp_rk_genereation_time_in_millis) {
		this.dhcp_rk = dhcp_rk;
		this.dhcp_rk_id = dhcp_rk_id;
		this.dhcp_rk_lifetime_in_seconds = dhcp_rk_lifetime_in_seconds;
		this.dhcp_rk_genereation_time_in_millis = dhcp_rk_genereation_time_in_millis;
	}

	public byte[] getDhcp_rk() {
		return dhcp_rk;
	}
	
	public void setDhcp_rk(byte[] dhcp_rk) {
		this.dhcp_rk = dhcp_rk;
	}
	
	public long getDhcp_rk_lifetime_in_seconds() {
		return dhcp_rk_lifetime_in_seconds;
	}
	
	public void setDhcp_rk_lifetime_in_seconds(long dhcp_rk_lifetime_in_seconds) {
		this.dhcp_rk_lifetime_in_seconds = dhcp_rk_lifetime_in_seconds;
	}
	
	public long getDhcp_rk_genereation_time_in_millis() {
		return dhcp_rk_genereation_time_in_millis;
	}
	
	public void setDhcp_rk_genereation_time_in_millis(long dhcp_rk_genereation_time_in_millis) {
		this.dhcp_rk_genereation_time_in_millis = dhcp_rk_genereation_time_in_millis;
	}
	
	public int getDhcp_rk_id() {
		return dhcp_rk_id;
	}
	
	public void setDhcp_rk_id(int dhcp_rk_id) {
		this.dhcp_rk_id = dhcp_rk_id;
	}
	
	public long getElapsedTimeInSeconds(long currentTimeInMillis) {
		return TimeUnit.MILLISECONDS.toSeconds(currentTimeInMillis - dhcp_rk_genereation_time_in_millis);
	}
	
	public long getRemainingLifetimeInSeconds(long currentTimeInMillis) {
		return dhcp_rk_lifetime_in_seconds - getElapsedTimeInSeconds(currentTimeInMillis);
	}

	public boolean isExpired(long currentTimeInMillis) {
		return dhcp_rk_lifetime_in_seconds < getElapsedTimeInSeconds(currentTimeInMillis);
	}
}
