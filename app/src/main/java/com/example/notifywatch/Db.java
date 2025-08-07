package com.example.notifywatch;

public final class Db {

    private Db() {}

    public static class Entry {
        public static final String TABLE_NAME = "Notification";
        public static final String COLUMN_ID = "NotificationID";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_TEXT = "Text";
        public static final String COLUMN_TIME = "Time";
    }
}
