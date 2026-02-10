package org.xuanfeng.idphotosbackend.constant;

public class CommonConstants {

    /**
     * 存储的照片的key，例如：photo/2026-01-01/407e9598-c32e-44b3-b0dd-d9055cd24494
     */
    public static final String S3_PHOTO_KEY = "photo/%s/%s";

    /**
     * 存储的上传的图片的key，例如：security/2026-01-01/407e9598-c32e-44b3-b0dd-d9055cd24494
     */
    public static final String S3_SECURITY_KEY = "security/%s/%s";

    /**
     * 存储的用户头像的key，例如：avatars/407e9598-c32e-44b3-b0dd-d9055cd24494
     */
    public static final String S3_AVATARS_KEY = "avatars/%s";
}
