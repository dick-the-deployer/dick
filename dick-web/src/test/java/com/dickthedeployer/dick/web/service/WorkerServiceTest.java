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
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.domain.Worker;
import com.dickthedeployer.dick.web.service.util.OptymisticLockService;
import static com.watchrabbit.commons.sleep.Sleep.sleep;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
