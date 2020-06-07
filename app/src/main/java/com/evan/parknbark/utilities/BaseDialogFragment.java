package com.evan.parknbark.utilities;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.DialogFragment;

import com.evan.parknbark.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public abstract class BaseDialogFragment extends DialogFragment {
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore db;
    protected String ERROR_MSG;
    protected boolean isFirebaseProcessRunning = false;

    @Override
    public void onStart() {
        super.onStart();
        ERROR_MSG = getResources().getString(R.string.error_message);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    protected void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected void showErrorToast() {
        Toasty.error(getContext(), ERROR_MSG, Toasty.LENGTH_LONG).show();
    }

    protected void showSuccessToast(int resId) {
        Toasty.success(getContext(), getResources().getString(resId), Toasty.LENGTH_SHORT).show();
    }

    protected void showInfoToast(int resId) {
        Toasty.info(getContext(), getResources().getString(resId), Toasty.LENGTH_SHORT).show();
    }
}
