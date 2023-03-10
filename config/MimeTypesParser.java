package config;

import java.io.*;
import java.util.*;

public class MimeTypesParser {
  public HashMap<String, ArrayList<String>> getMimeTypes() {
    HashMap<String, ArrayList<String>> newMap = new HashMap<String, ArrayList<String>>();

    try (BufferedReader reader = new BufferedReader(new FileReader("conf/mime.types"))) {
      String str;
      String[] newLine;

      while ((str = reader.readLine()) != null) {
        newLine = str.trim().split("\\s+");

        if (newLine.length > 1 && !newLine[0].equals("#")) {
          ArrayList<String> extentions = new ArrayList<String>();

          for (int i = 1; i < newLine.length; i++) {
            extentions.add(newLine[i].trim());
          }

          newMap.put(newLine[0].trim(), extentions);
        }
      }
    } catch (Exception e) {
      System.out.println("Could not read file provided.");
    }

    return newMap;
  }

  public String lookUpExtention(Map<String, ArrayList<String>> map, String target) {
    String[] arr = target.split("/");
    target = arr[arr.length - 1];
    arr = target.split("\\.");

    if (arr.length > 1) {
      target = arr[1].trim();
    } else {
      return "none";
    }

    for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
      ArrayList<String> values = entry.getValue();

      for (String extention : values) {
        if (extention.equals(target)) {
          return entry.getKey();
        }
      }
    }
    return "none";
  }

  public void printMap(Map<String, ArrayList<String>> map) {
    System.out.println("\n ****** Key and Values (arraylist of extensions) of Mime Types map: \n");

    for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
      System.out.println(entry.getKey() + "  --extention(s): ");

      ArrayList<String> values = entry.getValue();
      for (String extention : values) {
        System.out.print(extention + "   ");
      }
      System.out.print("\n\n");
    }
  }
}
