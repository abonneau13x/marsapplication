package mars.service;

public class CachedPhoto {
    private String date;
    private String fileName;

    @SuppressWarnings("unused")
    public CachedPhoto() {
    }

    public CachedPhoto(String date, String fileName) {
        this.date = date;
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public String getFileName() {
        return fileName;
    }
}
