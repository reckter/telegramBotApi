package me.reckter.telegram.model;

/**
 * @author hannes
 */
public class Location extends BaseModel {


    float longitude;

    float latitude;

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
