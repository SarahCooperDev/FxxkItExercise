package com.example.fxxkit.DataClass

class Exercise {
    var id: Int = -1
    var name: String = ""
    var description: String = ""
    var isStrengthening: Boolean = false
    var isConditioning: Boolean = false
    var repTime: Int = 10
    var possibleSetSize: ArrayList<String> = ArrayList<String>()
    var possibleRepSize: ArrayList<String> = ArrayList<String>()
    var targettedMuscles: ArrayList<String> = ArrayList<String>()

    constructor(id:Int, name: String){
        this.id = id
        this.name = name
    }

    constructor(id:Int?, name: String){
        if(id != null){
            this.id = id
        } else {
            this.id = -1
        }

        this.name = name
    }

    constructor(name: String){
        this.name = name
    }

    public fun getSetAsString(): String?{
        if(possibleSetSize.size > 0){
            var setString = ""

            for(i in 0..possibleSetSize.size - 2){
                setString += possibleSetSize[i] + ", "
            }

            setString += possibleSetSize[possibleSetSize.size - 1]

            return setString
        }

        return null
    }

    public fun setStringToSet(setString: String){
        var setList: List<String> = setString.split(",") as List<String>

        for(set in setList){
            possibleSetSize.add(set.trim())
        }
    }

    public fun getRepsAsString(): String?{
        if(possibleRepSize.size > 0){
            var repString = ""

            for(i in 0..possibleRepSize.size - 2){
                repString += possibleRepSize[i] + ", "
            }

            repString += possibleRepSize[possibleRepSize.size - 1]

            return repString
        }

        return null
    }

    public fun setStringToRep(repString: String){
        var repList: List<String> = repString.split(",") as List<String>

        for(rep in repList){
            possibleRepSize.add(rep.trim())
        }
    }

    public fun getMusclesAsString(): String?{
        if(targettedMuscles.size > 0){
            var muscleString = ""

            for(i in 0..targettedMuscles.size - 2){
                muscleString += targettedMuscles[i] + ", "
            }

            muscleString += targettedMuscles[targettedMuscles.size - 1]

            return muscleString
        }

        return null
    }

    public fun setStringToMuscle(muscleString: String){
        var muscleList: List<String> = muscleString.split(",") as List<String>

        for(muscle in muscleList){
            targettedMuscles.add(muscle.trim())
        }
    }
}