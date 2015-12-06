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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.Version;
import lombok.Data;

/**
 *
 * @author mariusz
 */
@Data
@Entity
public class Worker {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date registrationDate = new Date();

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastHeartbeat = new Date();

    @Version
    private int versionNo = 0;

    private Status status = Status.READY;

    public static enum Status {

        READY, BUSY, DEAD
    }
}
