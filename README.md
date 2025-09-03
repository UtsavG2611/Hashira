# Hashira - Quadratic Equation Constant C Finder

This project contains two implementations for finding the constant C in a quadratic equation ax² + bx + c using roots provided in JSON format.

## Implementations

### 1. main.java

A simple implementation that:
- Uses a custom JSON parser
- Reads input from JSON files (test1.json and ex.json)
- Decodes values from different number bases to decimal
- Calculates the constant C using the product of roots
- Uses double for calculations

### 2. MainLagrange.java

An advanced implementation that:
- Uses a custom JSON parser (no external dependencies)
- Reads input from JSON files (test1.json and ex.json)
- Implements Lagrange interpolation to find the constant C
- Uses BigInteger for precise calculations with large numbers
- Implements a custom BigFraction class for exact rational arithmetic

## Input Files

- `test1.json`: Contains the first test case with 4 roots
- `ex.json`: Contains the second test case with 10 roots

## Running the Code

### main.java
```
javac main.java
java main
```

### MainLagrange.java
```
javac MainLagrange.java
java MainLagrange
```

## Results

The implementations produce different constant C values due to precision differences:

### Test Case 1
- main.java: Constant C = 336.0
- MainLagrange.java: Constant C = 3

### Test Case 2
- main.java: Constant C = 6.825131192247421E124 (double representation with limited precision)
- MainLagrange.java: Constant C = -6290016743746469796 (BigInteger representation with exact precision)

The difference in results is due to the different mathematical approaches used:
- main.java simply multiplies the roots together
- MainLagrange.java uses Lagrange interpolation to find the constant term in the polynomial