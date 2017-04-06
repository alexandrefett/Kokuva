package com.kokuva;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kokuva.adapter.ItemGalleryAdapter;
import com.kokuva.adapter.ItemGalleryAdapter.OnItemClickListener;

import java.util.ArrayList;

public class FragmentGallery extends BaseFragment {
    // LogCat tag
    private static final String TAG = "--->>>";
    private Uri fileUri;
    private RecyclerView listGallery;
    private ArrayList<String> paths = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            //category = args.getString("category", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_gallery, container, false);

        listGallery = (RecyclerView) view.findViewById(R.id.imagelist);
        listGallery.setAdapter(new ItemGalleryAdapter(this.getContext(), new OnItemClickListener(){
            @Override
            public void onItemClick(ImageView item, String path) {
                Intent intent = new Intent();
                intent.putExtra("path", path);
                intent.putExtra("provider", 20);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
//                    image = scaleCenterCrop(Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(new File(path))), 1280, 1280);
//                    imgPreview.setImageBitmap(image);
        }));
        listGallery.setItemAnimator(new DefaultItemAnimator());
        listGallery.setLayoutManager(new GridLayoutManager(this.getContext(), 2, GridLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}