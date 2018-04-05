package com.kokuva.firestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.kokuva.common.BaseSnapshotParser;

public interface SnapshotParser<T> extends BaseSnapshotParser<DocumentSnapshot, T> {}
