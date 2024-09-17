package com.roman.import_sales_info.batch.faulttolerance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
@Slf4j
@Component
public class CustomSkipPolicy implements SkipPolicy {
    private static final Integer SKIP_LIMIT = 10;
    @Override
    public boolean shouldSkip(Throwable exc, long skipCount) throws SkipLimitExceededException {
        if (exc instanceof FileNotFoundException){
            return Boolean.FALSE;
        } else if ((exc instanceof FlatFileParseException) && SKIP_LIMIT >= skipCount) {
            logError((FlatFileParseException) exc);
            return Boolean.TRUE;
        }
        return false;
    }

    private void logError(FlatFileParseException exc) {
        String input = exc.getInput();
        int lineNumber = exc.getLineNumber();

        log.info("The line where exception was thrown: {}, line number is: {}", input, lineNumber);
    }
}
