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

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 *
 * @author mariusz
 */
@Data
@Entity
public class Build {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Stack stack;

    @Enumerated(EnumType.STRING)
    private Status status = Status.READY;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    private String buildUrl;

    private String commitUrl;

    private String sha;

    private String currentStage;

    public static class Builder {

        private final Build item;

        public Builder() {
            this.item = new Build();
        }

        public Builder withId(final Long id) {
            this.item.id = id;
            return this;
        }

        public Builder withStack(final Stack stack) {
            this.item.stack = stack;
            return this;
        }

        public Builder withBuildUrl(final String buildUrl) {
            this.item.buildUrl = buildUrl;
            return this;
        }

        public Builder withCommitUrl(final String commitUrl) {
            this.item.commitUrl = commitUrl;
            return this;
        }

        public Builder withSha(final String sha) {
            this.item.sha = sha;
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
