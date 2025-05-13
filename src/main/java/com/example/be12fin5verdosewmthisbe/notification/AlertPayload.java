package com.example.be12fin5verdosewmthisbe.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertPayload {
    private String storeId;
    private String message;
}
