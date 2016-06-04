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

import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.model.LogChunkModel;
import com.dickthedeployer.dick.web.model.OutputModel;
import com.dickthedeployer.dick.web.service.JobBuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author mariusz
 */
@RestController
@RequestMapping("/api/job-builds")
public class JobBuildController {

    @Autowired
    JobBuildService jobBuildService;

    @RequestMapping(value = "/{id}/chunks", method = RequestMethod.GET)
    List<LogChunkModel> getLogChunks(@PathVariable Long id,
                                     @RequestParam(required = false, name = "creationDate")
                                     Long creationDate) throws NotFoundException {
        return jobBuildService.getLogChunks(id, creationDate != null ? new Date(creationDate) : null);
    }

    @RequestMapping(value = "/{id}/output", method = RequestMethod.GET)
    OutputModel getOutput(@PathVariable Long id) throws NotFoundException {
        return jobBuildService.getOutput(id);
    }


}
