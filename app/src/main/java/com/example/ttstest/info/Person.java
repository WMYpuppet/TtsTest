package com.example.ttstest.info;

/**
 * 作者：Created by Administrator on 2020/11/24.
 * 邮箱：
 */
public class Person {
    private String Titie;
    private String Matter;
    private String time;
    private String bundleid;

    public Person(String titie, String matter, String time, String bundleid) {
        Titie = titie;
        Matter = matter;
        this.time = time;
        this.bundleid = bundleid;
    }

    public String getTitie() {
        return Titie;
    }

    public void setTitie(String titie) {
        Titie = titie;
    }

    public String getMatter() {
        return Matter;
    }

    public void setMatter(String matter) {
        Matter = matter;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBundleid() {
        return bundleid;
    }

    public void setBundleid(String bundleid) {
        this.bundleid = bundleid;
    }

    @Override
    public String toString() {
        return "Person{" +
                "Titie='" + Titie + '\'' +
                ", Matter='" + Matter + '\'' +
                ", time='" + time + '\'' +
                ", bundleid='" + bundleid + '\'' +
                '}';
    }
}
