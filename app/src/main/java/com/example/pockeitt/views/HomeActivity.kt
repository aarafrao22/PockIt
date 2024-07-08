package com.example.pockeitt.views


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pockeitt.R
import com.example.pockeitt.utils.CustomExpandableListAdapter
import com.example.pockeitt.utils.ListDataPump
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Objects

class HomeActivity : AppCompatActivity() {
    private var datePickerDialog: DatePickerDialog? = null
    private var dateButton: Button? = null
    private var expandableListView: ExpandableListView? = null
    private var expandableListAdapter: ExpandableListAdapter? = null
    private var expandableListTitle: MutableList<String>? = null
    private var expandableListDetail: HashMap<String, MutableList<String>>? = null
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout?>? = null
    private var bottomSheetShown = false

    private lateinit var sheet: ConstraintLayout
    private var btnRepeat: ConstraintLayout? = null
    private var btnWeekly: Button? = null
    private var btnMonthly: Button? = null
    private var imgRepeat: ImageView? = null
    private var textRepeat: TextView? = null
    private var textAmount: TextView? = null
    var edExpense: TextInputEditText? = null
    var edNotes: TextInputEditText? = null
    var spinnerCat: AutoCompleteTextView? = null
    var spinnerDate: AutoCompleteTextView? = null

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        setContentView(R.layout.activity_home)

