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
import com.dickthedeployer.dick.web.model.BuildModel;
import com.dickthedeployer.dick.web.model.StageModel;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mariusz
 */
public class BuildMapper {

    public static BuildModel mapBuild(Build build) {
        Map<String, Build.Status> buildStatusMap = getBuildStatusMap(build);


        return BuildModel.builder()
                .creationDate(build.getCreationDate())
                .id(build.getId())
                .currentStage(build.getCurrentStage())
                .lastMessage(build.getLastMessage())
                .stages(
                        build.getStages().stream()
                                .map(stageName ->
                                        StageModel.builder()
                                                .status(buildStatusMap.get(stageName))
                                                .name(stageName)
                                                .build()
                                ).collect(Collectors.toList())
                ).status(build.getStatus())
                .build();
    }

    public static Map<String, Build.Status> getBuildStatusMap(Build build) {
        Map<String, Build.Status> buildStatusMap = new HashMap<>();
        boolean afterCurrentStage = StringUtils.isEmpty(build.getCurrentStage());
        for (String stageName : build.getStages()) {
            if (stageName.equals(build.getCurrentStage())) {
                buildStatusMap.put(stageName, build.getStatus());
                afterCurrentStage = true;
            } else {
                if (afterCurrentStage) {
                    buildStatusMap.put(stageName, Build.Status.READY);
                } else {
                    buildStatusMap.put(stageName, Build.Status.DEPLOYED);
                }
            }
        }
        return buildStatusMap;
    }
}
