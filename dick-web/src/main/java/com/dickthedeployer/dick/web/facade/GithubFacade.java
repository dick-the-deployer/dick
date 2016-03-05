package com.dickthedeployer.dick.web.facade;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(url = "https://api.github.com/repos/dick-the-deployer/dick")
public interface GithubFacade {

    @RequestMapping(value = "/commits", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<GithubCommita> getCommits();

}
