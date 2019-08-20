package com.elitecore.core.serverx.snmp.mib.os.extended;

import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.serverx.snmp.mib.os.autogen.EnumMemSwapError;
import com.elitecore.core.serverx.snmp.mib.os.autogen.Memory;
import com.elitecore.core.serverx.snmp.mib.os.data.SystemDetailProvider;

public class MemoryMBeanImpl extends Memory {

	private static final long serialVersionUID = 1L;
	transient private final SystemDetailProvider systemDetailProvider;
	
	public MemoryMBeanImpl(SystemDetailProvider systemDetailProvider) {
		this.systemDetailProvider = systemDetailProvider;
	}

	@Override
	@Column(name="memSwapErrorMsg", type = Types.VARCHAR)
	public String getMemSwapErrorMsg() {
		return systemDetailProvider.getMemoryDetails().getMemSwapErrorMsg();
	}

	@Override
	@Column(name="memSwapError", type = Types.VARCHAR)
	public EnumMemSwapError getMemSwapError() {
		return systemDetailProvider.getMemoryDetails().getMemSwapError();
	}

	@Override
	@Column(name="memUsedRealTXT", type = Types.INTEGER)
	public Integer getMemUsedRealTXT() {
		return systemDetailProvider.getMemoryDetails().getMemUsedRealTXT();
	}

	@Override
	@Column(name="memUsedSwapTXT", type = Types.INTEGER)
	public Integer getMemUsedSwapTXT() {
		return systemDetailProvider.getMemoryDetails().getMemUsedSwapTXT();
	}

	@Override
	@Column(name="memCached", type = Types.INTEGER)
	public Integer getMemCached() {
		return systemDetailProvider.getMemoryDetails().getMemCached();
	}

	@Override
	@Column(name="memBuffer", type = Types.INTEGER)
	public Integer getMemBuffer() {
		return systemDetailProvider.getMemoryDetails().getMemBuffer();
	}

	@Override
	@Column(name="memShared", type = Types.INTEGER)
	public Integer getMemShared() {
		return systemDetailProvider.getMemoryDetails().getMemShared();
	}

	@Override
	@Column(name="memMinimumSwap", type = Types.INTEGER)
	public Integer getMemMinimumSwap() {
		return systemDetailProvider.getMemoryDetails().getMemMinimumSwap();
	}

	@Override
	@Column(name="memTotalFree", type = Types.INTEGER)
	public Integer getMemTotalFree() {
		return systemDetailProvider.getMemoryDetails().getMemTotalFree();
	}

	@Override
	@Column(name="memAvailRealTXT", type = Types.INTEGER)
	public Integer getMemAvailRealTXT() {
		return systemDetailProvider.getMemoryDetails().getMemAvailRealTXT();
	}

	@Override
	@Column(name="memTotalRealTXT", type = Types.INTEGER)
	public Integer getMemTotalRealTXT() {
		return systemDetailProvider.getMemoryDetails().getMemTotalRealTXT();
	}

	@Override
	@Column(name="memAvailSwapTXT", type = Types.INTEGER)
	public Integer getMemAvailSwapTXT() {
		return systemDetailProvider.getMemoryDetails().getMemAvailSwapTXT();
	}

	@Override
	@Column(name="memTotalSwapTXT", type = Types.INTEGER)
	public Integer getMemTotalSwapTXT() {
		return systemDetailProvider.getMemoryDetails().getMemTotalSwapTXT();
	}

	@Override
	@Column(name="memAvailReal", type = Types.INTEGER)
	public Integer getMemAvailReal() {
		return systemDetailProvider.getMemoryDetails().getMemAvailReal();
	}

	@Override
	@Column(name="memTotalReal", type = Types.INTEGER)
	public Integer getMemTotalReal() {
		return systemDetailProvider.getMemoryDetails().getMemTotalReal();
	}

	@Override
	@Column(name="memAvailSwap", type = Types.INTEGER)
	public Integer getMemAvailSwap() {
		return systemDetailProvider.getMemoryDetails().getMemAvailSwap();
	}

	@Override
	@Column(name="memTotalSwap", type = Types.INTEGER)
	public Integer getMemTotalSwap() {
		return systemDetailProvider.getMemoryDetails().getMemTotalSwap();
	}

	@Override
	@Column(name="memErrorName", type = Types.VARCHAR)
	public String getMemErrorName() {
		return systemDetailProvider.getMemoryDetails().getMemErrorName();
	}

	@Override
	@Column(name="memIndex", type = Types.INTEGER)
	public Integer getMemIndex() {
		return systemDetailProvider.getMemoryDetails().getMemIndex();
	}
}