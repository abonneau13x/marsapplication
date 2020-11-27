package mars.service;

import mars.core.MarsApplicationException;

import java.util.List;

public interface PhotoService {
    List<CachedPhoto> cachePhotos(String earthDate) throws MarsApplicationException;
    List<String> getCachedDates();
    void removeFromCache(String earthDate) throws MarsApplicationException;
    void clearCache() throws MarsApplicationException;
}
