package com.sterlite.pcc.parsing.csv;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVRecord;

public class CsvLine {
  private List<String> csvData;

  CsvLine() {
    csvData = new ArrayList<>();
  }

  private void addColoum(String coloum) {
    csvData.add(coloum);
  }


  public static CsvLine fromCsvRecord(CSVRecord csvRecord) {
    CsvLine csvLine = new CsvLine();

    csvRecord.iterator().forEachRemaining(csvLine::addColoum);

    return csvLine;
  }

  public List<String> getColoumData() {
    return csvData;
  }
}
