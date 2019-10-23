package com.example.unifriends.groups;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    public String id;
    public String degree;
    public String email;
    public String facialID;
    public List<Long> interets;
    public String major;
    public String name;
    public String password;
    public String photo;
    public List<String> subjects;
    public String uni;
    public List<String> groups;

    public User() {

    }

    public User(String degree, String email, String facialID, List<Long> interets, String major, String name, String password, String photo, List<String> subjects, String uni, List<String> groups) {
        this.degree= degree;
        this.email= email;
        this.facialID=facialID;
        this.interets = interets;
        this.major= major;
        this.name=name;
        this.password=password;
        this.photo=photo;
        this.subjects=subjects;
        this.uni=uni;
        this.groups=groups;
    }

    public void setId(String documentId) {
        this.id = documentId;
    }

    public List<String> getSubjects() {
        return subjects;
    }
}
