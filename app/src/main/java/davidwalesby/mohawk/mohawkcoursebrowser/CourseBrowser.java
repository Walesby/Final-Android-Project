package davidwalesby.mohawk.mohawkcoursebrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CourseBrowser extends AppCompatActivity implements ListView.OnItemClickListener{
    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
    SimpleCursorAdapter cursorAdapter;
    ListView courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_browser);
        displayCourses();
    }

    /**
     * When a user clicks an item in the list it will open a description of that item in a new activity
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent courseDetails = new Intent(CourseBrowser.this, CourseDetailsActivity.class);
        Cursor cursor = (Cursor) cursorAdapter.getItem(position);
        int getCourseId = cursor.getInt(cursor.getColumnIndex("_id"));
        courseDetails.putExtra("selectedCourse", String.valueOf(getCourseId));
        startActivity(courseDetails);
    }

    /**
     * Displays all the courses based on what the user selected when they pressed the view courses button in the main activity
     */
    public void displayCourses(){
        courseList = findViewById(R.id.coursesListView);
        Intent semesterSelection = getIntent();
        SharedPreferences preferences = getPreferences(0);
        String semesterChoice = semesterSelection.getStringExtra("semesterChoice");
        String programChoice = preferences.getString("programChoice", "555");
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        Cursor courseSelection = database.rawQuery("SELECT _id, courseCode , courseTitle " +
                "FROM courseTable WHERE semesterNum = ?  and program = ? GROUP BY courseCode", new String[]{semesterChoice,programChoice});
        String[] fromColumns = {"courseCode", "courseTitle"};
        int[] toDisplay = {R.id.courseCode, R.id.courseName};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_row, courseSelection, fromColumns, toDisplay, 0);
        courseList.setAdapter(cursorAdapter);
        courseList.setOnItemClickListener(this);
    }
}
