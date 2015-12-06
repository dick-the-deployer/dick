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
package com.dickthedeployer.dick.web.service;

import com.dickthedeployer.dick.web.dao.JobBuildDao;
import com.dickthedeployer.dick.web.dao.WorkerDao;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.Worker;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author mariusz
 */
@Slf4j
@Service
public class SchedulerService {

    @Autowired
    JobBuildDao jobBuildDao;

    @Autowired
    WorkerDao workerDao;

    @Autowired
    WorkerService workerService;

    @Scheduled(fixedRateString = "${dock.web.scheduler.assign:1000}")
    public void assignJobsToWorkers() {
        List<JobBuild> jobBuilds = jobBuildDao.findByStatusAndWorkerNull(com.dickthedeployer.dick.web.domain.JobBuild.Status.READY);
        List<Worker> availableWorkers = workerDao.findByStatus(Worker.Status.READY);
        if (!availableWorkers.isEmpty()) {
            log.info("Found available workers. Scheduling jobs");
            jobBuilds.forEach(workerService::assignJobBuildToWorker);
        }
    }
}
