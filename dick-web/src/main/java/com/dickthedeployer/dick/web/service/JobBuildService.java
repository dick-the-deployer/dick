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
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.BuildStatus;
import com.dickthedeployer.dick.web.domain.JobBuildStatus;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.model.CommandResult;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    JobBuildDao deploymentDao;

    @Autowired
    BuildDao buildDao;

    @Async
    public void buildStage(Build build, Dickfile dickfile, Stage stage) {
        buildStageBlocking(build, dickfile, stage);
    }

    public Build buildStageBlocking(Build build, Dickfile dickfile, Stage stage) {
        List<Job> jobs = dickfile.getJobs(stage);
        boolean atLeastOneFailed = jobs.stream()
                .map(job -> buildJob(build, job))
                .anyMatch(status -> JobBuildStatus.FAILED.equals(status));
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
        return build;
    }

    private JobBuildStatus buildJob(Build build, Job job) {
        JobBuild jobBuild = new JobBuild();
        jobBuild.setBuild(build);
        deploymentDao.save(jobBuild);
        return performJobBuild(jobBuild, job.getDeploy(), getEnvironment(build, job));
    }

    private Map<String, String> getEnvironment(Build build, Job job) {
        Map<String, String> environment = new HashMap<>();
        environment.put("SHA", build.getSha());
        environment.put("REF", build.getStack().getRef());
        build.getStack().getEnvironmentVariables().forEach(variable -> environment.put(variable.getVariableKey(), variable.getVariableValue()));
        job.getEnvironmentVariables().forEach(variable -> environment.put(variable.getKey(), variable.getValue()));
        return environment;
    }

    private JobBuildStatus performJobBuild(JobBuild jobBuild, List<String> deploy, Map<String, String> environment) {
        jobBuild.setJobBuildStatus(JobBuildStatus.IN_PROGRESS);
        deploymentDao.save(jobBuild);

        for (String command : deploy) {
            try {
                Path temp = Files.createTempDirectory("deployment-" + jobBuild.getId());
                CommandResult result = commandService.invokeWithEnvironment(temp, environment, command.split(" "));
                StringBuilder builder = new StringBuilder(jobBuild.getDeploymentLog()).append("\n");
                jobBuild.setDeploymentLog(builder.append(result.getOutput()).toString());
                deploymentDao.save(jobBuild);
                if (result.getResult() != 0) {
                    jobBuild.setJobBuildStatus(JobBuildStatus.FAILED);
                    deploymentDao.save(jobBuild);
                    return JobBuildStatus.FAILED;
                }
            } catch (IOException ex) {
                throw Throwables.propagate(ex);
            }
        }
        jobBuild.setJobBuildStatus(JobBuildStatus.DEPLOYED);
        deploymentDao.save(jobBuild);
        return JobBuildStatus.DEPLOYED;
    }

    public Page<JobBuild> getJobBuilds(int page, int size) {
        return deploymentDao.findAll(new PageRequest(page, size));
    }

}
