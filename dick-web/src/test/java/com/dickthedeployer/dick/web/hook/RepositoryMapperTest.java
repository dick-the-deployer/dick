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
package com.dickthedeployer.dick.web.hook;

import com.dickthedeployer.dick.web.exception.RepositoryParsingException;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class RepositoryMapperTest {

    @Test
    public void shouldParse() throws URISyntaxException, RepositoryParsingException {
        String http = "https://github.com/dick-the-deployer/dick.git";
        String ssh = "git@github.com:dick-the-deployer/dick.git";

        String httpHost = RepositoryMapper.getHost(http);
        String sshHost = RepositoryMapper.getHost(ssh);
        String httpPath = RepositoryMapper.getPath(http);
        String sshPath = RepositoryMapper.getPath(ssh);

        assertThat(httpHost).isEqualTo(sshHost).isEqualTo("github.com");
        assertThat(httpPath).isEqualTo(sshPath).isEqualTo("dick-the-deployer/dick");
    }

}
