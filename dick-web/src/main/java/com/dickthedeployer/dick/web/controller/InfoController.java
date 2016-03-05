package com.dickthedeployer.dick.web.controller;

import com.dickthedeployer.dick.web.model.InfoModel;
import com.dickthedeployer.dick.web.service.InfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/info")
public class InfoController {

    @Autowired
    InfoService infoService;

    @RequestMapping(method = RequestMethod.GET)
    public InfoModel getInfo() {
        return infoService.getInfo();
    }

}
