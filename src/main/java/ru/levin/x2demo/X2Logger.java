package ru.levin.x2demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X2Logger {
    public static final Logger LOGGER = LoggerFactory.getLogger("x2demo");
    
    public static void log(String msg) {
        LOGGER.info(msg);
    }
}