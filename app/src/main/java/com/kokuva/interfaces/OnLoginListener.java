package com.kokuva.interfaces;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.kokuva.model.Profile;

/**
 * Created by Alexandre on 18/10/2016.
 */

public interface OnLoginListener {
    public void onComplete(Profile profile, String url, AccessToken accessToken);
    public void onError(FacebookException exception);
    public void onCancel();
}