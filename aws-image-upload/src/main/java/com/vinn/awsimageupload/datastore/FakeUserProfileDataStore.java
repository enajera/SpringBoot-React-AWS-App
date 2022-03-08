package com.vinn.awsimageupload.datastore;

import com.vinn.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static{
        USER_PROFILES.add(new UserProfile(UUID.fromString("341ada8d-f0c1-4408-b650-852c442f2fb0"),"JanetJones",null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("f656e8d7-9d88-49a3-b005-117108763249"),"AntonioJunior",null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
