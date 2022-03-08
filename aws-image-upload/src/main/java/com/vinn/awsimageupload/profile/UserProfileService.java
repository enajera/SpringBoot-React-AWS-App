package com.vinn.awsimageupload.profile;

import com.amazonaws.services.s3.model.S3Object;
import com.vinn.awsimageupload.bucket.BucketName;
import com.vinn.awsimageupload.filestore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore filestore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore filestore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.filestore = filestore;
    }


     List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

     void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
       //1. Check if image is not empty
         isFileEmpty(file);
         //2. If file is an image
         isImage(file);
         //3. The user exists in our database
         UserProfile user = getUserProfileOrThrow(userProfileId);

         //4. Grab some metadata from file if any
         Map<String,String> metadata = new HashMap<>();
         metadata.put("Content-Type", file.getContentType());
         metadata.put("Content-Length", String.valueOf(file.getSize()));

       //5. Store the image in s3 and update database (userProfileImageLink) with s3 image link
         String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
         String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

         try {
             filestore.save(path,filename, Optional.of(metadata), file.getInputStream());
             user.setUserProfileImageLink(filename);
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }

     }

    byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);

        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());

        return user.getUserProfileImageLink()
                .map(key -> filestore.download(path, key))
                .orElse(new byte[0]);

    }

    private void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()){
            throw new IllegalStateException("Cannot upload empty file ["+ file.getSize()+"]");
        }
    }

    private void isImage(MultipartFile file) {
        if(!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(file.getContentType())){
            throw new IllegalStateException("File must be an image["+file.getContentType()+"]");
        }
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId){
        return userProfileDataAccessService
                .getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst().orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));

    }


}
