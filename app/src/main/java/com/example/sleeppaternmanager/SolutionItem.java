package com.example.sleeppaternmanager;

public class SolutionItem {
    int imgRes;
    String contents;
    boolean check,isRequiredvalue;

    public SolutionItem(String solution, boolean check, boolean isRequiredvalue) {
        this.contents = solution;
        this.check = check;
        this.isRequiredvalue = isRequiredvalue;
        this.imgRes = (check ? R.drawable.verified : R.drawable.unverified);
    }

    public SolutionItem() {
        this.contents = "";
        this.check = false;
        this.isRequiredvalue = false;
        this.imgRes = R.drawable.unverified;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }
    public boolean getRequiredvalue() {return isRequiredvalue;}
    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public String getContents() {
        return contents;
    }

    public int getImgRes() {
        return imgRes;
    }
}
