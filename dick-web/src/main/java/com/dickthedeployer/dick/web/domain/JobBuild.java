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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
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

    @ManyToOne(cascade = CascadeType.MERGE)
    private Build build;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Lob
    private String deploymentLog = "";

    private String job;

    @Enumerated(EnumType.STRING)
    private Status status = Status.READY;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Worker worker;

    @Version
    private int versionNo = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "entry_name")
    @Column(name = "entry_value")
    @CollectionTable(name = "job_build_attributes", joinColumns = @JoinColumn(name = "job_build_id"))
    Map<String, String> environment = new HashMap<String, String>();

    @ElementCollection(fetch = FetchType.EAGER)
    List<String> deploy;

    public boolean isStopped() {
        return !status.equals(Status.IN_PROGRESS);
    }

    public static enum Status {

        FAILED, DEPLOYED, READY, IN_PROGRESS, STOPPED
    }
}
