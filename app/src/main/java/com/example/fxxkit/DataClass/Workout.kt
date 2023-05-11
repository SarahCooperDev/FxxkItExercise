package com.example.fxxkit.DataClass

class Workout {
    var id: Int = -1
    var workoutName: String? = null
    var description: String? = null
    var isFavourited: Boolean = false
    var exercises: ArrayList<Exercise> = ArrayList<Exercise>()

    constructor(id: Int, workoutName: String, exercises: ArrayList<Exercise>){
        this.id = id
        this.workoutName = workoutName
        this.exercises = exercises
    }
    constructor(id: Int, workoutName: String){
        this.id = id
        this.workoutName = workoutName
    }

    constructor(workoutName: String){
        this.workoutName = workoutName
    }

    constructor(id: Int){
        this.id = id
    }

    public fun setWorkoutExercises(exercises: ArrayList<Exercise>){
        this.exercises = exercises
    }

    public fun clearWorkoutExercises(){
        exercises.clear()
    }

    public fun addExercise(exercise: Exercise){
        exercises.add(exercise)
    }
}