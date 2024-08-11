import java.io.*;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenSettingsUpdater {

    private static final String SETTINGS_SECURITY_FILE = "settings-security.xml";
    private static final String SETTINGS_FILE = "settings.xml";


    public static void main(String[] args) {
        String encryptedMasterPassword = args.length > 0 ? args[0] : "test1";
        String encryptedPassword = args.length > 1 ? args[1] : "test2";

        try {
            File settingsSecurityFile = new File(SETTINGS_SECURITY_FILE);
            File settingsFile = new File(SETTINGS_FILE);

            if (!settingsSecurityFile.exists()) {
                generateSettingsSecurityFile(encryptedMasterPassword);
            } else {
                updateSettingsSecurityFile(encryptedMasterPassword);
            }

            if (!settingsFile.exists()) {
                generateSettingsFile(encryptedPassword);
            } else {
                updateSettingsFile(encryptedPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method generates the settings-security.xm file
     *
     * @throws IOException
     */
    private static void generateSettingsSecurityFile(String encryptedMasterPassword) throws IOException {
        File file = new File(SETTINGS_SECURITY_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("<settingsSecurity>\n");
            writer.write("   <master>master_password</master>\n");
            writer.write("</settingsSecurity>\n");
        }
        System.out.println("Generated settings-security.xml");
        updateSettingsSecurityFile(encryptedMasterPassword);
    }

    /**
     * This method reads the security file and updates the master password in the file.
     *
     * @param encryptedMasterPassword encrypted master password
     * @throws IOException
     */
    private static void updateSettingsSecurityFile(String encryptedMasterPassword) throws IOException {
        File file = new File(SETTINGS_SECURITY_FILE);
        String content = new String(Files.readAllBytes(file.toPath()));

        // Regular expression to find content between <master> and </master>
        Pattern pattern = Pattern.compile("<master>.*?</master>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        // Replace the content within <master> tags
        if (matcher.find()) {
            content = matcher.replaceAll("<master>" + encryptedMasterPassword + "</master>");
        }

        // Write the updated content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }

        System.out.println("Updated settings-security.xml with new master password");
    }

    /**
     * This method generates the settings.xml file
     *
     * @throws IOException
     */
    private static void generateSettingsFile(String encryptedPassword) throws IOException {
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
        updateSettingsFile(encryptedPassword);
    }

    /**
     * This method updates the settings.xml file
     *
     * @param encryptedPassword
     * @throws IOException
     */
    private static void updateSettingsFile(String encryptedPassword) throws IOException {

        File file = new File(SETTINGS_FILE);
        String content = new String(Files.readAllBytes(file.toPath()));

        // Regular expression to find content between <master> and </master>
        Pattern pattern = Pattern.compile("<password>.*?</password>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        // Replace the content within <master> tags
        if (matcher.find()) {
            content = matcher.replaceAll("<password>" + encryptedPassword + "</password>");
        }

        // Write the updated content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }

        System.out.println("Updated settings.xml with new Artifactory password");
    }
}