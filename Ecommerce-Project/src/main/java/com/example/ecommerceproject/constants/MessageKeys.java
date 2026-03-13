package com.example.ecommerceproject.constants;

public final class MessageKeys {

    private MessageKeys() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Authentication Messages
    public static final String PROTECTED_ADMIN_FIRST_NAME = "System";
    public static final String PROTECTED_ADMIN_LAST_NAME = "Admin";
    public static final String PROTECTED_ADMIN_PASSWORD = "Admin@123";
    public static final String PROTECTED_ADMIN_EMAIL = "admin@ecommerce.com";
    public static final String AUTH_INVALID_CREDENTIALS = "auth.invalid_credentials";
    public static final String AUTH_ACCOUNT_LOCKED = "auth.account_locked";
    public static final String AUTH_ACCOUNT_NOT_ACTIVATED = "auth.account_not_activated";
    public static final String AUTH_PASSWORD_EXPIRED = "auth.password_expired";
    public static final String AUTH_LOGOUT_SUCCESS = "auth.logout_success";
    public static final String AUTH_TOKEN_REQUIRED = "auth.token_required";
    public static final String AUTH_REGISTRATION_SUCCESS = "auth.registration_success";
    public static final String AUTH_ACTIVATION_SUCCESS = "auth.activation_success";
    public static final String AUTH_ACTIVATION_EXPIRED = "auth.activation_expired";
    public static final String AUTH_RESEND_ACTIVATION_SUCCESS = "auth.resend_activation_success";
    public static final String AUTH_SELLER_REGISTRATION_SUCCESS = "auth.seller_registration_success";
    public static final String AUTH_SELLER_APPROVED = "auth.seller_approved";
    public static final String AUTH_SELLER_REJECTED = "auth.seller_rejected";
    public static final String AUTH_ADMIN_PROTECTED = "auth.admin_protected";
    public static final String AUTH_LOGIN_SUCCESS = "auth.login_success";
    public static final String AUTH_REFRESH_SUCCESS = "auth.refresh_success";
    public static final String AUTH_INVALID_REFRESH_TOKEN = "auth.invalid_refresh_token";
    public static final String AUTH_REFRESH_TOKEN_REVOKED = "auth.refresh_token_revoked";
    public static final String AUTH_REFRESH_TOKEN_EXPIRED = "auth.refresh_token_expired";
    public static final String AUTH_PASSWORD_RESET_SENT = "auth.password_reset_sent";
    public static final String AUTH_PASSWORD_UPDATED = "auth.password_updated";
    public static final String AUTH_INVALID_ACTIVATION_TOKEN = "auth.invalid_activation_token";
    public static final String AUTH_ACCOUNT_ALREADY_ACTIVATED = "auth.account_already_activated";
    public static final String AUTH_USER_NOT_FOUND = "auth.user_not_found";
    public static final String AUTH_ACCESS_DENIED = "auth.access_denied";
    public static final String AUTH_AUTHENTICATION_REQUIRED = "auth.authentication_required";

    // Validation Messages
    public static final String VALIDATION_FAILED = "validation.failed";
    public static final String VALIDATION_EMAIL_EXISTS = "validation.email_exists";
    public static final String VALIDATION_GST_EXISTS = "validation.gst_exists";
    public static final String VALIDATION_COMPANY_NAME_EXISTS = "validation.company_name_exists";
    public static final String VALIDATION_PASSWORDS_DO_NOT_MATCH = "validation.passwords_do_not_match";
    public static final String VALIDATION_ACCOUNT_NOT_ACTIVATED = "validation.account_not_activated";
    public static final String VALIDATION_INVALID_RESET_TOKEN = "validation.invalid_reset_token";
    public static final String VALIDATION_RESET_TOKEN_USED = "validation.reset_token_used";
    public static final String VALIDATION_INVALID_EMAIL = "validation.invalid_email";
    public static final String VALIDATION_FIELD_REQUIRED = "validation.field_required";
    public static final String VALIDATION_INVALID_SELLER_ADDRESS_LABEL = "validation.invalid_seller_address_label";

    // Error Messages
    public static final String ERROR_INTERNAL_SERVER = "error.internal_server";
    public static final String ERROR_RESOURCE_NOT_FOUND = "error.resource_not_found";
    public static final String ERROR_SELLER_NOT_FOUND = "error.seller_not_found";
    public static final String ERROR_ROLE_NOT_FOUND = "error.role_not_found";
    public static final String ERROR_BAD_REQUEST = "error.bad_request";
}
