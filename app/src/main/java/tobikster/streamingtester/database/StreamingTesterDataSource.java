package tobikster.streamingtester.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tobikster.streamingtester.utils.Samples;

/**
 * Created by tobikster on 2016-01-03.
 */
public final
class StreamingTesterDataSource {
	private static final String[] ALL_COLUMNS = {
//			                                            StreamingTesterContract.Samples._ID,
			                                            StreamingTesterContract.Samples.COLUMN_NAME_SAMPLE_NAME,
			                                            StreamingTesterContract.Samples.COLUMN_NAME_CONTENT_ID,
			                                            StreamingTesterContract.Samples.COLUMN_NAME_URI,
			                                            StreamingTesterContract.Samples.COLUMN_NAME_SAMPLE_TYPE
	};

	private StreamingTesterDatabaseHelper mDatabaseHelper;

	public
	StreamingTesterDataSource(Context context) {
		mDatabaseHelper = new StreamingTesterDatabaseHelper(context);
	}

	public
	List<Samples.Sample> getAllSamples() {
		List<Samples.Sample> result = new ArrayList<>();
		SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = database.query(StreamingTesterContract.Samples.TABLE_NAME, ALL_COLUMNS, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			String name = cursor.getString(0);
			String contentId = cursor.getString(1);
			String uri = cursor.getString(2);
			int type = cursor.getInt(3);
			Samples.Sample sample = new Samples.Sample(name, contentId, uri, type);
			result.add(sample);
			cursor.moveToNext();
		}
		cursor.close();
		database.close();
		return result;
	}

	public
	void putDefaultSamples() {

	}
}
