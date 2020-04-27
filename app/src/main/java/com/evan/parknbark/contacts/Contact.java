package com.evan.parknbark.contacts;

public class Contact {
    private int fax;
    private int phoneNum;

    public Contact(int fax, int phoneNum) {
        this.fax = fax;
        this.phoneNum = phoneNum;
    }

    public Contact() {
    }

    public int getFax() {
        return fax;
    }

    public void setFax(int fax) {
        this.fax = fax;
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }
}
