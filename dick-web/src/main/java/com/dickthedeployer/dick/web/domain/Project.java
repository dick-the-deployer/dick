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

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author mariusz
 */
@Data
@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"name", "namespace_id"}))
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<EnvironmentVariable> environmentVariables;

    @ManyToOne(optional = false)
    private Namespace namespace;

    private String name;
    private String repository;
    private String repositoryHost;
    private String repositoryPath;
    private String ref;
    private String description;


    public static class Builder {

        private final Project item;

        public Builder() {
            this.item = new Project();
        }

        public Builder withId(final Long id) {
            this.item.id = id;
            return this;
        }

        public Builder withName(final String name) {
            this.item.name = name;
            return this;
        }

        public Builder withNamespace(final Namespace namespace) {
            this.item.namespace = namespace;
            return this;
        }

        public Builder withDescription(final String description) {
            this.item.description = description;
            return this;
        }

        public Builder withRepositoryHost(final String repositoryHost) {
            this.item.repositoryHost = repositoryHost;
            return this;
        }

        public Builder withRepositoryPath(final String repositoryPath) {
            this.item.repositoryPath = repositoryPath;
            return this;
        }

        public Builder withRepository(final String repository) {
            this.item.repository = repository;
            return this;
        }

        public Builder withCreationDate(final Date creationDate) {
            this.item.creationDate = creationDate;
            return this;
        }

        public Builder withEnvironmentVariables(final List<EnvironmentVariable> environmentVariables) {
            this.item.environmentVariables = environmentVariables;
            return this;
        }

        public Builder withRef(final String ref) {
            this.item.ref = ref;
            return this;
        }

        public Project build() {
            return this.item;
        }
    }

}
