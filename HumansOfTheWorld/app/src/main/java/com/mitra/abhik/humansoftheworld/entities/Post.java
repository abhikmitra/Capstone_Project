package com.mitra.abhik.humansoftheworld.entities;

import android.database.Cursor;

import com.mitra.abhik.humansoftheworld.Constants;

import java.net.URL;

/**
 * Created by abmitra on 7/21/2015.
 */
public class Post {
    private long _ID;
    private String id;
    private long object_id;
    private String message;
    private String full_picture;
private Boolean is_favorite;
    private long page_id;
    private String page_title;
    private String created_time;
    private Boolean isDeleted;

    public void setIs_favorite(Boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public void setPage_title(String page_title) {
        this.page_title = page_title;
    }

    public void setPage_id(long page_id) {
        this.page_id = page_id;
    }

    public String getPage_title() {
        return page_title;
    }

    public String getCreated_time() {
        return created_time;
    }

    public long getPage_id() {
        return page_id;
    }

    public Boolean getIs_favorite() {
        return is_favorite;
    }
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }


    public Post(String id,
                long object_id,
                String message,
                String full_picture,
                String page_title,
                int is_favorite,
                long page_id,
                String created_time,
                int isDeleted
    ){
        this.id = id;
        this.object_id = object_id;
        this.message = message;
        this.full_picture = full_picture;
        this.page_title = page_title;
        this.is_favorite = is_favorite==1?true:false;
        this.created_time = created_time;
        this.page_id = page_id;
        this.isDeleted = isDeleted==1?true:false;

    }
    public Post(Cursor c){
        this(c.getString(Constants.COL_POST_FB_ID),
                c.getLong(Constants.COL_POST_OBJECT_ID),
                c.getString(Constants.COL_POST_MESSAGE),
                c.getString(Constants.COL_POST_PICTURE),
                c.getString(Constants.COL_POST_TITLE),
                c.getInt(Constants.COL_POST_FAVORITE),
                c.getLong(Constants.COL_POST_PAGE_ID),
                c.getString(Constants.COL_CREATED_DATE),
                c.getInt(Constants.COL_DELETED)
        );
        this._ID = c.getLong(Constants.COL_POST_ID);
    }
    public long get_ID() {
        return _ID;
    }

    public String getId() {
        return id;
    }

    public long getObject_id() {
        return object_id;
    }

    public String getMessage() {
        return message;
    }

    public String getFull_pictureUrlString() {
        return full_picture;

    }
    public URL getFull_picture() {
        try{
            URL url = new URL(full_picture);
            return url;
        } catch (Exception e){
            return null;
        }

    }
}
