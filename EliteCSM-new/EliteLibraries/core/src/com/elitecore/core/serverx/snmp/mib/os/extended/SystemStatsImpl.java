package com.elitecore.core.serverx.snmp.mib.os.extended;

import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.serverx.snmp.mib.os.autogen.SystemStats;
import com.elitecore.core.serverx.snmp.mib.os.data.SystemDetailProvider;

public class SystemStatsImpl extends SystemStats {

	private static final long serialVersionUID = 1L;
	transient private final SystemDetailProvider systemDetailProvider;

	public SystemStatsImpl(SystemDetailProvider systemDetailProvider) {
		this.systemDetailProvider = systemDetailProvider;
	}
	
	@Override
	@Column(name = "ssCpuRawUser", type = Types.BIGINT)
	public Long getSsCpuRawUser() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawUser();
	}

	@Override
	@Column(name = "ssCpuRawGuestNice", type = Types.BIGINT)
	public Long getSsCpuRawGuestNice() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawGuestNice();
	}

	@Override
	@Column(name = "ssCpuRawGuest", type = Types.BIGINT)
	public Long getSsCpuRawGuest() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawGuest();
	}

	@Override
	@Column(name = "ssCpuRawSteal", type = Types.BIGINT)
	public Long getSsCpuRawSteal() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawSteal();
	}

	@Override
	@Column(name = "ssRawSwapOut", type = Types.BIGINT)
	public Long getSsRawSwapOut() {
		return systemDetailProvider.getCpuDetails().getSsRawSwapOut();
	}

	@Override
	@Column(name = "ssRawSwapIn", type = Types.BIGINT)
	public Long getSsRawSwapIn() {
		return systemDetailProvider.getCpuDetails().getSsRawSwapIn();
	}

	@Override
	@Column(name = "ssCpuRawSoftIRQ", type = Types.BIGINT)
	public Long getSsCpuRawSoftIRQ() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawSoftIRQ();
	}

	@Override
	@Column(name = "ssRawContexts", type = Types.BIGINT)
	public Long getSsRawContexts() {
		return systemDetailProvider.getCpuDetails().getSsRawContexts();
	}

	@Override
	@Column(name = "ssCpuIdle", type = Types.INTEGER)
	public Integer getSsCpuIdle() {
		return systemDetailProvider.getCpuDetails().getSsCpuIdle();
	}

	@Override
	@Column(name = "ssCpuSystem", type = Types.INTEGER)
	public Integer getSsCpuSystem() {
		return systemDetailProvider.getCpuDetails().getSsCpuSystem();
	}

	@Override
	@Column(name = "ssCpuUser", type = Types.INTEGER)
	public Integer getSsCpuUser() {
		return systemDetailProvider.getCpuDetails().getSsCpuUser();
	}

	@Override
	@Column(name = "ssSysContext", type = Types.INTEGER)
	public Integer getSsSysContext() {
		return systemDetailProvider.getCpuDetails().getSsSysContext();
	}

	@Override
	@Column(name = "ssSysInterrupts", type = Types.INTEGER)
	public Integer getSsSysInterrupts() {
		return systemDetailProvider.getCpuDetails().getSsSysInterrupts();
	}

	@Override
	@Column(name = "ssRawInterrupts", type = Types.BIGINT)
	public Long getSsRawInterrupts() {
		return systemDetailProvider.getCpuDetails().getSsRawInterrupts();
	}

	@Override
	@Column(name = "ssIORawReceived", type = Types.BIGINT)
	public Long getSsIORawReceived() {
		return systemDetailProvider.getCpuDetails().getSsIORawReceived();
	}

	@Override
	@Column(name = "ssIOReceive", type = Types.INTEGER)
	public Integer getSsIOReceive() {
		return systemDetailProvider.getCpuDetails().getSsIOReceive();
	}

	@Override
	@Column(name = "ssIORawSent", type = Types.BIGINT)
	public Long getSsIORawSent() {
		return systemDetailProvider.getCpuDetails().getSsIORawSent();
	}

	@Override
	@Column(name = "ssIOSent", type = Types.INTEGER)
	public Integer getSsIOSent() {
		return systemDetailProvider.getCpuDetails().getSsIOSent();
	}

	@Override
	@Column(name = "ssSwapOut", type = Types.INTEGER)
	public Integer getSsSwapOut() {
		return systemDetailProvider.getCpuDetails().getSsSwapOut();
	}

	@Override
	@Column(name = "ssCpuRawInterrupt", type = Types.BIGINT)
	public Long getSsCpuRawInterrupt() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawInterrupt();
	}

	@Override
	@Column(name = "ssSwapIn", type = Types.INTEGER)
	public Integer getSsSwapIn() {
		return systemDetailProvider.getCpuDetails().getSsSwapIn();
	}

	@Override
	@Column(name = "ssCpuRawKernel", type = Types.BIGINT)
	public Long getSsCpuRawKernel() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawKernel();
	}

	@Override
	@Column(name = "ssErrorName", type = Types.VARCHAR)
	public String getSsErrorName() {
		return systemDetailProvider.getCpuDetails().getSsErrorName();
	}

	@Override
	@Column(name = "ssCpuRawWait", type = Types.BIGINT)
	public Long getSsCpuRawWait() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawWait();
	}

	@Override
	@Column(name = "ssIndex", type = Types.INTEGER)
	public Integer getSsIndex() {
		return systemDetailProvider.getCpuDetails().getSsIndex();
	}

	@Override
	@Column(name = "ssCpuRawIdle", type = Types.BIGINT)
	public Long getSsCpuRawIdle() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawIdle();
	}

	@Override
	@Column(name = "ssCpuRawSystem", type = Types.BIGINT)
	public Long getSsCpuRawSystem() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawSystem();
	}

	@Override
	@Column(name = "ssCpuRawNice", type = Types.BIGINT)
	public Long getSsCpuRawNice() {
		return systemDetailProvider.getCpuDetails().getSsCpuRawNice();
	}
}