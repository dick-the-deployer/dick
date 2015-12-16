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
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.domain.Worker;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import com.dickthedeployer.dick.web.model.dickfile.EnvironmentVariable;
import com.dickthedeployer.dick.web.model.dickfile.Job;
import com.dickthedeployer.dick.web.model.dickfile.Pipeline;
import com.dickthedeployer.dick.web.model.dickfile.Stage;
import com.dickthedeployer.dick.web.service.util.OptymisticLockService;
import static com.watchrabbit.commons.sleep.Sleep.sleep;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 *
 * @author mariusz
 */
public class WorkerServiceTest extends ContextTestBase {

    @Autowired
    WorkerService workerService;

    @Autowired
    OptymisticLockService optymisticLockService;

    @Test
    public void shouldRegisterNewWorker() {
        String workerName = workerService.registerWorker();
        Worker worker = workerDao.findByName(workerName);

        assertThat(workerName).isNotEmpty();
        assertThat(worker).isNotNull();
    }

    @Test
    public void shouldScheduleExecution() {
        Build build = prepareBuild();
        Stage firstStage = new Stage("first", true);
        Dickfile dickfile = prepareDickfile(firstStage);

        workerService.sheduleJobBuild(build, dickfile.getJobs(firstStage).get(0));

        List<JobBuild> builds = jobBuildDao.findByBuild(build);
        assertThat(builds).hasSize(1);
        assertThat(builds.get(0).getEnvironment()).containsEntry("FOOKEY", "foo");
        assertThat(builds.get(0).getDeploy()).containsExactly("echo foo");
    }

    private Build prepareBuild() {
        final Project project = new Project.Builder()
                .withRef("master")
                .withName(UUID.randomUUID().toString())
                .withRepository(UUID.randomUUID().toString())
                .withEnvironmentVariables(asList(
                                new com.dickthedeployer.dick.web.domain.EnvironmentVariable("BARKEY", "bar")
                        )
                ).build();
        projectDao.save(project);
        Build build = new Build.Builder()
                .withSha("somesha")
                .withProject(project).build();
        buildDao.save(build);
        return build;
    }

    private Dickfile prepareDickfile(Stage firstStage) {
        Dickfile dickfile = new Dickfile();
        Pipeline pipeline = new Pipeline();
        pipeline.setStages(asList(
                firstStage
        ));
        dickfile.setPipeline(pipeline);
        Job first = new Job();
        first.setEnvironmentVariables(asList(
                new EnvironmentVariable("FOOKEY", "foo")
        ));
        first.setName("first job");
        first.setStage("first");
        first.setDeploy(asList("echo foo"));
        dickfile.setJobs(asList(first));
        return dickfile;
    }

    @Test
    public void shouldAssignWorker() {
        JobBuild jobBuild = jobBuildDao.save(new JobBuild());
        Worker worker = workerDao.save(new Worker());

        sleep(2, TimeUnit.SECONDS);

        jobBuild = jobBuildDao.findOne(jobBuild.getId());
        worker = workerDao.findOne(worker.getId());
        assertThat(jobBuild.getWorker()).isNotNull();
        assertThat(jobBuild.getWorker().getId()).isEqualTo(worker.getId());
        assertThat(worker.getStatus()).isEqualTo(Worker.Status.BUSY);
    }

    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void shouldThrowOptymistickLockException() throws InterruptedException, TimeoutException, ExecutionException {
        Worker worker = workerDao.save(new Worker());
        optymisticLockService.shouldThrowOptymisticLockException(worker.getId());
    }
}
