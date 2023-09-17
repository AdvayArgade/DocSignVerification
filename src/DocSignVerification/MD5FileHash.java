package DocSignVerification;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5FileHash {
    public static String doFinal(String filename) {
//        if (args.length != 1) {
//            //System.out.println("Usage: java MD5FileHash <filename>");
//            return;
//        }


        String fileName = filename;

        try {
            FileInputStream fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            MessageDigest md = MessageDigest.getInstance("MD5");

            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] digest = md.digest();
            fis.close();

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }


            System.out.println("MD5 Hash: " + sb.toString());
            return sb.toString();
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        catch (NoSuchAlgorithmException e) {
            System.err.println("MD5 algorithm not available: " + e.getMessage());
        }
        return "";
    }

    public static void main(String[] args) {
        String output = doFinal("D:/Advay/SY BTech/OOP/smalltestfile.txt");
        try{
            RSAFileEncryption rsaFileEncryption = new RSAFileEncryption();
            System.out.println(rsaFileEncryption.encryptString(output));
        }catch (Exception e){
            System.out.println("Error in RSA.");
        }


        if(output.equals(""))System.out.println("Null.");
    }
}
