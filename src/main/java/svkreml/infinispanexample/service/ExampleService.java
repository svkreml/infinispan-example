package svkreml.infinispanexample.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import svkreml.infinispanexample.dto.ExampleDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService {

    private final SessionCacheService sessionCacheService;


    @Cacheable("test1")
    public ExampleDto test1(Integer i) {
        log.info("test1 created value");
        return new ExampleDto(UUID.randomUUID().toString());
    }

    public ExampleDto test2(Integer i) {
        final ExampleDto exampleDto = sessionCacheService.getExample(i);
        if (exampleDto == null) {
            log.info("test2 created value");
            sessionCacheService.putExample(i, new ExampleDto(UUID.randomUUID().toString()));
        } else {
            log.info("Get from cache");
        }
        return exampleDto;
    }
}
