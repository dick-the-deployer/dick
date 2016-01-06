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

import com.dickthedeployer.dick.web.ContextTestBase;
import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.LogChunk;
import com.dickthedeployer.dick.web.domain.Worker;
import com.dickthedeployer.dick.web.model.BuildOrder;
import com.dickthedeployer.dick.web.service.util.TransactionalQueryingService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author mariusz
 */
public class JobBuildServiceTest extends ContextTestBase {

    @Autowired
    JobBuildService jobBuildService;

    @Autowired
    TransactionalQueryingService transactionalQueryingService;

    @Test
    public void shouldReturnBuildOrder() {
        Worker worker = produceWorker(Worker.Status.READY);
        Build build = produceBuild();
        JobBuild jobBuild = produceJobBuild(worker, build, JobBuild.Status.READY);

        BuildOrder buildOrder = jobBuildService.peekBuildFor("testingWorker");

        assertThat(buildOrder).isNotNull();
        assertThat(buildOrder.getBuildId()).isEqualTo(jobBuild.getId());
        assertThat(buildOrder.getCommands()).containsExactly("echo bar");
        assertThat(buildOrder.getEnvironment()).containsEntry("FOO", "bar");

        buildOrder = jobBuildService.peekBuildFor("testingWorker");

        assertThat(buildOrder).isNull();
    }

    @Test
    public void shouldStopBuild() {
        Worker worker = produceWorker(Worker.Status.BUSY);
        Build build = produceBuild();
        JobBuild jobBuild = produceJobBuild(worker, build, JobBuild.Status.IN_PROGRESS);

        jobBuildService.stop(build);

        jobBuild = jobBuildDao.findOne(jobBuild.getId());
        worker = workerDao.findByName(jobBuild.getWorkerName());
        assertThat(jobBuild.getStatus()).isEqualTo(JobBuild.Status.STOPPED);
        assertThat(jobBuild.getWorker()).isNull();
        assertThat(worker.getStatus()).isEqualTo(Worker.Status.READY);
        assertThat(jobBuild.getBuild().getStatus()).isEqualTo(Build.Status.STOPPED);
    }

    @Test
    public void shouldReturnStoppedStatus() {
        Worker worker = produceWorker(Worker.Status.BUSY);
        Build build = produceBuild();
        JobBuild jobBuild = produceJobBuild(worker, build, JobBuild.Status.STOPPED);

        boolean stopped = jobBuildService.isStopped(jobBuild.getId());

        assertThat(stopped).isTrue();
    }

    @Test
    public void shouldReturnNotStoppedStatus() {
        Worker worker = produceWorker(Worker.Status.BUSY);
        Build build = produceBuild();
        JobBuild jobBuild = produceJobBuild(worker, build, JobBuild.Status.IN_PROGRESS);

        boolean stopped = jobBuildService.isStopped(jobBuild.getId());

        assertThat(stopped).isFalse();
    }

    @Test
    public void shouldSetFailureStatus() {
        Worker worker = produceWorker(Worker.Status.BUSY);
        Build build = produceBuild();
        JobBuild jobBuild = produceJobBuild(worker, build, JobBuild.Status.IN_PROGRESS);

        jobBuildService.reportFailure(jobBuild.getId(), "foo");

        jobBuild = jobBuildDao.findOne(jobBuild.getId());
        worker = workerDao.findByName(jobBuild.getWorkerName());
        assertThat(jobBuild.getStatus()).isEqualTo(JobBuild.Status.FAILED);
        assertThat(jobBuild.getWorker()).isNull();
        assertThat(worker.getStatus()).isEqualTo(Worker.Status.READY);
        assertThat(jobBuild.getBuild().getStatus()).isEqualTo(Build.Status.FAILED);

        String output = transactionalQueryingService.getOutput(jobBuild.getId());
        assertThat(output).isEqualTo("foo");
    }

    @Test
    public void shouldNotSetFailureStatusBecauseJobNotCompleted() {
        Worker worker = produceWorker(Worker.Status.BUSY);
        Build build = produceBuild();
        JobBuild jobBuild = produceJobBuild(worker, build, JobBuild.Status.IN_PROGRESS);
        produceJobBuild(worker, build, JobBuild.Status.IN_PROGRESS);

        jobBuildService.reportFailure(jobBuild.getId(), "foo");

        jobBuild = jobBuildDao.findOne(jobBuild.getId());
        worker = workerDao.findByName(jobBuild.getWorkerName());
        assertThat(jobBuild.getStatus()).isEqualTo(JobBuild.Status.FAILED);
        assertThat(jobBuild.getWorker()).isNull();
        assertThat(worker.getStatus()).isEqualTo(Worker.Status.READY);
        assertThat(jobBuild.getBuild().getStatus()).isEqualTo(Build.Status.IN_PROGRESS);
    }

    @Test
    public void shouldAppendLog() {
        Worker worker = produceWorker(Worker.Status.BUSY);
        Build build = produceBuild();
        JobBuild jobBuild = produceJobBuild(worker, build, JobBuild.Status.IN_PROGRESS);

        jobBuildService.reportProgress(jobBuild.getId(), "foo");
        jobBuildService.reportProgress(jobBuild.getId(), "bar");

        List<LogChunk> logChunks = logChunkDao.findByJobBuild(jobBuild);
        assertThat(logChunks).extracting("buildLog").extracting("output").containsExactly("foo", "bar");
    }

    private JobBuild produceJobBuild(Worker worker, Build build, JobBuild.Status status) {
        JobBuild jobBuild = new JobBuild();
        jobBuild.setWorker(worker);
        jobBuild.setWorkerName(worker.getName());
        jobBuild.setBuild(build);
        jobBuild.setStatus(status);
        jobBuild.setDeploy(asList("echo bar"));
        jobBuild.setEnvironment(singletonMap("FOO", "bar"));
        jobBuildDao.save(jobBuild);
        return jobBuild;
    }

    private Build produceBuild() {
        Build build = new Build();
        build.setStatus(Build.Status.IN_PROGRESS);
        buildDao.save(build);
        return build;
    }

    private Worker produceWorker(Worker.Status status) {
        Worker worker = new Worker();
        worker.setStatus(status);
        worker.setName("testingWorker");
        workerDao.save(worker);
        return worker;
    }

}
