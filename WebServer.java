import java.io.*;
import java.net.*;
import java.util.*;

import config.*;
import server.*;

public class WebServer {
  public static void main(String[] args) throws IOException {
    HttpdConfParser confToParse = new HttpdConfParser();
    HashMap<String, String> confParsed = confToParse.ParseFile();
    HttpdConf httpdConfObj = new HttpdConf(confParsed);

    MimeTypesParser newMT = new MimeTypesParser();
    HashMap<String, ArrayList<String>> mimeTypes = newMT.getMimeTypes();
    HttpRequest request = new HttpRequest();
    ServerSocket server = null;

    try {
      server = new ServerSocket((httpdConfObj.getPort()));
      server.setReuseAddress(true);
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true) {
      Socket client = server.accept();
      Thread thread = new Thread(new RequestHandler(client, request, httpdConfObj, mimeTypes));

      try {
        thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      thread.run();
    }
  }
}
