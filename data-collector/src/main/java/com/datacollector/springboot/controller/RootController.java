package com.datacollector.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/index")
public class RootController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView homeGet() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        mv.getModel().put("data", "Welcome home man");
        return mv;
    }

}
