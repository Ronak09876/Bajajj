import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java DestinationHashGenerator <roll_number> <file_location>");
            return;
        }

        String rollNumber = args[0];
        String fileLocation = args[1];

        // Step 2: Read and Parse the JSON File
        String jsonContent = readFile(fileLocation);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = (JsonNode) objectMapper.readTree(jsonContent);

        // Step 3: Traverse JSON to find "destination" key
        String destination = findDestination(rootNode);
        if (destination == null) {
            System.out.println("No 'destination' key found in the JSON file.");
            return;
        }

        // Step 4: Generate a random alphanumeric string
        String randomString = generateRandomString(8);

        // Step 5: Generate the hash
        String concatenatedString = rollNumber + destination + randomString;
        String hash = generateHash(concatenatedString);

        // Step 6: Format the output
        String output = hash + ";" + randomString;

        // Print the result
        System.out.println(output);
    }

    // Method to read the content of the file
    public static String readFile(String fileLocation) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    // Method to traverse JSON and find the first "destination"
    /**
     * @param rootNode
     * @return
     */
    public static String findDestination(JsonNode rootNode) {
        // Check if the 'destination' key is present at the root level
        if (rootNode.has("destination")) {
            try {
                return (String) ((Object) rootNode.get("destination")).asText();
            } catch (Exception e) {s
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // If the 'destination' is not directly at the root, traverse nested nodes
        for (JsonNode node : rootNode) {
            if (node.isObject()) {
                String destination = findDestination(node);
                if (destination != null) return destination;
            }
        }

        return null; // If no "destination" key is found
    }

    // Method to generate a random alphanumeric string of given length
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    // Method to generate a hash of the concatenated string
    public static String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
