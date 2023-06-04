package model;

import java.util.ArrayList;

public class Account {

    private String username;
    private String password;
    private String displayName;
    private Group gro;
    private ArrayList<Feature> features = new ArrayList<>();

    public Account() {
    }

    public Account(String username, String password, String displayName, Group gro) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.gro = gro;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Group getGro() {
        return gro;
    }

    public void setGro(Group gro) {
        this.gro = gro;
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Feature> features) {
        this.features = features;
    }

    @Override
    public String toString() {
        return "username=" + username + " password=" + password + " displayName=" + displayName + " gro=" + gro + "\n";
    }

}
