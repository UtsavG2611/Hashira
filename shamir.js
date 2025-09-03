const fs = require('fs');

class ShamirSecretSharing {
    constructor() {
        this.testCases = [];
    }

    convertToDecimal(base, value) {
        const baseNum = parseInt(base);
        let result = 0n;
        const bigBase = BigInt(baseNum);
        const getDigitValue = (char) => {
            if (char >= '0' && char <= '9') {
                return parseInt(char);
            } else if (char >= 'a' && char <= 'z') {
                return char.charCodeAt(0) - 'a'.charCodeAt(0) + 10;
            } else if (char >= 'A' && char <= 'Z') {
                return char.charCodeAt(0) - 'A'.charCodeAt(0) + 10;
            }
            return 0;
        };

        for (let i = 0; i < value.length; i++) {
            const digit = getDigitValue(value[i]);
            result = result * bigBase + BigInt(digit);
        }
        
        console.log(`Converting: base ${base}, value "${value}" → decimal ${result.toString()}`);
        return result;
    }

    loadTestCase(testCaseData, caseNumber) {
        try {
            this.testCases.push({
                caseNumber: caseNumber,
                data: testCaseData
            });
            console.log(`✅ Test Case ${caseNumber} loaded successfully`);
        } catch (error) {
            console.error(`❌ Error loading Test Case ${caseNumber}:`, error.message);
        }
    }

    extractPoints(testCase) {
        const points = [];
        const n = testCase.keys.n;
        const k = testCase.keys.k;
        
        console.log(`\nExtracting points: n=${n}, k=${k}`);
        console.log(`Polynomial degree: ${k-1}`);
        
        for (let i = 1; i <= n; i++) {
            if (testCase[i.toString()]) {
                const point = testCase[i.toString()];
                const x = BigInt(i);
                const y = this.convertToDecimal(point.base, point.value);
                points.push([x, y]);
                console.log(`Point ${i}: (${x}, ${y})`);
            }
        }
        
        return points;
    }

    divideForInterpolation(a, b) {
        return Number(a) / Number(b);
    }

    lagrangeInterpolation(points, k) {
        const selectedPoints = points.slice(0, k);
        console.log(`\nUsing ${k} points for Lagrange interpolation:`);
        selectedPoints.forEach((point, idx) => {
            console.log(`Point ${idx + 1}: (${point[0]}, ${point[1]})`);
        });

        let constantTerm = 0;

        for (let i = 0; i < selectedPoints.length; i++) {
            const xi = selectedPoints[i][0];
            const yi = selectedPoints[i][1];

            let numerator = 1n;
            let denominator = 1n;

            for (let j = 0; j < selectedPoints.length; j++) {
                if (i !== j) {
                    const xj = selectedPoints[j][0];
                    numerator *= (0n - xj);
                    denominator *= (xi - xj);
                }
            }

            const li = this.divideForInterpolation(numerator, denominator);
            const contribution = Number(yi) * li;
            
            constantTerm += contribution;
            
            console.log(`L${i}(0) = ${numerator}/${denominator} = ${li}`);
            console.log(`Contribution = ${yi} * ${li} = ${contribution}`);
        }

        return Math.round(constantTerm);
    }

    solveTestCase(testCaseObj) {
        const { caseNumber, data } = testCaseObj;
        
        console.log('\n' + '='.repeat(60));
        console.log(`SOLVING TEST CASE ${caseNumber}`);
        console.log('='.repeat(60));

        const points = this.extractPoints(data);
        const k = data.keys.k;
        const n = data.keys.n;

        if (points.length < k) {
            throw new Error(`Insufficient points: need ${k}, got ${points.length}`);
        }

        const secret = this.lagrangeInterpolation(points, k);

        const result = {
            testCase: caseNumber,
            n: n,
            k: k,
            polynomialDegree: k - 1,
            pointsAvailable: points.length,
            pointsUsed: k,
            constantTerm: secret,
            secret: secret
        };

        console.log('\n🎯 RESULT:');
        console.log(`Secret (constant term c): ${secret}`);
        
        return result;
    }

    solveAllTestCases() {
        const results = [];
        
        this.testCases.forEach((testCaseObj) => {
            try {
                const result = this.solveTestCase(testCaseObj);
                results.push(result);
            } catch (error) {
                console.error(`❌ Error in Test Case ${testCaseObj.caseNumber}:`, error.message);
                results.push({
                    testCase: testCaseObj.caseNumber,
                    error: error.message
                });
            }
        });

        return results;
    }

    displayFinalResults(results) {
        console.log('\n' + '='.repeat(70));
        console.log('🏆 FINAL SUBMISSION RESULTS');
        console.log('='.repeat(70));

        results.forEach((result) => {
            if (result.error) {
                console.log(`❌ Test Case ${result.testCase}: Error - ${result.error}`);
            } else {
                console.log(`✅ Test Case ${result.testCase}: Secret = ${result.secret}`);
                console.log(`   Polynomial: degree ${result.polynomialDegree}, used ${result.pointsUsed}/${result.pointsAvailable} points`);
            }
        });
        console.log('\n📋 SUBMISSION SUMMARY:');
        results.forEach((result) => {
            if (!result.error) {
                console.log(`Test Case ${result.testCase}: ${result.secret}`);
            }
        });
    }

    saveResults(results) {
        const output = {
            assignment: "Hashira Placements Assignment",
            method: "Shamir's Secret Sharing with Lagrange Interpolation",
            results: results.map(result => {
                if (result.error) {
                    return {
                        testCase: result.testCase,
                        error: result.error
                    };
                }
                return {
                    testCase: result.testCase,
                    secret: result.secret,
                    polynomialDegree: result.polynomialDegree,
                    pointsUsed: result.pointsUsed
                };
            })
        };

        fs.writeFileSync('results.json', JSON.stringify(output, null, 2));
        console.log('\n💾 Results saved to results.json');
    }
}

function loadJsonFile(filePath) {
    try {
        const data = fs.readFileSync(filePath, 'utf8');
        return JSON.parse(data);
    } catch (error) {
        console.error(`Error loading file ${filePath}:`, error.message);
        process.exit(1);
    }
}

function main() {
    const shamir = new ShamirSecretSharing();
    
    // Load test cases from JSON files
    const testCase1 = loadJsonFile('./test1.json');
    const testCase2 = loadJsonFile('./ex.json');
    
    shamir.loadTestCase(testCase1, 1);
    shamir.loadTestCase(testCase2, 2);
    
    // Solve all test cases
    const results = shamir.solveAllTestCases();
    
    // Display and save results
    shamir.displayFinalResults(results);
    shamir.saveResults(results);
}

main();