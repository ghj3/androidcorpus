package com.pocketsoft.corpus;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmActivity extends Activity {

	private Intent intent = new Intent();
	private String dateTime = null;
	private String currentLocation = null;

	private ResultCheck resultCheck = new ResultCheck();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.confirm);

		intent = getIntent();

		SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy. MM. dd");
		Date date = new Date();
		String formatDate = displayFormat.format(date);
		TextView currentDateText = (TextView) findViewById(R.id.textView_date);
		currentDateText.setText(formatDate);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		dateTime = sdf.format(date);

		// intentで現在時刻を受け取って表示
		TextView currentTimeText = (TextView) findViewById(R.id.textView_currentTime);
		currentTimeText.setText(intent.getStringExtra("currentTime"));

		// intentで住所を表示
		currentLocation = intent.getStringExtra("currentLocation");
		TextView locationText = (TextView) findViewById(R.id.textView_location);
		if (currentLocation == null) {
			locationText.setText(R.string.error_location);
			locationText.setTextColor(Color.RED);
		} else {
			locationText.setText(currentLocation);
		}

		// intentでstatusを受け取って表示
		TextView statusText = (TextView) findViewById(R.id.textView_status);
		switch (intent.getIntExtra("status", 0)) {
		case StatusConstants.START_WORK:
			statusText.setText(R.string.work_start);
			break;
		case StatusConstants.END_WORK:
			statusText.setText(R.string.work_end);
			break;
		case StatusConstants.START_OUT:
			statusText.setText(R.string.out_start);
			break;
		case StatusConstants.END_OUT:
			statusText.setText(R.string.out_end);
			break;
		default:
		}

		// OKボタンの処理
		Button okButton = (Button) findViewById(R.id.button_ok);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (currentLocation != null) {
					// UTF-8でエンコード
					try {
						currentLocation = URLEncoder.encode(currentLocation,
								"UTF-8");
					} catch (UnsupportedEncodingException e) {
						resultCheck.Check("ERR", ConfirmActivity.this);
					}
				}

				/*
				 * TODO 本番環境で利用する場合はURLを変更する
				 * 本番環境：http://s2corpus.ps-corpus.com/view/working/stamp.html
				 * テスト環境：http://182.48.35.169/s2corpus/view/working/stamp.html
				 */
				// GET送信するデータを準備
				String phonenumber = Settings.getPhoneNumber(getBaseContext());
				String url = "http://s2corpus.ps-corpus.com/view/working/stamp.html";
				String param = "?companyCd="
						+ Settings.getCompanyCd(getBaseContext())
						+ "&employeeCd=" + Settings.getUserId(getBaseContext())
						+ "&password=" + Settings.getPassword(getBaseContext())
						+ "&deviceCd=02" + "&stampKind="
						+ intent.getIntExtra("status", 0) + "&time=" + dateTime
						+ "&location=" + currentLocation;
				url = url + param;

				// ログ出力
				Log.d("Log",
						"companyCd = "
								+ Settings.getCompanyCd(getBaseContext()));
				Log.d("Log",
						"employeeCd = " + Settings.getUserId(getBaseContext()));
				Log.d("Log",
						"password = " + Settings.getPassword(getBaseContext()));
				Log.d("Log", "deviceCd = 02");
				Log.d("Log", "stampKind = " + intent.getIntExtra("status", 1));
				Log.d("Log", "time = " + dateTime);
				Log.d("Log", "location = " + currentLocation);

				try {
					Log.d("Log", "url = " + url);

					// GET送信してレスポンスをStringに格納
					String content = getInputStreamFromUrl(url);
					resultCheck.Check(content, ConfirmActivity.this);
				} catch (Exception e) {
					resultCheck.Check("ERR", ConfirmActivity.this);
				}
			}
		});

		// CANCELボタンの処理
		Button cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
	}

	public String getInputStreamFromUrl(String url) {
		String content = null;
		try {
			Log.i("Log", "GETリクエストを送信");
			HttpGet httpGet = new HttpGet(url);
			HttpParams httpParms = new BasicHttpParams();
			httpParms.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					3000);
			httpParms.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

			HttpClient httpclient = new DefaultHttpClient(httpParms);
			HttpResponse response = httpclient.execute(httpGet);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			response.getEntity().writeTo(byteArrayOutputStream);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				content = byteArrayOutputStream.toString();
				Log.d("Log", "GETResponse = " + content);
			}
		} catch (Exception e) {
			Log.e("Log", "HttpExceptionが発生");
			return null;
		}
		Log.i("Log", "GET送信終了");
		return content;
	}
}