import kotlin.reflect.full.*
import kotlin.reflect.jvm.*
import kotlin.collections.*
import java.util.*

class CopyUtils{
    companion object {
        fun DeepCopy(obj: Any?):Any? {
            if(obj == null) return obj

            val objClassJava = obj::class.java
            val objClass = obj::class

            if(objClassJava.isPrimitive()) return obj
            else{
                if(objClass.javaPrimitiveType != null)
                    return obj
            }

                when (obj) {
                    is String -> return obj.subSequence(0, obj.length - 1)//!!!
                    is Array<*> -> {
                        var arrList = ArrayList<Any>()

                         for (elem in obj) {

                             if(elem != null) arrList.add(DeepCopy(elem)!!)
                         }

                        arrList.toArray(obj)
                        return obj.clone()
                    }
                    is List<*> -> {
                        var arrList = ArrayList<Any>()
                         for (elem in obj){
                            if(elem !=null) arrList.add(DeepCopy(elem)!!)
                         }

                        return arrList
                    }
                    is Map<*, *> -> {
                        var newMap = mutableMapOf<Any, Any>()

                        for((key, value) in obj){
                            if(key != null && value != null)
                            newMap.put(DeepCopy(key)!!, DeepCopy(value)!!)
                        }

                        return newMap
                    }
                    is Set<*> -> {
                        var newSet = mutableSetOf<Any>()

                        for(elem in obj){
                            if(elem != null)
                                newSet.add(DeepCopy(elem)!!)
                        }

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

                                field.set(newCopy, DeepCopy(value))
                            }
                        }
                        return newCopy
                    }
                }
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

