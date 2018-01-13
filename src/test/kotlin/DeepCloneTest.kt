
import deepclone.CopyUtils
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class DeepClonerTest {

    @Test
    fun testStringClone() {
        val string = "съешь ещё этих мягких французских булок, да выпей чаю"
        val stringClone = CopyUtils.deepCopy(string)

        // склонированный объект не должен быть null
        assertNotNull(stringClone)

        // склонированный объект должен быть равен оригинальному по equals()
        assertEquals(string, stringClone)

        // ссылки у склонированного и оригинального объектов должны быть разные
        assertFalse(string === stringClone)
    }

    @Test
    fun testIntegerPrimitiveClone() {
        val integer = 100500
        val integerClone =  CopyUtils.deepCopy(integer)

        assertNotNull(integerClone)
        assertTrue(integer == integerClone)
    }

    @Test
    fun testIntegerClone() {
        val integer = Integer(100500)
        val integerClone =  CopyUtils.deepCopy(integer)

        assertNotNull(integerClone)
        assertTrue(integer == integerClone)
        assertFalse(integer === integerClone)
    }

    @Test
    fun testListClone() {
        val listOfStrings = listOf("раз", "два", "три")
        val listOfStringsClone =  CopyUtils.deepCopy(listOfStrings) as List<String>

        assertNotNull(listOfStringsClone)
        assertTrue(listOfStringsClone.isNotEmpty())
        assertTrue(listOfStringsClone.size == 3)
    }

    @Test
    fun testSimpleObjectClone() {
        val simpleObject = SimpleObject()
        val simpleObjectClone =  CopyUtils.deepCopy(simpleObject)

        assertNotNull(simpleObjectClone)
        assertTrue(simpleObject == simpleObjectClone)
        assertFalse(simpleObject === simpleObjectClone)
    }

    @Test
    fun testComplexObjectClone() {
        val complexObject = ComplexObject()
        val complexObjectClone =  CopyUtils.deepCopy(complexObject)

        assertNotNull(complexObjectClone)
        assertFalse(complexObject === complexObjectClone)

//        println(complexObject.hashCode())
//        println(complexObjectClone!!.hashCode())
//        println(complexObjectClone)
//        printObject(complexObjectClone)
    }

}

private fun printObject(obj: Any?) {
    if (obj == null) {
        println("null")
        return
    }

    val properties = obj!!::class.memberProperties

    properties.forEach { prop ->
        println(prop.name + " - " + prop.getter.call(obj))
        val field = prop.javaField
        if (field != null) {
            field.isAccessible = true
            val value = field.get(obj)
            println(value)
        }

        //println("The value of the field: " + prop.javaField!!.get(obj))
    }
}

data class SimpleObject(
        var foo: String = "foo",
        var bar: String = "bar"
)

class ComplexObject {
    val noField: String
        get() {
            return "123"
        }

    var a = 1
        get() = field + 1
        set(value) {
            field = value + 5
        }

    val b = 2

    var listExample = mutableListOf(1, 2, 3)
    var mapExample = mutableMapOf(1 to 1, 2 to 2, 3 to 3)
    var arrExample = arrayOf(arrayOf(1, 2, 3), 2, 3)
    var hashMapExample = hashMapOf(1 to 2, 3 to 2, 2 to 3)
    var setExample = setOf(1, 2, 3)
    var nullableInt: Int? = null
    var date = Date()

    val simpleNestedObject = SimpleObject()
}
