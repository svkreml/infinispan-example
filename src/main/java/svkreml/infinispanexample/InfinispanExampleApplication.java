package svkreml.infinispanexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class InfinispanExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfinispanExampleApplication.class, args);
    }

}
