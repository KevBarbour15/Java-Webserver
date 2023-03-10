package config;

import java.io.*;
import java.util.*;

public class HttpdConfParser {

  public List<String> keys = new ArrayList<>();

  private void keysInit() {
    keys.add("Listen");
    keys.add("LogFile");
    keys.add("ScriptAlias");
    keys.add("DirectoryIndex");
    keys.add("DocumentRoot");
  }

  public HashMap<String, String> ParseFile() throws IOException {
    HashMap<String, String> newMap = new HashMap<String, String>();

    keysInit();

    try (BufferedReader reader = new BufferedReader(new FileReader("conf/httpd.conf"))) {
      String str, line;
      String[] newLine;

      while ((str = reader.readLine()) != null) {
        newLine = str.split(" ");
        if (keys.contains(newLine[0])) {
          line = buildOutValue(newLine);
          newMap.put(newLine[0], line.trim());
        }
      }

      newMap.put("DirectoryIndex", "index.html");

    } catch (Exception e) {
      System.out.println("Could not read file provided.");
    }
    return newMap;
  }

  public String buildOutValue(String[] line) {
    String newValue = "";
    String[] lineArr;
    for (int i = 1; i < line.length; i++) {
      if (!line[i].equals("/cgi-bin/")) {
        newValue += line[i] + " ";
      }
    }

    if (line[0].equals("Listen")) {
      return newValue.trim();
    }

    lineArr = newValue.split("\"");
    newValue = "";

    for (int i = 1; i < lineArr.length - 1; i++) {
      newValue = lineArr[i] + " ";
    }
  
    return newValue.trim();
  }

  public void printMap(Map<String, String> map) {
    System.out.println("\n *** Key and Values in HttpdConfig Map:\n");
    map.forEach((key, value) -> System.out.println(key + " --->  " + value + "\n"));
  }
}
