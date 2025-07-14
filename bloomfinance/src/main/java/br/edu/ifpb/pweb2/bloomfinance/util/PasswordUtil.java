package br.edu.ifpb.pweb2.bloomfinance.util;

import org.mindrot.jbcrypt.BCrypt;

public abstract class PasswordUtil {

    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}


