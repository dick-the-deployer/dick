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
package com.dickthedeployer.dick.web.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.FetchType.EAGER;

/**
 * @author mariusz
 */
@Data
@Entity
public class LogChunk {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private JobBuild jobBuild;

    @OneToOne(cascade = CascadeType.ALL, fetch = EAGER)
    private JobBuildLog buildLog = new JobBuildLog();

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

}
