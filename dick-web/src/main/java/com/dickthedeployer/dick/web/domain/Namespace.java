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

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 *
 * @author mariusz
 */
@Data
@Entity
public class Namespace {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "namespace", fetch = FetchType.EAGER)
    private List<Project> projects;

    public static class Builder {

        private final Namespace item;

        public Builder() {
            this.item = new Namespace();
        }

        public Builder withId(final Long id) {
            this.item.id = id;
            return this;
        }

        public Builder withName(final String name) {
            this.item.name = name;
            return this;
        }

        public Builder withProjects(final List<Project> projects) {
            this.item.projects = projects;
            return this;
        }

        public Namespace build() {
            return this.item;
        }
    }

}
