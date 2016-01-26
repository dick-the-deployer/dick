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
package com.dickthedeployer.dick.web.domain;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author mariusz
 */
@Data
@Entity
public class Build {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Project project;

    @Enumerated(EnumType.STRING)
    private Status status = Status.READY;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    private boolean inQueue = true;
    private String sha;
    private String currentStage;
    private String lastMessage;
    private String repository;
    private String ref;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<String> stages;

    public static class Builder {

        private final Build item;

        public Builder() {
            this.item = new Build();
        }

        public Builder withRepository(final String repository) {
            this.item.repository = repository;
            return this;
        }

        public Builder withRef(final String ref) {
            this.item.ref = ref;
            return this;
        }

        public Builder withProject(final Project project) {
            this.item.project = project;
            return this;
        }

        public Builder withSha(final String sha) {
            this.item.sha = sha;
            return this;
        }

        public Builder withLastMessage(final String lastMessage) {
            this.item.lastMessage = lastMessage;
            return this;
        }

        public Build build() {
            return this.item;
        }
    }

    public static enum Status {

        FAILED, DEPLOYED_STAGE, DEPLOYED, READY, IN_PROGRESS, MISSING_DICKFILE, STOPPED
    }

}
