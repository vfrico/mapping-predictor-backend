package org.dbpedia.mappingschecker.web;

import java.sql.Timestamp;

public class UserDAO {
    int id;
    String username;
    String email;
    String password_md5;
    Timestamp creation_date;
    String jwt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_md5() {
        return password_md5;
    }

    public void setPassword_md5(String password_md5) {
        this.password_md5 = password_md5;
    }

    public Timestamp getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Timestamp creation_date) {
        this.creation_date = creation_date;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
