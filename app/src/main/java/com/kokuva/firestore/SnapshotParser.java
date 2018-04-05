package com.kokuva.firestore;

import com.firebase.ui.common.BaseSnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Base interface for a {@link BaseSnapshotParser} for {@link DocumentSnapshot}.
 */
public interface SnapshotParser<T> extends BaseSnapshotParser<DocumentSnapshot, T> {}
