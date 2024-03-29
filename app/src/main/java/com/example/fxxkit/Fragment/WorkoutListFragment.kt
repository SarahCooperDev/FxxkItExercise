package com.example.fxxkit.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.WorkoutListAdapter
import com.example.fxxkit.ViewModel.WorkoutViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Shows a list of all workouts
 */
class WorkoutListFragment : Fragment() {
    private var filterSetting = 0
    private var sortSetting = 2

    private var allWorkouts: ArrayList<WorkoutViewModel> = ArrayList()
    private var workoutList: ArrayList<WorkoutViewModel> = ArrayList()

    private lateinit var randomWorkoutBtn: FloatingActionButton
    private lateinit var createWorkoutBtn : FloatingActionButton
    private lateinit var sortBtn: ImageButton
    private lateinit var searchBtn: ImageButton
    private lateinit var filterSearchBtn: ImageButton
    private lateinit var searchClearBtn: ImageButton
    private lateinit var searchEdit: EditText
    private lateinit var workoutListRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_workout_list, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.workout_list_title))

        randomWorkoutBtn = view.findViewById(R.id.random_workout_btn)
        createWorkoutBtn = view.findViewById(R.id.create_workout_btn)
        sortBtn = view.findViewById(R.id.sort_btn)
        workoutListRecycler = view.findViewById(R.id.workout_list_rv)
        searchBtn = view.findViewById(R.id.search_btn)
        filterSearchBtn = view.findViewById(R.id.filter_search_btn)
        searchClearBtn = view.findViewById(R.id.search_clear_btn)
        searchEdit = view.findViewById(R.id.search_edit)

        loadWorkouts()
        sortByReverseChrono()
        setUpSortBtn()
        setUpFilterBtn()

        workoutListRecycler.layoutManager = LinearLayoutManager(activity)
        workoutListRecycler.adapter = WorkoutListAdapter((activity as MainActivity), workoutList)
        workoutListRecycler.adapter?.notifyDataSetChanged()

        randomWorkoutBtn.setOnClickListener { view -> goToRandom() }
        createWorkoutBtn.setOnClickListener { view -> (activity as MainActivity).navToCreateWorkout() }
        searchBtn.setOnClickListener { search() }
        searchClearBtn.setOnClickListener { clearSearch() }

        return view
    }

    /**
     * Selects a random workout, and navigates to its' details page
     */
    private fun goToRandom(){
        var workoutNumber = (0..workoutList.size-1).random()
        (activity as MainActivity).navToWorkoutDetails(workoutList[workoutNumber].id)
    }

    /**
     * Searches according to the current filter
     */
    private fun search(){
        when(filterSetting){
            0 -> filterByName()
            1 -> filterByFavourite()
            2 -> filterByTags()
        }

        // Clears the keyboard
        searchEdit.clearFocus()
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    /**
     * Clears any search inputs
     */
    private fun clearSearch(){
        searchEdit.text.clear()
        searchEdit.clearFocus()
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)

        workoutList.clear()
        workoutList.addAll(allWorkouts)
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Searches for workouts that contains the given string array and displays
     */
    private fun filterByName(){
        if(searchEdit.text.toString().length > 0){
            var filteredList: ArrayList<WorkoutViewModel> = allWorkouts.filter{ it.name!!.lowercase().contains(searchEdit.text.toString().lowercase())} as ArrayList<WorkoutViewModel>
            workoutList.clear()
            workoutList.addAll(filteredList)
            workoutListRecycler.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(activity, getString(R.string.error_search_term_missing), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Shows all workouts that are favourited
     */
    private fun filterByFavourite(){
        var filteredList: ArrayList<WorkoutViewModel> = allWorkouts.filter{ it.isFavourited } as ArrayList<WorkoutViewModel>
        workoutList.clear()
        workoutList.addAll(filteredList)
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Shows all workouts that have the tag that has been searched
     */
    private fun filterByTags(){
        if(searchEdit.text.toString().length > 0){
            var filteredList: ArrayList<WorkoutViewModel> = allWorkouts.filter{ (it.tags.firstOrNull{it.name == searchEdit.text.toString().lowercase()}) != null} as ArrayList<WorkoutViewModel>
            workoutList.clear()
            workoutList.addAll(filteredList)
            workoutListRecycler.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(activity, getString(R.string.error_search_term_missing), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Sets up the search menu, that the users use to filter the workout list
     */
    private fun setUpFilterBtn(){
        filterSearchBtn.setOnClickListener {
            val filterPopup = PopupMenu(activity, filterSearchBtn)
            filterPopup.menuInflater.inflate(R.menu.workout_list_filter_menu, filterPopup.menu)
            when(filterSetting){
                0 -> filterPopup.menu.findItem(R.id.filter_name_item).setChecked(true)
                1 -> filterPopup.menu.findItem(R.id.filter_fav_item).setChecked(true)
                2 -> filterPopup.menu.findItem(R.id.filter_tags_item).setChecked(true)
            }
            filterPopup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.filter_name_item -> {
                        menuItem.setChecked(true)
                        filterSetting = 0
                        filterByName()
                    }
                    R.id.filter_fav_item -> {
                        menuItem.setChecked(true)
                        filterSetting = 1
                        filterByFavourite()
                    }
                    R.id.filter_tags_item -> {
                        menuItem.setChecked(true)
                        filterSetting = 2
                        filterByTags()
                    }
                }
                true
            }
            filterPopup.show()
        }
    }

    /**
     * Sets up the sort menu, that sorts the shown list
     */
    private fun setUpSortBtn(){
        sortBtn.setOnClickListener {
            val sortPopup = PopupMenu(activity, sortBtn)
            sortPopup.menuInflater.inflate(R.menu.workout_list_sort_menu, sortPopup.menu)
            when(sortSetting){
                0 -> sortPopup.menu.findItem(R.id.sort_alpha_item)
                1 -> sortPopup.menu.findItem(R.id.sort_reverse_alpha_item)
                2 -> { val menuView = sortPopup.menu.findItem(R.id.sort_chrono_item) }
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
     * Sorts the workouts by chronological order (by id)
     */
    private fun sortByChrono() {
        workoutList.sortBy{ it.id }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Sorts the workouts by reverse chronological order (by id)
     */
    private fun sortByReverseChrono(){
        workoutList.sortByDescending { it.id }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Sorts the workouts by alphabetical order (name)
     */
    private fun sortByAlpha(){
        workoutList.sortBy { it.name }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Sorts the workouts by reverse alphabetical order (name)
     */
    private fun sortByReverseAlpha(){
        workoutList.sortByDescending { it.name }
        workoutListRecycler.adapter?.notifyDataSetChanged()
    }

    /**
     * Loads all workouts in the database
     */
    fun loadWorkouts() {
        workoutList.clear()
        allWorkouts.clear()
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val workouts = dbHandler.getAllWorkouts()

        if(workouts != null && workouts.size > 0){
            for(workout in workouts){
                var workoutVM = WorkoutViewModel(workout.id, workout.workoutName!!)
                workoutVM.description = workout.description
                workoutVM.isFavourited = workout.isFavourited
                workoutVM.workExList = dbHandler.findAllWorkoutExercises(workout)
                workoutVM.workExList.sortBy { it.orderNo }

                for(workEx in workoutVM.workExList){
                    if(workEx.exerciseId > -1){
                        var exercise = dbHandler.findExerciseById(workEx.exerciseId)
                        workEx.exercise = exercise
                        workEx.isSelected = true
                    }
                }

                var workoutTags = dbHandler.getTagsForWorkout(workout)
                if(workoutTags != null){
                    workout.tags = workoutTags
                    workoutVM.tags = workoutTags
                }
                allWorkouts.add(workoutVM)
            }
            workoutList = allWorkouts.clone() as ArrayList<WorkoutViewModel>
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutListFragment().apply {}
    }
}