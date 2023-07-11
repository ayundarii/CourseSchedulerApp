package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var viewModel: AddCourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.title = getString(R.string.add_course)

        val factory = AddCourseViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]


        setupTimePicker()

        viewModel.saved.observe(this) {
            if (it.getContentIfNotHandled() == true) {
                onBackPressed()
            } else {
                Toast.makeText(applicationContext, "Please choose a time.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                insertCourse()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun insertCourse() {
        val courseName = findViewById<TextView>(R.id.add_ed_course_name).text.trim().toString()
        val day = findViewById<Spinner>(R.id.spinner_day).selectedItemPosition
        val startTime = findViewById<TextView>(R.id.add_tv_start_time).text.trim().toString()
        val endTime = findViewById<TextView>(R.id.add_tv_end_time).text.trim().toString()
        val lecturer = findViewById<TextInputEditText>(R.id.add_ed_lecture).text?.trim().toString()
        val note = findViewById<TextInputEditText>(R.id.add_ed_note).text?.trim().toString()

        if (courseName.isNotEmpty() && startTime.isNotEmpty()
            && endTime.isNotEmpty() && lecturer.isNotEmpty()
            && note.isNotEmpty()) {

            viewModel.insertCourse(
                courseName,
                day,
                startTime,
                endTime,
                lecturer,
                note
            )

            Toast.makeText(applicationContext, "New course schedule added.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(applicationContext, "Make sure you have included every detail.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupTimePicker() {
        findViewById<ImageButton>(R.id.btn_add_start_time).setOnClickListener {
            val dialogTimePicker = TimePickerFragment()
            dialogTimePicker.show(supportFragmentManager, START_TIME_PICKER)
        }

        findViewById<ImageButton>(R.id.btn_add_end_time).setOnClickListener {
            val dialogTimePicker = TimePickerFragment()
            dialogTimePicker.show(supportFragmentManager, END_TIME_PICKER)
        }
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        if (tag == START_TIME_PICKER)
            findViewById<TextView>(R.id.add_tv_start_time).text = dateFormat.format(calendar.time)
        else
            findViewById<TextView>(R.id.add_tv_end_time).text = dateFormat.format(calendar.time)
    }

    companion object {
        private const val START_TIME_PICKER = "start_time_picker"
        private const val END_TIME_PICKER = "end_time_picker"
    }
}