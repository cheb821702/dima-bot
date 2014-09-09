package com.dima.bot.util;

import org.apache.log4j.Level;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 9/9/14
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskSenderLevel extends Level {

    /**
     * Value of TaskSenderLevel level. This value is lesser than DEBUG_INT and higher
     * than TRACE_INT}
     */
    public static final int SENDER_INT = TRACE_INT - 10;

    /**
     * Level representing my log level
     */
    public static final Level SENDER = new TaskSenderLevel(SENDER_INT, "SENDER", 0);

    /**
     * @param level
     * @param levelStr
     * @param syslogEquivalent
     */
    protected TaskSenderLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

    /**
     * Checks whether logArgument is "SENDER" level. If yes then returns
     * SENDER}, else calls TaskSenderLevel#toLevel(String, Level) passing
     * it Level#DEBUG as the defaultLevel.
     */
    public static Level toLevel(String logArgument) {
        if (logArgument != null && logArgument.toUpperCase().equals("SENDER")) {
            return SENDER;
        }
        return (Level) toLevel(logArgument);
    }

    /**
     * Checks whether val is TaskSenderLevel#SENDER_INT. If yes then
     * returns CrunchifyLog4jLevel#CRUNCHIFY, else calls
     * TaskSenderLevel#toLevel(int, Level) passing it Level#DEBUG as the
     * defaultLevel
     *
     */
    public static Level toLevel(int val) {
        if (val == SENDER_INT) {
            return SENDER;
        }
        return (Level) toLevel(val, Level.DEBUG);
    }

    /**
     * Checks whether val is TaskSenderLevel#SENDER_INT. If yes
     * then returns TaskSenderLevel#SENDER, else calls Level#toLevel(int, org.apache.log4j.Level)
     *
     */
    public static Level toLevel(int val, Level defaultLevel) {
        if (val == SENDER_INT) {
            return SENDER;
        }
        return Level.toLevel(val, defaultLevel);
    }

    /**
     * Checks whether logArgument is "SENDER" level. If yes then returns
     * TaskSenderLevel#CRUNCHIFY, else calls
     * Level#toLevel(java.lang.String, org.apache.log4j.Level)
     *
     */
    public static Level toLevel(String logArgument, Level defaultLevel) {
        if (logArgument != null && logArgument.toUpperCase().equals("SENDER")) {
            return SENDER;
        }
        return Level.toLevel(logArgument, defaultLevel);
    }
}
