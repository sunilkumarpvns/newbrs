package com.elitecore.commons.base;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nonnull;

public abstract class TimeRange {
	
	public static TimeRange open(@Nonnull String pattern, @Nonnull String startDateValue, @Nonnull String endDateValue) throws ParseException {
		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(pattern);
		return new TimeRangeOpen(simpleDateFormatThreadLocal, 
				timestampOf(simpleDateFormatThreadLocal.get(), startDateValue), timestampOf(simpleDateFormatThreadLocal.get(), endDateValue));
	}

	public static TimeRange closed(@Nonnull String pattern, @Nonnull String startDateValue, @Nonnull String endDateValue) throws ParseException {
		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(pattern);
		return new TimeRangeClosed(simpleDateFormatThreadLocal, 
				timestampOf(simpleDateFormatThreadLocal.get(), startDateValue), timestampOf(simpleDateFormatThreadLocal.get() ,endDateValue));
	}

	public static TimeRange open(@Nonnull String pattern, @Nonnull String startDateValue) throws ParseException {
		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(pattern);
		return new TimeRangeGreaterThan(simpleDateFormatThreadLocal, timestampOf(simpleDateFormatThreadLocal.get(), startDateValue));
	}
	
	public static TimeRange closed(@Nonnull String pattern, @Nonnull String startDateValue) throws ParseException {
		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(pattern);
		return new TimeRangeAtLeast(simpleDateFormatThreadLocal, timestampOf(simpleDateFormatThreadLocal.get(),startDateValue));
	}
	
	public static TimeRange atMost(@Nonnull String pattern, @Nonnull String endDateValue) throws ParseException {
		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(pattern);
		return new TimeRangeAtMost(simpleDateFormatThreadLocal, timestampOf(simpleDateFormatThreadLocal.get(),endDateValue));
	}
	
	public static TimeRange lessThan(@Nonnull String pattern, @Nonnull String endDateValue) throws ParseException {
		SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(pattern);
		return new TimeRangeLessThan(simpleDateFormatThreadLocal, timestampOf(simpleDateFormatThreadLocal.get(),endDateValue));
	}
	
	public static TimeRange open() {
		return new TimeRangeInfinite();
	}
	
	static Timestamp timestampOf(SimpleDateFormat format, String date) throws ParseException {
		Date startDate = format.parse(date);
		return new Timestamp(startDate.getTime());
	}

	public abstract boolean contains(String checkingDate) throws ParseException;

	public abstract boolean contains(Timestamp checkingTimestamp);


}

class TimeRangeOpen extends TimeRange {

	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;

	TimeRangeOpen(SimpleDateFormatThreadLocal simpleDateFormatThreadLocal, Timestamp startTimestamp, Timestamp endTimestamp) {
		this.simpleDateFormatThreadLocal = simpleDateFormatThreadLocal;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}

	public boolean contains(String checkingDate) throws ParseException {
		return contains(timestampOf(simpleDateFormatThreadLocal.get(), checkingDate));
	}

	@Override
	public boolean contains(Timestamp checkingTimestamp) {
		return checkingTimestamp.after(startTimestamp) 
				&& checkingTimestamp.before(endTimestamp);
	}
}

class TimeRangeClosed extends TimeRange {

	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;

	public TimeRangeClosed(SimpleDateFormatThreadLocal simpleDateFormatThreadLocal, Timestamp startTimestamp,
			Timestamp endTimestamp) {
		this.simpleDateFormatThreadLocal = simpleDateFormatThreadLocal;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		
	}

	public boolean contains(String checkingDate) throws ParseException {
		return contains(timestampOf(simpleDateFormatThreadLocal.get(), checkingDate));
	}

	@Override
	public boolean contains(Timestamp checkingTimestamp) {
		return (checkingTimestamp.after(startTimestamp) || checkingTimestamp.equals(startTimestamp))
				&& (checkingTimestamp.before(endTimestamp) || checkingTimestamp.equals(endTimestamp));
	}
}

class TimeRangeGreaterThan extends TimeRange {

	private Timestamp startTimestamp;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;

	public TimeRangeGreaterThan(SimpleDateFormatThreadLocal simpleDateFormatThreadLocal, Timestamp startTimestamp) {
		this.simpleDateFormatThreadLocal = simpleDateFormatThreadLocal;
		this.startTimestamp = startTimestamp;
	}

	@Override
	public boolean contains(String checkingDate) throws ParseException {
		return contains(timestampOf(simpleDateFormatThreadLocal.get(), checkingDate));
	}

	@Override
	public boolean contains(Timestamp checkingTimestamp) {
		return checkingTimestamp.after(startTimestamp);
	}
}

class TimeRangeAtLeast extends TimeRange {

	private Timestamp startTimestamp;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;

	public TimeRangeAtLeast(SimpleDateFormatThreadLocal simpleDateFormatThreadLocal, Timestamp startTimestamp) {
		this.simpleDateFormatThreadLocal = simpleDateFormatThreadLocal;
		this.startTimestamp = startTimestamp;
	}

	@Override
	public boolean contains(String checkingDate) throws ParseException {
		return contains(timestampOf(simpleDateFormatThreadLocal.get(),checkingDate));
	}

	@Override
	public boolean contains(Timestamp checkingTimestamp) {
		return checkingTimestamp.after(startTimestamp) || checkingTimestamp.equals(startTimestamp);
	}
}

class TimeRangeAtMost extends TimeRange {

	private Timestamp endTimestamp;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;

	public TimeRangeAtMost(SimpleDateFormatThreadLocal simpleDateFormatThreadLocal, Timestamp endTimestamp) {
		this.simpleDateFormatThreadLocal = simpleDateFormatThreadLocal;
		this.endTimestamp = endTimestamp;
	}

	@Override
	public boolean contains(String checkingDate) throws ParseException {
		return contains(timestampOf(simpleDateFormatThreadLocal.get(),checkingDate));
	}

	@Override
	public boolean contains(Timestamp checkingTimestamp) {
		return checkingTimestamp.before(endTimestamp) || checkingTimestamp.equals(endTimestamp);
	}
}

class TimeRangeLessThan extends TimeRange {

	private Timestamp endTimestamp;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;

	public TimeRangeLessThan(SimpleDateFormatThreadLocal simpleDateFormatThreadLocal, Timestamp endTimestamp) {
		this.simpleDateFormatThreadLocal = simpleDateFormatThreadLocal;
		this.endTimestamp = endTimestamp;
	}

	@Override
	public boolean contains(String checkingDate) throws ParseException {
		return contains(timestampOf(simpleDateFormatThreadLocal.get(), checkingDate));
	}

	@Override
	public boolean contains(Timestamp checkingTimestamp) {
		return checkingTimestamp.before(endTimestamp);
	}
}

class TimeRangeInfinite extends TimeRange {

	@Override
	public boolean contains(String checkingDate) {
		return true;
	}

	@Override
	public boolean contains(Timestamp checkingTimestamp) {
		return true;
	}
	
}