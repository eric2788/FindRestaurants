package me.sin.findrestaurants.data;

public class RequestState {
    public static final int LOGIN_REQUEST = 1;
    public static final int LOGOUT = 2;
    public static final int EDIT_REQUEST = 3;

    public static final int PHOTO_CAPTURE_REQUEST = 4;
    public static final int GALLERY_PHOTO_REQUEST = 5;
    public static final int COMMENTS_VIEW_REQUEST = 6;
    public static final int RATINGS_VIEW_REQUEST = 7;
    public static final int VIEW_REQUEST = 8;

    public static final int CREATE_COMMENT = 9;
    public static final int CREATE_RATING = 10;

    public static class Result {
        public static final int LOGIN_SUCCESS = 11;
        public static final int LOGIN_FAILED = 12;

        public static final int SAVE_SUCCESS = 13;
        public static final int SAVE_FAILED = 14;

        public static final int COMMENTS_CHANGED = 15;
        public static final int RATINGS_CHANGED = 16;
    }
}
