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
import com.dickthedeployer.dick.web.exception.WorkerBusyException;
import com.dickthedeployer.dick.web.model.WorkerModel;
import com.dickthedeployer.dick.web.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author mariusz
 */
@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    WorkerService workerService;

    @RequestMapping(method = RequestMethod.GET)
    public List<WorkerModel> getWorkers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return workerService.getWorkers(page, size);
    }

    @RequestMapping(method = DELETE, value = "/{workerName}")
    public void deleteWorker(@PathVariable("workerName") String workerName) throws NotFoundException, WorkerBusyException {
        workerService.deleteWorker(workerName);
    }

    @RequestMapping(method = GET, value = "/{workerName}")
    public WorkerModel getWorker(@PathVariable("workerName") String workerName) throws NotFoundException {
        return workerService.getWorker(workerName);
    }
}
