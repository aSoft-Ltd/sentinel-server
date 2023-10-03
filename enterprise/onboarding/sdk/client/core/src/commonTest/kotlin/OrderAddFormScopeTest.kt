import kase.Failure
import kase.Loading
import kase.Pending
import kase.Success
import koncurrent.later.await
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.fail

class OrderAddFormScopeTest {
    val scope = PiCortexAppScopeTest()

    @Test
    fun should_not_crash_when_order_add_form_has_been_initialized() = runTest {
        println("Order add scope")
        val s = scope.orderAdd
        val res = s.initialize().await()
        when(val value=s.ui.value) {
            is Loading -> fail("Should not be loading")
            is Success -> println("Worked")
            is Pending -> fail("Should not be in a pending state")
            is Failure -> {
                value.cause.printStackTrace()
                fail("Should not fail")
            }
        }
    }
}