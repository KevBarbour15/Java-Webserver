package response;

import java.io.*;

public class BadRequest extends Response {
  final String codeHeader = "HTTP/1.1 400 Bad Request\r\n";
  final String status = "400";
  OutputStream clientOutput;
  FileInputStream content;
  String contentType = "Content-Type: none\r\n";
  String contentSize = "Content-Size: 0\r\n";

  public BadRequest(OutputStream out) {
    super(out);
    this.clientOutput = out;
  }

  public void sendResponse() throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(codeHeader.getBytes());
    output.write(date.getBytes());
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