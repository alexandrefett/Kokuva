package com.kokuva.interfaces;

import com.kokuva.model.PhotoFB;

import java.util.ArrayList;

/**
 * Created by Alexandre on 18/10/2016.
 */

public interface OnPhotosListener {
    public void onComplete(ArrayList<PhotoFB> urls);
}
