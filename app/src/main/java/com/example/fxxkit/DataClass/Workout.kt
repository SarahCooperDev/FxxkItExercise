package com.example.fxxkit.DataClass

class Workout {
    var id: Int = 0
    var workoutName: String? = null
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