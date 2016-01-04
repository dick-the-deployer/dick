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
package com.dickthedeployer.dick.web.model;

import com.dickthedeployer.dick.web.domain.Build;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author mariusz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildDetailsModel {

    private ProjectModel project;

    private Long id;
    private List<StageDetailsModel> stages;
    private String currentStage;
    private Date creationDate;
    private Build.Status status;
    private String commitUrl;

}
