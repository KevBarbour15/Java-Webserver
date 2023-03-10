package config;

import java.util.HashMap;

public class HttpdConf {
  int port;
  String logFile, documentRoot, directoryIndex,
      scriptAliasSymbolic, scriptAliasAbsolute, scriptAlias;

  public HttpdConf(HashMap<String, String> httpConfInfo) {
    this.port = Integer.parseInt(httpConfInfo.get("Listen"));
    this.logFile = httpConfInfo.get("LogFile");
    this.documentRoot = httpConfInfo.get("DocumentRoot");
    this.directoryIndex = httpConfInfo.get("DirectoryIndex");
    this.scriptAlias = httpConfInfo.get("ScriptAlias");
  };

  public int getPort() {
    return port;
  }

  public String getLogFile() {
    return logFile.trim();
  }

  public String getDocumentRoot() {
    return documentRoot.trim();
  }

  public String getDirectoryIndex() {
    return directoryIndex.trim();
  }

  public String getScriptAlias() {
    return scriptAlias.trim();
  }

  public String getScriptAliasSymbolic() {
    this.scriptAliasSymbolic = "/cgi-bin/";
    return scriptAliasSymbolic.trim();
  }

  public String getScriptAliasAbsolute() {
    return scriptAliasAbsolute.trim();
  }
}