package mars.service;

public class Photo {
    private final String earthDate;
    private final String imgSrc;

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
