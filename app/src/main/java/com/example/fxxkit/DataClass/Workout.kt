package com.example.fxxkit.DataClass

class Workout {
    var id: Int = -1
    var workoutName: String? = null
    var description: String? = null
    var isFavourited: Boolean = false
    var exercises: ArrayList<Exercise> = ArrayList<Exercise>()
    var tags: ArrayList<Tag> = ArrayList<Tag>()

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

    public fun getTagDisplayString(): String?{
        if(tags.size < 1){
            return null
        } else {
            var tagString = ""
            for(tag in this.tags){
                tagString += tag.name + ", "
            }
            tagString = tagString.dropLast(2)

            return tagString
        }
    }

    public fun getTagInputString(): String?{
        if(tags.size < 1){
            return null
        } else {
            var tagString = ""
            for(tag in this.tags){
                tagString += tag.name + " "
            }
            tagString = tagString.dropLast(1)

            return tagString
        }
    }
}