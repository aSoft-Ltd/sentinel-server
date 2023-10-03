package astrab

import kommander.expect
import kommander.expect
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class CircuitTest {
    @Test
    fun should_easily_connect_different_terminal() {
        Circuit().apply {
            val c1 = add(DCVoltage(10.0))
            val r1 = add(Resistor(10.0))
            connect(c1.positive, r1.terminal1)
            connect(c1.negative, r1.terminal2)

            expect<Int>(network.size).toBe(4)
        }
    }

    @Test
    fun should_easily_identify_loops_in_the_network() {
        Circuit().apply {
            val c1 = add(DCVoltage(10.0))
            val r1 = add(Resistor(10.0))
            connect(c1.positive, r1.terminal1)
            connect(c1.negative, r1.terminal2)

            val loops = network.uniqueLoops()
            expect(loops.size).toBe(1, "$loops")
            val loop = loops.first()
            val terminals = loop.size
            expect(terminals).toBe(4, "There were supposed to be 4 terminals in this loop")
        }
    }
}