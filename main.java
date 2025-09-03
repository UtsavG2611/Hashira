import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class main {
    public static void main(String[] args) {
        try {
            String testCase1 = readJsonFile("/Users/utsavgupta/Desktop/hashira/test1.json");
            String testCase2 = readJsonFile("/Users/utsavgupta/Desktop/hashira/ex.json");

            System.out.println("Test Case 1 Result:");
            processTestCase(testCase1);
            
            System.out.println("\nTest Case 2 Result:");
            processTestCase(testCase2);
        } catch (IOException e) {
            System.out.println("Error reading JSON files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String readJsonFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    private static void processTestCase(String jsonStr) {
        try {
            Map<String, Object> jsonData = parseJson(jsonStr);
            
            Map<String, Object> keys = (Map<String, Object>) jsonData.get("keys");
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString());
            
            System.out.println("n = " + n + ", k = " + k);
            
            double[] roots = new double[k];
            int rootIndex = 0;
            
            for (int i = 1; i <= n; i++) {
                if (jsonData.containsKey(String.valueOf(i)) && rootIndex < k) {
                    Map<String, Object> rootData = (Map<String, Object>) jsonData.get(String.valueOf(i));
                    String base = rootData.get("base").toString();
                    String value = rootData.get("value").toString();
                    
                    double decodedValue = decodeValue(value, base);
                    roots[rootIndex++] = decodedValue;
                    
                    System.out.println("Root " + i + ": " + decodedValue + " (Base " + base + ": " + value + ")");
                }
                
                if (rootIndex >= k) {
                    break;
                }
            }
            
            double constantC = calculateConstantC(roots);
            
            System.out.println("Constant C = " + constantC);
            
        } catch (Exception e) {
            System.out.println("Error processing test case: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static double calculateConstantC(double[] roots) {
        double product = 1.0;
        for (double root : roots) {
            product *= root;
        }
        
        return product;
    }
    
    private static double decodeValue(String value, String baseStr) {
        int base = Integer.parseInt(baseStr);
        
        double result = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int digit;
            
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'a' && c <= 'z') {
                digit = c - 'a' + 10;
            } else if (c >= 'A' && c <= 'Z') {
                digit = c - 'A' + 10;
            } else {
                throw new IllegalArgumentException("Invalid character in value: " + c);
            }
            
            if (digit >= base) {
                throw new IllegalArgumentException("Digit " + digit + " is not valid in base " + base);
            }
            
            result = result * base + digit;
        }
        
        return result;
    }
    
    private static Map<String, Object> parseJson(String jsonStr) {
        Map<String, Object> result = new HashMap<>();
        
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("{")) {
            jsonStr = jsonStr.substring(1);
        }
        if (jsonStr.endsWith("}")) {
            jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        }
        
        int depth = 0;
        StringBuilder currentPart = new StringBuilder();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            
            if (c == '{') {
                depth++;
                currentPart.append(c);
            } else if (c == '}') {
                depth--;
                currentPart.append(c);
            } else if (c == ',' && depth == 0) {
                processPart(currentPart.toString(), result);
                currentPart = new StringBuilder();
            } else {
                currentPart.append(c);
            }
        }
        
        if (currentPart.length() > 0) {
            processPart(currentPart.toString(), result);
        }
        
        return result;
    }
    
    private static void processPart(String part, Map<String, Object> result) {
        part = part.trim();
        if (part.isEmpty()) {
            return;
        }
        
        int colonIndex = -1;
        int depth = 0;
        for (int i = 0; i < part.length(); i++) {
            char c = part.charAt(i);
            
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
            } else if (c == ':' && depth == 0) {
                colonIndex = i;
                break;
            }
        }
        
        if (colonIndex == -1) {
            throw new IllegalArgumentException("Invalid JSON part: " + part);
        }
        
        String key = part.substring(0, colonIndex).trim();
        String value = part.substring(colonIndex + 1).trim();
        
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }
        
        if (value.startsWith("{") && value.endsWith("}")) {
            result.put(key, parseJson(value));
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            result.put(key, value.substring(1, value.length() - 1));
        } else if (value.equals("true")) {
            result.put(key, true);
        } else if (value.equals("false")) {
            result.put(key, false);
        } else if (value.equals("null")) {
            result.put(key, null);
        } else {
            try {
                if (value.contains(".")) {
                    result.put(key, Double.parseDouble(value));
                } else {
                    result.put(key, Integer.parseInt(value));
                }
            } catch (NumberFormatException e) {
                result.put(key, value);
            }
        }
    }
}
