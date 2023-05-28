package com.example.fxxkit.Fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.fxxkit.DBHandler
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.MultiselectLists
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.MainActivity
import com.example.fxxkit.R
import kotlin.collections.ArrayList

/**
 * Fragment that allows user to create an exercise
 */
class CreateExerciseFragment : Fragment() {
    private var newExercise: Exercise = Exercise("Null")
    private var selectedSets = ArrayList<String>()
    private var selectedReps = ArrayList<String>()
    private var selectedAreas = ArrayList<String>()
    private var allTags = ArrayList<Tag>()

    private lateinit var exerciseNameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var repTimeInput: EditText
    private lateinit var isStrengthBtn: ToggleButton
    private lateinit var isConditioningBtn: ToggleButton
    private lateinit var setSizeMultiselect: TextView
    private lateinit var repSizeMultiselect: TextView
    private lateinit var targettedAreasMultiselect: TextView
    private lateinit var tagInput: EditText
    private lateinit var cancelBtn: ImageButton
    private lateinit var createBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_add_exercise, container, false)
        (activity as MainActivity).getSupportActionBar()?.customView?.findViewById<TextView>(R.id.appbar_title_id)?.setText(getString(R.string.create_exercise_title))

        selectedSets.add(MultiselectLists.setSizesArray[0])
        selectedReps.add(MultiselectLists.repSizesArray[0])
        selectedAreas.add(MultiselectLists.targettedAreaArray[0])
        getAllTags()

        exerciseNameInput = view.findViewById<EditText>(R.id.exercise_name)
        descriptionInput = view.findViewById<EditText>(R.id.description_txt)
        repTimeInput = view.findViewById<EditText>(R.id.rep_time_txt)
        isStrengthBtn = view.findViewById<ToggleButton>(R.id.strengthening_toggle_btn)
        isConditioningBtn = view.findViewById<ToggleButton>(R.id.conditioning_toggle_btn)
        setSizeMultiselect = view.findViewById(R.id.set_size_multiselect)
        repSizeMultiselect = view.findViewById(R.id.rep_size_multiselect)
        targettedAreasMultiselect = view.findViewById<TextView>(R.id.area_select)
        tagInput = view.findViewById<EditText>(R.id.tag_input)
        cancelBtn = view.findViewById<ImageButton>(R.id.cancel_btn)
        createBtn = view.findViewById<ImageButton>(R.id.create_btn)

        createBtn.setOnClickListener{view ->
            if(exerciseNameInput.text.toString().length < 1){
                Toast.makeText(activity, getString(R.string.error_blank_name_txt), Toast.LENGTH_LONG).show()
                exerciseNameInput.setBackgroundColor(Color.parseColor(getString(R.string.colour_error)))
            } else {
                createExercise()
                Toast.makeText(activity, "Created exercise ${exerciseNameInput.text.toString()}", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navToExerciseList()
            }
        }

        cancelBtn.setOnClickListener { view ->
            (activity as MainActivity).navToPrevious()
        }

        setSizeMultiselect.setText(selectedSets[0])
        setSizeMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.setSizesArray,  selectedSets, setSizeMultiselect)
        }

        repSizeMultiselect.setText(selectedReps[0])
        repSizeMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.repSizesArray, selectedReps, repSizeMultiselect)
        }

        targettedAreasMultiselect.setText(selectedAreas[0])
        targettedAreasMultiselect.setOnClickListener { view ->
            MultiselectLists.showDialog(activity as MainActivity, layoutInflater, MultiselectLists.targettedAreaArray, selectedAreas, targettedAreasMultiselect)
        }

        return view
    }

    /**
     * Adds exercise to database
     * Calls the addTags function
     */
    private fun createExercise(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)

        newExercise.name = exerciseNameInput.text.toString()
        newExercise.description = descriptionInput.text.toString()

        if(isStrengthBtn.isChecked()){ newExercise.isStrengthening = true }
        if(isConditioningBtn.isChecked()){ newExercise.isConditioning = true }
        if(selectedSets.size == 0){selectedSets.add(MultiselectLists.setSizesArray[0])}
        if(selectedReps.size == 0){selectedReps.add(MultiselectLists.repSizesArray[0])}
        if(selectedAreas.size == 0){selectedAreas.add(MultiselectLists.targettedAreaArray[0])}

        newExercise.possibleSetSize = selectedSets
        newExercise.possibleRepSize = selectedReps
        newExercise.targettedAreas = selectedAreas

        try {
            var repTime = repTimeInput.text.toString().toInt()
            if(repTime != null){ newExercise.repTime = repTime}
        } catch(e: Exception){
            Toast.makeText(requireContext(), getString(R.string.error_reptime_not_number), Toast.LENGTH_LONG)
        }

        var newId = dbHandler.addExercise(newExercise)
        if(newId != null){
            addTags(newId)
        }
    }

    /**
     * Adds any new tags to the database, then adds all tags to the exercise with exerciseId
     */
    private fun addTags(exerciseId: Int){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        var splitTags = tagInput.text.split(" ")
        for(tag in splitTags){
            var foundTag = allTags.firstOrNull{ it.name!!.lowercase() == tag.toString().lowercase() }
            if(foundTag == null){
                foundTag = Tag(tag.toString().lowercase())
                foundTag.id = dbHandler.addTag(foundTag)!!
            }

            if(exerciseId != null && foundTag.id != null){
                var result = dbHandler.addTagToExerciseByIds(exerciseId, foundTag.id)
            }
        }
    }

    /**
     * Gets all existing tags, to ensure existing tags aren't re-added to database
     */
    private fun getAllTags(){
        val dbHandler = DBHandler(this.requireContext(), null, null, 1)
        allTags = dbHandler.getAllTags()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            CreateExerciseFragment().apply { }
    }
}