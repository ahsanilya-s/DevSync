package com.devsync.fypv1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Fypv1Application {

    public static void main(String[] args) {
        FilePathReader.main(args);
        SpringApplication.run(Fypv1Application.class, args);
    }

}
