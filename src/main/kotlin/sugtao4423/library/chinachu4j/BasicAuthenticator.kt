package sugtao4423.library.chinachu4j

import java.net.Authenticator
import java.net.PasswordAuthentication

class BasicAuthenticator(private val username: String, private val password: String) : Authenticator() {

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(username, password.toCharArray())
    }

}