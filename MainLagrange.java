import java.math.BigInteger; 
import java.util.*; 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class MainLagrange { 
  static class Pt { final int x; final BigInteger y; Pt(int x, BigInteger y){ this.x=x; this.y=y; } } 
  
  static final class BigFraction { 
    final BigInteger numer, denom; 
    static final BigFraction ZERO = new BigFraction(BigInteger.ZERO, BigInteger.ONE); 
    BigFraction(BigInteger n, BigInteger d) { 
      if (d.signum()==0) throw new ArithmeticException("den=0"); 
      if (d.signum()<0) { n=n.negate(); d=d.negate(); } 
      BigInteger g = n.gcd(d); 
      numer = n.divide(g); denom = d.divide(g); 
    } 
    BigFraction add(BigFraction o){ return new BigFraction(numer.multiply(o.denom).add(o.numer.multiply(denom)), denom.multiply(o.denom)); } 
    BigFraction multiply(BigFraction o){ return new BigFraction(numer.multiply(o.numer), denom.multiply(o.denom)); } 
    boolean isInteger(){ return denom.equals(BigInteger.ONE); } 
    public String toString(){ return isInteger()? numer.toString(): numer + "/" + denom; } 
  }

  public static void main(String[] args) throws Exception { 
    try {
      String testCase1 = readJsonFile("/Users/utsavgupta/Desktop/hashira/test1.json");
      String testCase2 = readJsonFile("/Users/utsavgupta/Desktop/hashira/ex.json");

      System.out.println("Test Case 1 Result:");
      processJson(testCase1);
      
      System.out.println("\nTest Case 2 Result:");
      processJson(testCase2);
    } catch (IOException e) {
      System.out.println("Error reading JSON files: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private static String readJsonFile(String filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }
  
  private static void processJson(String json) throws Exception {
    Map<String, Object> jsonData = parseJson(json);
    
    Map<String, Object> keys = (Map<String, Object>) jsonData.get("keys");
    int n = Integer.parseInt(keys.get("n").toString());
    int k = Integer.parseInt(keys.get("k").toString());
    
    System.out.println("n = " + n + ", k = " + k);

    List<Pt> pts = new ArrayList<>();
    for (int i = 1; i <= n; i++) {
      String key = String.valueOf(i);
      if (jsonData.containsKey(key)) {
        Map<String, Object> rootData = (Map<String, Object>) jsonData.get(key);
        int x = Integer.parseInt(key);
        int base = Integer.parseInt(rootData.get("base").toString());
        String val = rootData.get("value").toString().toLowerCase(Locale.ROOT);
        BigInteger decodedValue = parseBase(val, base);
        pts.add(new Pt(x, decodedValue));
        
        System.out.println("Root " + x + ": " + decodedValue + " (Base " + base + ": " + val + ")");
      }
    }

    pts.sort(Comparator.comparingInt(p -> p.x));
    BigInteger c = lagrangeAtZero(pts.subList(0, k));
    System.out.println("Constant C = " + c.toString());
  }

  static BigInteger parseBase(String s, int base) { 
    final String digits = "0123456789abcdefghijklmnopqrstuvwxyz"; 
    BigInteger res = BigInteger.ZERO; 
    BigInteger B = BigInteger.valueOf(base); 
    for (char ch : s.toCharArray()) { 
      int v = digits.indexOf(ch); 
      if (v < 0 || v >= base) throw new IllegalArgumentException("Bad digit "+ch+" for base "+base); 
      res = res.multiply(B).add(BigInteger.valueOf(v)); 
    } 
    return res; 
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

  static BigInteger lagrangeAtZero(List<Pt> pts) { 
    BigFraction sum = BigFraction.ZERO; 
    for (int i = 0; i < pts.size(); i++) { 
      BigInteger xi = BigInteger.valueOf(pts.get(i).x); 
      BigInteger yi = pts.get(i).y; 
      BigFraction term = new BigFraction(yi, BigInteger.ONE); 
      for (int j = 0; j < pts.size(); j++) if (i != j) { 
        BigInteger xj = BigInteger.valueOf(pts.get(j).x); 
        term = term.multiply(new BigFraction(xj.negate(), xi.subtract(xj))); 
      } 
      sum = sum.add(term); 
    } 
    if (!sum.isInteger()) throw new IllegalStateException("Non-integer C; got " + sum); 
    return sum.numer; 
  } 

}