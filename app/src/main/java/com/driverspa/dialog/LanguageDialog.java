package com.driverspa.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.util.LocaleManager;
import com.driverspa.util.otto.ws.AboutUSRequestEvent;
import com.driverspa.util.otto.ws.InitRequestEvent;

/**
 * Bottom-sheet language picker: one row per supported language (native name +
 * a check on the current one). Built as a plain bottom-gravity {@link Dialog}
 * with a custom layout, so it matches the app's look and doesn't depend on a
 * Material Components theme. Applying a choice switches the UI language via
 * {@link LocaleManager} and re-fetches the server reference dictionary.
 */
public final class LanguageDialog {

    private LanguageDialog() {
    }

    public static void show(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View content = LayoutInflater.from(activity).inflate(R.layout.dialog_language, null);
        dialog.setContentView(content);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.getAttributes().windowAnimations = R.style.LanguageSheetAnimation;
        }

        LinearLayout container = content.findViewById(R.id.languageContainer);
        String current = LocaleManager.current();
        for (String tag : LocaleManager.SUPPORTED) {
            View row = LayoutInflater.from(activity).inflate(R.layout.item_language, container, false);
            TextView name = row.findViewById(R.id.languageName);
            ImageView check = row.findViewById(R.id.languageCheck);
            name.setText(LocaleManager.displayName(tag));
            check.setVisibility(tag.equals(current) ? View.VISIBLE : View.INVISIBLE);
            final String selected = tag;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!selected.equals(LocaleManager.current())) {
                        // Apply first so the stored language (used by the request
                        // interceptor) is updated, then refresh the server-provided
                        // reference dictionary so it comes back localized.
                        LocaleManager.apply(selected);
                        BA.getEventBus().post(new InitRequestEvent(LocaleManager.backendCode(selected)));
                        BA.getEventBus().post(new AboutUSRequestEvent());
                    }
                }
            });
            container.addView(row);
        }

        dialog.show();
    }
}
