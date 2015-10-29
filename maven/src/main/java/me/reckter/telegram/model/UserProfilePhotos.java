package me.reckter.telegram.model;

import me.reckter.json.JsonName;

/**
 * @author hannes
 */
public class UserProfilePhotos extends BaseModel {


    @JsonName("total_count")
    int totalCount;

    PhotoSize[] photos;

}
