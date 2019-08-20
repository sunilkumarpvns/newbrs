package com.elitecore.aaa.core.util.cli;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.drivers.IEliteAcctDriver;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;

/**
 * 
 * @author narendra.pathai
 *
 */
public class CdrFlushCommand extends EliteBaseCommand {
	@VisibleForTesting static final String PARAM_DETAIL_LOCAL = "-d";
	@VisibleForTesting static final String PARAM_CSV = "-c";
	@VisibleForTesting static final String PARAM_ALL = "-all";
	
	private static final String[] HEADER = {"DRIVER_NAME", "DRIVER_TYPE", "RESULT"};
	private static final int[] WIDTH = {40, 30, 30};
	
	private static final CdrFlushCommand INSTANCE = new CdrFlushCommand();

	private List<FlushEventHandler> handlers;

	@VisibleForTesting
	CdrFlushCommand() {
		handlers = new ArrayList<CdrFlushCommand.FlushEventHandler>(2);
	}

	public static CdrFlushCommand getInstance() {
		return INSTANCE;
	}

	/**
	 * Registers {@code flushEventHandler} that will receive flush events
	 */
	public void registerFlushEventHandler(@Nonnull FlushEventHandler flushEventHandler) {
		this.handlers.add(checkNotNull(flushEventHandler, "flushEventHandler is null"));
	}

	@Override
	public String execute(String parameter) {
		if (Strings.isNullOrBlank(parameter)) {
			return getHelp();
		}

		if (isHelpParameter(parameter)) {
			return getHelp();
		}

		String response = null;

		if (isDetailLocal(parameter)) {
			response = flushCdr(EnumSet.of(DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER, DriverTypes.NAS_DETAIL_LOCAL_ACCT_DRIVER));					
		} else if(isClassicCSV(parameter)) {
			response = flushCdr(EnumSet.of(DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER, DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER));					
		} else if (isAll(parameter)) {
			response = flushAllCdr();
		} else {
			response = invalidArgumentMessage(parameter);
		}

		return response;
	}

	private String invalidArgumentMessage(String parameter) {
		return "Invalid Argument: " + parameter + getHelp();
	}

	private String flushAllCdr() {
		return flushCdr(allCdrDriverTypes());
	}

	private Predicate<DriverTypes> allCdrDriverTypes() {
		return new Predicate<DriverTypes>() {

			@Override
			public boolean apply(DriverTypes input) {
				return input == DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER
				|| input == DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER
				|| input == DriverTypes.NAS_DETAIL_LOCAL_ACCT_DRIVER
				|| input == DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER;
			}
		};
	}

	private String flushCdr(EnumSet<DriverTypes> driverTypes) {
		return flushCdr(specificDriverTypes(driverTypes));
	}

	private Predicate<DriverTypes> specificDriverTypes(final EnumSet<DriverTypes> driverTypes) {
		return new Predicate<DriverTypes>() {

			@Override
			public boolean apply(DriverTypes input) {
				return driverTypes.contains(input);
			}
		};
	}

	private String flushCdr(Predicate<DriverTypes> driverTypePredicate) {
		TableFormatter formatter = new TableFormatter(HEADER, WIDTH, TableFormatter.OUTER_BORDER);

		for (FlushEventHandler handler : handlers) {
			handler.onEvent(driverTypePredicate, formatter);
		}

		return formatter.getFormattedValues();
	}

	private boolean isAll(String parameter) {
		return PARAM_ALL.equalsIgnoreCase(parameter);
	}

	private boolean isClassicCSV(String parameter) {
		return PARAM_CSV.equalsIgnoreCase(parameter);
	}

	private boolean isDetailLocal(String parameter) {
		return PARAM_DETAIL_LOCAL.equalsIgnoreCase(parameter);
	}

	@Override
	public String getCommandName() {
		return "cdrflush";
	}

	@Override
	public String getDescription() {
		return "Flushing the CDR into Detail Local or Classic CSV Format";			
	}

	private String getHelp(){
		StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append("\nUsage : cdrflush <options>");
		descriptionBuilder.append("\nPossible options");
		descriptionBuilder.append(String.format("\n    %s    Flushes the CDR in the Classic CSV Format.", PARAM_CSV));
		descriptionBuilder.append(String.format("\n    %s    Flushes the CDR in the Detail Local Format.", PARAM_DETAIL_LOCAL));
		descriptionBuilder.append(String.format("\n    %s  Flushes the CDR in All the above mentioned format.", PARAM_ALL));
		return descriptionBuilder.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return String.format("{'cdrflush':{'%s':{}, '%s':{}, '%s':{}, '%s':{}}}",
				HELP_OPTION,
				PARAM_ALL,
				PARAM_CSV,
				PARAM_DETAIL_LOCAL);
	}

	/**
	 * A handler that is called when flush event is fired.
	 * Implementation should handle the flush event using this handler.
	 * 
	 * <p>Handlers can be registered using {@link CdrFlushCommand#registerFlushEventHandler(FlushEventHandler)}
	 * method.
	 *
	 */
	public abstract static class FlushEventHandler {
		/**
		 * Called when flush event is fired. Implementation should pass all the configured drivers through
		 * the predicate and if succeeds then should flush that driver using {@link #tryFlush(DriverConfiguration, DriverManager)}.
		 * and add the result in table formatter
		 * 
		 * @param driverTypePredicate type filter 
		 * @param tableFormatter output formatter 
		 */
		public abstract void onEvent(@Nonnull Predicate<DriverTypes> driverTypePredicate,
				@Nonnull TableFormatter tableFormatter);

		/**
		 * Tries to flush the driver with provided configuration using the driver manager.
		 * If driver is not found from driver manager then that driver result contains not 
		 * initialized status.
		 *  
		 * @param driverConfiguration a non-null driver configuration of driver that should be flushed
		 * @param driverManager a non-null manager using which driver will be located 
		 * @return output result
		 */
		protected String[] tryFlush(@Nonnull DriverConfiguration driverConfiguration,
				@Nullable DriverManager driverManager) {

			IEliteAcctDriver driver = (IEliteAcctDriver) driverManager.getDriver(driverConfiguration.getDriverInstanceId());

			if (driver != null) {
				return new String[] {
						driverConfiguration.getDriverName(),
						driverConfiguration.getDriverType().name(),
						driver.cdrflush()};
			} else {
				return new String[] {
						driverConfiguration.getDriverName(),
						driverConfiguration.getDriverType().name(),
						" not initialized"};
			}
		}
	}
}
