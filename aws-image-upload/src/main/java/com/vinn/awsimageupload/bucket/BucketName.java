package com.vinn.awsimageupload.bucket;

public enum BucketName {
    PROFILE_IMAGE("vinn-image-upload-123");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
