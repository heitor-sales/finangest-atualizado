package br.edu.ifpb.pweb2.finangest.Util;

import org.mindrot.jbcrypt.BCrypt;


public abstract class PasswordUtil {

    public static String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public static boolean checkPass(String plainPassword, String hashePassword){
        if (BCrypt.checkpw(plainPassword, hashePassword)){
            return true;
        }else {
            return false;
        }
    }
}
