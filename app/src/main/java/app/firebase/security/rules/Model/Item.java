package app.firebase.security.rules.Model;

import com.google.firebase.Timestamp;

public class Item {

    private String key;
    private String iName;
    private Timestamp created_at;
    private boolean checkFavorite;

    public Item() {
    }

    public Item(String key, String iName, Timestamp created_at, boolean checkFavorite) {
        this.key = key;
        this.iName = iName;
        this.created_at = created_at;
        this.checkFavorite = checkFavorite;
    }

    public String getiName() {
        return iName;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public boolean isCheckFavorite() {
        return checkFavorite;
    }

    public void setCheckFavorite(boolean checkFavorite) {
        this.checkFavorite = checkFavorite;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
