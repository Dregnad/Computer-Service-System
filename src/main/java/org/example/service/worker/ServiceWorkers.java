package org.example.service.worker;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import jakarta.mail.internet.MimeMessage;
import org.example.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class ServiceWorkers {

    @Autowired private DatabaseService dbService;
    @Autowired private JavaMailSender mailSender;
    @Value("${spring.mail.username}") private String senderEmail;

    // 1. Potwierdzenie przyjęcia (email-service)
    @JobWorker(type = "email-service")
    public void handleWelcomeEmail(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        String orderId = (String) vars.get("orderId");
        String html = "<div style='font-family: Arial; padding: 20px; border: 1px solid #eee;'>" +
                "<h2 style='color: #1a73e8;'>Zgłoszenie zarejestrowane</h2>" +
                "<p>Witaj <b>" + vars.get("customerFirstName") + "</b>!</p>" +
                "<p>Numer Twojego zlecenia: <b>" + orderId + "</b></p>" +
                "<p>Urządzenie: <b>" + vars.get("deviceType") + "</b></p>" +
                "<p>Nasz serwisant wkrótce dokona manualnej wyceny.</p></div>";
        sendHtmlEmail((String) vars.get("customerEmail"), "Potwierdzenie przyjęcia zgłoszenia: " + orderId, html);
    }

    // 2. Rejestracja w bazie (db-register-worker)
    @JobWorker(type = "db-register-worker")
    public Map<String, Object> register(final ActivatedJob job) throws SQLException {
        Map<String, Object> vars = job.getVariablesAsMap();
        String orderId = (String) vars.get("orderId");
        dbService.updateStatus(orderId, "W_KOLEJCE");

        // Przygotowanie pełnego imienia i nazwiska dla formularza pakowania
        String fullName = vars.get("customerFirstName") + " " + vars.get("customerLastName");
        return Map.of("customerFullName", fullName);
    }

    // 3. Wycena i zapisanie danych technika (email-sender-worker)
    @JobWorker(type = "email-sender-worker")
    public Map<String, Object> sendQuote(final ActivatedJob job) throws SQLException {
        Map<String, Object> vars = job.getVariablesAsMap();
        String orderId = (String) vars.get("orderId");
        Object cost = vars.get("repairCost");
        String notes = (String) vars.get("techNotes");
        String device = (String) vars.get("deviceType");

        // Pobieramy listę części zaznaczoną w formularzu i zamieniamy na tekst do bazy
        String fieldKey = "parts" + (device.equalsIgnoreCase("pc") ? "PC" : device.substring(0, 1).toUpperCase() + device.substring(1).toLowerCase());
        List<String> partsList = (List<String>) vars.get(fieldKey);
        String partsString = (partsList != null) ? String.join(", ", partsList) : "Brak części";

        // ZAPIS DO BAZY: Wszystko co wpisał technik w Form_Tech_Valuation_Final
        dbService.updateValuation(orderId, cost, notes, partsString);

        String html = "<div style='font-family: Arial; padding: 20px; background-color: #fffde7;'>" +
                "<h3>Gotowa wycena naprawy zlecenia " + orderId + "</h3>" +
                "<p>Całkowity koszt: <b style='color: #d32f2f;'>" + cost + " PLN</b></p>" +
                "<p>Wybrane komponenty: <i>" + partsString + "</i></p>" +
                "<p>Prosimy o podjęcie decyzji w panelu klienta.</p></div>";

        sendHtmlEmail((String) vars.get("customerEmail"), "Wycena naprawy", html);
        return Map.of("finalPrice", cost);
    }

    // 4. Magazyn (check-parts-availability)
    @JobWorker(type = "check-parts-availability")
    public Map<String, Object> checkParts(final ActivatedJob job) throws Exception {
        Map<String, Object> vars = job.getVariablesAsMap();
        if (vars.containsKey("orderPlaced") && (boolean) vars.get("orderPlaced")) return Map.of("partsAvailable", true);

        String device = (String) vars.get("deviceType");
        String fieldKey = "parts" + (device.equalsIgnoreCase("pc") ? "PC" : device.substring(0, 1).toUpperCase() + device.substring(1).toLowerCase());
        List<String> selectedParts = (List<String>) vars.get(fieldKey);

        boolean available = dbService.arePartsAvailable(device, selectedParts);
        return Map.of("partsAvailable", available);
    }

    // 5. Zapis wyniku naprawy (hardware-quality-check)
    @JobWorker(type = "hardware-quality-check")
    public void qaTest(final ActivatedJob job) throws SQLException {
        Map<String, Object> vars = job.getVariablesAsMap();
        String orderId = (String) vars.get("orderId");
        String repairDesc = (String) vars.get("repairDescription");
        boolean isSuccess = (boolean) vars.get("repairSuccessful");

        // ZAPIS DO BAZY: Dane z formularza naprawa-serwisowa.form
        dbService.updateRepairResult(orderId, repairDesc, isSuccess);
        System.out.println("LOG: Zapisano wynik naprawy dla " + orderId + ". Sukces: " + isSuccess);
    }

    // 6. Faktura
    @JobWorker(type = "invoice-generator-worker")
    public void handleInvoice(final ActivatedJob job) throws SQLException {
        dbService.updateStatus((String) job.getVariablesAsMap().get("orderId"), "NAPRAWIONE_DO_OPLATY");
    }

    // 7. Płatność
    @JobWorker(type = "email-payment-request")
    public void paymentReq(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        String html = "<div style='font-family: Arial; padding: 20px; border: 2px solid #28a745;'>" +
                "<h3>Twoje urządzenie jest gotowe! 🔧</h3>" +
                "<p>Do zapłaty: <b>" + vars.get("repairCost") + " PLN</b></p></div>";
        sendHtmlEmail((String) vars.get("customerEmail"), "Urządzenie gotowe - Prośba o płatność", html);
    }

    // 8. Przypomnienie
    @JobWorker(type = "wyslij-przypomnienie-task")
    public void reminder(final ActivatedJob job) {
        sendHtmlEmail((String) job.getVariablesAsMap().get("customerEmail"), "Przypomnienie o płatności", "<p>Czekamy na wpłatę za zlecenie " + job.getVariablesAsMap().get("orderId") + "</p>");
    }

    // 9. Kurier, Opinia i Zamknięcie (email-service-1)
    @JobWorker(type = "email-service-1")
    public void handleFinal(final ActivatedJob job) throws SQLException {
        Map<String, Object> vars = job.getVariablesAsMap();
        String orderId = (String) vars.get("orderId");
        String tracking = (String) vars.get("trackingNumber");

        // ZAPIS DO BAZY: Numer listu z pakowanie.form
        dbService.updateShippingInfo(orderId, tracking);

        String html = "<div style='font-family: Arial; padding: 20px; border-radius: 10px; border: 2px solid #28a745;'>" +
                "<h2 style='color: #28a745;'>Twoja paczka jest w drodze! 🚚</h2>" +
                "<p>Zlecenie <b>" + orderId + "</b> przekazane kurierowi.</p>" +
                "<p>Numer listu: <b>" + tracking + "</b></p>" +
                "<p>Dziękujemy! Oceń naszą pracę ⭐⭐⭐⭐⭐</p></div>";

        sendHtmlEmail((String) vars.get("customerEmail"), "Urządzenie wysłane!", html);
        dbService.updateStatus(orderId, "ZAMKNIĘTE");
    }

    // 10. ANULOWANIE ZLECENIA
    @JobWorker(type = "order-cancellation-worker")
    public void handleCancellation(final ActivatedJob job) throws SQLException {
        String orderId = (String) job.getVariablesAsMap().get("orderId");

        // Zapisujemy, że klient NIE zaakceptował oferty
        dbService.updateAcceptance(orderId, false);
        dbService.updateStatus(orderId, "ANULOWANE");

        System.out.println("LOG: Zlecenie " + orderId + " anulowane (decyzja: odmowa).");
    }

    @JobWorker(type = "log-acceptance-worker")
    public void logAcceptance(final ActivatedJob job) throws SQLException {
        String orderId = (String) job.getVariablesAsMap().get("orderId");

        // Zapisujemy, że klient zaakceptował ofertę
        dbService.updateAcceptance(orderId, true);
        System.out.println("LOG: Klient zaakceptował wycenę dla zlecenia: " + orderId);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            System.out.println("LOG: E-mail HTML wysłany do " + to);
        } catch (Exception e) { System.err.println("BŁĄD WYSYŁKI: " + e.getMessage()); }
    }
}