package edu.orangecoastcollege.cs273.kdo94.cs273superheroes;

/**
 * Created by kevin_000 on 10/10/2016.
 */

public class Superheroes {
    private String mUsername;
    private String mName;
    private String mSuperpower;
    private String mOneThing;

    public Superheroes(){
        mUsername = "";
        mName = "";
        mOneThing = "";
        mSuperpower = "";
    }

    public Superheroes(String username, String name, String superpower, String oneThing) {
        mUsername = username;
        mName = name;
        mSuperpower = superpower;
        mOneThing = oneThing;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSuperpower() {
        return mSuperpower;
    }

    public void setSuperpower(String superpower) {
        mSuperpower = superpower;
    }

    public String getOneThing() {
        return mOneThing;
    }

    public void setOneThing(String oneThing) {
        mOneThing = oneThing;
    }
}
