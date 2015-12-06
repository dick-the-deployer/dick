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

import com.dickthedeployer.dick.web.dao.BuildDao;
import com.dickthedeployer.dick.web.dao.JobBuildDao;
import com.dickthedeployer.dick.web.dao.WorkerDao;
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.Worker;
import com.dickthedeployer.dick.web.model.BuildOrder;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mariusz
 */
@Slf4j
@Service
public class JobBuildService {

    @Autowired
    CommandService commandService;

    @Autowired
    WorkerService workerService;

    @Autowired
    JobBuildDao jobBuildDao;

    @Autowired
    WorkerDao workerDao;

    @Autowired
    BuildDao buildDao;

    @Async
    public void buildStage(Build build, Dickfile dickfile, Stage stage) {
        List<Job> jobs = dickfile.getJobs(stage);
        jobs.stream()
                .forEach(job -> workerService.sheduleJobBuild(build, job));
    }

    public Page<JobBuild> getJobBuilds(int page, int size) {
        return jobBuildDao.findAll(new PageRequest(page, size));
    }

    @Transactional
    public BuildOrder peekBuildFor(String workerName) {
        Worker worker = workerDao.findByName(workerName);
        JobBuild jobBuild = jobBuildDao.findByStatusAndWorker(JobBuild.Status.READY, worker);

        return jobBuild == null ? null : prepareBuildOrder(jobBuild, worker);
    }

    /*
     if (atLeastOneFailed) {
     build.setBuildStatus(BuildStatus.FAILED);
     } else {
     build.setBuildStatus(BuildStatus.DEPLOYED);
     }
     build.setCurrentStage(stage.getName());
     buildDao.save(build);

     Stage nextStage = dickfile.getNextStage(stage);
     if (!atLeastOneFailed && nextStage != null && nextStage.isAutorun()) {
     buildStageBlocking(build, dickfile, nextStage);
     }
     */
//
//    private Status performJobBuild(JobBuild jobBuild, List<String> deploy, Map<String, String> environment) {
//        jobBuild.setJobBuildStatus(Status.IN_PROGRESS);
//        deploymentDao.save(jobBuild);
//
//        for (String command : deploy) {
//            try {
//                Path temp = Files.createTempDirectory("deployment-" + jobBuild.getId());
//                CommandResult result = commandService.invokeWithEnvironment(temp, environment, command.split(" "));
//                StringBuilder builder = new StringBuilder(jobBuild.getDeploymentLog()).append("\n");
//                jobBuild.setDeploymentLog(builder.append(result.getOutput()).toString());
//                deploymentDao.save(jobBuild);
//                if (result.getResult() != 0) {
//                    jobBuild.setJobBuildStatus(Status.FAILED);
//                    deploymentDao.save(jobBuild);
//                    return Status.FAILED;
//                }
//            } catch (IOException ex) {
//                throw Throwables.propagate(ex);
//            }
//        }
//        jobBuild.setJobBuildStatus(Status.DEPLOYED);
//        deploymentDao.save(jobBuild);
//        return Status.DEPLOYED;
//    }
    private BuildOrder prepareBuildOrder(JobBuild jobBuild, Worker worker) {
        jobBuild.setStatus(JobBuild.Status.IN_PROGRESS);
        jobBuildDao.save(jobBuild);
        worker.setStatus(Worker.Status.BUSY);
        workerDao.save(worker);

        return new BuildOrder(jobBuild.getId(), jobBuild.getDeploy(), jobBuild.getEnvironment());
    }
}
