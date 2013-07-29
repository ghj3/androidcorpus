package com.pocketsoft.corpus;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ResultCheck{
	public void Check(String content, Context context){
		if(content.equals("OK")){
			// 完了画面へ遷移
			Intent intent = new Intent(context, AcceptedActivity.class);
			context.startActivity(intent);

		}else if(content.equals("ERR1")){
			Log.e("Log", "Error = ログインエラー");
			Toast.makeText(context, R.string.error_login, Toast.LENGTH_SHORT).show();

		}else if(content.equals("ERR2") || content.equals("ERR3")){
			Log.e("Log", "Error = 入力値エラー");
			Toast.makeText(context, R.string.error_input, Toast.LENGTH_SHORT).show();

		}else if(content.equals("ERR")){
			Log.e("Log", "Error = 原因不明エラー");
			Toast.makeText(context, R.string.error_unknown, Toast.LENGTH_SHORT).show();

		}else if(content.equals("ERR_GPS")){
			Log.e("Log", "Error = GPSエラー");
			Toast.makeText(context, R.string.error_gps, Toast.LENGTH_SHORT).show();
		}
	}
}
