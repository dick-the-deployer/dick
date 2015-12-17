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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import lombok.Data;

/**
 *
 * @author mariusz
 */
@Data
@Entity(name = "groupTable")
public class Group {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Namespace namespace;

    private String describtion;

    public static class Builder {

        private final Group item;

        public Builder() {
            this.item = new Group();
        }

        public Builder withId(final Long id) {
            this.item.id = id;
            return this;
        }

        public Builder withCreationDate(final Date creationDate) {
            this.item.creationDate = creationDate;
            return this;
        }

        public Builder withNamespace(final Namespace namespace) {
            this.item.namespace = namespace;
            return this;
        }

        public Builder withDescribtion(final String describtion) {
            this.item.describtion = describtion;
            return this;
        }

        public Group build() {
            return this.item;
        }
    }

}
