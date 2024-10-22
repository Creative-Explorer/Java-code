package spring_security.JWT_Token.Utils;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilloService {

    @Value("${twilio.accountSid}")
    private final String accountSid;

    @Value("${twilio.authToken}")
    private final String authToken;

    @Value("${twilio.verifyServiceSid}")
    private final String verifyServiceSid;

    public TwilloService(@Value("${twilio.accountSid}") String accountSid, @Value("${twilio.authToken}") String authToken, @Value("${twilio.verifyServiceSid}") String verifyServiceSid) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.verifyServiceSid = verifyServiceSid;

        Twilio.init(accountSid, authToken);
    }

    public boolean sendVerificationCode(String phoneNumber) {
        try {
            Twilio.init(accountSid, authToken);
            Verification.creator(verifyServiceSid,   // Replace with your actual Verify service SID
                            formatPhoneNumber(phoneNumber),   // Format phone number if needed
                            "sms")   // Change to "call" if you want to send a voice call instead of SMS
                    .create();
            System.out.println("OTP sent successfully.");
            return true;
        } catch (ApiException e) {
            System.out.println("Twilio API Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }
        return phoneNumber;
    }

    public boolean checkVerificationCode(String phoneNumber, String code) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid, code).setTo(formatPhoneNumber(phoneNumber)).create();
            System.out.println(verificationCheck);
            return true;
        } catch (ApiException e) {
            return false;
        }
    }
}
