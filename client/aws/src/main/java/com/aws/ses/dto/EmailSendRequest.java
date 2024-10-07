package com.aws.ses.dto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record EmailSendRequest(String to, String subject, String body) {
}
