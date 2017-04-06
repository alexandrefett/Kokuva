package com.kokuva.interfaces;

import com.kokuva.model.AlbumFB;

import java.util.ArrayList;

/**
 * Created by Alexandre on 18/10/2016.
 */

public interface OnAlbumsListener {
    public void onComplete(ArrayList<AlbumFB> albums);
}