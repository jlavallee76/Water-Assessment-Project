package com.example.wap;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageLinkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addImageLink(ImageLink imageLink);

    @Query("select * from image_links")
    public List<ImageLink> getImageLinks();

    @Query("DELETE FROM image_links")
    public void nukeDatabase();

    @Delete
    public void deleteImageLink(ImageLink imageLink);

    @Update
    public void updateImageLink(ImageLink imageLink);

}