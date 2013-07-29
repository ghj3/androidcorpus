package com.pocketsoft.corpus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	private static final String OPT_COMPANY_CD = "companyCd";
	private static final String OPT_USER_ID = "userId";
	private static final String OPT_PASSWORD = "password";
	private static final String OPT_PHONENUMBER = "phonenumber";
	private String currentSetting;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		addPreferencesFromResource(R.layout.settings);

		currentSetting = getString(R.string.current_setting);
		EditTextPreference editTextPreference = (EditTextPreference) getPreferenceScreen()
				.findPreference(OPT_COMPANY_CD);
		editTextPreference.setSummary(currentSetting + " "
				+ editTextPreference.getText());
		editTextPreference = (EditTextPreference) getPreferenceScreen()
				.findPreference(OPT_USER_ID);
		editTextPreference.setSummary(currentSetting + " "
				+ editTextPreference.getText());
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(listener);
	}

	private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {

			if (!key.equals("password")) {
				findPreference(key).setSummary(
						currentSetting + " "
								+ sharedPreferences.getString(key, "00000000"));
			}
		}
	};

	// ID用ゲッタの定義
	public static String getCompanyCd(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_COMPANY_CD, "");
	}

	// ID用ゲッタの定義
	public static String getUserId(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_USER_ID, "");
	}

	// PASSWORD用ゲッタの定義
	public static String getPassword(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_PASSWORD, "");
	}

	// PHONENUMBER用ゲッタの定義
	public static String getPhoneNumber(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_PHONENUMBER, "");
	}

	// PHONENUMBER用ｾｯﾀの定義
	public static void setPhoneNumber(Context context, String phonenumber) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		editor.putString(OPT_PHONENUMBER, phonenumber);
		editor.commit();

	}
}