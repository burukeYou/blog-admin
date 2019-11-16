package com.myblog;

import org.elasticsearch.common.settings.Settings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Myblog2Application {

    public static void main(String[] args) {


        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(Myblog2Application.class, args);


    }

}
