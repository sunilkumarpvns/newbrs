package com.elitecore.core.commons.utilx.db;

/**
 * DSStatusListener Listener the event whenever status of DS is change
 * i.e. from DSDown to DSUp, from DSUp to DSDown
 * @author harsh
 *
 */
public interface DSStatusListener {
	/**
	 * markAlive listen  the event for DS Up. whenever DS Status changed from DSDown --> DSUp
	 */
	public void dsUp();
	
	/**
	 * markDead listen  the event for DS Down. whenever DS Status changed from DSUp --> DSDown
	 */
	public void dsDown();
}
