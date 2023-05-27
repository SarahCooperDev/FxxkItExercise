package com.example.fxxkit

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fxxkit.DataClass.*

class DBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
    SQLiteOpenHelper(context, DATABASE_NAME, factory, CURRENT_DATABASE_VERSION){

    companion object{
        private val CURRENT_DATABASE_VERSION = 1
        private val NEW_DATABASE_VERSION = 1
        private val DATABASE_NAME = "workoutDB.db"

        val TABLE_EXERCISES = "exercise"
        val TABLE_WORKOUTS = "workout"
        val TABLE_WORKOUT_EXERCISE = "workout_exercise"
        val TABLE_TAGS = "tag"
        val TABLE_WORKOUT_TAGS = "workout_tag"
        val TABLE_EXERCISE_TAGS = "exercise_tag"

        val COLUMN_ID = "_id"
        val COLUMN_EXERCISE_NAME = "exercise_name"
        val COLUMN_IS_STRENGTH = "is_strength"
        val COLUMN_IS_CONDITION = "is_condition"
        val COLUMN_POSSIBLE_SET_SIZE = "possible_set_size"
        val COLUMN_POSSIBLE_REP_SIZE = "possible_rep_size"
        val COLUMN_TARGETTED_AREAS = "targetted_muscles"
        val COLUMN_REP_TIME = "rep_time"

        val COLUMN_WORKOUTNAME = "workout_name"

        val COLUMN_DESCRIPTION = "description"
        val COLUMN_IS_FAVOURITED = "is_favourited"

        val COLUMN_WORKOUT = "workout_id"
        val COLUMN_EXERCISE = "exercise_id"
        val COLUMN_TAG = "tag_id"
        val COLUMN_SET_SIZE = "set_size"
        val COLUMN_REP_SIZE = "rep_size"
        val COLUMN_ORDER_NO = "order_no"

        val COLUMN_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_EXERCISES +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_EXERCISE_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " + COLUMN_REP_TIME + " INTEGER, " +
                COLUMN_IS_STRENGTH + " BOOLEAN, " + COLUMN_IS_CONDITION + " BOOLEAN, " +
                COLUMN_POSSIBLE_SET_SIZE + " TEXT, " + COLUMN_POSSIBLE_REP_SIZE + " TEXT, " +
                COLUMN_TARGETTED_AREAS + " TEXT)")

        val CREATE_WORKOUT_TABLE = ("CREATE TABLE " + TABLE_WORKOUTS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_WORKOUTNAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " + COLUMN_IS_FAVOURITED + " BOOLEAN)")

        val CREATE_WORKOUT_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_WORKOUT_EXERCISE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_WORKOUT + " INTEGER, " + COLUMN_EXERCISE + " INTEGER, " +
                COLUMN_SET_SIZE + " TEXT, " + COLUMN_REP_SIZE + " TEXT, " + COLUMN_ORDER_NO + " INTEGER" + ")")

        val CREATE_TAG_TABLE = ("CREATE TABLE " + TABLE_TAGS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_NAME + " TEXT)")

        val CREATE_WORKOUT_TAG_TABLE = ("CREATE TABLE " + TABLE_WORKOUT_TAGS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_WORKOUT + " INTEGER, " + COLUMN_TAG + " INTEGER)")

        val CREATE_EXERCISE_TAG_TABLE = ("CREATE TABLE " + TABLE_EXERCISE_TAGS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_EXERCISE + " INTEGER, " + COLUMN_TAG + " INTEGER)")

        db.execSQL(CREATE_EXERCISE_TABLE)
        db.execSQL(CREATE_WORKOUT_TABLE)
        db.execSQL(CREATE_TAG_TABLE)

        db.execSQL(CREATE_WORKOUT_EXERCISE_TABLE)
        db.execSQL(CREATE_WORKOUT_TAG_TABLE)
        db.execSQL(CREATE_EXERCISE_TAG_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_EXERCISE)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_TAGS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_TAGS)

        onCreate(db)
    }


    fun migrateDatabase(){
        println("DB: Migrating database: saving exercises and workouts")
        println("Note: workout exercises will be lost")
        val dbold = this.writableDatabase
        var allExercises = getAllExercises()
        var allWorkouts = getAllWorkouts()
        var allTags = getAllTags()

        onUpgrade(dbold, CURRENT_DATABASE_VERSION, NEW_DATABASE_VERSION)

        for(exercise in allExercises!!){
            addExercise(exercise)
        }

        for(workout in allWorkouts!!){
            addWorkout(workout)
        }

        for(tag in allTags){
            addTag(tag)
        }
    }



    fun addExercise(exercise: Exercise): Int?{
        println("DB: Adding exercise: ${exercise.name}")
        println("DB: rep time: ${exercise.repTime}")

        var setString = exercise.getSetAsString()
        var repString = exercise.getRepsAsString()
        var areaString = exercise.getAreasAsString()

        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_EXERCISE_NAME, exercise.name)
        values.put(COLUMN_DESCRIPTION, exercise.description)
        values.put(COLUMN_IS_CONDITION, exercise.isConditioning)
        values.put(COLUMN_IS_STRENGTH, exercise.isStrengthening)
        values.put(COLUMN_REP_TIME, exercise.repTime)

        if(setString != null){ values.put(COLUMN_POSSIBLE_SET_SIZE, setString) }
        if(repString != null){ values.put(COLUMN_POSSIBLE_REP_SIZE, repString) }
        if(areaString != null){ values.put(COLUMN_TARGETTED_AREAS, areaString) }

        var id = db.insert(TABLE_EXERCISES, null, values)
        db.close()

        return id.toInt()
    }

    fun addExerciseToWorkout(workoutExercise: WorkoutExercise): Int?{
        println("DB: Adding exercise ${workoutExercise.exerciseId.toString()} to workout ${workoutExercise.workoutId.toString()}")

        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUT, workoutExercise.workoutId)
        values.put(COLUMN_EXERCISE, workoutExercise.exerciseId)
        if(workoutExercise.setSize != null){ values.put(COLUMN_SET_SIZE, workoutExercise.setSize) }
        if(workoutExercise.repSize != null){ values.put(COLUMN_REP_SIZE, workoutExercise.repSize) }
        values.put(COLUMN_ORDER_NO, workoutExercise.orderNo)

        val newId = db.insert(TABLE_WORKOUT_EXERCISE, null, values)
        db.close()

        return newId.toInt()
    }

    fun addTag(tag: Tag): Int?{
        println("DB: Adding tag ${tag.name}")

        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_NAME, tag.name)

        var result = db.insert(TABLE_TAGS, null, values)
        db.close()

        return result.toInt()
    }

    fun addTagToExerciseByIds(exerciseId: Int, tagId: Int): Int?{
        println("DB: Adding tag ${tagId} to exercise ${exerciseId}")
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_EXERCISE, exerciseId)
        values.put(COLUMN_TAG, tagId)

        var result = db.insert(TABLE_EXERCISE_TAGS, null, values)
        db.close()

        return result.toInt()
    }

    fun addTagToExercise(exercise: Exercise, tag: Tag): Int?{
        println("DB: Adding tag ${tag.name} to exercise ${exercise.name}")
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_EXERCISE, exercise.id)
        values.put(COLUMN_TAG, tag.id)

        var result = db.insert(TABLE_EXERCISE_TAGS, null, values)
        db.close()

        return result.toInt()
    }

    fun addTagToWorkoutByIds(workoutId: Int, tagId: Int): Int?{
        println("DB: Adding tag ${tagId} to workout ${workoutId}")
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUT, workoutId)
        values.put(COLUMN_TAG, tagId)

        var result = db.insert(TABLE_WORKOUT_TAGS, null, values)
        db.close()

        return result.toInt()
    }

    fun addTagToWorkout(workout: Workout, tag: Tag): Int?{
        println("DB: Adding tag ${tag.name} to workout ${workout.workoutName}")
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUT, workout.id)
        values.put(COLUMN_TAG, tag.id)

        var result = db.insert(TABLE_WORKOUT_TAGS, null, values)
        db.close()

        return result.toInt()
    }

    fun addWorkout(workout: Workout): Int?{
        println("DB: Adding workout ${workout.workoutName}")

        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUTNAME, workout.workoutName)
        values.put(COLUMN_DESCRIPTION, workout.description)
        values.put(COLUMN_IS_FAVOURITED, workout.isFavourited)

        var result = db.insert(TABLE_WORKOUTS, null, values)
        db.close()

        return result.toInt()
    }

    @SuppressLint("Range")
    fun findExerciseByName(name: String): Exercise?{
        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_EXERCISE_NAME = \"${name}\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var exercise: Exercise? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)
            val exDesc = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
            val isStrength = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_STRENGTH)) > 0
            val isCondition = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CONDITION)) > 0
            val repTime = cursor.getInt(cursor.getColumnIndex(COLUMN_REP_TIME))
            val setString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLE_SET_SIZE))
            val repString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLE_REP_SIZE))
            val areaString = cursor.getString(cursor.getColumnIndex(COLUMN_TARGETTED_AREAS))

            exercise = Exercise(id, name)
            exercise.description = exDesc
            if(repTime > 0){ exercise.repTime = repTime}
            if(isCondition != null){ exercise.isConditioning = isCondition }
            if(isStrength != null){ exercise.isStrengthening = isStrength }
            if(setString != null){ exercise.setStringToSet(setString) }
            if(repString != null){ exercise.setStringToRep(repString) }
            if(areaString != null){ exercise.setStringToArea(areaString) }

            cursor.close()
        }

        db.close()
        return exercise
    }

    @SuppressLint("Range")
    fun findExerciseById(exId: Int): Exercise?{
        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_ID = \"${exId}\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val exId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val exName = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE_NAME))
            val exDesc = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
            val isStrength = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_STRENGTH)) > 0
            val isCondition = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CONDITION)) > 0
            val repTime = cursor.getInt(cursor.getColumnIndex(COLUMN_REP_TIME))
            val setString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLE_SET_SIZE))
            val repString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLE_REP_SIZE))
            val areaString = cursor.getString(cursor.getColumnIndex(COLUMN_TARGETTED_AREAS))

            if(exId < 0 || exName == null){
                return null
            }

            val exercise = Exercise(exId, exName)
            exercise.description = exDesc
            if(repTime > 0){ exercise.repTime = repTime}
            if(isCondition != null){ exercise.isConditioning = isCondition }
            if(isStrength != null){ exercise.isStrengthening = isStrength }
            if(setString != null){ exercise.setStringToSet(setString) }
            if(repString != null){ exercise.setStringToRep(repString) }
            if(areaString != null){ exercise.setStringToArea(areaString) }

            cursor.close()
            db.close()
            return exercise
        } else {
            db.close()
            return null
        }
    }

    @SuppressLint("Range")
    fun findTagById(id: Int): Tag?{
        println("DB: finding tag of id ${id}")
        val query = "SELECT * FROM $TABLE_TAGS WHERE $COLUMN_ID = \"${id}\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))

            val tag = Tag(id, name)

            cursor.close()
            db.close()
            return tag
        } else {
            db.close()
            return null
        }
    }

    @SuppressLint("Range")
    fun findTagByName(name: String): Tag?{
        println("DB: finding tag of name ${name}")
        val query = "SELECT * FROM $TABLE_TAGS WHERE $COLUMN_NAME = \"${name}\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))

            val tag = Tag(id, name)

            cursor.close()
            db.close()
            return tag
        } else {
            db.close()
            return null
        }
    }

    @SuppressLint("Range")
    fun findWorkoutById(id: Int): Workout?{
        println("DB: finding workout of id ${id}")
        val query = "SELECT * FROM $TABLE_WORKOUTS WHERE $COLUMN_ID = \"${id}\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val workId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val workName = cursor.getString(cursor.getColumnIndex(COLUMN_WORKOUTNAME))
            val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
            val isFavourited = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVOURITED)) > 0

            val workout = Workout(workId, workName)
            workout.description = description
            workout.isFavourited = isFavourited

            if(workId < 0 || workId == null || workName == null){
                return null
            }

            cursor.close()
            db.close()
            return workout
        } else {
            db.close()
            return null
        }
    }

    @SuppressLint("Range")
    fun findAllWorkoutExercisesByExerciseId(exerciseId: Int): ArrayList<WorkoutExercise>{
        var workExList = ArrayList<WorkoutExercise>()
        val query = "SELECT * FROM $TABLE_WORKOUT_EXERCISE WHERE $COLUMN_EXERCISE = \"${exerciseId}\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var id: Int = -1
        var workoutId: Int = -1
        var setSize: String? = null
        var repSize: String? = null
        var orderNo: Int = -1

        if(cursor.moveToFirst()){
            cursor.moveToFirst()
            do{
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                workoutId = cursor.getInt(cursor.getColumnIndex(COLUMN_WORKOUT))
                setSize = cursor.getString(cursor.getColumnIndex(COLUMN_SET_SIZE))
                repSize = cursor.getString(cursor.getColumnIndex(COLUMN_REP_SIZE))
                orderNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_NO))

                var workEx = WorkoutExercise()
                workEx.id = id
                workEx.workoutId = workoutId
                workEx.exerciseId = exerciseId
                workEx.setSize = setSize
                workEx.repSize = repSize
                workEx.orderNo = orderNo

                var exercise = findExerciseById(exerciseId)

                if(workEx.workoutId != null && workEx.exerciseId != null && exercise != null){
                    workEx.exercise = exercise
                    workExList.add(workEx)
                }
            } while(cursor.moveToNext())

            cursor.close()
        }

        return workExList
    }

    @SuppressLint("Range")
    fun findAllWorkoutExercisesByWorkoutId(workoutId: Int): ArrayList<WorkoutExercise>{
        var workExList = ArrayList<WorkoutExercise>()
        val query = "SELECT * FROM $TABLE_WORKOUT_EXERCISE WHERE $COLUMN_WORKOUT = \"${workoutId}\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var id: Int = -1
        var exerciseId: Int = -1
        var setSize: String? = null
        var repSize: String? = null
        var orderNo: Int = -1

        if(cursor.moveToFirst()){
            cursor.moveToFirst()
            do{
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                exerciseId = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE))
                setSize = cursor.getString(cursor.getColumnIndex(COLUMN_SET_SIZE))
                repSize = cursor.getString(cursor.getColumnIndex(COLUMN_REP_SIZE))
                orderNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_NO))

                var workEx = WorkoutExercise()
                workEx.id = id
                workEx.workoutId = workoutId
                workEx.exerciseId = exerciseId
                workEx.setSize = setSize
                workEx.repSize = repSize
                workEx.orderNo = orderNo

                var exercise = findExerciseById(exerciseId)

                if(workEx.workoutId != null && workEx.exerciseId != null && exercise != null){
                    workEx.exercise = exercise
                    workExList.add(workEx)
                }
            } while(cursor.moveToNext())

            cursor.close()
        }

        return workExList
    }

    @SuppressLint("Range")
    fun findAllWorkoutExercises(workout: Workout): ArrayList<WorkoutExercise>{
        var workExList = ArrayList<WorkoutExercise>()

        if(workout.id >= 0) {
            var workid = workout.id
            val query = "SELECT * FROM $TABLE_WORKOUT_EXERCISE WHERE $COLUMN_WORKOUT = \"${workid}\""

            val db = this.readableDatabase
            val cursor = db.rawQuery(query, null)

            var id: Int = -1
            var exerciseId: Int = -1
            var setSize: String? = null
            var repSize: String? = null
            var orderNo: Int = -1

            if(cursor.moveToFirst()){
                cursor.moveToFirst()
                do{
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    exerciseId = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE))
                    setSize = cursor.getString(cursor.getColumnIndex(COLUMN_SET_SIZE))
                    repSize = cursor.getString(cursor.getColumnIndex(COLUMN_REP_SIZE))
                    orderNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_NO))

                    var workEx = WorkoutExercise()
                    workEx.id = id
                    workEx.workoutId = workout.id
                    workEx.exerciseId = exerciseId
                    workEx.setSize = setSize
                    workEx.repSize = repSize
                    workEx.orderNo = orderNo

                    var exercise = findExerciseById(exerciseId)

                    if(workEx.workoutId != null && workEx.exerciseId != null && exercise != null){
                        workEx.exercise = exercise
                        workExList.add(workEx)
                    }
                } while(cursor.moveToNext())

                cursor.close()
            }
        }

        return workExList
    }

    @SuppressLint("Range")
    fun getAllExercises(): ArrayList<Exercise>?{
        println("DB: getting all exercises")
        val exerciseList: ArrayList<Exercise> = ArrayList<Exercise>()

        val query = "SELECT * FROM $TABLE_EXERCISES"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var exId: Int
        var exName: String
        var description: String
        var isStrength: Boolean?
        var isCondition: Boolean?
        var setString: String?
        var repString: String?
        var areaString: String?
        var repTime: Int

        if(cursor.moveToFirst()){
            do{
                exId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                exName = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE_NAME))
                description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                isStrength = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_STRENGTH)) > 0
                isCondition = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CONDITION)) > 0
                setString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLE_SET_SIZE))
                repString = cursor.getString(cursor.getColumnIndex(COLUMN_POSSIBLE_REP_SIZE))
                areaString = cursor.getString(cursor.getColumnIndex(COLUMN_TARGETTED_AREAS))
                repTime = cursor.getInt(cursor.getColumnIndex(COLUMN_REP_TIME))

                val exercise = Exercise(exId, exName)
                exercise.description = description
                if(isCondition != null){ exercise.isConditioning = isCondition }
                if(isStrength != null){ exercise.isStrengthening = isStrength }
                if(setString != null){ exercise.setStringToSet(setString) }
                if(repString != null){ exercise.setStringToRep(repString) }
                if(areaString != null){ exercise.setStringToArea(areaString) }
                if(repTime > 0){ exercise.repTime = repTime}

                exerciseList.add(exercise)
            } while(cursor.moveToNext())
        }
        cursor.close()
        return exerciseList
    }

    @SuppressLint("Range")
    fun getAllTags(): ArrayList<Tag>{
        val tagList = ArrayList<Tag>()
        val query = "SELECT * FROM $TABLE_TAGS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var id: Int
        var name: String

        if(cursor.moveToFirst()){
            cursor.moveToFirst()
            do{
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))

                var tag = Tag(id, name)
                tagList.add(tag)
            } while(cursor.moveToNext())
        }

        cursor.close()
        return tagList
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
        var setSize: String? = null
        var repSize: String? = null
        var orderNo: Int = -1

        if(cursor.moveToFirst()){
            cursor.moveToFirst()
            do{
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                workoutId = cursor.getInt(cursor.getColumnIndex(COLUMN_WORKOUT))
                exerciseId = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE))
                setSize = cursor.getString(cursor.getColumnIndex(COLUMN_SET_SIZE))
                repSize = cursor.getString(cursor.getColumnIndex(COLUMN_REP_SIZE))
                orderNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_NO))

                val workEx = WorkoutExercise(id, workoutId, exerciseId)
                workEx.setSize = setSize
                workEx.repSize = repSize
                workEx.orderNo = orderNo
                workExList.add(workEx)
            } while(cursor.moveToNext())
        }

        cursor.close()
        return workExList
    }

    @SuppressLint("Range")
    fun getAllWorkouts(): ArrayList<Workout>?{
        println("DB: getting all workouts")
        val workoutList: ArrayList<Workout> = ArrayList<Workout>()

        val query = "SELECT * FROM $TABLE_WORKOUTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var workId: Int
        var workName: String
        var description: String
        var isFavourited: Boolean

        if(cursor.moveToFirst()){
            do{
                workId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                workName = cursor.getString(cursor.getColumnIndex(COLUMN_WORKOUTNAME))
                description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                isFavourited = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVOURITED)) > 0

                val workout = Workout(workId, workName)
                workout.description = description
                workout.isFavourited = isFavourited

                workoutList.add(workout)
            } while(cursor.moveToNext())
        }

        println("DB: number of workouts is ${workoutList.size.toString()}")
        cursor.close()
        return workoutList
    }

    @SuppressLint("Range")
    fun getTagsForExercise(exercise: Exercise): ArrayList<Tag>{
        println("DB: finding tags for exercise ${exercise.name} of id ${exercise.id}")
        var tagList = ArrayList<Tag>()

        val query = "SELECT * FROM $TABLE_EXERCISE_TAGS WHERE $COLUMN_EXERCISE = \"${exercise.id}\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var id: Int
        var exerciseId: Int
        var tagId: Int

        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                exerciseId = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE))
                tagId = cursor.getInt(cursor.getColumnIndex(COLUMN_TAG))

                var tag = findTagById(tagId)

                if (tag != null) {
                    tagList.add(tag)
                }
                println("Found tag of id ${tagId} and name ${tag?.name}")
            } while(cursor.moveToNext())
        }

        cursor.close()
        return tagList
    }


    @SuppressLint("Range")
    fun getTagsForWorkout(workout: Workout): ArrayList<Tag>{
        println("DB: finding tags for workout ${workout.workoutName}")
        var tagList = ArrayList<Tag>()

        val query = "SELECT * FROM $TABLE_WORKOUT_TAGS WHERE $COLUMN_WORKOUT = \"${workout.id}\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var id: Int
        var workoutId: Int
        var tagId: Int

        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                workoutId = cursor.getInt(cursor.getColumnIndex(COLUMN_WORKOUT))
                tagId = cursor.getInt(cursor.getColumnIndex(COLUMN_TAG))

                var tag = findTagById(tagId)

                if (tag != null) {
                    tagList.add(tag)
                }
                println("Found tag of id ${tag?.id} and name ${tag?.name}")
            } while(cursor.moveToNext())
        }

        cursor.close()
        return tagList
    }

    fun updateExercise(exercise: Exercise): Boolean{
        println("DB: Updating exercise ${exercise.name}")

        var setString = exercise.getSetAsString()
        var repString = exercise.getRepsAsString()
        var areaString = exercise.getAreasAsString()
        var result = -1

        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_EXERCISE_NAME, exercise.name)
        values.put(COLUMN_DESCRIPTION, exercise.description)
        values.put(COLUMN_IS_CONDITION, exercise.isConditioning)
        values.put(COLUMN_IS_STRENGTH, exercise.isStrengthening)
        if(exercise.repTime > 0){ values.put(COLUMN_REP_TIME, exercise.repTime) }
        if(setString != null){ values.put(COLUMN_POSSIBLE_SET_SIZE, setString) }
        if(repString != null){ values.put(COLUMN_POSSIBLE_REP_SIZE, repString) }
        if(areaString != null){ values.put(COLUMN_TARGETTED_AREAS, areaString) }

        result = db.update(TABLE_EXERCISES, values, "$COLUMN_ID=?", arrayOf(exercise.id.toString()))
        db.close()
        return (result != -1)
    }

    fun updateWorkoutExercise(workoutExercise: WorkoutExercise): Boolean{
        println("DB: updating workout exercise: ${workoutExercise.id.toString()}")

        var result = -1
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUT, workoutExercise.workoutId)
        values.put(COLUMN_EXERCISE, workoutExercise.exerciseId)
        if(workoutExercise.setSize != null){ values.put(COLUMN_SET_SIZE, workoutExercise.setSize) }
        if(workoutExercise.repSize != null){ values.put(COLUMN_REP_SIZE, workoutExercise.repSize) }
        values.put(COLUMN_ORDER_NO, workoutExercise.orderNo)

        result = db.update(TABLE_WORKOUT_EXERCISE, values, "$COLUMN_ID=?", arrayOf(workoutExercise.id.toString()))
        db.close()
        return result == 0
    }

    fun updateWorkout(workout: Workout): Boolean{
        println("DB: Updating workout: ${workout.id} - ${workout.workoutName}")

        var result = -1
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_WORKOUTNAME, workout.workoutName)
        values.put(COLUMN_DESCRIPTION, workout.description)
        values.put(COLUMN_IS_FAVOURITED, workout.isFavourited)

        result = db.update(TABLE_WORKOUTS, values, "$COLUMN_ID=?", arrayOf(workout.id.toString()))
        db.close()
        return result == 0
    }

    fun deleteExercise(name: String): Boolean{
        println("DB: Deleting exercise: ${name}")

        var result = -1
        val query = "SELECT * FROM $TABLE_EXERCISES WHERE $COLUMN_EXERCISE_NAME = \"${name}\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var id = -1

        if(cursor.moveToFirst()){
            id = Integer.parseInt(cursor.getString(0))
            result = db.delete(TABLE_EXERCISES, COLUMN_ID + " = ?", arrayOf(id.toString()))
            cursor.close()
        }
        db.close()
        if(id >= 0){
            deleteAllWorkoutExercisesOfExerciseId(id)
        }
        return result == 0
    }

    @SuppressLint("Range")
    fun deleteAllWorkoutExercisesOfExerciseId(exerciseId: Int): Boolean{
        println("Deleting all workout exercises of exercise ${exerciseId}")
        val db = this.writableDatabase
        var result = db.delete(TABLE_WORKOUT_EXERCISE, COLUMN_EXERCISE + " = ?", arrayOf(exerciseId.toString()))
        db.close()
        return true
    }

    fun deleteWorkout(id: Int): Boolean{
        println("DB: Deleting workout: ${id}")

        var result = false
        val query = "SELECT * FROM $TABLE_WORKOUTS WHERE $COLUMN_ID = \"${id}\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_WORKOUTS, COLUMN_ID + " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }

        db.close()
        deleteAllWorkoutExercisesOfWorkout(id)
        println("Result is ${result.toString()}")
        return result
    }

    @SuppressLint("Range")
    fun deleteAllWorkoutExercisesOfWorkout(workoutId: Int): Boolean{
        println("Deleting all workout exercises of workout ${workoutId}")
        val db = this.writableDatabase
        var result = db.delete(TABLE_WORKOUT_EXERCISE, COLUMN_WORKOUT + " = ?", arrayOf(workoutId.toString()))
        db.close()
        return true
    }

    fun deleteWorkoutExercise(id: Int): Boolean{
        println("DB: Deleting workout exercise: ${id}")

        val db = this.writableDatabase
        var result = db.delete(TABLE_WORKOUT_EXERCISE, "$COLUMN_ID = ?", arrayOf(id.toString()))

        db.close()
        println("result is ${result.toString()}")
        return result == 0
    }

    fun deleteTagFromExercise(exercise: Exercise, tag: Tag): Boolean{
        println("DB: Deleting tag ${tag.id} from exercise ${exercise.name}")
        val db = this.writableDatabase
        var result = db.delete(TABLE_EXERCISE_TAGS, "$COLUMN_EXERCISE =? AND $COLUMN_TAG =?",
            arrayOf(exercise.id.toString(), tag.id.toString()))

        db.close()
        println("result is ${result.toString()}")
        return result == 0
    }

    fun deleteTagFromWorkoutById(workoutId: Int, tag: Tag): Boolean{
        println("DB: Deleting tag ${tag.id} from workout ${workoutId}")
        val db = this.writableDatabase
        var result = db.delete(
            TABLE_WORKOUT_TAGS, "$COLUMN_WORKOUT =? AND $COLUMN_TAG =?",
            arrayOf(workoutId.toString(), tag.id.toString()))

        db.close()
        println("result is ${result.toString()}")
        return result == 0
    }
}