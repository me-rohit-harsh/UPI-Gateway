package com.emailSender.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.emailSender.Repository.UpiRepository;
import com.emailSender.model.UPI;

@Service
public class UpiService {

    @Autowired
    UpiRepository upiRepository;

    public UPI getLatestUpiDetailsForCurrentUser(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 1); // Fetch only the latest entry
        List<UPI> upi = upiRepository.findLatestByUserId(userId, pageRequest);
        return upi.isEmpty() ? null : upi.get(0);
    }

    public void saveUpiDetails(UPI upi) {
        upiRepository.save(upi);
    }
}
