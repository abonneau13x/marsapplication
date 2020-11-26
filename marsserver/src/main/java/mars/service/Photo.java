package mars.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Photo {
    @JsonProperty("earth_date")
    private String earthDate;
    @JsonProperty("img_src")
    private String imgSrc;

    @SuppressWarnings("unused")
    public Photo() {
    }

    public Photo(String earthDate, String imgSrc) {
        this.earthDate = earthDate;
        this.imgSrc = imgSrc;
    }

    public String getEarthDate() {
        return earthDate;
    }

    public String getImgSrc() {
        return imgSrc;
    }
}
