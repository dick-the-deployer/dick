package com.dickthedeployer.dick.web.model;

import lombok.Data;

@Data
public class HookModel {

    String host;
    String path;
    String ref;
    String sha;
}
