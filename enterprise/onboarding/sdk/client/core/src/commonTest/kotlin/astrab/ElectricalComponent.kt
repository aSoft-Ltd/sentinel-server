package astrab

import kollections.MutableGraph

interface ElectricalComponent {
    fun onPlacedOn(network: MutableGraph<Terminal, ElectricalComponent>)
}