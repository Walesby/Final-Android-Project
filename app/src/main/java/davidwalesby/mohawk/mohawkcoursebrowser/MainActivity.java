package davidwalesby.mohawk.mohawkcoursebrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * I, David Walesby, 000732130 certify that this material is my original work.
 * No other person's work has been used without due acknowledgement.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MyDatabaseHelper databaseHelper = new MyDatabaseHelper(this);
    Spinner semesterChoiceSpinner;
    Button viewCourse;
    String semesterSelected;
    TextView semesterPrompt;
    Button refreshDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
        retrieveData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        semesterChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semesterSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "You must select a semester", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hides display elements and initializes the global variables
     */
    public void initializeVariables(){
        semesterChoiceSpinner = (Spinner) findViewById(R.id.semesterChoiceSpinner);
        viewCourse = (Button) findViewById(R.id.viewCoursesButton);
        semesterPrompt = (TextView) findViewById(R.id.selectMessageTextView);
        refreshDatabase = (Button) findViewById(R.id.refreshDatabaseButton);

        //at the start of the program make these objects invisible
        semesterChoiceSpinner.setVisibility(View.INVISIBLE);
        viewCourse.setVisibility(View.INVISIBLE);
        semesterPrompt.setVisibility(View.INVISIBLE);
    }

    /**
     * On startup it will call the DownloadAsyncTask class to grab all the info from the api
     */
    public void retrieveData(){
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(this);
        String uri = "https://csunix.mohawkcollege.ca/~geczy/mohawkprograms.php";
        downloadAsyncTask.execute(uri);
    }

    /**
     * Drops the current database table and repopulates it with the most up to date info from the api
     * @param view
     */
    public void updateDatabase(View view){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.delete("courseTable", null,null);
        retrieveData();
        invalidateOptionsMenu();
    }

    /**
     * Responsible for opening the course list when the user presses the view courses button
     * @param view
     */
    public void openCourseList(View view){
        Intent courseBrowser = new Intent(MainActivity.this, CourseBrowser.class);
        courseBrowser.putExtra("semesterChoice", semesterSelected);
        startActivity(courseBrowser);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    /**
     * When a user selects an item in the navigation bar it will run a query to get all the semesters that
     * the chosen course has and places them in the spinner and makes the spinner for the semester choice visible
     * and makes the view course button visible
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String programChoice = item.getTitle().toString();
        SharedPreferences preferences = getPreferences(0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("programChoice", programChoice);
        editor.commit();
        Cursor allSemestersCursor = database.rawQuery("SELECT DISTINCT semesterNum " +
                                                           "FROM courseTable " +
                                                           "WHERE program = ? " +
                                                           "ORDER BY semesterNum ASC", new String[]{programChoice});
        ArrayList<Integer> semesterNumberArrayList = new ArrayList<>();
        while(allSemestersCursor.moveToNext()){
            int semesterNumber = allSemestersCursor.getInt(allSemestersCursor.getColumnIndex("semesterNum"));
            semesterNumberArrayList.add(semesterNumber);
        }
        allSemestersCursor.close();
        ArrayAdapter<Integer> semesterChoiceSpinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,semesterNumberArrayList);
        semesterChoiceSpinnerAdapter.setDropDownViewResource(R.layout.spinner_row);
        Spinner semesterSpinner = (Spinner) findViewById(R.id.semesterChoiceSpinner);
        semesterSpinner.setAdapter(semesterChoiceSpinnerAdapter);
        semesterChoiceSpinner.setVisibility(View.VISIBLE);
        viewCourse.setVisibility(View.VISIBLE);
        semesterPrompt.setVisibility(View.VISIBLE);
        refreshDatabase.setVisibility(View.INVISIBLE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
