package org.dbpedia.mappingschecker.web;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum UserRole {
    NO_ROLE(0), MAPPER(1), ANNOTATOR(2), ADMIN(100);

    private int id;

    UserRole(int id) {
        this.id = id;
    }

    public static UserRole fromString(String role) {
        if (role.equals("ADMIN")) {
            return UserRole.ADMIN;
        } else if (role.equals("ANNOTATOR")) {
            return UserRole.ANNOTATOR;
        } else if (role.equals("MAPPER")) {
            return UserRole.MAPPER;
        } else {
            return UserRole.NO_ROLE;
        }
    }

    public String toString() {
        return this.name();
    }
}
