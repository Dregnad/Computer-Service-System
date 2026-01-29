package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    @Value("${database.postgresql.dburl}")
    private String dbUrl;
    @Value("${database.postgresql.dbuser}")
    private String dbUser;
    @Value("${database.postgresql.dbpassword}")
    private String dbPass;

    // Początkowy zapis zgłoszenia
    public void saveNewOrder(Map<String, Object> vars, String orderId) throws SQLException {
        String sql = "INSERT INTO serwis.zlecenia (order_id, klient_imie, klient_nazwisko, klient_email, klient_telefon, klient_adres, typ_urzadzenia, opis_usterki, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'NOWE')";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            pstmt.setString(2, (String) vars.get("customerFirstName"));
            pstmt.setString(3, (String) vars.get("customerLastName"));
            pstmt.setString(4, (String) vars.get("customerEmail"));
            pstmt.setString(5, (String) vars.get("customerPhone"));
            pstmt.setString(6, (String) vars.get("customerAddress"));
            pstmt.setString(7, (String) vars.get("deviceType"));
            pstmt.setString(8, (String) vars.get("issueDescription"));
            pstmt.executeUpdate();
        }
    }

    // Zapis wyceny technika (koszt, notatki i lista części jako tekst)
    public void updateValuation(String orderId, Object cost, String notes, String parts) throws SQLException {
        String sql = "UPDATE serwis.zlecenia SET cena_finalna = ?, notatki_technika = ?, komponent = ?, data_aktualizacji = CURRENT_TIMESTAMP WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, new java.math.BigDecimal(cost.toString()));
            pstmt.setString(2, notes);
            pstmt.setString(3, parts);
            pstmt.setString(4, orderId);
            pstmt.executeUpdate();
        }
    }

    // Zapis wyniku naprawy i opisu prac
    public void updateRepairResult(String orderId, String desc, boolean success) throws SQLException {
        String sql = "UPDATE serwis.zlecenia SET opis_naprawy = ?, czy_naprawa_udana = ?, data_aktualizacji = CURRENT_TIMESTAMP WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, desc);
            pstmt.setBoolean(2, success);
            pstmt.setString(3, orderId);
            pstmt.executeUpdate();
        }
    }

    // Zapis numeru listu przewozowego
    public void updateShippingInfo(String orderId, String tracking) throws SQLException {
        String sql = "UPDATE serwis.zlecenia SET numer_listu_przewozowego = ?, czy_wyslano = true, data_aktualizacji = CURRENT_TIMESTAMP WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tracking);
            pstmt.setString(2, orderId);
            pstmt.executeUpdate();
        }
    }

    public void updateStatus(String orderId, String status) throws SQLException {
        String sql = "UPDATE serwis.zlecenia SET status = ?, data_aktualizacji = CURRENT_TIMESTAMP WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, orderId);
            pstmt.executeUpdate();
        }
    }

    public void updateAcceptance(String orderId, boolean accepted) throws SQLException {
        String sql = "UPDATE serwis.zlecenia SET czy_oferta_zaakceptowana = ?, data_aktualizacji = CURRENT_TIMESTAMP WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, accepted);
            pstmt.setString(2, orderId);
            pstmt.executeUpdate();
        }
    }

    public boolean arePartsAvailable(String deviceType, List<String> parts) throws SQLException {
        if (parts == null || parts.isEmpty()) return true;
        String sql = "SELECT czy_dostepna FROM serwis.magazyn WHERE typ_urzadzenia = ? AND klucz_czesci = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            for (String part : parts) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, deviceType);
                    pstmt.setString(2, part);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next() && !rs.getBoolean("czy_dostepna")) return false;
                }
            }
        }
        return true;
    }
}