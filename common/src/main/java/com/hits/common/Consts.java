package com.hits.common;

public class Consts {
    public static final String UPLOAD_FILE = "/api/file/upload";
    public static final String DOWNLOAD_FILE = "/api/file/download";
    public static final String GET_FILES = "/api/file/get";
    public static final String REFRESH_TOKEN = "/api/account/refreshToken";
    public static final String REGISTER_USER = "/api/account/register";
    public static final String LOGIN_USER = "/api/account/login";
    public static final String LOGOUT_USER = "/api/account/logout";
    public static final String VALIDATE_TOKEN = "/api/token/validate";
    public static final String EUREKA = "/eureka";
    public static final String CREATE_CATEGORY = "/api/forum/category";
    public static final String GET_CATEGORIES = "/api/forum/category/get";
    public static final String CREATE_THEME = "/api/forum/category/theme";
    public static final String GET_THEMES = "/api/forum/category/theme/get";
    public static final String SEND_MESSAGE = "/api/forum/theme/message";
    public static final String EDIT_CATEGORY = "/api/forum/category/edit";
    public static final String EDIT_THEME = "/api/forum/category/theme/edit";
    public static final String EDIT_MESSAGE = "/api/forum/theme/message/edit";
    public static final String DELETE_CATEGORY = "/api/forum/category/delete";
    public static final String DELETE_THEME = "/api/forum/category/theme/delete";
    public static final String DELETE_MESSAGE = "/api/forum/theme/message/delete";
    public static final String GET_MESSAGES = "/api/forum/theme/{themeId}/message/get";
    public static final String GET_MESSAGES_WITH_FILTERS = "/api/forum/message/get";
    public static final String GET_CATEGORIES_WITH_SUBSTRING = "/api/forum/categories";
    public static final String GET_THEMES_WITH_SUBSTRING = "/api/forum/themes";
    public static final String GET_MESSAGES_WITH_SUBSTRING = "/api/forum/messages";
}
