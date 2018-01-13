import kotlin.collections.*
import java.util.*

fun main(args: Array<String>) {
    val a = A()
    a.BObj.aClass = a!!

    var copy = CopyUtils.deepCopy(a)

    CopyUtils.PrintObject(copy)
    println("______________")
    CopyUtils.PrintObject(a.BObj)
}

class A() {
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

    var BObj = B()

}

class B() {
    var c = "foo"
    var d = "bar"

    var aClass: A? = null
}