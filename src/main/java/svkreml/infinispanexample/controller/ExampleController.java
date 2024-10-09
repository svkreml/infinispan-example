package svkreml.infinispanexample.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import svkreml.infinispanexample.dto.ExampleDto;
import svkreml.infinispanexample.service.ExampleService;

@RestController
@RequiredArgsConstructor
public class ExampleController {


    final private ExampleService exampleService;

    @GetMapping("/test1")
    public ExampleDto test1() {
        return exampleService.test1(1);
    }
    @GetMapping("/test2")
    public ExampleDto test2() {
        return exampleService.test2(1);
    }
}
