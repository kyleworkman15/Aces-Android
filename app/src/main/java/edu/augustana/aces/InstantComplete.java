package edu.augustana.aces;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

/**
 * Created by Kyle Workman
 *
 * For always showing drop down even with no characters entered yet
 */

public class InstantComplete extends AppCompatAutoCompleteTextView {

    public InstantComplete(Context context) {
        super(context);
    }

    public InstantComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            performFiltering(getText(), 0);
        }
    }

}
