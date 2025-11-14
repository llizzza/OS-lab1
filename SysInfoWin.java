import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class SysInfoWin {

    public void printSystemInfo() {

        String osName = System.getProperty("os.name");
        System.out.println("OS: " + osName);

        String computerName = System.getenv("COMPUTERNAME");
        System.out.println("Computer Name: " + computerName);

        System.out.println("User: " + System.getProperty("user.name"));

        String arch = System.getProperty("os.arch");
        if (arch.equals("amd64")) {
            arch = "x64 (AMD64)";
        }
        System.out.println("Architecture: " + arch);

        printFormattedMemoryInfo();

        Runtime runtime = Runtime.getRuntime();
        System.out.println("\nProcessors: " + runtime.availableProcessors());

        System.out.println("\nDrives:");
        java.io.File[] roots = java.io.File.listRoots();
        for (java.io.File root : roots) {
            if (root.exists() && root.getTotalSpace() > 0) {
                long totalGB = root.getTotalSpace() / (1024 * 1024 * 1024);
                long freeGB = root.getFreeSpace() / (1024 * 1024 * 1024);
                String driveType = getDriveType(root);
                System.out.println("  - " + root.getPath() + "  (" + driveType + "): " + freeGB + " GB free / " + totalGB + " GB total");
            }
        }
    }

    private void printFormattedMemoryInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;

        long totalPhysicalMemory = sunOsBean.getTotalPhysicalMemorySize() / (1024 * 1024);
        long freePhysicalMemory = sunOsBean.getFreePhysicalMemorySize() / (1024 * 1024);
        long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;
        int memoryLoad = (int) ((usedPhysicalMemory * 100) / totalPhysicalMemory);

        long totalSwapSpace = sunOsBean.getTotalSwapSpaceSize() / (1024 * 1024);
        long freeSwapSpace = sunOsBean.getFreeSwapSpaceSize() / (1024 * 1024);
        long usedSwapSpace = totalSwapSpace - freeSwapSpace;

        System.out.println("RAM: " + usedPhysicalMemory + "MB / " + totalPhysicalMemory + "MB");
        System.out.println("Virtual Memory: " + totalSwapSpace + "MB");
        System.out.println("Memory Load: " + memoryLoad + "%");
        System.out.println("Pagefile: " + usedSwapSpace + "MB / " + totalSwapSpace + "MB");
    }

    private String getDriveType(java.io.File root) {
        return "NTFS";
    }

    public static void main(String[] args) {
        SysInfoWin sysInfo = new SysInfoWin();
        sysInfo.printSystemInfo();
    }
}
