/*
 * Open API Goibibo API Program
 * Open API Spec of Goibibo APIs. You can find more about Open API Spec here (https://openapis.org/)
 *
 * OpenAPI spec version: 1.0.0
 * Contact: apiteam@goibibo.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.brs.searchservice.models.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

/**
 * Ratings
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-07-25T08:25:14.822+05:30")
public class Ratings {
  @JsonProperty("onBoardExperience")
  private BigDecimal onBoardExperience = null;

  @JsonProperty("onTimeAD")
  private BigDecimal onTimeAD = null;

  @JsonProperty("stopOverExperience")
  private BigDecimal stopOverExperience = null;

  @JsonProperty("seats")
  private BigDecimal seats = null;

  public Ratings onBoardExperience(BigDecimal onBoardExperience) {
    this.onBoardExperience = onBoardExperience;
    return this;
  }

   /**
   * Get onBoardExperience
   * @return onBoardExperience
  **/
  @ApiModelProperty(required = true, value = "")
  public BigDecimal getOnBoardExperience() {
    return onBoardExperience;
  }

  public void setOnBoardExperience(BigDecimal onBoardExperience) {
    this.onBoardExperience = onBoardExperience;
  }

  public Ratings onTimeAD(BigDecimal onTimeAD) {
    this.onTimeAD = onTimeAD;
    return this;
  }

   /**
   * Get onTimeAD
   * @return onTimeAD
  **/
  @ApiModelProperty(required = true, value = "")
  public BigDecimal getOnTimeAD() {
    return onTimeAD;
  }

  public void setOnTimeAD(BigDecimal onTimeAD) {
    this.onTimeAD = onTimeAD;
  }

  public Ratings stopOverExperience(BigDecimal stopOverExperience) {
    this.stopOverExperience = stopOverExperience;
    return this;
  }

   /**
   * Get stopOverExperience
   * @return stopOverExperience
  **/
  @ApiModelProperty(required = true, value = "")
  public BigDecimal getStopOverExperience() {
    return stopOverExperience;
  }

  public void setStopOverExperience(BigDecimal stopOverExperience) {
    this.stopOverExperience = stopOverExperience;
  }

  public Ratings seats(BigDecimal seats) {
    this.seats = seats;
    return this;
  }

   /**
   * Get seats
   * @return seats
  **/
  @ApiModelProperty(required = true, value = "")
  public BigDecimal getSeats() {
    return seats;
  }

  public void setSeats(BigDecimal seats) {
    this.seats = seats;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ratings ratings = (Ratings) o;
    return Objects.equals(this.onBoardExperience, ratings.onBoardExperience) &&
        Objects.equals(this.onTimeAD, ratings.onTimeAD) &&
        Objects.equals(this.stopOverExperience, ratings.stopOverExperience) &&
        Objects.equals(this.seats, ratings.seats);
  }

  @Override
  public int hashCode() {
    return Objects.hash(onBoardExperience, onTimeAD, stopOverExperience, seats);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Ratings {\n");
    
    sb.append("    onBoardExperience: ").append(toIndentedString(onBoardExperience)).append("\n");
    sb.append("    onTimeAD: ").append(toIndentedString(onTimeAD)).append("\n");
    sb.append("    stopOverExperience: ").append(toIndentedString(stopOverExperience)).append("\n");
    sb.append("    seats: ").append(toIndentedString(seats)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

