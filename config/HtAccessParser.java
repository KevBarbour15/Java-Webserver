package config;

import java.io.*;
import java.util.*;

public class HtAccessParser {
  public List<String> keys = new ArrayList<>();

  private void keysInit() {
    keys.add("AuthUserFile");
    keys.add("AuthType");
    keys.add("AuthName");
  }

  public HashMap<String, String> ParseFile() throws IOException {
    HashMap<String, String> newMap = new HashMap<String, String>();

    keysInit();

    try (BufferedReader reader = new BufferedReader(
        new FileReader("server/public_html/protected/.htaccess"))) {
      String str, line;
      String[] newLine;

      while ((str = reader.readLine()) != null) {
        newLine = str.split(" ");
        if (keys.contains(newLine[0])) {
          if (newLine.length == 2) {
            newMap.put(newLine[0], newLine[1].trim());
          } else {
            line = buildOutValue(newLine);
            newMap.put(newLine[0], line.trim());
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Could not read file provided.");
    }
    return newMap;
  }

  public String buildOutValue(String[] line) {
    String newValue = "";
    String[] lineArr;

    for (int i = 0; i < line.length; i++) {
      newValue += line[i] + " ";
    }

    lineArr = newValue.split("\"");
    newValue = "";

    for (int i = 1; i < lineArr.length - 1; i++) {
      newValue = lineArr[i] + " ";
    }
    newValue.trim();
    return newValue;
  }
}