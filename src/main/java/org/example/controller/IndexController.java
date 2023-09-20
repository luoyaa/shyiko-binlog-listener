package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luoyaa
 * @date 2023-09-15 10:05:24
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @GetMapping("/hello")
    public String hello() {
        return "helloooooooooo~~~";
    }
}
