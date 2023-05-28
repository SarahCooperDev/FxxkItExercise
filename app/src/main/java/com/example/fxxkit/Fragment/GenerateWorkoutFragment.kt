package com.example.fxxkit.Fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.WorkoutExercise
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import com.example.fxxkit.ViewHolder.SelectWorkoutExerciseListAdapter

/**
 * Takes user input to algorithmically select exercises for a generated workout
 */
class GenerateWorkoutFragment : Fragment() {
    private lateinit var durationInput: EditText
    private lateinit var areaTargetTxt: TextView
    private lateinit var areaExcludeTxt: TextView
    private lateinit var strengthChkbx: CheckBox
    private lateinit var conditionChkbx: CheckBox
    private lateinit var excludeExTxt: TextView
    private lateinit var generateBtn: ImageButton
    private lateinit var cancelBtn: ImageButton

    private var allExercises: ArrayList<Exercise> = ArrayList<Exercise>()
    private var allWorkoutExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    private var targettedAreas: ArrayList<String> = ArrayList<String>()
    private var excludedAreas: ArrayList<String> = ArrayList<String>()
    var selectedWorkExes = ArrayList<WorkoutExercise>()
    var workoutSelectedExercises: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_generate_workout, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.generate_workout_title))

        allExercises.clear()
        allWorkoutExercises.clear()
        selectedWorkExes.clear()
        workoutSelectedExercises.clear()

        loadExercises(view)
        loadExercisesIntoWorkout()

        durationInput = view.findViewById(R.id.duration_input)
        areaTargetTxt = view.findViewById(R.id.target_area_txt)
        areaExcludeTxt = view.findViewById(R.id.area_exclude_txt)
        strengthChkbx = view.findViewById(R.id.strength_chkbx)
        conditionChkbx = view.findViewById(R.id.condition_chkbx)
        excludeExTxt = view.findViewById(R.id.exclude_ex_txt)
        cancelBtn = view.findViewById(R.id.cancel_btn)
        generateBtn = view.findViewById(R.id.generate_btn)

        areaTargetTxt.setOnClickListener { view -> buildTargetAreasDialog() }
        areaExcludeTxt.setOnClickListener { view -> buildExcludeAreasDialog() }
        excludeExTxt.setOnClickListener { view -> buildExcludeExerciseDialog() }

        generateBtn.setOnClickListener { view ->
            generateWorkout()
            (activity as MainActivity).navToSuggestedWorkout(workoutSelectedExercises)
        }

        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToPrevious()
        }

        return view
    }

    /**
     * Algorithmically selects random exercises that meet the given criteria
     */
    private fun generateWorkout(){
        // Removes the exercises the user explicitly excludes
        for(exercise in selectedWorkExes){
            allWorkoutExercises.removeAll{it.exerciseId == exercise.exerciseId}
        }

        var filteredWorkExes: ArrayList<WorkoutExercise> = allWorkoutExercises.clone() as ArrayList<WorkoutExercise>

        if(excludedAreas.size == 0 && targettedAreas.size == 0){
            // User has not put any filters on regarding targetted/excluded areas
        } else if(targettedAreas.size == 0){
            // User has excluded some areas; removes exercises that contain those areas
            for(workEx in filteredWorkExes){
                if(doesStringListContainListItem(workEx.exercise!!.targettedAreas, excludedAreas)){
                    allWorkoutExercises.remove(workEx)
                }
            }
        } else if(excludedAreas.size == 0) {
            // User has focused on some areas; removes exercises that don't have any of those areas
            for(workEx in filteredWorkExes){
                if(!doesStringListContainListItem(workEx.exercise!!.targettedAreas, targettedAreas)){
                    allWorkoutExercises.remove(workEx)
                }
            }
        } else if(excludedAreas.size > 0 && targettedAreas.size > 0){
            // User has focused on some areas, and excluded others; removes all not focused, and all that have the excluded
            for(workEx in filteredWorkExes){
                if(!doesStringListContainListItem(workEx.exercise!!.targettedAreas, targettedAreas)){
                    allWorkoutExercises.remove(workEx)
                } else if(doesStringListContainListItem(workEx.exercise!!.targettedAreas, excludedAreas)){
                    allWorkoutExercises.remove(workEx)
                }
            }
        }

        filteredWorkExes = allWorkoutExercises.clone() as ArrayList<WorkoutExercise>

        if(strengthChkbx.isChecked && conditionChkbx.isChecked){
            // User requires all exercises be both strengthening and conditioning; filters out all exercises that aren't both
            for(workEx in filteredWorkExes){
                if(!workEx.exercise!!.isStrengthening && !workEx.exercise!!.isConditioning){
                    allWorkoutExercises.remove(workEx)
                }
            }
        } else if(strengthChkbx.isChecked){
            // User requires all exercises be strengthening; filters out all exercises that aren't
            for(workEx in filteredWorkExes){
                if(!workEx.exercise!!.isStrengthening){
                    allWorkoutExercises.remove(workEx)
                }
            }
        } else if(conditionChkbx.isChecked){
            // User requires all exercises be conditioning; filters out all exercises that aren't
            for(workEx in filteredWorkExes){
                if(!workEx.exercise!!.isConditioning){
                    allWorkoutExercises.remove(workEx)
                }
            }
        } else {
            // User requires that all exercises be neither strengthening nor conditioning; filters out all exercises that are either
            for(workEx in filteredWorkExes){
                if(workEx.exercise!!.isStrengthening || workEx.exercise!!.isConditioning){
                    allWorkoutExercises.remove(workEx)
                }
            }
        }

        filteredWorkExes = allWorkoutExercises.clone() as ArrayList<WorkoutExercise>

        // Calculates how long the workout should last for
        var targetTime = durationInput.text.toString().toInt() * 60
        var runningTime = 0
        var random = 0

        // Loops through remaining exercises, selecting ones at random to add to the workout
        while(runningTime < targetTime && filteredWorkExes.size > 0){
            random = (0..filteredWorkExes.size - 1).random()

            var workEx = filteredWorkExes[random]
            filteredWorkExes.removeAt(random)

            // Randomly selects the set and rep sizes for the workout (weighted)
            var setSize = 0
            var repSize = 0
            if(workEx.exercise!!.possibleSetSize.size < 1 || workEx.exercise!!.possibleSetSize.contains(MultiselectLists.setSizesArray[0])){
                var weightedList = getWeigtedList(MultiselectLists.getSetAsArrayList())
                setSize = weightedList[(0..weightedList.size-1).random()].toInt()
            } else {
                var weightedList = getWeigtedList(workEx.exercise!!.possibleSetSize)
                setSize = weightedList[(0..weightedList.size-1).random()].toInt()
            }

            if(workEx.exercise!!.possibleRepSize.size < 1 || workEx.exercise!!.possibleRepSize.contains(MultiselectLists.repSizesArray[0])){
                var weightedList = getWeigtedList(MultiselectLists.getRepAsArrayList())
                repSize = weightedList[(0..weightedList.size-1).random()].toInt()
            } else {
                var weightedList = getWeigtedList(MultiselectLists.getRepAsArrayList())
                repSize = weightedList[(0..weightedList.size-1).random()].toInt()
            }

            workEx.setSize = setSize.toString()
            workEx.repSize = repSize.toString()
            workEx.totalTime = setSize * repSize * workEx.exercise!!.repTime

            // Adds the workoutexercise, and calculates the total time already in the workout
            workoutSelectedExercises.add(workEx)
            runningTime += workEx.totalTime
        }
    }

    /**
     * Takes a list (sets or reps), and returns one weighted to make smaller values more likely to be randomly selected (via duplicates)
     */
    private fun getWeigtedList(sizeList: ArrayList<String>): ArrayList<String>{
        var weightedList = ArrayList<String>()

        for(i in 0..((sizeList.size-1)*0.2).toInt()){
            weightedList.add(sizeList[i])
        }
        for(i in 0..((sizeList.size-1)*0.4).toInt()){
            weightedList.add(sizeList[i])
        }
        for(i in 0..((sizeList.size-1)*0.6).toInt()){
            weightedList.add(sizeList[i])
        }
        for(i in 0..((sizeList.size-1)*0.8).toInt()){
            weightedList.add(sizeList[i])
        }
        for(i in 0..sizeList.size-1){
            weightedList.add(sizeList[i])
        }
        return weightedList
    }

    /**
     * Determines if a list contains any of the values in another list
     */
    private fun doesStringListContainListItem(list1: ArrayList<String>, list2: ArrayList<String>): Boolean{
        for(item in list1){
            if(list2.contains(item)){
                return true
            }
        }
        return false
    }

    /**
     * Loads exercise objects into WorkoutExercises, so sets/reps can be set
     */
    private fun loadExercisesIntoWorkout(){
        for(exercise in allExercises){
            var workEx = WorkoutExercise(exercise)
            workEx.exercise = exercise
            allWorkoutExercises.add(workEx)
        }
    }

    /**
     * Builds and shows a dialog that is a list of selectable exercises, that the user can choose to exclude
     * Uses:
     *  - dialog_select_exercises
     *  - SelectWorkoutExerciseListAdapter
     */
    fun buildExcludeExerciseDialog(){
        val builder = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.dialog_select_exercises, null)
        var exerciseRV = view.findViewById<RecyclerView>(R.id.exercise_rv)
        exerciseRV.layoutManager = LinearLayoutManager(activity)
        var excludedExercisesAdapter = SelectWorkoutExerciseListAdapter((activity as MainActivity), allWorkoutExercises)
        exerciseRV.adapter = excludedExercisesAdapter

        var doneBtn = view.findViewById<Button>(R.id.done_btn)
        var cancelBtn = view.findViewById<Button>(R.id.cancel_btn)

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }
        doneBtn.setOnClickListener { view ->
            for(workExes in allWorkoutExercises){
                if(workExes.isSelected){
                    selectedWorkExes.add(workExes)
                }
            }
            var excludeTxt = "[None]"
            if(selectedWorkExes.size > 0){
                excludeTxt = ""
                for(workEx in selectedWorkExes){
                    excludeTxt += "- " + workEx.exercise?.name + "\n"
                }
            }

            excludeExTxt.setText(excludeTxt)

            builder.dismiss()
        }

        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    /**
     * Builds and shows a dialog that allows uses to select areas to target
     * Uses:
     *  - custom_dialog_areas
     */
    fun buildTargetAreasDialog(){
        val builder = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.custom_dialog_areas, null)
        var checkLayout = view.findViewById<LinearLayout>(R.id.checkbox_layout)
        var checkLayout2 = view.findViewById<LinearLayout>(R.id.checkbox_layout_2)
        var clearBtn = view.findViewById<Button>(R.id.clear_btn)
        var cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        var doneBtn = view.findViewById<Button>(R.id.done_btn)
        var areaBoxes: MutableMap<String, CheckBox> = HashMap<String, CheckBox>()

        // Sets up the all option
        var allChk = CheckBox(activity)
        allChk.setHint("All")
        checkLayout.addView(allChk)

        // Sets up the checkboxes for all the different areas
        var isLeft = false
        for(i in 1..MultiselectLists.targettedAreaArray.size-1){
            var area = MultiselectLists.targettedAreaArray[i]
            var chkbx = CheckBox(activity)
            chkbx.setHint(area)
            areaBoxes[area] = chkbx
            if(isLeft){
                isLeft = false
                checkLayout.addView(chkbx)
            } else {
                isLeft = true
                checkLayout2.addView(chkbx)
            }
        }

        // Set initial checks
        if(targettedAreas.size == 0){
            allChk?.isChecked = true
        } else {
            for(area in targettedAreas){
                areaBoxes[area]?.isChecked = true
            }
        }

        // Clears all other checkboxes if all is checked
        allChk?.setOnClickListener { view ->
            if(allChk.isChecked == true){
                for(area in areaBoxes){
                    area.value.isChecked = false
                }
            }
        }

        // Clears the all checkbox if anything else is checked
        for(area in areaBoxes){
            area.value.setOnClickListener { view ->
                if(area.value.isChecked){
                    allChk.isChecked = false
                }
            }
        }

        clearBtn.setOnClickListener { view ->
            allChk?.isChecked = true
            for(area in areaBoxes){
                area.value.isChecked = false
            }
        }

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }

        doneBtn.setOnClickListener { view ->
            if(allChk.isChecked == true){
                targettedAreas.clear()
                areaTargetTxt.setText("[All]")
            } else {
                var targetTxt = "["
                for(area in areaBoxes){
                    if(area.value.isChecked){
                        targettedAreas.add(area.key)
                        targetTxt += (area.key + ", ")
                    }
                }

                targetTxt = targetTxt.dropLast(2)
                targetTxt += "]"
                areaTargetTxt.setText(targetTxt)
            }

            builder.dismiss()
        }

        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    /**
     * Builds and shows a dialog that allows users to select areas they want to exclude
     * Uses: custom_dialog_areas
     */
    fun buildExcludeAreasDialog(){
        val builder = AlertDialog.Builder(context).create()
        val view = layoutInflater.inflate(R.layout.custom_dialog_areas, null)
        var checkLayout = view.findViewById<LinearLayout>(R.id.checkbox_layout)
        var checkLayout2 = view.findViewById<LinearLayout>(R.id.checkbox_layout_2)
        var clearBtn = view.findViewById<Button>(R.id.clear_btn)
        var cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        var doneBtn = view.findViewById<Button>(R.id.done_btn)
        var areaBoxes: MutableMap<String, CheckBox> = HashMap<String, CheckBox>()

        // Sets up the all option
        var allChk = CheckBox(activity)
        allChk.setHint("None")
        checkLayout.addView(allChk)

        // Sets up the area checkboxes
        var isLeft = false
        for(i in 1..MultiselectLists.targettedAreaArray.size-1){
            var area = MultiselectLists.targettedAreaArray[i]
            var chkbx = CheckBox(activity)
            chkbx.setHint(area)
            areaBoxes[area] = chkbx
            if(isLeft){
                isLeft = false
                checkLayout.addView(chkbx)
            } else {
                isLeft = true
                checkLayout2.addView(chkbx)
            }


        }

        // Sets up initial checks
        if(excludedAreas.size == 0){
            allChk?.isChecked = true
        } else {
            for(area in excludedAreas){
                areaBoxes[area]?.isChecked = true
            }
        }

        // Sets up the all check to set all else to false if checked
        allChk?.setOnClickListener { view ->
            if(allChk.isChecked){
                for(area in areaBoxes){
                    area.value.isChecked = false
                }
            }
        }

        // Sets all check to false if anything else is checked
        for(area in areaBoxes){
            area.value.setOnClickListener { view ->
                if(area.value.isChecked){
                    allChk?.isChecked = false
                }
            }
        }

        clearBtn.setOnClickListener { view ->
            allChk?.isChecked = true
            for(area in areaBoxes){
                area.value.isChecked = false
            }
        }

        cancelBtn.setOnClickListener { view ->
            builder.dismiss()
        }

        doneBtn.setOnClickListener { view ->
            if(allChk?.isChecked == true){
                excludedAreas.clear()
                areaExcludeTxt.setText("[None]")
            } else {
                var targetTxt = "["
                for(area in areaBoxes){
                    if(area.value.isChecked){
                        excludedAreas.add(area.key)
                        targetTxt += (area.key + ", ")
                    }
                }

                targetTxt = targetTxt.dropLast(2)
                targetTxt += "]"
                areaExcludeTxt.setText(targetTxt)
            }

            builder.dismiss()
        }

        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    /**
     * Loads all the exercises from the database
     */
    private fun loadExercises(view: View){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        val retrievedList = dbHandler.getAllExercises()

        if(retrievedList != null && retrievedList.size > 0){
            allExercises = retrievedList
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GenerateWorkoutFragment().apply { }
    }
}