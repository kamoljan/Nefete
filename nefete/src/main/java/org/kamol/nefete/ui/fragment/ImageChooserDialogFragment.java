package org.kamol.nefete.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ImageChooserDialogFragment extends DialogFragment {
  private OnImageChooserDialogListener mListener;

  /* The activity that creates an instance of this dialog fragment must
   * implement this interface in order to receive event callbacks.
   * Each method passes the DialogFragment in case the host needs to query it. */
  public interface OnImageChooserDialogListener {
    public void onCloseDialog(int item);
  }

  public void setOnImageChooserDialogListener(OnImageChooserDialogListener listener) {
    mListener = listener;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    ListAdapter adapter = new ArrayAdapter<Item>(getActivity(),
        android.R.layout.select_dialog_item, android.R.id.text1, items) {
      public View getView(int position, View convertView, ViewGroup parent) {
        //User super class to create the View
        View v = super.getView(position, convertView, parent);
        TextView tv = (TextView) v.findViewById(android.R.id.text1);

        //Put the image on the TextView
        tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

        //Add margin between image and text (support various screen densities)
        int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
        tv.setCompoundDrawablePadding(dp5);

        return v;
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        if (mListener != null) {
          mListener.onCloseDialog(item);
        }
      }
    });
    return builder.create();
  }

  public static class Item {
    public final String text;
    public final int icon;

    public Item(String text, Integer icon) {
      this.text = text;
      this.icon = icon;
    }

    @Override
    public String toString() {
      return text;
    }
  }

  final Item[] items = {
      new Item("Take photo", android.R.drawable.ic_menu_camera),
      new Item("Attach photo", android.R.drawable.ic_menu_gallery),
  };
}

