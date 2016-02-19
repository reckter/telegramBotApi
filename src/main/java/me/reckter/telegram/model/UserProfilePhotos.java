package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hannes
 */
public class UserProfilePhotos extends BaseModel {


    @JsonProperty("total_count")
    int totalCount;

    List<PhotoSize> photos;


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<PhotoSize> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoSize> photos) {
        this.photos = photos;
    }
}
