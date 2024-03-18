package com.emailSender.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import reactor.core.publisher.Mono;

@Controller
@SessionAttributes("base64Image")
public class QrController {

	@GetMapping("qr")
	public String showPage() {
		return "qrcode";
	}
	
	@PostMapping("/generateQRCode")
    @ResponseBody
    public Mono<ResponseEntity<String>> generateQRCode(@RequestBody Map<String, Object> requestBody) {
        double amount = Double.parseDouble(requestBody.get("amount").toString());
        try {
            // Construct UPI URL
            String vpa = "rohitkumarah369@ibl"; //My Vpa
            String upiUrl = "upi://pay?pa=" + vpa + "&am=" + amount;

            // Generate QR code
            BitMatrix bitMatrix = new MultiFormatWriter().encode(upiUrl, BarcodeFormat.QR_CODE, 300, 300);

            // Convert BitMatrix to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();

            // Convert byte array to base64 string
            String base64Image = Base64.getEncoder().encodeToString(qrCodeBytes);

            // Return the base64 encoded QR code image
            return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(base64Image));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating QR code"));
        }
    }
}