package com.example.unifriends.friendFinder;

import java.util.List;

public class Friend {
    private String firstName;
    private String lastName;
    private String major;
    private String gender;
    private List<String> interests;
    private List<String> subjects;

    public Friend(String f, String l, String m, String g, List<String> i, List<String> s) {
        this.firstName = f;
        this.lastName = l;
        this.major = m;
        this.gender = g;
        this.interests = i;
        this.subjects = s;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMajor() {
        return major;
    }

    public List<String> getInterests() {
        return interests;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public String getFirstName() {
        return firstName;
    }
}
