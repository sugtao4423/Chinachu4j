package sugtao4423.library.chinachu4j;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class BasicAuthenticator extends Authenticator{

    private String username;
    private String password;

    public BasicAuthenticator(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(username, password.toCharArray());
    }

}