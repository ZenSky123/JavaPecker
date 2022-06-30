package com.github.realsky.aweb;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class HelloController {
    @RequestMapping("/")
    public String hello() {
        try {
            int millis = new Random().nextInt(0, 1000);
            System.out.printf("Get:%d\n", millis);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Hello World!";
    }
}
