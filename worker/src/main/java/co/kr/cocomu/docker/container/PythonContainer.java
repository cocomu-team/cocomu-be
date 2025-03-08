package co.kr.cocomu.docker.container;

import static co.kr.cocomu.docker.DockerConstant.DEFAULT_CPUS;
import static co.kr.cocomu.docker.DockerConstant.DEFAULT_MEMORY;
import static co.kr.cocomu.docker.DockerConstant.DEFAULT_WORK_DIR;

import co.kr.cocomu.docker.DockerCommander;
import java.nio.file.Path;
import java.util.List;

public class PythonContainer {

    private static final String PYTHON_IMAGE = "python-code-executor";
    private static final String PYTHON_COMMAND_FORMAT = """
        compilation_output=$(python3 -m py_compile Main.py 2>&1); \
        compilation_status=$?; \
        echo "$compilation_output" > compile.log; \
        if [ $compilation_status -ne 0 ]; then \
            exit 1; \
        fi && \
        
        { \
            output=$(echo '%s' | timeout 2 /usr/bin/time -f "%%e\\n%%M" python3 Main.py 2>&1); \
            exit_code=$?; \
            echo "$output" > output.log; \
            if [ $exit_code -eq 124 ]; then \
                exit 124; \
            elif [ $exit_code -ne 0 ]; then \
                exit 2; \
            fi \
        }
        """;

    public static List<String> generate(final String containerId, final Path tempDir, final String input) {
        return DockerCommander.builder()
                .withRun()
                .withRemove()
                .withLimits(DEFAULT_MEMORY, DEFAULT_CPUS)
                .withSecurity()
                .withName(containerId)
                .withVolume(tempDir)
                .withWorkDir(DEFAULT_WORK_DIR)
                .withImage(PYTHON_IMAGE)
                .withCommand(String.format(PYTHON_COMMAND_FORMAT, input))
                .build();
    }
}