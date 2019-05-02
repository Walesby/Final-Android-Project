package davidwalesby.mohawk.mohawkcoursebrowser;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAsyncTask extends AsyncTask<String, Void, String> {
    private Activity passedActivity;

    public DownloadAsyncTask(Activity activity){
        this.passedActivity = activity;
    }

    /**
     * connects to the api and scrapes all the data and passes it to the on post execute
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        Log.d("log", "Starting Background Task");
        String results = "";
        try{
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int statusCode = connection.getResponseCode();
            Log.d("log", "Response Code: " + statusCode);
            if(statusCode == 200){
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = null;
                while((line = reader.readLine()) != null){
                    results += line;
                }
            }
        } catch(IOException ex){
            Toast.makeText(passedActivity,"Failed download",Toast.LENGTH_SHORT).show();
        }
        return results;
    }

    /**
     * Adds all the data from the api to the database
     * @param result
     */
    protected void onPostExecute(String result){
        Gson gson = new Gson();
        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(passedActivity);
        CourseList courseList = gson.fromJson(result, CourseList.class);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues databaseValues = new ContentValues();
        database.beginTransaction();
        try{
            for(Course course: courseList){
                databaseValues.put("program",course.program);
                databaseValues.put("semesterNum",course.semesterNum);
                databaseValues.put("courseCode",course.courseCode);
                databaseValues.put("courseTitle",course.courseTitle);
                databaseValues.put("courseDescription",course.courseDescription);
                databaseValues.put("courseOwner",course.courseOwner);
                databaseValues.put("optional",course.optional);
                databaseValues.put("hours",course.hours);
                database.insert("courseTable",null, databaseValues);
            }
            database.setTransactionSuccessful();
        }
        catch(Exception ex){
            Toast errorMessage = Toast.makeText(passedActivity, "Couldn't insert record into table", Toast.LENGTH_SHORT);
            errorMessage.show();
        }
        finally{
            database.endTransaction();
        }
        database.close();
    }
}
