package com.dickthedeployer.dick.web.controller;

import com.dickthedeployer.dick.web.model.BuildForm;
import com.dickthedeployer.dick.web.model.BuildOrder;
import com.dickthedeployer.dick.web.model.BuildStatus;
import com.dickthedeployer.dick.web.model.RegistrationData;
import com.dickthedeployer.dick.web.service.JobBuildService;
import com.dickthedeployer.dick.web.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api/internal")
public class InternalWorkerController {

    @Autowired
    WorkerService workerService;

    @Autowired
    JobBuildService jobBuildService;

    @RolesAllowed("ROLE_WORKER")
    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    RegistrationData register() {
        String workerName = workerService.registerWorker();
        return new RegistrationData(workerName);
    }

    @RolesAllowed("ROLE_WORKER")
    @RequestMapping(value = "/job/{id}/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    BuildStatus checkStatus(@PathVariable("id") Long id) {
        boolean isStopped = jobBuildService.isStopped(id);
        return new BuildStatus(isStopped);
    }

    @RolesAllowed("ROLE_WORKER")
    @RequestMapping(value = "/job/{id}/failure", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reportFailure(@PathVariable Long id, @RequestBody BuildForm form) {
        jobBuildService.reportFailure(id, form.getLog());
    }

    @RolesAllowed("ROLE_WORKER")
    @RequestMapping(value = "/job/{id}/success", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reportSuccess(@PathVariable Long id, @RequestBody BuildForm form) {
        jobBuildService.reportSuccess(id, form.getLog());
    }

    @RolesAllowed("ROLE_WORKER")
    @RequestMapping(value = "/job/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reportProgress(@PathVariable Long id, @RequestBody BuildForm form) {
        jobBuildService.reportProgress(id, form.getLog());
    }

    @RolesAllowed("ROLE_WORKER")
    @RequestMapping(value = "/job/peek/{workerName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    BuildOrder peekBuild(@PathVariable("workerName") String workerName) {
        return jobBuildService.peekBuildFor(workerName).orElse(null);
    }
}
