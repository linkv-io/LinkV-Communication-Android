package com.linkv.lvcdemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.linkv.lvcdemo.R;

/**
 * Created by LiuYu on 2020/5/25 6:11 PM
 */
public class DialogUtils {

    public interface OnPositiveListener {
        void positive(String roomId);
    }

    public void showInputDialog(final Context context, final OnPositiveListener onPositiveListener) {

        final EditText editText = new EditText(context);
        editText.setGravity(Gravity.CENTER);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(context);
        inputDialog.setTitle(context.getString(R.string.please_input_pk_room_id)).setView(editText);
        inputDialog.setPositiveButton(context.getString(R.string.btn_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(editText.getText().toString())) {
                            Toast.makeText(context, context.getString(R.string.input_room_id_empty), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (onPositiveListener != null) {
                            onPositiveListener.positive(editText.getText().toString());
                        }
                    }
                });
        inputDialog.setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        inputDialog.show();
    }

}
