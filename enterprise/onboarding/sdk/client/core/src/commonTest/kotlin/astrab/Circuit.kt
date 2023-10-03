package astrab

import kollections.directedGraphOf
import kollections.undirectedGraphOf
import kotlin.reflect.KClass

class Circuit {
    private val componentCounts = mutableMapOf<KClass<out Any>,Int>()
    val network = directedGraphOf<Terminal, ElectricalComponent>()
    inline fun <reified C : ElectricalComponent> add(component: C): C {
        component.onPlacedOn(network)
        return component
    }

    fun connect(from: Terminal, to: Terminal) = network.apply {
        val wire = Wire()
        connect(from, to, wire)
        connect(to, from, wire)
    }
}