package com.kokuva.firestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kokuva.common.BaseChangeEventListener;

/**
 * Listener for changes to a {@link FirestoreArray}.
 */
public interface ChangeEventListener extends BaseChangeEventListener<DocumentSnapshot, FirebaseFirestoreException> {}
