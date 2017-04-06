package com.kokuva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kokuva.adapter.ItemAlbumFbAdapter;
import com.kokuva.adapter.ItemFbAdapter;
import com.kokuva.adapter.ItemFbAdapter.OnItemClickListener;
import com.kokuva.interfaces.OnAlbumsListener;
import com.kokuva.interfaces.OnPhotosListener;
import com.kokuva.model.AlbumFB;
import com.kokuva.model.PhotoFB;

import java.util.ArrayList;

public class FragmentFacebook extends BaseFragment {
    // LogCat tag
    private static final String TAG = "--->>>";
    private RecyclerView listGallery;
    private RecyclerView listAlbuns;
    private EasyFacebook easyFace;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        easyFace = EasyFacebook.getInstance(getContext());

        Bundle args = getArguments();
        if(args!=null) {
            //category = args.getString("category", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fb, container, false);

        listGallery = (RecyclerView) view.findViewById(R.id.imagelist);
        listGallery.setItemAnimator(new DefaultItemAnimator());
        listGallery.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));

        listAlbuns = (RecyclerView) view.findViewById(R.id.albumlist);
        listAlbuns.setItemAnimator(new DefaultItemAnimator());
        listAlbuns.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));

        getFacebookPhotos();
        getFacebookAlbuns();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setListGallery(ArrayList<PhotoFB> urls){
        ItemFbAdapter adapter = new ItemFbAdapter(getContext(), urls, new OnItemClickListener(){
            @Override
            public void onItemClick(String path) {
                Intent intent = new Intent();
                intent.putExtra("path", path);
                intent.putExtra("provider", 10);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        listGallery.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getFacebookAlbum(String id){
        easyFace.getAlbumPhotos(id, new OnPhotosListener() {
            @Override
            public void onComplete(ArrayList<PhotoFB> urls) {
                setListGallery(urls);
            }
        });
    }

    private void getFacebookAlbuns(){
        easyFace.getMyAlbuns(new OnAlbumsListener() {
            @Override
            public void onComplete(ArrayList<AlbumFB> albums) {
                ItemAlbumFbAdapter adapter = new ItemAlbumFbAdapter(getContext(), albums, new ItemAlbumFbAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String id) {
                        getFacebookAlbum(id);
                    }
                } );
                listAlbuns.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getFacebookPhotos(){
        easyFace.getMyUploadedPhotos(new OnPhotosListener() {
            @Override
            public void onComplete(ArrayList<PhotoFB> urls) {
                Log.d(TAG, "oncomplete "+urls.size());
                setListGallery(urls);
            }
        });
    }


}