package by.bsu.famcs.lab6lastfm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Collection;

import de.umass.lastfm.*;

public class MainActivity extends AppCompatActivity {

    private MyAsyncTask mt;
    private final int DB_VERSION = 1;
    private final String TABLE_NAME = "ARTISTS";
    private DBHelper dbHelper;
    private EditText etInfo;
    private final String LISTENERS_COLUMN = "listeners";
    private int showed = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etInfo = (EditText) findViewById(R.id.etInfo);

        dbHelper = new DBHelper(this);
        mt = new MyAsyncTask();
        if (isOnline()){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("drop table if exists " + TABLE_NAME);
            db.execSQL("create table " + TABLE_NAME + " ("
                    + "id integer primary key autoincrement, "
                    + "artist_name text, "
                    + "track_name text, "
                    + "listeners integer, "
                    + "play_count integer);");
            dbHelper.close();
            mt.execute();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onClick(View view) {
        if(!isOnline() && showed == 1) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No internet connection! Starting in autonomous mode.", Toast.LENGTH_LONG);
            toast.show();
            showed++;
        }
        etInfo.setText("");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int artistNameColIndex = c.getColumnIndex("artist_name");
            int trackNameColIndex = c.getColumnIndex("track_name");
            int playCountColIndex = c.getColumnIndex("play_count");
            int listenersColIndex = c.getColumnIndex(LISTENERS_COLUMN);
            do {
                etInfo.setText(etInfo.getText().toString() + "ID = " + c.getInt(idColIndex) +
                                  ", artist_name = " + c.getString(artistNameColIndex) +
                                  ", track_name = " + c.getString(trackNameColIndex) +
                                  ", play_count = " + c.getInt(playCountColIndex) +
                                  ", listeners = " + c.getInt(listenersColIndex) + "\n\n");
            } while (c.moveToNext());
        } else
            etInfo.setText("0 rows");
        c.close();
        dbHelper.close();
    }

    class MyAsyncTask extends AsyncTask<String, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... params) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Caller.getInstance().setCache(null);
            String user = "joma42";
            Caller.getInstance().setUserAgent("tst");
            String key = "db8657b525f5956a1fae83caf37e9091";

            Collection<Track> tracks = Artist.getTopTracks("Queen", key);
            Track[] aTracks = new Track[50];
            tracks.toArray(aTracks);

            for (int i = 0; i < 10;i++) {
                ContentValues cv = new ContentValues();
                cv.put("artist_name", aTracks[i].getArtist());
                cv.put("track_name", aTracks[i].getName());
                cv.put("play_count", aTracks[i].getPlaycount());
                cv.put(LISTENERS_COLUMN, aTracks[i].getListeners());
                try {
                    long rowID = db.insertOrThrow(TABLE_NAME, "track_name", cv);
                }catch(SQLException e)
                {
                    Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
                    e.printStackTrace();
                }
            }

            tracks = Artist.getTopTracks("Metallica", key);
            tracks.toArray(aTracks);
            for (int i = 0; i < 10;i++) {
                ContentValues cv = new ContentValues();
                cv.put("artist_name", aTracks[i].getArtist());
                cv.put("track_name", aTracks[i].getName());
                cv.put("play_count", aTracks[i].getPlaycount());
                cv.put(LISTENERS_COLUMN, aTracks[i].getListeners());
                try {
                    long rowID = db.insertOrThrow(TABLE_NAME, "track_name", cv);
                }catch(SQLException e)
                {
                    Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
                    e.printStackTrace();
                }
            }

            tracks = Artist.getTopTracks("Powerwolf", key);
            tracks.toArray(aTracks);

            for (int i = 0; i < 10;i++) {
                ContentValues cv = new ContentValues();
                cv.put("artist_name", aTracks[i].getArtist());
                cv.put("track_name", aTracks[i].getName());
                cv.put("play_count", aTracks[i].getPlaycount());
                cv.put(LISTENERS_COLUMN, aTracks[i].getListeners());
                try {
                    long rowID = db.insertOrThrow(TABLE_NAME, "track_name", cv);
                }catch(SQLException e)
                {
                    Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
                    e.printStackTrace();
                }
            }

            dbHelper.close();
            return null;
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
        }
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + " ("
                    + "id integer primary key autoincrement, "
                    + "artist_name text, "
                    + "track_name text, "
                    + "listeners integer, "
                    + "play_count integer);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL( "DROP ТАВLЕ IF EXISTS " + TABLE_NAME );
            onCreate(db);
        }
    }
}
