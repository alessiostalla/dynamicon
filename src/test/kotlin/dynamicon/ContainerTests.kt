package dynamicon

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ContainerTests : StringSpec({

    "Object insertion" {
        val container = Container()
        val arrayList = container.insert(ArrayList<Any>())

        container.get(ArrayList::class.java) shouldBe arrayList
        container.get(List::class.java) shouldBe arrayList
        container.get(Object::class.java) shouldBe arrayList
        container.get(String::class.java) shouldBe null
    }

    "Injection" {
        val container = Container()
        val arrayList = container.insert(ArrayList<Any>())
        val bean1 = container.inject(Bean1())

        bean1.list shouldBe arrayList
        bean1.arrayList shouldBe arrayList
        bean1.string shouldBe ""
    }

})

class Bean1 {

    var list : List<Any> = ArrayList()
    var arrayList : ArrayList<Any> = ArrayList()
    var string = ""

}