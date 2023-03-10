package Authentication;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;

public class Htpassword {
  public boolean authenticated = false;
  private HashMap<String, String> passwords;

  public Htpassword(String filename) throws IOException {
    this.passwords = new HashMap<String, String>();
    parseLine(filename);
  }

  protected void parseLine(String line) {
    try (BufferedReader reader = new BufferedReader(new FileReader(line))) {
      String str;
      String[] newLine;

      while ((str = reader.readLine()) != null) {

        newLine = str.split(":");

        if (newLine.length == 2) {
          passwords.put(newLine[0], newLine[1].replace("{SHA}", "").trim());
        }
      }

    } catch (Exception e) {
      System.out.println("unable to read password file.");
    }
  }

  public boolean isAuthorized(String authInfo) {
    String username, password;

    String credentials = new String(
        Base64.getDecoder().decode(authInfo),
        Charset.forName("UTF-8"));

    String[] tokens = credentials.split(":");
    username = tokens[0].trim();
    password = tokens[1].trim();

    if (verifyPassword(username, password)) {
      return true;
    }
    return false;
  }

  private boolean verifyPassword(String username, String password) {
    String encryptedPassword = encryptClearPassword(password);

    if (encryptedPassword.equals(passwords.get(username))) {
      return true;
    }
    return false;
  }

  private String encryptClearPassword(String password) {
    try {
      MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
      byte[] result = mDigest.digest(password.getBytes());

      return Base64.getEncoder().encodeToString(result);
    } catch (Exception e) {
      return "";
    }
  }
}
