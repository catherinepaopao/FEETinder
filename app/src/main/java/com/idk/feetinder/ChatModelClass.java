// Aadya
package com.idk.feetinder;

public class ChatModelClass {
    private int imageview1;
    private String textview1;
    private String textview2;
    private String textview3;
    private String divider = "----------------------------------------------------------------------------------------------";

    ChatModelClass(int imageview1, String textview1, String textview2, String textview3){
        this.imageview1=imageview1;
        this.textview1=textview1;
        this.textview2=textview2;
        this.textview3=textview3;
    }

    public int getImageview1(){
        return imageview1;
    }
    public String getTextview1(){
        return textview1;
    }
    public String getTextview2(){
        return textview2;
    }
    public String getTextview3(){
        return textview3;
    }
    public String getDivider(){
        return divider;
    }

}
