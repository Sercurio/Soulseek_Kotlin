import fr.sercurio.soulseek.SoulseekApi
import fr.sercurio.soulseek.repositories.LoginRepository
import kotlinx.coroutines.*
import org.junit.Test

@ExperimentalCoroutinesApi
class UnitTests {
    @Test
    fun soulseekApiLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            val soulseekApi = object : SoulseekApi("DebugApp", "159753") {}
            delay(2000)
            assert(LoginRepository.getLoginStatus().connected)
        }
    }
}