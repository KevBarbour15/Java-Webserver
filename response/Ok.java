package response;

import java.io.*;

public class Ok extends Response {
  final String codeHeader = "HTTP/1.1 200 OK\r\n";
  final String status = "200";
  OutputStream clientOutput;
  FileInputStream content;
  String contentType, contentSize;

  public Ok(FileInputStream in, OutputStream out, String contentType) throws IOException {
    super(in, out);
    this.content = in;
    this.clientOutput = out;
    this.contentType = contentType;
  }

  public Ok(OutputStream out) {
    super(out);
    this.clientOutput = out;
  }

  public void sendResponse() throws IOException {
    clientOutput.write((codeHeader).getBytes());
    clientOutput.write((date).getBytes());
    clientOutput.write((serverName).getBytes());
    clientOutput.write(("Content-type: " + contentType + "\r\n").getBytes());
    clientOutput.write(("Content-length: " + content.available() + "\r\n").getBytes());
    clientOutput.write(("Access-Control-Allow-Origin: *\r\n").getBytes());
    clientOutput.write(("Access-Control-Allow-Methods: *\r\n").getBytes());
    clientOutput.write(("\r\n").getBytes());
    clientOutput.write(content.readAllBytes());
    clientOutput.flush();
  }

  public void sendHeadResponse() throws IOException {
    contentType = "Content-Type: html\r\n";
    contentSize = "Content-Size: 0\r\n";
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

  public void sendCGIResponse(Process process) throws IOException {
    clientOutput.write((codeHeader).getBytes());
    clientOutput.write((date).getBytes());
    clientOutput.write((serverName).getBytes());

    process.getInputStream().transferTo(clientOutput);

    clientOutput.flush();
  }

  public String getStatusCode(){
    return status;
  }
}