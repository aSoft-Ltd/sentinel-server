package astrab

import kollections.MutableGraph

abstract class Resistive(
    val resistance: Double = 10.0,
    label: String,
) : ElectricalComponent {
    val terminal1 = Terminal("${label}1")
    val terminal2 = Terminal("${label}2")

    override fun onPlacedOn(network: MutableGraph<Terminal, ElectricalComponent>) {
        network.add(terminal1)
        network.add(terminal2)
        network.connect(terminal1, terminal2, this)
        network.connect(terminal2, terminal1, this)
    }
}