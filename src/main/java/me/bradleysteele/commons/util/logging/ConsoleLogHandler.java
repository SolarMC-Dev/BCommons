package me.bradleysteele.commons.util.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author Bradley Steele
 * @version 1.0
 *
 * @see java.util.logging.Handler
 */
public class ConsoleLogHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        StaticLog.log(ConsoleLevel.from(record.getLevel()), record.getMessage());
    }

    @Override
    public void flush() {
        // Not implemented
    }

    @Override
    public void close() throws SecurityException {
        // Not implemented
    }
}
