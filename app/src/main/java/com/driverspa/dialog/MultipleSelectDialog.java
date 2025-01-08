
package com.driverspa.dialog;

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
public abstract class MultipleSelectDialog {

	public abstract void onDismiss(HashMap<Integer,String> namesSelectedItm);
	
    public static final String NAME = "NAME";
    public static final String ID = "ID";    
	AlertDialog.Builder dialogBuilder;
	AlertDialog dialog = null;
	Activity activity;
	List<HashMap<String,String>> selectedItems;
	boolean[] itemsChecked;
	String[] itemsNames;
	String[] itemsIds;
    String title;
    public static String ALL_CHOICES;
    
	public MultipleSelectDialog(Activity activity, List<HashMap<String,String>> model, String title) {
		this.activity = activity;
		this.title = title;
		if (model != null && model.size() > 0) {
			createFilterData(model);
		}		
	}

	public MultipleSelectDialog(Activity activity, List<HashMap<String,String>> model, String title,
			List<HashMap<String,String>> selectedItems) {
		this.activity = activity;
		this.title = title;
		this.selectedItems = selectedItems;
		if (model != null && model.size() > 0) {
			createFilterData(model);
		}
	}

	void createFilterData(List<HashMap<String,String>> model) {
		itemsNames = new String[model.size()];
		itemsChecked = new boolean[model.size()];
		itemsIds = new String[model.size()];
		for (int i = 0; i < model.size(); i++) {
			itemsNames[i] = model.get(i).get(NAME).toString();
			itemsIds[i] = model.get(i).get(ID).toString();
			if(selectedItems != null){
				itemsChecked[i] = selectedItems.contains(model.get(i));				
			}else
				itemsChecked[i] = false;				
		}
		init(itemsNames, itemsChecked);
	}

	private void init(String[] items, boolean[] checked) {

		dialogBuilder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
		dialogBuilder.setTitle(title);
		dialogBuilder.setPositiveButton("Ок",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					HashMap<Integer,String> selectedMap = new HashMap<Integer,String>();
						int j = 0;
						for (int i = 0; i < itemsChecked.length; i++) {
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
						itemsChecked[arg1] = arg2;
					}
				});

		dialogBuilder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				HashMap<Integer,String> selectedMap = new HashMap<Integer,String>();
				int j = 0;
				for (int i = 0; i < itemsChecked.length; i++) {
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
