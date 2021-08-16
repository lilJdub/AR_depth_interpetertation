package com.google.ar.core.examples.java.helloar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.common.base.Preconditions;
//cloud anchor sample給的...我就照搬
public class HostDialogFragment extends DialogFragment {
    interface OkListener {
        /**
         * This method is called by the dialog box when its OK button is pressed.
         *
         * @param dialogValue the long value from the dialog box
         */
        void onOkPressed(String dialogValue);
    }

    private EditText nicknameField;
    private OkListener okListener;

    public void setOkListener(OkListener okListener) {
        this.okListener = okListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String defaultNickname = getArguments().getString("nickname");
        FragmentActivity activity =
                Preconditions.checkNotNull(getActivity(), "The activity cannot be null.");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Passing null as the root is fine, because the view is for a dialog.
        View dialogView = activity.getLayoutInflater().inflate(R.layout.save_anchor_dialog, null);
        nicknameField = dialogView.findViewById(R.id.nickname_edit_text);
        nicknameField.setText(defaultNickname);
        builder
                .setView(dialogView)
                .setTitle("ENTER")
                .setPositiveButton(
                        "OK",
                        (dialog, which) -> {
                            Editable nicknameText = nicknameField.getText();
                            if (okListener != null) {
                                okListener.onOkPressed(nicknameText.toString());
                            }
                        });
        return builder.create();
    }

}
