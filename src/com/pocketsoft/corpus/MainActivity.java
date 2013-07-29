package com.pocketsoft.corpus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends Activity implements LocationListener {
	/** Called when the activity is first created. */

	private DigitalClock digitalClock;
	private LocationManager locationManager;
	private ProgressDialog progressDialog;
	private Intent intent;
	private Geocoder geoCoder;
	private Handler handler;
	private Looper looper;
	private AdView adView;

	/**
	 * 位置情報取得時に立つフラグ
	 */
	private int locationFlg = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		// AdMobのViewを作成
		adView = new AdView(this, AdSize.BANNER, "a151f535f3d5c21");
		LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout1);
		layout.addView(adView);

		// 広告の表示を開始
		adView.loadAd(new AdRequest());

		// LocationManagerのインスタンスを取得
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		digitalClock = (DigitalClock) findViewById(R.id.digitalClock1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");
		Date date = new Date();
		String formatDate = sdf.format(date);
		TextView currentDateText = (TextView) findViewById(R.id.textView_date);
		currentDateText.setText(formatDate);

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Settings.setPhoneNumber(getBaseContext(),
				telephonyManager.getLine1Number());
		// Log.d("TM", "Line1Number " + telephonyManager.getLine1Number());
		// Log.d("TM", "DeviceId " + telephonyManager.getDeviceId());
		// Log.d("TM", "SimCountryIso " + telephonyManager.getSimCountryIso());
		// Log.d("TM", "SimOperator " + telephonyManager.getSimOperator());
		// Log.d("TM", "SimOperatorName " +
		// telephonyManager.getSimOperatorName());
		// Log.d("TM", "SimSerialNumber " +
		// telephonyManager.getSimSerialNumber());
		// Log.d("TM", "SimState " + telephonyManager.getSimState());
		// Log.d("TM", "VoiceMailNumber " +
		// telephonyManager.getVoiceMailNumber());

		// Preferenceから会社コードを取得
		String companyCd = Settings.getCompanyCd(getBaseContext());

		// PreferenceからIDを取得
		String userId = Settings.getUserId(getBaseContext());

		// Preferenceからパスワードを取得
		String password = Settings.getPassword(getBaseContext());

		// 会社コード、ID、パスワードが未設定の場合に警告
		if (companyCd.equals("") || userId.equals("") || password.equals("")) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle(R.string.setting);
			alertDialogBuilder.setMessage(R.string.aleat_setting);
			alertDialogBuilder.setIcon(android.R.drawable.ic_menu_manage);

			// OKボタンとリスナを設定
			alertDialogBuilder.setPositiveButton(R.string.OK,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(MainActivity.this,
									Settings.class);
							startActivity(intent);
						}
					});

			// ダイアログを表示
			alertDialogBuilder.create().show();
		}

		// ネットワーク接続の確認
		ConnectivityManager connectivityManager;
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();

		if (info == null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle(R.string.error);
			alertDialogBuilder.setMessage(R.string.error_network);
			alertDialogBuilder.setIcon(android.R.drawable.ic_menu_manage);

			// OKボタンとリスナを設定
			alertDialogBuilder.setPositiveButton(R.string.OK,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});

			// ダイアログを表示
			alertDialogBuilder.create().show();
		}

		// 設定ボタンの処理
		Button settingButton = (Button) findViewById(R.id.Button_settings);
		settingButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, Settings.class);
				startActivity(intent);
			}
		});

		// 出勤ボタンの処理
		Button startWorkButton = (Button) findViewById(R.id.button_startWork);
		startWorkButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				intentConfirm(digitalClock.getText().toString(),
						StatusConstants.START_WORK);
			}
		});

		// 退勤ボタンの処理
		Button endWorkButton = (Button) findViewById(R.id.button_endWork);
		endWorkButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				intentConfirm(digitalClock.getText().toString(),
						StatusConstants.END_WORK);
			}
		});

		// 外出ボタンの処理
		Button startOutButton = (Button) findViewById(R.id.button_startOut);
		startOutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				intentConfirm(digitalClock.getText().toString(),
						StatusConstants.START_OUT);
			}
		});

		// 再入ボタンの宣言
		Button endOutButton = (Button) findViewById(R.id.button_endOut);
		endOutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				intentConfirm(digitalClock.getText().toString(),
						StatusConstants.END_OUT);
			}
		});

	}

	/**
	 * データをセットして確認画面へ遷移
	 * 
	 * @param currentTime
	 * @param status
	 */
	public void intentConfirm(String currentTime, Integer status) {
		intent = new Intent(MainActivity.this, ConfirmActivity.class);
		intent.putExtra("currentTime", currentTime);
		intent.putExtra("status", status);

		// プログレスダイアログを表示
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.setCancelable(true);
		progressDialog.show();

		// マルチスレッドで位置情報を取得
		handler = new Handler();
		new Thread(new Runnable() {
			public void run() {

				// 位置情報取得を３回まで実行
				int i = 3;
				while (i > 0 && locationFlg == 0) {
					getLocation();
					i--;
				}

				// UI処理はhandlerでpost
				if (locationFlg == 1) {
					handler.post(new Runnable() {
						public void run() {
							locationFlg = 0;
							progressDialog.dismiss();
							startActivity(intent);
						}
					});
				} else {
					handler.post(new Runnable() {
						public void run() {
							Toast.makeText(MainActivity.this,
									R.string.error_gps, Toast.LENGTH_SHORT)
									.show();
							progressDialog.dismiss();
							startActivity(intent);
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 位置情報取得
	 */
	private void getLocation() {
		// 現在の状況に最適なプロバイダを取得
		String provider;
		Location location = null;

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);

		try {
			looper = Looper.getMainLooper();
			locationManager.requestLocationUpdates(provider, 60000, 0, this,
					looper);

			// 最後に取得した位置情報を取得
			location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				onLocationChanged(location);
			}
		} catch (Exception e) {
			locationFlg = 0;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// リスナーを解除
		locationManager.removeUpdates(this);
	}

	public void onLocationChanged(Location location) {
		geoCoder = new Geocoder(this, Locale.JAPAN);

		List<Address> addressList;
		try {
			addressList = geoCoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 5);

			// ジオコーディングに成功したら画面に表示
			if (!addressList.isEmpty()) {

				Address address = addressList.get(0);
				String currentLocation = null;

				// adressをStringへ
				String buf;
				for (int i = 0; (buf = address.getAddressLine(i)) != null; i++) {

					// 都道府県・市区町村・番地がnullじゃない場合のデータを取得
					if (address.getAdminArea() != null
							&& address.getLocality() != null
							&& address.getThoroughfare() != null) {
						currentLocation = buf;
						intent.putExtra("currentLocation", currentLocation);
						locationFlg = 1;
					}
				}
			}
		} catch (Exception e) {
			locationFlg = 0;
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onDestroy() {
		// AdMobのViewの破棄処理
		adView.destroy();
		super.onDestroy();
	}
}