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
import com.dickthedeployer.dick.web.domain.Project;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author mariusz
 */
public class DickYmlServiceTest extends ContextTestBase {

    @Autowired
    DickYmlService dickYmlService;

    @Test
    public void shouldLoadDickfile() throws DickFileMissingException {
        Dickfile dickfile = dickYmlService.loadDickFile(new Build.Builder()
                .withSha("cdc902352d18080292daea9b4f99ba5d16801b88")
                .withRef("master")
                .withRepository("https://github.com/dick-the-deployer/examples.git")
                .withProject(new Project.Builder()
                        .withRef("master")
                        .withName("dick-the-deployer/dick")
                        .withRepository("https://github.com/dick-the-deployer/examples.git")
                        .build()
                ).build()
        );

        assertThat(dickfile).isNotNull();
        assertThat(dickfile.getPipeline().getStages())
                .asList().hasSize(2).extracting("name").containsExactly("first", "second");
        assertThat(dickfile.getJobs()).asList().hasSize(2);
    }
}
