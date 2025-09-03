import java.util.HashMap;
import java.util.Map;

public class main {
    public static void main(String[] args) {
        // Test case 1
        String testCase1 = "{ \n" +
                "    \"keys\": { \n" +
                "        \"n\": 4, \n" +
                "        \"k\": 3 \n" +
                "    }, \n" +
                "    \"1\": { \n" +
                "        \"base\": \"10\", \n" +
                "        \"value\": \"4\" \n" +
                "    }, \n" +
                "    \"2\": { \n" +
                "        \"base\": \"2\", \n" +
                "        \"value\": \"111\" \n" +
                "    }, \n" +
                "    \"3\": { \n" +
                "        \"base\": \"10\", \n" +
                "        \"value\": \"12\" \n" +
                "    }, \n" +
                "    \"6\": { \n" +
                "        \"base\": \"4\", \n" +
                "        \"value\": \"213\" \n" +
                "    } \n" +
                "}"; 

        // Test case 2
        String testCase2 = "{ \n" +
                "\"keys\": { \n" +
                "    \"n\": 10, \n" +
                "    \"k\": 7 \n" +
                "  }, \n" +
                "  \"1\": { \n" +
                "    \"base\": \"6\", \n" +
                "    \"value\": \"13444211440455345511\" \n" +
                "  }, \n" +
                "  \"2\": { \n" +
                "    \"base\": \"15\", \n" +
                "    \"value\": \"aed7015a346d635\" \n" +
                "  }, \n" +
                "  \"3\": { \n" +
                "    \"base\": \"15\", \n" +
                "    \"value\": \"6aeeb69631c227c\" \n" +
                "  }, \n" +
                "  \"4\": { \n" +
                "    \"base\": \"16\", \n" +
                "    \"value\": \"e1b5e05623d881f\" \n" +
                "  }, \n" +
                "  \"5\": { \n" +
                "    \"base\": \"8\", \n" +
                "    \"value\": \"316034514573652620673\" \n" +
                "  }, \n" +
                "  \"6\": { \n" +
                "    \"base\": \"3\", \n" +
                "    \"value\": \"2122212201122002221120200210011020220200\" \n" +
                "  }, \n" +
                "  \"7\": { \n" +
                "    \"base\": \"3\", \n" +
                "    \"value\": \"20120221122211000100210021102001201112121\" \n" +
                "  }, \n" +
                "  \"8\": { \n" +
                "    \"base\": \"6\", \n" +
                "    \"value\": \"20220554335330240002224253\" \n" +
                "  }, \n" +
                "  \"9\": { \n" +
                "    \"base\": \"12\", \n" +
                "    \"value\": \"45153788322a1255483\" \n" +
                "  }, \n" +
                "  \"10\": { \n" +
                "    \"base\": \"7\", \n" +
                "    \"value\": \"1101613130313526312514143\" \n" +
                "  } \n" +
                "}"; 

        // Process test cases
        System.out.println("Test Case 1 Result:");
        processTestCase(testCase1);
        
        System.out.println("\nTest Case 2 Result:");
        processTestCase(testCase2);
    }
    
    private static void processTestCase(String jsonStr) {
        try {
            // Parse the JSON string manually
            Map<String, Object> jsonData = parseJson(jsonStr);
            
            // Extract keys
            Map<String, Object> keys = (Map<String, Object>) jsonData.get("keys");
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString());
            
            System.out.println("n = " + n + ", k = " + k);
            
            // Collect and decode the roots
            double[] roots = new double[k];
            int rootIndex = 0;
            
            for (int i = 1; i <= n; i++) {
                if (jsonData.containsKey(String.valueOf(i)) && rootIndex < k) {
                    Map<String, Object> rootData = (Map<String, Object>) jsonData.get(String.valueOf(i));
                    String base = rootData.get("base").toString();
                    String value = rootData.get("value").toString();
                    
                    // Decode the value from the given base to decimal
                    double decodedValue = decodeValue(value, base);
                    roots[rootIndex++] = decodedValue;
                    
                    System.out.println("Root " + i + ": " + decodedValue + " (Base " + base + ": " + value + ")");
                }
                
                if (rootIndex >= k) {
                    break;
                }
            }
            
            // Calculate the constant C using Vieta's formulas
            // For a quadratic equation ax^2 + bx + c = 0 with roots r and s:
            // r + s = -b/a and r*s = c/a
            // Therefore, c = a * r * s
            
            // Assuming a = 1 for simplicity (ax^2 + bx + c becomes x^2 + bx + c)
            double constantC = calculateConstantC(roots);
            
            System.out.println("Constant C = " + constantC);
            
        } catch (Exception e) {
            System.out.println("Error processing test case: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static double calculateConstantC(double[] roots) {
        // For a quadratic equation with roots r and s: c = a * r * s (assuming a = 1)
        // For higher degree polynomials, we need to use the appropriate formula
        
        // For this problem, we're told it's a quadratic equation ax^2 + bx + c
        // So we need the product of all roots
        double product = 1.0;
        for (double root : roots) {
            product *= root;
        }
        
        return product; // Assuming a = 1
    }
    
    private static double decodeValue(String value, String baseStr) {
        int base = Integer.parseInt(baseStr);
        
        // Handle bases > 10 which may include letters
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
        // A simple JSON parser for the given format
        Map<String, Object> result = new HashMap<>();
        
        // Remove curly braces and whitespace
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("{")) {
            jsonStr = jsonStr.substring(1);
        }
        if (jsonStr.endsWith("}")) {
            jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        }
        
        // Split by commas not inside nested objects
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
        
        // Process the last part
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
        
        // Find the first colon not inside nested objects
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
        
        // Remove quotes from key
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }
        
        // Process value based on its type
        if (value.startsWith("{") && value.endsWith("}")) {
            // Nested object
            result.put(key, parseJson(value));
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            // String value
            result.put(key, value.substring(1, value.length() - 1));
        } else if (value.equals("true")) {
            result.put(key, true);
        } else if (value.equals("false")) {
            result.put(key, false);
        } else if (value.equals("null")) {
            result.put(key, null);
        } else {
            // Try to parse as number
            try {
                if (value.contains(".")) {
                    result.put(key, Double.parseDouble(value));
                } else {
                    result.put(key, Integer.parseInt(value));
                }
            } catch (NumberFormatException e) {
                // If not a number, store as string
                result.put(key, value);
            }
        }
    }
}
