package com.example.nouno.locateme.Data;

import android.provider.BaseColumns;

/**
 * Created by nouno on 14/08/2017.
 */

public final class LocalsContract {
    private LocalsContract() {}

    public static class ClassroomsEntry implements BaseColumns {
        public static final String TABLE_NAME = "classrooms";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_ID_STRUCTURE = "id_structure";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_TAGS = "tags";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
    }

    public static class StructuresEntry implements BaseColumns {
        public static final String TABLE_NAME = "structures";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_TAGS = "tags";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
    }

    public static class CenterOfInterestsEntry implements BaseColumns {
        public static final String TABLE_NAME = "center_of_interests";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_ID_STRUCTURE = "id_structure";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_TAGS = "tags";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";

        public static final long NO_STRUCTURE = -1;
    }
}
