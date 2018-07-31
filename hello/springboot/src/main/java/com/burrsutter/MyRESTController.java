package com.burrsutter;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class MyRESTController {
   final String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
   private int count = 0; // simple counter to see lifecycle

   @RequestMapping("/")
   public String sayHello() {
       System.out.println("/ " + hostname);
       return "Hello from Spring Boot! " + count++ + " on " + hostname + "\n";
   }

   @RequestMapping("/sysresources") 
   public String getSystemResources() {
        long memory = Runtime.getRuntime().maxMemory();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("/sysresources " + hostname);
        return 
            " Memory: " + (memory / 1024 / 1024) +
            " Cores: " + cores + "\n";
   }

   @RequestMapping("/consume") 
   public String consumeSome() {
        System.out.println("/consume " + hostname);
        
        Runtime rt = Runtime.getRuntime();
        StringBuilder sb = new StringBuilder();
        long maxMemory = rt.maxMemory();
        long usedMemory = 0;
        // while usedMemory is less than 80% of Max
        while (((float) usedMemory / maxMemory) < 0.80) {
            sb.append(System.nanoTime() + sb.toString());
            usedMemory = rt.totalMemory();
        }
        String msg = "Allocated about 80% (" + humanReadableByteCount(usedMemory, false) + ") of the max allowed JVM memory size ("
            + humanReadableByteCount(maxMemory, false) + ")";
        System.out.println(msg);
        return msg + "\n";
   }

   @RequestMapping(method = RequestMethod.GET, value = "/health")
   public ResponseEntity<String> health() {
        return ResponseEntity.status(HttpStatus.OK)
            .body("I am fine, thank you\n");
   }

   public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}