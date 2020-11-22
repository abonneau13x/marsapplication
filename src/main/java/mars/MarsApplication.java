package mars;

import mars.controller.PhotoController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import mars.service.PhotoService;

@SpringBootApplication(scanBasePackageClasses = {PhotoController.class, MarsApplication.class, PhotoService.class})
public class MarsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarsApplication.class, args);
    }
}
