package com.example.fxxkit.DataClass

data class WorkoutListData(val workoutDataList: ArrayList<WorkoutData>){
    data class WorkoutData(val workoutName: String, val exercises: ArrayList<ExerciseData>){
        data class ExerciseData(val exerciseName: String)
    }
}

class ExpandableWorkoutModel{
    companion object {
        const val PARENT = 0
        const val CHILD = 1
    }

    lateinit var workoutParent: WorkoutListData.WorkoutData
    var type: Int
    lateinit var workoutChild: WorkoutListData.WorkoutData.ExerciseData
    var isExpanded: Boolean
    private var isCloseShown: Boolean

    constructor(type: Int, workoutParent: WorkoutListData.WorkoutData, isExpanded: Boolean = false, isCloseShown: Boolean = false){
        this.type = type
        this.workoutParent = workoutParent
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    constructor(type: Int, workoutChild: WorkoutListData.WorkoutData.ExerciseData, isExpanded: Boolean = false, isCloseShown: Boolean = false){
        this.type = type
        this.workoutChild = workoutChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }
}
