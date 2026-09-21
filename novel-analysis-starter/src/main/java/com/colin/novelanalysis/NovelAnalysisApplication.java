package com.colin.novelanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = "com.colin.novelanalysis")
public class NovelAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelAnalysisApplication.class, args);
    }
}
