package com.iitm.interiitapp.objects;

public class EWasteObject {
    public int imageId;
    public String text;
    public int qty;
    public double approxWeight;

    public EWasteObject(int imageId, String text, double approxWeight){
        this.imageId = imageId;
        this.text = text;
        this.qty = 0;
        this.approxWeight = approxWeight;
    }
}
