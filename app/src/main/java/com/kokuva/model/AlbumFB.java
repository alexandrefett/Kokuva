package com.kokuva.model;

/**
 * Created by Alexandre on 16/10/2016.
 */

public class AlbumFB{
    private String url;
    private String id;

    public AlbumFB(String id, String url){
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
