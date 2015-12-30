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

import java.util.ArrayList;
import java.util.List;

/**
 * @author mariusz
 */
public class BuildMapper {

    public static BuildModel mapBuild(Build build) {
        List<StageModel> stages = getStageModels(build);

        return BuildModel.builder()
                .creationDate(build.getCreationDate())
                .id(build.getId())
                .currentStage(build.getCurrentStage())
                .stages(stages)
                .status(build.getStatus())
                .build();
    }

    private static List<StageModel> getStageModels(Build build) {
        List<StageModel> stages = new ArrayList<>();
        boolean afterCurrentStage = StringUtils.isEmpty(build.getCurrentStage());
        for (String stageName : build.getStages()) {
            if (stageName.equals(build.getCurrentStage())) {
                stages.add(
                        StageModel.builder()
                                .name(stageName)
                                .status(build.getStatus())
                                .build()
                );
                afterCurrentStage = true;
            } else {
                if (afterCurrentStage) {
                    stages.add(
                            StageModel.builder()
                                    .name(stageName)
                                    .status(Build.Status.READY)
                                    .build()
                    );
                } else {
                    stages.add(
                            StageModel.builder()
                                    .name(stageName)
                                    .status(Build.Status.DEPLOYED)
                                    .build()
                    );
                }
            }
        }
        return stages;
    }
}
