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
import com.dickthedeployer.dick.web.model.BuildOrder;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mariusz
 */
public class JobBuildServiceTest extends ContextTestBase {

    @Autowired
    JobBuildService jobBuildService;

    @Test
    public void shouldReturnBuildOrder() {
        Worker worker = new Worker();
        worker.setName("testingWorker");
        workerDao.save(worker);
        JobBuild jobBuild = new JobBuild();
        jobBuild.setWorker(worker);
        jobBuild.setStatus(JobBuild.Status.READY);
        jobBuild.setDeploy(asList("echo bar"));
        jobBuild.setEnvironment(singletonMap("FOO", "bar"));
        jobBuildDao.save(jobBuild);

        BuildOrder buildOrder = jobBuildService.peekBuildFor("testingWorker");

        assertThat(buildOrder).isNotNull();
        assertThat(buildOrder.getBuildId()).isEqualTo(jobBuild.getId());
        assertThat(buildOrder.getCommands()).containsExactly("echo bar");
        assertThat(buildOrder.getEnvironment()).containsEntry("FOO", "bar");

        buildOrder = jobBuildService.peekBuildFor("testingWorker");

        assertThat(buildOrder).isNull();
    }

}
