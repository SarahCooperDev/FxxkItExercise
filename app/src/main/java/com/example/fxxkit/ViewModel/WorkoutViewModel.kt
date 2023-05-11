package com.example.fxxkit.ViewModel

import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise

class WorkoutViewModel{
    var id: Int = -1
    var name: String? = null
    var description: String? = null
    var isFavourited: Boolean = false
    var workExList: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

    constructor(id: Int, name: String){
        this.id = id
        this.name = name
    }

    constructor(id: Int){
        this.id = id
    }

    public fun castWorkoutVMToWorkout() : Workout {
        var currentWorkout = Workout(this.id)

        if(this.name != null){ currentWorkout.workoutName = this.name }
        if(this.description != null){ currentWorkout.description = this.description }
        currentWorkout.isFavourited = this.isFavourited

        return currentWorkout
    }
}
