package response;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

// TODO: Double check for correct headers!
// TODO: Add a method for retreiving the response code of a Response! (If possible)
public class Response {
  HashMap<Integer, String> responseCodes = new HashMap<Integer, String>();
  final String serverName = "Server: Barbour-Apolinar\r\n";
  FileInputStream content;
  String date, status;

  public Response(FileInputStream in, OutputStream out) {
    this.content = in;
    setDateHeader();
  }

  public Response(OutputStream out) {
    setDateHeader();
  }

  public void setDateHeader() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    this.date = "Date: " + dateFormat.format(calendar.getTime()) + "\r\n";
  }

  public String getDateHeader() {
    return date;
  }

  public String getStatusCode(){
    return status;
  }
}
