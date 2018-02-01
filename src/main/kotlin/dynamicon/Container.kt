package dynamicon

class Container {

    protected val byClass : MutableMap<Class<in Any>, ManagedObjects> = HashMap()
    protected val byName : MutableMap<String, ManagedObjects> = HashMap()

    fun <T : Any> insert(obj : T, vararg names : String) : T {
        val managedObjects = ensureManagedObjects(obj.javaClass as Class<in Any>)
        val managedObject = ManagedObject(obj)
        managedObjects.objects.add(managedObject)
        managedObjects.others.forEach {
            it.objects.add(managedObject)
        }
        return obj
    }

    fun <T : Any> get(type : Class<in T>) : T? {
        val results = byClass[type]
        if(results != null && !results.objects.isEmpty()) {
            return results.objects[0].obj as T
        }
        return null
    }

    //fun <T : Any> getAll(type : Class<in Any>) : List<T> {}

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
    val objects : MutableList<ManagedObject> = ArrayList()

}

class ManagedObject(obj : Any) {

    val obj = obj

}