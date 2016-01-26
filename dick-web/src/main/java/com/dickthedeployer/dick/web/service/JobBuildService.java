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
import com.dickthedeployer.dick.web.dao.LogChunkDao;
import com.dickthedeployer.dick.web.dao.WorkerDao;
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.LogChunk;
import com.dickthedeployer.dick.web.domain.Worker;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.model.BuildOrder;
import com.dickthedeployer.dick.web.model.LogChunkModel;
import com.dickthedeployer.dick.web.model.OutputModel;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
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

    @Autowired
    LogChunkDao logChunkDao;

    @Transactional
    public void prepareJobs(Build build, Dickfile dickfile) {
        dickfile.getStageNames().stream()
                .map(dickfile::getStage)
                .forEach(stage ->
                        dickfile.getJobs(stage).stream()
                                .map(job -> {
                                    JobBuild jobBuild = new JobBuild();
                                    jobBuild.setName(job.getName());
                                    jobBuild.setBuild(build);
                                    jobBuild.setStage(stage.getName());
                                    jobBuild.setDeploy(job.getDeploy());
                                    jobBuild.setStatus(JobBuild.Status.WAITING);
                                    jobBuild.setRequireRepository(job.isRequireRepository());
                                    return jobBuild;
                                }).forEach(jobBuildDao::save)
                );
    }

    @Transactional
    public void buildStage(Build build, Dickfile dickfile, Stage stage) {
        List<Job> jobs = dickfile.getJobs(stage);
        build.setCurrentStage(stage.getName());
        buildDao.save(build);
        jobs.stream()
                .forEach(job -> workerService.scheduleJobBuild(build, stage.getName(), job));

        updateBuildStatus(build);
    }

    @Transactional
    public Optional<BuildOrder> peekBuildFor(String workerName) {
        Worker worker = workerDao.findByName(workerName).get();
        Optional<JobBuild> jobBuildOptional = jobBuildDao.findByStatusAndWorker(JobBuild.Status.READY, worker);

        return jobBuildOptional.map(jobBuild -> prepareBuildOrder(jobBuild, worker));
    }

    public boolean isStopped(Long id) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        return jobBuild == null || jobBuild.isStopped();
    }

    @Transactional
    public void reportProgress(Long id, String partialLog) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        log.info("Reporting progress on {}", id);
        if (jobBuild != null) {
            LogChunk logChunk = new LogChunk();
            logChunk.setJobBuild(jobBuild);
            logChunk.getBuildLog().setOutput(partialLog);
            logChunkDao.save(logChunk);

            updateBuildStatus(jobBuild.getBuild());
        }
    }

    @Transactional
    public void reportFailure(Long id, String fullLog) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        log.info("Reporting failure on {}", id);
        if (jobBuild != null) {
            if (!jobBuild.getStatus().equals(JobBuild.Status.STOPPED)) {
                jobBuild.setStatus(JobBuild.Status.FAILED);
            }
            clearJob(fullLog, jobBuild);

            updateBuildStatus(jobBuild.getBuild());
        }
    }

    @Transactional
    public void reportSuccess(Long id, String fullLog) {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        log.info("Reporting success on {}", id);
        if (jobBuild != null) {
            jobBuild.setStatus(JobBuild.Status.DEPLOYED);
            clearJob(fullLog, jobBuild);

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
    }

    private void clearJob(String fullLog, JobBuild jobBuild) {
        workerService.readyWorker(Optional.ofNullable((jobBuild.getWorker())));
        jobBuild.setWorker(null);
        jobBuild.getBuildLog().setOutput(fullLog);
        jobBuildDao.save(jobBuild);
        logChunkDao.deleteByJobBuild(jobBuild);
    }

    public Build.Status updateBuildStatus(Build build) {
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
        } else if (statuses.containsKey(JobBuild.Status.IN_PROGRESS)) {
            return Build.Status.IN_PROGRESS;
        } else {
            return Build.Status.READY;
        }
    }

    private BuildOrder prepareBuildOrder(JobBuild jobBuild, Worker worker) {
        jobBuild.setStatus(JobBuild.Status.IN_PROGRESS);
        jobBuildDao.save(jobBuild);
        worker.setStatus(Worker.Status.BUSY);
        workerDao.save(worker);

        return BuildOrder.builder()
                .buildId(jobBuild.getId())
                .commands(jobBuild.getDeploy())
                .environment(jobBuild.getEnvironment())
                .requireRepository(jobBuild.isRequireRepository())
                .ref(jobBuild.getBuild().getRef())
                .sha(jobBuild.getBuild().getSha())
                .repository(jobBuild.getBuild().getRepository())
                .build();
    }

    @Transactional
    public void stop(Build build) {
        List<JobBuild> jobBuilds = jobBuildDao.findByBuild(build);
        jobBuilds.stream()
                .filter(jobBuild -> jobBuild.getStatus().equals(JobBuild.Status.IN_PROGRESS))
                .forEach(jobBuild -> {
                    jobBuild.setStatus(JobBuild.Status.STOPPED);
                    workerService.readyWorker(Optional.of(jobBuild.getWorker()));
                    jobBuild.setWorker(null);
                    jobBuildDao.save(jobBuild);
                });
        jobBuilds.stream()
                .filter(jobBuild -> jobBuild.getStatus().equals(JobBuild.Status.READY))
                .forEach(jobBuild -> {
                    jobBuild.setStatus(JobBuild.Status.STOPPED);
                    workerService.readyWorker(Optional.ofNullable(jobBuild.getWorker()));
                    jobBuild.setWorker(null);
                    jobBuildDao.save(jobBuild);
                });
        updateBuildStatus(build);
    }

    @Transactional
    public List<LogChunkModel> getLogChunks(Long id, Date creationDate) throws NotFoundException {
        JobBuild jobBuild = getAndCheckJobBuild(id);
        List<LogChunk> logChunks = creationDate == null ?
                logChunkDao.findByJobBuild(jobBuild) :
                logChunkDao.findByJobBuildAndCreationDateAfter(jobBuild, creationDate);
        return logChunks.stream()
                .map(logChunk ->
                        LogChunkModel.builder()
                                .output(logChunk.getBuildLog().getOutput())
                                .creationDate(logChunk.getCreationDate())
                                .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public OutputModel getOutput(Long id) throws NotFoundException {
        JobBuild jobBuild = getAndCheckJobBuild(id);
        return OutputModel.builder()
                .output(jobBuild.getBuildLog().getOutput())
                .build();
    }

    private JobBuild getAndCheckJobBuild(Long id) throws NotFoundException {
        JobBuild jobBuild = jobBuildDao.findOne(id);
        if (jobBuild == null) {
            throw new NotFoundException();
        }
        return jobBuild;
    }

    @Transactional
    public void deleteJobBuilds(Build build) {
        jobBuildDao.findByBuild(build).stream()
                .forEach(jobBuild -> {
                    logChunkDao.deleteByJobBuild(jobBuild);
                    workerService.readyWorker(Optional.ofNullable(jobBuild.getWorker()));
                    jobBuildDao.delete(jobBuild);
                });
    }
}
