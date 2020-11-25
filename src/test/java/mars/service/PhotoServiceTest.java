package mars.service;

import mars.MarsApplication;
import mars.core.MarsApplicationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MarsApplication.class)
public class PhotoServiceTest {
    private static final String PHOTO_CACHE = "photo_cache";
    private static final String ROVER1 = "Rover1";
    private static final String ROVER2 = "Rover2";
    private static final String DATE1 = "2018-06-02";
    private static final String DATE2= "2017-02-27";
    private static final String PREFIX = "prefix/";
    private static final String IMG_SRC1 = "img_src1.png";
    private static final String IMG_SRC2 = "img_src2.png";

    @Mock
    private RoverService roverService;

    @Mock
    private MarsApiService marsApiService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testCachePhotos() throws MarsApplicationException, IOException {
        PhotoService photoService = new PhotoServiceImpl(roverService, marsApiService);

        FileUtils.deleteQuietly(new File(PHOTO_CACHE));

        cachePhotos(photoService);
    }

    @Test
    public void testGetCachedDates() throws MarsApplicationException, IOException {
        PhotoService photoService = new PhotoServiceImpl(roverService, marsApiService);

        photoService.clearCache();
        cachePhotos(photoService);

        Assert.assertEquals(Collections.singletonList(DATE1), photoService.getCachedDates());
    }

    @Test
    public void testRemoveFromCache() throws MarsApplicationException {
        PhotoService photoService = new PhotoServiceImpl(null, null);

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

        photoService.removeFromCache(DATE1);
        Assert.assertTrue(cacheDirectory.exists());
        Assert.assertFalse(dateDirectory1.exists());
        Assert.assertTrue(dateDirectory2.exists());

        // Should not throw exception since the directory does not exist.
        photoService.removeFromCache(DATE1);

        photoService.removeFromCache(DATE2);
        Assert.assertTrue(cacheDirectory.exists());
        Assert.assertFalse(dateDirectory1.exists());
        Assert.assertFalse(dateDirectory2.exists());

        // Should not throw exception since the directory does not exist.
        photoService.removeFromCache(DATE1);
    }

    @Test
    public void testClearCache() throws MarsApplicationException {
        PhotoService photoService = new PhotoServiceImpl(null, null);

        FileUtils.deleteQuietly(new File(PHOTO_CACHE));

        File cacheDirectory = new File(PHOTO_CACHE);
        //noinspection ResultOfMethodCallIgnored
        cacheDirectory.mkdir();

        //noinspection ResultOfMethodCallIgnored
        new File(PHOTO_CACHE + "/" + DATE1).mkdir();
        //noinspection ResultOfMethodCallIgnored
        new File(PHOTO_CACHE + "/" + DATE2).mkdir();

        photoService.clearCache();
        String[] cachedDates = cacheDirectory.list();
        Assert.assertNotNull(cachedDates);
        Assert.assertEquals(0, cachedDates.length);
    }

    private void cachePhotos(PhotoService photoService) throws IOException, MarsApplicationException {
        List<String> roverNames = Arrays.asList(ROVER1, ROVER2);
        Mockito.when(roverService.requestRoverNames()).thenReturn(roverNames);

        List<Photo> photos = Arrays.asList(
                new Photo(DATE1, PREFIX + IMG_SRC1),
                new Photo(DATE1, PREFIX + IMG_SRC2)
        );
        Mockito.when(marsApiService.requestPhotos(roverNames, DATE1)).thenReturn(photos);

        Mockito.doAnswer(invocation -> {
            File imgSrc = invocation.getArgument(1);

            String textToWrite = null;
            switch(imgSrc.getName()) {
                case IMG_SRC1:
                    textToWrite = "1";
                    break;
                case IMG_SRC2:
                    textToWrite = "2";
                    break;
                default:
                    Assert.fail("Unexpected imgSrc [" + imgSrc.getName() + "].");
            }

            try(FileOutputStream outputStream = new FileOutputStream(imgSrc)) {
                IOUtils.write(textToWrite, outputStream, Charset.defaultCharset());
            }
            return null;
        }).when(marsApiService).downloadPhoto(Mockito.anyString(), Mockito.notNull());

        photoService.cachePhotos(DATE1);

        Mockito.verify(roverService, Mockito.times(1)).requestRoverNames();
        Mockito.verify(marsApiService, Mockito.times(1)).requestPhotos(roverNames, DATE1);
        Mockito.verify(marsApiService, Mockito.times(1)).downloadPhoto(Mockito.eq(PREFIX + IMG_SRC1), Mockito.notNull());
        Mockito.verify(marsApiService, Mockito.times(1)).downloadPhoto(Mockito.eq(PREFIX + IMG_SRC2), Mockito.notNull());

        File dateDirectory = new File(PHOTO_CACHE + "/" + DATE1);
        Assert.assertTrue(dateDirectory.exists());
        Assert.assertEquals(2, Objects.requireNonNull(dateDirectory.list()).length);

        File img1 = new File(PHOTO_CACHE + "/" + DATE1 + "/" + IMG_SRC1);
        Assert.assertEquals("1", IOUtils.toString(img1.toURI(), Charset.defaultCharset()));

        File img2 = new File(PHOTO_CACHE + "/" + DATE1 + "/" + IMG_SRC2);
        Assert.assertEquals("2", IOUtils.toString(img2.toURI(), Charset.defaultCharset()));
    }
}
