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
import com.dickthedeployer.dick.web.domain.EnvironmentVariable;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.Worker;
import com.dickthedeployer.dick.web.exception.NotFoundException;
import com.dickthedeployer.dick.web.exception.WorkerBusyException;
import com.dickthedeployer.dick.web.mapper.WorkerMapper;
import com.dickthedeployer.dick.web.model.WorkerModel;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.randname.RandomNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author mariusz
 */
@Slf4j
@Service
public class WorkerService {

    @Autowired
    RandomNameGenerator nameGenerator;

    @Autowired
    WorkerDao workerDao;

    @Autowired
    JobBuildDao jobBuildDao;

    public String registerWorker() {
        log.info("Registering new worker");
        String workerName = nameGenerator.next();
        log.info("Selected name for new worker: {}", workerName);
        Worker worker = new Worker();
        worker.setName(workerName);
        workerDao.save(worker);
        return workerName;
    }

    @Transactional
    public void onHeartbeat(String name) {
        Worker worker = workerDao.findByName(name)
                .orElseGet(() -> Worker.builder()
                        .name(name)
                        .status(Worker.Status.READY)
                        .registrationDate(new Date())
                        .build()
                );
        worker.setLastHeartbeat(new Date());
        workerDao.save(worker);
    }

    public void scheduleJobBuild(Build build, String stageName, Job job) {
        JobBuild jobBuild = jobBuildDao.findByBuildAndStageAndName(build, stageName, job.getName()).get();
        jobBuild.setStatus(JobBuild.Status.READY);
        jobBuild.setWorkerName(null);
        jobBuild.setEnvironmentVariables(getEnvironment(build));
        jobBuildDao.save(jobBuild);
    }

    @Transactional
    public void assignJobBuildToWorker(JobBuild jobBuild) {
        workerDao.findByStatus(Worker.Status.READY).stream()
                .findAny()
                .ifPresent(worker -> assignToWorker(jobBuild, worker));
    }

    private List<EnvironmentVariable> getEnvironment(Build build) {
        Map<String, EnvironmentVariable> environment = new HashMap<>();

        environment.put("SHA", new EnvironmentVariable("SHA", build.getSha()));
        environment.put("REPOSITORY", new EnvironmentVariable("REPOSITORY", build.getProject().getRepository()));
        environment.put("REF", new EnvironmentVariable("REF", build.getProject().getRef()));
        build.getProject().getEnvironmentVariables()
                .forEach(variable -> environment.put(
                        variable.getVariableKey(),
                        new EnvironmentVariable(variable.getVariableKey(), variable.getVariableValue(), variable.isSecure()))
                );
        build.getEnvironmentVariables()
                .forEach(variable -> environment.put(
                        variable.getVariableKey(),
                        new EnvironmentVariable(variable.getVariableKey(), variable.getVariableValue(), variable.isSecure()))
                );
        return environment.values().stream().collect(Collectors.toList());
    }

    private void assignToWorker(JobBuild jobBuild, Worker worker) {
        jobBuild.setWorker(worker);
        jobBuild.setWorkerName(worker.getName());
        jobBuild.getBuild().setInQueue(false);
        worker.setStatus(Worker.Status.BUSY);
        jobBuildDao.save(jobBuild);
        workerDao.save(worker);
    }

    public List<WorkerModel> getWorkers(int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.DESC, "registrationDate");
        return workerDao.findAll(pageRequest).getContent().stream()
                .map(WorkerMapper::mapWorker)
                .collect(toList());
    }

    public WorkerModel getWorker(String name) throws NotFoundException {
        return workerDao.findByName(name)
                .map(WorkerMapper::mapWorker)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void deleteWorker(String name) throws NotFoundException, WorkerBusyException {
        Optional<Worker> workerOptional = workerDao.findByName(name);
        Worker worker = workerOptional.orElseThrow(NotFoundException::new);
        Long assigned = jobBuildDao.countByWorker(worker);
        if (assigned != 0) {
            throw new WorkerBusyException();
        }
        workerDao.delete(worker);
    }

    @Transactional
    public void readyWorker(Optional<Worker> workerOptional) {
        workerOptional.ifPresent(worker -> {
            worker.setStatus(Worker.Status.READY);
            workerDao.save(worker);
        });
    }
}
