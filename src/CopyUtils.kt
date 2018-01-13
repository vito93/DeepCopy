import kotlin.reflect.full.*
import kotlin.reflect.jvm.*
import kotlin.collections.*
import java.util.*

class CopyUtils{
    companion object {
        private fun<T: Any> deepCopy(obj: T?, copiedObjects: MutableMap<Any, Any>): T? {
            if(obj == null) return obj

            val objClassJava = obj::class.java
            val objClass = obj::class

            // Проверяем на примитив или его обёртку
            if(objClassJava.isPrimitive()) return obj
            else{
                if(objClass.javaPrimitiveType != null)
                    return obj
            }

            //if (copiedObjects.keys.contains(obj) && copiedObjects.size > 1) return copiedObjects.get(obj) as T

            // Коллекции нужно разобрать поэлементно. Все писать для тестового задания нет смысла.
                when (obj) {
                    is String -> {
                            var newString = String(obj.toCharArray())
                            copiedObjects.put(obj, newString)
                            return newString as T
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
                        return newArray as T
                    }
                    is List<*> -> {
                        var arrList = ArrayList<Any?>()
                         for (elem in obj){
                             arrList.add(getValueFromCopiedCollection(elem, copiedObjects))
                         }
                        copiedObjects.put(obj, arrList)
                        return arrList as T
                    }
                    is Map<*, *> -> {
                        var newMap = mutableMapOf<Any?, Any?>()

                        for((key, value) in obj){
                            newMap.put(getValueFromCopiedCollection(key, copiedObjects), getValueFromCopiedCollection(value, copiedObjects))
                        }
                        copiedObjects.put(obj, newMap)
                        return newMap as T
                    }
                    is Set<*> -> {
                        var newSet = mutableSetOf<Any?>()

                        for(elem in obj){
                            newSet.add(getValueFromCopiedCollection(elem, copiedObjects))
                        }
                        copiedObjects.put(obj, newSet)
                        return newSet as T
                    }
                    is Date -> { return Date(obj.time) as T}
                    // Ссылочный тип в общем рассмотрим, копируя значения его полей в новый инстанс
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

                        // Вернём объект, предварительно записав его в коллекцию
                        copiedObjects.put(obj, newCopy)
                        return newCopy as T
                    }
                }
            }

        // Вызываемый извне метод
        fun <T: Any> deepCopy(obj: T?): T?{
            // Для скопированных объектов будем использовать словарь соответствий оригинал-копия;
            // в этот словарь будут заноситься только ссылочные типы
            var copiedObjects = mutableMapOf<Any, Any>()

            // Объявляем объект, ссылка которого будет содержать копию исходного. Нужно для проверки замкнутости на себя и проставление ссылки на него же самого.
            // Вызовом функции getValueFromCopiedCollection заносим в общую коллекцию со ссылкой на новый экземпляр класса объекта
            var initialObject: Any? = getValueFromCopiedCollection(obj, copiedObjects)
            // Попробуем скопировать его как ссылочный
            initialObject = deepCopy(obj, copiedObjects)

            return initialObject as T
        }

        // Используется для клонирования значений полей объектов и занесения исходного объекта в коллекцию сос ссылкой
        private fun<T: Any?> getValueFromCopiedCollection(value: Any?, copiedObjects: MutableMap<Any, Any>): T?{
            if(value == null)
                return null

            // Если объект содержится в коллекции, возвращаем ссылку на его копию. Если это, конечно, не единственный исходный со своим незавершенным клоном.
            if(copiedObjects.containsKey(value))
                return copiedObjects.get(value) as T

            var tempValue: Any? = null

            // Проверка на ссылочный объект и объект-неколлекцию.
            if(!value::class.java.isPrimitive()
                    && value::class.javaPrimitiveType == null
                    && !value::class.java.isArray)
                tempValue = value::class.createInstance()

            // Если это исходный объект, добавляем его сюда.
            if(copiedObjects.isNotEmpty())
                tempValue = deepCopy(value, copiedObjects)

            // Исходный объект кладётся в соответствие своей формирующейся ссылке
            if(tempValue == null)
                tempValue = value
            else copiedObjects.put(value, tempValue)

            return tempValue as T
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

