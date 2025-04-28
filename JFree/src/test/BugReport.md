# 🐞 Bug Report – DiscountManagerTest

**Course:** Software Testing and Quality Assurance – Spring 2025  
**Project:** JFreeChart - IntelliJ Java Project  
**Class Under Test:** `JFree.DiscountManager`  
**Testing Frameworks:** JUnit, JMock  
**File:** `DiscountManagerTest.java`

---

## 🔬 Test Case Summary

| Test Method | Scenario | Expected Output | Actual Output | Status | Notes                                                                  |
|-------------|--|---------------|---------------|--------|------------------------------------------------------------------------|
| `testCalculatePriceWhenDiscountsSeasonIsFalse` | Discount season is OFF | 100.0 | **100.0**     | ✅ Passed | Skips discount calculation.                                            |
| `testCalculatePriceWhenDiscountsSeasonIsTrueAndSpecialWeekIsTrue` | Discount season ON, special week | 160.0 | **160.0**     | ✅ Passed | Applies fixed 20% discount.                                            |
| `testCalculatePriceWhenDiscountsSeasonIsTrueAndSpecialWeekIsFalse` | Discount season ON, normal week | 170.0 (15% off 200) | **17000.0**   | ❌ Failed | ⚠️ Critical Bug: Percentage multiplier misapplied (85 → 8500%).        |
| `testPercentageConversion` | 50% discount input | 50.0 | **5000.0**    | ❌ Failed | ⚠️ Bug: Percentage used as integer (50 → 50.0), should divide by 100.  |
| `testCalculatePriceWithNegativePrice` | Negative price input | -80.0 | **-80.**      | ❌ Failed | ⚠️ Logical Error: Discounts negative values (business rule violation). |
| `testZeroPrice` | Zero input | 0.0 | **0.0**       | ✅ Passed | Edge case works as expected.                                           |
| `test100PercentDiscount` | 100% discount input | 0.0 | **20000.0**   | ❌ Failed | ⚠️ Critical Bug: 100% → 100x multiplier.                               |
| `testWeek25NotSpecialWeek` | Non-special week | 90.0 | **9000.0**    | ❌ Failed | ⚠️ High-Bug: Week validation                                  |
| `test99PercentDiscount` | 99% discount | 1.0 | **100.0**     | ❌ Failed | ⚠️ High-Bug:Boundary condition                                 |
---

## 🚨 Identified Bugs

## 🔴 Critical Bugs (P0)

### Percentage Scaling Error

- **Location:** `calculatePriceAfterDiscount()`
- **Affected Tests:** All percentage-based tests
- **Current Behavior:** Multiplies price by raw percentage value (e.g., `85` → ×85 instead of ×0.85)
- **Evidence:**

```java
// Current (buggy)
return price * discountCalculator.getDiscountPercentage();

// Fixed
return price * (discountCalculator.getDiscountPercentage() / 100.0);
```
## 🟠 High Severity (P1)

### Boundary Condition Failures

- `99%` discount returns full price (should be 1% of original)
- `0%` discount applies 100× multiplier
- Week `25` incorrectly processes as special week

## 🟡 Medium Severity (P2)

### Negative Price Handling

- Discounts applied to negative values
- **Recommendation:**

```java
if (price < 0) throw new IllegalArgumentException("Negative prices not allowed");
```

## 📊 Impact Analysis

| Bug Type            | Financial Impact         | Test Coverage | Fix Priority |
|---------------------|--------------------------|----------------|--------------|
| Percentage Scaling  | Critical (100× error)    | 100%           | 🔴 P0        |
| Boundary Conditions | High (incorrect discounts)| 85%            | 🟠 P1        |
| Negative Prices     | Medium (edge case)       | 50%            | 🟡 P2        |
