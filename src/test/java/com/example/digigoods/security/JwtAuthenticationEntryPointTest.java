package com.example.digigoods.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private AuthenticationException authException;

  @InjectMocks
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  private StringWriter stringWriter;
  private PrintWriter printWriter;

  @BeforeEach
  void setUp() {
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
  }

  @Test
  @DisplayName("Given authentication exception, when commence is called, "
      + "then return unauthorized response")
  void givenAuthenticationException_whenCommenceIsCalled_thenReturnUnauthorizedResponse()
      throws IOException, ServletException {
    // Arrange
    String requestUri = "/api/products";
    when(request.getRequestURI()).thenReturn(requestUri);
    when(response.getWriter()).thenReturn(printWriter);

    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    verify(response).getWriter();
    verify(request).getRequestURI();

    // Verify the response content contains the expected JSON structure
    printWriter.flush();
    String responseContent = stringWriter.toString();
    
    // Basic assertions to ensure JSON response structure
    assert responseContent.contains("\"status\":401");
    assert responseContent.contains("\"error\":\"Unauthorized\"");
    assert responseContent.contains("\"message\":\"JWT token is missing or invalid\"");
    assert responseContent.contains("\"path\":\"" + requestUri + "\"");
    assert responseContent.contains("\"timestamp\":");
  }
}
