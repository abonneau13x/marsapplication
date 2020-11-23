package mars.service;

import mars.MarsApplication;
import mars.core.MarsApplicationException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MarsApplication.class)
public class PhotoServiceTest {
    private static final String PHOTO_CACHE = "photo_cache";
    private static final String DATE1 = "2016-07-13";
    private static final String DATE2= "2017-02-27";

    @Autowired
    private PhotoService photoService;

    @Test
    public void testProcessPhotos() throws MarsApplicationException {
        FileUtils.deleteQuietly(new File(PHOTO_CACHE));
        photoService.cachePhotos(DATE1);

        File dateDirectory = new File(PHOTO_CACHE + "/" + DATE1);
        Assert.assertTrue(dateDirectory.exists());
        Assert.assertEquals(442, Objects.requireNonNull(dateDirectory.list()).length);
    }

    @Test
    public void testRemoveFromCache() {
        FileUtils.deleteQuietly(new File(PHOTO_CACHE));

        File cacheDirectory = new File(PHOTO_CACHE);
        //noinspection ResultOfMethodCallIgnored
        cacheDirectory.mkdir();

        File dateDirectory1 = new File(PHOTO_CACHE + "/" + DATE1);
        //noinspection ResultOfMethodCallIgnored
        dateDirectory1.mkdir();

        File dateDirectory2 = new File(PHOTO_CACHE + "/" + DATE2);
        //noinspection ResultOfMethodCallIgnored
        dateDirectory2.mkdir();

        Assert.assertTrue(photoService.removeFromCache(DATE1));
        Assert.assertTrue(cacheDirectory.exists());
        Assert.assertFalse(dateDirectory1.exists());
        Assert.assertTrue(dateDirectory2.exists());
        Assert.assertFalse(photoService.removeFromCache(DATE1));

        Assert.assertTrue(photoService.removeFromCache(DATE2));
        Assert.assertTrue(cacheDirectory.exists());
        Assert.assertFalse(dateDirectory1.exists());
        Assert.assertFalse(dateDirectory2.exists());
        Assert.assertFalse(photoService.removeFromCache(DATE1));


    }

    @Test
    public void testClearCache() {
        FileUtils.deleteQuietly(new File(PHOTO_CACHE));

        File cacheDirectory = new File(PHOTO_CACHE);
        //noinspection ResultOfMethodCallIgnored
        cacheDirectory.mkdir();

        //noinspection ResultOfMethodCallIgnored
        new File(PHOTO_CACHE + "/" + DATE1).mkdir();
        //noinspection ResultOfMethodCallIgnored
        new File(PHOTO_CACHE + "/" + DATE2).mkdir();

        Assert.assertTrue(photoService.clearCache());
        Assert.assertFalse(cacheDirectory.exists());
    }
}
