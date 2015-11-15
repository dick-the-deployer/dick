/*
 * Copyright 2015 Pivotal Software, Inc..
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
import com.dickthedeployer.dick.web.domain.StackEntity;
import com.dickthedeployer.dick.web.model.StackModel;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mariusz
 */
public class StackServiceTest extends ContextTestBase{
   
    @Autowired
    StackService stackService;
    
    @Test
    public void shouldCreateStack() {
        StackModel model = new StackModel();
        model.setRepository("foo/bar");
        model.setProjectName("git@some.com:foo/bar.git");
        model.setServer("128.0.0.1");
        model.setRef("master");
        
        StackEntity entity = stackService.createStack(model);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getRef()).isEqualTo("master");
        assertThat(entity.getRepository()).isEqualTo("foo/bar");
        assertThat(entity.getProjectName()).isEqualTo("git@some.com:foo/bar.git");
        assertThat(entity.getServer()).isEqualTo("128.0.0.1");
        assertThat(entity.getCreationDate()).isNotNull();
        
    }
}
