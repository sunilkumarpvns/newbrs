package com.sterlite.pcc.parsing.csv;

import java.util.ArrayList;
import java.util.List;

public class CsvData {
  private List<CsvLine> csvLines;

  public CsvData() {
    csvLines = new ArrayList<>();
  }

  public CsvData(List<CsvLine> csvLines) {
    this.csvLines = csvLines;
  }

  public void add(CsvLine csvLine) {
    csvLines.add(csvLine);
  }

  public List<CsvLine> getCsvLines() {
    return csvLines;
  }
}
