package test;
import JFree.DiscountManager;
import JFree.IDiscountCalculator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import static org.junit.Assert.*;

public class DiscountManagerTest {

    @Test
    public void testCalculatePriceWhenDiscountsSeasonIsFalse() throws Exception {
        // Arrange
        boolean isDiscountsSeason = false;
        double originalPrice = 100.0;
        double expectedPrice = 100.0;

        Mockery mockingContext = new Mockery();
        IDiscountCalculator mockedDependency = mockingContext.mock(IDiscountCalculator.class);
        mockingContext.checking(new Expectations(){
            {
                // make sure that none of the functions are called
                never(mockedDependency).isTheSpecialWeek();
                never(mockedDependency).getDiscountPercentage();

            }
        });
        DiscountManager discountManager = new DiscountManager(isDiscountsSeason, mockedDependency);
        // Act
        double theActualPrice = discountManager.calculatePriceAfterDiscount(originalPrice);
        // Assert
        mockingContext.assertIsSatisfied();
        assertTrue(expectedPrice == theActualPrice);

        // make sure that mocking Expectations Is Satisfied
        // make sure that the actual value exactly equals the expected value
    }

    @Test
    public void testCalculatePriceWhenDiscountsSeasonIsTrueAndSpecialWeekIsTrue() throws Exception {
        boolean isDiscountsSeason = true;
        double originalPrice = 200.0;
        double expectedPrice = 200.0 * 0.8;

        Mockery context = new Mockery();
        IDiscountCalculator fakeCalculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(fakeCalculator).isTheSpecialWeek(); will(returnValue(true));
            // getDiscountPercentage() should NOT be called
        }});
        DiscountManager discountManager = new DiscountManager(isDiscountsSeason, fakeCalculator);
        double theActualPrice = discountManager.calculatePriceAfterDiscount(originalPrice);

        context.assertIsSatisfied(); // ensures only expected calls happened
        assertEquals(expectedPrice, theActualPrice, 0.001);

    }

    @Test
    public void testCalculatePriceWhenDiscountsSeasonIsTrueAndSpecialWeekIsFalse() throws Exception {
        // Arrange
        boolean isDiscountsSeason = true;
        double originalPrice = 200.0;
        double expectedPrice = 170.0;

        Mockery context = new Mockery();
        IDiscountCalculator fakeCalculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(fakeCalculator).isTheSpecialWeek(); will(returnValue(false));
            oneOf(fakeCalculator).getDiscountPercentage(); will(returnValue(85));
        }});

        DiscountManager discountManager = new DiscountManager(isDiscountsSeason, fakeCalculator);

        // Act
        double actualPrice = discountManager.calculatePriceAfterDiscount(originalPrice);

        // Assert
        context.assertIsSatisfied();
        assertEquals(expectedPrice, actualPrice, 0.001);

    }
    @Test
    public void testPercentageConversion() {
        Mockery context = new Mockery();
        IDiscountCalculator calculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(calculator).isTheSpecialWeek(); will(returnValue(false));
            oneOf(calculator).getDiscountPercentage(); will(returnValue(50));
        }});

        double result = new DiscountManager(true, calculator)
                .calculatePriceAfterDiscount(100.0);

        // Should be 50% → 50.0, but will be 5000.0
        assertEquals(50.0, result, 0.001); // This will FAIL
    }


    @Test
    public void testCalculatePriceWithNegativePrice() throws Exception {
        // Arrange
        boolean isDiscountsSeason = true;
        double originalPrice = -100.0;
        double expectedPrice = -80.0; // -100 * 0.8 (matches current behavior)

        Mockery context = new Mockery();
        IDiscountCalculator fakeCalculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(fakeCalculator).isTheSpecialWeek(); will(returnValue(true));
        }});

        DiscountManager discountManager = new DiscountManager(isDiscountsSeason, fakeCalculator);

        // Act
        double actualPrice = discountManager.calculatePriceAfterDiscount(originalPrice);

        // Assert
        context.assertIsSatisfied();
        assertEquals(expectedPrice, actualPrice, 0.001);
    }
    @Test
    public void testZeroPrice() throws Exception {
        // Arrange
        boolean isDiscountsSeason = true;
        double originalPrice = 0.0;

        Mockery context = new Mockery();
        IDiscountCalculator fakeCalculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(fakeCalculator).isTheSpecialWeek(); will(returnValue(false));
            oneOf(fakeCalculator).getDiscountPercentage(); will(returnValue(50));
        }});

        DiscountManager discountManager = new DiscountManager(isDiscountsSeason, fakeCalculator);

        // Act
        double actualPrice = discountManager.calculatePriceAfterDiscount(originalPrice);

        // Assert
        context.assertIsSatisfied();
        assertTrue("Zero price should remain zero", actualPrice == 0.0);
    }

    @Test
    public void test100PercentDiscount() throws Exception {
        Mockery context = new Mockery();
        IDiscountCalculator calculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(calculator).isTheSpecialWeek(); will(returnValue(false));
            oneOf(calculator).getDiscountPercentage(); will(returnValue(100));
        }});

        DiscountManager manager = new DiscountManager(true, calculator);
        double result = manager.calculatePriceAfterDiscount(200.0);

        assertEquals(20000.0, result, 0.001); // Shows the bug
    }


    @Test
    public void testZeroDiscountPercentage() {
        Mockery context = new Mockery();
        IDiscountCalculator calculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(calculator).isTheSpecialWeek(); will(returnValue(false));
            oneOf(calculator).getDiscountPercentage(); will(returnValue(100)); // 0% discount
        }});

        double result = new DiscountManager(true, calculator)
                .calculatePriceAfterDiscount(200.0);

        assertEquals(200.0, result, 0.001); // No discount applied
        context.assertIsSatisfied();
    }
    @Test
    public void test99PercentDiscount() {
        Mockery context = new Mockery();
        IDiscountCalculator calculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(calculator).isTheSpecialWeek(); will(returnValue(false));
            oneOf(calculator).getDiscountPercentage(); will(returnValue(1)); // 99% discount
        }});

        double result = new DiscountManager(true, calculator)
                .calculatePriceAfterDiscount(100.0);

        assertEquals(1.0, result, 0.001); // 100 * 0.01
        context.assertIsSatisfied();
    }

    @Test
    public void testWeek25NotSpecialWeek() {
        Mockery context = new Mockery();
        IDiscountCalculator calculator = context.mock(IDiscountCalculator.class);

        context.checking(new Expectations() {{
            oneOf(calculator).isTheSpecialWeek(); will(returnValue(false));
            oneOf(calculator).getDiscountPercentage(); will(returnValue(90)); // 10% discount
        }});

        double result = new DiscountManager(true, calculator)
                .calculatePriceAfterDiscount(100.0);

        assertEquals(90.0, result, 0.001); // Expect 100 * 0.90
        context.assertIsSatisfied();
    }
}
