package com.evan.parknbark.contacts;

public class Contact {
    private String fax;
    private String  phoneNum;

    public Contact(String fax, String phoneNum) {
        this.fax = fax;
        this.phoneNum = phoneNum;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Contact() {
    }
}
