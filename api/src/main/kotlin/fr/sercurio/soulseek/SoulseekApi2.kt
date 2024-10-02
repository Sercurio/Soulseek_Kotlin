package fr.sercurio.soulseek

import fr.sercurio.soulseek.client.LoginService
import fr.sercurio.soulseek.client.ServerSocket
import kotlinx.coroutines.runBlocking
import org.koin.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class SoulseekApi2() : KoinComponent {
  val loginService: LoginService by inject()
}

val myModule = module {
  single { ServerSocket() }
  single { LoginService(get()) }
}

fun main() {
  runBlocking {
    startKoin { modules(myModule) }

    SoulseekApi2().loginService.login("Airmess", "159753") {
      if (it.isConnected) println("Login successful") else println("Error logging in")
    }
    while (true) {}
  }
}
