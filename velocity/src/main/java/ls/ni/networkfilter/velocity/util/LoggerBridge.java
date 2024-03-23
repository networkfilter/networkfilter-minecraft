package ls.ni.networkfilter.velocity.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerBridge extends Logger {

    private final org.slf4j.Logger base;

    public LoggerBridge(org.slf4j.Logger base) {
        super("logger", null);
        this.base = base;
    }

    @Override
    public void log(LogRecord record) {
        Level level = record.getLevel();
        String msg = record.getMessage();

        if (level == Level.SEVERE)
            this.base.error(msg);
        else if (level == Level.WARNING)
            this.base.warn(msg);
        else if (level == Level.INFO)
            this.base.info(msg);
        else if (level == Level.FINE)
            this.base.debug(msg);
        else
            this.base.trace(msg);
    }
}
