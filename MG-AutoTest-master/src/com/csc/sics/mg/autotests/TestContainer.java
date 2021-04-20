package com.csc.sics.mg.autotests;

import java.util.ArrayList;
import java.util.List;

public class TestContainer {

  private List<TestCase> business;
  private List<TestCase> businessPartner;
  private List<TestCase> claims;

  public List<TestCase> getClaims() {
	return claims;
}

public void setClaims(List<TestCase> claims) {
	this.claims = claims;
}

public List<TestCase> getBusiness() {
    return business;
  }

  public void setBusiness(List<TestCase> business) {
    this.business = business;
  }

  public List<TestCase> getBusinessPartner() {
    return businessPartner;
  }

  public void setBusinessPartner(List<TestCase> businessPartner) {
    this.businessPartner = businessPartner;
  }

  public List<TestCase> getAllTestCases() {
    List<TestCase> allTestCases = new ArrayList<TestCase>();
    allTestCases.addAll(this.getBusiness());
    allTestCases.addAll(this.getBusinessPartner());
    allTestCases.addAll(this.getClaims());
    return allTestCases;
  }
}
