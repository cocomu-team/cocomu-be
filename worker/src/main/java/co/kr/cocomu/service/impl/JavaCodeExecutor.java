package co.kr.cocomu.service.impl;

import co.kr.cocomu.docker.container.JavaContainer;
import co.kr.cocomu.dto.CodeExecutionMessage;
import co.kr.cocomu.dto.EventMessage;
import co.kr.cocomu.dto.ExecutionMessage;
import co.kr.cocomu.service.CodeExecutor;
import co.kr.cocomu.service.DockerExecutor;
import co.kr.cocomu.utils.CodeFileManager;
import co.kr.cocomu.utils.TempDirectoryManager;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaCodeExecutor implements CodeExecutor {

    private final static String FILE_NAME = "Main.java";

    @Override
    public EventMessage<ExecutionMessage> execute(final CodeExecutionMessage message) {
        final String containerId = "java-exec-" + UUID.randomUUID();
        Path tempDir = null;

        try {
            tempDir = TempDirectoryManager.createTempDirectory();
            CodeFileManager.createFile(tempDir, FILE_NAME, message.code());
            final List<String> dockerCommand = JavaContainer.generate(containerId, tempDir, message.input().trim());
            log.info("Executing code in container: {}", containerId);

            return DockerExecutor.execute(dockerCommand, message.tabId(), tempDir);
        } catch (final Exception e) {
            return EventMessage.createErrorMessage(message.tabId(), e.getMessage());
        } finally {
            TempDirectoryManager.cleanup(tempDir);
        }
    }

}
