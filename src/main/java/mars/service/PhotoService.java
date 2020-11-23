package mars.service;

import mars.core.MarsApplicationException;

import java.util.List;

public interface PhotoService {
    List<Photo> requestPhotos(List<String> roverNames, String earthDate) throws MarsApplicationException;
    List<Photo> cachePhotos(String earthDate) throws MarsApplicationException;
    boolean removeFromCache(String earthDate);
    boolean clearCache();
}
