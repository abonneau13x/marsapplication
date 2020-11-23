package mars.controller;

import mars.MarsApplication;
import mars.service.PhotoService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

@SpringBootTest(classes = MarsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MarsApplication.class)
public class PhotoControllerTest {
    private static final String PHOTO_CACHE = "photo_cache";
    private static final String DATE1 = "2016-07-13";
    private static final String DATE2= "2017-02-27";

    @Autowired
    private PhotoService photoService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    public void testCache() throws IOException, InterruptedException {
        photoService.clearCache();

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/cache?date=" + DATE1);
            HttpResponse rawResponse = client.execute(request);
            Assert.assertEquals("true", IOUtils.toString(rawResponse.getEntity().getContent(), Charset.defaultCharset()));
        }

        Thread.sleep(60000);

        File dateDirectory = new File(PHOTO_CACHE + "/" + DATE1);
        Assert.assertTrue(dateDirectory.exists());
        Assert.assertEquals(442, Objects.requireNonNull(dateDirectory.list()).length);
    }

    @Test
    public void testRemoveFromCache() throws IOException {
        photoService.clearCache();

        File cacheDirectory = new File(PHOTO_CACHE);
        //noinspection ResultOfMethodCallIgnored
        cacheDirectory.mkdir();

        File dateDirectory1 = new File(PHOTO_CACHE + "/" + DATE1);
        //noinspection ResultOfMethodCallIgnored
        dateDirectory1.mkdir();

        File dateDirectory2 = new File(PHOTO_CACHE + "/" + DATE2);
        //noinspection ResultOfMethodCallIgnored
        dateDirectory2.mkdir();

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/removeFromCache?date=" + DATE1);
            HttpResponse rawResponse = client.execute(request);
            Assert.assertEquals("true", IOUtils.toString(rawResponse.getEntity().getContent(), Charset.defaultCharset()));
        }

        Assert.assertTrue(cacheDirectory.exists());
        Assert.assertFalse(dateDirectory1.exists());
        Assert.assertTrue(dateDirectory2.exists());

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/removeFromCache?date=" + DATE1);
            HttpResponse rawResponse = client.execute(request);
            Assert.assertEquals("false", IOUtils.toString(rawResponse.getEntity().getContent(), Charset.defaultCharset()));
        }

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/removeFromCache?date=" + DATE2);
            HttpResponse rawResponse = client.execute(request);
            Assert.assertEquals("true", IOUtils.toString(rawResponse.getEntity().getContent(), Charset.defaultCharset()));
        }

        Assert.assertTrue(cacheDirectory.exists());
        Assert.assertFalse(dateDirectory1.exists());
        Assert.assertFalse(dateDirectory2.exists());

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/removeFromCache?date=" + DATE2);
            HttpResponse rawResponse = client.execute(request);
            Assert.assertEquals("false", IOUtils.toString(rawResponse.getEntity().getContent(), Charset.defaultCharset()));
        }
    }

    @Test
    public void testClearCache() throws IOException {
        photoService.clearCache();

        File cacheDirectory = new File(PHOTO_CACHE);
        //noinspection ResultOfMethodCallIgnored
        cacheDirectory.mkdir();

        //noinspection ResultOfMethodCallIgnored
        new File(PHOTO_CACHE + "/" + DATE1).mkdir();
        //noinspection ResultOfMethodCallIgnored
        new File(PHOTO_CACHE + "/" + DATE2).mkdir();

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/clearCache");
            HttpResponse rawResponse = client.execute(request);
            Assert.assertEquals("true", IOUtils.toString(rawResponse.getEntity().getContent(), Charset.defaultCharset()));
        }
        Assert.assertFalse(cacheDirectory.exists());
    }
}
