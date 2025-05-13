package ru.job4j.dreamjob.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class CounterController {

    private final AtomicInteger total = new AtomicInteger(0);

    @GetMapping("/count")
    public String count() {
        int value = total.incrementAndGet();
        return String.format("Total execute : %d", value);
    }
}