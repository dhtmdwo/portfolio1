package com.example.be12fin5verdosewmthisbe.common;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyTask {

    // 5초마다 실행
    //@Scheduled(fixedRate = 5000)
    public void runTask() {
        System.out.println("Task executed at: " + System.currentTimeMillis());
    }
}
