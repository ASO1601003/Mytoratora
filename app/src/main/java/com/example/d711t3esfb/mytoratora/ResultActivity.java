package com.example.d711t3esfb.mytoratora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    final int toratora_tora = 0;
    final int toratora_bba = 1;
    final int toratora_kiyomasa =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int myHand = 0;
        Intent intent = getIntent();
        int id = intent.getIntExtra("MY_HAND",0);


        ImageView myHandImageView = (ImageView) findViewById(R.id.my_hand_img);
        switch (id){
            case R.id.tora:
                myHandImageView.setImageResource(R.drawable.tora);
                myHand = toratora_tora;
                break;
            case R.id.bba:
                myHandImageView.setImageResource(R.drawable.bba);
                myHand = toratora_bba;
                break;
            case R.id.kiyomasa:
                myHandImageView.setImageResource(R.drawable.kiyomasa);
                myHand = toratora_kiyomasa;
                break;
            default:
                myHand = toratora_bba;
                break;
        }
        //コンピュータの手を決める
        int comHand = getHand();

        ImageView comHandImageView = (ImageView) findViewById(R.id.com_hand_img);
        switch (comHand) {
            case toratora_tora:
                comHandImageView.setImageResource(R.drawable.tora);
                break;
            case toratora_bba:
                comHandImageView.setImageResource(R.drawable.bba);
                break;
            case toratora_kiyomasa:
                comHandImageView.setImageResource(R.drawable.kiyomasa);
                break;

        }
        //勝敗を判定する
        TextView resultLable = (TextView) findViewById(R.id.result_label);
        int gameResult = (comHand - myHand + 3) % 3;
        switch (gameResult) {
            case 0:

                //あいこの場合
                resultLable.setText(R.string.result_draw);
                break;
            case 1:
                //勝った場合
                resultLable.setText(R.string.result_win);
                break;
            case 2:
                //負けた場合
                resultLable.setText(R.string.result_lose);
                break;
        }
        //ジャンケンの結果を保存する
        saveDate(myHand,comHand,gameResult);
    }
    public void onBackButtonTapped(View view) {
        finish();
    }
    private void saveDate(int myHand,int comHand,int gameResult){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        int gameCount = pref.getInt("GAME_COUNT",0);
        int winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0);
        int lastComHand = pref.getInt("LAST_COM_HAND",0);
        int lastGameResult = pref.getInt("GAME_RESULT",-1);
        editor.putInt("GAME_COUNT",gameCount +1);
        if(lastGameResult == 2 && gameResult == 2){
            //コンピュータが連勝した場合
            editor.putInt("WINNING_STREAK_COUNT",winningStreakCount + 1);
        }else{
            editor.putInt("WINNTING_STREAK_COUNT",0);
        }
        editor.putInt("LAST_MY_HAND",myHand);
        editor.putInt("LAST_COM_HAND",comHand);
        editor.putInt("BEFORE_LAST_COM_HAND",lastComHand);
        editor.putInt("GAME_RESULT",gameResult);

        editor.commit();
    }
    private int getHand() {
        int hand = (int) (Math.random() * 3);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int gameCount = pref.getInt("GAME_COUNT",0);
        int winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0);
        int lastMyHand = pref.getInt("LAST_MY_HAND",0);
        int lastComHand = pref.getInt("LAST_COM_HAND",0);
        int beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0);
        int gameResult = pref.getInt("GAME_RESULT",-1);

        if(gameCount == 1){
            if (gameResult == 2){
                //前回の勝負が1回目で、コンピュータが買った場合、
                //コンピュータは次に出す手を変える
                while (lastComHand == hand) {
                    hand = (int) (Math.random() * 3);
                }
            }else if (gameResult == 1){
                //前回の勝負が1回目で、コンピュータが負けた場合
                //あいての出した手にかつてでを出す
                hand = (lastMyHand - 1 + 3)%3;
            }
        }else if(winningStreakCount > 0){
            if (beforeLastComHand == lastComHand){
                //同じ手で連勝した場合は手を変える
                while (lastComHand == hand){
                    hand = (int) (Math.random() * 3);
                }
            }
        }
        return hand;
    }
}
