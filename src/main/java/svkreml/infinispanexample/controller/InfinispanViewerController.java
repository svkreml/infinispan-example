package svkreml.infinispanexample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "infinispan.remote.test.enabled", havingValue = "true", matchIfMissing = true)
public class InfinispanViewerController {

    private final RemoteCacheManager sessionCache;
    private final ObjectMapper objectMapper;


    @RequestMapping("/is-print")
    public ResponseEntity<String> sendRequestToSPM(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
        StringBuilder stringBuilder = new StringBuilder();

        for (String cacheName : sessionCache.getCacheNames()) {
            stringBuilder.append(cacheName).append("\n");
            try {
                final RemoteCache<Object, Object> cache = sessionCache.getCache(cacheName);
                cache.forEach((key, value) -> {
                    try {
                        stringBuilder.append(key).append(": ").append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)).append("\n");
                    } catch (Exception e) {
                        stringBuilder.append(key).append(": ").append("---").append("\n");
                    }
                });
            } catch (Exception e) {
                stringBuilder.append("---").append("\n");
            }
            stringBuilder.append("*************************************************************************\n");
        }

        log.info(stringBuilder.toString());
        return ResponseEntity.ok(stringBuilder.toString());
    }
}
