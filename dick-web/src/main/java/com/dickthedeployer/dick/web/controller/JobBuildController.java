/*
 * Copyright dick the deployer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dickthedeployer.dick.web.controller;

import com.dickthedeployer.dick.web.model.BuildForm;
import com.dickthedeployer.dick.web.model.BuildOrder;
import com.dickthedeployer.dick.web.model.BuildStatus;
import com.dickthedeployer.dick.web.service.JobBuildService;
import com.dickthedeployer.dick.web.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mariusz
 */
@RestController
@RequestMapping("/job-builds")
public class JobBuildController {

    @Autowired
    JobBuildService jobBuildService;

    @Autowired
    WorkerService workerService;

    @RequestMapping(value = "/peek/{workerName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    BuildOrder peekBuild(@PathVariable("workerName") String workerName) {
        workerService.onHeartbeat(workerName);
        return jobBuildService.peekBuildFor(workerName);
    }

    @RequestMapping(value = "/{id}/kill", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void stopJobBuild(@PathVariable Long id) {
        jobBuildService.stop(id);
    }

    @RequestMapping(value = "/{id}/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    BuildStatus checkStatus(@PathVariable("id") Long id) {
        boolean isStopped = jobBuildService.isStopped(id);
        return new BuildStatus(isStopped);
    }

    @RequestMapping(value = "/{id}/failure", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reportFailure(@PathVariable Long id, @RequestBody BuildForm form) {
        jobBuildService.reportFailure(id, form.getLog());
    }

    @RequestMapping(value = "/{id}/success", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reportSuccess(@PathVariable Long id, @RequestBody BuildForm form) {
        jobBuildService.reportSuccess(id, form.getLog());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reportProgress(@PathVariable Long id, @RequestBody BuildForm form) {
        jobBuildService.reportProgress(id, form.getLog());
    }

}