package com.example.ecommerceproject.util;

public final class MessageKeys {

    private MessageKeys() {
    }

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

    public static final String VALIDATION_FAILED = "validation.failed";
    public static final String VALIDATION_EMAIL_EXISTS = "validation.email_exists";
    public static final String VALIDATION_GST_EXISTS = "validation.gst_exists";
    public static final String VALIDATION_COMPANY_NAME_EXISTS = "validation.company_name_exists";
    public static final String VALIDATION_PASSWORDS_DO_NOT_MATCH = "validation.passwords_do_not_match";
    public static final String VALIDATION_PASSWORD_DO_NOT_MATCH = "validation.password_do_not_match";
    public static final String VALIDATION_ACCOUNT_NOT_ACTIVATED = "validation.account_not_activated";
    public static final String VALIDATION_INVALID_RESET_TOKEN = "validation.invalid_reset_token";
    public static final String VALIDATION_RESET_TOKEN_USED = "validation.reset_token_used";
    public static final String VALIDATION_PHONE_INVALID = "validation.phone_invalid";
    public static final String VALIDATION_CITY_INVALID = "validation.city_invalid";
    public static final String VALIDATION_STATE_INVALID = "validation.state_invalid";
    public static final String VALIDATION_COUNTRY_INVALID = "validation.country_invalid";
    public static final String VALIDATION_ADDRESS_LINE_INVALID = "validation.address_line_invalid";
    public static final String VALIDATION_ZIP_CODE_INVALID = "validation.zip_code_invalid";
    public static final String VALIDATION_ADDRESS_LABEL_REQUIRED = "validation.address_label_required";
    public static final String VALIDATION_USER_ALREADY_DELETED = "validation.user_already_deleted";
    public static final String VALIDATION_USER_ALREADY_DEACTIVATED = "validation.user_already_deactivated";

    public static final String ERROR_INTERNAL_SERVER = "error.internal_server";
    public static final String ERROR_RESOURCE_NOT_FOUND = "error.resource_not_found";
    public static final String ERROR_SELLER_NOT_FOUND = "error.seller_not_found";
    public static final String ERROR_ROLE_NOT_FOUND = "error.role_not_found";
    public static final String ERROR_BAD_REQUEST = "error.bad_request";
    public static final String ERROR_ADDRESS_NOT_FOUND = "error.address_not_found";
    public static final String ERROR_CUSTOMER_NOT_FOUND = "error.customer_not_found";
    public static final String ERROR_USER_IS_DELETED = "error.user_is_deleted";

    public static final String ADMIN_CUSTOMER_ACTIVATED = "admin.customer_activated";
    public static final String ADMIN_CUSTOMER_DEACTIVATED = "admin.customer_deactivated";
    public static final String ADMIN_SELLER_ACTIVATED = "admin.seller_activated";
    public static final String ADMIN_SELLER_DEACTIVATED = "admin.seller_deactivated";

    public static final String SELLER_PROFILE_UPDATED = "seller.profile_updated";
    public static final String SELLER_PASSWORD_UPDATED = "seller.password_updated";
    public static final String SELLER_ADDRESS_UPDATED = "seller.address_updated";

    public static final String EMAIL_ACTIVATION_SUBJECT = "email.activation.subject";
    public static final String EMAIL_SELLER_REGISTRATION_SUBJECT = "email.seller_registration.subject";
    public static final String EMAIL_ACCOUNT_LOCKED_SUBJECT = "email.account_locked.subject";
    public static final String EMAIL_PASSWORD_RESET_SUBJECT = "email.password_reset.subject";
    public static final String EMAIL_PASSWORD_CHANGED_SUBJECT = "email.password_changed.subject";
    public static final String EMAIL_ACCOUNT_ACTIVATED_SUBJECT = "email.account_activated.subject";
    public static final String EMAIL_ACCOUNT_DEACTIVATED_SUBJECT = "email.account_deactivated.subject";

    public static final String EMAIL_ACTIVATION_BODY = "email.activation.body";
    public static final String EMAIL_SELLER_REGISTRATION_BODY = "email.seller_registration.body";
    public static final String EMAIL_ACCOUNT_LOCKED_BODY = "email.account_locked.body";
    public static final String EMAIL_PASSWORD_RESET_BODY = "email.password_reset.body";
    public static final String EMAIL_PASSWORD_CHANGED_BODY = "email.password_changed.body";
    public static final String EMAIL_ACCOUNT_ACTIVATED_BODY = "email.account_activated.body";
    public static final String EMAIL_ACCOUNT_DEACTIVATED_BODY = "email.account_deactivated.body";
}