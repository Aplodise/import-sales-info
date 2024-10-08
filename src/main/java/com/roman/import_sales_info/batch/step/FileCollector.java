package com.roman.import_sales_info.batch.step;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@Component
public class FileCollector implements Tasklet {

    @Value("${sales.info.dir}")
    private String processedDirectory;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
      log.info("-------------------> Executing the file Collector");
        Path directoryPath = Paths.get(processedDirectory + File.separator + "processed");
        try(Stream<Path> filesToDelete = Files.walk(directoryPath)){
            filesToDelete.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        return null;
    }
}
