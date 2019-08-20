package com.elitecore.core.serverx.snmp.mib.os.extended;

import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.serverx.snmp.mib.os.autogen.DskEntry;
import com.elitecore.core.serverx.snmp.mib.os.autogen.EnumDskErrorFlag;
import com.elitecore.core.serverx.snmp.mib.os.data.SystemDetailProvider.DiskDetails;

public class DskEntryMBeanImpl extends DskEntry {

	private static final long serialVersionUID = 1L;
	transient private final DiskDetails diskDetails;

	public DskEntryMBeanImpl(DiskDetails diskDetails) {
		this.diskDetails = diskDetails;
	}
	
	@Override
	@Column(name = "dskErrorMsg", type = Types.VARCHAR)
	public String getDskErrorMsg() {
		return diskDetails.getDskErrorMsg();
	}

	@Override
	@Column(name = "dskErrorFlag", type = Types.VARCHAR)
	public EnumDskErrorFlag getDskErrorFlag() {
		return diskDetails.getDskErrorFlag();
	}

	@Override
	@Column(name = "dskUsedHigh", type = Types.BIGINT)
	public Long getDskUsedHigh() {
		return diskDetails.getDskUsedHigh();
	}

	@Override
	@Column(name = "dskUsedLow", type = Types.BIGINT)
	public Long getDskUsedLow() {
		return diskDetails.getDskUsedLow();
	}

	@Override
	@Column(name = "dskAvailHigh", type = Types.BIGINT)
	public Long getDskAvailHigh() {
		return diskDetails.getDskAvailHigh();
	}

	@Override
	@Column(name = "dskAvailLow", type = Types.BIGINT)
	public Long getDskAvailLow() {
		return diskDetails.getDskAvailLow();
	}

	@Override
	@Column(name = "dskTotalHigh", type = Types.BIGINT)
	public Long getDskTotalHigh() {
		return diskDetails.getDskTotalHigh();
	}

	@Override
	@Column(name = "dskTotalLow", type = Types.BIGINT)
	public Long getDskTotalLow() {
		return diskDetails.getDskTotalLow();
	}

	@Override
	@Column(name = "dskPercentNode", type = Types.INTEGER)
	public Integer getDskPercentNode() {
		return diskDetails.getDskPercentNode();
	}

	@Override
	@Column(name = "dskPercent", type = Types.INTEGER)
	public Integer getDskPercent() {
		return diskDetails.getDskPercent();
	}

	@Override
	@Column(name = "dskUsed", type = Types.INTEGER)
	public Integer getDskUsed() {
		return diskDetails.getDskUsed();
	}

	@Override
	@Column(name = "dskAvail", type = Types.INTEGER)
	public Integer getDskAvail() {
		return diskDetails.getDskAvail();
	}

	@Override
	@Column(name = "dskTotal", type = Types.INTEGER)
	public Integer getDskTotal() {
		return diskDetails.getDskTotal();
	}

	@Override
	@Column(name = "dskMinPercent", type = Types.INTEGER)
	public Integer getDskMinPercent() {
		return diskDetails.getDskMinPercent();
	}

	@Override
	@Column(name = "dskMinimum", type = Types.INTEGER)
	public Integer getDskMinimum() {
		return diskDetails.getDskMinimum();
	}

	@Override
	@Column(name = "dskDevice", type = Types.VARCHAR)
	public String getDskDevice() {
		return diskDetails.getDskDevice();
	}

	@Override
	@Column(name = "dskPath", type = Types.VARCHAR)
	public String getDskPath() {
		return diskDetails.getDskPath();
	}

	@Override
	@Column(name = "dskIndex", type = Types.INTEGER)
	public Integer getDskIndex() {
		return diskDetails.getDskIndex();
	}
}