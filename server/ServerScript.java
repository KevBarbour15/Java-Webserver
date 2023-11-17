package server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import response.InternalServerError;
import response.NotFound;
import response.Ok;
import response.Response;

public class ServerScript {
  String path;
  OutputStream clientOutput;
  HttpRequest request;

  public ServerScript(String path, HttpRequest request, OutputStream clientOutput) {
    this.path = path;
    this.clientOutput = clientOutput;
    this.request = request;
  }

  public Response RunScript() throws IOException {

    String osName = System.getProperty("os.name").toLowerCase();

    String requestBody = request.getEntireRequest();
    request.setArgsMap();

    ProcessBuilder builder;

    File file = new File(path);
    if (!file.exists()) {
      System.out.print("FILE or DirectoryIndex doesnt exist: " + path);
      NotFound notfound = new NotFound(clientOutput);
      notfound.sendResponse();
      return notfound;
    }

    // Has not been tested with scripts other than perl.
    if (path.contains("perl") || path.contains(".pl")) {
      if (osName.contains("windows")) {
        builder = new ProcessBuilder("cmd", "/c", "perl -T \"" + path);
      } else {
        builder = new ProcessBuilder(path);
      }
    } else {
      builder = new ProcessBuilder(path);
    }

    Map<String, String> env = builder.environment();
    env.put("HTTP_VERSION", request.getProtocol());
    env.put("SERVER_PROTOCOL", request.getMethod());
    env.put("QUERY_STRING", request.getAbsolutePath());
    env.put("REQUEST_BODY", requestBody);
    if (request.getArgsMap() != null) {
      if (request.getMethod().equals("PUT") || request.getMethod().equals("POST")) {
        request.getArgsMap().forEach((key, value) -> {
          env.put(key, value);
        });
      }
    }

    try {
      Process process = builder.start();

      // 200
      Ok ok = new Ok(clientOutput);
      ok.sendCGIResponse(process);
      return ok;

    } catch (IOException e) {
      // 500
      e.printStackTrace();
      InternalServerError internalServerError = new InternalServerError(clientOutput);
      internalServerError.sendResponse();
      return internalServerError;
    }
  }
}