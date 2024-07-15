package com.example.pockeitt.views


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.example.pockeitt.database.AppDatabase
import com.example.pockeitt.database.DatabaseBuilder
import com.example.pockeitt.models.Domain
import com.example.pockeitt.models.IncomeExpense
import com.example.pockeitt.models.RepeatType
import com.example.pockeitt.utils.CustomExpandableListAdapter
import com.example.pockeitt.utils.ListDataPump
import com.example.pockeitt.utils.Methods
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
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
    private var edAmount: TextView? = null
    var edName: TextInputEditText? = null
    private var list: MutableList<IncomeExpense>? = null
    var edNotes: TextInputEditText? = null
    var spinnerCat: AutoCompleteTextView? = null
    private lateinit var spinnerDate: TextInputEditText
    private var repeatType: RepeatType = RepeatType.NEVER
    private var domain: Domain = Domain.INCOME
    private lateinit var database: AppDatabase

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        setContentView(R.layout.activity_home)

        initViews()
        setupBottomSheet()

        MainScope().launch {
            setupExpandableListView(domain)
        }


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
        fab = findViewById(R.id.floating)

        btnRepeat = findViewById(R.id.mainlayout)
        dateButton = findViewById(R.id.calender_btn)

        expandableListView = findViewById(R.id.expandableListViewSample)
        btnWeekly = findViewById(R.id.button_weekly)
        btnMonthly = findViewById(R.id.button_monthly)


        //sheet init
        edName = sheet.findViewById(R.id.ed_name)
        edAmount = sheet.findViewById(R.id.amount_edit)
        spinnerCat = sheet.findViewById(R.id.spinner_cat)
        spinnerDate = sheet.findViewById(R.id.ed_date)
        edNotes = sheet.findViewById(R.id.ed_notes)


        database = DatabaseBuilder.getInstance(this)


        setData()


        spinnerDate.setOnFocusChangeListener { _, _ ->
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    spinnerDate.setText(selectedDate)
                }, year, month, day)

            datePickerDialog.show()
        }

        fab.setOnClickListener {
            bottomSheetBehavior!!.state = STATE_EXPANDED
        }

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
                    val name = edName!!.text.toString()
                    val amount = edAmount!!.text.toString()
                    val date = spinnerDate.text.toString()
                    val category = spinnerCat!!.text.toString()
                    val notes = edNotes!!.text.toString()

                    // Check if any field is empty
                    var isValid = true

                    if (name.isNotEmpty() || amount.isNotEmpty() || date.isNotEmpty() || category.isNotEmpty() || notes.isNotEmpty()) {
                        isValid = false
                    }



                    if (!isValid) {
                        // Don't let them collapse the bottom sheet
                        bottomSheetBehavior!!.state = STATE_EXPANDED
                        Toast.makeText(
                            applicationContext,
                            "Before saving, fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    if (notes.isNotEmpty() && category.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty() && name.isNotEmpty()) {
                        val builder = AlertDialog.Builder(this@HomeActivity)
                        builder.setTitle("Are you sure?")
                        builder.setMessage("Do you want to save this?")
                        builder.setPositiveButton("Yes") { _, _ ->
                            Toast.makeText(this@HomeActivity, "Saved", Toast.LENGTH_SHORT).show()

                            // Perform your save operation here
                            saveDataInDatabase(
                                name,
                                Methods.convertStringToDate(date) as Date,
                                category,
                                notes,
                                amount.toDouble(),
                                repeatType
                            )

                            // Clear all fields after saving
                            edName!!.text = null
                            edAmount!!.text = null
                            spinnerDate.text = null
                            spinnerCat!!.text = null
                            edNotes!!.text = null

                            Toast.makeText(applicationContext, "Saved!", Toast.LENGTH_SHORT).show()
                            builder.setNegativeButton("No") { _, _ ->
                                Toast.makeText(this@HomeActivity, "Cancelled", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            builder.show()
                        }

                        // All fields are filled, show the alert dialog to save

                    }

                }
            }

            override fun onSlide(view: View, v: Float) {
                // Handle bottom sheet sliding
            }
        })
    }


    private fun saveDataInDatabase(
        name: String,
        date: Date,
        category: String,
        notes: String,
        amount: Double,
        repeatType: RepeatType,
    ) {
        // To add an item
        ListDataPump.addItem(category, name)

        // Getting the DAO
        val incomeExpenseDao = database.incomeExpenseDao()

        val record = IncomeExpense(
            category = category,
            amount = amount,
            emoji = "",
            date = date,
            domain = domain,
            repeat = repeatType,
            notes = notes,
            name = name
        )


        // Using a coroutine to perform database operations

        MainScope().launch {
            withContext(Dispatchers.IO) {
                incomeExpenseDao.insert(record)
                withContext(Dispatchers.Main) {
                    refreshData()
                }
            }
        }


    }

    private fun refreshData() {
        MainScope().launch {
            setupExpandableListView(domain)
        }
    }


    private fun setupExpandableListView(domain: Domain) {
        MainScope().launch {

            list = withContext(Dispatchers.IO) {
                getDataFromDatabase() as MutableList<IncomeExpense>
            }

            for (i in 1..<list!!.size) {

                val categories = arrayListOf<String>()
                val catItem = list!![i].category

                if (!categories.contains(catItem))
                    categories.add(catItem)
                else
                    println(categories.toString())
            }


            expandableListDetail = ListDataPump.data
            expandableListTitle = ArrayList(expandableListDetail!!.keys)

            // Add Dummy Data
            for (key in expandableListTitle as ArrayList<String>) {
                if (expandableListDetail!![key]!!.isEmpty()) ListDataPump.addItem(
                    key,
                    "➕ Add $key here"
                )
                // if there is more than one item in the list
                // then remove add here item
            }

            expandableListAdapter =
                CustomExpandableListAdapter(
                    this@HomeActivity,
                    expandableListTitle,
                    expandableListDetail
                )
            expandableListView!!.setAdapter(expandableListAdapter)
            expandableListView!!.setOnGroupExpandListener {

            }
        }
    }

    private fun getDataFromDatabase(): List<IncomeExpense> {
        return database.incomeExpenseDao().getAllIncomeExpenses()
    }

    private fun setupTabLayout() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView: View = tab.view

                domain = when (tab.position) {
                    0 -> Domain.INCOME
                    else -> Domain.EXPENSE
                }

                updateExpandableListViews(domain)

                tabView.setBackgroundResource(R.drawable.tab_border)
                setTabTextSize(tab, 19, R.color.blue)


                Log.d("TAG SLECTED", "${tab.position} ")
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

    private fun updateExpandableListViews(domain: Domain) {

//        TODO()
        when (domain) {
            Domain.INCOME -> {
                //Write to Load Incomes
                Log.d(TAG, "Domain.INCOME: ")
            }

            Domain.EXPENSE -> {
                //Write to Load Expenses
                Log.d(TAG, "Domain.Expense: ")
            }

        }

    }

    private fun setupButtons() {
        btnMonthly!!.setOnClickListener {
            setButtonState(isMonthlySelected = true)

        }

        btnWeekly!!.setOnClickListener {
            setButtonState(isMonthlySelected = false)

        }
    }

    private fun setButtonState(isMonthlySelected: Boolean) {

        if (isMonthlySelected) {

            // If Monthly button is selected:
            // Disable the Monthly button
            btnMonthly!!.setBackgroundColor(resources.getColor(R.color.green)) // Set Monthly button background to green
            // Enable the Weekly button
            btnWeekly!!.setBackgroundColor(resources.getColor(R.color.white)) // Set Weekly button background to white
            repeatType = RepeatType.MONTHLY // Set repeat type to MONTHLY
        } else {

            // If Weekly button is selected:
            // Disable the Weekly button
            btnWeekly!!.setBackgroundColor(resources.getColor(R.color.green)) // Set Weekly button background to green
            // Enable the Monthly button
            btnMonthly!!.setBackgroundColor(resources.getColor(R.color.white)) // Set Monthly button background to white
            repeatType = RepeatType.WEEKLY // Set repeat type to WEEKLY
        }

        // Set the repeat circle image resource
        imgRepeat!!.setImageResource(R.drawable.repeat_circle_greeen)

        Log.d("repeatType", "setButtonState: $repeatType")
    }

    private fun setupTextWatcher() {
        edAmount!!.addTextChangedListener(object : TextWatcher {
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
