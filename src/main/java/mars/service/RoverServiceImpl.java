package mars.service;

import mars.core.MarsApplicationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoverServiceImpl implements RoverService {
    private final MarsApiService marsApiService;
    public RoverServiceImpl(MarsApiService marsApiService) {
        this.marsApiService = marsApiService;
    }

    @Override
    public List<String> requestRoverNames() throws MarsApplicationException {
        List<Rover> rovers = marsApiService.requestRovers();
        List<String> result = new ArrayList<>(rovers.size());
        rovers.forEach(rover -> result.add(rover.getName()));
        return result;
    }
}
