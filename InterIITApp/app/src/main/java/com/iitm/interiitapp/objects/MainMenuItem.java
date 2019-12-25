package com.iitm.interiitapp.objects;

public class MainMenuItem{
    public MainMenuItem(int icon, String title, String info, Class toActivity){
        this.icon = icon;
        this.title = title;
        this.info = info;
        this.toActivity = toActivity;
        intentExtraName = null;
        intentExtraValue = -1;
    }
    public MainMenuItem(int icon, String title, String info, Class toActivity, String intentExtraName, int intentExtraValue){
        this.icon = icon;
        this.title = title;
        this.info = info;
        this.toActivity = toActivity;
        this.intentExtraName = intentExtraName;
        this.intentExtraValue = intentExtraValue;
    }
    public int icon;
    public String title;
    public  String info;
    public Class toActivity;
    public String intentExtraName;
    public int intentExtraValue;
}