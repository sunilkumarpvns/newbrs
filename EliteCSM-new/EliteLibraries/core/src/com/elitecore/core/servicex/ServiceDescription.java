package com.elitecore.core.servicex;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Equality;

/**
 * 
 * @author sanjay.dhamelia
 * @author narendra.pathai
 *
 */
public class ServiceDescription {
	
	private @Nonnull final String name;
	private @Nonnull final String status;
	private @Nullable final String socketAddress;	
	private @Nullable final Date startDate;
	private @Nullable final String remarks;
	
	public ServiceDescription(@Nonnull String name, @Nonnull String status,
			@Nullable String socketAddress, @Nullable Date startDate, 
			@Nullable String remarks) {
		this.name = checkNotNull(name, "name is null");
		this.status = checkNotNull(status, "status is null");
		this.socketAddress = socketAddress;
		this.startDate = startDate;
		this.remarks= remarks;
	}
	
	public @Nonnull String getName() {
		return name;
	}
	
	public @Nonnull String getStatus() {
		return status;
	}
	
	public @Nullable String getSocketAddress() {
		return socketAddress;
	}
	
	public @Nullable Date getStartDate(){
		return startDate;
	}
	
	public @Nullable String getRemarks(){
		return remarks;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ServiceDescription)) {
			return false;
		}
		
		ServiceDescription that = (ServiceDescription) obj;
		
		return Equality.areEqual(getName(), that.getName())
				&& Equality.areEqual(getStatus(), that.getStatus())
				&& Equality.areEqual(getRemarks(), that.getRemarks())
				&& Equality.areEqual(getStartDate(), that.getStartDate())
				&& Equality.areEqual(getSocketAddress(), that.getSocketAddress());
	}
}
