package com.mygroupcx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;

public class InputDialog extends AlertDialog {

    private ActionListener listener;
    private final TextView titleTextView;

    protected InputDialog(Context context) {
        super(context);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null);
        titleTextView = (TextView) contentView.findViewById(R.id.title_tv);
        final EditText inputEditText = (EditText) contentView.findViewById(R.id.et);
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ObjectUtils.isNotEmpty(listener)) {
                    listener.onConfirm(inputEditText.getText().toString(), InputDialog.this);
                }
            }
        });
        setView(contentView);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public ActionListener getListener() {
        return listener;
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    public interface ActionListener {
        void onConfirm(String content, Dialog dialog);
    }
}
