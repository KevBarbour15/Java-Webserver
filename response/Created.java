package response;

import java.io.*;

public class Created extends Response {
  final String codeHeader = "HTTP/1.1 201 Created\r\n";
  final String status = "201";
  OutputStream clientOutput;
  FileInputStream content;
  String contentType = "Content-Type: none\r\n";
  String contentSize = "Content-Size: 0\r\n";

  public Created(FileInputStream in, OutputStream out, String contentType) throws IOException {
    super(in, out);
    this.content = in;
    this.clientOutput = out;
    this.contentType = contentType;
  }

  public Created(OutputStream out) {
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
