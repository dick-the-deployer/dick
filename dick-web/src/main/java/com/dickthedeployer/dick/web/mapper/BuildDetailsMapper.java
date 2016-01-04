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
package com.dickthedeployer.dick.web.mapper;

import com.dickthedeployer.dick.web.domain.Build;
import com.dickthedeployer.dick.web.domain.JobBuild;
import com.dickthedeployer.dick.web.model.BuildDetailsModel;
import com.dickthedeployer.dick.web.model.JobBuildModel;
import com.dickthedeployer.dick.web.model.ProjectModel;
import com.dickthedeployer.dick.web.model.StageDetailsModel;

import java.util.List;
import java.util.Map;

import static com.dickthedeployer.dick.web.mapper.BuildMapper.getBuildStatusMap;
import static java.util.stream.Collectors.toList;

/**
 * @author mariusz
 */
public class BuildDetailsMapper {

    public static BuildDetailsModel mapBuildDetails(Build build, ProjectModel projectModel, List<JobBuild> jobBuilds) {
        Map<String, Build.Status> buildStatusMap = getBuildStatusMap(build);
        return BuildDetailsModel.builder()
                .project(projectModel)
                .creationDate(build.getCreationDate())
                .commitUrl(build.getCommitUrl())
                .currentStage(build.getCurrentStage())
                .id(build.getId())
                .stages(
                        build.getStages().stream()
                                .map(stageName ->
                                        StageDetailsModel.builder()
                                                .name(stageName)
                                                .status(buildStatusMap.get(stageName))
                                                .jobBuilds(jobBuilds.stream()
                                                        .filter(jobBuild -> jobBuild.getStage().equals(stageName))
                                                        .map(jobBuild ->
                                                                JobBuildModel.builder()
                                                                        .name(jobBuild.getName())
                                                                        .status(jobBuild.getStatus())
                                                                        .id(jobBuild.getId())
                                                                        .build()
                                                        ).collect(toList())
                                                ).build()
                                ).collect(toList())
                ).status(build.getStatus())
                .build();
    }
}
