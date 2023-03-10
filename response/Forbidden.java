package response;

import java.io.*;

public class Forbidden extends Response {
  final String codeHeader = "HTTP/1.1 403 Forbidden\r\n";
  final String status = "403";
  OutputStream clientOutput;
  FileInputStream content;
  String contentType = "Content-Type: none\r\n";
  String contentSize = "Content-Size: 0\r\n";

  public Forbidden(FileInputStream in, OutputStream out) {
    super(in, out);
    this.content = in;
    this.clientOutput = out;
  }

  public Forbidden(OutputStream out) {
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
