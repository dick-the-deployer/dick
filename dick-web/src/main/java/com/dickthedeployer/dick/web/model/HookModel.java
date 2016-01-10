package com.dickthedeployer.dick.web.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HookModel {

    @NotNull
    String host;
    @NotNull
    String path;
    @NotNull
    String ref;
    @NotNull
    String sha;
    @NotNull
    String lastMessage;
}
