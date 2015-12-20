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
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.model.BuildOrder;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    DickYmlService dickYmlService;

    @Async
    @Transactional
    public void buildStage(Build build, Dickfile dickfile, Stage stage) {
        List<Job> jobs = dickfile.getJobs(stage);
        build.setStatus(Build.Status.IN_PROGRESS);
        build.setCurrentStage(stage.getName());
        buildDao.save(build);
        jobs.stream()
                .forEach(job -> workerService.sheduleJobBuild(build, stage.getName(), job));
    }

    @Transactional
    public BuildOrder peekBuildFor(String workerName) {
        Worker worker = workerDao.findByName(workerName);
        JobBuild jobBuild = jobBuildDao.findByStatusAndWorker(JobBuild.Status.READY, worker);

        return jobBuild == null ? null : prepareBuildOrder(jobBuild, worker);
    }

    public boolean isStopped(Long id) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        return jobBuild.isStopped();
    }

    public void stop(Long id) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        jobBuild.setStatus(JobBuild.Status.STOPPED);
        jobBuild.getWorker().setStatus(Worker.Status.READY);
        jobBuildDao.save(jobBuild);

        updateBuildStatus(jobBuild.getBuild());
    }

    @Transactional
    public void reportProgress(Long id, String log) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        String deploymentLog = jobBuild.getBuildLog().getOutput();
        jobBuild.getBuildLog().setOutput(deploymentLog + log);
        jobBuildDao.save(jobBuild);
    }

    @Transactional
    public void reportFailure(Long id, String log) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        jobBuild.setStatus(JobBuild.Status.FAILED);
        jobBuild.getWorker().setStatus(Worker.Status.READY);
        jobBuild.getBuildLog().setOutput(log);
        jobBuildDao.save(jobBuild);

        updateBuildStatus(jobBuild.getBuild());
    }

    @Transactional
    public void reportSuccess(Long id, String buildOutput) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        jobBuild.setStatus(JobBuild.Status.DEPLOYED);
        jobBuild.getWorker().setStatus(Worker.Status.READY);
        jobBuild.getBuildLog().setOutput(buildOutput);
        jobBuildDao.save(jobBuild);

        Build build = jobBuild.getBuild();
        Build.Status status = updateBuildStatus(build);
        if (status.equals(Build.Status.DEPLOYED_STAGE)) {
            log.info("Build status after job build {} is {}", id, status);
            try {
                Dickfile dickfile = dickYmlService.loadDickFile(build);
                Stage nextStage = dickfile.getNextStage(dickfile.getStage(build.getCurrentStage()));
                log.info("Next stage is {}", nextStage);
                if (nextStage == null) {
                    build.setStatus(Build.Status.DEPLOYED);
                    buildDao.save(build);
                } else if (nextStage.isAutorun()) {
                    buildStage(build, dickfile, nextStage);
                }
            } catch (DickFileMissingException ex) {
                throw Throwables.propagate(ex);
            }
        }
    }

    private Build.Status updateBuildStatus(Build build) {
        Build.Status buildStatus = determineBuildStatus(build);
        build.setStatus(buildStatus);
        buildDao.save(build);
        return buildStatus;
    }

    private Build.Status determineBuildStatus(Build build) {
        Map<JobBuild.Status, List<JobBuild.Status>> statuses = jobBuildDao.findByBuild(build)
                .stream()
                .map(JobBuild::getStatus)
                .collect(Collectors.groupingBy(Function.identity()));
        if (!statuses.containsKey(JobBuild.Status.IN_PROGRESS) && !statuses.containsKey(JobBuild.Status.READY)) {
            if (statuses.containsKey(JobBuild.Status.FAILED)) {
                return Build.Status.FAILED;
            } else if (statuses.containsKey(JobBuild.Status.STOPPED)) {
                return Build.Status.STOPPED;
            } else {
                return Build.Status.DEPLOYED_STAGE;
            }
        } else {
            return Build.Status.IN_PROGRESS;
        }
    }

    private BuildOrder prepareBuildOrder(JobBuild jobBuild, Worker worker) {
        jobBuild.setStatus(JobBuild.Status.IN_PROGRESS);
        jobBuildDao.save(jobBuild);
        worker.setStatus(Worker.Status.BUSY);
        workerDao.save(worker);

        return new BuildOrder(jobBuild.getId(), jobBuild.getDeploy(), jobBuild.getEnvironment());
    }

}
