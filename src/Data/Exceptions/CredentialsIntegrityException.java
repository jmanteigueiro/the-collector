package Data.Exceptions;

/*** Este tipo de exceção é lançado quando o HMAC-SHA256 não é verificado corretamente */
public class CredentialsIntegrityException extends Exception{
    public CredentialsIntegrityException(){
        super("Integridade das credenciais comprometida.");
    }
}