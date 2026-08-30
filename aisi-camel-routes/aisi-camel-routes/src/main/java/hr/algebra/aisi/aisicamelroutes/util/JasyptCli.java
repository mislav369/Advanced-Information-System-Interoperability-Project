package hr.algebra.aisi.aisicamelroutes.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptCli {

    public static void main(String[] args) {
        String operation = args[0];
        String value = args[1];
        String password = args[2];

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        if (operation.equals("encrypt")) {
            System.out.println("ENC(" + encryptor.encrypt(value) + ")");
        } else {
            System.out.println(encryptor.decrypt(value));
        }
    }
}
