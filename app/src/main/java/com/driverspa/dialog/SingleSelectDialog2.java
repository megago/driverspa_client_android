
package com.driverspa.dialog;
import com.driverspa.R;
import com.driverspa.BA;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import java.util.HashMap;
import java.util.List;

/**
 * @author Yerzhan Tanatov
 */
public abstract class SingleSelectDialog2 {

	public abstract void onDismiss(HashMap<Integer,String> namesSelectedItm);
	
    public static final String FILTER_NAME = "FILTER_NAME";
    public static final String FILTER_ID = "FILTER_ID";
    
	AlertDialog.Builder dialogBuilder;
	AlertDialog dialog = null;
	Activity activity;
	HashMap<Integer,String> selectedItem;
	boolean[] itemsChecked;
	String[] itemsNames;
	String[] itemsIds;
    String title;
    public static String ALL_CHOICES;
    
	public SingleSelectDialog2(Activity activity, List<HashMap<String,String>> model, String title) {
		this.activity = activity;
		this.title = title;
//		ALL_CHOICES = activity.getResources().getString(R.string.history_choice_all);
		if (model != null && model.size() > 0) {
			createFilterData(model);
		}
		
	}

	public SingleSelectDialog2(Activity activity, List<HashMap<String,String>> model, String title,
			HashMap<Integer,String> selectedItem) {
		this.activity = activity;
//		ALL_CHOICES = activity.getResources().getString(R.string.history_choice_all);
		this.title = title;
//		this.selectedItem = selectedItem;
		if (model != null && model.size() > 0) {
			createFilterData(model);
		}
	}

	void createFilterData(List<HashMap<String,String>> model) {
		itemsNames = new String[model.size() + 1];
		itemsChecked = new boolean[model.size() + 1];
		itemsIds = new String[model.size() + 1];
		itemsNames[0] = ALL_CHOICES;
		itemsChecked[0] = true;
		itemsIds[0] = "";
		for (int i = 0; i < model.size(); i++) {
			itemsNames[i + 1] = model.get(i).get(FILTER_NAME).toString();
			itemsIds[i + 1] = model.get(i).get(FILTER_ID).toString();
			itemsChecked[i + 1] = selectedItem != null
					&& selectedItem.get(FILTER_NAME).equals(itemsNames[i + 1]) ? true : false;
		}
		init(itemsNames, itemsChecked);
	}

	private void init(String[] items, boolean[] checked) {

		dialogBuilder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
		dialogBuilder.setTitle(title);
		dialogBuilder.setPositiveButton(BA.str(R.string.ok_word),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					HashMap<Integer,String> selectedMap = new HashMap<Integer,String>();
						int j = 0;
						for (int i = 1; i < itemsChecked.length; i++) {
							if (itemsChecked[i]) {
								selectedMap.put(j, itemsIds[i]);
								j++;
							}							
						}
					onDismiss(selectedMap);
					}
				});

		
		dialogBuilder.setMultiChoiceItems(items, checked,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1,
							boolean arg2) {
						if (!itemsNames[arg1].equals(ALL_CHOICES)) {

							for (int i = 1; i < itemsNames.length; i++) {
								itemsChecked[i] = false;
								itemsChecked[0] = false;
								((AlertDialog) arg0).getListView().setItemChecked(i, false);
							}
							
							itemsChecked[arg1] = arg2;
							itemsChecked[0] = false;
							((AlertDialog) arg0).getListView().setItemChecked(0, false);

							boolean noCheckBoxChecked = true;
							for (int i = 1; i < itemsNames.length; i++) {
								if (itemsChecked[i])
									noCheckBoxChecked = false;
							}
							if (noCheckBoxChecked) {
								itemsChecked[0] = true;
								((AlertDialog) arg0).getListView().setItemChecked(0, true);
							}
						} else if (itemsChecked[0]) {
							for (int i = 1; i < itemsNames.length; i++) {
								((AlertDialog) arg0).getListView().setItemChecked(i, false);
								itemsChecked[i] = false;
							}
						} else {
							itemsChecked[0] = true;
							((AlertDialog) arg0).getListView().setItemChecked(0, true);
						}
					}
				});

		dialogBuilder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				HashMap<Integer,String> selectedMap = new HashMap<Integer,String>();
				int j = 0;
				for (int i = 1; i < itemsChecked.length; i++) {
					if (itemsChecked[i]) {						
						selectedMap.put(j, itemsIds[i]);
						j++;
					}
				}				
				onDismiss(selectedMap);
			}
		});
		dialog = dialogBuilder.create();
	}

	public void show() {
		dialogBuilder.show();
	}

	public void clearFilter() {
		for (int i = 0; i < itemsChecked.length; i++) {
			if (itemsChecked[i]) {
				itemsChecked[i] = false;
			}
		}
	}

	public String[] getItemsIds() {
		return itemsIds;
	}

	public boolean[] getItemsChecked() {
		return itemsChecked;
	}

	public void setItemsChecked(final boolean[] itemsChecked) {
		this.itemsChecked = itemsChecked;
		dialogBuilder.setMultiChoiceItems(itemsNames, itemsChecked,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1,
							boolean arg2) {
						if (!itemsNames[arg1].equals(ALL_CHOICES)) {
							
							for (int i = 1; i < itemsNames.length; i++) {
								itemsChecked[i] = false;
								itemsChecked[0] = false;
								((AlertDialog) arg0).getListView().setItemChecked(i, false);
							}
							
							itemsChecked[arg1] = arg2;
							itemsChecked[0] = false;
							((AlertDialog) arg0).getListView().setItemChecked(0, false);
							boolean noCheckBoxChecked = true;
							for (int i = 1; i < itemsNames.length; i++) {
								if (itemsChecked[i])
									noCheckBoxChecked = false;
							}
							if (noCheckBoxChecked) {
								itemsChecked[0] = true;
								((AlertDialog) arg0).getListView().setItemChecked(0, true);
							}
						} else if (itemsChecked[0]) {
							for (int i = 1; i < itemsNames.length; i++) {
								((AlertDialog) arg0).getListView().setItemChecked(i, false);
								itemsChecked[i] = false;
							}
						} else {
							itemsChecked[0] = true;
							((AlertDialog) arg0).getListView().setItemChecked(0, true);
						}
					}
	    });
	}
}
