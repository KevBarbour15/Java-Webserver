package response;

import java.io.*;
import java.util.HashMap;

public class Unauthorized extends Response {
  final String codeHeader = "HTTP/1.1 401 Unauthorized\r\n";
  final String status = "401";
  String WWWauth;
  OutputStream clientOutput;
  FileInputStream content;
  String contentType = "Content-Type: none\r\n";
  String contentSize = "Content-Size: 0\r\n";
  HashMap<String, String> htaccessInfo = new HashMap<String, String>();

  public Unauthorized(FileInputStream in, OutputStream out) {
    super(in, out);
    this.content = in;
    this.clientOutput = out;
  }

  public Unauthorized(OutputStream out, HashMap<String, String> map) {
    super(out);
    this.clientOutput = out;
    this.htaccessInfo = map;
  }

  public void sendResponse() throws IOException {
    WWWauth = "WWW-Authenticate: Basic realm=" + htaccessInfo.get("AuthName").trim() + "\r\n";
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(codeHeader.getBytes());
    output.write(date.getBytes());
    output.write(WWWauth.getBytes());
    output.write(serverName.getBytes());
    output.write(contentType.getBytes());
    output.write(contentSize.getBytes());
    clientOutput.write(output.toByteArray());

    output.flush();
    output.close();
    clientOutput.flush();
  }

  public String getStatusCode(){
    return status;
  }
}
