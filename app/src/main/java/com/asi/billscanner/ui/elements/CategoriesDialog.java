package com.asi.billscanner.ui.elements;

import android.app.Dialog;
import android.content.Context;

import com.asi.billscanner.R;

public class CategoriesDialog {

    private Dialog dialog;
    private Context context;

    public CategoriesDialog(Context context){
        this.context = context;
    }

    public void build(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.categories_edit_dialog);
        dialog.setTitle(R.string.categories_dialog_title);
    }

    public void show(){
        dialog.show();
    }
}
