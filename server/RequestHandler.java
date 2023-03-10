package server;

import java.io.*;
import java.net.Socket;
import java.util.*;

import Authentication.Htpassword;
import config.*;
import response.*;

public class RequestHandler implements Runnable {
  Socket client;
  InputStream in;
  OutputStream clientOutput;
  FileInputStream contents;
  HttpRequest request;
  HttpdConf httpConf;
  HashMap<String, ArrayList<String>> mimeTypes;
  Response response;
  String responseCode;

  public RequestHandler(Socket client, HttpRequest request, HttpdConf httpConf,
      HashMap<String, ArrayList<String>> mimeTypes) throws IOException {

    this.client = client;
    this.clientOutput = client.getOutputStream();
    this.request = request;
    this.httpConf = httpConf;
    this.mimeTypes = mimeTypes;

  }

  public void run() {
    try {

      in = client.getInputStream();
      request.readInRequest(in);
      HandleRequest();
      in.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void HandleRequest() throws IOException {

    String documentPath = httpConf.getDocumentRoot();

    if (request.isValidRequest()) {
      
      documentPath += request.getAbsolutePath();
      MimeTypesParser mtParser = new MimeTypesParser();

      if (request.hasHtaccess(documentPath)) {
        HtAccessParser accessParser = new HtAccessParser();
        HashMap<String, String> htaccessMap = accessParser.ParseFile();
        Htpassword htpass = new Htpassword(htaccessMap.get("AuthUserFile"));
        String requestInfo = request.getAuthHeader();

        if (!requestInfo.equals("-1")) {
          if (htpass.isAuthorized(requestInfo)) {
            documentPath += "/index.html";
          } else {
            Forbidden forbidden = new Forbidden(clientOutput);
            forbidden.sendResponse();
            response = forbidden;
            clientOutput.close();
            return;
          }
        } else {
          Unauthorized unauthorized = new Unauthorized(clientOutput, htaccessMap);
          unauthorized.sendResponse();
          response = unauthorized;
          clientOutput.close();
          return;
        }
      }
      System.out.println("Document Path: " + documentPath);
      System.out.println("Script Alias: " + httpConf.getScriptAliasSymbolic());
      if (documentPath.contains(httpConf.getScriptAliasSymbolic())) {

        ServerScript script = new ServerScript(httpConf.getDocumentRoot() + request.getNoArgsPath(), request,
            clientOutput);
        response = script.RunScript();

      } else {

        if (request.getMethod().equals("GET")) {
          if (request.getAbsolutePath().equals("/")) {
            documentPath = httpConf.getDocumentRoot();

            documentPath += File.separator + httpConf.getDirectoryIndex();
            contents = new FileInputStream((documentPath));

            Ok ok = new Ok(contents, clientOutput, "index.html");
            ok.sendResponse();
            response = ok;
            contents.close();

          }
          if (request.getMethod().equals("PUT")) {
            try {
              File file = new File(documentPath);
              if (file.createNewFile()) {
                System.out.println("File created: " + documentPath);
                Created created = new Created(clientOutput);
                created.sendResponse();
                response = created;
              } else {
                System.out.println("File already exists.");
                Ok ok = new Ok(clientOutput);
                ok.sendResponse();
                response = ok;
              }
            } catch (Exception e) {
              System.out.println("Could not create file");
              InternalServerError internalServerError = new InternalServerError(clientOutput);
              internalServerError.sendResponse();
              response = internalServerError;
            }
          } else {

            try {

              String contentType = mtParser.lookUpExtention(mimeTypes, documentPath);
              contents = new FileInputStream(documentPath);
              Ok ok = new Ok(contents, clientOutput, contentType);
              ok.sendResponse();
              response = ok;
              contents.close();

            } catch (Exception e) {
              e.printStackTrace();
              NotFound notFound = new NotFound(clientOutput);
              notFound.sendResponse();
              response = notFound;
            }
          }
        } else if (request.getMethod().equals("HEAD")) {
          Ok ok = new Ok(clientOutput);
          ok.sendHeadResponse();
          response = ok;

        } else if (request.getMethod().equals("POST")) {
          String contentType = mtParser.lookUpExtention(mimeTypes, documentPath);
          contents = new FileInputStream(documentPath);
          Ok ok = new Ok(contents, clientOutput, contentType);

          ok.sendResponse();
          response = ok;
          contents.close();

        } else if (request.getMethod().equals("DELETE")) {
          File file = new File(documentPath);
          if (file.delete()) {
            System.out.println("Deleted the file: " + file.getName());
            Ok ok = new Ok(clientOutput);
            ok.sendResponse();
            response = ok;
          } else {
            System.out.println("Failed to delete the file.");
            InternalServerError internalServerError = new InternalServerError(clientOutput);
            internalServerError.sendResponse();
            response = internalServerError;
          }

        } else {
          InternalServerError internalServerError = new InternalServerError(clientOutput);
          internalServerError.sendResponse();
          response = internalServerError;
        }
      }

    } else {
      BadRequest badRequest = new BadRequest(clientOutput);
      badRequest.sendResponse();
      response = badRequest;
    }

    if (request != null) {
      ServerLogger logger = new ServerLogger(this);
      logger.LogServerActivity(httpConf, request);
    }
  }

  public void PrintRequestBody() throws IOException {
    System.out.print(request.getHeader());
  }
}
