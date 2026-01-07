```
-Xms2g
-Xmx2g
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/app/heapdump.hprof
-XX:+ExitOnOutOfMemoryError

////////////
@PostMapping("/upload")
public void upload(@RequestParam MultipartFile file) throws IOException {
    try (InputStream in = file.getInputStream()) {
        Files.copy(in, Path.of("/data/" + file.getOriginalFilename()));
    }
}

///////////////
@GetMapping("/download/{name}")
public ResponseEntity<StreamingResponseBody> download(@PathVariable String name) {
    Path path = Path.of("/data/" + name);

    StreamingResponseBody stream = output ->
        Files.copy(path, output);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name)
            .body(stream);
}

[Spring Boot buffers uploads in memory by default.]
spring:
  servlet:
    multipart:
      max-file-size: 5GB
      max-request-size: 5GB
      file-size-threshold: 2MB   # after this → disk
      location: /tmp/uploads

[Tomcat can leak memory if clients disconnect mid-transfer.]
server:
  tomcat:
    max-swallow-size: -1
    max-connections: 200
    accept-count: 100
    connection-timeout: 20000

if OutOfMemoryError: Metaspace
-XX:MaxMetaspaceSize=512m

there should be some script to remove tmp files
ls -lh /tmp
ls -lh /tmp/tomcat.*


/////////
@PostMapping("/upload")
public void upload(MultipartFile file, HttpServletRequest request) throws IOException {

    try (InputStream in = file.getInputStream();
         OutputStream out = Files.newOutputStream(Path.of("/data/file.bin"))) {

        byte[] buffer = new byte[8192];
        int len;

        while ((len = in.read(buffer)) != -1) {
            if (!request.isAsyncStarted() && request.isAsyncComplete()) {
                break; // client gone
            }
            out.write(buffer, 0, len);
        }
    }
}

[If using Nginx / Apache, buffering can hide disconnects.]
location /files/ {
    proxy_pass http://app;
    proxy_request_buffering off;
    proxy_buffering off;
    client_body_timeout 20s;
    send_timeout 20s;
}
and server:
  tomcat:
    mbeanregistry:
      enabled: true
```

server:
  port: 8080

  tomcat:
    max-connections: 200
    accept-count: 100
    connection-timeout: 20000

    keep-alive-timeout: 15000
    max-keep-alive-requests: 100

    max-swallow-size: -1
@GetMapping("/download/{name}")
public ResponseEntity<StreamingResponseBody> download(@PathVariable String name) {

    StreamingResponseBody stream = output -> {
        try (InputStream in = Files.newInputStream(Path.of("/data/" + name))) {
            in.transferTo(output);
        } catch (org.apache.catalina.connector.ClientAbortException e) {
            // client disconnected — normal, ignore
        }
    };

    return ResponseEntity.ok().body(stream);
}
@PostMapping("/upload")
public void upload(MultipartFile file) throws IOException {

    try (InputStream in = file.getInputStream();
         OutputStream out = Files.newOutputStream(Path.of("/data/file.bin"))) {

        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    } catch (org.apache.catalina.connector.ClientAbortException e) {
        // Abort upload safely
    }
}
