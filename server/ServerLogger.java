package server;

import java.io.File;
import java.io.IOException;
import java.lang.StringBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import config.HttpdConf;

public class ServerLogger {
    RequestHandler requestHandler;
    StringBuilder logString = new StringBuilder();

    public ServerLogger(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void LogServerActivity(HttpdConf httpdconf, HttpRequest request) throws IOException {

      File logFile = new File(requestHandler.httpConf.getLogFile());
        logString.append(requestHandler.client.getInetAddress().getHostAddress());

        logString.append(request.getUser());
        if (requestHandler.response != null) {
            logString.append("[");
            logString.append(requestHandler.response.getDateHeader().replace("\n", "").replace("\r", ""));
            logString.append("]");
            logString.append(" ");
            logString.append('"');
            logString.append(requestHandler.request.getHeader().replace("\n", "").replace("\r", ""));
            logString.append('"');
            logString.append(" ");
            logString.append(requestHandler.response.getStatusCode());
            logString.append("\n");
            // logString.append(requestHandler.contents.available());
            logString.append("\n");
            
        } else {
            logString.append(" - ");
            logString.append("\n");
        }

        WriteToLog(logFile, httpdconf.getLogFile());
    }

    public void WriteToLog(File logFile, String path) throws IOException {
        if (logFile.createNewFile()) {
            Files.write(Paths.get(requestHandler.httpConf.getLogFile()), logString.toString().getBytes(), StandardOpenOption.APPEND);
        } else {
            Files.write(Paths.get(requestHandler.httpConf.getLogFile()), logString.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }
}