package com.lanouette.app.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.lanouette.app.shared.ServerPaths;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/bffConn")
public class BffController {
    public static final String JSON_TYPE = "application/json";

    private List<String> testStrings;

    //private final RequestMappingHandlerMapping handlerMapping;

    //@Autowired
    //public BffController(RequestMappingHandlerMapping handlerMapping) {
    //    this.handlerMapping = handlerMapping;
    //}

    @RequestMapping(method = RequestMethod.GET)
    public Integer get() {
        return 2233;
    }

    @RequestMapping(value = "/get_motd/{userId}/{sessionId}/{rndValue}",
                    method = GET, produces = JSON_TYPE)
    public ResponseEntity<List<String>> getMotd(@PathVariable("userId") Integer userId,
                                                @PathVariable("sessionId") Integer sessionId,
                                                @PathVariable("rndValue") Integer rndValue) {
        return new ResponseEntity<List<String>>(testStrings, HttpStatus.OK);
    }

    @RequestMapping(value="/endpointdoc", method= RequestMethod.GET)
    public void show(Model model) {
      //  model.addAttribute("handlerMethods", this.handlerMapping.getHandlerMethods());
    }

    @PostConstruct
    private void init() {
        testStrings = new ArrayList<String>(10);
        for (int i =0; i < 10; i++) {
            String str = "TestString" + i;
            testStrings.add(str);
        }

        return;
    }
}
