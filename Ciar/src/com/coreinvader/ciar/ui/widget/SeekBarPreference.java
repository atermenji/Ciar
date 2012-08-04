package com.coreinvader.ciar.ui.widget;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.coreinvader.ciar.R;

public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener {
    
    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/res/com.searcher";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";

    private static final int DEFAULT_CURRENT_VALUE = 30;
    private static final int DEFAULT_MIN_VALUE = 1;
    private static final int DEFAULT_MAX_VALUE = 100;

    private final int mDefaultValue;
    private final int mMaxValue;
    private final int mMinValue;

    private int mCurrentValue;

    private SeekBar mSeekBar;
    private TextView mValueText;

    public SeekBarPreference(Context context, AttributeSet attrs) {
	super(context, attrs);

	mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
	mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
	mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_CURRENT_VALUE);
    }

    @Override
    protected View onCreateDialogView() {
	mCurrentValue = getPersistedInt(mDefaultValue);

	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view = inflater.inflate(R.layout.dialog_slider, null);

	((TextView) view.findViewById(R.id.min_value)).setText(Double.toString(mMinValue / 10.0));
	((TextView) view.findViewById(R.id.max_value)).setText(Double.toString(mMaxValue / 10.0));

	mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
	mSeekBar.setMax(mMaxValue - mMinValue);
	mSeekBar.setProgress(mCurrentValue - mMinValue);
	mSeekBar.setOnSeekBarChangeListener(this);

	mValueText = (TextView) view.findViewById(R.id.current_value);
	mValueText.setText(Double.toString(mCurrentValue / 10.0));

	return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
	super.onDialogClosed(positiveResult);

	if (!positiveResult) {
	    return;
	}

	if (shouldPersist()) {
	    persistInt(mCurrentValue);
	}

	notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
	String summary = super.getSummary().toString();
	double value = getPersistedInt(mDefaultValue) / 10.0;
	return String.format(summary, value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	mCurrentValue = progress + mMinValue;
	mValueText.setText(Double.toString(mCurrentValue / 10.0));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
