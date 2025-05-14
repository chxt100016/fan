package com.chxt;



import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestController {

   
    @GetMapping(value = "/helloworld")
    public String helloWorld(){
        return "Hello, welcome to COLA world!";
    }

    @GetMapping(value = "/helloworld2")
    public String helloWorld2(){
        return "Hello2, welcome to COLA world!";
    }

   
}
