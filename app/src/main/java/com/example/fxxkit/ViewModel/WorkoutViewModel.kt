package com.example.fxxkit.ViewModel

import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Tag
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise

class WorkoutViewModel{
    var id: Int = -1
    var name: String? = null
    var description: String? = null
    var isFavourited: Boolean = false
    var workExList: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()
    var tags: ArrayList<Tag> = ArrayList<Tag>()

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
