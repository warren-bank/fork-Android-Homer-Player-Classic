package com.studio4plus.homerplayer.ui;

import com.studio4plus.homerplayer.R;
import com.studio4plus.homerplayer.ui.UiControllerPlayback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class ElapsedTimeViewLongClickListener implements View.OnLongClickListener {
  private Context context;
  private UiControllerPlayback controller;

  public ElapsedTimeViewLongClickListener(Context context, UiControllerPlayback controller) {
    this.context = context;
    this.controller = controller;
  }

  public boolean onLongClick(View v) {
    LayoutInflater inflator = LayoutInflater.from(context);
    final View dialogView = inflator.inflate(R.layout.dialog_elapsed_time, null);

    final EditText newPositionHH = dialogView.findViewById(R.id.newPositionHH);
    final EditText newPositionMM = dialogView.findViewById(R.id.newPositionMM);
    final EditText newPositionSS = dialogView.findViewById(R.id.newPositionSS);
    final TextView totDurationHH = dialogView.findViewById(R.id.totDurationHH);
    final TextView totDurationMM = dialogView.findViewById(R.id.totDurationMM);
    final TextView totDurationSS = dialogView.findViewById(R.id.totDurationSS);

    long currentPositionMs = controller.getCurrentTotalPositionMs();
    newPositionHH.setText(
      String.format("%02d", TimeUnit.MILLISECONDS.toHours(currentPositionMs))
    );
    newPositionMM.setText(
      String.format("%02d", (TimeUnit.MILLISECONDS.toMinutes(currentPositionMs) % 60))
    );
    newPositionSS.setText(
      String.format("%02d", (TimeUnit.MILLISECONDS.toSeconds(currentPositionMs) % 60))
    );

    long totalDurationMs = controller.getTotalDurationMs();
    totDurationHH.setText(
      String.format("%02d", TimeUnit.MILLISECONDS.toHours(totalDurationMs))
    );
    totDurationMM.setText(
      String.format("%02d", (TimeUnit.MILLISECONDS.toMinutes(totalDurationMs) % 60))
    );
    totDurationSS.setText(
      String.format("%02d", (TimeUnit.MILLISECONDS.toSeconds(totalDurationMs) % 60))
    );

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    AlertDialog dialog;

    builder.setCancelable(true);
    builder.setTitle(R.string.dialog_elapsed_time_title);
    builder.setNegativeButton(android.R.string.cancel, null);
    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        long totalPositionMs = 0L;

        String HH = newPositionHH.getText().toString();
        String MM = newPositionMM.getText().toString();
        String SS = newPositionSS.getText().toString();
        try {
          totalPositionMs += (Long.parseLong(HH) * 3600000L);
        }
        catch(Exception e) {}
        try {
          totalPositionMs += (Long.parseLong(MM) * 60000L);
        }
        catch(Exception e) {}
        try {
          totalPositionMs += (Long.parseLong(SS) * 1000L);
        }
        catch(Exception e) {}

        controller.updateTotalPosition(totalPositionMs);

        dialog.dismiss();
      }
    });
    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
      public void onDismiss(DialogInterface dialog) {
        controller.resumeFromRewind();
      }
    });
    builder.setView(dialogView);

    dialog = builder.create();
    dialog.setCanceledOnTouchOutside(true);
    dialog.show();

    controller.pauseForRewind();

    return true;
  }

  public boolean onLongClickUseDefaultHapticFeedback(View v) {
    return false;
  }
}
