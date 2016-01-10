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
package com.dickthedeployer.dick.web.hook.gitlabci;

import com.dickthedeployer.dick.web.hook.gitlab.GitlabTrigger;
import com.google.common.base.Throwables;
import lombok.Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author mariusz
 */
@Data
public class GitlabCiTrigger {

    String build_id;
    String build_status;
    String project_name;
    String gitlab_url;
    String ref;
    String sha;
    PushData pushData;

    public String getLastMessage() {
        return pushData.getCommits().stream()
                .filter(commit -> commit.getId().equals(sha))
                .map(GitlabTrigger.Commit::getMessage)
                .findAny()
                .orElseGet(() -> null);
    }

    public String getHost() {
        try {
            return new URI(getGitlab_url()).getHost();
        } catch (URISyntaxException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public String getProjectName() {
        return getProject_name().replaceAll(" ", "");
    }

    @Data
    public static class PushData {
        List<GitlabTrigger.Commit> commits;
    }
}
