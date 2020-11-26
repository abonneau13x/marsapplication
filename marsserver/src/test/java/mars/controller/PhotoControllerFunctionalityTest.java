package mars.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mars.MarsApplication;
import mars.service.Photo;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MarsApplication.class)
public class PhotoControllerFunctionalityTest {
    private static final String DATE = "2018-06-02";
    private static final String IMG_SRC = "1P581112616ESFD2FCP2119L2M1-BR.JPG";

    @Autowired
    private ObjectMapper objectMapper;

    @ClassRule
    @SuppressWarnings("rawtypes")
    public static DockerComposeContainer compose = new DockerComposeContainer(
            new File("../docker-compose.yml")
    ).withExposedService("mars-server", 8080);

    @Test
    public void testCacheAndDownload() throws IOException {
        clearCache();

        Assert.assertEquals(32, cachePhotos().size());

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/download?date=" + DATE + "&fileName=" + IMG_SRC);
            HttpResponse response = client.execute(request);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            byte[] photoData = IOUtils.toByteArray(response.getEntity().getContent());
            Assert.assertEquals(18510, photoData.length);
        }
    }

    @Test
    public void testGetCachedDates() throws IOException {
        clearCache();
        cachePhotos();

        Assert.assertEquals(Collections.singletonList(DATE), getCachedDates());
    }

    @Test
    public void testRemoveFromCache() throws IOException {
        clearCache();
        cachePhotos();

        Assert.assertEquals(Collections.singletonList(DATE), getCachedDates());

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/removeFromCache?date=" + DATE);
            HttpResponse response = client.execute(request);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }

        Assert.assertTrue(getCachedDates().isEmpty());
    }

    @Test
    public void testClearCache() throws IOException {
        clearCache();
        cachePhotos();

        Assert.assertEquals(Collections.singletonList(DATE), getCachedDates());

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/clearCache");
            HttpResponse response = client.execute(request);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }

        Assert.assertTrue(getCachedDates().isEmpty());
    }

    private void clearCache() throws IOException {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/clearCache");
            HttpResponse response = client.execute(request);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    private List<Photo> cachePhotos() throws IOException {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/cache?date=" + DATE);
            HttpResponse response = client.execute(request);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            //noinspection Convert2Diamond
            return objectMapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<List<Photo>>() {}
            );
        }
    }

    private List<String> getCachedDates() throws IOException {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/api/v1/cachedDates");
            HttpResponse response = client.execute(request);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            //noinspection Convert2Diamond
            return objectMapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<List<String>>() {}
            );
        }
    }
}
