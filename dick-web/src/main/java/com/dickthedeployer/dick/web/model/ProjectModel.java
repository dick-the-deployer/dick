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
package com.dickthedeployer.dick.web.model;

import com.dickthedeployer.dick.web.model.dickfile.EnvironmentVariable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 *
 * @author mariusz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModel {

    @NotNull
    private String name;
    @NotNull
    private String namespace;
    @NotNull
    private String repository;
    @NotNull
    private String ref;

    private Long id;
    private Date creationDate;
    private String description;
    private List<EnvironmentVariable> environmentVariables = emptyList();
    private BuildModel lastBuild;
}
