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

import com.dickthedeployer.dick.web.model.CommandResult;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author mariusz
 */
@Slf4j
@Service
public class CommandService {

    public int invoke(Path workingDir, String... command) {
        return invokeWithEnvironment(workingDir, emptyMap(), command).getResult();
    }

    public CommandResult invokeWithEnvironment(Path workingDir, Map<String, String> environemnt, String... command) throws RuntimeException {
        try {
            log.info("Executing command {} in path {}", Arrays.toString(command), workingDir.toString());
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(workingDir.toFile());
            builder.redirectErrorStream(true);
            StringBuilder text = new StringBuilder();
            environemnt.forEach((key, value)
                    -> text.append("Setting environment variable: ").append(key).append("=").append(value).append("\n")
            );
            builder.environment().putAll(environemnt);
            Process process = builder.start();

            try (Scanner s = new Scanner(process.getInputStream())) {
                while (s.hasNextLine()) {
                    text.append(s.nextLine());
                    text.append("\n");
                }
                int result = process.waitFor();
                log.info("Process exited with result {} and output {}", result, text);

                CommandResult commandResult = new CommandResult();
                commandResult.setOutput(text.toString());
                commandResult.setResult(result);
                return commandResult;
            }
        } catch (IOException | InterruptedException ex) {
            throw Throwables.propagate(ex);
        }
    }

}
