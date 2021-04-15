package com.example.wap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_links")
public class ImageLink {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "link")
    public String link;

    public ImageLink(String link){
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}