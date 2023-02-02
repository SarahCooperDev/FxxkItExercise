package com.example.fxxkit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/**
 * A simple [Fragment] subclass.
 * Use the [AddExerciseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExerciseFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_exercise, container, false)

        val exNameInput = view.findViewById<EditText>(R.id.exercise_name)
        val exSetNosInput = view.findViewById<EditText>(R.id.exercise_set_nos)
        val exRepNosInput = view.findViewById<EditText>(R.id.exercise_rep_nos)


        val showBtn = view.findViewById<Button>(R.id.add_exercise_btn)
        showBtn.setOnClickListener{view ->
            val text = exNameInput.text.toString() + ", " + exSetNosInput.text.toString() + ", " + exRepNosInput.text.toString()
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment AddExerciseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            AddExerciseFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}