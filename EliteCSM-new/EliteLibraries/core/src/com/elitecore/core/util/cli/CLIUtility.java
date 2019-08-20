package com.elitecore.core.util.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CLIUtility {

	public static List<String> readCommandsFromFile(String filePath) throws FileNotFoundException, IOException {
		List<String> commandList = new ArrayList<String>();
		File batchInputFile = new File(filePath);
		BufferedReader reader =  new BufferedReader(new FileReader(batchInputFile));
		String command = reader.readLine();
		while (command != null) {
			commandList.add(command.trim());
			command = reader.readLine();
		}
		reader.close();
		return commandList;
	}
	
	public static FileWriter getOutputFile(String outputFileStr) throws IOException {
		File outputFile = new File(outputFileStr);
		return new FileWriter(outputFile , true);
	}
}
