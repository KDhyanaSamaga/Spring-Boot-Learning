package com.dhyana.javaslf4j.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BasicService {
    public String sayHello(){
        log.trace("TRACE: BasicService LOG");
        log.debug("DEBUG:  BasicService LOG");
        log.info("INFO:  BasicServicer LOG");
        log.warn("WARN:  BasicService LOG");
        log.error("ERROR:  BasicServicer LOG");
        return "Hello";
    }
}
