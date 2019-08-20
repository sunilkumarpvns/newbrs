package com.elitecore.aaa.core.plugins.transactionlogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.PathEvaluator;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.commons.util.sequencer.Sequencer;
import com.elitecore.core.commons.util.sequencer.SynchronizedSequencer;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FileWriterPool {
	
	private static final String MODULE = "FILE_WRITER-POOL";
	private Map<String, EliteFileWriter> locationToWriterMap;
	private FileLocationParser locationParser;
	private final int timeBoundryRollingUnit;
	private final String activeExtension;
	private final AAAServerContext serverContext;
	private final String sequencePosition;
	private final String sequenceRange;
	private final boolean isSequenceGlobalization;
	public Sequencer fileSequencer;
	private TimeSource timesource;
	
	public FileWriterPool(String locationExpression, String moduleName, int timeBoundryRollingUnit,
			String activeExtension, AAAServerContext serverContext, String sequencePosition, String sequenceRange,
			boolean isSequenceGlobalization) {
		this(locationExpression, moduleName, timeBoundryRollingUnit, activeExtension, serverContext, 
				sequencePosition, sequenceRange, isSequenceGlobalization, TimeSource.systemTimeSource());
	}
	
	FileWriterPool(String locationExpression, String moduleName, int timeBoundryRollingUnit,
			String activeExtension, AAAServerContext serverContext, String sequencePosition, String sequenceRange,
			boolean isSequenceGlobalization, TimeSource timesource) {
		this.timeBoundryRollingUnit = timeBoundryRollingUnit;
		this.activeExtension = activeExtension;
		this.serverContext = serverContext;
		this.timesource = timesource;
		this.locationToWriterMap = new HashMap<String, EliteFileWriter>();
		this.locationParser = new FileLocationParser(locationExpression, moduleName, timesource);
		this.sequencePosition = sequencePosition;
		this.sequenceRange = sequenceRange;
		this.isSequenceGlobalization = isSequenceGlobalization;
	}
	
	public void init() throws InvalidExpressionException {
		this.locationParser.init();
		if(isSequenceGlobalization) {
			this.fileSequencer = initSequencer(fileSequencer);
		}
	}
	
	public EliteFileWriter getWriter(ValueProvider valueProvider) 
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, IOException {
		String locKey = this.locationParser.getKey(valueProvider);
		EliteFileWriter eliteFileWriter = locationToWriterMap.get(locKey);
		if (eliteFileWriter == null) {
			eliteFileWriter = createNewWriter(locKey);
		}
		return eliteFileWriter;
	}

	private synchronized EliteFileWriter createNewWriter(String locKey)
			throws IOException {
		EliteFileWriter eliteFileWriter = locationToWriterMap.get(locKey);
		if (eliteFileWriter == null) {
			int lastFileSeparatorIndex = locKey.lastIndexOf(File.separator);
			File file = new File(locKey);
			String path = "";
			if (file.isAbsolute() == false) {
				path += serverContext.getServerHome() + File.separator + "logs" + File.separator;
			}
			if (lastFileSeparatorIndex >= 0) {
				path += locKey.substring(0, lastFileSeparatorIndex);
			}
			String fileName = locKey.substring(lastFileSeparatorIndex+1);
			Map<RollingTypeConstant, Integer> rollingTypeMap = new HashMap<RollingTypeConstant, Integer>();
			rollingTypeMap.put(RollingTypeConstant.TIME_BOUNDRY_ROLLING, this.timeBoundryRollingUnit);
			
			if(isSequenceGlobalization == false) {
				this.fileSequencer = initSequencer(fileSequencer);
			}
			
			eliteFileWriter = new EliteFileWriter.Builder()
								.rollingTypeMap(rollingTypeMap)
								.fileName(fileName)
								.activeFileExt(activeExtension)
								.destinationPath(path)
								.createBlankFiles(true)
								.sequencer(fileSequencer)
								.cdrSequenceRequired(true)
								.pattern(sequencePosition)
								.taskScheduler(serverContext.getTaskScheduler())
								.pathEvaluator(new PathEvaluator() {
									
									@Override
									public String evaluate(String dynamicPath) {
										return (locationParser.getLocation(dynamicPath));
									}
								})
								.timesource(timesource)
								.build();
			eliteFileWriter.open();
			serverContext.getTaskScheduler().scheduleIntervalBasedTask(
					new FileRoller(
							timeBoundryRollingUnit,
							eliteFileWriter
							)
				);
			locationToWriterMap.put(locKey, eliteFileWriter);
		}
		return eliteFileWriter;
	}
	

	private Sequencer initSequencer(Sequencer fileSequencer) {
		String prefix, suffix, startSeq, endSeq;
		if(sequenceRange != null) {
			String regx = "[a-z0-9A-Z]*\\[[a-z0-9A-Z]+\\-[a-z0-9A-Z]+\\][a-z0-9A-Z]*";
			if(Pattern.matches(regx, sequenceRange)){
				prefix = sequenceRange.substring(0, sequenceRange.indexOf('['));
				suffix = sequenceRange.substring(sequenceRange.indexOf(']')+1);
				startSeq = sequenceRange.substring(sequenceRange.indexOf('[')+1, sequenceRange.indexOf('-'));
				endSeq = sequenceRange.substring(sequenceRange.indexOf('-')+1, sequenceRange.indexOf(']'));
				fileSequencer = new SynchronizedSequencer(startSeq, endSeq, prefix, suffix);
				try {
					fileSequencer.init();
				} catch (InitializationFailedException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + sequenceRange + ". So sequencing will be disabled.");
					}
				}
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + sequenceRange + ". So sequencing will be disabled.");
				}
			}
		}
		return fileSequencer;
			
	}

	public void stop() {
		
		for (EliteFileWriter writer : this.locationToWriterMap.values()) {
			writer.flush();
			writer.close();
		}
	}

}
