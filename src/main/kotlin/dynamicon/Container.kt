package dynamicon

import java.lang.reflect.Constructor
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberExtensionProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

class Container {

    protected val byClass : MutableMap<Class<in Any>, ManagedObjects> = HashMap()
    protected val byName : MutableMap<String, ManagedObjects> = HashMap()

    fun <T : Any> insert(obj : T, vararg names : String) : T {
        val managedObjects = ensureManagedObjects(obj.javaClass as Class<in Any>)
        managedObjects.objects.add(obj)
        managedObjects.others.forEach {
            it.objects.add(obj)
        }
        return obj
    }

    fun <T : Any> get(type : Class<in T>) : T? {
        val results = byClass[type]
        if(results != null && !results.objects.isEmpty()) {
            return results.objects[0] as T
        }
        return null
    }

    //fun <T : Any> getAll(type : Class<in Any>) : List<T> {}

    fun <T : Any> inject(obj : T) : T {
        obj.javaClass.kotlin.memberProperties.forEach {
            if(it is KMutableProperty1) {
                val dependency : Any? = get(it.returnType.jvmErasure.java)
                if(dependency != null) {
                    it.setter.call(obj, dependency)
                }
            }
        }
        return obj
    }

    fun <T : Any> injectAndInsert(obj : T, vararg names : String) : T {
        return insert(inject(obj), *names)
    }

    fun <T : Any> create(type : Class<T>) : T {
        type.constructors.sortedByDescending { it.parameterCount }.forEach {
            val arguments = ArrayList<Any?>()
            it.parameterTypes.forEach {
                arguments.add(get(it))
            }
            if(!arguments.contains(null)) {
                return inject(it.newInstance(*arguments.toArray()) as T)
            }
        }
        throw InstantiationException()
    }

    fun <T : Any> createAndInsert(type : Class<T>, vararg names : String) : T {
        return insert(create(type), *names)
    }

    fun remove(obj: Any) : Boolean {
        val results = byClass[obj.javaClass]
        if(results != null) {
            //TODO GC?
            val removed = results.objects.remove(obj)
            if(removed) {
                results.others.forEach {
                    it.objects.remove(obj)
                }
            }
            return removed
        }
        return false
    }

    protected fun ensureManagedObjects(javaClass: Class<in Any>): ManagedObjects {
        var managedObjects = byClass[javaClass]
        if (managedObjects == null) {
            managedObjects = ManagedObjects()
            byClass[javaClass] = managedObjects
            if(javaClass.superclass != null) {
                val other = ensureManagedObjects(javaClass.superclass)
                managedObjects.others.add(other)
                managedObjects.others.addAll(other.others)
            }
            javaClass.interfaces.forEach {
                val other = ensureManagedObjects(it as Class<in Any>)
                managedObjects.others.add(other)
                managedObjects.others.addAll(other.others)
            }
        }
        return managedObjects
    }

}

class ManagedObjects {

    val others  : MutableSet<ManagedObjects> = HashSet()
    val objects : MutableList<Any> = ArrayList()

}