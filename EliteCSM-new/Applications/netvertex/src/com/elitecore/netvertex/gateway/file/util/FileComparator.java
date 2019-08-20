package com.elitecore.netvertex.gateway.file.util;


import java.io.File;
import java.util.Comparator;

import com.elitecore.netvertex.core.constant.OfflineRnCConstants;

public class FileComparator implements Comparator<File> {

	/** The sorting type. */
	private String sortingType = OfflineRnCConstants.NA_ORDER;

	/** The sorting criteria. */
	private String sortingCrieteria = OfflineRnCConstants.LAST_MODIFIED_DATE;

	/**
	 * Instantiates a new comparator for sorting.
	 *
	 * @param sortingType the sorting type
	 * @param sortingCrieteria the sorting crieteria
	 */
	public FileComparator(String sortingType, String sortingCrieteria){
		this.sortingType = sortingType;
		this.sortingCrieteria = sortingCrieteria;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(File file1, File file2) {
		if(sortingCrieteria.equalsIgnoreCase(OfflineRnCConstants.LAST_MODIFIED_DATE)) {
			int result = Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
			if(sortingType.equalsIgnoreCase(OfflineRnCConstants.ASCENDING_ORDER)){
				return result * 1;
			}else if(sortingType.equalsIgnoreCase(OfflineRnCConstants.DESCENDING_ORDER)){
				return result * (-1);
			}else{
				return result*0;
			}
		} else if(sortingCrieteria.equalsIgnoreCase(OfflineRnCConstants.FILE_NAME)){
			int result = file1.getName().compareTo(file2.getName());
			if(sortingType.equalsIgnoreCase(OfflineRnCConstants.ASCENDING_ORDER)){
				return result * 1;
			}else if(sortingType.equalsIgnoreCase(OfflineRnCConstants.DESCENDING_ORDER)){
				return result * (-1);
			}else{
				return result * 0;
			}
		}else if(sortingCrieteria.equalsIgnoreCase(OfflineRnCConstants.NUMERIC_FILENAME)) {
			Integer intVal1 = new Integer(file1.getName());
			Integer intVal2 = new Integer(file2.getName());
			if(sortingType.equalsIgnoreCase(OfflineRnCConstants.DESCENDING_ORDER)){
				return intVal2.compareTo(intVal1);
			}else{
				return intVal1.compareTo(intVal2);
			}
		}  else {
			int result = Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
			if(sortingType.equalsIgnoreCase(OfflineRnCConstants.ASCENDING_ORDER)){
				return result * 1;
			}else if(sortingType.equalsIgnoreCase(OfflineRnCConstants.DESCENDING_ORDER)){
				return result * (-1);
			}else{
				return result * 0;
			}
		}
	}
}
