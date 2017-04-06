package com.kokuva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kokuva.model.Profile;
import com.kokuva.rangebar.RangeBar;
import com.kokuva.rangebar.RangeBar.OnRangeBarChangeListener;

public class FragmentPreferences extends BaseFragment implements OnCheckedChangeListener{
    private DatabaseReference myRef;
    private Profile profile;
    private Switch male;
    private Switch female;
    private Switch crush;
    private Switch msg;
    private RangeBar range;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            //category = args.getString("category", "");
        }
        myRef = FirebaseDatabase.getInstance().getReference();
        profile = KokuvaApp.getInstance().getProfile();

    }

    private void initViews(){
        male.setChecked(profile.isMale());
        female.setChecked(profile.isFemale());
        crush.setChecked(profile.isCrush());
        msg.setChecked(profile.isMsg());
        range.setThumbIndices((int)profile.getAgemin(), (int)profile.getAgemax());
        male.setOnCheckedChangeListener(this);
        female.setOnCheckedChangeListener(this);
        crush.setOnCheckedChangeListener(this);
        msg.setOnCheckedChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_preferences, container, false);
        male = (Switch)view.findViewById(R.id.switch2);
        female = (Switch)view.findViewById(R.id.switch3);
        crush = (Switch)view.findViewById(R.id.switch4);
        msg = (Switch)view.findViewById(R.id.switch5);

        range = (RangeBar)view.findViewById(R.id.range_slider6);
        range.setLeft(18);
        range.setRight(99);
        range.setOnRangeBarChangeListener(new OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                profile.setAgemin(leftThumbIndex);
                profile.setAgemax(rightThumbIndex);
            }
        });

        initViews();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        switch(id){
            case R.id.switch2: profile.setMale(compoundButton.isChecked());
                break;
            case R.id.switch3: profile.setFemale(compoundButton.isChecked());
                break;
            case R.id.switch4: profile.setCrush(compoundButton.isChecked());
                break;
            case R.id.switch5: profile.setMsg(compoundButton.isChecked());
                break;
        }
    }
    private void savePreferences(){
        myRef.child("users").child(profile.getUserid()).child(Profile.AGE_MAX).setValue(profile.getAgemax());
        myRef.child("users").child(profile.getUserid()).child(Profile.AGE_MIN).setValue(profile.getAgemin());
        myRef.child("users").child(profile.getUserid()).child(Profile.CRUSH).setValue(profile.isCrush());
        myRef.child("users").child(profile.getUserid()).child(Profile.MSG).setValue(profile.isMsg());
        myRef.child("users").child(profile.getUserid()).child(Profile.MALE).setValue(profile.isMale());
        myRef.child("users").child(profile.getUserid()).child(Profile.FEMALE).setValue(profile.isFemale());
    }

    @Override
    public void onPause(){
        super.onPause();
        savePreferences();
    }
}