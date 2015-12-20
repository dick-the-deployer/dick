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

import com.dickthedeployer.dick.web.model.RegistrationData;
import com.dickthedeployer.dick.web.model.WorkerModel;
import com.dickthedeployer.dick.web.service.WorkerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mariusz
 */
@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    WorkerService workerService;

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    RegistrationData register() {
        String workerName = workerService.registerWorker();
        return new RegistrationData(workerName);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<WorkerModel> getWorkers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return workerService.getWorkers(page, size);
    }
}
