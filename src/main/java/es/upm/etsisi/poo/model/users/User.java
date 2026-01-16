package es.upm.etsisi.poo.model.users;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected String name;
    protected String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String getIdentifier();

    @Override
    public abstract String toString();
}
