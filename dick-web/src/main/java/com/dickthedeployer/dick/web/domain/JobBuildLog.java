package com.dickthedeployer.dick.web.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Data
@Entity
public class JobBuildLog {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String output = "";
}
