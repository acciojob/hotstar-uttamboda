package com.driver.EntryDto;

import com.driver.model.SubscriptionType;
import java.util.Date;

public class SubscriptionRequestDto {
  private int userId;
  private SubscriptionType subscriptionType;
  private int noOfScreensSubscribed;
  private Date startSubscriptionDate;
  private int totalAmountPaid;

  public SubscriptionRequestDto() {
  }

  public SubscriptionRequestDto(int userId, SubscriptionType subscriptionType, int noOfScreensSubscribed, Date startSubscriptionDate, int totalAmountPaid) {
    this.userId = userId;
    this.subscriptionType = subscriptionType;
    this.noOfScreensSubscribed = noOfScreensSubscribed;
    this.startSubscriptionDate = startSubscriptionDate;
    this.totalAmountPaid = totalAmountPaid;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public SubscriptionType getSubscriptionType() {
    return subscriptionType;
  }

  public void setSubscriptionType(SubscriptionType subscriptionType) {
    this.subscriptionType = subscriptionType;
  }

  public int getNoOfScreensSubscribed() {
    return noOfScreensSubscribed;
  }

  public void setNoOfScreensSubscribed(int noOfScreensSubscribed) {
    this.noOfScreensSubscribed = noOfScreensSubscribed;
  }

  public Date getStartSubscriptionDate() {
    return startSubscriptionDate;
  }

  public void setStartSubscriptionDate(Date startSubscriptionDate) {
    this.startSubscriptionDate = startSubscriptionDate;
  }

  public int getTotalAmountPaid() {
    return totalAmountPaid;
  }

  public void setTotalAmountPaid(int totalAmountPaid) {
    this.totalAmountPaid = totalAmountPaid;
  }
}
