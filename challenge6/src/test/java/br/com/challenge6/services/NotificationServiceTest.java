package br.com.challenge6.services;

import br.com.challenge6.domain.investment.Investment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Spy
    private NotificationService spyService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(notificationService, "username", "test@example.com");
        ReflectionTestUtils.setField(notificationService, "password", "password");
        ReflectionTestUtils.setField(notificationService, "smtpHost", "smtp.example.com");
        ReflectionTestUtils.setField(notificationService, "smtpPort", "587");
        ReflectionTestUtils.setField(notificationService, "smtpAuth", true);
        ReflectionTestUtils.setField(notificationService, "startTls", true);
    }

    @Test
    @DisplayName("Should send alert email when price drops more than 10%")
    void notifyLost_sendsEmailOnLoss() {
        Investment inv = new Investment();
        inv.setTicker("PETR4");
        inv.setBuyPrice(30.00);

        List<Investment> investments = List.of(inv);

        NotificationService spy = Mockito.spy(notificationService);
        doNothing().when(spy).sendEmail(anyString(), anyString(), anyString());

        spy.notifyLost(investments, "user@example.com");

        verify(spy).sendEmail(eq("user@example.com"), eq("🚨 Alerta de Investimentos"), contains("caiu"));
    }

    @Test
    @DisplayName("Should NOT send email when no investment drops enough")
    void notifyLost_doesNotSendEmailIfNoLoss() {
        Investment inv = new Investment();
        inv.setTicker("PETR4");
        inv.setBuyPrice(23.00);

        List<Investment> investments = List.of(inv);

        NotificationService spy = Mockito.spy(notificationService);

        spy.notifyLost(investments, "user@example.com");

        verify(spy, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Should send alert email when price increases at most 10%")
    void notifyHighGain_sendsEmailOnGain() {
        Investment inv = new Investment();
        inv.setTicker("PETR4");
        inv.setBuyPrice(21.00);

        List<Investment> investments = List.of(inv);

        NotificationService spy = Mockito.spy(notificationService);
        doNothing().when(spy).sendEmail(anyString(), anyString(), anyString());

        spy.notifyHighGain(investments, "user@example.com");

        verify(spy).sendEmail(eq("user@example.com"), eq("🚨 Alerta de Investimentos"), contains("cresceu"));
    }

    @Test
    @DisplayName("Should NOT send email when gain is over 10%")
    void notifyHighGain_doesNotSendEmailIfTooHigh() {
        Investment inv = new Investment();
        inv.setTicker("PETR4");
        inv.setBuyPrice(20.00);

        List<Investment> investments = List.of(inv);

        NotificationService spy = Mockito.spy(notificationService);

        spy.notifyHighGain(investments, "user@example.com");

        verify(spy, never()).sendEmail(any(), any(), any());
    }
}