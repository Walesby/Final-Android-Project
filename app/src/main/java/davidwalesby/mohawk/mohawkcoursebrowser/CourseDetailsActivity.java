package davidwalesby.mohawk.mohawkcoursebrowser;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CourseDetailsActivity extends AppCompatActivity {
    MyDatabaseHelper databaseHelper = new MyDatabaseHelper(this);
    ListView courseDetailsListView;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_details);
        displayDetails();
    }


    public void displayDetails(){
        String requirement;
        Intent userChoice = getIntent();
        String chosenCourse = userChoice.getStringExtra("selectedCourse");
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor getDetails = database.rawQuery("SELECT DISTINCT courseDescription, courseOwner, " +
                "optional, hours from courseTable WHERE _id = ?", new String[]{chosenCourse});

        ArrayList<String> courseDetails = new ArrayList<>();
        courseDetails.add("COURSE DETAILS");
        while(getDetails.moveToNext()){
            String courseDescription = getDetails.getString(getDetails.getColumnIndex("courseDescription"));
            String courseOwner = getDetails.getString(getDetails.getColumnIndex("courseOwner"));
            int optionalCourse = getDetails.getInt(getDetails.getColumnIndex("optional"));
            if(optionalCourse == 1){
                requirement = "Elective";
            }
            else{
                requirement = "Required";
            }
            int weeklyHours = getDetails.getInt(getDetails.getColumnIndex("hours"));
            courseDetails.add("Description: \n" + courseDescription + "\n" + "Owner: " + courseOwner
                    +  "\n" + "\n" + "Optional/Required: " + requirement +  "\n"  + "\n" + "Hours: " + weeklyHours + "hr/ Week" + "\n"+ "\n");
        }
        getDetails.close();
        courseDetailsListView = findViewById(R.id.courseDetailsListView);
        ArrayAdapter<String> detailList = new ArrayAdapter(this,android.R.layout.simple_list_item_1, courseDetails);
        courseDetailsListView.setAdapter(detailList);
    }
}