        initViews()
        setupBottomSheet()
        setupExpandableListView()
        setupTabLayout()
        setupDatePicker()
        setupButtons()
        setupTextWatcher()

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            dateButton!!.text = todayDate
            insets
        }
    }

    private fun initViews() {
        sheet = findViewById(R.id.bottomsheet)
        bottomSheetBehavior = BottomSheetBehavior.from(sheet)
        bottomSheetBehavior!!.peekHeight = 140
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED

        imgRepeat = findViewById(R.id.repeat_circle)
        textRepeat = findViewById(R.id.textView2)
        btnRepeat = findViewById(R.id.mainlayout)
        textAmount = findViewById(R.id.amount_edit)
        dateButton = findViewById(R.id.calender_btn)

        expandableListView = findViewById(R.id.expandableListViewSample)
        btnWeekly = findViewById(R.id.button_weekly)
        btnMonthly = findViewById(R.id.button_monthly)


        //sheet init
        edExpense = sheet.findViewById(R.id.ed_expense)
        spinnerCat = sheet.findViewById(R.id.spinner_cat)
        spinnerDate = sheet.findViewById(R.id.spinner_date)
        edNotes = sheet.findViewById(R.id.ed_notes)


        setData()
    }

    private fun setData() {
        val type = arrayOf("Needs", "Wants", "Savings", "Bills")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, type
        )

        spinnerCat!!.setAdapter(adapter)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    val expense = Objects.requireNonNull(
                        edExpense!!.text
                    ).toString()
                    val date = spinnerDate!!.text.toString()
                    val category = spinnerCat!!.text.toString()
                    val notes = Objects.requireNonNull(edNotes!!.text).toString()

                    if (expense.isEmpty() && date.isEmpty() && category.isEmpty() && notes.isEmpty()) {
                        Toast.makeText(this@HomeActivity, "Nahi Kr sktay", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val builder = AlertDialog.Builder(this@HomeActivity)
                        builder.setTitle("Are you sure?")
                        builder.setMessage("Do you wanna save this?")
                        builder.setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                            Toast.makeText(
                                this@HomeActivity,
                                "Saved",
                                Toast.LENGTH_SHORT
                            ).show()
                            saveDataInDatabase(expense, date, category, notes)
                        }
                        builder.setNegativeButton(
                            "No"
                        ) { dialog: DialogInterface?, which: Int ->
                            Toast.makeText(
                                this@HomeActivity,
                                "Cancelled",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        builder.show()
                    }
                }
            }

            override fun onSlide(view: View, v: Float) {
                // Handle bottom sheet sliding
            }
        })
    }

    private fun saveDataInDatabase(expense: String, date: String, category: String, notes: String) {
        // To add an item
        ListDataPump.addItem(category, expense)


        refreshData()

        //        // To remove an item
//        ListDataPump.removeItem("Bills", "➕ Add Bills here");
//
//        // To update an item
//        ListDataPump.updateItem("Bills", 0, "Updated Bill Item");
//
//        // To get the list of items
//        List<String> bills = ListDataPump.getList("Bills");
//
//        // To get the entire data
//        HashMap<String, List<String>> data = ListDataPump.getData();
    }

    private fun refreshData() {
        setupExpandableListView()

        //remove all dummy items for potential lists
    }

    private fun showAlertDialogue(title: String, body: String) {
    }


    private fun setupExpandableListView() {
        expandableListDetail = ListDataPump.data
        expandableListTitle = ArrayList(expandableListDetail!!.keys)

        //Add Dummy Data
        for (key in expandableListTitle as ArrayList<String>) {
            if (expandableListDetail!![key]!!.isEmpty()) ListDataPump.addItem(
                key,
                "➕ Add $key here"
            )

            //if there is more than one items in the list
            // than remove add here item

//            else if (expandableListDetail.get(key).size() > 1) {
//                ListDataPump.removeItem(key, "➕ Add " + key + " here");
//
//            }
        }

        expandableListAdapter =
            CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail)
        expandableListView!!.setAdapter(expandableListAdapter)

        expandableListView!!.setOnGroupExpandListener { groupPosition: Int -> }
    }

    private fun setupTabLayout() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView: View = tab.view
                tabView.setBackgroundResource(R.drawable.tab_border)
                setTabTextSize(tab, 19, R.color.blue)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView: View = tab.view
                tabView.setBackgroundResource(android.R.color.transparent)
                setTabTextSize(tab, 14, R.color.black)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Handle tab reselect
            }
        })
    }

    private fun setupButtons() {
        btnMonthly!!.setOnClickListener { v: View? ->

            if (btnMonthly!!.isEnabled) {
                btnMonthly!!.isEnabled = false
                btnWeekly!!.isEnabled = true
                btnMonthly!!.setBackgroundColor(resources.getColor(R.color.white))
                btnWeekly!!.setBackgroundColor(resources.getColor(R.color.green))
                imgRepeat!!.setImageResource(R.drawable.repeat_circle_greeen)
            }
        }

        btnWeekly!!.setOnClickListener { v: View? ->
            if (btnWeekly!!.isEnabled) {
                btnWeekly!!.isEnabled = false
                btnMonthly!!.isEnabled = true
                btnWeekly!!.setBackgroundColor(resources.getColor(R.color.white))
                btnMonthly!!.setBackgroundColor(resources.getColor(R.color.green))
                imgRepeat!!.setImageResource(R.drawable.repeat_circle_greeen)
            }
        }
    }

    private fun setupTextWatcher() {
        textAmount!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Handle before text changed
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.d(ContentValues.TAG, "onTextChanged: $charSequence")
            }

            override fun afterTextChanged(editable: Editable) {
                Log.d(ContentValues.TAG, "afterTextChanged: $editable")
            }
        })
    }

    private fun setupDatePicker() {
        val dateSetListener =
            OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                var month = month
                month += 1
                val date = makeDateString(dayOfMonth, month, year)
                dateButton!!.text = date
            }

        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]

        datePickerDialog =
            DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day)
    }

    private val todayDate: String
        get() {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH] + 1
            val day = cal[Calendar.DAY_OF_MONTH]
            return makeDateString(day, month, year)
        }

    private fun makeDateString(dayOfMonth: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + dayOfMonth + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> "Jan"
        }
    }

    fun openDatePicker(view: View?) {
        datePickerDialog!!.show()
    }

    private fun showBottomSheetDialog() {

        if (!bottomSheetShown) {
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.bottomsheet)

            bottomSheetDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            bottomSheetDialog.setOnDismissListener { dialog: DialogInterface? ->
                bottomSheetShown = false
            }

            bottomSheetDialog.show()

            bottomSheetShown = true
        }
    }

    private fun setTabTextSize(tab: TabLayout.Tab, tabSizeSp: Int, textColor: Int) {
        val tabCustomView = tab.customView
        if (tabCustomView != null) {
            val tabTextView = tabCustomView.findViewById<TextView>(R.id.tabItem1)
            tabTextView.textSize = tabSizeSp.toFloat()
            tabTextView.setTextColor(ContextCompat.getColor(tabCustomView.context, textColor))
        }
    }

    private fun createCustomTabView(tabText: String, tabSizeSp: Int, textColor: Int): View {
        val tabCustomView = layoutInflater.inflate(R.layout.activity_home, null)
        val tabTextView = tabCustomView.findViewById<TextView>(R.id.tabItem1)
        tabTextView.text = tabText
        tabTextView.textSize = tabSizeSp.toFloat()
        tabTextView.setTextColor(ContextCompat.getColor(tabCustomView.context, textColor))
        return tabCustomView
    }
}
