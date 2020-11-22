package mars.service;

import mars.MarsApplication;
import mars.core.MarsApplicationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MarsApplication.class)
public class RoverServiceTest {
    @Autowired
    private RoverService roverService;

    @Test
    public void requestRoverNames() throws MarsApplicationException {
        Assert.assertEquals(Arrays.asList("Curiosity", "Spirit", "Opportunity"), roverService.requestRoverNames());
    }
}
