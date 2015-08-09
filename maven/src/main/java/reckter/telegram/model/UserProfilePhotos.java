package reckter.telegram.model;

import reckter.json.JsonName;

/**
 * @author hannes
 */
public class UserProfilePhotos extends BaseModel{


    @JsonName("total_count")
    int totalCount;

    PhotoSize[] photos;

}
