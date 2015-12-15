/*
 * Copyright 2015 dick the deployer.
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
import com.dickthedeployer.dick.web.domain.Stack;
import com.google.common.base.Charsets;
import java.io.IOException;
import java.io.InputStream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

/**
 *
 * @author mariusz
 */
public class RepositoryServiceTest extends ContextTestBase {

    @Autowired
    RepositoryService service;

    @Test
    public void shouldCloneRepository() throws IOException {
        InputStream file = service.getFile(new Stack.Builder()
                .withName("dick-the-deployer/dick")
                .withRepository("https://github.com/dick-the-deployer/dick.git")
                .withRef("master")
                .build(), "74ab161c6b4df731ded57dd434c8df120d140832", ".dick.yml");

        String content = StreamUtils.copyToString(file, Charsets.UTF_8);
        assertThat(content).isNotNull().contains("foo: bar");
    }
}
