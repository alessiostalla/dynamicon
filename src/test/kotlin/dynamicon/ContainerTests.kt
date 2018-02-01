package dynamicon

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrowAny
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

    "Injection and insertion" {
        val container = Container()
        val arrayList = container.insert(ArrayList<Any>())
        val bean1 = Bean1()
        val bean2 = container.injectAndInsert(bean1)

        bean2 shouldBe bean1
        container.get(Bean1::class.java) shouldBe bean1
        bean1.list shouldBe arrayList
        bean1.arrayList shouldBe arrayList
        bean1.string shouldBe ""
    }

    "Object creation" {
        val container = Container()

        shouldThrowAny { container.create(Bean2::class.java) }

        val arrayList = container.insert(ArrayList<Any>())
        val bean1 = container.create(Bean1::class.java)
        val bean2 = container.create(Bean2::class.java)

        bean1.list shouldBe arrayList
        bean1.arrayList shouldBe arrayList
        bean1.string shouldBe ""

        bean2.list1 shouldBe arrayList
        bean2.list2 shouldBe arrayList
    }

    "Object creation and insertion" {
        val container = Container()
        container.insert(ArrayList<Any>())
        val bean1 = container.createAndInsert(Bean1::class.java)
        val bean2 = container.createAndInsert(Bean2::class.java)
        val bean3 = container.create(DependentBean1::class.java)

        bean3.b1 shouldBe bean1
        bean3.b2 shouldBe bean2
    }

    "Object removal" {
        val container = Container()
        val arrayList = container.insert(ArrayList<Any>())

        container.remove(arrayList) shouldBe true
        container.remove(arrayList) shouldBe false

        container.get(ArrayList::class.java) shouldBe null
        container.get(List::class.java) shouldBe null
        container.get(Object::class.java) shouldBe null
    }

})

class Bean1 {

    var list : List<Any> = ArrayList()
    var arrayList : ArrayList<Any> = ArrayList()
    var string = ""

}

class Bean2(list1 : List<*>, list2: List<Any>) {

    val list1 = list1
    val list2 = list2

}

class DependentBean1(b1 : Bean1) {

    val b1 = b1
    var b2 : Bean2? = null

}