import java.io.*;
import java.nio.file.*;
import java.util.*;


public class SysInfoLinux {


   public void printSystemInfo() {


       System.out.println("OS: " + getOSInfo());
       System.out.println("Kernel: " + getKernelInfo());
       System.out.println("Architecture: " + System.getProperty("os.arch"));
       System.out.println("Hostname: " + getHostname());
       System.out.println("User: " + System.getProperty("user.name"));


       printMemoryInfo();


       Runtime runtime = Runtime.getRuntime();
       System.out.println("Processors: " + runtime.availableProcessors());
       System.out.println("Load average: " + getLoadAverage());


       printVirtualMemoryInfo();


       System.out.println("\nDrives:");
       printDrivesInfo();
   }


   private String getOSInfo() {
       try {
           List<String> lines = Files.readAllLines(Paths.get("/etc/os-release"));
           for (String line : lines) {
               if (line.startsWith("PRETTY_NAME=")) {
                   return line.substring("PRETTY_NAME=".length()).replace("\"", "").trim();
               }
           }
       } catch (Exception e) {
       }
       return System.getProperty("os.name") + " " + System.getProperty("os.version");
   }


   private String getKernelInfo() {
       try {
           Process process = Runtime.getRuntime().exec("uname -r");
           BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
           return "Linux " + reader.readLine();
       } catch (Exception e) {
           return "Unknown";
       }
   }


   private String getHostname() {
       try {
           return Files.readAllLines(Paths.get("/etc/hostname")).get(0).trim();
       } catch (Exception e) {
           return "Unknown";
       }
   }


   private void printMemoryInfo() {
       try {
           List<String> lines = Files.readAllLines(Paths.get("/proc/meminfo"));
           long memTotal = 0, memFree = 0, memAvailable = 0, swapTotal = 0, swapFree = 0;


           for (String line : lines) {
               String[] parts = line.split("\\s+");
               if (parts.length < 2) continue;


               String key = parts[0];
               long value = Long.parseLong(parts[1]);


               if ("MemTotal:".equals(key)) {
                   memTotal = value;
               } else if ("MemAvailable:".equals(key)) {
                   memAvailable = value;
               } else if ("MemFree:".equals(key)) {
                   memFree = value;
               } else if ("SwapTotal:".equals(key)) {
                   swapTotal = value;
               } else if ("SwapFree:".equals(key)) {
                   swapFree = value;
               }
           }


           long freeMB = memAvailable > 0 ? memAvailable / 1024 : memFree / 1024;
           System.out.println("RAM: " + freeMB + "MB free / " + (memTotal / 1024) + "MB total");


           if (swapTotal > 0) {
               System.out.println("Swap: " + (swapTotal / 1024) + "MB total / " + (swapFree / 1024) + "MB free");
           }
       } catch (Exception e) {
           System.out.println("Memory information unavailable");
       }
   }


   private void printVirtualMemoryInfo() {
       try {
           List<String> lines = Files.readAllLines(Paths.get("/proc/meminfo"));


           for (String line : lines) {
               String[] parts = line.split("\\s+");
               if (parts.length < 2) continue;


               String key = parts[0];
               long value = Long.parseLong(parts[1]);


               if ("VmallocTotal:".equals(key)) {
                   System.out.println("Virtual memory: " + (value / 1024) + " MB");
                   return;
               }
           }


           System.out.println("Virtual memory: Information unavailable");
       } catch (Exception e) {
           System.out.println("Virtual memory: Information unavailable");
       }
   }


   private String getLoadAverage() {
       try {
           List<String> lines = Files.readAllLines(Paths.get("/proc/loadavg"));
           String[] parts = lines.get(0).split(" ");
           return parts[0] + ", " + parts[1] + ", " + parts[2];
       } catch (Exception e) {
           return "Unknown";
       }
   }


   private void printDrivesInfo() {
       try {
           List<String> lines = Files.readAllLines(Paths.get("/proc/mounts"));


           for (String line : lines) {
               String[] parts = line.split(" ");
               if (parts.length >= 4 && isRelevantDrive(parts[0], parts[2], parts[1])) {
                   File mount = new File(parts[1]);
                   if (mount.exists()) {
                       long totalGB = mount.getTotalSpace() / (1024 * 1024 * 1024);
                       long freeGB = mount.getFreeSpace() / (1024 * 1024 * 1024);
                       System.out.println("  " + parts[1] + "     " + parts[2] + "     " +
                               freeGB + "GB free / " + totalGB + "GB total");
                   }
               }
           }
       } catch (Exception e) {
           System.out.println("  Drives information unavailable");
       }
   }


   private boolean isRelevantDrive(String device, String fsType, String mountPoint) {
       return (device.startsWith("/dev/") || fsType.contains("fuse")) &&
               !mountPoint.equals("/proc") && !mountPoint.equals("/sys") &&
               !mountPoint.equals("/dev") && !mountPoint.startsWith("/sys/") &&
               !mountPoint.startsWith("/proc/") && !mountPoint.startsWith("/dev/");
   }


   public static void main(String[] args) {
       new SysInfoLinux().printSystemInfo();
   }
}
