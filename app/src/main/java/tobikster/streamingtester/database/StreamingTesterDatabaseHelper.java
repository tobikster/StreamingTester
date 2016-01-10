package tobikster.streamingtester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tobikster on 2016-01-03.
 */
public
class StreamingTesterDatabaseHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "StreamingTester.db";

	public
	StreamingTesterDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public
	void onCreate(SQLiteDatabase db) {

		db.execSQL(String.format("CREATE TABLE %s (", StreamingTesterContract.Samples.TABLE_NAME) +
				           String.format("%s INTEGER PRIMARY KEY, ", StreamingTesterContract.Samples._ID) +
				           String.format("%s TEXT, ", StreamingTesterContract.Samples.COLUMN_NAME_SAMPLE_NAME) +
				           String.format("%s TEXT, ", StreamingTesterContract.Samples.COLUMN_NAME_CONTENT_ID) +
				           String.format("%s TEXT, ", StreamingTesterContract.Samples.COLUMN_NAME_URI) +
				           String.format("%s INTEGER", StreamingTesterContract.Samples.COLUMN_NAME_SAMPLE_TYPE) +
				           ");");
	}

	@Override
	public
	void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", StreamingTesterContract.Samples.TABLE_NAME));
	}
}
