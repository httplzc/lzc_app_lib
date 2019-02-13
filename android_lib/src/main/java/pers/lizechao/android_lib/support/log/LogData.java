package pers.lizechao.android_lib.support.log;

import java.io.File;

/**
 * Created by Lzc on 2018/4/3 0003.
 */

public class LogData {
    public final String log;
    private final LogType logType;

    private File logFile;


    private LogData(LogType logType, String log) {
        this.logType = logType;
        this.log = log;
        logFile = LogUtil.getOriginLogFile(logType);
    }


    public String getLog() {
        return log;
    }


    public static class Builder {
        private StringBuilder stringBuilder;
        private final LogType logType;

        public Builder(LogType logType) {
            this.logType = logType;
        }


        public Builder addData(String data) {
            if (data.endsWith("\n"))
                data = data.substring(0, data.length() - 1);
            data = data.replaceAll("\n", LogUtil.EMPTY + "\n");
            if (stringBuilder == null) {
                stringBuilder = new StringBuilder()
                  .append(LogUtil.BR)
                  .append(LogUtil.BR)
                  .append(LogUtil.BR)
                  .append(LogUtil.BR)
                  .append(LogUtil.BR)
                  .append(LogUtil.BR)
                  .append(LogUtil.TOP_BORDER)
                  .append(LogUtil.EMPTY)
                  .append(data);
            } else {
                stringBuilder.append(LogUtil.BR)
                  .append(LogUtil.MIDDLE_BORDER)
                  .append(LogUtil.EMPTY)
                  .append(data);
            }
            return this;
        }


        public LogData build() {
            stringBuilder.append(LogUtil.BR);
            stringBuilder.append(LogUtil.BOTTOM_BORDER);
            LogData logData = new LogData(logType, stringBuilder.toString());
            stringBuilder = null;
            return logData;
        }
    }

    public File getLogFile() {
        return logFile;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }


}
