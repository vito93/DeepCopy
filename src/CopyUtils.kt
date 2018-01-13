import kotlin.reflect.full.*
import kotlin.reflect.jvm.*
import kotlin.collections.*
import java.util.*

class CopyUtils{
    companion object {
        fun DeepCopy(obj: Any?, copiedObjects: MutableMap<Any, Any>):Any? {
            if(obj == null) return obj

            val objClassJava = obj::class.java
            val objClass = obj::class

            if(objClassJava.isPrimitive()) return obj
            else{
                if(objClass.javaPrimitiveType != null)
                    return obj
            }

            if (copiedObjects.keys.contains(obj) && copiedObjects.keys.size > 1) return copiedObjects.get(obj)

                when (obj) {
                    is String -> {
                            var newString = String(obj.toCharArray())
                            copiedObjects.put(obj, newString)
                            return newString
                    }
                    is Array<*> -> {
                        var arrList = ArrayList<Any?>()
                        var newArray:Any?

                         for (elem in obj) {
                             arrList.add(getValueFromCopiedCollection(elem, copiedObjects))
                         }

                        newArray = obj.clone()
                        arrList.toArray(newArray)
                        copiedObjects.put(obj, newArray)
                        return newArray
                    }
                    is List<*> -> {
                        var arrList = ArrayList<Any?>()
                         for (elem in obj){
                             arrList.add(getValueFromCopiedCollection(elem, copiedObjects))
                         }
                        copiedObjects.put(obj, arrList)
                        return arrList
                    }
                    is Map<*, *> -> {
                        var newMap = mutableMapOf<Any?, Any?>() //!!!

                        for((key, value) in obj){
                            newMap.put(getValueFromCopiedCollection(key, copiedObjects), getValueFromCopiedCollection(value, copiedObjects))
                        }
                        copiedObjects.put(obj, newMap)
                        return newMap
                    }
                    is Set<*> -> {
                        var newSet = mutableSetOf<Any?>()

                        for(elem in obj){
                            newSet.add(getValueFromCopiedCollection(elem, copiedObjects))
                        }
                        copiedObjects.put(obj, newSet)
                        return newSet
                    }
                    is Date -> { return Date(obj.time)}
                    else -> {
                        println(objClass.simpleName)
                        val properties = objClass.memberProperties
                        var newCopy = objClassJava.newInstance()//createInstance();

                        properties.forEach { prop ->
                            var field = prop.javaField
                            if (field != null) {
                                field.setAccessible(true);
                                var value = field.get(obj)
                                var type = field.getType()

                                if (value != null)
                                    println(prop.returnType.isMarkedNullable.toString() + " " + value.toString() + " " + type.getTypeName() + " " + type.isPrimitive().toString())
                                else {
                                    println("null " + type.getTypeName() + " " + type.isPrimitive().toString())
                                }
                                field.set(newCopy, getValueFromCopiedCollection(value, copiedObjects))

                            }
                        }

                        copiedObjects.put(obj, newCopy)
                        return newCopy
                    }
                }
            }

        fun DeepCopy(obj:Any?): Any?{
            var copiedObjects = mutableMapOf<Any, Any>()

            var initialObject: Any? = getValueFromCopiedCollection(obj, copiedObjects)
            initialObject = DeepCopy(obj, copiedObjects)

            return initialObject
        }

        private fun getValueFromCopiedCollection(value: Any?, copiedObjects: MutableMap<Any, Any>): Any?{
            if(value == null)
                return null

            if(copiedObjects.containsKey(value))
                return copiedObjects.get(value)

            var tempValue: Any? = null

            if(!value::class.java.isPrimitive()
                    && value::class.javaPrimitiveType == null
                    && !value::class.java.isArray)
                tempValue = value::class.createInstance()

            if(copiedObjects.isNotEmpty())
                tempValue = DeepCopy(value, copiedObjects)

            if(tempValue == null)
                tempValue = value
            else copiedObjects.put(value, tempValue!!)

            return tempValue
        }

        fun PrintObject(obj: Any?){
            if(obj == null){
                println("null")
                return
            }

            val properties = obj!!::class.memberProperties

            properties.forEach{
                prop ->

                println(prop.name + " - " + prop.getter.call(obj))
                    var field = prop.javaField
                    if(field != null){
                        field.setAccessible(true);
                        var value = field.get(obj)
                        println(value)
                }

                //println("The value of the field: " + prop.javaField!!.get(obj))
            }
        }
    }
}

