package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.ExerciseListAdapter
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Shows a list of all exercises
 */
class ExerciseListFragment : Fragment() {
    private var filterSetting = 0
    private var sortSetting = 2
    private var areaSearchList: ArrayList<String> = ArrayList<String>()

    private var allTags: ArrayList<Tag> = ArrayList<Tag>()
    private var allExercises: ArrayList<Exercise> = ArrayList<Exercise>()
    private var exerciseList: ArrayList<Exercise> = ArrayList<Exercise>()

    private lateinit var addExerciseBtn : FloatingActionButton
    private lateinit var searchBtn: ImageButton
    private lateinit var filterSearchBtn: ImageButton
    private lateinit var searchEdit: EditText
    private lateinit var searchClearBtn: ImageButton
    private lateinit var sortBtn: ImageButton
    private lateinit var exerciseRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_exercise_list, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.exercise_list_title))

        addExerciseBtn = view.findViewById(R.id.add_exercise_btn)
        exerciseRecycler = view.findViewById(R.id.exercise_list_rv)
        sortBtn = view.findViewById(R.id.sort_btn)
        searchBtn = view.findViewById(R.id.search_btn)
        filterSearchBtn = view.findViewById(R.id.filter_search_btn)
        searchClearBtn = view.findViewById(R.id.search_clear_btn)
        searchEdit = view.findViewById(R.id.search_edit)

        addExerciseBtn.setOnClickListener { view -> (activity as MainActivity).navToAddExercise() }
        searchBtn.setOnClickListener { search() }
        searchClearBtn.setOnClickListener { clearSearch() }

        setUpSortBtn()
        setUpFilterBtn()
        loadExercises()
        sortByReverseChrono()

        exerciseRecycler.layoutManager = LinearLayoutManager(activity)
        exerciseRecycler.adapter = ExerciseListAdapter(exerciseList, activity as MainActivity)

        return view
    }

    /**
     * Decides which way to search/filter through the workouts
     */
    private fun search(){
        when(filterSetting){
            0 -> filterByName()
            1 -> filterByArea()
            2 -> filterByTags()
        }

        // Makes the keyboard go away
        searchEdit.clearFocus()
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    /**
     * Removes any filtering, showing all exercises
     */
    private fun clearSearch(){
        searchEdit.text.clear()
        searchEdit.clearFocus()
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)

        areaSearchList.clear()
        exerciseList.clear()
        exerciseList.addAll(allExercises)
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Filters the exercises, comparing name to a given search term
     */
    private fun filterByName(){
        if(searchEdit.text.toString().length > 0){
            var filteredList: ArrayList<Exercise> = allExercises.filter{ it.name!!.lowercase().contains(searchEdit.text.toString().lowercase())} as ArrayList<Exercise>
            exerciseList.clear()
            exerciseList.addAll(filteredList)
            exerciseRecycler.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(activity, getString(R.string.error_search_term_missing), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Filters the exercises by a given list of targetted areas
     */
    private fun filterByArea(){
        if(areaSearchList.size < 1){
            Toast.makeText(activity!!.baseContext, getString(R.string.error_search_target_areas_missing), Toast.LENGTH_LONG)
        } else {
            exerciseList.clear()
            var filteredList = ArrayList<Exercise>()

            // Goes through all the exercises, seeing if the exercise areas are in the search areas
            for(exercise in allExercises){
                if(exercise.targettedAreas.contains(MultiselectLists.targettedAreaArray[0])){
                    filteredList.add(exercise)
                } else {
                    for(area in exercise.targettedAreas){
                        if(areaSearchList.contains(area)){
                            filteredList.add(exercise)
                        }
                    }
                }
            }

            exerciseList.clear()
            exerciseList.addAll(filteredList)
            exerciseRecycler.adapter?.notifyDataSetChanged()
        }
    }

    /**
     * Clears the area searching changes needed for dialog support
     */
    private fun undoAreaFiltering(){
        searchEdit.setHint(getString(R.string.search_hint))
        searchEdit.inputType = InputType.TYPE_CLASS_TEXT
        searchEdit.setOnClickListener { view ->

        }
    }

    /**
     * Builds and shows a dialog that users can use to select targetted areas to search by
     * Uses:
     *  - dialog_search_areas
     */
    private fun buildAreaSelect(){
        val checkboxes = ArrayList<CheckBox>()
        val builder = AlertDialog.Builder(activity).create()
        val dialog = layoutInflater.inflate(R.layout.dialog_search_areas, null)

        val leftLayout = dialog.findViewById<LinearLayout>(R.id.left_layout)
        val rightLayout = dialog.findViewById<LinearLayout>(R.id.right_layout)
        val clearBtn = dialog.findViewById<ImageButton>(R.id.clear_btn)
        val cancelBtn = dialog.findViewById<ImageButton>(R.id.cancel_btn)
        val doneBtn = dialog.findViewById<ImageButton>(R.id.done_btn)

        var isLeft = true
        for(i in 1..MultiselectLists.targettedAreaArray.size-1){
            var item = MultiselectLists.targettedAreaArray[i]
            var chkbx = CheckBox(activity)
            chkbx.setText(item)
            chkbx.setPadding(5, 5, 5, 5)
            chkbx.setOnClickListener { view ->
               areaSearchList.add(item)
            }

            if(areaSearchList.contains(item)){
                chkbx.isChecked = true
            }

            if(isLeft){
                isLeft = false
                leftLayout.addView(chkbx)
            } else {
                isLeft = true
                rightLayout.addView(chkbx)
            }
            checkboxes.add(chkbx)
        }

        doneBtn.setOnClickListener { view ->
            var textString = ""
            for(item in areaSearchList){
                textString += item + ", "
            }

            textString = textString.dropLast(2)
            searchEdit.setText(textString)

            builder.dismiss()
        }

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }

        clearBtn.setOnClickListener { view ->
            searchEdit.setText("")
            areaSearchList.clear()
            for(chk in checkboxes){
                chk.isChecked = false
            }
        }

        builder.setView(dialog)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    /**
     * Changes the search edit to a button that triggers a popup
     */
    private fun setUpAreaFiltering(){
        searchEdit.setHint(getString(R.string.choose_areas_hint))
        searchEdit.inputType = InputType.TYPE_NULL
        searchEdit.setOnClickListener { view -> buildAreaSelect() }

        // Triggers the dialog to show immediately
        buildAreaSelect()
    }

    /**
     * Filters the exercise list by given tags
     */
    private fun filterByTags(){
        if(searchEdit.text.toString().length > 0){
            var filteredList: ArrayList<Exercise> = allExercises.filter{ (it.tags.firstOrNull{it.name == searchEdit.text.toString().lowercase()}) != null} as ArrayList<Exercise>
            exerciseList.clear()
            exerciseList.addAll(filteredList)
            exerciseRecycler.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(activity, getString(R.string.error_search_term_missing), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Sets up the filtering button menu; shows a drop down menu of available filtering methods
     */
    private fun setUpFilterBtn(){
        filterSearchBtn.setOnClickListener {
            val filterPopup = PopupMenu(activity, filterSearchBtn)
            filterPopup.menuInflater.inflate(R.menu.exercise_list_filter_menu, filterPopup.menu)
            when(filterSetting){
                0 -> filterPopup.menu.findItem(R.id.filter_name_item).setChecked(true)
                1 -> filterPopup.menu.findItem(R.id.filter_area_item).setChecked(true)
                2 -> filterPopup.menu.findItem(R.id.filter_tags_item).setChecked(true)
            }
            filterPopup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.filter_name_item -> {
                        filterSetting = 0
                        undoAreaFiltering()
                        filterByName()
                    } R.id.filter_area_item -> {
                        filterSetting = 1
                        setUpAreaFiltering()
                    } R.id.filter_tags_item -> {
                        filterSetting = 2
                        undoAreaFiltering()
                        filterByTags()
                    }
                }
                true
            }
            filterPopup.show()
        }
    }

    /**
     * Sets up the sort button, shows a drop-down menu of sorting options, that sort the exercises on click
     */
    private fun setUpSortBtn(){
        sortBtn.setOnClickListener {
            val sortPopup = PopupMenu(activity, sortBtn)
            sortPopup.menuInflater.inflate(R.menu.workout_list_sort_menu, sortPopup.menu)
            when(sortSetting){
                0 -> sortPopup.menu.findItem(R.id.sort_alpha_item)
                1 -> sortPopup.menu.findItem(R.id.sort_reverse_alpha_item)
                2 -> { val menuView = sortPopup.menu.findItem(R.id.sort_chrono_item)  }
                3 -> sortPopup.menu.findItem(R.id.sort_reverse_chrono_item)
            }

            sortPopup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.sort_alpha_item -> { sortByAlpha() }
                    R.id.sort_reverse_alpha_item -> { sortByReverseAlpha() }
                    R.id.sort_chrono_item -> { sortByChrono() }
                    R.id.sort_reverse_chrono_item -> { sortByReverseChrono() }
                }
                true
            }
            sortPopup.show()
        }
    }

    /**
     * Sorts the exercises by chronological order (the exericse ids)
     */
    private fun sortByChrono() {
        exerciseList.sortBy{ it.id }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Sorts the exercises by reverse chronological order (the exercise ids)
     */
    private fun sortByReverseChrono(){
        exerciseList.sortByDescending { it.id }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Sorts the exercises by alphabetical order (the exercise name)
     */
    private fun sortByAlpha(){
        exerciseList.sortBy { it.name }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Sorts the exercises by reverse alphabetical order (the exercise name)
     */
    private fun sortByReverseAlpha(){
        exerciseList.sortByDescending { it.name }
        exerciseRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Loads all the exercises from the database
     */
    private fun loadExercises() {
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val retrievedList = dbHandler.getAllExercises()

        if(retrievedList != null && retrievedList.size > 0){
            for(exercise in retrievedList){
                var exerciseTags = dbHandler.getTagsForExercise(exercise)
                if(exerciseTags != null){
                    exercise.tags = exerciseTags
                }
            }

            allExercises = retrievedList
            exerciseList = allExercises.clone() as ArrayList<Exercise>
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ExerciseListFragment().apply { }
    }
}