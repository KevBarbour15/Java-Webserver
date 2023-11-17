package server;

import java.io.*;
import java.util.*;

public class HttpRequest {
  private ArrayList<String> requestMethods = new ArrayList<String>();
  public InputStream request;
  public String requestString, method, firstLine, protocol, absolutePath, user;
  public HashMap<String, String> argsMap;
  public HashMap<String, String> headersMap;
  public boolean isValidRequest, hasArgs;
  public String[] requestBody;
  public int statusCode;

  public void requestMethodsInit() {
    requestMethods.add("GET");
    requestMethods.add("HEAD");
    requestMethods.add("POST");
    requestMethods.add("PUT");
    requestMethods.add("DELETE");
  }

  public String getNoArgsPath() {
    if (hasArgs)
      return absolutePath.substring(0, absolutePath.indexOf('?'));
    else
      return getAbsolutePath();
  }

  public boolean isValidRequest() {
    return isValidRequest;
  }

  public String getHeader() {
    return firstLine;
  }

  public String getMethod() {
    return method;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getEntireRequest() {
    return requestString;
  }

  public String getUser() {
    return user;
  }

  public String getAbsolutePath() {
    String tempPath = absolutePath;
    String[] tempArr = tempPath.split("\\?");

    if (tempArr.length > 1) {
      this.hasArgs = true;
    } else {
      this.hasArgs = false;
    }
    return absolutePath;
  }

  public String[] getRequestBody() {
    return requestBody;
  }

  public boolean hasArgs() {
    return hasArgs;
  }

  public void setArgsMap() {
    String tempPath = absolutePath;
    HashMap<String, String> map = new HashMap<String, String>();
    String[] args = tempPath.split("\\?");

    if (args.length > 1) {
      args = args[1].split("&");
      for (int i = 0; i < args.length; i++) {
        System.out.println(args[i]);
        String tempArr[] = args[i].split("=");
        map.put(tempArr[0], tempArr[1]);
      }
    }

    map.entrySet().forEach(entry -> {
      // System.out.println(entry.getKey() + " " + entry.getValue());
    });

    this.argsMap = map;
  }

  public HashMap<String, String> getArgsMap() {
    return argsMap;
  }

  public String getAuthHeader() {
    String temp = requestString;
    String[] lineArr = temp.split("\n");
    String[] lineArr2;
    String tempLine;

    for (int i = 0; i < lineArr.length; i++) {
      tempLine = lineArr[i];
      lineArr2 = tempLine.split(" ");
      if (lineArr2[0].equals("Authorization:")) {
        return lineArr2[2].trim();
      }
    }
    return "-1";
  }

  public String getAccessPath(String path) {
    String absolutePath = path + ".htaccess";
    return absolutePath;
  }

  public boolean hasHtaccess(String path) {
    String newPath = getAccessPath(path);
    File file = new File(newPath);

    if (file.exists()) {
      return true;
    }
    return false;
  }

  public void setMethod() {
    String tempRequest, tempFirstLine;
    String[] lines, firstLineArr;

    tempRequest = requestString;
    lines = tempRequest.split("\n");
    this.firstLine = lines[0];
    tempFirstLine = firstLine;
    firstLineArr = tempFirstLine.split(" ");

    if (firstLineArr.length > 1) {
      this.absolutePath = firstLineArr[1];
    }

    for (int i = 0; i < firstLineArr.length; i++) {
      isValidRequest = false;

      if (requestMethods.contains(firstLineArr[i])) {
        this.method = firstLineArr[i];
        isValidRequest = true;
        break;
      }
    }
  }

  public void setProtocol() {
    String tempFirstLine;
    String firstLineArr[];

    tempFirstLine = firstLine;
    firstLineArr = tempFirstLine.split(" ");
    this.protocol = firstLineArr[firstLineArr.length - 1];
  }

  public void setRequestBody() {
    String tempRequestString = requestString;
    String[] tempReqBodyArr = tempRequestString.split("\n");
    String[] temp = new String[tempReqBodyArr.length - 1];

    for (int i = 1; i < tempReqBodyArr.length; i++) {
      temp[i - 1] = tempReqBodyArr[i];
    }
    this.requestBody = temp;
  }

  public void setUser() {
    String tempRequestString = requestString;
    String[] tempReqBodyArr = tempRequestString.split("\n");

    for (int i = 0; i < tempReqBodyArr.length; i++) {
      String tempLine = tempReqBodyArr[i];
      String[] tempLineArr = tempLine.split(":");
      if (tempLineArr[0].equals("User-Agent")) {
        this.user = tempLineArr[1];
        break;
      }
    }
  }

  public void readInRequest(InputStream in) throws IOException {
    StringBuilder string = new StringBuilder();

    requestMethodsInit();

    do {
      string.append((char) in.read());
    } while (in.available() > 0);
    this.requestString = string.toString();

    setMethod();
    isValidRequest();
    setProtocol();
    setRequestBody();
    setUser();
  }
}