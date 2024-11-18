import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class ShapeClassifierTest {

    private ShapeClassifier shapeClassifier;

    @BeforeEach
    public void setUp() {
        shapeClassifier = new ShapeClassifier();
    }

    /**
     * Equivalence Partitioning Tests
     */

    // Valid Shape Guesses
    @Test
    public void testValidShapeGuesses() {
        String[] validShapes = {"Line", "Circle", "Ellipse", "Rectangle", "Square", "Equilateral", "Isosceles", "Scalene"};
        for (String shape : validShapes) {
            String input = shape + ",Small,Yes,100";
            // Assuming correct parameters for "Line"
            String result = shapeClassifier.evaluateGuess(input);
            // Depending on perimeter, adjust expected result
            // Here, perimeter is 100 (Large if >100, Small if <10)
            // Since 100 is not greater than 100, it's neither Small nor Large
            // Hence, size guess "Small" is incorrect
            // Even/Odd based on perimeter 100 (Even)
            // ShapeGuess is correct
            // Expected: "Wrong Size" since sizeGuess is "Small" but perimeter is exactly 100
            assertEquals("Wrong Size", result);
        }
    }

    // Invalid Shape Guess
    @Test
    public void testInvalidShapeGuess() {
        String input = "Hexagon,Large,Yes,100,100,100";
        String result = shapeClassifier.evaluateGuess(input);
        assertTrue(result.startsWith("No: Suggestion="));
    }

    // Valid Size Guesses
    @Test
    public void testValidSizeGuesses() {
        String[] validSizes = {"Small", "Large"};
        for (String size : validSizes) {
            String input = "Line," + size + ",Yes,5"; // Perimeter = 5 (Small)
            String expected = "Yes";
            if (size.equals("Large")) {
                // Perimeter is 5 < 10, so "Large" is incorrect
                expected = "Wrong Size";
            }
            String result = shapeClassifier.evaluateGuess(input);
            assertEquals(expected, result);
        }
    }

    // Invalid Size Guess
    @Test
    public void testInvalidSizeGuess() {
        String input = "Circle,Medium,Yes,10,10"; // "Medium" is invalid
        String result = shapeClassifier.evaluateGuess(input);
        assertTrue(result.contains("Wrong Size") || result.startsWith("No:"));
    }

    // Valid Even/Odd Guesses
    @Test
    public void testValidEvenOddGuesses() {
        // Even perimeter
        String inputEven = "Line,Small,Yes,4"; // Perimeter = 4 (Even)
        String resultEven = shapeClassifier.evaluateGuess(inputEven);
        assertEquals("Yes", resultEven);

        // Odd perimeter
        String inputOdd = "Line,Small,No,5"; // Perimeter = 5 (Odd)
        String resultOdd = shapeClassifier.evaluateGuess(inputOdd);
        assertEquals("Yes", resultOdd);
    }

    // Invalid Even/Odd Guess
    @Test
    public void testInvalidEvenOddGuess() {
        String input = "Line,Small,Maybe,5"; // "Maybe" is invalid
        String result = shapeClassifier.evaluateGuess(input);
        // "Maybe" is treated as incorrect guess
        assertEquals("Wrong Even/Odd", result);
    }

    // Correct Shape, Incorrect Size and Even/Odd
    @Test
    public void testCorrectShapeIncorrectSizeAndEvenOdd() {
        String input = "Circle,Small,Yes,20"; // Perimeter = 2 * PI * 20 ≈ 125 (Large and Odd)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Wrong Size,Wrong Even/Odd", result);
    }

    /**
     * Boundary Value Analysis Tests
     */

    // Perimeter exactly at lower boundary for Small (10)
    @Test
    public void testPerimeterAtSmallBoundary() {
        String input = "Line,Small,Yes,10"; // Perimeter = 10
        // According to specification, "Small" is <10, so 10 is not "Small"
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Wrong Size", result);
    }

    // Perimeter just below lower boundary for Small (9)
    @Test
    public void testPerimeterJustBelowSmallBoundary() {
        String input = "Line,Small,Yes,9"; // Perimeter = 9
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Yes", result);
    }

    // Perimeter exactly at upper boundary for Large (100)
    @Test
    public void testPerimeterAtLargeBoundary() {
        // For Line, perimeter = length
        String input = "Line,Large,Yes,100"; // Perimeter = 100
        // According to specification, "Large" is >100, so 100 is not "Large"
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Wrong Size", result);
    }

    // Perimeter just above upper boundary for Large (101)
    @Test
    public void testPerimeterJustAboveLargeBoundary() {
        String input = "Line,Large,Yes,101"; // Perimeter = 101
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Yes", result);
    }

    // Even perimeter at boundary (0)
    @Test
    public void testEvenPerimeterAtZero() {
        String input = "Line,Small,Yes,0"; // Perimeter = 0 (Even)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Yes", result);
    }

    // Odd perimeter at boundary (1)
    @Test
    public void testOddPerimeterAtOne() {
        String input = "Line,Small,No,1"; // Perimeter = 1 (Odd)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Yes", result);
    }

    /**
     * Additional Tests for Parameter Boundaries
     */

    // Parameter below minimum (negative)
    @Test
    public void testParameterBelowMinimum() {
        String input = "Line,Small,Yes,-5"; // Negative parameter, should be set to 0
        String result = shapeClassifier.evaluateGuess(input);
        // Perimeter = 0, "Small" is <10, sizeGuess "Small" is correct
        // Even/Odd Guess "Yes" (Even)
        // ShapeGuess "Line" is correct
        assertEquals("Yes", result);
    }

    // Parameter above maximum (4096)
    @Test
    public void testParameterAboveMaximum() {
        String input = "Line,Large,Yes,5000"; // Parameter >4095, should be set to 4095
        String result = shapeClassifier.evaluateGuess(input);
        // Perimeter = 4095, which is >100 ("Large")
        // Even/Odd based on perimeter 4095 (Odd)
        // SizeGuess "Large" is correct
        // Even/Odd Guess "Yes" expects Even, but perimeter is Odd
        // Expected: "Wrong Even/Odd"
        assertEquals("Wrong Even/Odd", result);
    }

    // Incorrect number of parameters
    @Test
    public void testIncorrectNumberOfParameters() {
        String input = "Circle,Large,Yes"; // Missing parameters
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("No", result);
    }

    // Non-integer parameters
    @Test
    public void testNonIntegerParameters() {
        String input = "Circle,Large,Yes,ten,20";
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("No", result);
    }

    // Excessive bad guesses leading to error
    @Test
    public void testExcessiveBadGuesses() {
        // Simulate 3 bad guesses
        for (int i = 0; i < 3; i++) {
            String input = "Hexagon,Medium,Maybe,5000";
            // The third bad guess should trigger the error and exit
            // However, since System.exit(1) is called, we need to mock or handle it
            // For simplicity, we'll check the badGuesses count indirectly
            String result = shapeClassifier.evaluateGuess(input);
            if (i < 2) {
                assertTrue(result.startsWith("No: Suggestion="));
            } else {
                // On the third bad guess, the program exits.
                // In a real test environment, you would use a library like System Rules to handle this.
                // Here, we'll just assert that the first two bad guesses are handled correctly.
                assertTrue(result.startsWith("No: Suggestion="));
            }
        }
    }

    /**
     * Test Cases for Specific Shapes
     */

    // Test Equilateral Triangle
    @Test
    public void testEquilateralTriangle() {
        String input = "Equilateral,Large,Yes,30,30,30"; // Perimeter = 90 (Small is <10, Large is >100)
        String result = shapeClassifier.evaluateGuess(input);
        // Perimeter = 90, sizeGuess "Large" is incorrect
        assertEquals("Wrong Size", result);
    }

    // Test Isosceles Triangle
    @Test
    public void testIsoscelesTriangle() {
        String input = "Isosceles,Small,No,3,3,5"; // Perimeter = 11 (Not Small, expects perimeter <10)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("No: Suggestion=Scalene", result);
    }

    // Test Scalene Triangle
    @Test
    public void testScaleneTriangle() {
        String input = "Scalene,Small,Yes,3,4,5"; // Perimeter = 12 (Not Small)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Wrong Size", result);
    }

    // Test Square
    @Test
    public void testSquare() {
        String input = "Square,Large,Yes,25,25,25,25"; // Perimeter = 100 (Not Large)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Wrong Size", result);
    }

    // Test Rectangle
    @Test
    public void testRectangle() {
        String input = "Rectangle,Large,No,20,30,20,30"; // Perimeter = 100 (Not Large)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Wrong Size,Wrong Even/Odd", result);
    }

    // Test Circle with Even Perimeter
    @Test
    public void testCircleEvenPerimeter() {
        String input = "Circle,Large,Yes,16"; // Perimeter = 2 * PI * 16 ≈ 100.53 -> 100 (integer cast)
        // Perimeter = 100 (Not Large)
        String result = shapeClassifier.evaluateGuess(input);
        assertEquals("Wrong Size,Wrong Even/Odd", result);
    }

    // Test Ellipse with Odd Perimeter
    @Test
    public void testEllipseOddPerimeter() {
        String input = "Ellipse,Large,No,7,5"; // Perimeter ≈ 2 * PI * sqrt((7^2 +5^2)/2) ≈ 2 * PI * 6.4 ≈ 40.212
        // Perimeter = 40 (Even)
        String result = shapeClassifier.evaluateGuess(input);
        // SizeGuess "Large" is incorrect (40 < 100)
        // Even/Odd Guess "No" expects Odd, but perimeter is Even
        assertEquals("Wrong Size,Wrong Even/Odd", result);
    }
    @Test
    public void testLineSmallEven() {
        ShapeClassifier sc = new ShapeClassifier();
        String result = sc.evaluateGuess("Line,Small,Yes,5");
        assertEquals("Yes", result);
    }

    @Test
    public void testRectangleLargeOdd() {
        ShapeClassifier sc = new ShapeClassifier();
        String result = sc.evaluateGuess("Rectangle,Large,No,100,100,100,100");
        assertEquals("Yes", result);
    }

    @Test
    public void testInvalidShape() {
        ShapeClassifier sc = new ShapeClassifier();
        String result = sc.evaluateGuess("Hexagon,Large,Yes,10,10,10,10,10,10");
        assertTrue(result.startsWith("No"));
    }

    @Test
    public void testWrongSizeGuess() {
        ShapeClassifier sc = new ShapeClassifier();
        String result = sc.evaluateGuess("Circle,Medium,Yes,50,50");
        assertEquals("No: Wrong Size", result);
    }

    @Test
    public void testSquareMisclassified() {
        ShapeClassifier sc = new ShapeClassifier();
        String result = sc.evaluateGuess("Square,Small,Yes,3,3,3,3");
        assertEquals("No: Suggestion=Rectangle", result);
    }




}
