package com.elitecore.aaa.core.plugins.transactionlogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.plugins.conf.FormatMappingData;
import com.elitecore.aaa.core.plugins.conf.TransactionLoggerConfigurable;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.plugins.BasePlugin;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.serverx.Stopable;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public abstract class TransactionLogger<T> extends BasePlugin implements Stopable{
	
	private static final String ACTIVE_FILE_EXTENSION = "inp";
	
	private Map<String, List<Formatter>> keyMap;
	private FileWriterPool fileWriterPool;

	private TimeSource timesource;
	
	protected abstract String getModule();
	protected abstract TransactionLoggerConfigurable<T> getConfigurable();

	public TransactionLogger(PluginContext pluginContext, PluginInfo pluginInfo) {
		this(pluginContext, pluginInfo, TimeSource.systemTimeSource());
	}
	
	public TransactionLogger(PluginContext pluginContext, PluginInfo pluginInfo, TimeSource timesource) {
		super(pluginContext, pluginInfo);
		this.timesource = timesource;
		this.keyMap = new HashMap<String, List<Formatter>>();
	}

	@Override
	public void init() throws InitializationFailedException {
		this.fileWriterPool = new FileWriterPool(getConfigurable().getLogFileName(), getModule(), getConfigurable().getTimeBoundryRollingUnit(), 
				ACTIVE_FILE_EXTENSION, (AAAServerContext)getPluginContext().getServerContext(), getConfigurable().getSequencePosition(),
				getConfigurable().getSequenceRange(), getConfigurable().isSequenceGlobalization(), timesource);
		formatMappings();
		
		try {
			fileWriterPool.init();
		} catch (InvalidExpressionException e) {
			LogManager.getLogger().warn(getModule(), "Initialization failed, Reason: " + e.getMessage());
			LogManager.getLogger().trace(getModule(), e);
		}
		
		getPluginContext().getServerContext().registerStopable(this);
	}
	
	private void formatMappings() {
		for (FormatMappingData data : getConfigurable().getFormatMappings()) {
			List<Formatter> formatters = new ArrayList<Formatter>();
			char[] values = data.getFormat().toCharArray();
			String tmp = "";
			try {
				for (int i=0; i<values.length; i++) {
					switch(values[i]) {

					case '{':
						Formatter stringFormatter = new StringFormatter(tmp);
						stringFormatter.init();
						formatters.add(stringFormatter);
						tmp = "";
						break;

					case '}':
						if (tmp.contains("$")) {
							tmp = tmp.replaceAll("\\$", "\\\\\\$");
						}
						Formatter expressionFormatter = new ExpressionFormatter(tmp);
						expressionFormatter.init();
						formatters.add(expressionFormatter);
						tmp = "";
						break;

					case '\\':
						tmp += values[++i];
						break;

					default :
						tmp += values[i];
						break;

					}
				}
				Formatter stringFormatter = new StringFormatter(tmp);
				stringFormatter.init();
				formatters.add(stringFormatter);
				this.keyMap.put(data.getKey(), formatters);
			} catch (InvalidExpressionException e) {
				LogManager.getLogger().warn(getModule(), "Format: " + data.getFormat() + " for Key: " + data.getKey() + 
						" parsing failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(getModule(), e);
			}
		}
	}

	@Override
	public void stop() {
		this.fileWriterPool.stop();
	}
	
	private EliteFileWriter getEliteFileWriter(ValueProvider valueProvider) {
		EliteFileWriter eliteFileWriter = null;
		try {
			eliteFileWriter = fileWriterPool.getWriter(valueProvider);
		} catch (InvalidTypeCastException e) {
			LogManager.getLogger().warn(getModule(), "Unable to write in file, Reason: " + e.getMessage());
			LogManager.getLogger().trace(getModule(), e);
		} catch (IllegalArgumentException e) {
			LogManager.getLogger().warn(getModule(), "Unable to write in file, Reason: " + e.getMessage());
			LogManager.getLogger().trace(getModule(), e);
		} catch (MissingIdentifierException e) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getModule(), "Unable to write in file, Reason: " + e.getMessage());
			}
		} catch (IOException e) {
			LogManager.getLogger().warn(getModule(), "File writer creation failed, Reason: " + e.getMessage());
			LogManager.getLogger().trace(getModule(), e);
		}
		return eliteFileWriter;
	}
	
	protected void logTransaction(ValueProvider valueProvider, String key) {
		
		List<Formatter> formatters = keyMap.get(key);
		if (Collectionz.isNullOrEmpty(formatters)) {
			LogManager.getLogger().warn(getModule(), "Argument not configured for diameter transaction logger");
			return;
		}
		
		EliteFileWriter writer = getEliteFileWriter(valueProvider);
		if (writer == null) {
			return;
		}
		
		try {
			String record = "";
			for (Formatter formatter : formatters) {
				try {
					record += formatter.process(valueProvider);
				} catch (MissingIdentifierException e) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(getModule(), "Record can be incomplete for key: " + key + ", Reason: " + e.getMessage());
					}
				}
			}
			writer.appendRecordln(record);
		} catch (InvalidTypeCastException e) {
			LogManager.getLogger().warn(getModule(), "Unable to generate record for key: " + key + ", Reason: " + e.getMessage());
			LogManager.getLogger().trace(getModule(), e);
		} catch (IllegalArgumentException e) {
			LogManager.getLogger().warn(getModule(), "Unable to generate record for key: " + key + ", Reason: " + e.getMessage());
			LogManager.getLogger().trace(getModule(), e);
		}
		
	}
}
