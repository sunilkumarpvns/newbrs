package com.sterlite.pcc.parsing.csv;

import java.util.List;
import java.util.Map;

public class CsvHeader {
  private List<String> headers;

  public CsvHeader(List<String> headers) {
    this.headers = headers;
  }

  public String getHeaderAt(int index) {
    return headers.get(index);
  }
}
