package com.elitecore.aaa.diameter.translators.data;

import javax.xml.bind.annotation.XmlElement;

class MapElements
{
  @XmlElement public String  key;
  @XmlElement public String value;

  private MapElements() {} //Required by JAXB

  public MapElements(String key, String value)
  {
    this.key   = key;
    this.value = value;
  }
}
