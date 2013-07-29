package com.pocketsoft.corpus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AcceptedActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.accepted);

		// OKボタンの処理
		Button okButton = (Button) findViewById(R.id.button_ok);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				Intent intent = new Intent(AcceptedActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});
	}
}
