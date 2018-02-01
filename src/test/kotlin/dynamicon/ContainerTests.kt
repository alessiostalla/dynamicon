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

})