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
import com.dickthedeployer.dick.web.domain.Stack;
import com.dickthedeployer.dick.web.exception.DickFileMissingException;
import com.dickthedeployer.dick.web.model.dickfile.Dickfile;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mariusz
 */
public class DickYmlServiceTest extends ContextTestBase {

    @Autowired
    DickYmlService dickYmlService;

    @Test
    public void shouldLoadDickfile() throws DickFileMissingException {
        Dickfile dickfile = dickYmlService.loadDickFile(new Build.Builder()
                .withSha("2b1e9ab0207e91d3135163030d7c7ca05a40798f")
                .withStack(new Stack.Builder()
                        .withRef("master")
                        .withName("dick-the-deployer/dick")
                        .withRepository("https://github.com/dick-the-deployer/dick.git")
                        .build()
                ).build()
        );

        assertThat(dickfile).isNotNull();
        assertThat(dickfile.getPipeline().getStages()).asList().hasSize(3).extracting("name").containsExactly("first", "second", "third");
        assertThat(dickfile.getJobs()).asList().hasSize(4);
    }
}
