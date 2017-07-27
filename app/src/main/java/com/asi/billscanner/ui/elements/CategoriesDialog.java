package com.asi.billscanner.ui.elements;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asi.billscanner.BillsAdapter;
import com.asi.billscanner.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoriesDialog {

    @BindView(R.id.newCategory) EditText newCategory;
    @BindView(R.id.addProductButton) Button addCategory;

    private final String CLASS_TAG = "CategoriesDialog";

    private BillsAdapter billsAdapter;
    private Dialog dialog;
    private Context context;

    private Vector<View> categoryItem = new Vector<>();
    private Vector<View> newCategoryItem = new Vector<>();
    private ArrayAdapter<String> categorySpinnerAdapter;
    private LinearLayout layout;

    public CategoriesDialog(Context context){
        this.context = context;
    }

    public CategoriesDialog(Context context, ArrayAdapter<String> adapter){
        this(context);
        categorySpinnerAdapter = adapter;
    }

    private void build(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.categories_edit_dialog);
        dialog.setTitle(R.string.categories_dialog_title);

        ButterKnife.bind(this, dialog);

        layout = (LinearLayout) dialog.findViewById(R.id.categoriesDialogItems);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateCategorySpinner();
                updateDb();
            }
        });

        loadCategoryItems();
    }

    private void updateCategorySpinner(){
        if(categorySpinnerAdapter != null){
            for (View view : newCategoryItem){
                TextView categoryName = (TextView) view.findViewById(R.id.categoryName);
                CheckBox categoryCheckBox = (CheckBox) view.findViewById(R.id.categoryCheckBox);
                if(categoryCheckBox.isChecked()){
                    categorySpinnerAdapter.add(categoryName.getText().toString());
                    categorySpinnerAdapter.notifyDataSetChanged();
                    newCategoryItem.remove(view);
                }
            }
        }
    }

    private void updateDb(){

    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    private void loadCategoryItems(){
        Vector<String> categories = billsAdapter.getUsedCategories();
        Vector<String> discardedCategories = billsAdapter.getDiscardedCategories();
        for(int i = 0; i < categories.size(); ++i){
            View view = dialog.getLayoutInflater().
                    inflate(R.layout.categories_item, layout, false);

            TextView categoryName = (TextView) view.findViewById(R.id.categoryName);
            categoryName.setText(categories.get(i));

            CheckBox categoryCheckBox = (CheckBox) view.findViewById(R.id.categoryCheckBox);
            categoryCheckBox.setChecked(true);

            for(int j = 0; j < discardedCategories.size(); ++j){
                if(categories.elementAt(i).equals(discardedCategories.elementAt(j))){
                    categoryCheckBox.setChecked(false);
                    break;
                }
            }

            categoryItem.addElement(view);
            layout.addView(view);
        }
    }

    private void addNewProduct(String name){
        View view = dialog.getLayoutInflater().
                inflate(R.layout.categories_item, layout, false);

        TextView categoryName = (TextView) view.findViewById(R.id.categoryName);
        categoryName.setText(name);

        CheckBox categoryCheckBox = (CheckBox) view.findViewById(R.id.categoryCheckBox);
        categoryCheckBox.setChecked(true);

        newCategoryItem.add(view);
        categoryItem.add(view);
        layout.addView(view);
    }

    private void addNewVisibility(int i){
        newCategory.setVisibility(i);
        addCategory.setVisibility(i);
    }

    @OnClick(R.id.addProductButton)
    void addProductOnClick(View view){
        if(!newCategory.getText().toString().isEmpty()) {
            addNewProduct(newCategory.getText().toString());
            newCategory.setText("");
        }
    }

    public void onStart(){
        EventBus.getDefault().register(this);
    }

    public void onStop(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void onGetBillsAdapterEvent(CategoriesDialog.GetBillsAdapter event) {
        Log.i(CLASS_TAG, "onGetBillsAdapterEvent called");
        if(event.getBillsAdapter() != null) {
            this.billsAdapter = event.getBillsAdapter();
            build();
        }
        else {
            Log.wtf(CLASS_TAG, "onGetBillsAdapterEvent; BillsAdapter not initialized");
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    public static class GetBillsAdapter {
        private final BillsAdapter billsAdapter;

        public GetBillsAdapter(BillsAdapter billsAdapter) {
            this.billsAdapter = billsAdapter;
        }

        BillsAdapter getBillsAdapter() {
            return billsAdapter;
        }
    }

}
