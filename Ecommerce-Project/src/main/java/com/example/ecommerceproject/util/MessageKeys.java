package com.example.ecommerceproject.util;

public final class MessageKeys {

    private MessageKeys() {
    }

    public static final String PROTECTED_ADMIN_FIRST_NAME = "System";
    public static final String PROTECTED_ADMIN_LAST_NAME = "Admin";
    public static final String PROTECTED_ADMIN_PASSWORD = "Admin@123";
    public static final String PROTECTED_ADMIN_EMAIL = "admin@ecommerce.com";
    public static final String AUTH_INVALID_CREDENTIALS = "auth.invalid_credentials";
    public static final String AUTH_ACCOUNT_LOCKED = "auth.account_locked";
    public static final String AUTH_ACCOUNT_NOT_ACTIVATED = "auth.account_not_activated";
    public static final String AUTH_PASSWORD_EXPIRED = "auth.password_expired";
    public static final String AUTH_ACCESS_DENIED = "auth.access_denied";
    public static final String AUTH_AUTHENTICATION_REQUIRED = "auth.authentication_required";
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
    public static final String AUTH_USER_NOT_AUTHENTICATED = "auth.user_not_authenticated";

    public static final String VALIDATION_FAILED = "validation.failed";
    public static final String VALIDATION_EMAIL_INVALID = "validation.email_invalid";
    public static final String VALIDATION_EMAIL_EXISTS = "validation.email_exists";
    public static final String VALIDATION_GST_EXISTS = "validation.gst_exists";
    public static final String VALIDATION_GST_INVALID="validation.gst_invalid";
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
    public static final String VALIDATION_ADDRESS_REQUIRED = "validation.address_required";
    public static final String VALIDATION_CITY_REQUIRED = "validation.city_required";
    public static final String VALIDATION_ZIP_CODE_REQUIRED = "validation.zip_code_required";
    public static final String VALIDATION_STATE_REQUIRED = "validation.state_required";
    public static final String VALIDATION_ADDRESS_LINE_REQUIRED = "validation.address_line_required";
    public static final String VALIDATION_COUNTRY_REQUIRED = "validation.country_required";
    public static final String VALIDATION_COUNTRY_LENGTH = "validation.country_length";
    public static final String VALIDATION_CITY_LENGTH = "validation.city_length";
    public static final String VALIDATION_STATE_LENGTH = "validation.state_length";
    public static final String VALIDATION_USER_ALREADY_DELETED = "validation.user_already_deleted";
    public static final String VALIDATION_USER_ALREADY_DEACTIVATED = "validation.user_already_deactivated";
    public static final String VALIDATION_USER_ALREADY_ACTIVATED = "validation.user_already_activated";
    
    
    // Admin Controller Validation Messages
    public static final String VALIDATION_PAGE_OFFSET_NEGATIVE = "validation.page_offset_negative";
    public static final String VALIDATION_PAGE_SIZE_MIN = "validation.page_size_min";
    public static final String VALIDATION_PAGE_SIZE_MAX = "validation.page_size_max";
    public static final String VALIDATION_CUSTOMER_ID_POSITIVE = "validation.customer_id_positive";
    public static final String VALIDATION_SELLER_ID_POSITIVE = "validation.seller_id_positive";
    public static final String VALIDATION_INVALID_SELLER_ADDRESS_LABEL = "validation.invalid_seller_address_label";
    public static final String VALIDATION_INVALID_CUSTOMER_ADDRESS_LABEL = "validation.invalid_customer_address_label";
    public static final String VALIDATION_SELLER_SINGLE_ADDRESS = "validation.seller_single_address";

    public static final String ERROR_INTERNAL_SERVER = "error.internal_server";
    public static final String ERROR_RESOURCE_NOT_FOUND = "error.resource_not_found";
    public static final String ERROR_SELLER_NOT_FOUND = "error.seller_not_found";
    public static final String ERROR_ROLE_NOT_FOUND = "error.role_not_found";
    public static final String ERROR_BAD_REQUEST = "error.bad_request";
    public static final String ERROR_ADDRESS_NOT_FOUND = "error.address_not_found";
    public static final String ERROR_CUSTOMER_NOT_FOUND = "error.customer_not_found";
    public static final String ERROR_USER_IS_DELETED = "error.user_is_deleted";
    public static final String ERROR_ADDRESS_PERMISSION_DENIED = "error.address_permission_denied";
    public static final String ERROR_ACCESS_DENIED = "error.access_denied";
    

    public static final String ADMIN_CUSTOMER_ACTIVATED = "admin.customer_activated";
    public static final String ADMIN_CUSTOMER_DEACTIVATED = "admin.customer_deactivated";
    public static final String ADMIN_SELLER_ACTIVATED = "admin.seller_activated";
    public static final String ADMIN_SELLER_DEACTIVATED = "admin.seller_deactivated";

    // Customer Service Messages
    public static final String CUSTOMER_PROFILE_UPDATED = "customer.profile_updated";
    public static final String CUSTOMER_PASSWORD_UPDATED = "customer.password_updated";
    public static final String CUSTOMER_ADDRESS_ADDED = "customer.address_added";
    public static final String CUSTOMER_ADDRESS_UPDATED = "customer.address_updated";
    public static final String CUSTOMER_ADDRESS_DELETED = "customer.address_deleted";
    public static final String CUSTOMER_ADDRESS_ALREADY_EXISTS = "customer.address_already_exists";

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
    
    // JWT Token Messages
    public static final String JWT_TOKEN_EXPIRED = "jwt.token_expired";
    public static final String JWT_TOKEN_INVALID = "jwt.token_invalid";
    public static final String JWT_TOKEN_MALFORMED = "jwt.token_malformed";
    public static final String JWT_TOKEN_REVOKED = "jwt.token_revoked";
    public static final String FIELD_VALUE_MUST_BE_UNIQUE = null;
    public static final String FIELD_CREATED_SUCCESSFULLY = null;
    public static final String INVALID_PARENT_CATEGORY_ID = null;
    public static final String CATEGORY_NAME_MUST_BE_UNIQUE = null;
    public static final String CATEGORY_NAME_MUST_BE_UNIQUE_WITHIN_PARENT = null;
    public static final String CATEGORY_CREATED_SUCCESSFULLY = null;
    public static final String INVALID_CATEGORY_ID = null;
    public static final String CATEGORY_UPDATED_SUCCESSFULLY = null;
    public static final String INVALID_METADATA_FIELD_ID = null;
    public static final String METADATA_FIELDS_ADDED_TO_CATEGORY_SUCCESSFULLY = null;

}