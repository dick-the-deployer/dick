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
package com.dickthedeployer.dick.web.service;

import com.dickthedeployer.dick.web.exception.CommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static java.util.Collections.emptyMap;

/**
 *
 * @author mariusz
 */
@Slf4j
@Service
public class CommandService {

    public void invoke(Path workingDir, String... command) {
        invokeWithEnvironment(workingDir, emptyMap(), command);
    }

    public void invokeWithEnvironment(Path workingDir, Map<String, String> environment, String... command) throws RuntimeException {
        try {
            log.info("Executing command {} in path {}", Arrays.toString(command), workingDir.toString());
            StringBuilder text = new StringBuilder();
            text.append("Executing command ").append(Arrays.toString(command)).append("\n");
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(workingDir.toFile());
            builder.redirectErrorStream(true);
            environment.forEach((key, value)
                    -> text.append("Setting environment variable: ").append(key).append("=").append(value).append("\n")
            );
            builder.environment().putAll(environment);
            Process process = builder.start();

            try (Scanner s = new Scanner(process.getInputStream())) {
                while (s.hasNextLine()) {
                    text.append(s.nextLine());
                    text.append("\n");
                }
                int result = process.waitFor();
                log.info("Process exited with result {} and output {}", result, text);

                if (result != 0) {
                    throw new CommandExecutionException();
                }
            }
        } catch (IOException | InterruptedException ex) {
            throw new CommandExecutionException(ex);
        }
    }

}
