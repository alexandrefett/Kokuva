package com.kokuva.firestore;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.kokuva.common.BaseCachingSnapshotParser;
import com.kokuva.common.BaseSnapshotParser;

public class CachingSnapshotParser<T> extends BaseCachingSnapshotParser<DocumentSnapshot, T>
        implements SnapshotParser<T> {

    public CachingSnapshotParser(@NonNull BaseSnapshotParser<DocumentSnapshot, T> parser) {
        super(parser);
    }

    @NonNull
    @Override
    public String getId(@NonNull DocumentSnapshot snapshot) {
        return snapshot.getId();
    }
}
