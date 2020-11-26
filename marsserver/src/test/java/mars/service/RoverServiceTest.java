package mars.service;

import mars.MarsApplication;
import mars.core.MarsApplicationException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MarsApplication.class)
public class RoverServiceTest {
    @Mock
    private MarsApiService marsApiService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void requestRoverNames() throws MarsApplicationException {
        RoverService roverService = new RoverServiceImpl(marsApiService);

        List<Rover> rovers = Arrays.asList(
                new Rover("Curiosity"),
                new Rover("Spirit"),
                new Rover("Opportunity")
        );
        Mockito.when(marsApiService.requestRovers()).thenReturn(rovers);

        Assert.assertEquals(Arrays.asList("Curiosity", "Spirit", "Opportunity"), roverService.requestRoverNames());

        Mockito.verify(marsApiService, Mockito.times(1)).requestRovers();
    }
}
