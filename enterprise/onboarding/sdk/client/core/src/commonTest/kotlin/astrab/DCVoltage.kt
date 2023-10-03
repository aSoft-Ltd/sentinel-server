package astrab

import kollections.MutableGraph

class DCVoltage(val voltage: Double = 10.0,val label: String = "DC") : ElectricalComponent {
    val positive = Terminal("+")
    val negative = Terminal("-")

    override fun onPlacedOn(network: MutableGraph<Terminal, ElectricalComponent>) {
        network.add(positive)
        network.add(negative)
        network.connect(negative, positive, this)
    }
}