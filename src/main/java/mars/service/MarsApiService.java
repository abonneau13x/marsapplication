package mars.service;

import mars.core.MarsApplicationException;

import java.io.File;
import java.util.List;

public interface MarsApiService {
    List<Rover> requestRovers() throws MarsApplicationException;

    List<Photo> requestPhotos(List<String> roverNames, String earthDate) throws MarsApplicationException;

    void downloadPhoto(String imgSrc, File photoFile);
}
