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
    var targettedAreas: ArrayList<String> = ArrayList<String>()
    var tags: ArrayList<Tag> = ArrayList<Tag>()

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

    public fun getAreasAsString(): String?{
        if(targettedAreas.size > 0){
            var areaString = ""

            for(i in 0..targettedAreas.size - 2){
                areaString += targettedAreas[i] + ", "
            }

            areaString += targettedAreas[targettedAreas.size - 1]

            return areaString
        }

        return null
    }

    public fun setStringToArea(areaString: String){
        var areaList: List<String> = areaString.split(",") as List<String>

        for(area in areaList){
            targettedAreas.add(area.trim())
        }
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