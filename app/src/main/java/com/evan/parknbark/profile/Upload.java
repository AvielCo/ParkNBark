package com.evan.parknbark.profile;
//this class is for photos.
public class Upload {
    private String mName ,mImageURL;

    public Upload(){}

    public Upload(String name ,String imageURL){
        if(name.trim().equals("")){
            name = "UNKNOWN";
        }
        name = mName;
        imageURL = mImageURL;
    }

    public void setName(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public void setImageURL(String imageURL){
        mImageURL = imageURL;
    }

    public String getImageURL(){
        return mImageURL;
    }
}
