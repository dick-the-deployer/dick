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

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import lombok.Data;

/**
 *
 * @author mariusz
 */
@Data
@Entity
public class JobBuild {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Build build;

    @OneToOne
    private JobBuild rollback;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Lob
    private String deploymentLog = "";

    private String job;

    @Enumerated(EnumType.STRING)
    private JobBuildStatus jobBuildStatus = JobBuildStatus.READY;
}
