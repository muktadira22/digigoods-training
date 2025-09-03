package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.exception.InvalidDiscountException;
import com.example.digigoods.model.Discount;
import com.example.digigoods.repository.DiscountRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for DiscountService.
 * Tests follow AAA (Arrange-Act-Assert) pattern and JUnit 5 conventions.
 */
@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

  @Mock
  private DiscountRepository discountRepository;

  @InjectMocks
  private DiscountService discountService;

  private Discount validDiscount;
  private Discount expiredDiscount;
  private Discount futureDiscount;
  private Discount noUsesDiscount;

  @BeforeEach
  void setUp() {
    // Arrange - Set up test data
    LocalDate today = LocalDate.now();
    
    validDiscount = createDiscount(1L, "VALID10", BigDecimal.valueOf(10.0), 5,
        today.minusDays(1), today.plusDays(30));
    
    expiredDiscount = createDiscount(2L, "EXPIRED20", BigDecimal.valueOf(20.0), 3,
        today.minusDays(30), today.minusDays(1));
    
    futureDiscount = createDiscount(3L, "FUTURE15", BigDecimal.valueOf(15.0), 2,
        today.plusDays(1), today.plusDays(30));
    
    noUsesDiscount = createDiscount(4L, "NOUSE25", BigDecimal.valueOf(25.0), 0,
        today.minusDays(1), today.plusDays(30));
  }

  @Test
  @DisplayName("Should return all discounts when repository has data")
  void givenDiscountsExist_whenGetAllDiscounts_thenReturnAllDiscounts() {
    // Arrange - Prepare mock data
    List<Discount> expectedDiscounts = Arrays.asList(validDiscount, expiredDiscount);
    when(discountRepository.findAll()).thenReturn(expectedDiscounts);

    // Act - Execute the method under test
    List<Discount> actualDiscounts = discountService.getAllDiscounts();

    // Assert - Verify the results
    assertEquals(expectedDiscounts.size(), actualDiscounts.size());
    assertEquals(expectedDiscounts, actualDiscounts);
    verify(discountRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should return empty list when no discounts exist")
  void givenNoDiscountsExist_whenGetAllDiscounts_thenReturnEmptyList() {
    // Arrange - Mock repository to return empty list
    when(discountRepository.findAll()).thenReturn(new ArrayList<>());

    // Act - Execute the method under test
    List<Discount> actualDiscounts = discountService.getAllDiscounts();

    // Assert - Verify empty list is returned
    assertTrue(actualDiscounts.isEmpty());
    verify(discountRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should return valid discounts when all codes are valid and active")
  void givenValidDiscountCodes_whenValidateAndGetDiscounts_thenReturnValidDiscounts() {
    // Arrange - Set up valid discount codes and mock repository response
    List<String> discountCodes = Arrays.asList("VALID10");
    List<Discount> expectedDiscounts = Arrays.asList(validDiscount);
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(expectedDiscounts);

    // Act - Execute the method under test
    List<Discount> actualDiscounts = discountService.validateAndGetDiscounts(discountCodes);

    // Assert - Verify valid discounts are returned
    assertEquals(expectedDiscounts.size(), actualDiscounts.size());
    assertEquals(expectedDiscounts, actualDiscounts);
    verify(discountRepository, times(1)).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Should return empty list when discount codes list is null")
  void givenNullDiscountCodes_whenValidateAndGetDiscounts_thenReturnEmptyList() {
    // Arrange - Pass null discount codes
    List<String> discountCodes = null;

    // Act - Execute the method under test
    List<Discount> actualDiscounts = discountService.validateAndGetDiscounts(discountCodes);

    // Assert - Verify empty list is returned
    assertTrue(actualDiscounts.isEmpty());
    verify(discountRepository, never()).findAllByCodeIn(any());
  }

  @Test
  @DisplayName("Should return empty list when discount codes list is empty")
  void givenEmptyDiscountCodes_whenValidateAndGetDiscounts_thenReturnEmptyList() {
    // Arrange - Pass empty discount codes list
    List<String> discountCodes = new ArrayList<>();

    // Act - Execute the method under test
    List<Discount> actualDiscounts = discountService.validateAndGetDiscounts(discountCodes);

    // Assert - Verify empty list is returned
    assertTrue(actualDiscounts.isEmpty());
    verify(discountRepository, never()).findAllByCodeIn(any());
  }

  @Test
  @DisplayName("Should throw InvalidDiscountException when discount code not found")
  void givenNonExistentDiscountCode_whenValidateAndGetDiscounts_thenThrowException() {
    // Arrange - Set up non-existent discount code
    List<String> discountCodes = Arrays.asList("NONEXISTENT");
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(new ArrayList<>());

    // Act & Assert - Verify exception is thrown
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("discount code not found"));
    verify(discountRepository, times(1)).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Should throw InvalidDiscountException when discount is expired")
  void givenExpiredDiscount_whenValidateAndGetDiscounts_thenThrowInvalidDiscountException() {
    // Arrange - Set up expired discount
    List<String> discountCodes = Arrays.asList("EXPIRED20");
    List<Discount> expiredDiscounts = Arrays.asList(expiredDiscount);
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(expiredDiscounts);

    // Act & Assert - Verify exception is thrown for expired discount
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("discount has expired"));
    verify(discountRepository, times(1)).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Should throw InvalidDiscountException when discount is not yet valid")
  void givenFutureDiscount_whenValidateAndGetDiscounts_thenThrowInvalidDiscountException() {
    // Arrange - Set up future discount
    List<String> discountCodes = Arrays.asList("FUTURE15");
    List<Discount> futureDiscounts = Arrays.asList(futureDiscount);
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(futureDiscounts);

    // Act & Assert - Verify exception is thrown for future discount
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("discount is not yet valid"));
    verify(discountRepository, times(1)).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Should throw InvalidDiscountException when discount has no remaining uses")
  void givenDiscountWithNoUses_whenValidateAndGetDiscounts_thenThrowInvalidDiscountException() {
    // Arrange - Set up discount with no remaining uses
    List<String> discountCodes = Arrays.asList("NOUSE25");
    List<Discount> noUsesDiscounts = Arrays.asList(noUsesDiscount);
    when(discountRepository.findAllByCodeIn(discountCodes)).thenReturn(noUsesDiscounts);

    // Act & Assert - Verify exception is thrown for discount with no uses
    InvalidDiscountException exception = assertThrows(InvalidDiscountException.class,
        () -> discountService.validateAndGetDiscounts(discountCodes));

    assertTrue(exception.getMessage().contains("discount has no remaining uses"));
    verify(discountRepository, times(1)).findAllByCodeIn(discountCodes);
  }

  @Test
  @DisplayName("Should update discount usage by decrementing remaining uses")
  void givenValidDiscounts_whenUpdateDiscountUsage_thenDecrementRemainingUses() {
    // Arrange - Set up discounts with remaining uses
    List<Discount> discounts = Arrays.asList(validDiscount);
    int originalUses = validDiscount.getRemainingUses();

    // Act - Execute the method under test
    discountService.updateDiscountUsage(discounts);

    // Assert - Verify remaining uses are decremented and discount is saved
    assertEquals(originalUses - 1, validDiscount.getRemainingUses());
    verify(discountRepository, times(1)).save(validDiscount);
  }

  @Test
  @DisplayName("Should handle multiple discounts when updating usage")
  void givenMultipleDiscounts_whenUpdateDiscountUsage_thenUpdateAllDiscounts() {
    // Arrange - Set up multiple discounts
    Discount discount1 = createDiscount(5L, "MULTI1", BigDecimal.valueOf(10.0), 3,
        LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
    Discount discount2 = createDiscount(6L, "MULTI2", BigDecimal.valueOf(15.0), 2,
        LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
    List<Discount> discounts = Arrays.asList(discount1, discount2);
    int originalUses1 = discount1.getRemainingUses();
    int originalUses2 = discount2.getRemainingUses();

    // Act - Execute the method under test
    discountService.updateDiscountUsage(discounts);

    // Assert - Verify both discounts are updated
    assertEquals(originalUses1 - 1, discount1.getRemainingUses());
    assertEquals(originalUses2 - 1, discount2.getRemainingUses());
    verify(discountRepository, times(2)).save(any(Discount.class));
  }

  @Test
  @DisplayName("Should handle empty discount list when updating usage")
  void givenEmptyDiscountList_whenUpdateDiscountUsage_thenNoUpdatesPerformed() {
    // Arrange - Pass empty discount list
    List<Discount> discounts = new ArrayList<>();

    // Act - Execute the method under test
    discountService.updateDiscountUsage(discounts);

    // Assert - Verify no repository calls are made
    verify(discountRepository, never()).save(any(Discount.class));
  }

  /**
   * Helper method to create discount test data.
   */
  private Discount createDiscount(Long id, String code, BigDecimal percentage,
                                int remainingUses, LocalDate validFrom, LocalDate validUntil) {
    Discount discount = new Discount();
    discount.setId(id);
    discount.setCode(code);
    discount.setPercentage(percentage);
    discount.setRemainingUses(remainingUses);
    discount.setValidFrom(validFrom);
    discount.setValidUntil(validUntil);
    return discount;
  }
}
