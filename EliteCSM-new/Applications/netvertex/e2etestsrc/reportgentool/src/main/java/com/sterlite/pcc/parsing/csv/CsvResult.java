package com.sterlite.pcc.parsing.csv;

public class CsvResult {

  private CsvHeader header;
  private CsvData csvData;


  public CsvResult(CsvHeader header, CsvData csvData) {
    this.header = header;
    this.csvData = csvData;
  }

  public CsvData getCsvData() {
    return csvData;
  }

  public CsvHeader getHeader() {
    return header;
  }
}
