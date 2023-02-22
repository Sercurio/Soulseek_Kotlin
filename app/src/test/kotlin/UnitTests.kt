import fr.sercurio.soulseek.SoulSeekApi
import fr.sercurio.soulseek.repositories.LoginRepository
import fr.sercurio.soulseek.utils.SoulStack
import kotlinx.coroutines.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class UnitTests {
    @Test
    fun soulseekApiLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            val soulSeekApi = SoulSeekApi("DebugApp", "159753")
            delay(2000)
            assert(LoginRepository.getLoginStatus().connected)
        }
    }
}