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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.persistence.FetchType.LAZY;

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

    @ManyToOne(cascade = CascadeType.MERGE)
    private Build build;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
    private JobBuildLog buildLog = new JobBuildLog();

    private String name;

    private String workerName;

    private String stage;

    private boolean requireRepository;

    private String dockerImage;

    @Enumerated(EnumType.STRING)
    private Status status = Status.READY;

    @OneToOne(cascade = CascadeType.MERGE)
    private Worker worker;

    @Version
    private int versionNo = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "entry_name")
    @Column(name = "entry_value")
    @CollectionTable(name = "job_build_attributes", joinColumns = @JoinColumn(name = "job_build_id"))
    Map<String, String> environment = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    List<String> deploy;

    public boolean isStopped() {
        return !status.equals(Status.IN_PROGRESS);
    }

    public enum Status {

        FAILED, DEPLOYED, READY, IN_PROGRESS, STOPPED, WAITING
    }
}
