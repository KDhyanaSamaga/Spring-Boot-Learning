package com.dhyana.javaslf4j.controller;

import com.dhyana.javaslf4j.service.BasicService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BasicController {
   // private static final Logger logger = LoggerFactory.getLogger(BasicController.class);

    private static BasicService basicService = new BasicService(); //instince of bascicservice

    public BasicController(BasicService basicService) {
        this.basicService = basicService;
    } //constructor injuction

    @GetMapping("/hello")
    public String sayHello(){
        String userName = "Dhyana";
        log.info("Recived request for /hello :{}",userName);
        log.trace("TRACE:  BasicController LOG");
        log.debug("DEBUG:  BasicController LOG");
        log.info("INFO:  BasicController LOG");
        log.warn("WARN:  BasicController LOG");
        log.error("ERROR:  BasicController LOG");
        return basicService.sayHello();
    }
}
