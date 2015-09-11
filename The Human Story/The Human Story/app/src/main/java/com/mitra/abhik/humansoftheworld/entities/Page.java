package com.mitra.abhik.humansoftheworld.entities;

/**
 * Created by abmitra on 7/24/2015.
 */
public class Page {
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private Long id;
    private String title;

    public Page(String name, Long id, String title) {
        this.name = name;
        this.id = id;
        this.title = title;
    }
}
