package mars.service;

import mars.core.MarsApplicationException;

import java.util.List;

public interface PhotoService {
    List<Photo> cachePhotos(String earthDate) throws MarsApplicationException;
    List<String> getCachedDates();
    boolean removeFromCache(String earthDate);
    boolean clearCache();
}
