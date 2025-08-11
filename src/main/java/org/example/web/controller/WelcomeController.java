package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/Welcome")
@RequiredArgsConstructor
public class WelcomeController {

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String Welcome(@RequestParam(value = "name", required = false) String name){
        String greeting = "Hellow ";

        if(name != null || !name.isBlank()){
            greeting = greeting + name;
        }else{
            greeting = greeting + "World";
        }

        return "<h1>"+greeting+"<h1>";
    }
}
