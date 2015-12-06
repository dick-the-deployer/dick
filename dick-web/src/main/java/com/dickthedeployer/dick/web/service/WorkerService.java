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
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.Worker;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import org.kohsuke.randname.RandomNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mariusz
 */
@Service
public class WorkerService {

    @Autowired
    RandomNameGenerator nameGenerator;

    @Autowired
    WorkerDao workerDao;

    @Autowired
    JobBuildDao jobBuildDao;

    public String registerWorker() {
        String workerName = nameGenerator.next();
        Worker worker = new Worker();
        worker.setName(workerName);
        workerDao.save(worker);
        return workerName;
    }

    public void sheduleJobBuild(Build build, Job job) {
        JobBuild jobBuild = new JobBuild();
        jobBuild.setBuild(build);
        jobBuildDao.save(jobBuild);
        //return performJobBuild(jobBuild, job.getDeploy(), getEnvironment(build, job));
    }

    @Transactional
    public void assignJobBuildToWorker(JobBuild jobBuild) {
        workerDao.findByStatus(Worker.Status.READY).stream()
                .findAny()
                .ifPresent(worker -> assignToWorker(jobBuild, worker));
    }

    private void assignToWorker(JobBuild jobBuild, Worker worker) {
        jobBuild.setWorker(worker);
        worker.setStatus(Worker.Status.BUSY);
        jobBuildDao.save(jobBuild);
        workerDao.save(worker);
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
//    private Map<String, String> getEnvironment(Build build, Job job) {
//        Map<String, String> environment = new HashMap<>();
//        environment.put("SHA", build.getSha());
//        environment.put("REF", build.getStack().getRef());
//        build.getStack().getEnvironmentVariables().forEach(variable -> environment.put(variable.getVariableKey(), variable.getVariableValue()));
//        job.getEnvironmentVariables().forEach(variable -> environment.put(variable.getKey(), variable.getValue()));
//        return environment;
//    }
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
}
