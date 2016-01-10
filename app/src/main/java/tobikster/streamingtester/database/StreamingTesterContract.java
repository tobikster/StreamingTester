package tobikster.streamingtester.database;

import android.provider.BaseColumns;

/**
 * Created by tobikster on 2016-01-03.
 */
public final
class StreamingTesterContract {
	private
	StreamingTesterContract() {
	}

	public static abstract class Samples implements BaseColumns {
		public static final String TABLE_NAME = "Samples";
		public static final String COLUMN_NAME_SAMPLE_NAME = "name";
		public static final String COLUMN_NAME_CONTENT_ID = "contentId";
		public static final String COLUMN_NAME_URI = "uri";
		public static final String COLUMN_NAME_SAMPLE_TYPE = "type";
	}
}
