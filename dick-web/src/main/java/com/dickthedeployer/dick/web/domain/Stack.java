/*
 * Copyright 2015 Pivotal Software, Inc..
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import lombok.Data;

/**
 *
 * @author mariusz
 */
@Data
@Entity
public class Stack {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Project project;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    private String server;

    private String ref;

    public static class Builder {

        private final Stack item;

        public Builder() {
            this.item = new Stack();
        }

        public Builder withId(final Long id) {
            this.item.id = id;
            return this;
        }

        public Builder withProject(final Project project) {
            this.item.project = project;
            return this;
        }

        public Builder withCreationDate(final Date creationDate) {
            this.item.creationDate = creationDate;
            return this;
        }

        public Builder withServer(final String server) {
            this.item.server = server;
            return this;
        }

        public Builder withRef(final String ref) {
            this.item.ref = ref;
            return this;
        }

        public Stack build() {
            return this.item;
        }
    }

}
