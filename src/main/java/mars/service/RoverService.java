package mars.service;

import mars.core.MarsApplicationException;

import java.util.List;

public interface RoverService {
    List<String> requestRoverNames() throws MarsApplicationException;
}
