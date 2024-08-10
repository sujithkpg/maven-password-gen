import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class MavenSettingsUpdater {

    //    private static final String DEFAULT_MAVEN_PATH = "C:\\Program Files\\apache-maven-3.9.6\\bin\\mvn"; // Change as needed
    private static final String DEFAULT_MAVEN_PATH = "C:\\Program Files\\apache-maven-3.9.6"; // Change as needed
    private static final String DEFAULT_JAVA_PATH = "C:\\Program Files\\Amazon Corretto\\jdk11.0.22_7"; // Change as needed
    private static final String SETTINGS_SECURITY_FILE = "settings-security.xml";
    private static final String SETTINGS_FILE = "settings.xml";

    public static String mavenPath = null;
    public static String javaPath = null;

    public static void main(String[] args) {
        mavenPath = args.length > 0 ? args[0] : DEFAULT_MAVEN_PATH;
        javaPath = args.length > 1 ? args[1] : DEFAULT_JAVA_PATH;

        try {
            File settingsSecurityFile = new File(SETTINGS_SECURITY_FILE);
            File settingsFile = new File(SETTINGS_FILE);

            executeScript("encrypt-password.bat", mavenPath, javaPath, "my_secure_master_password.txt");
//            if (!settingsSecurityFile.exists()) {
//                generateSettingsSecurityFile(mavenPath, javaPath);
//            } else {
//                updateSettingsSecurityFile(mavenPath, javaPath);
//            }
//
//            if (!settingsFile.exists()) {
//                generateSettingsFile();
//            } else {
//                updateSettingsFile(mavenPath, javaPath);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateSettingsSecurityFile(String mavenPath, String javaPath) throws IOException {
        File file = new File(SETTINGS_SECURITY_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("<settingsSecurity>\n");
            writer.write("   <master>master_password</master>\n");
            writer.write("</settingsSecurity>\n");
        }
        System.out.println("Generated settings-security.xml");
        promptForMasterPassword(mavenPath, javaPath);
    }

    private static void updateSettingsSecurityFile(String mavenPath, String javaPath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the new master password: ");
        String masterPassword = scanner.nextLine();
        String encryptedMasterPassword = encryptMasterPassword(mavenPath, javaPath, masterPassword);

        File file = new File(SETTINGS_SECURITY_FILE);
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        content = content.replace("<master>master_password</master>", "<master>" + encryptedMasterPassword + "</master>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        System.out.println("Updated settings-security.xml with new master password");
    }

    private static void generateSettingsFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(SETTINGS_FILE)))) {
            writer.write("<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n");
            writer.write("          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            writer.write("          xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd\">\n");
            writer.write("    <servers>\n");
            writer.write("        <server>\n");
            writer.write("            <id>repoid1</id>\n");
            writer.write("            <username>ldap_user_name</username>\n");
            writer.write("            <password>encrypted_ldap_password</password>\n");
            writer.write("        </server>\n");
            writer.write("        <server>\n");
            writer.write("            <id>repoid2</id>\n");
            writer.write("            <username>ldap_user_name</username>\n");
            writer.write("            <password>encrypted_ldap_password</password>\n");
            writer.write("        </server>\n");
            writer.write("    </servers>\n");
            writer.write("</settings>\n");
        }
        System.out.println("Generated settings.xml");
        promptForArtifactoryPassword(mavenPath, javaPath);
    }

    private static void updateSettingsFile(String mavenPath, String javaPath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Artifactory password: ");
        String artifactoryPassword = scanner.nextLine();
        String encryptedPassword = encryptPassword(mavenPath, javaPath, artifactoryPassword);

        File file = new File(SETTINGS_FILE);
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        content = content.replace("<password>encrypted_ldap_password</password>", "<password>" + encryptedPassword + "</password>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        System.out.println("Updated settings.xml with new Artifactory password");
    }

    private static void promptForMasterPassword(String mavenPath, String javaPath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the master password: ");
        String masterPassword = scanner.nextLine();
        String encryptedMasterPassword = encryptMasterPassword(mavenPath, javaPath, masterPassword);

        File file = new File(SETTINGS_SECURITY_FILE);
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        content = content.replace("<master>master_password</master>", "<master>" + encryptedMasterPassword + "</master>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        System.out.println("Updated settings-security.xml with new master password");
    }

    private static void promptForArtifactoryPassword(String mavenPath, String javaPath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Artifactory password: ");
        String artifactoryPassword = scanner.nextLine();
        String encryptedPassword = encryptPassword(mavenPath, javaPath, artifactoryPassword);

        File file = new File(SETTINGS_FILE);
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        content = content.replace("<password>encrypted_ldap_password</password>", "<password>" + encryptedPassword + "</password>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        System.out.println("Updated settings.xml with new Artifactory password");
    }

    private static String encryptMasterPassword(String mavenPath, String javaPath, String masterPassword) throws IOException {
//        ProcessBuilder processBuilder = new ProcessBuilder(
//                "\"" + mavenPath + "\\bin\\mvn\""+
//                " --encrypt-master-password"+
//                " -Dmaven.compiler.executable=\"" + javaPath + "\\bin\\javac.exe\""
//        );

        // Create a temporary file for the password
        Path tempPasswordFile = Files.createTempFile("masterPassword", ".txt");

        // Write the master password to the temporary file
        try (BufferedWriter writer = Files.newBufferedWriter(tempPasswordFile, StandardOpenOption.WRITE)) {
            writer.write(masterPassword);
            writer.newLine(); // Ensure the password is followed by a newline
        }

        String mvnCommand = mavenPath + "\\bin\\mvn.cmd";
        String javacCommand = javaPath + "\\bin\\javac.exe";
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(
                mvnCommand,
                "--encrypt-master-password",
                "-Dmaven.compiler.executable=" + javacCommand
        );
        processBuilder.redirectErrorStream(true);

        // Redirect input to the temporary file
        processBuilder.redirectInput(tempPasswordFile.toFile());
        Process process = processBuilder.start();

        // Capture the process output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        // Ensure the process has completed
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("Process interrupted: " + e.getMessage());
            throw new IOException("Process was interrupted", e);
        }

        // Delete the temporary file
        Files.deleteIfExists(tempPasswordFile);

        // Process output to extract the password
        result = output.toString().trim();
        if (result.startsWith("{") && result.endsWith("}")) {
            return result;
        } else {
            throw new IOException("Unexpected format of encrypted password: " + result);
        }
    }

    private static String encryptPassword(String mavenPath, String javaPath, String password) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "\"" + mavenPath + "\\bin\\mvn.cmd\"",
                "--encrypt-password",
                "-Dmaven.compiler.executable=\"" + javaPath + "\\bin\\javac.exe\""
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(password);
            writer.flush();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine().trim();
        }
    }

    private static String executeScript(String scriptPath, String mavenPath, String javaPath, String masterPasswordFile) throws IOException {
        // Create a temporary file for the password
        Path tempPasswordFile = Files.createTempFile("masterPassword", ".txt");

        // Write the master password to the temporary file
        try (BufferedWriter writer = Files.newBufferedWriter(tempPasswordFile, StandardOpenOption.WRITE)) {
            writer.write("dskfsdfnsdf");
            writer.newLine(); // Ensure the password is followed by a newline
        }

        // Prepare the command to execute the batch script
        String batchScriptPath = "encrypt-password.bat"; // Update this path
        ProcessBuilder processBuilder = new ProcessBuilder(
                "cmd.exe", "/c", batchScriptPath, "\"" + mavenPath + "\"", "\"" + javaPath + "\""

        );
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Capture the process output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        // Ensure the process has completed
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("Process interrupted: " + e.getMessage());
            throw new IOException("Process was interrupted", e);
        }

        // Delete the temporary file
        Files.deleteIfExists(tempPasswordFile);

        // Process output to extract the password
        String result = output.toString().trim();
        System.out.println("RESULT+++++"+result);
        if (result.startsWith("{") && result.endsWith("}")) {
            return result;
        } else {
            throw new IOException("Unexpected format of encrypted password: " + result);
        }
    }
}
