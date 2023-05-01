package com.example.fxxkit

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fxxkit.DataClass.Debugger
import com.example.fxxkit.DataClass.Exercise
import com.example.fxxkit.DataClass.Workout
import com.example.fxxkit.DataClass.WorkoutExercise

class DBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
    SQLiteOpenHelper(context, DATABASE_NAME, factory, CURRENT_DATABASE_VERSION){

    companion object{
        private val CURRENT_DATABASE_VERSION = 4
        private val NEW_DATABASE_VERSION = 5
        private val DATABASE_NAME = "exerciseDB.db"

        val TABLE_EXERCISES = "exercise"
        val TABLE_WORKOUTS = "workout"
        val TABLE_WORKOUT_EXERCISE = "workout_exercise"

        val COLUMN_ID = "_id"
        val COLUMN_EXERCISENAME = "exercise_name"
        val COLUMN_ISSTRENGTH = "is_strength"
        val COLUMN_ISCONDITION = "is_condition"
        val COLUMN_POSSIBLESETSIZE = "possible_set_size"
        val COLUMN_SET_SIZE = "set_size"
        val COLUMN_POSSIBLEREPSIZE = "possible_rep_size"
        val COLUMN_REP_SIZE = "rep_size"
        val COLUMN_TARGETTEDMUSCLES = "targetted_muscles"

        val COLUMN_WORKOUTNAME = "workout_name"
        val COLUMN_WORKOUT = "workout_id"
        val COLUMN_EXERCISE = "exercise_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_EXERCISES +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_EXERCISENAME + " TEXT, " +
                COLUMN_ISSTRENGTH + " BOOLEAN, " + COLUMN_ISCONDITION + " BOOLEAN, " +
                COLUMN_POSSIBLESETSIZE + " TEXT, " + COLUMN_POSSIBLEREPSIZE + " TEXT, " +
                COLUMN_TARGETTEDMUSCLES + " TEXT)")
        val CREATE_WORKOUT_TABLE = ("CREATE TABLE " + TABLE_WORKOUTS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_WORKOUTNAME + " TEXT)")
        val CREATE_WORKOUT_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_WORKOUT_EXERCISE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_WORKOUT + " INTEGER, " + COLUMN_EXERCISE + " INTEGER, " +
                 COLUMN_SET_SIZE + " TEXT, " + COLUMN_REP_SIZE + " TEXT" + ")")

        db.execSQL(CREATE_EXERCISE_TABLE)
        db.execSQL(CREATE_WORKOUT_TABLE)
        db.execSQL(CREATE_WORKOUT_EXERCISE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_EXERCISE)

        onCreate(db)
    }



    fun addInitialExercises(){
        val ex1 = Exercise("Push ups")
        val ex2 = Exercise("Sit ups")
        val ex3 = Exercise("Side crunches")

        addExercise(ex1)
        addExercise(ex2)
        addExercise(ex3)
    }

    fun addInitialWorkoutExercises(){
        val exerciseList: ArrayList<Exercise>? = getAllExercises()
        val workoutList: ArrayList<Workout>? = getAllWorkouts()

        if(workoutList != null && workoutList.size > 0){
            if(exerciseList != null && exerciseList.size > 0){
                if(exerciseList.size >= 3){
                    for(j in 0..2){
                        //addExerciseToWorkout(workoutList[0], exerciseList[j])
                    }
                    //addExerciseToWorkout(workoutList[1], exerciseList[0])
                } else {
                    for(j in exerciseList.indices){
                        //addExerciseToWorkout(workoutList[0], exerciseList[j])
                        //addExerciseToWorkout(workoutList[1], exerciseList[0])
                    }
                }
            }


        }
    }

    fun addInitialWorkouts(){
        val work1 = Workout("10 minute workout")
        val work2 = Workout("Basic half")

        addWorkout(work1)
        addWorkout(work2)
    }

    fun initialiseDatabase(){
        println("Initialising database")
        val dbold = this.writableDatabase
        onUpgrade(dbold, CURRENT_DATABASE_VERSION, NEW_DATABASE_VERSION)

        addInitialExercises()
        addInitialWorkouts()
        addInitialWorkoutExercises()
    }



    fun addExercise(exercise: Exercise){
        var setString = exercise.getSetAsString()
        var repString = exercise.getRepsAsString()
        var muscleString = exercise.getMusclesAsString()

        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_EXERCISENAME, exercise.name)
        values.put(COLUMN_ISCONDITION, exercise.isConditioning)
        values.put(COLUMN_ISSTRENGTH, exercise.isStrengthening)

        if(setString != null){ values.put(COLUMN_POSSIBLESETSIZE, setString) }
        if(repString != null){ values.put(COLUMN_POSSIBLEREPSIZE, repString) }
        if(muscleString != null){ values.put(COLUMN_TARGETTEDMUSCLES, muscleString) }

        db.insert(TABLE_EXERCISES, null, values)
        db.close()
    }

    fun addExerciseToWorkout(workoutExercise: WorkoutExercise): Int?{
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUT, workoutExercise.workoutId)
        values.put(COLUMN_EXERCISE, workoutExercise.exerciseId)

        if(workoutExercise.setSize != null){ values.put(COLUMN_SET_SIZE, workoutExercise.setSize) }
        if(workoutExercise.repSize != null){ values.put(COLUMN_REP_SIZE, workoutExercise.repSize) }

        val newId = db.insert(TABLE_WORKOUT_EXERCISE, null, values)
        db.close()

        return newId.toInt()
    }

    fun addWorkout(workout: Workout): Int?{
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUTNAME, workout.workoutName)

        var result = db.insert(TABLE_WORKOUTS, null, values)
        db.close()

        return result.toInt()
    }

    fun findExercise(name: String): Exercise?{
        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_EXERCISENAME = \"$name\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var exercise: Exercise? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)

            exercise = Exercise(id, name)
            cursor.close()
        }

        db.close()
        return exercise
    }

    fun findExerciseById(exId: Int): Exercise?{
        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_ID = \"$exId\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var exercise: Exercise? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)

            exercise = Exercise(id, name)
            cursor.close()
        }

        db.close()
        return exercise
    }

    @SuppressLint("Range")
    fun findAllWorkoutExercises(workout: Workout): ArrayList<WorkoutExercise>{
        var workExList = ArrayList<WorkoutExercise>()

        if(workout.id >= 0) {
            var workid = workout.id
            val query = "SELECT * FROM $TABLE_WORKOUT_EXERCISE WHERE $COLUMN_WORKOUT = \"$workid\""

            val db = this.readableDatabase
            val cursor = db.rawQuery(query, null)

            var id: Int = -1
            var exerciseId: Int = -1

            if(cursor.moveToFirst()){
                cursor.moveToFirst()
                do{
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    exerciseId = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE))

                    var workEx = WorkoutExercise(id)
                    workEx.workoutId = workout.id
                    workEx.exerciseId = exerciseId

                    workExList.add(workEx)
                } while(cursor.moveToNext())

                cursor.close()
            }
        }

        return workExList
    }

    @SuppressLint("Range")
    fun getAllExercises(): ArrayList<Exercise>?{
        val exerciseList: ArrayList<Exercise> = ArrayList<Exercise>()

        val query = "SELECT * FROM $TABLE_EXERCISES"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var exId: Int
        var exName: String
        var isStrength: Boolean?
        var isCondition: Boolean?
        var setString: String?
        var repString: String?
        var muscleString: String?

        if(cursor.moveToFirst()){
            do{
                exId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                exName = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISENAME))
                isStrength = cursor.getInt(cursor.getColumnIndex(COLUMN_ISSTRENGTH)) > 0
                isCondition = cursor.getInt(cursor.getColumnIndex(COLUMN_ISCONDITION)) > 0
                setString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLESETSIZE))
                repString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLEREPSIZE))
                muscleString = cursor.getString(cursor.getColumnIndex(COLUMN_TARGETTEDMUSCLES))

                val exercise = Exercise(exId, exName)
                if(isCondition != null){ exercise.isConditioning = isCondition }
                if(isStrength != null){ exercise.isStrengthening = isStrength }
                if(setString != null){ exercise.setStringToSet(setString) }
                if(repString != null){ exercise.setStringToRep(repString) }
                if(muscleString != null){ exercise.setStringToMuscle(muscleString) }

                exerciseList.add(exercise)
            } while(cursor.moveToNext())
        }
        return exerciseList
    }

    @SuppressLint("Range")
    fun getAllWorkoutExercises(): ArrayList<WorkoutExercise>?{
        val workExList: ArrayList<WorkoutExercise> = ArrayList<WorkoutExercise>()

        val query = "SELECT * FROM $TABLE_WORKOUT_EXERCISE"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var id: Int
        var workoutId: Int
        var exerciseId: Int

        if(cursor.moveToFirst()){
            cursor.moveToFirst()
            do{
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                workoutId = cursor.getInt(cursor.getColumnIndex(COLUMN_WORKOUT))
                exerciseId = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE))

                val workEx = WorkoutExercise(id, workoutId, exerciseId)
                workExList.add(workEx)
            } while(cursor.moveToNext())
        }

        return workExList
    }

    @SuppressLint("Range")
    fun getAllWorkouts(): ArrayList<Workout>?{
        val workoutList: ArrayList<Workout> = ArrayList<Workout>()

        val query = "SELECT * FROM $TABLE_WORKOUTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var workId: Int
        var workName: String

        if(cursor.moveToFirst()){
            do{
                workId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                workName = cursor.getString(cursor.getColumnIndex(COLUMN_WORKOUTNAME))

                val workout = Workout(workId, workName)
                workoutList.add(workout)
            } while(cursor.moveToNext())
        }

        return workoutList
    }

    fun updateExercise(exercise: Exercise): Boolean{
        var setString = exercise.getSetAsString()
        var repString = exercise.getRepsAsString()
        var muscleString = exercise.getMusclesAsString()
        var result = -1

        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_EXERCISENAME, exercise.name)
        values.put(COLUMN_ISCONDITION, exercise.isConditioning)
        values.put(COLUMN_ISSTRENGTH, exercise.isStrengthening)

        if(setString != null){ values.put(COLUMN_POSSIBLESETSIZE, setString) }

        if(repString != null){ values.put(COLUMN_POSSIBLEREPSIZE, repString) }

        if(muscleString != null){ values.put(COLUMN_TARGETTEDMUSCLES, muscleString) }

        result = db.update(TABLE_EXERCISES, values, "$COLUMN_ID=?", arrayOf(exercise.id.toString()))

        return (result != -1)
    }

    fun deleteExercise(name: String): Boolean{
        var result = false

        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_EXERCISENAME = \"$name\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_EXERCISES, COLUMN_ID + " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }

        db.close()
        return result
    }

    fun deleteWorkout(id: Int): Boolean{
        var result = false

        val query = "SELECT * FROM $TABLE_WORKOUTS WHERE $COLUMN_ID = \"$id\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_WORKOUTS, COLUMN_ID + " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }

        db.close()
        return result
    }

    fun deleteWorkoutExercises(id: Int): Boolean{
        println("Deleting workout: $id")
        val db = this.writableDatabase

        var result = db.delete(TABLE_WORKOUTS, "$COLUMN_WORKOUT = ?", arrayOf(id.toString()))

        db.close()
        return result == 0
    }
}