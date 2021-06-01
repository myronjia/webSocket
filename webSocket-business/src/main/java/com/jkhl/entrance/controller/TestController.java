package com.jkhl.entrance.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RequestMapping("test")
@RestController
public class TestController {

    @RequestMapping("/testController")
    public String testController() {
        log.info("testController");
        return "Hello,testController!";
    }

}
