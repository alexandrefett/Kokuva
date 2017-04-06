package com.kokuva.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Alexandre on 21/09/2016.
 */

public class Profile {
    private String userid;
    private String username;
    private String phone;
    private String email;
    private String aboutme;
    private long logintime;
    private String gender;
    private long age;
    private HashMap<String, Object> photos = new HashMap<String, Object>();
    private HashMap<String, Object> likes = new HashMap<String, Object>();
    private ArrayList<String> arrayPhotos = new ArrayList<String>();
    private int agemax;
    private int agemin;
    private boolean male;
    private boolean female;
    private boolean crush;
    private boolean msg;

    public static final String  USERID = "userid";
    public static final String  USERNAME = "username";
    public static final String  EMAIL = "email";
    public static final String  PHONE = "phone";
    public static final String  PHOTOS = "photos";
    public static final String  LIKES = "likes";
    public static final String  ABOUTME = "aboutme";
    public static final String  LOGINTIME = "logintime";
    public static final String  AGE = "age";
    public static final String  GENDER = "gender";
    public static final String  MALE = "male";
    public static final String  FEMALE = "female";
    public static final String  CRUSH = "crush";
    public static final String  MSG = "msg";
    public static final String  AGE_MIN = "age_min";
    public static final String  AGE_MAX = "age_max";

    public Profile(){
    }

    public Profile(Object O){
        HashMap<String, Object> o = (HashMap<String, Object>)O;
        this.userid  = (String)o.get(USERID);
        this.email  = (String)o.get(EMAIL);
        this.phone = (String)o.get(PHONE);
        this.username = (String)o.get(USERNAME);
        this.aboutme = (String)o.get(ABOUTME);
        this.logintime = (long)o.get(LOGINTIME);
        this.age = (long)o.get(AGE);
        this.gender = (String)o.get(GENDER);
        this.photos = (HashMap<String, Object>) o.get(PHOTOS);
        this.likes = (HashMap<String, Object>) o.get(LIKES);
        this.agemax = (int)o.get(AGE_MAX);
        this.agemin = (int)o.get(AGE_MIN);
        this.crush = (boolean)o.get(CRUSH);
        this.msg = (boolean)o.get(MSG);
        this.male = (boolean)o.get(MALE);
        this.female = (boolean)o.get(FEMALE);
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> o = new HashMap<>();
        o.put(USERID, this.userid);
        o.put(USERNAME, this.username);
        o.put(EMAIL, this.email);
        o.put(PHONE, this.phone);
        o.put(PHOTOS, this.photos);
        o.put(ABOUTME, this.aboutme);
        o.put(LIKES, this.likes);
        o.put(LOGINTIME, this.logintime);
        o.put(AGE, this.age);
        o.put(GENDER, this.gender);
        o.put(AGE_MAX, this.agemax);
        o.put(AGE_MIN, this.agemin);
        o.put(CRUSH, this.crush);
        o.put(MSG, this.msg);
        o.put(MALE, this.male);
        o.put(FEMALE, this.female);
        return o;
    }

    public ArrayList<String> getArrayPhotos() {
        return arrayPhotos;
    }

    public void setArrayPhotos(ArrayList<String> arrayPhotos) {
        this.arrayPhotos = arrayPhotos;
    }

    public HashMap<String, Object> getLikes() {
        if(likes==null)
            this.likes = new HashMap<String, Object>();
        return likes;
    }

    public void setLikes(HashMap<String, Object> likes) {
        this.likes = likes;

    }

    public HashMap<String, Object> getPhotos() {
        if(photos==null)
            this.photos = new HashMap<String, Object>();
        this.photos.clear();
        for (int i = 0;i<arrayPhotos.size();i++){
            this.photos.put("_"+i, arrayPhotos.get(i));
        }
        return photos;
    }

    public void setPhotos(HashMap<String, Object> photos) {
        if(photos.size()>0) {
            arrayPhotos.clear();
            Iterator<Object> u = photos.values().iterator();
            while (u.hasNext()) {
                arrayPhotos.add((String)u.next());
            }
        }
        this.photos = photos;
    }

    public long getLogintime() {
        return logintime;
    }

    public void setLogintime(long logintime) {
        this.logintime = logintime;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }
    public String getName() {
        return username.split(" ")[0];
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAge() {
        return getAge(age);
        //return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void addPhoto(String url) {
        this.arrayPhotos.add(arrayPhotos.size(), url);
    }

    public void deletePhoto(int position) {
        this.arrayPhotos.remove(position);
    }

    private int getAge(long date){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTimeInMillis(date);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        Integer ageInt = new Integer(age);
        return ageInt;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return this.female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public boolean isCrush() {
        return crush;
    }

    public void setCrush(boolean crush) {
        this.crush = crush;
    }

    public boolean isMsg() {
        return msg;
    }

    public void setMsg(boolean msg) {
        this.msg = msg;
    }
    public int getAgemax() {
        return agemax;
    }

    public void setAgemax(int agemax) {
        this.agemax = agemax;
    }

    public int getAgemin() {
        return agemin;
    }

    public void setAgemin(int agemin) {
        this.agemin = agemin;
    }

}
