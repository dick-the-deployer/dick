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
package com.dickthedeployer.dick.web.hook.gitlab;

import com.dickthedeployer.dick.web.exception.RepositoryParsingException;
import com.dickthedeployer.dick.web.hook.RepositoryMapper;
import com.google.common.base.Throwables;
import lombok.Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Data
public class GitlabTrigger {

    String after;
    String ref;
    Repository repository;
    List<Commit> commits;

    public String getRef() {
        String[] split = ref.split("/");
        return split[split.length - 1];
    }

    public String getLastMessage() {
        return commits.stream()
                .filter(commit -> commit.getId().equals(after))
                .map(Commit::getMessage)
                .findAny()
                .orElseGet(() -> null);
    }

    @Data
    public static class Repository {
        String git_http_url;
        String name;

        public String getHost() {
            try {
                return new URI(git_http_url).getHost();
            } catch (URISyntaxException ex) {
                throw Throwables.propagate(ex);
            }
        }

        public String getPath() {
            try {
                return RepositoryMapper.getPath(git_http_url);
            } catch (RepositoryParsingException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Data
    public static class Commit {
        String id;
        String message;
    }
}
